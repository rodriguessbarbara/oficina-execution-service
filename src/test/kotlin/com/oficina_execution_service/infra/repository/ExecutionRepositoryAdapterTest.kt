package com.oficina_execution_service.infra.repository

import com.oficina_execution_service.domain.enum.ExecutionItemStatus
import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.domain.model.ExecutionItem
import com.oficina_execution_service.infra.repository.entity.ExecutionEntity
import com.oficina_execution_service.infra.repository.entity.ExecutionItemEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.time.Instant

class ExecutionRepositoryAdapterTest {
    private lateinit var jpaRepository: ExecutionJpaRepository
    private lateinit var adapter: ExecutionRepositoryAdapter

    @BeforeEach
    fun setUp() {
        jpaRepository = mock(ExecutionJpaRepository::class.java)
        adapter = ExecutionRepositoryAdapter(jpaRepository)
    }

    @Test
    fun `should map entity to domain when finding by OS id`() {
        `when`(jpaRepository.findByOsId(10)).thenReturn(entity(osId = 10, version = 3))

        val result = adapter.findByOsId(10)

        assertEquals(10, result?.osId)
        assertEquals(3, result?.version)
        assertEquals(ExecutionStatus.QUEUED, result?.status)
        assertEquals(1, result?.items?.size)
        assertEquals(ExecutionItemStatus.PENDING, result?.items?.single()?.status)
    }

    @Test
    fun `should return null when execution does not exist`() {
        `when`(jpaRepository.findByOsId(99)).thenReturn(null)

        assertNull(adapter.findByOsId(99))
    }

    @Test
    fun `should map queue preserving order`() {
        `when`(
            jpaRepository.findAllByStatusOrderByQueuedAtAsc(ExecutionStatus.QUEUED)
        ).thenReturn(listOf(entity(10), entity(20)))

        val result = adapter.findQueue()

        assertEquals(listOf(10L, 20L), result.map { it.osId })
    }

    @Test
    fun `should preserve optimistic lock version and item relation when saving`() {
        `when`(jpaRepository.save(any(ExecutionEntity::class.java))).thenAnswer { invocation ->
            invocation.getArgument<ExecutionEntity>(0).also { saved ->
                saved.id = 100
                saved.items.single().id = 200
            }
        }
        val execution = domainExecution(version = 4)

        val result = adapter.save(execution)

        assertEquals(100, result.id)
        assertEquals(4, result.version)
        assertEquals(200, result.items.single().id)
        assertEquals(10, result.items.single().serviceId)
        verify(jpaRepository).save(any(ExecutionEntity::class.java))
    }

    private fun domainExecution(version: Long = 0) = Execution(
        id = 1,
        version = version,
        osId = 10,
        status = ExecutionStatus.QUEUED,
        items = listOf(
            ExecutionItem(
                id = 2,
                serviceId = 10,
                quantity = 2,
                appliedPrice = BigDecimal("150.00")
            )
        ),
        createdAt = Instant.parse("2026-01-01T10:00:00Z"),
        queuedAt = Instant.parse("2026-01-01T11:00:00Z")
    )

    private fun entity(osId: Long, version: Long = 0): ExecutionEntity {
        val entity = ExecutionEntity(
            id = osId,
            version = version,
            osId = osId,
            status = ExecutionStatus.QUEUED,
            createdAt = Instant.parse("2026-01-01T10:00:00Z"),
            queuedAt = Instant.parse("2026-01-01T11:00:00Z")
        )
        entity.items += ExecutionItemEntity(
            id = osId * 10,
            execution = entity,
            serviceId = 50,
            quantity = 1,
            appliedPrice = BigDecimal("99.90"),
            status = ExecutionItemStatus.PENDING
        )
        return entity
    }
}
