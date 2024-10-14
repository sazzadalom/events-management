package com.alom.constant;

/**
 * <p>This class is use to define contant message.</p>
 * 
 * @author sazzad.alom
 * @version 1.0.0
 * @since 09-10-2024
 */
public class ApiResponseMessage {
	private ApiResponseMessage() {
	}

	public static final String SUCCESS = "success";
	public static final String FAILURE = "failed";
	
	
	public static final String FAILED_TO_LOGGED_IN = "failed to logged in";
	public static final String UN_SUPPORTED_MEDIA_TYPE = "media type is not supported";
	public static final Object JSON_PARSE_ERROR = "json perser error";
	public static final Object JSON_MAPPING_ERROR = "json mapping error";
	public static final Object MESSAGE_NOT_REDABLE = "message is not readable";
	public static final String METHOD_NOT_ALLLOWED = "method is not allowed";
	public static final String USER_REGISTERED_SUCCESSFULLY = "user has been registered successfully.";
	public static final String USER_REGISTRATION_FAILED = "user is not registered";
	public static final String USER_ALREADY_EXIST = "user details already exist, please login";
	public static final String FAILED_TO_WRITE_EXCEL_FILE = "failed to write excel file";
	public static final String REDIS_HASH_KEY = "event";
	public static final String REDIS_FIND_DATE_RANGE_KEY = "events_daterange";
	public static final String REDIS_FIND_ALL_KEY = "events";
	
	public static final String CONTACT_NUMBER_PATTERN =  "^[789]\\d{9}$";
	public static final String EVENT_REMOVED_SUCCESSFULLY = "event has been successfully removed from the inventory. ";
	public static final String EVENT_NOT_FOUNDED = "event did not found in the inventory. ";
	public static final String REDIS_CLEAR_SUCCESSFULLY = "redis memory has been clear successfully.";
	

}
