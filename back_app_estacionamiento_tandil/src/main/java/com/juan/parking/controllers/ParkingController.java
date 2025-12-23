package com.juan.parking.controllers;

import com.juan.parking.services.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/start/{patent}")
    public ResponseEntity<String> startParkingSession(@PathVariable String patent) {
        return parkingService.startParkingSession(patent);
    }

    @PostMapping("/finish/{patent}/{userid}")
    public ResponseEntity<String> finishParkingSession(@PathVariable String patent, @PathVariable Long userid) {
        return parkingService.finishParkingSession(patent, userid);
    }

    @GetMapping("/active/{patent}")
    public ResponseEntity<Boolean> hasActiveSession(@PathVariable String patent) {
        return parkingService.hasActiveSession(patent);
    }

}
