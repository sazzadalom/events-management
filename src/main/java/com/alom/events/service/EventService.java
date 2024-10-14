package com.alom.events.service;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import com.alom.events.model.EventMasterModel;
import com.alom.events.model.PaginationResponse;
import com.alom.payload.GenericResponse;

public interface EventService {

	public PaginationResponse<EventMasterModel> getAllEvents( int page, int size);

	public PaginationResponse<EventMasterModel> getEventByName(String eventName, int page, int size);

	public PaginationResponse<EventMasterModel> getEventBetween(Date fromEventDate, Date uptoEventDate, int page, int size);

	public GenericResponse addOrUpdateEvent(MultipartFile mediaFile, MultipartFile excelFile, String jsonData);

	public GenericResponse removeEventByName(String eventName);

	public long getTotalEventCount();
	

}
