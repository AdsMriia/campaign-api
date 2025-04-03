package com.example.mapper;

import com.example.entity.Action;
import com.example.model.dto.ActionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActionMapper {

    ActionDto toActionDto(Action action);

    @Mapping(target = "message", ignore = true)
    Action toAction(ActionDto dto);
}
