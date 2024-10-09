package com.alom.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.alom.model.EventMasterDto;

public interface EventService {

	public Page<EventMasterDto> getAllEvents(int page, int size);

	public EventMasterDto getEventByName(String eventName);

	public Page<EventMasterDto> getEventBetween(LocalDate fromEventDate, LocalDate uptoEventDate, int page, int size);
	

}
