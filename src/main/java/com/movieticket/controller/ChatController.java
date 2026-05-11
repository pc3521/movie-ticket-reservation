package com.movieticket.controller;

import com.movieticket.dto.ChatMessageDto;
import com.movieticket.model.*;
import com.movieticket.service.ChatService;
import com.movieticket.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, UserService userService,
                          SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    // Customer: open chat page
    @GetMapping("/chat")
    public String customerChat(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        ChatSession session = chatService.createSession(user);
        model.addAttribute("session", session);
        model.addAttribute("messages", chatService.getSessionMessages(session.getId()));
        model.addAttribute("currentUser", user);
        return "chat/chat-room";
    }

    // Admin: view support requests
    @GetMapping("/admin/support")
    public String adminSupport(Model model) {
        model.addAttribute("pendingSessions", chatService.getPendingSessions());
        model.addAttribute("activeSessions", chatService.getActiveSessions());
        return "admin/support";
    }

    // Admin: join a chat session
    @GetMapping("/admin/chat/{sessionId}")
    public String adminJoinChat(@PathVariable Long sessionId, Model model, Principal principal) {
        User admin = userService.findByUsername(principal.getName());
        ChatSession session = chatService.getSession(sessionId);

        if (session.getStatus() == ChatSession.ChatStatus.PENDING) {
            session = chatService.joinSession(sessionId, admin);
            // Notify customer that admin joined
            ChatMessageDto joinMsg = ChatMessageDto.builder()
                    .sessionId(sessionId)
                    .senderUsername("System")
                    .content("An admin has joined the chat.")
                    .type("JOIN")
                    .build();
            messagingTemplate.convertAndSend("/topic/chat/" + sessionId, joinMsg);
        }

        model.addAttribute("session", session);
        model.addAttribute("messages", chatService.getSessionMessages(sessionId));
        model.addAttribute("currentUser", admin);
        return "chat/chat-room";
    }

    // WebSocket: send message
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto messageDto, Principal principal) {
        User sender = userService.findByUsername(principal.getName());
        SupportMessage saved = chatService.saveMessage(messageDto.getSessionId(), sender, messageDto.getContent());

        ChatMessageDto response = ChatMessageDto.builder()
                .sessionId(messageDto.getSessionId())
                .senderUsername(sender.getUsername())
                .content(messageDto.getContent())
                .sentAt(saved.getSentAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .type("MESSAGE")
                .build();

        messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getSessionId(), response);
    }

    // REST: close session
    @PostMapping("/api/chat/close/{sessionId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> closeChat(@PathVariable Long sessionId) {
        chatService.closeSession(sessionId);

        ChatMessageDto closeMsg = ChatMessageDto.builder()
                .sessionId(sessionId)
                .senderUsername("System")
                .content("Chat session has been closed.")
                .type("CLOSE")
                .build();
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, closeMsg);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
