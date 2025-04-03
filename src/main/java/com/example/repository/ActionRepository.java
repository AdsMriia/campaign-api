package com.example.repository;

import com.example.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с действиями (кнопками) сообщений.
 */
@Repository
public interface ActionRepository extends JpaRepository<Action, UUID> {

    /**
     * Находит все действия, связанные с указанным сообщением.
     *
     * @param messageId идентификатор сообщения
     * @return список действий
     */
    List<Action> findByMessageId(UUID messageId);

    /**
     * Удаляет все действия, связанные с указанным сообщением.
     *
     * @param messageId идентификатор сообщения
     */
    void deleteAllByMessageId(UUID messageId);
}
