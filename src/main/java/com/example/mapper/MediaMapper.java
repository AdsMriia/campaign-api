package com.example.mapper;

import com.example.model.Media;
import com.example.model.dto.MediaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Маппер для преобразования между сущностью Media и DTO MediaDto.
 */
@Mapper(componentModel = "spring")
public interface MediaMapper {

    /**
     * Преобразует Media в MediaDto.
     *
     * @param media сущность медиафайла
     * @return DTO медиафайла
     */
    @Mapping(target = "fullName", expression = "java(getFullName(media))")
    MediaDto toMediaDto(Media media);

    /**
     * Преобразует MediaDto в Media.
     *
     * @param mediaDto DTO медиафайла
     * @return сущность медиафайла
     */
    Media toMedia(MediaDto mediaDto);

    /**
     * Вспомогательный метод для формирования полного имени файла.
     *
     * @param media сущность медиафайла
     * @return полное имя файла
     */
    default String getFullName(Media media) {
        if (media == null || media.getFileName() == null) {
            return null;
        }
        return media.getFileName().toString() + (media.getFileExtension() != null ? media.getFileExtension() : "");
    }
}
