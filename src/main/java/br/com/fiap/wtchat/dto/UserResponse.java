package br.com.fiap.wtchat.dto;

import br.com.fiap.wtchat.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String position;
    private String company;
    private String avatarUrl;

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getPhone(),
                user.getPosition(),
                user.getCompany(),
                user.getAvatarUrl()
        );
    }
}
