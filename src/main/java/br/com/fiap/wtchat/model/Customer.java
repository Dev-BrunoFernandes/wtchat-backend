package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "customers")
public class Customer {

    @Id
    private String id;

    private String userId;

    private String name;

    private String email;

    private String phone;

    private String segmentId;

    private List<String> tags = new ArrayList<>();

    private int score = 0;

    private Status status = Status.LEAD;

    private List<String> notes = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Status {
        ACTIVE, INACTIVE, LEAD
    }
}
