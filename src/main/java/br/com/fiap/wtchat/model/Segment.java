package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "segments")
public class Segment {

    @Id
    private String id;

    private String name;

    private String description;

    private List<String> customerIds = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}
