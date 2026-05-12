package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String senderId;

    private String recipientId;

    private String content;

    private MessageType type = MessageType.TEXT;

    private MessageStatus status = MessageStatus.SENT;

    private List<MessageAction> actions;

    private String imageUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime readAt;

    public enum MessageType {
        TEXT, IMAGE, CAMPAIGN
    }

    public enum MessageStatus {
        SENT, DELIVERED, READ, FAILED
    }

    @Data
    public static class MessageAction {
        private String action;
        private String title;
        private String url;
    }
}
