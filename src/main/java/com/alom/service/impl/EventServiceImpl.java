package com.alom.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.alom.dao.entities.EventMasterEntity;
import com.alom.dao.entities.EventMediaEntity;
import com.alom.dao.repositories.EventMasterRepository;
import com.alom.dao.repositories.EventMediaRepository;
import com.alom.dto.EventMasterDto;
import com.alom.mapper.EventMapperService;
import com.alom.model.EventModel;
import com.alom.payload.GenericResponse;
import com.alom.service.EventService;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class EventServiceImpl implements EventService {

	private final EventMasterRepository eventMasterRepository;
	private final EventMediaRepository eventMediaRepository;
	private final EventMapperService eventMapperService;

	public EventServiceImpl(EventMasterRepository eventMasterRepository, EventMapperService eventMapperService,
			EventMediaRepository eventMediaRepository) {
		this.eventMasterRepository = eventMasterRepository;
		this.eventMapperService = eventMapperService;
		this.eventMediaRepository = eventMediaRepository;
	}

	@Override
	public Page<EventMasterDto> getAllEvents(int page, int size) {

		// Create a PageRequest object
		PageRequest pageRequest = PageRequest.of(page, size);

		// Fetch paginated EventMasterEntity
		Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findAll(pageRequest);

		// Map the entity to DTO and return
		return eventEntityPage.map(eventMapperService::mappedToEventMasterDto); // MapStruct will handle mapping of each
																				// page element
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
		Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventDateBetween(fromDateTime, toDateTime,
				pageRequest);

		// Map the entity to DTO and return
		return eventEntityPage.map(eventMapperService::mappedToEventMasterDto);
	}

	@Override
	public GenericResponse addOrUpdateEvent(EventModel eventModel) {
		
		
		try {
			
			Path path = Paths.get(eventModel.getImagePath());
			String fileName = path.getFileName().toString();
			System.err.println("fileName: " + fileName);
	        byte[] data = Files.readAllBytes(path);
			
			EventMasterEntity eventMasterEntity = EventMasterEntity.builder()
//					.eventId(11l)
					.eventName(eventModel.getEventName())
					.eventUrl(eventModel.getEventWebLink())
					.eventDate(eventModel.getEventDate())
					.eventCreatedAt(LocalDateTime.now())
					.build();
			
			EventMediaEntity eventMediaEntity = EventMediaEntity.builder()
					.fileType(eventModel.getEventType())
					.fileName(fileName)
					.fileData(data)
					.uploadedAt(LocalDateTime.now()).build();
			
			eventMasterRepository.save(eventMasterEntity);
			eventMediaRepository.save(eventMediaEntity);
		}catch (IOException ioException) {
			ioException.getStackTrace();
		}
		
		
		
		return GenericResponse.builder().result("success").responseCode("00").message("event created successfully.")
				.build();
	}

}
