package com.alom.events.mapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;

import com.alom.events.dao.entities.EventAttendeeEntity;
import com.alom.events.dao.entities.EventMasterEntity;
import com.alom.events.dao.entities.EventMediaEntity;
import com.alom.events.model.AttendeeModel;
import com.alom.events.model.EventMasterModel;

public class ManualMapperService {

	public static EventMasterModel convertToModel(EventMasterEntity entity) {
        EventMasterModel model = new EventMasterModel();
        model.setEventId(entity.getEventId());
        model.setEventName(entity.getEventName());
        model.setEventUrl(entity.getEventUrl());
        model.setEventDate(entity.getEventDate());
        model.setEventCreatedAt(entity.getEventCreatedAt());

        if (entity.getEventMediaEntity() != null) {
        	model.setFileId(entity.getEventMediaEntity().getFileId());
        	model.setFileType(entity.getEventMediaEntity().getFileType());
        	model.setFileName(entity.getEventMediaEntity().getFileName());

			try {
				model.setFileDate(entity.getEventMediaEntity().getFileData().getBinaryStream().readAllBytes());
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
 /* 
         // Retrieve and convert Blob to Base64
            Blob blob = entity.getEventMediaEntity().getFileData();

            if (blob != null) {
                try {
                    InputStream inputStream = blob.getBinaryStream();
                    byte[] byteArray = inputStream.readAllBytes();
                    String base64Data = Base64.getEncoder().encodeToString(byteArray);
                 // Decode the Base64 string to byte array
                    byte[] bytes = Base64.getDecoder().decode(base64Data);
                    log.debug("******************bytes : ", bytes);

                    dto.setFileDate(entity.getEventMediaEntity().getFileData()); // Set Base64-encoded data
                    
                } catch (Exception e) {
                    // Handle exception (e.g., log the error)
                    dto.setFileDate(null); // or some error indicator
                }
            }
  */

        }
        
        List<AttendeeModel> attendeeDtoList= new ArrayList<>();
        entity.getEventAttendeeEntityList().forEach(attendee -> {
        	attendeeDtoList .add (new AttendeeModel(attendee.getAttId(),attendee.getAttName(),attendee.getBusinessTitle(),attendee.getCity(),attendee.getContactNumber()));
        	
        });
        model.setAttendeeList(attendeeDtoList);

        return model;
    }
	
	public static EventMasterEntity convertToEntity( EventMasterModel model) {
		Date createdOn = new Date();
		EventMediaEntity eventMediaEntity = EventMediaEntity.builder().fileId(model.getFileId()).fileName(model.getFileName()).fileType(model.getFileType()).fileData(BlobProxy.generateProxy(model.getFileDate())).uploadedAt(createdOn).build();
		List<EventAttendeeEntity> eventAttendeeEntityList = new ArrayList<>();
		model.getAttendeeList().forEach(attendee -> {
			eventAttendeeEntityList.add(EventAttendeeEntity.builder().attId(attendee.getAttId()).attName(attendee.getName()).contactNumber(attendee.getContactNumber()).businessTitle(attendee.getBusinessTitle()).city(attendee.getCity()).build());
		});
		
		EventMasterEntity entity = EventMasterEntity.builder()
				.eventId(model.getEventId())
				.eventName(model.getEventName())
				.eventUrl(model.getEventUrl())
				.eventDate(model.getEventDate())
				.eventCreatedAt(createdOn)
				.eventMediaEntity(eventMediaEntity)
				.eventAttendeeEntityList(eventAttendeeEntityList)
				.build();
        
        

        return entity;
    }
	
	
	/**
	 * this methhod is used to decode to byte array
	 * @param base64EncodedData
	 * @return
	 */
	public static byte[] decodeToBlob(String base64EncodedData) {
		return  Base64.getDecoder().decode(base64EncodedData);
	}
}
