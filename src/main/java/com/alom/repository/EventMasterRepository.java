package com.alom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alom.dao.entities.EventMasterEntity;

@Repository
public interface EventMasterRepository extends JpaRepository<EventMasterEntity, Long>{

}
