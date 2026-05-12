package br.com.fiap.wtchat.dto;

import br.com.fiap.wtchat.model.Message;
import lombok.Data;

import java.util.List;

@Data
public class MessageRequest {
    private String recipientId;
    private String content;
    private Message.MessageType type = Message.MessageType.TEXT;
    private List<Message.MessageAction> actions;
    private String imageUrl;
}
