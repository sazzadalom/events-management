package com.alom.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * This will ensure that when Jackson serializes this object, it includes the @class property, which will help with deserialization.
 * @author sazzad
 *
 */

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true) // use for all properties which are not provided
@Schema(description = "Event Master Model")
public class EventMasterModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Schema(description = "The field is  not required it will managed by JPA(Java Persistance API) of the event", example = "0")
	private Long eventId;
	
	@Schema(description = "The field is required of the event", example = "Happy Holi")
    private String eventName;
	
	 @Schema(description = "The field is required of the event", example = "www.aurusit.com")
    private String eventUrl;
    
	 @Schema(description = "The field is required of the event", example = "2024-12-01 00:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventDate;
    
    
	 @Schema(description = "The field is  not required it will managed by JPA(Java Persistance API) of the event", example = "0")
    private Long fileId;
	 
	@Schema(description = "The field is  not required it will managed from file name of the event")
    private String fileType;
	
	@Schema(description = "The field is  not required it will managed from file name of the event")
    private String fileName;
	
	@Schema(description = "The field is  not required it will managed from actual file of the event")
    private byte[] fileDate;
	
	@Schema(description = "The field is  not required it will managed from system")
    private Date eventCreatedAt;
	
	@Schema(description = "The field is  not required it will managed from excel sheet of attendees")
    private List<AttendeeModel> attendeeList;
    

    public EventMasterModel(String eventName, String eventUrl, Date eventDate) {
    	this.eventName = eventName;
    	this.eventUrl = eventUrl;
    	this.eventDate = eventDate;
    }
}

