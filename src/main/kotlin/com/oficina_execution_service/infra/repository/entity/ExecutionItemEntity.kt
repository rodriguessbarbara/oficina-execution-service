package com.oficina_execution_service.infra.repository.entity

import com.oficina_execution_service.domain.enum.ExecutionItemStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "execution_item")
class ExecutionItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "execution_order_id", nullable = false)
    var execution: ExecutionEntity? = null,

    @Column(name = "service_id", nullable = false)
    var serviceId: Long = 0,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(name = "applied_price", nullable = false, precision = 19, scale = 2)
    var appliedPrice: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    var status: ExecutionItemStatus = ExecutionItemStatus.PENDING,

    @Column(name = "started_at")
    var startedAt: Instant? = null,

    @Column(name = "completed_at")
    var completedAt: Instant? = null
)
