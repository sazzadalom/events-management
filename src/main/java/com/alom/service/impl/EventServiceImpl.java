package com.alom.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alom.configuration.properties.ExcelProperties;
import com.alom.dao.entities.EventAttendeeEntity;
import com.alom.dao.entities.EventMasterEntity;
import com.alom.dao.entities.EventMediaEntity;
import com.alom.dao.repositories.EventAttendeeRepository;
import com.alom.dao.repositories.EventMasterRepository;
import com.alom.dao.repositories.EventMediaRepository;
import com.alom.dto.EventMasterDto;
import com.alom.exception.EntityNotFoundException;
import com.alom.mapper.EventMapperService;
import com.alom.model.EventModel;
import com.alom.payload.GenericResponse;
import com.alom.service.EventService;
import com.alom.utility.ExcelUtility;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;




@Service
@Transactional
public class EventServiceImpl implements EventService {

	private final EventMasterRepository eventMasterRepository;
	private final EventMediaRepository eventMediaRepository;
	private final EventAttendeeRepository attendeeRepository;
	private final EventMapperService eventMapperService;
	private final ExcelProperties excelProperties;

	public EventServiceImpl(EventMasterRepository eventMasterRepository, EventMediaRepository eventMediaRepository,
			EventAttendeeRepository attendeeRepository, EventMapperService eventMapperService,
			ExcelProperties excelProperties) {
		this.eventMasterRepository = eventMasterRepository;
		this.eventMediaRepository = eventMediaRepository;
		this.attendeeRepository = attendeeRepository;
		this.eventMapperService = eventMapperService;
		this.excelProperties = excelProperties;
	}

	@Override
	public Page<EventMasterDto> getAllEvents(int page, int size) {

		// Create a PageRequest object
		PageRequest pageRequest = PageRequest.of(page, size);

		// Fetch paginated EventMasterEntity
		Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findAll(pageRequest);

		// Map the entity to DTO and return
		return eventEntityPage.map(eventMapperService::mappedToEventMasterDto); // MapStruct will handle mapping of each
																				// page element
	}

	@Override
	public EventMasterDto getEventByName(String eventName) {
		EventMasterEntity eventMasterEntity = eventMasterRepository.findByEventName(eventName);
		return eventMapperService.mappedToEventMasterDto(eventMasterEntity);
	}

	@Override
	public Page<EventMasterDto> getEventBetween(LocalDate fromEventDate, LocalDate uptoEventDate, int page, int size) {
		// Convert LocalDate to LocalDateTime (if your eventDate is LocalDateTime)
		LocalDateTime fromDateTime = fromEventDate.atStartOfDay();
		LocalDateTime toDateTime = uptoEventDate.atTime(23, 59, 59); // Include the whole day

		// Create a PageRequest object for pagination
		PageRequest pageRequest = PageRequest.of(page, size);

		// Fetch the events within the date range
		Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventDateBetween(fromDateTime, toDateTime,
				pageRequest);

		// Map the entity to DTO and return
		return eventEntityPage.map(eventMapperService::mappedToEventMasterDto);
	}

	@Override
	public GenericResponse addOrUpdateEvent(MultipartFile multipartFile, String jsonData) {
		
		try (InputStream inputStream = multipartFile.getInputStream();
				Workbook workbook = WorkbookFactory.create(inputStream)) {

			ObjectMapper objectMapper = new ObjectMapper();
			EventModel eventModel = objectMapper.readValue(jsonData, EventModel.class);
			String eventDateString = eventModel.getEventDate();
			LocalDate eventDate = ZonedDateTime.parse(eventDateString, DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDate();
			
			EventMasterEntity eventMasterEntity = this.checkEventAlreadyExist(eventModel.getEventName());
			
			Path path = Paths.get(eventModel.getImagePath());
			String fileName = path.getFileName().toString();

			byte[] data = Files.readAllBytes(path);
			
			if(Objects.isNull(eventMasterEntity)) {
				eventMasterEntity = EventMasterEntity.builder()
						.eventName(eventModel.getEventName()).eventUrl(eventModel.getEventWebLink()).eventDate(eventDate)
						.eventCreatedAt(LocalDateTime.now()).build();
			}
			 

			EventMediaEntity eventMediaEntity = EventMediaEntity.builder().fileType(eventModel.getEventType())
					.fileName(fileName).fileData(data).uploadedAt(LocalDateTime.now()).build();

			ExcelUtility.validateFileExtention(multipartFile.getOriginalFilename());

			ExcelUtility.validateHeaderContents(workbook, excelProperties.getAttendeeUploadHeaders());

			List<EventAttendeeEntity> eventAttendeeEntityList = this.takeInputDataFromExcel(workbook);
			
			
			eventMediaEntity.setEventMasterEntity(eventMasterEntity);
			eventMasterEntity.setEventMediaEntity(eventMediaEntity);
			eventMasterEntity.setEventAttendeeEntityList(eventAttendeeEntityList);
			
			for(EventAttendeeEntity eventAttendee : eventAttendeeEntityList) {
				eventAttendee.setEventMasterEntity(eventMasterEntity);
			}
			
			eventMasterRepository.save(eventMasterEntity);
			
			
		} catch (IOException ioException) {
			System.err.println(ioException.getMessage());
			ioException.getStackTrace();
		}

		return GenericResponse.builder().result("success").responseCode("00").message("event created successfully.")
				.build();
	}

	private EventMasterEntity checkEventAlreadyExist(String eventName) {
		return eventMasterRepository.findByEventName(eventName);
	}

	private List<EventAttendeeEntity> takeInputDataFromExcel(Workbook workbook) {
		List<EventAttendeeEntity> eventAttendeeEntityList = new ArrayList<>();
		Iterator<Row> iteratorRow = workbook.getSheetAt(0).iterator();
		iteratorRow.next();

		while (iteratorRow.hasNext()) {

			EventAttendeeEntity eventAttendeeEntity = EventAttendeeEntity.builder().build();

			Row row = iteratorRow.next();
			DataFormatter dataFormatter = new DataFormatter();

			for (int i = 0; i < excelProperties.getAttendeeUploadHeaders().size(); ++i) {
				Cell cell = row.getCell(i);

				switch (i) {
				case 0:
					eventAttendeeEntity.setAttName(dataFormatter.formatCellValue(cell).trim());
					break;
				case 1:
					eventAttendeeEntity.setContactNumber(dataFormatter.formatCellValue(cell).trim());
					break;
				case 2:
					eventAttendeeEntity.setBusinessTitle(dataFormatter.formatCellValue(cell).trim());
					break;
				case 3:
					eventAttendeeEntity.setCity(dataFormatter.formatCellValue(cell).trim());
					break;
				default:
					break;

				}
			}
			eventAttendeeEntityList.add(eventAttendeeEntity);
		}
		return eventAttendeeEntityList;

	}

	@Override
	public GenericResponse removeEventByName(String eventName) {
		
		Optional<EventMasterEntity> event = Optional.of(eventMasterRepository.findByEventName(eventName));
	    
	    if (event.isPresent()) {
	        eventMasterRepository.deleteByEventName(eventName);
	    } else {
	        throw new EntityNotFoundException("Event with name " + eventName + " not found");
	    }
		return null;
	}

	@Override
	public long getTotalEventCount() {
		return eventMasterRepository.count();
	}

}
