package com.juan.app_estacionamiento_tandil.entities.data_transfer_objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Parking_time_data_transfer {
    private long parkingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Coordinate coordinate;
}
