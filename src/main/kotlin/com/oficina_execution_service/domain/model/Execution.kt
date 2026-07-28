package com.oficina_execution_service.domain.model

import com.oficina_execution_service.domain.enum.ExecutionStatus
import java.time.Instant

data class Execution(
    val id: Long? = null,
    val osId: Long,
    val status: ExecutionStatus = ExecutionStatus.AWAITING_APPROVAL,
    val items: List<ExecutionItem> = emptyList(),
    val createdAt: Instant,
    val queuedAt: Instant? = null,
    val startedAt: Instant? = null,
    val completedAt: Instant? = null,
    val cancelledAt: Instant? = null,
    val cancellationReason: String? = null
) {
    fun enqueue(at: Instant): Execution {
        if (status == ExecutionStatus.QUEUED) return this
        check(status == ExecutionStatus.AWAITING_APPROVAL) {
            "A execução só pode entrar na fila após aguardar aprovação"
        }
        return copy(status = ExecutionStatus.QUEUED, queuedAt = at)
    }

    fun start(at: Instant): Execution {
        if (status == ExecutionStatus.IN_PROGRESS) return this
        check(status == ExecutionStatus.QUEUED) {
            "A execução precisa estar na fila para ser iniciada"
        }
        return copy(status = ExecutionStatus.IN_PROGRESS, startedAt = at)
    }

    fun complete(at: Instant): Execution {
        if (status == ExecutionStatus.COMPLETED) return this
        check(status == ExecutionStatus.IN_PROGRESS) {
            "A execução precisa estar em andamento para ser finalizada"
        }
        return copy(status = ExecutionStatus.COMPLETED, completedAt = at)
    }

    fun cancel(reason: String?, at: Instant): Execution {
        if (status == ExecutionStatus.CANCELLED) return this
        check(status != ExecutionStatus.COMPLETED) {
            "Uma execução finalizada não pode ser cancelada automaticamente"
        }
        return copy(
            status = ExecutionStatus.CANCELLED,
            cancelledAt = at,
            cancellationReason = reason
        )
    }
}
