package com.juan.app_estacionamiento_tandil.repositories;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParkingRespository extends JpaRepository<ParkingTime, Long> {

    @Query("""
        SELECT pt
        FROM ParkingTime pt
        JOIN pt.vehicle v
        WHERE v.patent = :patent
    """)
    Optional<ParkingTime> findByPatent(String patent);
}
