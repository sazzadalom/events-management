package com.alom.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alom.model.EventMasterDto;
import com.alom.service.EventService;

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
		System.err.println(LocalDate.now());
		return eventService.getAllEvents(page, size);
	}

	@GetMapping("/view-event-by-name")
	public ResponseEntity<EventMasterDto> viewEventByName(@RequestParam String eventName) {
		EventMasterDto eventResponse = eventService.getEventByName(eventName);
		return ResponseEntity.ok(eventResponse);
	}
	
	@GetMapping("/view-event-by-date-range")
	public Page<EventMasterDto> viewEventByName(@RequestParam LocalDate fromEventDate, @RequestParam LocalDate uptoEventDate, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		return eventService.getEventBetween(fromEventDate,uptoEventDate,page,size);
	}

}
