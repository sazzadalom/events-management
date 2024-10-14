package com.alom.events.model;

import java.io.Serializable;

import com.alom.events.annotations.ValidContactNumber;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long attId;
	
	@NotNull(message = "Attendee name cannot be null ")
	@Size(max = 50, message = "Name cannot exceed 50 characters.")
	private String name;
	
	@ValidContactNumber(message = "Invalida contact number ")
	private String contactNumber;
	
	@Size(max = 100, message = "Business title cannot exceed 100 characters.")
	private String businessTitle;
	
	@Size(max = 50, message = "City cannot exceed 50 characters.")
	private String city;
}