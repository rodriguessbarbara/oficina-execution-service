package com.oficina_execution_service.infra.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()

    @Test
    fun `should map missing resource to 404`() {
        val response = handler.notFound(NoSuchElementException("Execução ausente"))

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals(404, response.body?.status)
        assertEquals("Execução ausente", response.body?.message)
        assertNotNull(response.body?.timestamp)
    }

    @Test
    fun `should map invalid transition to 409`() {
        val response = handler.conflict(IllegalStateException("Transição inválida"))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals(409, response.body?.status)
        assertEquals("Transição inválida", response.body?.message)
    }

    @Test
    fun `should use fallback messages`() {
        assertEquals(
            "Recurso não encontrado",
            handler.notFound(NoSuchElementException(null as String?)).body?.message
        )
        assertEquals(
            "Transição inválida",
            handler.conflict(IllegalStateException(null as String?)).body?.message
        )
    }
}
