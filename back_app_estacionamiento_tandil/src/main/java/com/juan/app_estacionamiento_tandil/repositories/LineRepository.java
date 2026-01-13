package com.juan.app_estacionamiento_tandil.repositories;

import com.juan.app_estacionamiento_tandil.entities.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    Optional<Line> findByNumber(Integer number);

    boolean existsByNumber(Integer number);
}
