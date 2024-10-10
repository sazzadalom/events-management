package com.alom.dao.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.alom.dao.entities.EventMasterEntity;

public interface EventMasterRepository extends JpaRepository<EventMasterEntity, Long> {

    Page<EventMasterEntity> findAll(Pageable pageable);  // Add for pageable support

	EventMasterEntity findByEventName(String eventName);
	
	Page<EventMasterEntity> findByEventDateBetween(LocalDateTime fromEventDate, LocalDateTime uptoEventDate, Pageable pageable);

	void deleteByEventName(String eventName);
}
