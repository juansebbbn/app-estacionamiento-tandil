package com.juan.app_estacionamiento_tandil.entities;

import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "parking_times")
public class ParkingTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingState state;

    public ParkingTime(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime, ParkingState state) {
        this.vehicle = vehicle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.state = state;
    }

    protected ParkingTime() {}
}
