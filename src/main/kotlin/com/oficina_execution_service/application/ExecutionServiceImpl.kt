package com.oficina_execution_service.application

import com.oficina_execution_service.domain.model.Execution
import com.oficina_execution_service.domain.model.ExecutionItem
import com.oficina_execution_service.domain.port.ExecutionEventPublisher
import com.oficina_execution_service.domain.port.ExecutionRepository
import com.oficina_execution_service.infra.messaging.events.OsCreatedEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant

@Service
class ExecutionServiceImpl(
    private val repository: ExecutionRepository,
    private val eventPublisher: ExecutionEventPublisher,
    private val clock: Clock
) : ExecutionService {

    @Transactional
    override fun register(event: OsCreatedEvent): Execution =
        repository.findByOsId(event.osId) ?: repository.save(
            Execution(
                osId = event.osId,
                createdAt = Instant.now(clock),
                items = event.itensServico.map {
                    ExecutionItem(
                        serviceId = it.servicoId,
                        quantity = it.quantidade,
                        appliedPrice = it.precoAplicado
                    )
                }
            )
        )

    @Transactional
    override fun enqueue(osId: Long): Execution =
        repository.save(findByOsId(osId).enqueue(Instant.now(clock)))

    @Transactional
    override fun start(osId: Long): Execution {
        val saved = repository.save(findByOsId(osId).start(Instant.now(clock)))
        eventPublisher.executionStarted(saved)
        return saved
    }

    @Transactional
    override fun complete(osId: Long): Execution {
        val saved = repository.save(findByOsId(osId).complete(Instant.now(clock)))
        eventPublisher.executionCompleted(saved)
        return saved
    }

    @Transactional
    override fun cancel(osId: Long, reason: String?): Execution =
        repository.save(findByOsId(osId).cancel(reason, Instant.now(clock)))

    @Transactional(readOnly = true)
    override fun findByOsId(osId: Long): Execution =
        repository.findByOsId(osId)
            ?: throw NoSuchElementException("Execução não encontrada para a OS $osId")

    @Transactional(readOnly = true)
    override fun findQueue(): List<Execution> = repository.findQueue()
}
