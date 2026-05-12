package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByRecipientIdOrderByCreatedAtDesc(String recipientId);

    @Query("{ $or: [ { senderId: ?0, recipientId: ?1 }, { senderId: ?1, recipientId: ?0 } ] }")
    List<Message> findConversation(String userId, String otherUserId);

    List<Message> findBySenderIdOrRecipientIdOrderByCreatedAtDesc(String senderId, String recipientId);
}
