package com.oficina_execution_service.application

import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.infra.messaging.events.OsCreatedEvent
import com.oficina_execution_service.infra.messaging.events.ServiceItemEventDto
import com.oficina_execution_service.support.InMemoryExecutionRepository
import com.oficina_execution_service.support.RecordingExecutionEventPublisher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ExecutionServiceImplTest {
    private lateinit var repository: InMemoryExecutionRepository
    private lateinit var publisher: RecordingExecutionEventPublisher
    private lateinit var service: ExecutionServiceImpl

    @BeforeEach
    fun setUp() {
        repository = InMemoryExecutionRepository()
        publisher = RecordingExecutionEventPublisher()
        service = ExecutionServiceImpl(
            repository,
            publisher,
            Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC)
        )
    }

    @Test
    fun `should register OS idempotently with service items`() {
        val event = event(10)

        val first = service.register(event)
        val repeated = service.register(event)

        assertEquals(first.id, repeated.id)
        assertEquals(1, first.items.size)
        assertEquals(ExecutionStatus.AWAITING_APPROVAL, first.status)
    }

    @Test
    fun `should enqueue start and complete publishing integration events`() {
        service.register(event(10))

        assertEquals(ExecutionStatus.QUEUED, service.enqueue(10).status)
        assertEquals(ExecutionStatus.IN_PROGRESS, service.start(10).status)
        assertEquals(ExecutionStatus.COMPLETED, service.complete(10).status)
        assertEquals(1, publisher.started.size)
        assertEquals(1, publisher.completed.size)
    }

    @Test
    fun `should cancel an unfinished execution`() {
        service.register(event(10))

        val cancelled = service.cancel(10, "OS cancelada")

        assertEquals(ExecutionStatus.CANCELLED, cancelled.status)
        assertEquals("OS cancelada", cancelled.cancellationReason)
    }

    @Test
    fun `should list queue by entry order and fail for missing OS`() {
        service.register(event(10))
        service.enqueue(10)

        assertEquals(listOf(10L), service.findQueue().map { it.osId })
        assertThrows(NoSuchElementException::class.java) { service.findByOsId(99) }
    }

    private fun event(osId: Long) = OsCreatedEvent(
        osId = osId,
        clienteId = 1,
        veiculoId = 2,
        itensServico = listOf(
            ServiceItemEventDto(
                servicoId = 3,
                quantidade = 1,
                precoAplicado = BigDecimal("120.00")
            )
        )
    )
}
