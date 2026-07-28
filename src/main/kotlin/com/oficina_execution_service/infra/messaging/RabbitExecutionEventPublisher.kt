package com.oficina_execution_service.infra.messaging

import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.domain.port.ExecutionEventPublisher
import com.oficina_execution_service.infra.config.RabbitMqConfig
import com.oficina_execution_service.infra.messaging.events.ExecutionCompletedEvent
import com.oficina_execution_service.infra.messaging.events.ExecutionStartedEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitExecutionEventPublisher(
    private val rabbitTemplate: RabbitTemplate
) : ExecutionEventPublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun executionStarted(execution: Execution) {
        val event = ExecutionStartedEvent(
            osId = execution.osId,
            executionId = requireNotNull(execution.id)
        )
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.OS_EXCHANGE,
            RabbitMqConfig.ROUTING_EXECUTION_STARTED,
            event
        )
        log.info("Publicado execucao.iniciada osId={} execucaoId={}", event.osId, event.executionId)
    }

    override fun executionCompleted(execution: Execution) {
        val event = ExecutionCompletedEvent(
            osId = execution.osId,
            executionId = requireNotNull(execution.id)
        )
        rabbitTemplate.convertAndSend(
            RabbitMqConfig.OS_EXCHANGE,
            RabbitMqConfig.ROUTING_EXECUTION_COMPLETED,
            event
        )
        log.info("Publicado execucao.finalizada osId={} execucaoId={}", event.osId, event.executionId)
    }
}
