package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingZone;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import org.springframework.stereotype.Service;

@Service
public class ParkingZoneService {
    public boolean isAtParkingZone(Coordinate coord) {
        System.out.println("service test boolean " + ParkingZone.isUserInParkingZone(coord.getLatitude(), coord.getLongitude()));
        return ParkingZone.isUserInParkingZone(coord.getLatitude(), coord.getLongitude());
    }
}
