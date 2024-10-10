package com.alom.api;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alom.dto.EventMasterDto;
import com.alom.model.PaginationResponse;
import com.alom.payload.GenericResponse;
import com.alom.service.EventService;

import lombok.extern.log4j.Log4j2;


/**
 * <P> This controller is used to perform complete curd operation with pagination.</p>
 * @author sazzad
 * @since 09-10-2024
 * @version 1.0.0
 *
 */
@Log4j2
@RestController
@RequestMapping("/api")
public class EventApi {

	private final EventService eventService;

	public EventApi(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping("/events")
	public PaginationResponse<EventMasterDto> getPaginatedEvents(Pageable pageable) {
		
		log.debug("/get/request/api/events: {}", pageable);
		PaginationResponse<EventMasterDto> response = eventService.getAllEvents(pageable);
		log.debug("/get/response/api/events: {}", response);
		
		return response;
	}
	
    @GetMapping("/total/count")
    public long getTotalEventCount() {
        return eventService.getTotalEventCount();
    }

	@GetMapping("/events/search-name")
	public ResponseEntity<EventMasterDto> viewEventByName(@RequestParam String eventName) {
		log.debug("/get/request/api/events/search-name: {}", eventName);
		
		EventMasterDto eventResponse = eventService.getEventByName(eventName);
		log.debug("/get/response/api/events/search-name: {}", eventResponse);
		
		return ResponseEntity.ok(eventResponse);
	}
	
	@GetMapping("/events/search-date")
	public PaginationResponse<EventMasterDto> viewEventByDate(@RequestParam LocalDate fromEventDate, @RequestParam LocalDate uptoEventDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		log.debug("/get/request/api/events/search-date-range: {} : {}" , fromEventDate, uptoEventDate);
		
		PaginationResponse<EventMasterDto> pageResponse = eventService.getEventBetween(fromEventDate,uptoEventDate,page,size);
		 log.debug("/get/response/api/events/search-date-range: {} " , pageResponse);
		 
		 return pageResponse;
	}
	
	@PostMapping("/events/add-edit")
	public ResponseEntity<GenericResponse> addEvent(@RequestParam("jsonData") String jsonData, @RequestParam("file") MultipartFile file) throws IOException{
		log.debug("/post/request/api/events/add-edit: {}", jsonData);
		
		GenericResponse response = eventService.addOrUpdateEvent(file, jsonData);
		log.debug("/post/response/events/add-edit: {}", response);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/events/remove")
	public ResponseEntity<GenericResponse> removeEvent(@RequestParam String eventName){
		log.debug("/delete/request/api/events/remove: {}", eventName);
		GenericResponse response = eventService.removeEventByName(eventName);
		return ResponseEntity.ok(response);
	}

}
