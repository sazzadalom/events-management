package com.alom.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventMasterDto {
	private Long eventId;
	
	private String eventName;
	
	private String eventUrl;
	
	private LocalDateTime eventDate;
	
	private LocalDateTime eventCreatedAt;
}
