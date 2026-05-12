package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.model.Segment;
import br.com.fiap.wtchat.repository.UserRepository;
import br.com.fiap.wtchat.service.SegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/segments")
@RequiredArgsConstructor
public class SegmentController {

    private final SegmentService segmentService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Segment> create(@RequestBody Segment segment,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(segmentService.create(segment, resolveUserId(userDetails)));
    }

    @GetMapping
    public ResponseEntity<List<Segment>> findAll() {
        return ResponseEntity.ok(segmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Segment> findById(@PathVariable String id) {
        return ResponseEntity.ok(segmentService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Segment> update(@PathVariable String id,
                                          @RequestBody Segment segment,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(segmentService.update(id, segment, resolveUserId(userDetails)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        segmentService.delete(id, resolveUserId(userDetails));
        return ResponseEntity.noContent().build();
    }

    private String resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getId()).orElse(userDetails.getUsername());
    }
}
