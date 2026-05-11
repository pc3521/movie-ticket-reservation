package com.movieticket.service;

import com.movieticket.model.*;
import com.movieticket.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final SupportMessageRepository messageRepository;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       SupportMessageRepository messageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ChatSession createSession(User customer) {
        // Check if customer already has an active session
        List<ChatSession> active = chatSessionRepository.findActiveByCustomerId(customer.getId());
        if (!active.isEmpty()) {
            return active.get(0);
        }

        ChatSession session = ChatSession.builder()
                .customer(customer)
                .status(ChatSession.ChatStatus.PENDING)
                .build();
        return chatSessionRepository.save(session);
    }

    @Transactional
    public ChatSession joinSession(Long sessionId, User admin) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setAdmin(admin);
        session.setStatus(ChatSession.ChatStatus.ACTIVE);
        return chatSessionRepository.save(session);
    }

    @Transactional
    public void closeSession(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setStatus(ChatSession.ChatStatus.CLOSED);
        session.setClosedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    @Transactional
    public SupportMessage saveMessage(Long sessionId, User sender, String content) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        SupportMessage message = SupportMessage.builder()
                .chatSession(session)
                .sender(sender)
                .content(content)
                .build();
        return messageRepository.save(message);
    }

    public List<ChatSession> getPendingSessions() {
        return chatSessionRepository.findPendingSessions();
    }

    public List<ChatSession> getActiveSessions() {
        return chatSessionRepository.findActiveSessions();
    }

    public List<SupportMessage> getSessionMessages(Long sessionId) {
        return messageRepository.findBySessionIdOrdered(sessionId);
    }

    public ChatSession getSession(Long sessionId) {
        return chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }
}
