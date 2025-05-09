package com.udea.msvc_fishcontrol;

import com.udea.msvc_fishcontrol.models.user.ERole;
import com.udea.msvc_fishcontrol.models.user.RoleEntity;
import com.udea.msvc_fishcontrol.models.user.UserEntity;
import com.udea.msvc_fishcontrol.repositories.user.RefreshTokenRepository;
import com.udea.msvc_fishcontrol.repositories.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@SpringBootApplication
@EntityScan(basePackages = {"com.udea.msvc_fishcontrol.models"})
public class MsvcAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsvcAuthApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner init(UserRepository userRepository,
						   RefreshTokenRepository refreshTokenRepository,
						   PasswordEncoder passwordEncoder) {
		return args -> {
			refreshTokenRepository.deleteAll();
			userRepository.deleteAll();

			createUser("admin@mail.com", "admin", ERole.ADMIN, userRepository, passwordEncoder);
			createUser("user@mail.com", "user", ERole.USER, userRepository, passwordEncoder);
		};
	}

	private void createUser(String email, String username, ERole role,
							UserRepository userRepository,
							PasswordEncoder passwordEncoder) {
		if (userRepository.findByUsername(username).isEmpty()) {
			UserEntity userEntity = UserEntity.builder()
					.email(email)
					.username(username)
					.password(passwordEncoder.encode("123456"))
					.enabled(true)
					.roles(Set.of(RoleEntity.builder()
							.name(role)
							.build()))
					.build();

			userRepository.save(userEntity);
		}
	}
}