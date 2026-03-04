package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingZone;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParkingZoneService {
    private static final Logger logger = LoggerFactory.getLogger(ParkingZoneService.class);

    public boolean isAtParkingZone(Coordinate coord) {
        logger.info("[ZONE] [isAtParkingZone] START - lat={}, lng={}", coord.getLatitude(), coord.getLongitude());
        
        boolean result = ParkingZone.isUserInParkingZone(coord.getLatitude(), coord.getLongitude());
        
        logger.info("[ZONE] [isAtParkingZone] SUCCESS - lat={}, lng={}, in_zone={}", coord.getLatitude(), coord.getLongitude(), result);
        return result;
    }
}
