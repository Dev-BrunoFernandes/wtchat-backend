package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.dto.ReminderRequest;
import br.com.fiap.wtchat.model.Reminder;
import br.com.fiap.wtchat.repository.ReminderRepository;
import br.com.fiap.wtchat.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Reminder>> list(
            @RequestParam(required = false) String date,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        List<Reminder> result = (date != null && !date.isBlank())
                ? reminderRepository.findByDateForUser(date, userId)
                : reminderRepository.findAllForUser(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Reminder> create(
            @Valid @RequestBody ReminderRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        Reminder reminder = new Reminder();
        reminder.setUserId(userId);
        reminder.setTitle(request.getTitle());
        reminder.setSubtitle(request.getSubtitle());
        reminder.setDate(request.getDate());
        reminder.setTime(request.getTime());
        reminder.setAddress(request.getAddress());
        reminder.setDuration(request.getDuration());
        reminder.setNote(request.getNote());
        reminder.setOwnerName(userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getName()).orElse(""));
        if (request.getParticipants() != null) {
            reminder.setParticipants(request.getParticipants());
        } else {
            reminder.setParticipants(new java.util.ArrayList<>());
        }
        return ResponseEntity.ok(reminderRepository.save(reminder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = resolveUserId(userDetails);
        reminderRepository.findById(id).ifPresent(r -> {
            boolean isOwner = r.getUserId().equals(userId);
            boolean isParticipant = r.getParticipants() != null && r.getParticipants().contains(userId);
            if (isOwner || isParticipant) {
                reminderRepository.delete(r);
            }
        });
        return ResponseEntity.noContent().build();
    }

    private String resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getId())
                .orElse(userDetails.getUsername());
    }
}
