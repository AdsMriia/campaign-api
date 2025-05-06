package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.entity.Media;
import com.example.model.dto.MediaDto;

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
    @Mapping(target = "url", expression = "java(String.format(\"/static/mriia/%s.%s\", media.getFileName(), media.getFileExtension()))")
    @Mapping(target = "size", ignore = true)
    MediaDto toMediaDto(Media media);

    /**
     * Преобразует MediaDto в Media.
     *
     * @param mediaDto DTO медиафайла
     * @return сущность медиафайла
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Media toMedia(MediaDto mediaDto);

    /**
     * Преобразует MediaDto в Media.
     *
     * @param fileName имя файла
     * @param fileExtension расширение файла
     * @return сущность медиафайла
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "workspaceId", ignore = true)
    Media toMedia(String fileName, String fileExtension);

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
