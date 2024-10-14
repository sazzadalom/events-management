package com.alom.events.wrapper;

import com.alom.events.model.EventMasterModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
/**
 * Wrap the JSON data in to container @class EventMasterModel
 * @author sazzad
 * @version 1.0.0
 * @since 11-10-2024
 *
 */
@Getter
@Setter
public class EventWrapper {

	@JsonProperty("jsonData")
	private EventMasterModel eventMasterModel;
}
