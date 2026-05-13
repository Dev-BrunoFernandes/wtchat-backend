package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.dto.UserResponse;
import br.com.fiap.wtchat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
