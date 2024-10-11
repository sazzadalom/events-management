package com.alom.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * This will ensure that when Jackson serializes this object, it includes the @class property, which will help with deserialization.
 * @author sazza
 *
 */
@JsonTypeInfo(
	    use = JsonTypeInfo.Id.CLASS, 
	    include = JsonTypeInfo.As.PROPERTY, 
	    property = "@class"
	)
@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventMasterDto implements Serializable{
  
	private static final long serialVersionUID = 1L;
	private Long eventId;
    private String eventName;
    private String eventUrl;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventDate;
    private Date eventCreatedAt;
    private EventMediaDto eventMedia;
    private List<AttendeeDto> attendeeList;

}

