package com.oficina_execution_service.infra.repository

import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.infra.repository.entity.ExecutionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ExecutionJpaRepository : JpaRepository<ExecutionEntity, Long> {
    fun findByOsId(osId: Long): ExecutionEntity?
    fun findAllByStatusOrderByQueuedAtAsc(status: ExecutionStatus): List<ExecutionEntity>
}
