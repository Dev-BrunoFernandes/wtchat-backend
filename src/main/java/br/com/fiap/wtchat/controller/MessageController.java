package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.dto.MessageRequest;
import br.com.fiap.wtchat.model.Message;
import br.com.fiap.wtchat.repository.UserRepository;
import br.com.fiap.wtchat.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    @PostMapping("/messages")
    public ResponseEntity<Message> send(@RequestBody MessageRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        String senderId = resolveUserId(userDetails);
        return ResponseEntity.ok(messageService.send(senderId, request));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<Message> findById(@PathVariable String id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @GetMapping("/inbox/{userId}")
    public ResponseEntity<List<Message>> getInbox(@PathVariable String userId) {
        return ResponseEntity.ok(messageService.getInbox(userId));
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable String otherUserId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        String userId = resolveUserId(userDetails);
        return ResponseEntity.ok(messageService.getConversation(userId, otherUserId));
    }

    @PatchMapping("/messages/{id}/read")
    public ResponseEntity<Message> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }

    // WebSocket: cliente envia mensagem via STOMP
    @MessageMapping("/chat.send")
    public void sendViaWebSocket(@Payload MessageRequest request,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String senderId = resolveUserId(userDetails);
            messageService.send(senderId, request);
        }
    }

    private String resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getId()).orElse(userDetails.getUsername());
    }
}
