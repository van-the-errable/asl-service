package com.austinscotchlovers.asl_service.events.mapper;

import com.austinscotchlovers.asl_service.events.Event;
import com.austinscotchlovers.asl_service.events.dto.EventDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    Event fromDto(EventDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    void updateEventFromDto(EventDto dto, @MappingTarget Event event);
}
