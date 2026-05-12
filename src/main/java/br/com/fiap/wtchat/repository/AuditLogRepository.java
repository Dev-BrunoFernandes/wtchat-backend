package br.com.fiap.wtchat.repository;

import br.com.fiap.wtchat.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);
    List<AuditLog> findByResourceOrderByTimestampDesc(String resource);
    List<AuditLog> findAllByOrderByTimestampDesc();
}
