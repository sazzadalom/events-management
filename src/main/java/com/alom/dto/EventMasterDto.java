package com.alom.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDate eventDate;
    private LocalDateTime eventCreatedAt;
    private EventMediaDto eventMedia;
    private List<AttendeeDto> attendeeList;

}

