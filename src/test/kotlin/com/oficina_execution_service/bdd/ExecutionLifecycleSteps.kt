package com.oficina_execution_service.bdd

import com.oficina_execution_service.application.ExecutionServiceImpl
import com.oficina_execution_service.domain.enum.ExecutionStatus
import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.infra.messaging.events.OsCreatedEvent
import com.oficina_execution_service.support.InMemoryExecutionRepository
import com.oficina_execution_service.support.RecordingExecutionEventPublisher
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ExecutionLifecycleSteps {
    private lateinit var publisher: RecordingExecutionEventPublisher
    private lateinit var service: ExecutionServiceImpl
    private lateinit var execution: Execution

    @Before
    fun setUp() {
        publisher = RecordingExecutionEventPublisher()
        service = ExecutionServiceImpl(
            InMemoryExecutionRepository(),
            publisher,
            Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC)
        )
    }

    @Given("que a OS {long} foi criada")
    fun osWasCreated(osId: Long) {
        execution = service.register(
            OsCreatedEvent(osId = osId, clienteId = 1, veiculoId = 2)
        )
    }

    @When("o orçamento da OS é aprovado")
    fun budgetIsApproved() {
        execution = service.enqueue(execution.osId)
    }

    @When("a oficina inicia e finaliza o reparo")
    fun repairIsStartedAndCompleted() {
        service.start(execution.osId)
        execution = service.complete(execution.osId)
    }

    @Then("a execução termina com status {word}")
    fun executionHasStatus(status: String) {
        assertEquals(ExecutionStatus.valueOf(status), execution.status)
    }

    @Then("os eventos de início e fim são publicados")
    fun lifecycleEventsArePublished() {
        assertEquals(1, publisher.started.size)
        assertEquals(1, publisher.completed.size)
    }
}
