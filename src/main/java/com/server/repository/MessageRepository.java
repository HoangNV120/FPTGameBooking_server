package com.server.repository;

import com.server.entity.Message;
import com.server.enums.MessageTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,String> {

    List<Message> findMessageByRoom_codeOrderByUpdatedDateAsc(String codeRoom);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM Message U WHERE U.id = :id ")
    void deleteById(String id);

    List<Message> findMessageByMessageType(MessageTypeEnum messageType);

    @Modifying
    @Transactional
    void deleteAllByRoom_Code (String codeRoom);
}
