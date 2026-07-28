package com.oficina_execution_service.domain.model

import com.oficina_execution_service.domain.enum.ExecutionItemStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class ExecutionItemTest {
    @Test
    fun `should preserve values and support status evolution by copy`() {
        val item = ExecutionItem(
            id = 1,
            serviceId = 20,
            quantity = 2,
            appliedPrice = BigDecimal("100.00")
        )
        val startedAt = Instant.parse("2026-01-01T10:00:00Z")
        val started = item.copy(
            status = ExecutionItemStatus.IN_PROGRESS,
            startedAt = startedAt
        )
        val equivalent = item.copy()

        assertEquals(item, equivalent)
        assertEquals(item.hashCode(), equivalent.hashCode())
        assertNotEquals(item, started)
        assertEquals(ExecutionItemStatus.IN_PROGRESS, started.status)
        assertEquals(startedAt, started.startedAt)
        assertTrue(item.toString().contains("serviceId=20"))
    }
}
