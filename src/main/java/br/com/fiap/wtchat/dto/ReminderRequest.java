package br.com.fiap.wtchat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReminderRequest {

    @NotBlank
    private String title;

    private String subtitle;

    @NotBlank
    private String date; // yyyy-MM-dd

    @NotBlank
    private String time; // HH:mm

    private String address;
    private String duration;
    private String note;
}
