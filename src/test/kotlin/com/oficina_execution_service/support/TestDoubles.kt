package com.oficina_execution_service.support

import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.domain.port.ExecutionEventPublisher
import com.oficina_execution_service.domain.port.ExecutionRepository

class InMemoryExecutionRepository : ExecutionRepository {
    private val data = linkedMapOf<Long, Execution>()
    private var sequence = 1L

    override fun findByOsId(osId: Long): Execution? = data[osId]

    override fun findQueue(): List<Execution> =
        data.values.filter { it.status.name == "QUEUED" }.sortedBy { it.queuedAt }

    override fun save(execution: Execution): Execution {
        val saved = if (execution.id == null) execution.copy(id = sequence++) else execution
        data[saved.osId] = saved
        return saved
    }
}

class RecordingExecutionEventPublisher : ExecutionEventPublisher {
    val started = mutableListOf<Execution>()
    val completed = mutableListOf<Execution>()

    override fun executionStarted(execution: Execution) {
        started += execution
    }

    override fun executionCompleted(execution: Execution) {
        completed += execution
    }
}
