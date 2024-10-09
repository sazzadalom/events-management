package com.alom.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alom.dao.entities.EventMasterEntity;
import com.alom.mapper.EventMapperService;
import com.alom.model.EventMasterDto;
import com.alom.repository.EventMasterRepository;
import com.alom.service.EventService;

@Service
public class EventServiceImpl implements EventService {

	private final EventMasterRepository eventMasterRepository;
	private final EventMapperService eventMapperService;
	
	public EventServiceImpl(EventMasterRepository eventMasterRepository, EventMapperService eventMapperService) {
		this.eventMasterRepository = eventMasterRepository;
		this.eventMapperService = eventMapperService;
	}

	@Override
	public List<EventMasterDto> getAllEvents() {
		List<EventMasterEntity> findAll = eventMasterRepository.findAll();
		List<EventMasterDto> eventMasterDtoList = eventMapperService.mappedToEventMasterDto(findAll);
		return eventMasterDtoList;
	}

}
