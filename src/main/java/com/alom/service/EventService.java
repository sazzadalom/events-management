package com.alom.service;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.alom.dto.EventMasterModel;
import com.alom.model.PaginationResponse;
import com.alom.payload.GenericResponse;

public interface EventService {

	public PaginationResponse<EventMasterModel> getAllEvents(Pageable pageable);

	public EventMasterModel getEventByName(String eventName);

	public PaginationResponse<EventMasterModel> getEventBetween(Date fromEventDate, Date uptoEventDate, int page, int size);

	public GenericResponse addOrUpdateEvent(MultipartFile mediaFile, MultipartFile excelFile, EventMasterModel eventMasterModel);

	public GenericResponse removeEventByName(String eventName);

	public long getTotalEventCount();
	

}
