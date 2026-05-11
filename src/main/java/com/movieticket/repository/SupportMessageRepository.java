package com.movieticket.repository;

import com.movieticket.model.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {

    @Query("SELECT m FROM SupportMessage m JOIN FETCH m.sender WHERE m.chatSession.id = :sessionId ORDER BY m.sentAt ASC")
    List<SupportMessage> findBySessionIdOrdered(Long sessionId);
}
