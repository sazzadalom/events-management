package com.alom.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alom.configuration.properties.ExcelProperties;
import com.alom.constant.ApiResponseMessage;
import com.alom.dao.entities.EventAttendeeEntity;
import com.alom.dao.entities.EventMasterEntity;
import com.alom.dao.entities.EventMediaEntity;
import com.alom.dao.repositories.EventMasterRepository;
import com.alom.dto.AttendeeDto;
import com.alom.dto.EventMasterDto;
import com.alom.exception.EntityNotFoundException;
import com.alom.exception.ExcelFileReadWriteException;
import com.alom.mapper.ManualMapperService;
import com.alom.model.EventModel;
import com.alom.model.PaginationResponse;
import com.alom.payload.GenericResponse;
import com.alom.service.EventService;
import com.alom.utility.ExcelHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class EventServiceImpl implements EventService {

	private final EventMasterRepository eventMasterRepository;
	private final ExcelProperties excelProperties;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String REDIS_PARTITION_KEY = "event";
	private static final String REDIS_FIND_ALL_KEY = "events";

	public EventServiceImpl(EventMasterRepository eventMasterRepository, ExcelProperties excelProperties) {
		this.eventMasterRepository = eventMasterRepository;
		this.excelProperties = excelProperties;
	}

	@Override
	public PaginationResponse<EventMasterDto> getAllEvents(Pageable pageable) {
		PaginationResponse<EventMasterDto> response = null;
		 Long totalCountOfRedis = redisTemplate.opsForHash().size(REDIS_PARTITION_KEY);
		 
		 if(this.getTotalEventCount() == totalCountOfRedis) {
			  response = (PaginationResponse<EventMasterDto>) redisTemplate.opsForHash().get(REDIS_PARTITION_KEY, REDIS_FIND_ALL_KEY);
		 }
		
		/**
		 * Check here if it is not in REDIS the search from database
		 */
		if (Objects.isNull(response)) {
				// Fetch paginated EventMasterEntity
				 Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findAll(pageable);
				 log.debug("EventMasterEntity: {}", eventEntityPage);
				 Page<EventMasterDto> entityPage = eventEntityPage.map(ManualMapperService::convertToDto);
				 response = new PaginationResponse<>(entityPage.getTotalElements(),entityPage.getContent(), entityPage.getTotalPages());
				 log.info("################### fetch data from database");
				 
				 redisTemplate.opsForHash().put(REDIS_PARTITION_KEY, REDIS_FIND_ALL_KEY, response);
		}
		 

		/**
		 * Here get all event and get one by one event and get all attendees for the
		 * specific even. And send data for write excel file for that.
		 */
		List<EventMasterDto> eventMasterDtoList = response.getData();

		eventMasterDtoList.forEach(event -> {
			try {
				ExcelHelper.writeAttendeesToExcel(event.getAttendeeList(),
						"D:/media/" + event.getEventName() + " attendees.xlsx",
						excelProperties.getAttendeeUploadHeaders());
			} catch (IOException e) {
				throw new ExcelFileReadWriteException(ApiResponseMessage.FAILED_TO_WRITE_EXCEL_FILE);
			}
		});
		
		return response;
	}

	@Override
	public EventMasterDto getEventByName(String eventName) {
		EventMasterEntity eventMasterEntity = null;
		EventMasterDto eventMasterDto = null;

		try {
			eventMasterDto = (EventMasterDto) redisTemplate.opsForHash().get(REDIS_PARTITION_KEY, eventName);
			log.debug("redisData:{}", eventMasterDto);

//			eventMasterDto = (EventMasterDto) redisData;
			log.debug("eventMasterDto:{}", eventMasterDto);

			if (Objects.isNull(eventMasterDto)) {
				eventMasterEntity = eventMasterRepository.findByEventName(eventName);
				eventMasterDto = ManualMapperService.convertToDto(eventMasterEntity);
//			eventMasterDto.getAttendeeList().clear();
				redisTemplate.opsForHash().put(REDIS_PARTITION_KEY, eventName, eventMasterDto);
				log.debug("get data from sql database and put it into redis : {}", eventMasterEntity);

			}

			List<AttendeeDto> attendeeList = eventMasterDto.getAttendeeList();
			ExcelHelper.writeAttendeesToExcel(attendeeList, "D:/media/attendees.xlsx",
					excelProperties.getAttendeeUploadHeaders());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return eventMasterDto;
	}

	@Override
	public PaginationResponse<EventMasterDto> getEventBetween(LocalDate fromEventDate, LocalDate uptoEventDate,
			int page, int size) {
		// Convert LocalDate to LocalDateTime (if your eventDate is LocalDateTime)
		LocalDateTime fromDateTime = fromEventDate.atStartOfDay();
		LocalDateTime toDateTime = uptoEventDate.atTime(23, 59, 59); // Include the whole day

		// Create a PageRequest object for pagination
		PageRequest pageRequest = PageRequest.of(page, size);

		// Fetch the events within the date range
		Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventDateBetween(fromDateTime, toDateTime,
				pageRequest);

		Page<EventMasterDto> entityPage = eventEntityPage.map(ManualMapperService::convertToDto);

		// Create the custom response
		PaginationResponse<EventMasterDto> response = new PaginationResponse<>(entityPage.getTotalElements(),
				entityPage.getContent(), entityPage.getTotalPages());

		return response; // MapStruct will handle mapping of each

	}

	@Override
	public GenericResponse addOrUpdateEvent(MultipartFile multipartFile, String jsonData) {

		try (InputStream inputStream = multipartFile.getInputStream();
				Workbook workbook = WorkbookFactory.create(inputStream)) {

			ObjectMapper objectMapper = new ObjectMapper();
			EventModel eventModel = objectMapper.readValue(jsonData, EventModel.class);
			EventMasterEntity eventMasterEntity = this.checkEventAlreadyExist(eventModel.getEventName());

			Path path = Paths.get(eventModel.getImagePath());
			String fileName = path.getFileName().toString();

			byte[] data = Files.readAllBytes(path);

			if (Objects.isNull(eventMasterEntity)) {
				eventMasterEntity = EventMasterEntity.builder().eventName(eventModel.getEventName())
						.eventUrl(eventModel.getEventWebLink()).eventDate(eventModel.getEventDate())
						.eventCreatedAt(new Date()).build();
			}

			/**
			 * Set data as Blob instate of byte[] for fast retrival
			 */
			Blob blob = new SerialBlob(data);

			EventMediaEntity eventMediaEntity = EventMediaEntity.builder().fileType(eventModel.getEventType())
					.fileName(fileName).fileData(blob).uploadedAt(new Date()).build();

			ExcelHelper.validateFileExtention(multipartFile.getOriginalFilename());

			ExcelHelper.validateHeaderContents(workbook, excelProperties.getAttendeeUploadHeaders());

			List<EventAttendeeEntity> eventAttendeeEntityList = this.takeInputDataFromExcel(workbook);

			eventMediaEntity.setEventMasterEntity(eventMasterEntity);
			eventMasterEntity.setEventMediaEntity(eventMediaEntity);
			eventMasterEntity.setEventAttendeeEntityList(eventAttendeeEntityList);

			for (EventAttendeeEntity eventAttendee : eventAttendeeEntityList) {
				eventAttendee.setEventMasterEntity(eventMasterEntity);
			}

			eventMasterRepository.save(eventMasterEntity);

		} catch (IOException | SQLException ioException) {
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
