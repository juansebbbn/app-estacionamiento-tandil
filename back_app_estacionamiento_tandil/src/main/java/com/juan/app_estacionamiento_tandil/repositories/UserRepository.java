package com.juan.app_estacionamiento_tandil.repositories;

import com.juan.app_estacionamiento_tandil.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<Object> findByUsername(String username);
}
