package com.alom.dao.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_master")
public class EventMasterEntity {

	@Id
	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "event_name")
	private String eventName;

	@Column(name = "event_url")
	private String eventUrl;

	@Column(name = "event_date")
	private LocalDateTime eventDate;

	@Column(name = "event_created_at")
	private LocalDateTime eventCreatedAt;
}