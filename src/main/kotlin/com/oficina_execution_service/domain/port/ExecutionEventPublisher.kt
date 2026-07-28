package com.oficina_execution_service.domain.port

import com.oficina_execution_service.domain.model.Execution

interface ExecutionEventPublisher {
    fun executionStarted(execution: Execution)
    fun executionCompleted(execution: Execution)
}
