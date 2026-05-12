package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String userId;

    private String userEmail;

    private String action;

    private String resource;

    private String resourceId;

    private String details;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String ipAddress;
}
