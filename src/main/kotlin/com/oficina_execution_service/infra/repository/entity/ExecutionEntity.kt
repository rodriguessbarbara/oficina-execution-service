package com.oficina_execution_service.infra.repository.entity

import com.oficina_execution_service.domain.enum.ExecutionStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant

@Entity
@Table(name = "execution_order")
class ExecutionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "os_id", nullable = false, unique = true)
    var osId: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    var status: ExecutionStatus = ExecutionStatus.AWAITING_APPROVAL,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.EPOCH,

    @Column(name = "queued_at")
    var queuedAt: Instant? = null,

    @Column(name = "started_at")
    var startedAt: Instant? = null,

    @Column(name = "completed_at")
    var completedAt: Instant? = null,

    @Column(name = "cancelled_at")
    var cancelledAt: Instant? = null,

    @Column(name = "cancellation_reason", length = 500)
    var cancellationReason: String? = null,

    @OneToMany(
        mappedBy = "execution",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    var items: MutableList<ExecutionItemEntity> = mutableListOf(),

    @Version
    var version: Long = 0
)
