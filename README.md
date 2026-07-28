# oficina-execution-service

Microsserviço responsável pela fila de execução, pelo ciclo de reparo e pela comunicação de conclusão das ordens de serviço da Oficina MVP.

## Responsabilidade

Este serviço:

- mantém uma cópia local mínima dos dados necessários para executar uma OS;
- cria o registro de execução ao consumir `os.criada`;
- coloca a OS na fila ao consumir `orcamento.aprovado`;
- inicia e finaliza reparos;
- publica `execucao.iniciada` e `execucao.finalizada`;
- compensa uma execução ainda não concluída ao consumir `os.cancelada`;
- persiste somente em seu PostgreSQL próprio.

Este serviço não gerencia clientes, veículos, catálogo de serviços, orçamento, pagamento nem o estado canônico da OS. Também não acessa bancos de outros serviços e não utiliza NoSQL.

## Arquitetura

O código segue uma separação inspirada em arquitetura hexagonal:

```text
domain/
  enum/       estados do ciclo de execução
  model/      regras e transições de domínio
  port/       contratos de persistência e publicação
application/  casos de uso do serviço
infra/
  config/     Spring, OpenAPI e RabbitMQ
  controller/ API REST
  dto/        contratos HTTP
  messaging/  eventos consumidos e publicados
  repository/ adaptador JPA e entidades SQL
```

Detalhes e diagrama: [docs/architecture.md](docs/architecture.md).

## Saga

A escolha inicial é uma Saga coreografada, coerente com o `oficina-os-service`:

1. OS Service publica `os.criada`.
2. Execution Service registra a execução como `AWAITING_APPROVAL`.
3. Billing Service publica `orcamento.aprovado`.
4. Execution Service coloca a OS em `QUEUED`.
5. Ao iniciar, publica `execucao.iniciada`.
6. Ao concluir, publica `execucao.finalizada`.
7. Se `os.cancelada` chegar antes da conclusão, a compensação muda a execução para `CANCELLED`.

Consumidores são idempotentes no nível da OS: `os_id` é único e transições repetidas retornam o estado atual. As filas possuem DLQ. Para garantir publicação atômica entre PostgreSQL e RabbitMQ, o próximo incremento deve adotar transactional outbox.

## Contratos

Eventos consumidos:

| Routing key | Origem | Ação |
|---|---|---|
| `os.criada` | OS Service | registra execução e itens |
| `orcamento.aprovado` | Billing Service | coloca execução na fila |
| `os.cancelada` | OS Service | executa compensação |

Eventos publicados:

| Routing key | Destino principal | Ação |
|---|---|---|
| `execucao.iniciada` | OS Service | atualiza OS para `EM_EXECUCAO` |
| `execucao.finalizada` | OS Service | atualiza OS para `FINALIZADA` |

API inicial:

- `GET /execucoes/os/{osId}`
- `GET /execucoes/fila`
- `PATCH /execucoes/os/{osId}/iniciar`
- `PATCH /execucoes/os/{osId}/finalizar`
- Swagger: `http://localhost:8082/swagger-ui.html`
- Health: `http://localhost:8082/actuator/health`

## Executar localmente

Requisitos: Java 21 e Docker.

```bash
docker compose up -d db-execution rabbitmq
./gradlew bootRun
```

Ou execute tudo em containers:

```bash
docker compose up --build
```

O serviço usa a porta `8082`, PostgreSQL local em `5435` e RabbitMQ em `5672`. O painel local do RabbitMQ fica em `15672`.

## Testes e qualidade

```bash
./gradlew check jacocoTestReport --no-daemon
```

O `check` executa testes unitários, o cenário BDD e a verificação JaCoCo mínima de 80% sobre as regras de negócio em `application` e `domain.model`. O relatório HTML completo, incluindo infraestrutura, fica em `build/reports/jacoco/test/html/index.html`.

O projeto SonarQube Cloud e a organização permanecem declarados em `build.gradle.kts`. No GitHub, configure apenas o secret `SONAR_TOKEN` para a análise.

## CI/CD

O workflow `.github/workflows/ci-cd.yaml` executa build, testes, cobertura e Sonar. O deploy em EKS usa OIDC e só é habilitado quando a variável do repositório `ENABLE_DEPLOY=true`.

A política desejada para a branch `main` está documentada em
`.github/branch-protection.yml`. Esse arquivo não ativa a proteção no GitHub:
a aplicação da regra remota continua sendo uma configuração administrativa.

Para bloquear pushes locais diretos em `main` e `master`, instale o hook
versionado:

```bash
sh .github/install-hooks.sh
```

Configuração esperada:

- secret `SONAR_TOKEN`;
- secret `AWS_ROLE_TO_ASSUME`;
- secret `EXECUTION_DB_PASSWORD`;
- secret `CLOUDAMQP_URL` com URI `amqps://`;
- variável `ENABLE_DEPLOY=true`;
- variável opcional `K8S_NAMESPACE` (padrão `oficina-mvp-prod`).

## Migração a partir do monólito

O monólito continuará existindo, mas perderá a responsabilidade de execução. A migração será incremental:

- `IniciaExecucaoUseCase` passa a chamar a API ou publicar comando para este serviço;
- `AtualizarOSStatusServicoUseCase` e `ConsultaStatusServicoOSUseCase` passam a operar aqui;
- a parte de execução de `FinalizaOSUseCase` passa a finalizar neste serviço;
- `ListOrdemServicoUseCase.getOrderingByStatus()` é substituído pela fila deste serviço;
- os campos `inicio_execucao` e `fim_execucao` de OS e itens deixam de ser gravados pelo monólito;
- endpoints de iniciar/finalizar execução e atualizar itens passam a delegar para este serviço;
- tabelas de execução são migradas por cópia controlada; as tabelas originais do monólito não são removidas na primeira etapa.

O plano detalhado, incluindo o que é movido, criado e mantido, está em [docs/migration-plan.md](docs/migration-plan.md).

## Estado deste scaffold

Já estão criados os contratos, ciclo básico, banco, mensageria com DLQ, API, testes, BDD, cobertura, Docker, Kubernetes e CI/CD. Diagnóstico detalhado, atualização individual de itens, outbox, Testcontainers e observabilidade distribuída completa estão registrados como próximos incrementos no plano de migração.
