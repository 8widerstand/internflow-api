package com.internflow.api.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest registerRequest) {
        this.userService.registerUser(registerRequest.username(), registerRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = userService.login(loginRequest.username(), loginRequest.password());
        return new LoginResponse(token);
    }
}
