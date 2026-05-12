package br.com.fiap.wtchat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocialAuthRequest {
    @NotBlank
    private String provider; // "google", "facebook", "linkedin"

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;
}
