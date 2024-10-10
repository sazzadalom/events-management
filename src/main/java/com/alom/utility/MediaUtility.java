package com.alom.utility;

import java.io.InputStream;
import java.sql.Blob;
import java.util.Base64;

import com.alom.dao.entities.EventMasterEntity;
import com.alom.dto.EventMasterDto;
import com.alom.dto.EventMediaDto;

public class MediaUtility {

	public static EventMasterDto convertToDto(EventMasterEntity entity) {
        EventMasterDto dto = new EventMasterDto();
        dto.setEventId(entity.getEventId());
        dto.setEventName(entity.getEventName());
        dto.setEventUrl(entity.getEventUrl());
        dto.setEventDate(entity.getEventDate());
        dto.setEventCreatedAt(entity.getEventCreatedAt());

        if (entity.getEventMediaEntity() != null) {
            EventMediaDto mediaDTO = new EventMediaDto();
            mediaDTO.setFileId(entity.getEventMediaEntity().getFileId());
            mediaDTO.setFileType(entity.getEventMediaEntity().getFileType());
            mediaDTO.setFileName(entity.getEventMediaEntity().getFileName());
            mediaDTO.setUploadedAt(entity.getEventMediaEntity().getUploadedAt());

         // Retrieve and convert Blob to Base64
            Blob blob = entity.getEventMediaEntity().getFileData();
            if (blob != null) {
                try {
                    InputStream inputStream = blob.getBinaryStream();
                    byte[] byteArray = inputStream.readAllBytes();
                    String base64Data = Base64.getEncoder().encodeToString(byteArray);
                    mediaDTO.setFileData(base64Data); // Set Base64-encoded data
                    
                } catch (Exception e) {
                    // Handle exception (e.g., log the error)
                    mediaDTO.setFileData(null); // or some error indicator
                }
            }

            
            dto.setEventMediaEntity(mediaDTO);
        }

        return dto;
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
