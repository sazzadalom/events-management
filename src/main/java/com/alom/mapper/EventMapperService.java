package com.alom.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.alom.dao.entities.EventMasterEntity;
import com.alom.model.EventMasterDto;

@Mapper(componentModel = "spring")
public interface EventMapperService {

	EventMasterDto mappedToEventMasterDto(EventMasterEntity eventMasterEntity);
	List<EventMasterDto> mappedToEventMasterDto(List<EventMasterEntity> eventMasterEntity);
	
	EventMasterEntity mappedToEventMasterEntity(EventMasterDto eventMasterDto);
}
