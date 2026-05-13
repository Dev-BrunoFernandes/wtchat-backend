package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ReminderRepository extends MongoRepository<Reminder, String> {
    @Query("{ 'date': ?0, '$or': [ { 'userId': ?1 }, { 'participants': ?1 } ] }")
    List<Reminder> findByDateForUser(String date, String userId);

    @Query("{ '$or': [ { 'userId': ?0 }, { 'participants': ?0 } ] }")
    List<Reminder> findAllForUser(String userId);
}
