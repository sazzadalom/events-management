package com.alom.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 
 * Generic response use for a basic format of response. 
 * 
 * @author sazzad.alom
 * @version 1.0.0
 * @since 09-10-24
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@SuperBuilder(toBuilder = true)
public class GenericResponse {
	private String result;
	private String responseCode;
	private String message;
}