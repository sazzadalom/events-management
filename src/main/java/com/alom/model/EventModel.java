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
public class EventModel {
	private String EventName;
	private LocalDateTime eventDate;
	private String eventType;
	private String imagePath;
	private String attendeePath;
	private String eventWebLink;
}
