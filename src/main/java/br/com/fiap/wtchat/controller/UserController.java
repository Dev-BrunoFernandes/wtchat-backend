package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.dto.UserResponse;
import br.com.fiap.wtchat.model.User;
import br.com.fiap.wtchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam(required = false, defaultValue = "") String search
    ) {
        List<UserResponse> result;
        if (search.isBlank()) {
            result = userRepository.findAll().stream()
                    .map(UserResponse::from)
                    .collect(Collectors.toList());
        } else {
            result = userRepository
                    .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search)
                    .stream()
                    .map(UserResponse::from)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @RequestBody Map<String, String> updates,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (updates.containsKey("name") && updates.get("name") != null && !updates.get("name").isBlank())
            user.setName(updates.get("name"));
        if (updates.containsKey("phone")) user.setPhone(updates.get("phone"));
        if (updates.containsKey("position")) user.setPosition(updates.get("position"));
        if (updates.containsKey("company")) user.setCompany(updates.get("company"));
        if (updates.containsKey("avatarUrl")) user.setAvatarUrl(updates.get("avatarUrl"));
        userRepository.save(user);
        return ResponseEntity.ok(UserResponse.from(user));
    }
}
