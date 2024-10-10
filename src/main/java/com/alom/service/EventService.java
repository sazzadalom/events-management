package com.alom.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.alom.dto.EventMasterDto;
import com.alom.model.PaginationResponse;
import com.alom.payload.GenericResponse;

public interface EventService {

	public PaginationResponse<EventMasterDto> getAllEvents(Pageable pageable);

	public EventMasterDto getEventByName(String eventName);

	public PaginationResponse<EventMasterDto> getEventBetween(LocalDate fromEventDate, LocalDate uptoEventDate, int page, int size);

	public GenericResponse addOrUpdateEvent(MultipartFile file, String jsonData);

	public GenericResponse removeEventByName(String eventName);

	public long getTotalEventCount();
	

}
