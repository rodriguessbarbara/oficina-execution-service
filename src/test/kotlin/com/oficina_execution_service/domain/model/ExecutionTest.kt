package com.oficina_execution_service.domain.model

import com.oficina_execution_service.domain.enum.ExecutionStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.Instant

class ExecutionTest {
    private val createdAt = Instant.parse("2026-01-01T10:00:00Z")
    private val later = createdAt.plusSeconds(60)

    @Test
    fun `should follow the approved execution lifecycle`() {
        val waiting = Execution(osId = 10, createdAt = createdAt)
        val queued = waiting.enqueue(later)
        val started = queued.start(later.plusSeconds(60))
        val completed = started.complete(later.plusSeconds(120))

        assertEquals(ExecutionStatus.QUEUED, queued.status)
        assertEquals(ExecutionStatus.IN_PROGRESS, started.status)
        assertEquals(ExecutionStatus.COMPLETED, completed.status)
    }

    @Test
    fun `should reject invalid transitions`() {
        val waiting = Execution(osId = 10, createdAt = createdAt)

        assertThrows(IllegalStateException::class.java) { waiting.start(later) }
        assertThrows(IllegalStateException::class.java) { waiting.complete(later) }
        assertThrows(IllegalStateException::class.java) {
            waiting.enqueue(later).start(later).complete(later).cancel("late", later)
        }
    }

    @Test
    fun `should keep repeated transitions idempotent`() {
        val queued = Execution(osId = 10, createdAt = createdAt).enqueue(later)
        val started = queued.start(later)
        val completed = started.complete(later)
        val cancelled = Execution(osId = 20, createdAt = createdAt).cancel("reason", later)

        assertSame(queued, queued.enqueue(later))
        assertSame(started, started.start(later))
        assertSame(completed, completed.complete(later))
        assertSame(cancelled, cancelled.cancel("reason", later))
    }
}
