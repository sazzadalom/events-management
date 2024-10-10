package com.alom.constant;


/**
 * @author sazzad
 * @version 1.0.0
 * @since 10-10-2024
 */
public class ApiResponseCode {
	private ApiResponseCode() {}

	public static final String SUCCESS = "00";
	public static final String FAILED = "100";

	public static final String AUTHORIZATION_FAILED = "110";
	public static final String INVALID_HEADER_FIELDS = "111";
	public static final String TECHNICAL_ERROR = "112";
	public static final String INETNAL_SERVER_ERROR = "113";
	public static final String NOT_FOUND = "114";
	public static final String JSON_MAPPING_ERROR = "115";
	public static final String EXCEL_FILE_READING = "the excel file is not readable";
}
