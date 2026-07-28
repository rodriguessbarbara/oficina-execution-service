package com.oficina_execution_service.infra.messaging

import com.oficina_execution_service.application.ExecutionService
import com.oficina_execution_service.infra.config.RabbitMqConfig
import com.oficina_execution_service.infra.messaging.events.BudgetApprovedEvent
import com.oficina_execution_service.infra.messaging.events.OsCancelledEvent
import com.oficina_execution_service.infra.messaging.events.OsCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ExecutionEventConsumer(
    private val service: ExecutionService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [RabbitMqConfig.QUEUE_OS_CREATED])
    fun onOsCreated(event: OsCreatedEvent) {
        log.info("Recebido os.criada osId={}", event.osId)
        service.register(event)
    }

    @RabbitListener(queues = [RabbitMqConfig.QUEUE_BUDGET_APPROVED])
    fun onBudgetApproved(event: BudgetApprovedEvent) {
        log.info("Recebido orcamento.aprovado osId={} orcamentoId={}", event.osId, event.orcamentoId)
        service.enqueue(event.osId)
    }

    @RabbitListener(queues = [RabbitMqConfig.QUEUE_OS_CANCELLED])
    fun onOsCancelled(event: OsCancelledEvent) {
        log.info("Recebido os.cancelada osId={}", event.osId)
        service.cancel(event.osId, event.motivo)
    }
}
