package com.oficina_execution_service.application

import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.infra.messaging.events.OsCreatedEvent

interface ExecutionService {
    fun register(event: OsCreatedEvent): Execution
    fun enqueue(osId: Long): Execution
    fun start(osId: Long): Execution
    fun complete(osId: Long): Execution
    fun cancel(osId: Long, reason: String?): Execution
    fun findByOsId(osId: Long): Execution
    fun findQueue(): List<Execution>
}
