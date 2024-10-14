package com.alom.events.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alom.configuration.properties.ExcelProperties;
import com.alom.constant.ApiResponseCode;
import com.alom.constant.ApiResponseMessage;
import com.alom.constant.Result;
import com.alom.events.dao.entities.EventAttendeeEntity;
import com.alom.events.dao.entities.EventMasterEntity;
import com.alom.events.dao.entities.EventMediaEntity;
import com.alom.events.dao.repositories.EventMasterRepository;
import com.alom.events.mapper.ManualMapperService;
import com.alom.events.model.AttendeeModel;
import com.alom.events.model.EventMasterModel;
import com.alom.events.model.PaginationResponse;
import com.alom.events.service.EventService;
import com.alom.events.service.RedisService;
import com.alom.exception.ExcelFileReadWriteException;
import com.alom.payload.GenericResponse;
import com.alom.utility.ExcelHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class EventServiceImpl implements EventService {

	private final EventMasterRepository eventMasterRepository;
	private final ExcelProperties excelProperties;

	private RedisService redisService;

	public EventServiceImpl(EventMasterRepository eventMasterRepository, ExcelProperties excelProperties,RedisService redisService) {
		this.eventMasterRepository = eventMasterRepository;
		this.excelProperties = excelProperties;
		this.redisService = redisService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PaginationResponse<EventMasterModel> getAllEvents(int page, int size) {

		PaginationResponse<EventMasterModel> response = (PaginationResponse<EventMasterModel>) redisService.retrieveHashObject(ApiResponseMessage.REDIS_PARTITION_KEY, ApiResponseMessage.REDIS_FIND_ALL_KEY);
			 
		/**
		 * Check here if it is not in REDIS the search from database
		 */
		if (Objects.isNull(response) || response.getTotalRecord() <= 0) {
			
				// Fetch paginated EventMasterEntity
				 Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findAll(PageRequest.of(page, size));
				 log.debug("EventMasterEntity: {}", eventEntityPage);
				 Page<EventMasterModel> entityPage = eventEntityPage.map(ManualMapperService::convertToModel);
				 response = new PaginationResponse<>(entityPage.getTotalElements(),entityPage.getContent(), entityPage.getTotalPages());
				 log.info("################### fetch data from database ###################");
				 
				 redisService.addHashObject(ApiResponseMessage.REDIS_PARTITION_KEY, ApiResponseMessage.REDIS_FIND_ALL_KEY, response);
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
	public PaginationResponse<EventMasterModel> getEventByName(String eventName, int page, int size) {
		// Create a PageRequest object for pagination
		PageRequest pageRequest = PageRequest.of(page, size);		
		
		@SuppressWarnings("unchecked")
		PaginationResponse<EventMasterModel>  response = (PaginationResponse<EventMasterModel>) redisService.retrieveHashObject(ApiResponseMessage.REDIS_PARTITION_KEY, eventName);
		
	/**
	 * Check here if it is not in REDIS the search from database
	 */
	if (Objects.isNull(response) || response.getTotalRecord() <= 0) {
			// Fetch paginated EventMasterEntity
			 Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventName(eventName, pageRequest);
			 log.debug("after fetch data from event master entity using event name: {}", eventEntityPage);
			 
			 Page<EventMasterModel> entityPage = eventEntityPage.map(ManualMapperService::convertToModel);
			 
			 
			 response = new PaginationResponse<>(entityPage.getTotalElements(),entityPage.getContent(), entityPage.getTotalPages());
			 log.info("################### fetch data from database ###################");
			 
			 redisService.addHashObject(ApiResponseMessage.REDIS_PARTITION_KEY, eventName, response);
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

	@SuppressWarnings("unchecked")
	@Override
	public PaginationResponse<EventMasterModel> getEventBetween(Date fromEventDate, Date uptoEventDate,	int page, int size) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		String fromtDate = dateFormat.format(fromEventDate) ;
		String uptoDate = dateFormat.format(uptoEventDate);
		String fieldKey = fromtDate + uptoDate;
		
		// Create a PageRequest object for pagination
		PageRequest pageRequest = PageRequest.of(page, size);		
		
		PaginationResponse<EventMasterModel>  response = (PaginationResponse<EventMasterModel>) redisService.retrieveHashObject(ApiResponseMessage.REDIS_PARTITION_KEY, fieldKey);
	
	/**
	 * Check here if it is not in REDIS the search from database
	 */
	if (Objects.isNull(response) || response.getTotalRecord() <= 0) {
			// Fetch paginated EventMasterEntity
			 Page<EventMasterEntity> eventEntityPage = eventMasterRepository.findByEventDateBetween(fromEventDate, uptoEventDate,pageRequest);
			 log.debug("EventMasterEntity: {}", eventEntityPage);
			 Page<EventMasterModel> entityPage = eventEntityPage.map(ManualMapperService::convertToModel);
			 response = new PaginationResponse<>(entityPage.getTotalElements(),entityPage.getContent(), entityPage.getTotalPages());
			 log.info("################### fetch data from database");
			 
			 redisService.addHashObject(ApiResponseMessage.REDIS_PARTITION_KEY, fieldKey, response);
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
	public GenericResponse addEvent(MultipartFile mediaFile, MultipartFile excelFile, String jsonData) {

		
		try (InputStream inputStreamForExcelFile = excelFile.getInputStream();
				Workbook workbook = WorkbookFactory.create(inputStreamForExcelFile);
				InputStream inputStreamForMediaFile = excelFile.getInputStream()) {
			 ObjectMapper objectMapper = new ObjectMapper();
			EventMasterModel eventModel = objectMapper.readValue(jsonData, EventMasterModel.class);
			log.debug("after mapped the object from json string to eventMasterModel:{} ", eventModel);
			
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

			List<AttendeeModel> attendeeModelList = ExcelHelper.takeInputDataFromExcel(workbook, excelProperties.getAttendeeUploadHeaders());
			eventModel.setAttendeeList(attendeeModelList);
			
			
			/**
			 * Jakatra validation done here
			 */
			Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
			Set<ConstraintViolation<EventMasterModel>> violations = validator.validate(eventModel);

			if (!violations.isEmpty()) {
			    String errorMessage = violations.stream()
			        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
			        .collect(Collectors.joining(", "));
			    throw new ConstraintViolationException("Validation failed: " + errorMessage, violations);
			}

			
			/**
			 * After mapping model to entity.
			 */
			EventMasterEntity eventMasterEntity = ManualMapperService.convertToEntity(eventModel);
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
		
		redisService.clear();
		
		return GenericResponse.builder().result("success").responseCode("00").message("event modification request successfull.")
				.build();
	}

	@Override
	public GenericResponse removeEventByName(String eventName) {

		long totalCount = eventMasterRepository.findByEventName(eventName, PageRequest.of(0, 10)).getTotalElements();
		if (totalCount > 0) {
			eventMasterRepository.deleteByEventName(eventName);
			redisService.clear();
			return GenericResponse.builder().result(Result.SUCCESS).responseCode(ApiResponseCode.SUCCESS).message(ApiResponseMessage.EVENT_REMOVED_SUCCESSFULLY).build();
		}
		 return GenericResponse.builder().result(Result.FAILED).responseCode(ApiResponseCode.FAILED).message(ApiResponseMessage.EVENT_NOT_FOUNDED).build();
	}

	@Override
	public long getTotalEventCount() {
		return eventMasterRepository.count();
	}

	@Override
	public GenericResponse editEvent(Long eventId, String jsonData, MultipartFile mediaFile, MultipartFile excelFile) {
		log.debug("jsonData:{}",jsonData);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonDataMap = objectMapper.readValue(jsonData, HashMap.class);
			jsonDataMap.put("eventId", eventId);
			
			jsonData = objectMapper.writeValueAsString(jsonDataMap);
			
		} catch (JsonProcessingException e) {
		
		}

		return addEvent(mediaFile, excelFile, jsonData);
	}

}
