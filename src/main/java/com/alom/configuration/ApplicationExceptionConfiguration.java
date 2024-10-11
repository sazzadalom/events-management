package com.alom.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.alom.constant.ApiResponseCode;
import com.alom.constant.ApiResponseMessage;
import com.alom.constant.Result;
import com.alom.exception.ExcelFileReadWriteException;
import com.alom.exception.FileNotFoundException;
import com.alom.exception.InternalServerError;
import com.alom.payload.GenericResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;

/**
 * this class is used to handle globale exception
 * @author sazza
 * @version 1.0.0
 * @since 10-10-2024
 *
 */
@Log4j2
@ControllerAdvice
public class ApplicationExceptionConfiguration extends ResponseEntityExceptionHandler {
	

	@ExceptionHandler(InternalServerError.class)
	public ResponseEntity<GenericResponse> InternalServerError(Exception exception) {
		log.error("exception occured: {}", exception.getMessage());

		return new ResponseEntity<>(GenericResponse.builder()
				.message(exception.getMessage())
				.result(Result.FAILED)
				.responseCode(ApiResponseCode.INETNAL_SERVER_ERROR)
				.build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<GenericResponse> globalExceptionHandler(Exception exception) {
		log.error("exception occured: {}", exception.getMessage());

		return new ResponseEntity<>(GenericResponse.builder()
				.message(exception.getMessage())
				.result(Result.FAILED)
				.responseCode(ApiResponseCode.INETNAL_SERVER_ERROR)
				.build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<GenericResponse> fileNotFoundExceptionHandler(FileNotFoundException exception) {
		log.error("exception occured: {}", exception.getMessage());

		return new ResponseEntity<>(GenericResponse.builder()
				.message(exception.getMessage())
				.result(Result.FAILED)
				.responseCode(ApiResponseCode.NOT_FOUND)
				.build(),
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<GenericResponse> constraintViolationExceptionHandler(ConstraintViolationException cve) {
		log.error("constraint violation exception handler: {}", cve.getMessage());

		StringBuilder sb = new StringBuilder();

		for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
			sb.append(violation.getMessage());
		}

		return new ResponseEntity<>(GenericResponse.builder().message(sb.toString()).result(Result.FAILED).responseCode("400").build(), 
				HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error("method argument exception: {}", methodArgumentNotValidException.getMessage());

		StringBuilder errorMessage = new StringBuilder();

		for (FieldError error : methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
			errorMessage.append(error.getField());
			errorMessage.append(" : ");
			errorMessage.append(error.getDefaultMessage());
			errorMessage.append(" ");
		}

		for (ObjectError error : methodArgumentNotValidException.getBindingResult().getGlobalErrors()) {
			errorMessage.append(error.getObjectName());
			errorMessage.append(" : ");
			errorMessage.append(error.getDefaultMessage());
			errorMessage.append(" ");
		}

		return handleExceptionInternal(methodArgumentNotValidException, 
				GenericResponse.builder()
				.result(Result.FAILED)
				.message(errorMessage.toString())
				.responseCode(ApiResponseCode.FAILED)
				.build(),
				headers, 
				HttpStatus.BAD_REQUEST, 
				request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {


		log.error("media type not allowed exception: {}", httpMediaTypeNotSupportedException.getMessage());

		return handleExceptionInternal(httpMediaTypeNotSupportedException,
				GenericResponse.builder()
				.result(Result.FAILED)
				.message(ApiResponseMessage.UN_SUPPORTED_MEDIA_TYPE)
				.build(),
				headers,
				HttpStatus.UNSUPPORTED_MEDIA_TYPE,
				request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException httpMessageNotReadableException,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {


		log.error("message not readable exception: {}", httpMessageNotReadableException.getMessage());

		StringBuilder errorMessage = new StringBuilder();		
		final Throwable cause = httpMessageNotReadableException.getCause();

		if (cause instanceof JsonParseException) {
			errorMessage.append(ApiResponseMessage.JSON_PARSE_ERROR);
		} else if (cause instanceof JsonMappingException) {
			errorMessage.append(ApiResponseMessage.JSON_MAPPING_ERROR);
		} else {
			errorMessage.append(ApiResponseMessage.MESSAGE_NOT_REDABLE);
		}

		return handleExceptionInternal(httpMessageNotReadableException,
				GenericResponse.builder()
				.result(Result.FAILED)
				.message(errorMessage.toString())
				.build(),
				headers,
				HttpStatus.BAD_REQUEST,
				request);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException methodNotSupportedException,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		log.error("method not supported exception: {}", methodNotSupportedException.getMessage());

		return handleExceptionInternal(methodNotSupportedException,
				GenericResponse.builder()
				.result(Result.FAILED)
				.message(ApiResponseMessage.METHOD_NOT_ALLLOWED)
				.build(), 
				headers, 
				HttpStatus.METHOD_NOT_ALLOWED,
				request);
	}

	@ExceptionHandler(ExcelFileReadWriteException.class)
	public ResponseEntity<GenericResponse> excelFileReadingException(ExcelFileReadWriteException exception) {
		log.error("exception occured: {}", exception.getMessage());

		return new ResponseEntity<>(GenericResponse.builder()
				.message(exception.getMessage())
				.result(Result.FAILED)
				.responseCode(ApiResponseCode.EXCEL_FILE_READING)
				.build(),
				HttpStatus.BAD_REQUEST);
	}
}
