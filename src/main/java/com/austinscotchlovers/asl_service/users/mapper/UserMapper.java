package com.austinscotchlovers.asl_service.users.mapper;

import com.austinscotchlovers.asl_service.users.User;
import com.austinscotchlovers.asl_service.users.dto.UserUpdateDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "attendedEvents", ignore = true)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);
}
