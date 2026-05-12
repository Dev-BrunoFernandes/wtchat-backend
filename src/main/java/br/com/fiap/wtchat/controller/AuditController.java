package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.model.AuditLog;
import br.com.fiap.wtchat.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OPERATOR')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditLog>> findAll() {
        return ResponseEntity.ok(auditService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(auditService.findByUserId(userId));
    }
}
