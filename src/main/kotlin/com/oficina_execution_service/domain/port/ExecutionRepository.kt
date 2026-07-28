package com.oficina_execution_service.domain.port

import com.oficina_execution_service.domain.model.Execution

interface ExecutionRepository {
    fun findByOsId(osId: Long): Execution?
    fun findQueue(): List<Execution>
    fun save(execution: Execution): Execution
}
