package com.oficina_execution_service.infra.messaging.events

import java.math.BigDecimal

data class OsCreatedEvent(
    val osId: Long,
    val clienteId: Long,
    val veiculoId: Long,
    val itensServico: List<ServiceItemEventDto> = emptyList(),
    val itensEstoque: List<StockItemEventDto> = emptyList()
)

data class ServiceItemEventDto(
    val servicoId: Long,
    val quantidade: Int,
    val precoAplicado: BigDecimal
)

data class StockItemEventDto(
    val estoqueId: Long,
    val quantidade: BigDecimal,
    val precoUnitario: BigDecimal
)

data class BudgetApprovedEvent(
    val osId: Long,
    val orcamentoId: String
)

data class OsCancelledEvent(
    val osId: Long,
    val motivo: String?
)

data class ExecutionStartedEvent(
    val osId: Long,
    val executionId: Long
)

data class ExecutionCompletedEvent(
    val osId: Long,
    val executionId: Long
)
