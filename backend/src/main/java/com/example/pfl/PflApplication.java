package com.example.pfl;

import com.example.pfl.entities.Role;
import com.example.pfl.entities.User;
import com.example.pfl.enums.RoleType;
import com.example.pfl.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@EnableScheduling
@SpringBootApplication
public class PflApplication implements CommandLineRunner {
	UserRepository userRepository;
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(PflApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User admin = User.builder()
				.actif(true)
				.pseudo("gilles")
				.motDePasse(passwordEncoder.encode("gilles"))
				.email("gilles@bbox.fr")
				.role(
						Role.builder()
								.libelle(RoleType.ADMIN)
								.build()
				)
				.build();
		if (this.userRepository.findByEmail("gilles@bbox.fr").isEmpty()) {
			this.userRepository.save(admin);
		}
		User manager = User.builder()
				.actif(true)
				.pseudo("mymy")
				.motDePasse(passwordEncoder.encode("mymy"))
				.email("mymy@yahoo.com")
				.role(
						Role.builder()
								.libelle(RoleType.MANAGER)
								.build()
				)
				.build();
		if (this.userRepository.findByEmail("mymy@yahoo.com").isEmpty()) {
			this.userRepository.save(manager);
		}
		User user = User.builder()
				.actif(true)
				.pseudo("caroline")
				.motDePasse(passwordEncoder.encode("caroline"))
				.email("caroline@yahoo.fr")
				.role(
						Role.builder()
								.libelle(RoleType.USER)
								.build()
				)
				.build();
		if (this.userRepository.findByEmail("caroline@yahoo.fr").isEmpty()) {
			this.userRepository.save(user);
		}
	}
}
