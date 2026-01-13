package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<User> getUserById(Long id) {
        System.out.println("Fetching user by id: " + id);

        Optional<User> user = userRepository.findById(id);

        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<BigDecimal> getBalance(Long id) {
        System.out.println("Getting balance: " + id);

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User balance = user.get();

            System.out.println("User balance: " + balance.getBalance());

            return ResponseEntity.ok(balance.getBalance());
        }

        return ResponseEntity.notFound().build();
    }

}
