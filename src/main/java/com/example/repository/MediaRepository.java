package com.example.repository;

import com.example.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с медиафайлами.
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, UUID> {

    /**
     * Находит все медиафайлы, связанные с указанным сообщением.
     *
     * @param messageId идентификатор сообщения
     * @return список медиафайлов
     */
    List<Media> findByMessageId(UUID messageId);

    /**
     * Удаляет все медиафайлы, связанные с указанным сообщением.
     *
     * @param messageId идентификатор сообщения
     */
    void deleteAllByMessageId(UUID messageId);

    /**
     * Находит медиафайл по имени файла и идентификатору рабочего пространства.
     *
     * @param fileName имя файла
     * @param workspaceId идентификатор рабочего пространства
     * @return найденный медиафайл
     */
    Media findByFileNameAndWorkspaceId(String fileName, UUID workspaceId);

    /**
     * Находит медиафайл по его имени.
     *
     * @param fileName имя файла
     * @return опциональный объект медиафайла
     */
    Optional<Media> findByFileName(UUID fileName);

    /**
     * Находит первый медиафайл с указанным именем.
     *
     * @param fileName имя файла
     * @return опциональный объект медиафайла
     */
    Optional<Media> findFirstByFileName(UUID fileName);

    /**
     * Находит все медиафайлы, принадлежащие указанному рабочему пространству.
     *
     * @param workspaceId идентификатор рабочего пространства
     * @return список медиафайлов
     */
    List<Media> findByWorkspaceId(UUID workspaceId);

    /**
     * Находит все медиафайлы, принадлежащие указанным рабочим пространствам.
     *
     * @param workspaceIds список идентификаторов рабочих пространств
     * @return список медиафайлов
     */
    List<Media> findByWorkspaceIdIn(List<UUID> workspaceIds);
}
