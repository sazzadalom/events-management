package com.alom.dao.repositories;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.alom.dao.entities.EventMasterEntity;

public interface EventMasterRepository extends JpaRepository<EventMasterEntity, Long> {

	Page<EventMasterEntity> findAll(Pageable pageable);  

	Page<EventMasterEntity> findByEventName(String eventName, Pageable pageable);
	
	Page<EventMasterEntity> findByEventDateBetween(Date fromEventDate, Date uptoEventDate, Pageable pageable);

	void deleteByEventName(String eventName);
}
