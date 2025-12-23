package com.juan.parking.repositories;

import com.juan.parking.entities.ParkingTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRespository extends JpaRepository<ParkingTime, Long> {
}
