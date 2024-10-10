package com.alom.dto;

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
public class EventMediaDto {
	private Long fileId;
    private String fileType;
    private String fileName;
    private LocalDateTime uploadedAt;
    private String fileData; // Add this field for the Base64-encoded data
}
