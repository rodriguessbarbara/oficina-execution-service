package com.oficina_execution_service.infra.messaging

import com.oficina_execution_service.application.ExecutionService
import com.oficina_execution_service.infra.messaging.events.BudgetApprovedEvent
import com.oficina_execution_service.infra.messaging.events.OsCancelledEvent
import com.oficina_execution_service.infra.messaging.events.OsCreatedEvent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ExecutionEventConsumerTest {
    private lateinit var service: ExecutionService
    private lateinit var consumer: ExecutionEventConsumer

    @BeforeEach
    fun setUp() {
        service = mock(ExecutionService::class.java)
        consumer = ExecutionEventConsumer(service)
    }

    @Test
    fun `should register execution when OS is created`() {
        val event = OsCreatedEvent(osId = 10, clienteId = 20, veiculoId = 30)

        consumer.onOsCreated(event)

        verify(service).register(event)
    }

    @Test
    fun `should enqueue execution when budget is approved`() {
        consumer.onBudgetApproved(BudgetApprovedEvent(osId = 10, orcamentoId = "budget-1"))

        verify(service).enqueue(10)
    }

    @Test
    fun `should cancel execution when OS is cancelled`() {
        consumer.onOsCancelled(OsCancelledEvent(osId = 10, motivo = "Cliente desistiu"))

        verify(service).cancel(10, "Cliente desistiu")
    }
}
