package com.juan.transport.repositories;

import com.juan.transport.entities.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    Optional<Line> findByNumber(Integer number);

    List<Line> findByActiveTrue();

    boolean existsByNumber(Integer number);
}
