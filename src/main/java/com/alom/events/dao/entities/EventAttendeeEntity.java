package com.alom.events.dao.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "event_attendee")
public class EventAttendeeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "attendee_id")
	private Long attId;
	
	@Column(name = "attendee_name")
	private String attName;
	
	@Column(name = "attendee_contact_number")
	private String contactNumber;
	
	@Column(name = "business_title")
	private String businessTitle;
	
	@Column(name = "attendee_city")
	private String city;

	@ManyToOne
    @JoinColumn(name = "event_id")
    private EventMasterEntity eventMasterEntity;
}
