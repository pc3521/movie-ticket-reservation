package com.movieticket.repository;

import com.movieticket.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    @Query("SELECT c FROM ChatSession c JOIN FETCH c.customer WHERE c.status != 'CLOSED' ORDER BY c.createdAt DESC")
    List<ChatSession> findActiveSessions();

    @Query("SELECT c FROM ChatSession c JOIN FETCH c.customer WHERE c.status = 'PENDING' ORDER BY c.createdAt ASC")
    List<ChatSession> findPendingSessions();

    @Query("SELECT c FROM ChatSession c WHERE c.customer.id = :customerId AND c.status != 'CLOSED' ORDER BY c.createdAt DESC")
    List<ChatSession> findActiveByCustomerId(Long customerId);
}
