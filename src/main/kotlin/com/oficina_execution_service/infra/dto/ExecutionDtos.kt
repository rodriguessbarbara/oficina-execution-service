package com.oficina_execution_service.infra.dto

import com.oficina_execution_service.domain.enum.ExecutionItemStatus
import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.domain.model.Execution
import java.math.BigDecimal
import java.time.Instant

data class ExecutionResponse(
    val id: Long?,
    val osId: Long,
    val status: ExecutionStatus,
    val items: List<ExecutionItemResponse>,
    val createdAt: Instant,
    val queuedAt: Instant?,
    val startedAt: Instant?,
    val completedAt: Instant?,
    val cancelledAt: Instant?,
    val cancellationReason: String?
) {
    companion object {
        fun from(execution: Execution) = ExecutionResponse(
            id = execution.id,
            osId = execution.osId,
            status = execution.status,
            items = execution.items.map {
                ExecutionItemResponse(
                    id = it.id,
                    serviceId = it.serviceId,
                    quantity = it.quantity,
                    appliedPrice = it.appliedPrice,
                    status = it.status
                )
            },
            createdAt = execution.createdAt,
            queuedAt = execution.queuedAt,
            startedAt = execution.startedAt,
            completedAt = execution.completedAt,
            cancelledAt = execution.cancelledAt,
            cancellationReason = execution.cancellationReason
        )
    }
}

data class ExecutionItemResponse(
    val id: Long?,
    val serviceId: Long,
    val quantity: Int,
    val appliedPrice: BigDecimal,
    val status: ExecutionItemStatus
)
