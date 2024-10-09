package com.alom.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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


	@GetMapping("/get-events")
	public ResponseEntity<List<EventMasterDto>> getAllEvents(){
		List<EventMasterDto> allEvents = eventService.getAllEvents();
		return ResponseEntity.ok(allEvents);
	}
}
