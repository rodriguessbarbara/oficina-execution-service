package com.oficina_execution_service.infra.messaging

import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.infra.config.RabbitMqConfig
import com.oficina_execution_service.infra.messaging.events.ExecutionCompletedEvent
import com.oficina_execution_service.infra.messaging.events.ExecutionStartedEvent
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.time.Instant

class RabbitExecutionEventPublisherTest {
    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var publisher: RabbitExecutionEventPublisher

    @BeforeEach
    fun setUp() {
        rabbitTemplate = mock(RabbitTemplate::class.java)
        publisher = RabbitExecutionEventPublisher(rabbitTemplate)
    }

    @Test
    fun `should publish execution started event`() {
        publisher.executionStarted(execution(ExecutionStatus.IN_PROGRESS))

        verify(rabbitTemplate).convertAndSend(
            RabbitMqConfig.OS_EXCHANGE,
            RabbitMqConfig.ROUTING_EXECUTION_STARTED,
            ExecutionStartedEvent(osId = 10, executionId = 99)
        )
    }

    @Test
    fun `should publish execution completed event`() {
        publisher.executionCompleted(execution(ExecutionStatus.COMPLETED))

        verify(rabbitTemplate).convertAndSend(
            RabbitMqConfig.OS_EXCHANGE,
            RabbitMqConfig.ROUTING_EXECUTION_COMPLETED,
            ExecutionCompletedEvent(osId = 10, executionId = 99)
        )
    }

    @Test
    fun `should reject publishing an execution without persisted id`() {
        val execution = execution(ExecutionStatus.IN_PROGRESS).copy(id = null)

        assertThrows(IllegalArgumentException::class.java) {
            publisher.executionStarted(execution)
        }
    }

    private fun execution(status: ExecutionStatus) = Execution(
        id = 99,
        osId = 10,
        status = status,
        createdAt = Instant.parse("2026-01-01T10:00:00Z")
    )
}
