package com.anusha.paradise.auth;

import com.anusha.paradise.repository.UserRepository;
import com.anusha.paradise.security.*;
import com.anusha.paradise.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        String message;
        String jwtToken = null;
        try {
            var user = User.builder()
                    .id(UUID.randomUUID())
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            var savedUser = userRepository.save(user);
            jwtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jwtToken);
            message = "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getLocalizedMessage();
        }
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message(message)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String respMessage;
        String jwtToken = null;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow();
            jwtToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            respMessage = "SUCCESS";
        } catch (Exception e) {
            respMessage = e.getLocalizedMessage();
        }
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message(respMessage)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        validUserTokens.ifPresent(tokens -> {
            tokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(tokens);
        });
    }
}
