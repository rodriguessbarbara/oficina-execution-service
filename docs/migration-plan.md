# Plano de migração do domínio de execução

## Mover do monólito

Origem: `oficina-mvp-backend`.

- Regras de `IniciaExecucaoUseCase`.
- Regras de `AtualizarOSStatusServicoUseCase`.
- Regras de `ConsultaStatusServicoOSUseCase`.
- Parte de execução e tempos de `FinalizaOSUseCase`.
- Ordenação da fila hoje em `ListOrdemServicoUseCase.getOrderingByStatus`.
- Campos de execução de `OrdemServico` e `ItemServico`.
- Endpoints de iniciar/finalizar execução, consultar e atualizar item.
- Testes correspondentes desses casos de uso e controllers, reescritos em Kotlin.

`IniciaDiagnosticoUseCase` será dividido: execução mantém progresso/resultado técnico; Billing mantém composição e valor do orçamento; OS Service mantém somente o status global.

## Criado neste repositório

- modelo `Execution` e itens;
- máquina de estados inicial;
- API de consulta, fila, início e conclusão;
- consumidores de `os.criada`, `orcamento.aprovado` e `os.cancelada`;
- produtores de `execucao.iniciada` e `execucao.finalizada`;
- PostgreSQL e Flyway;
- DLQ e retry de mensageria;
- Swagger, Actuator e base para Prometheus/Datadog;
- testes unitários, fluxo BDD e gate JaCoCo;
- Docker, Compose, Kubernetes e pipeline CI/CD.

## Mantido no monólito

- clientes, veículos, estoque e catálogo enquanto não forem extraídos;
- compatibilidade das rotas públicas durante a transição;
- dados históricos existentes até a validação da cópia;
- delegação temporária para o Execution Service;
- fallback controlado somente durante a janela de migração.

## Sequência segura

1. Publicar o novo serviço sem tráfego de escrita.
2. Fazer o OS Service publicar contratos compatíveis e validar consumo sombra.
3. Copiar somente dados de execução e reconciliar por `os_id`.
4. Alterar o monólito para delegar leitura e comandos.
5. Ativar escrita exclusiva no Execution Service.
6. Monitorar divergências, DLQs, latência e taxa de erro.
7. Remover escrita antiga após a janela de estabilização.

As tabelas antigas não devem ser apagadas no primeiro deploy. A remoção é uma mudança posterior, com backup e validação de reconciliação.
