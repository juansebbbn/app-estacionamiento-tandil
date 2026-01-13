package com.juan.app_estacionamiento_tandil.controllers;


import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.services.ParkingZoneService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parkingzone")
public class ParkingZoneController {
    private final ParkingZoneService parkingZoneService;

    public ParkingZoneController(ParkingZoneService parkingZoneService) {
        this.parkingZoneService = parkingZoneService;
    }

    @PostMapping("/is_at_pz")
    public boolean isAtParkingZone(@RequestBody Coordinate coord) {
        return parkingZoneService.isAtParkingZone(coord);
    }
}
