package com.alom.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class EventMasterModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long eventId;
	
    private String eventName;
	
    private String eventUrl;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventDate;
    
    
    private Long fileId;
    private String fileType;
    private String fileName;
    private byte[] fileDate;
    private Date eventCreatedAt;
    private List<AttendeeModel> attendeeList;
    

    public EventMasterModel(String eventName, String eventUrl, Date eventDate) {
    	this.eventName = eventName;
    	this.eventUrl = eventUrl;
    	this.eventDate = eventDate;
    }
}

