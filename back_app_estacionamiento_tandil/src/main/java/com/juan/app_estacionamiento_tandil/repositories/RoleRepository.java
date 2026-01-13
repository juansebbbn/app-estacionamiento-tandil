package com.juan.app_estacionamiento_tandil.repositories;

import com.juan.app_estacionamiento_tandil.entities.Role;
import com.juan.app_estacionamiento_tandil.entities.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole eRole);
}
