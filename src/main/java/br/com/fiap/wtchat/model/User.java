package br.com.fiap.wtchat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private Role role;

    private String phone;
    private String position;
    private String company;
    private String avatarUrl;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        OPERATOR, CLIENT
    }
}
