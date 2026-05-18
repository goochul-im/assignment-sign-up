package com.thinkfree.tfinder.common.infrastructure.outbox.iface;

import com.thinkfree.tfinder.common.infrastructure.outbox.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IOutboxRepository extends JpaRepository<OutboxEventEntity, Long> {

    @Query(
            value = """
                    select *
                    from outbox_entity
                    where status = 'PENDING'
                    order by id
                    limit :limit
                    for update skip locked
                    """,
            nativeQuery = true
    )
    List<OutboxEventEntity> findPendingForUpdate(@Param("limit") int limit);
}
