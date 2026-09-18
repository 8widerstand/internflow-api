package com.internflow.api.user;

import com.internflow.api.common.error.ResourceConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder
    )
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public void registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResourceConflictException("Username already taken: " + username);
        }
        userRepository.save(new User(username, passwordEncoder.encode(password), "USER"));
    }


    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .build();
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
    }
}
