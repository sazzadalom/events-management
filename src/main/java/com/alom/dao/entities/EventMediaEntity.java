package com.alom.dao.entities;

import java.sql.Blob;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_media")
public class EventMediaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "file_id")
	private Long fileId;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "file_name")
	private String fileName;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "file_data", columnDefinition = "LONGBLOB")
	private Blob fileData; // Using Blob for large data instead of byte[]

	@Column(name = "uploaded_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date uploadedAt;

	@OneToOne
	@JoinColumn(name = "event_id")
	private EventMasterEntity eventMasterEntity;

}
