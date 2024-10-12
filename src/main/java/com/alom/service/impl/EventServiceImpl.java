package com.alom.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import com.alom.exception.EntityNotFoundException;
import com.alom.exception.ExcelFileReadWriteException;
import com.alom.mapper.ManualMapperService;
import com.alom.model.AttendeeModel;
import com.alom.model.EventMasterModel;
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

	public EventServiceImpl(EventMasterRepository eventMasterRepository, ExcelProperties excelProperties) {
		this.eventMasterRepository = eventMasterRepository;
		this.excelProperties = excelProperties;
	}

	@Override
	public PaginationResponse<EventMasterModel> getAllEvents(Pageable pageable) {
		PaginationResponse<EventMasterModel> response = null;
			response = (PaginationResponse<EventMasterModel>) redisTemplate.opsForHash().get(ApiResponseMessage.REDIS_PARTITION_KEY, ApiResponseMessage.REDIS_FIND_ALL_KEY);
			 
//			 Type type = object.getClass();
//			 log.debug("type of redis template: {}", type);

		/**
		 * Check here if it is not in REDIS the search from database
		 */
		if (Objects.isNull(response)) {
				// Fetch paginated EventMasterEntity
				 Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findAll(pageable);
				 log.debug("EventMasterEntity: {}", eventEntityPage);
				 Page<EventMasterModel> entityPage = eventEntityPage.map(ManualMapperService::convertToDto);
				 response = new PaginationResponse<>(entityPage.getTotalElements(),entityPage.getContent(), entityPage.getTotalPages());
				 log.info("################### fetch data from database");
				 
				 redisTemplate.opsForHash().put(ApiResponseMessage.REDIS_PARTITION_KEY, ApiResponseMessage.REDIS_FIND_ALL_KEY, response);
		}
		 

		/**
		 * Here get all event and get one by one event and get all attendees for the
		 * specific even. And send data for write excel file for that.
		 */
		List<EventMasterModel> eventMasterDtoList = response.getData();

		eventMasterDtoList.forEach(event -> {
			try {
				ExcelHelper.writeAttendeesToExcel(event.getAttendeeList(),"D:/media/" + event.getEventName() + " attendees.xlsx",excelProperties.getAttendeeUploadHeaders());
				Files.write(Paths.get("D:/media/" + event.getFileName()), event.getFileDate());
				event.setAttendeeList(null);
				event.setFileDate(null);
				event.setFileName(null);
				event.setFileType(null);
				event.setEventCreatedAt(null);
				event.setFileId(null);
			} catch (IOException e) {
				throw new ExcelFileReadWriteException(ApiResponseMessage.FAILED_TO_WRITE_EXCEL_FILE);
			}
		});
		
		
		
		return response;
	}

	@Override
	public EventMasterModel getEventByName(String eventName) {
		EventMasterEntity eventMasterEntity = null;
		EventMasterModel eventMasterDto = null;

		try {
			eventMasterDto = (EventMasterModel) redisTemplate.opsForHash().get(ApiResponseMessage.REDIS_PARTITION_KEY, eventName);
			log.debug("redisData:{}", eventMasterDto);

			if (Objects.isNull(eventMasterDto)) {
				eventMasterEntity = eventMasterRepository.findByEventName(eventName);
				eventMasterDto = ManualMapperService.convertToDto(eventMasterEntity);
//			eventMasterDto.getAttendeeList().clear();
				redisTemplate.opsForHash().put(ApiResponseMessage.REDIS_PARTITION_KEY, eventName, eventMasterDto);
				log.debug("get data from sql database and put it into redis : {}", eventMasterEntity);

			}

			List<AttendeeModel> attendeeList = eventMasterDto.getAttendeeList();
			ExcelHelper.writeAttendeesToExcel(attendeeList, "D:/media/attendees.xlsx",
					excelProperties.getAttendeeUploadHeaders());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return eventMasterDto;
	}

	@Override
	public PaginationResponse<EventMasterModel> getEventBetween(Date fromEventDate, Date uptoEventDate,	int page, int size) {

		// Create a PageRequest object for pagination
		PageRequest pageRequest = PageRequest.of(page, size);		
		
		PaginationResponse<EventMasterModel> response = null;
		  response = (PaginationResponse<EventMasterModel>) redisTemplate.opsForHash().get(ApiResponseMessage.REDIS_PARTITION_KEY, ApiResponseMessage.REDIS_FIND_DATE_RANGE_KEY);
	
	/**
	 * Check here if it is not in REDIS the search from database
	 */
	if (Objects.isNull(response)) {
			// Fetch paginated EventMasterEntity
			 Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventDateBetween(fromEventDate, uptoEventDate,pageRequest);
			 log.debug("EventMasterEntity: {}", eventEntityPage);
			 Page<EventMasterModel> entityPage = eventEntityPage.map(ManualMapperService::convertToDto);
			 response = new PaginationResponse<>(entityPage.getTotalElements(),entityPage.getContent(), entityPage.getTotalPages());
			 log.info("################### fetch data from database");
			 
			 redisTemplate.opsForHash().put(ApiResponseMessage.REDIS_PARTITION_KEY, ApiResponseMessage.REDIS_FIND_DATE_RANGE_KEY, response);
	}
	 

	/**
	 * Here get all event and get one by one event and get all attendees for the
	 * specific even. And send data for write excel file for that.
	 */
	List<EventMasterModel> eventMasterDtoList = response.getData();

	eventMasterDtoList.forEach(event -> {
		try {
			ExcelHelper.writeAttendeesToExcel(event.getAttendeeList(),"D:/media/" + event.getEventName() + " attendees.xlsx",excelProperties.getAttendeeUploadHeaders());
			Files.write(Paths.get("D:/media/" + event.getFileName()), event.getFileDate());
			event.setAttendeeList(null);
			event.setFileDate(null);
			event.setFileName(null);
			event.setFileType(null);
			event.setEventCreatedAt(null);
			event.setFileId(null);
		} catch (IOException e) {
			throw new ExcelFileReadWriteException(ApiResponseMessage.FAILED_TO_WRITE_EXCEL_FILE);
		}
	});
	
	
	
	return response;

	}

	@Override
	public GenericResponse addOrUpdateEvent(MultipartFile mediaFile, MultipartFile excelFile, String jsonData) {

		
		try (InputStream inputStreamForExcelFile = excelFile.getInputStream();
				Workbook workbook = WorkbookFactory.create(inputStreamForExcelFile);
				InputStream inputStreamForMediaFile = excelFile.getInputStream()) {
			 ObjectMapper objectMapper = new ObjectMapper();
			EventMasterModel eventModel = objectMapper.readValue(jsonData, EventMasterModel.class);
			log.debug("after mapped the object from json string to eventMasterModel:{} ", eventModel);
			
			EventMasterEntity eventMasterEntity = this.checkEventAlreadyExist(eventModel.getEventName());
			eventModel.setFileName(mediaFile.getOriginalFilename());
			eventModel.setFileType(mediaFile.getContentType());
			eventModel.setFileDate(mediaFile.getBytes());
			
//			/**
//			 * Set data as Blob instate of byte[] for fast retrival
//			 */
//			EventMediaEntity eventMediaEntity = EventMediaEntity.builder().fileType(eventModel.getFileType())
//					.fileName(eventModel.getFileName()).fileData(new SerialBlob(mediaFile.getBytes())).uploadedAt(new Date()).build();
//
//			
			
			/**
			 * validate the file if it is xlsx file.
			 */
			ExcelHelper.validateFileExtention(excelFile.getOriginalFilename());
			
			/**
			 * validate file headers if it is match with provided heeder set
			 */
			ExcelHelper.validateHeaderContents(workbook, excelProperties.getAttendeeUploadHeaders());

			List<AttendeeModel> attendeeModelList = this.takeInputDataFromExcel(workbook);
			eventModel.setAttendeeList(attendeeModelList);
			
			/**
			 * After mapping model to entity.
			 */
			eventMasterEntity = ManualMapperService.convertToEntity(eventModel);
			log.debug("prepared object to persist eventMasterEntity:{} ", eventMasterEntity);
			
			/**
			 * use this for hibernate mapping
			 * 
			 */
			List<EventAttendeeEntity> eventAttendeeEntityList = eventMasterEntity.getEventAttendeeEntityList();
			
			for (EventAttendeeEntity eventAttendeeEntity : eventAttendeeEntityList) {
				eventAttendeeEntity.setEventMasterEntity(eventMasterEntity);
			}
			
			EventMediaEntity eventMediaEntity = eventMasterEntity.getEventMediaEntity();
			eventMediaEntity.setEventMasterEntity(eventMasterEntity);
			
			eventMasterRepository.save(eventMasterEntity);

		} catch (IOException ioException) {
			log.debug("ioException occured:{}", ioException.getMessage());

		}

		return GenericResponse.builder().result("success").responseCode("00").message("event created successfully.")
				.build();
	}

	private EventMasterEntity checkEventAlreadyExist(String eventName) {
		return eventMasterRepository.findByEventName(eventName);
	}

	private List<AttendeeModel> takeInputDataFromExcel(Workbook workbook) {
		List<AttendeeModel> eventAttendeeModelList = new ArrayList<>();
		Iterator<Row> iteratorRow = workbook.getSheetAt(0).iterator();
		iteratorRow.next();

		while (iteratorRow.hasNext()) {

			AttendeeModel eventAttendeeMode = AttendeeModel.builder().build();

			Row row = iteratorRow.next();
			DataFormatter dataFormatter = new DataFormatter();

			for (int i = 0; i < excelProperties.getAttendeeUploadHeaders().size(); ++i) {
				Cell cell = row.getCell(i);

				switch (i) {
				case 0:
					eventAttendeeMode.setName(dataFormatter.formatCellValue(cell).trim());
					break;
				case 1:
					eventAttendeeMode.setContactNumber(dataFormatter.formatCellValue(cell).trim());
					break;
				case 2:
					eventAttendeeMode.setBusinessTitle(dataFormatter.formatCellValue(cell).trim());
					break;
				case 3:
					eventAttendeeMode.setCity(dataFormatter.formatCellValue(cell).trim());
					break;
				default:
					break;

				}
			}
			eventAttendeeModelList.add(eventAttendeeMode);
		}
		return eventAttendeeModelList;

	}

	@Override
	public GenericResponse removeEventByName(String eventName) {
		redisTemplate.opsForHash().delete(ApiResponseMessage.REDIS_PARTITION_KEY,ApiResponseMessage.REDIS_FIND_ALL_KEY);
		redisTemplate.opsForHash().delete(ApiResponseMessage.REDIS_PARTITION_KEY,ApiResponseMessage.REDIS_FIND_DATE_RANGE_KEY);
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
