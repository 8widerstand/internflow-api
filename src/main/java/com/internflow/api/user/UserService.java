package com.internflow.api.user;

import com.internflow.api.common.error.ResourceConflictException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResourceConflictException("Username already taken: " + username);
        }
        userRepository.save(new User(username, passwordEncoder.encode(password), "USER"));
    }
}
