package com.alom.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.alom.dto.EventMasterDto;
import com.alom.model.EventModel;
import com.alom.payload.GenericResponse;

public interface EventService {

	public Page<EventMasterDto> getAllEvents(int page, int size);

	public EventMasterDto getEventByName(String eventName);

	public Page<EventMasterDto> getEventBetween(LocalDate fromEventDate, LocalDate uptoEventDate, int page, int size);

	public GenericResponse addOrUpdateEvent(EventModel eventModel);
	

}
