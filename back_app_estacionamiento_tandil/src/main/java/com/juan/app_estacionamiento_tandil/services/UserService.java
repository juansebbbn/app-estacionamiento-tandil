package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.exceptions.ResourceNotFoundException;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<User> getUserByUsername(String username) {
        logger.info("[USER] [getUserByUsername] START - username={}", username);

        Optional<User> user = userRepository.findByUsername(username);

        logger.info("[USER] [getUserByUsername] FETCH_USER - username={}", username);

        if (user.isEmpty()) {
            logger.error("[USER] [getUserByUsername] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        logger.info("[USER] [getUserByUsername] SUCCESS - username={}", username);
        return ResponseEntity.ok(user.get());
    }

}
