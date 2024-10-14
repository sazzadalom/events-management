package com.alom.events.api;

import java.io.IOException;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alom.events.annotations.ValidFileExtension;
import com.alom.events.model.EventMasterModel;
import com.alom.events.model.PaginationResponse;
import com.alom.events.service.EventService;
import com.alom.payload.GenericResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.log4j.Log4j2;

/**
 * <P>
 * This controller is used to perform complete curd operation with pagination.
 * </p>
 * 
 * @author sazzad
 * @since 09-10-2024
 * @version 1.0.0
 *
 */

@Log4j2
@Validated
@RestController
@RequestMapping("/api")
public class EventApi {

	private final EventService eventService;

	public EventApi(EventService eventService) {
		this.eventService = eventService;
	}

	@Operation(summary = "Fetch all events", description = "Fetches all events available in the system.No parameter is required for this, JWT(JSON Web Token) is required.")
	@GetMapping("/events")
	public PaginationResponse<EventMasterModel> getPaginatedEvents(@RequestParam(defaultValue = "1") @Positive(message = "page must be greater than 0") int page, @RequestParam(defaultValue = "10") int size) {

		log.debug("/get/request/api/events page size : {} :{}", page, size);
		PaginationResponse<EventMasterModel> response = eventService.getAllEvents(page-1,size);
		log.debug("/get/response/api/events: {}", response);

		return response;
	}

	@Operation(summary = "Scearch for get total event counts", description = "No parameter is required for this, JWT(JSON Web Token) is required.")
	@GetMapping("/total/count")
	public long getTotalEventCount() {
		return eventService.getTotalEventCount();
	}
	
	@Operation(summary = "Scearch for event using event name.", description = "eventName is required as request parameter is required for this API, JWT(JSON Web Token) is required.")
	@GetMapping("/events/search-name")
	public PaginationResponse<EventMasterModel> viewEventByName(@RequestParam String eventName, @RequestParam(defaultValue = "1") @Positive(message = "page must be greater than 0") int page, @RequestParam(defaultValue = "10") int size) {
		log.debug("/get/request/api/events/search-name: {}", eventName);

		PaginationResponse<EventMasterModel> eventResponse = eventService.getEventByName(eventName, page-1, size);
		log.debug("/get/response/api/events/search-name: {}", eventResponse);

		return eventResponse;
	}

	@Operation(summary = "Search for events of a specific date range.", description = "Provide a range of date from date upto date is required as request parameter is required format is yyyy-MM-dd HH:mm:ss, JWT(JSON Web Token) is required.")
	@GetMapping("/events/search-date")
	public PaginationResponse<EventMasterModel> viewEventByDate(@RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromEventDate,
			@RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date uptoEventDate, @RequestParam(defaultValue = "1") @Positive(message = "page must be greater than 0") int page,
			@RequestParam(defaultValue = "10") int size) {
		log.debug("/get/request/api/events/search-date-range: {} : {}", fromEventDate, uptoEventDate);

		PaginationResponse<EventMasterModel> pageResponse = eventService.getEventBetween(fromEventDate, uptoEventDate,
				page-1, size);
		log.debug("/get/response/api/events/search-date-range: {} ", pageResponse);

		return pageResponse;
	}

	@Operation(summary = "Create event with upload attendees and media", description = "No parameter is required for this API, Provide string of json data like {\"eventName\": \"Happy Holi\",\"eventUrl\": \"www.aurusit.com\",\"eventDate\": \"2024-10-16 05:10:31\"}")
	@PostMapping(value = "/events/add-edit", consumes = "multipart/form-data")
	public ResponseEntity<GenericResponse> addEvent(@RequestParam("jsonData") String jsonData,
			@Parameter(description = "Select a image or video file")
			@RequestParam("file") @Valid @ValidFileExtension(acceptedExtensions = {"jpg", "jpeg", "mp4"}) MultipartFile mediaFile,
			@Parameter(description = " Add event attendees file (xlsx).")
			@RequestParam("file") @Valid @ValidFileExtension(acceptedExtensions = {"xlsx"}) MultipartFile excelFile) throws IOException {
		log.debug("/post/request/api/events/add-edit: {}", jsonData);

		
		GenericResponse response = eventService.addEvent(mediaFile, excelFile, jsonData);
		log.debug("/post/response/events/add-edit: {}", response);

		return ResponseEntity.ok(response);
	}

	
	@Operation(summary = "Edit an existing event")
	@PutMapping(value = "/api/events/edit/{eventId}", consumes = "multipart/form-data")
	public ResponseEntity<GenericResponse> updateEvent(@PathVariable("eventId") Long eventId, @RequestParam("jsonData") String jsonData,
	@RequestParam("mediaFile") @Valid @ValidFileExtension(acceptedExtensions = {"jpg", "jpeg", "mp4"}) MultipartFile mediaFile,
	@RequestParam("excelFile") @Valid @ValidFileExtension(acceptedExtensions = {"xlsx"}) MultipartFile excelFile) throws IOException {
		log.debug("/post/request/api/events/edit eventId:{} jsonData:{}",eventId, jsonData);
		GenericResponse response = eventService.editEvent(eventId, jsonData, mediaFile, excelFile);
		log.debug("/post/response/events/edit: {}", response);
		return ResponseEntity.ok(response);
	}
	
	
	@Operation(summary = "Remove event using event name", description = "Remove events from the inventory. This operation should also be restricted to admin users. eventName is required as request parameter is required for this API")
	@DeleteMapping("/events/remove")
	public ResponseEntity<GenericResponse> removeEvent(@RequestParam String eventName) {
		log.debug("/delete/request/api/events/remove: {}", eventName);
		GenericResponse response = eventService.removeEventByName(eventName);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Refresh redis", description = "Refresh redis chash memory from here.")
	@GetMapping("/events/refresh")
	public ResponseEntity<GenericResponse> refresh() {
		log.info("/get/request/api/events/refresh");
		GenericResponse response = eventService.refresh();
		log.info("/get/response/api/events/refresh:{}",response);
		return ResponseEntity.ok(response);
	}
}
