package com.alom.dto;

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
public class AttendeeDto {
	private long attId;
	private String name;
	private String contactNumber;
	private String businessTitle;
	private String city;
}