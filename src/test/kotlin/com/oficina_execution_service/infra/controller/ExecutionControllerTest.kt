package com.oficina_execution_service.infra.controller

import com.oficina_execution_service.application.ExecutionService
import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.domain.model.Execution
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.Instant

class ExecutionControllerTest {
    private lateinit var service: ExecutionService
    private lateinit var controller: ExecutionController

    @BeforeEach
    fun setUp() {
        service = mock(ExecutionService::class.java)
        controller = ExecutionController(service)
    }

    @Test
    fun `should find execution by OS id`() {
        val execution = execution(osId = 10)
        `when`(service.findByOsId(10)).thenReturn(execution)

        val response = controller.findByOsId(10)

        assertEquals(10, response.osId)
        assertEquals(ExecutionStatus.QUEUED, response.status)
        verify(service).findByOsId(10)
    }

    @Test
    fun `should list queue`() {
        `when`(service.findQueue()).thenReturn(listOf(execution(10), execution(20)))

        val response = controller.queue()

        assertEquals(listOf(10L, 20L), response.map { it.osId })
        verify(service).findQueue()
    }

    @Test
    fun `should start and complete execution`() {
        val started = execution(10).copy(status = ExecutionStatus.IN_PROGRESS)
        val completed = started.copy(status = ExecutionStatus.COMPLETED)
        `when`(service.start(10)).thenReturn(started)
        `when`(service.complete(10)).thenReturn(completed)

        assertEquals(ExecutionStatus.IN_PROGRESS, controller.start(10).status)
        assertEquals(ExecutionStatus.COMPLETED, controller.complete(10).status)
        verify(service).start(10)
        verify(service).complete(10)
    }

    private fun execution(osId: Long) = Execution(
        id = osId,
        osId = osId,
        status = ExecutionStatus.QUEUED,
        createdAt = Instant.parse("2026-01-01T10:00:00Z")
    )
}
