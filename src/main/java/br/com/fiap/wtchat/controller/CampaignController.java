package br.com.fiap.wtchat.controller;

import br.com.fiap.wtchat.dto.CampaignRequest;
import br.com.fiap.wtchat.model.Campaign;
import br.com.fiap.wtchat.repository.UserRepository;
import br.com.fiap.wtchat.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OPERATOR')")
public class CampaignController {

    private final CampaignService campaignService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Campaign> create(@RequestBody CampaignRequest request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(campaignService.create(request, resolveUserId(userDetails)));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Campaign> send(@PathVariable String id,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(campaignService.send(id, resolveUserId(userDetails)));
    }

    @PostMapping("/{id}/schedule")
    public ResponseEntity<Campaign> schedule(@PathVariable String id,
                                              @RequestBody CampaignRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(campaignService.create(request, resolveUserId(userDetails)));
    }

    @GetMapping
    public ResponseEntity<List<Campaign>> findAll() {
        return ResponseEntity.ok(campaignService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> findById(@PathVariable String id) {
        return ResponseEntity.ok(campaignService.findById(id));
    }

    private String resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getId()).orElse(userDetails.getUsername());
    }
}
