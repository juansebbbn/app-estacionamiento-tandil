package com.juan.app_estacionamiento_tandil.repositories;

import com.juan.app_estacionamiento_tandil.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
