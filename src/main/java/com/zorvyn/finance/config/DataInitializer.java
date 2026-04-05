package com.zorvyn.finance.config;

import com.zorvyn.finance.model.*;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@zorvyn.com")
                        .role(Role.ADMIN)
                        .active(true)
                        .build());
            }
            if (!userRepository.existsByUsername("analyst")) {
                userRepository.save(User.builder()
                        .username("analyst")
                        .password(passwordEncoder.encode("analyst123"))
                        .email("analyst@zorvyn.com")
                        .role(Role.ANALYST)
                        .active(true)
                        .build());
            }
            if (!userRepository.existsByUsername("viewer")) {
                userRepository.save(User.builder()
                        .username("viewer")
                        .password(passwordEncoder.encode("viewer123"))
                        .email("viewer@zorvyn.com")
                        .role(Role.VIEWER)
                        .active(true)
                        .build());
            }
            System.out.println("✅ Default users created: admin / analyst / viewer");
        };
    }
}