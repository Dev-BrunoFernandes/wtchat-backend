package br.com.fiap.wtchat.service;

import br.com.fiap.wtchat.dto.AuthResponse;
import br.com.fiap.wtchat.dto.LoginRequest;
import br.com.fiap.wtchat.dto.RegisterRequest;
import br.com.fiap.wtchat.dto.SocialAuthRequest;
import br.com.fiap.wtchat.model.User;
import br.com.fiap.wtchat.repository.UserRepository;
import br.com.fiap.wtchat.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final AuditService auditService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.CLIENT);
        userRepository.save(user);

        auditService.log(user.getId(), user.getEmail(), "REGISTER", "User", user.getId(), "Usuário registrado");

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        auditService.log(user.getId(), user.getEmail(), "LOGIN", "User", user.getId(), "Login realizado");

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse socialLogin(SocialAuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setRole(User.Role.CLIENT);
            userRepository.save(newUser);
            auditService.log(newUser.getId(), newUser.getEmail(), "SOCIAL_REGISTER",
                    "User", newUser.getId(), "Cadastro via " + request.getProvider());
            return newUser;
        });

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());
        auditService.log(user.getId(), user.getEmail(), "SOCIAL_LOGIN",
                "User", user.getId(), "Login via " + request.getProvider());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
