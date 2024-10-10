package com.alom.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alom.dao.entities.EventAttendeeEntity;

@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendeeEntity, Long> {

}
