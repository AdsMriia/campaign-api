// package com.example.mapper;

// import java.time.OffsetDateTime;
// import java.util.List;

// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.Named;
// import org.mapstruct.factory.Mappers;

// import com.example.model.dto.LinkClickStatsDto;

// /**
//  * Маппер для преобразования между LinkClickStats и LinkClickStatsDto.
//  */
// @Mapper(componentModel = "spring")
// public interface LinkClickStatsMapper {

//     LinkClickStatsMapper INSTANCE = Mappers.getMapper(LinkClickStatsMapper.class);

//     /**
//      * Преобразует сущность LinkClickStats в DTO.
//      *
//      * @param linkClickStats сущность статистики кликов
//      * @return DTO статистики кликов
//      */
//     @Mapping(source = "partnerLink.id", target = "partnerLinkId")
//     @Mapping(source = "partnerLink.originalUrl", target = "originalUrl")
//     @Mapping(source = "campaign.id", target = "campaignId")
//     @Mapping(source = "campaign.title", target = "campaignTitle")
//     @Mapping(source = "clickTime", target = "clickTime", qualifiedByName = "offsetDateTimeToLong")
//     LinkClickStatsDto toLinkClickStatsDto(LinkClickStats linkClickStats);

//     /**
//      * Преобразует список сущностей LinkClickStats в список DTO.
//      *
//      * @param linkClickStats список сущностей статистики кликов
//      * @return список DTO статистики кликов
//      */
//     List<LinkClickStatsDto> toLinkClickStatsDtoList(List<LinkClickStats> linkClickStats);

//     /**
//      * Преобразует OffsetDateTime в Long (миллисекунды).
//      *
//      * @param dateTime дата и время
//      * @return миллисекунды
//      */
//     @Named("offsetDateTimeToLong")
//     default Long offsetDateTimeToLong(OffsetDateTime dateTime) {
//         return dateTime != null ? dateTime.toInstant().toEpochMilli() : null;
//     }
// }