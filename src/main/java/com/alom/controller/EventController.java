package com.alom.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alom.dto.EventMasterDto;
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
@RequestMapping("/events")
public class EventController {

	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	@GetMapping("/view-events")
	public Page<EventMasterDto> getPaginatedEvents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		
		log.debug("/get/request/events/view-events: {}", page);
		Page<EventMasterDto> response = eventService.getAllEvents(page, size);
		log.debug("/get/response/events/view-events: {}", response);
		
		return response;
	}
	
    @GetMapping("/total/count")
    public long getTotalEventCount() {
        return eventService.getTotalEventCount();
    }

	@GetMapping("/view-event-by-name")
	public ResponseEntity<EventMasterDto> viewEventByName(@RequestParam String eventName) {
		log.debug("/get/request/events/view-event-by-name: {}", eventName);
		
		EventMasterDto eventResponse = eventService.getEventByName(eventName);
		log.debug("/get/response/events/view-events: {}", eventResponse);
		
		return ResponseEntity.ok(eventResponse);
	}
	
	@GetMapping("/view-event-by-date-range")
	public Page<EventMasterDto> viewEventByName(@RequestParam LocalDate fromEventDate, @RequestParam LocalDate uptoEventDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return eventService.getEventBetween(fromEventDate,uptoEventDate,page,size);
	}
	
	@PostMapping("/add-edit-events")
	public ResponseEntity<GenericResponse> addEvent(@RequestParam("jsonData") String jsonData, @RequestParam("file") MultipartFile file) throws IOException{
		log.debug("/post/request/events/add-edit-events: {}", jsonData);
		GenericResponse response = eventService.addOrUpdateEvent(file, jsonData);
		log.debug("/post/response/events/add-edit-events: {}", response);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/remove")
	public ResponseEntity<GenericResponse> removeEvent(@RequestParam String eventName){
		log.debug("/delete/request/events/remove: {}", eventName);
		GenericResponse response = eventService.removeEventByName(eventName);
		return ResponseEntity.ok(response);
	}

}
