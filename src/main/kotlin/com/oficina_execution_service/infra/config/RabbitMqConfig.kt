package com.oficina_execution_service.infra.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {
    companion object {
        const val OS_EXCHANGE = "os.exchange"
        const val DEAD_LETTER_EXCHANGE = "os.dlx"

        const val ROUTING_OS_CREATED = "os.criada"
        const val ROUTING_BUDGET_APPROVED = "orcamento.aprovado"
        const val ROUTING_OS_CANCELLED = "os.cancelada"
        const val ROUTING_EXECUTION_STARTED = "execucao.iniciada"
        const val ROUTING_EXECUTION_COMPLETED = "execucao.finalizada"

        const val QUEUE_OS_CREATED = "os.criada.execution-service"
        const val QUEUE_BUDGET_APPROVED = "orcamento.aprovado.execution-service"
        const val QUEUE_OS_CANCELLED = "os.cancelada.execution-service"

        const val DLQ_OS_CREATED = "$QUEUE_OS_CREATED.dlq"
        const val DLQ_BUDGET_APPROVED = "$QUEUE_BUDGET_APPROVED.dlq"
        const val DLQ_OS_CANCELLED = "$QUEUE_OS_CANCELLED.dlq"
    }

    @Bean
    fun osExchange() = TopicExchange(OS_EXCHANGE, true, false)

    @Bean
    fun deadLetterExchange() = DirectExchange(DEAD_LETTER_EXCHANGE, true, false)

    @Bean
    fun osCreatedQueue() = durableQueue(QUEUE_OS_CREATED, DLQ_OS_CREATED)

    @Bean
    fun budgetApprovedQueue() = durableQueue(QUEUE_BUDGET_APPROVED, DLQ_BUDGET_APPROVED)

    @Bean
    fun osCancelledQueue() = durableQueue(QUEUE_OS_CANCELLED, DLQ_OS_CANCELLED)

    @Bean
    fun osCreatedDeadLetterQueue() = Queue(DLQ_OS_CREATED, true)

    @Bean
    fun budgetApprovedDeadLetterQueue() = Queue(DLQ_BUDGET_APPROVED, true)

    @Bean
    fun osCancelledDeadLetterQueue() = Queue(DLQ_OS_CANCELLED, true)

    @Bean
    fun osCreatedBinding(osExchange: TopicExchange): Binding =
        BindingBuilder.bind(osCreatedQueue()).to(osExchange).with(ROUTING_OS_CREATED)

    @Bean
    fun budgetApprovedBinding(osExchange: TopicExchange): Binding =
        BindingBuilder.bind(budgetApprovedQueue()).to(osExchange).with(ROUTING_BUDGET_APPROVED)

    @Bean
    fun osCancelledBinding(osExchange: TopicExchange): Binding =
        BindingBuilder.bind(osCancelledQueue()).to(osExchange).with(ROUTING_OS_CANCELLED)

    @Bean
    fun osCreatedDeadLetterBinding(deadLetterExchange: DirectExchange): Binding =
        BindingBuilder.bind(osCreatedDeadLetterQueue()).to(deadLetterExchange).with(DLQ_OS_CREATED)

    @Bean
    fun budgetApprovedDeadLetterBinding(deadLetterExchange: DirectExchange): Binding =
        BindingBuilder.bind(budgetApprovedDeadLetterQueue()).to(deadLetterExchange).with(DLQ_BUDGET_APPROVED)

    @Bean
    fun osCancelledDeadLetterBinding(deadLetterExchange: DirectExchange): Binding =
        BindingBuilder.bind(osCancelledDeadLetterQueue()).to(deadLetterExchange).with(DLQ_OS_CANCELLED)

    @Bean
    fun messageConverter(objectMapper: ObjectMapper) = Jackson2JsonMessageConverter(objectMapper)

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: Jackson2JsonMessageConverter
    ) = RabbitTemplate(connectionFactory).also { it.messageConverter = messageConverter }

    private fun durableQueue(name: String, deadLetterRoutingKey: String): Queue =
        QueueBuilder.durable(name)
            .deadLetterExchange(DEAD_LETTER_EXCHANGE)
            .deadLetterRoutingKey(deadLetterRoutingKey)
            .build()
}
