package com.stocklens.user.config;

import com.stocklens.user.infra.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner demoUserPasswordSeeder(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.demo-user-email}") String demoEmail,
            @Value("${app.demo-user-password}") String demoPassword
    ) {
        return args -> appUserRepository.findByEmail(demoEmail).ifPresent(user -> {
            if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
                user.setPasswordHash(passwordEncoder.encode(demoPassword));
                appUserRepository.save(user);
            }
        });
    }
}
