package com.alom.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
	public Page<EventMasterDto> getAllEvents(int page, int size) {
		
	      // Create a PageRequest object
        PageRequest pageRequest = PageRequest.of(page, size);
		
		// Fetch paginated EventMasterEntity
        Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findAll(pageRequest);

        // Map the entity to DTO and return
        return eventEntityPage.map(eventMapperService::mappedToEventMasterDto);  // MapStruct will handle mapping of each page element
	}

	@Override
	public EventMasterDto getEventByName(String eventName) {
		EventMasterEntity eventMasterEntity = eventMasterRepository.findByEventName(eventName);
		return eventMapperService.mappedToEventMasterDto(eventMasterEntity);
	}

	@Override
	public Page<EventMasterDto> getEventBetween(LocalDate fromEventDate, LocalDate uptoEventDate, int page, int size) {
        // Convert LocalDate to LocalDateTime (if your eventDate is LocalDateTime)
        LocalDateTime fromDateTime = fromEventDate.atStartOfDay();
        LocalDateTime toDateTime = uptoEventDate.atTime(23, 59, 59); // Include the whole day

        // Create a PageRequest object for pagination
        PageRequest pageRequest = PageRequest.of(page, size);

        // Fetch the events within the date range
        Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventDateBetween(fromDateTime, toDateTime, pageRequest);

        // Map the entity to DTO and return
        return eventEntityPage.map(eventMapperService::mappedToEventMasterDto);
    }
}
