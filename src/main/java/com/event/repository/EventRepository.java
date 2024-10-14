package com.event.repository;

import com.event.entity.Event;
import com.event.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    boolean existsByName(String name);

    @Query("SELECT COUNT(e) > 0 FROM event e WHERE e.location = :location " +
            "AND (e.startTime < :endTime AND e.endTime > :startTime)")
    boolean existsAtSameLocationWithTimeOverlap(String location, LocalDateTime startTime, LocalDateTime endTime);

}
