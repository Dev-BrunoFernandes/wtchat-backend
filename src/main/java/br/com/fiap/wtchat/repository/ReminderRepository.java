package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReminderRepository extends MongoRepository<Reminder, String> {
    List<Reminder> findByUserIdAndDateOrderByTimeAsc(String userId, String date);
    List<Reminder> findByUserIdOrderByDateAscTimeAsc(String userId);
}
