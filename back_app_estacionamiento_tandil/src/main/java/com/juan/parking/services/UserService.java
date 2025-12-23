package com.juan.parking.services;

import com.juan.parking.entities.User;
import com.juan.parking.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<User> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<BigDecimal> getBalance(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User balance = user.get();
            return ResponseEntity.ok(balance.getBalance());
        }
        return ResponseEntity.notFound().build();
    }
}
