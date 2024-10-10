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
}
