package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.dto.CustomerRequest;
import br.com.fiap.wtchat.model.Customer;
import br.com.fiap.wtchat.repository.UserRepository;
import br.com.fiap.wtchat.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Customer> create(@RequestBody CustomerRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String operatorId = resolveUserId(userDetails);
        return ResponseEntity.ok(customerService.create(request, operatorId));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(customerService.findAll(search, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable String id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Customer> update(@PathVariable String id,
                                           @RequestBody CustomerRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(customerService.update(id, request, resolveUserId(userDetails)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        customerService.delete(id, resolveUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<Map<String, Object>> getTimeline(@PathVariable String id) {
        return ResponseEntity.ok(customerService.getTimeline(id));
    }

    private String resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getId())
                .orElse(userDetails.getUsername());
    }
}
