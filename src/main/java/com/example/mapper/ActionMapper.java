package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.entity.Action;
import com.example.model.dto.ActionDto;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    ActionDto toActionDto(Action action);

    @Mapping(target = "message", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Action toAction(ActionDto dto);
}
