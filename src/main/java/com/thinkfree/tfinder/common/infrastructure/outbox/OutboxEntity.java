package com.thinkfree.tfinder.common.infrastructure.outbox;

import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventStatus;
import com.thinkfree.tfinder.common.infrastructure.outbox.enumrate.OutboxEventType;
import com.thinkfree.tfinder.workspace.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "outbox_entity")
@Getter
@NoArgsConstructor
public class OutboxEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private OutboxEventType eventType;

    @Lob
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Column(name = "retry_count")
    private int retryCount;

    @Column
    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status;

    public OutboxEntity(OutboxEventType eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
        this.retryCount = 0;
        this.status = OutboxEventStatus.PENDING;
    }

    public void addRetryCount() {
        this.retryCount++;
        if (eventType.getMaxRetryCount() <= this.retryCount) {
            this.status = OutboxEventStatus.FAILED;
        }
    }

    public void markDone() {
        this.status = OutboxEventStatus.DONE;
    }

}
