package com.oficina_execution_service.domain.model

import com.oficina_execution_service.domain.enum.ExecutionItemStatus
import java.math.BigDecimal
import java.time.Instant

data class ExecutionItem(
    val id: Long? = null,
    val serviceId: Long,
    val quantity: Int,
    val appliedPrice: BigDecimal,
    val status: ExecutionItemStatus = ExecutionItemStatus.PENDING,
    val startedAt: Instant? = null,
    val completedAt: Instant? = null
)
