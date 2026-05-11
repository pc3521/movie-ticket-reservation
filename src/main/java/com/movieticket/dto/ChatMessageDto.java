package com.movieticket.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageDto {
    private Long sessionId;
    private String senderUsername;
    private String content;
    private String sentAt;
    private String type; // MESSAGE, JOIN, LEAVE, CLOSE
}
