package com.udea.msvc_fishcontrol.controllers;

import com.udea.msvc_fishcontrol.models.user.RefreshTokenEntity;
import com.udea.msvc_fishcontrol.models.user.UserEntity;
import com.udea.msvc_fishcontrol.repositories.user.UserRepository;
import com.udea.msvc_fishcontrol.request.LoginRequest;
import com.udea.msvc_fishcontrol.request.TokenRefreshRequest;
import com.udea.msvc_fishcontrol.response.TokenRefreshResponse;
import com.udea.msvc_fishcontrol.security.exceptions.TokenRefreshException;
import com.udea.msvc_fishcontrol.security.jwt.JwtUtils;
import com.udea.msvc_fishcontrol.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          UserRepository userRepository,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Intento de login para usuario: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserEntity user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String jwtToken = jwtUtils.generateAccessToken(user.getUsername());
            RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwtToken);
            response.put("refreshToken", refreshToken.getToken());
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        try {
            String newToken = refreshTokenService.findByToken(request.getRefreshToken())
                    .map(refreshTokenService::verifyExpiration)
                    .map(refreshToken -> {
                        refreshTokenService.markAsUsed(refreshToken.getToken());
                        return jwtUtils.generateAccessToken(refreshToken.getUser().getUsername());
                    })
                    .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(), "Refresh token inválido"));

            return ResponseEntity.ok(new TokenRefreshResponse(newToken, request.getRefreshToken()));
        } catch (TokenRefreshException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}