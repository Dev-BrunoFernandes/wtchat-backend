package br.com.fiap.wtchat.service;

import br.com.fiap.wtchat.dto.MessageRequest;
import br.com.fiap.wtchat.model.Message;
import br.com.fiap.wtchat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuditService auditService;

    public Message send(String senderId, MessageRequest request) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setRecipientId(request.getRecipientId());
        message.setContent(request.getContent());
        message.setType(request.getType() != null ? request.getType() : Message.MessageType.TEXT);
        message.setStatus(Message.MessageStatus.SENT);
        message.setActions(request.getActions());
        message.setImageUrl(request.getImageUrl());
        messageRepository.save(message);

        // WebSocket: entrega em tempo real ao destinatário (path explícito com mongoId)
        messagingTemplate.convertAndSend(
                "/user/" + request.getRecipientId() + "/queue/messages",
                message
        );

        auditService.log(senderId, null, "SEND_MESSAGE", "Message", message.getId(),
                "Mensagem enviada para " + request.getRecipientId());

        return message;
    }

    public List<Message> getInbox(String userId) {
        return messageRepository.findBySenderIdOrRecipientIdOrderByCreatedAtDesc(userId, userId);
    }

    public List<Message> getConversation(String userId, String otherUserId) {
        return messageRepository.findConversation(userId, otherUserId);
    }

    public Message findById(String id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada"));
    }

    public Message markAsRead(String messageId) {
        Message message = findById(messageId);
        message.setStatus(Message.MessageStatus.READ);
        message.setReadAt(LocalDateTime.now());
        return messageRepository.save(message);
    }
}
