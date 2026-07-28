# language: pt
Funcionalidade: Ciclo de execução de uma ordem de serviço

  Cenário: Executar uma OS após a aprovação do orçamento
    Dado que a OS 42 foi criada
    Quando o orçamento da OS é aprovado
    E a oficina inicia e finaliza o reparo
    Então a execução termina com status COMPLETED
    E os eventos de início e fim são publicados
