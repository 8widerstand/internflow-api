package com.internflow.api.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()){
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "ADMIN"));
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            userRepository.save(new User("user", passwordEncoder.encode("user123"), "USER"));
        }
    }
}
