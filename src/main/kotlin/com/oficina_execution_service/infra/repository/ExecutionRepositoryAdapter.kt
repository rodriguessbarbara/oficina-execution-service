package com.oficina_execution_service.infra.repository

import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.domain.model.ExecutionItem
import com.oficina_execution_service.domain.port.ExecutionRepository
import com.oficina_execution_service.infra.repository.entity.ExecutionEntity
import com.oficina_execution_service.infra.repository.entity.ExecutionItemEntity
import org.springframework.stereotype.Repository

@Repository
class ExecutionRepositoryAdapter(
    private val jpaRepository: ExecutionJpaRepository
) : ExecutionRepository {

    override fun findByOsId(osId: Long): Execution? =
        jpaRepository.findByOsId(osId)?.toDomain()

    override fun findQueue(): List<Execution> =
        jpaRepository.findAllByStatusOrderByQueuedAtAsc(ExecutionStatus.QUEUED)
            .map { it.toDomain() }

    override fun save(execution: Execution): Execution =
        jpaRepository.save(execution.toEntity()).toDomain()

    private fun Execution.toEntity(): ExecutionEntity {
        val entity = ExecutionEntity(
            id = id,
            version = version,
            osId = osId,
            status = status,
            createdAt = createdAt,
            queuedAt = queuedAt,
            startedAt = startedAt,
            completedAt = completedAt,
            cancelledAt = cancelledAt,
            cancellationReason = cancellationReason
        )
        entity.items = items.map {
            ExecutionItemEntity(
                id = it.id,
                execution = entity,
                serviceId = it.serviceId,
                quantity = it.quantity,
                appliedPrice = it.appliedPrice,
                status = it.status,
                startedAt = it.startedAt,
                completedAt = it.completedAt
            )
        }.toMutableList()
        return entity
    }

    private fun ExecutionEntity.toDomain(): Execution = Execution(
        id = id,
        version = version,
        osId = osId,
        status = status,
        items = items.map {
            ExecutionItem(
                id = it.id,
                serviceId = it.serviceId,
                quantity = it.quantity,
                appliedPrice = it.appliedPrice,
                status = it.status,
                startedAt = it.startedAt,
                completedAt = it.completedAt
            )
        },
        createdAt = createdAt,
        queuedAt = queuedAt,
        startedAt = startedAt,
        completedAt = completedAt,
        cancelledAt = cancelledAt,
        cancellationReason = cancellationReason
    )
}
