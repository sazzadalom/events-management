package com.alom.dao.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alom.dao.entities.EventMediaEntity;

@Repository
public interface EventMediaRepository extends JpaRepository<EventMediaEntity, Long> {

}
