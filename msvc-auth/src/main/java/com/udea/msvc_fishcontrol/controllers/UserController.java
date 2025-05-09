package com.udea.msvc_fishcontrol.controllers;

import com.udea.msvc_fishcontrol.models.user.ERole;
import com.udea.msvc_fishcontrol.models.user.RoleEntity;
import com.udea.msvc_fishcontrol.models.user.UserEntity;
import com.udea.msvc_fishcontrol.repositories.user.UserRepository;
import com.udea.msvc_fishcontrol.request.DTO.CreateUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO userDTO) {

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Usuario autenticado: {}", auth.getName());
            log.info("Roles del usuario: {}", auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
                log.warn("Intento de crear usuario existente: {}", userDTO.getUsername());
                return ResponseEntity.badRequest().body("El usuario ya existe");
            }

            Set<RoleEntity> roles = userDTO.getRoles().stream()
                    .map(role -> {
                        try {
                            return RoleEntity.builder()
                                    .name(ERole.valueOf(role))
                                    .build();
                        } catch (IllegalArgumentException e) {
                            log.error("Rol no válido: {}", role);
                            throw new IllegalArgumentException("Rol no válido: " + role);
                        }
                    })
                    .collect(Collectors.toSet());

            UserEntity userEntity = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password("{bcrypt}" + passwordEncoder.encode(userDTO.getPassword()))
                    .email(userDTO.getEmail())
                    .enabled(userDTO.getEnabled())
                    .roles(roles)
                    .build();

            userRepository.save(userEntity);
            log.info("Usuario creado exitosamente: {}", userDTO.getUsername());

            return ResponseEntity.ok().body(Map.of(
                    "message", "Usuario creado correctamente",
                    "username", userEntity.getUsername(),
                    "email", userEntity.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error al crear usuario", e);
            return ResponseEntity.internalServerError().body("Error interno del servidor");
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado con éxito");
    }

    @DeleteMapping("/by-username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserByUsername(@PathVariable String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        userRepository.delete(user);
        return ResponseEntity.ok("Usuario '" + username + "' eliminado con éxito");
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok("Usuario desactivado con éxito");
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("El usuario no existe");
        }

        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("Usuario activado con éxito");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = (List<UserEntity>) userRepository.findAll();

        // Ocultar las contraseñas en la respuesta
        users.forEach(user -> user.setPassword("[PROTECTED]"));

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserEntity user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Ocultar la contraseña en la respuesta
        user.setPassword("[PROTECTED]");

        return ResponseEntity.ok(user);
    }
}