package com.alom.dao.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_master")
public class EventMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "event_id")
	private Long eventId;

	@Column(name = "event_name")
	private String eventName;

	@Column(name = "event_url")
	private String eventUrl;

	@Column(name = "event_date")
	private LocalDate eventDate;

	@Column(name = "event_created_at")
	private LocalDateTime eventCreatedAt;
	
	@OneToOne(mappedBy = "eventMasterEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EventMediaEntity eventMediaEntity;
	
	@OneToMany(mappedBy = "eventMasterEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventAttendeeEntity> eventAttendeeEntityList;
}