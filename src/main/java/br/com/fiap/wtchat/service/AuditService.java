package br.com.fiap.wtchat.service;

import br.com.fiap.wtchat.model.AuditLog;
import br.com.fiap.wtchat.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String userId, String userEmail, String action, String resource, String resourceId, String details) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setUserEmail(userEmail);
        log.setAction(action);
        log.setResource(resource);
        log.setResourceId(resourceId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> findAll() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> findByUserId(String userId) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
