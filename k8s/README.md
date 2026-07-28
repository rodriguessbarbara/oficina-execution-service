# Kubernetes

Os manifests base não fixam namespace. A pipeline aplica todos os recursos com `kubectl -n "$K8S_NAMESPACE"` e substitui apenas `IMAGE_PLACEHOLDER`.

Antes do deploy, devem existir:

- namespace alvo;
- PostgreSQL exclusivo acessível pelo host do ConfigMap;
- secret `execution-service-secrets` com `DB_PASSWORD` e `CLOUDAMQP_URL`;
- agente Datadog no cluster, caso `DD_TRACE_ENABLED=true`.

Exemplo local de aplicação:

```bash
kubectl -n oficina-mvp-develop apply -f k8s/develop/configmap.yaml
kubectl -n oficina-mvp-develop apply -f k8s/base/service.yaml
sed "s|IMAGE_PLACEHOLDER|registry/execution-service:tag|g" k8s/base/deployment.yaml |
  kubectl -n oficina-mvp-develop apply -f -
```
