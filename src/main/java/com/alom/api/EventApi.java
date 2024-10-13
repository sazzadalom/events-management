package com.alom.api;

import java.io.IOException;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alom.annotations.ValidFileExtension;
import com.alom.model.EventMasterModel;
import com.alom.model.PaginationResponse;
import com.alom.payload.GenericResponse;
import com.alom.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
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
	public PaginationResponse<EventMasterModel> getPaginatedEvents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		log.debug("/get/request/api/events page : size {} {}", page, size);
		PaginationResponse<EventMasterModel> response = eventService.getAllEvents(page,size);
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
	public PaginationResponse<EventMasterModel> viewEventByName(@RequestParam String eventName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		log.debug("/get/request/api/events/search-name: {}", eventName);

		PaginationResponse<EventMasterModel> eventResponse = eventService.getEventByName(eventName, page, size);
		log.debug("/get/response/api/events/search-name: {}", eventResponse);

		return eventResponse;
	}

	@Operation(summary = "Search for events of a specific date range.", description = "Provide a range of date from date upto date is required as request parameter is required format is yyyy-MM-dd HH:mm:ss, JWT(JSON Web Token) is required.")
	@GetMapping("/events/search-date")
	public PaginationResponse<EventMasterModel> viewEventByDate(@RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date fromEventDate,
			@RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date uptoEventDate, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		log.debug("/get/request/api/events/search-date-range: {} : {}", fromEventDate, uptoEventDate);

		PaginationResponse<EventMasterModel> pageResponse = eventService.getEventBetween(fromEventDate, uptoEventDate,
				page, size);
		log.debug("/get/response/api/events/search-date-range: {} ", pageResponse);

		return pageResponse;
	}

	@Operation(summary = "Create event with upload attendees and media", description = "No parameter is required for this API, Provide string of json data like {\"eventName\": \"Happy Holi\",\"eventUrl\": \"www.aurusit.com\",\"eventDate\": \"2024-10-16 05:10:31\"}")
	@PostMapping(value = "/events/add-edit", consumes = "multipart/form-data")
	public ResponseEntity<GenericResponse> addEvent(@RequestParam("jsonData") String jsonData,
			@Parameter(description = "Select a image or video file")
			@RequestParam("mediaFile") @Valid @ValidFileExtension(acceptedExtensions = {"jpg", "jpeg", "mp4"}, message = "Failed to create event. Check the type of uploaded file. Acceptable jpg,jpeg,mp4") MultipartFile mediaFile,
			@Parameter(description = " Add event attendees file (xlsx).")
			@RequestParam("excelFile") @Valid @ValidFileExtension(acceptedExtensions = {"xlsx"}, message = "Failed to create event. Check the type of uploaded file. Acceptable xlsx") MultipartFile excelFile) throws IOException {
		log.debug("/post/request/api/events/add-edit: {}", jsonData);

		
		GenericResponse response = eventService.addOrUpdateEvent(mediaFile, excelFile, jsonData);
		log.debug("/post/response/events/add-edit: {}", response);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Remove event using event name", description = "Remove events from the inventory. This operation should also be restricted to admin users. eventName is required as request parameter is required for this API")
	@DeleteMapping("/events/remove")
	public ResponseEntity<GenericResponse> removeEvent(@RequestParam String eventName) {
		log.debug("/delete/request/api/events/remove: {}", eventName);
		GenericResponse response = eventService.removeEventByName(eventName);
		return ResponseEntity.ok(response);
	}

}
