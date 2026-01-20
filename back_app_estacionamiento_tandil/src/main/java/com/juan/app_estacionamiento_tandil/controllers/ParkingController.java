package com.juan.app_estacionamiento_tandil.controllers;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Parking_time_data_transfer;
import com.juan.app_estacionamiento_tandil.services.ParkingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/start/{patent}")
    public ResponseEntity<Parking_time_data_transfer> startParkingSession(@PathVariable String patent,
                                                                          @RequestBody Coordinate coordinate,
                                                                          @AuthenticationPrincipal UserDetails currentUser) {


        String username = currentUser.getUsername();
        System.out.println("controller startParkingSession for username: " + username);
        return parkingService.startParkingSession(patent, username, coordinate);
    }

    @PostMapping("/finish/{parkingId}")
    public ResponseEntity<String> finishParkingSession(
            @PathVariable Long parkingId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        String username = currentUser.getUsername();
        return parkingService.finishParkingSession(parkingId, username);
    }

    @GetMapping("/admin/active/{patent}")
    public ResponseEntity<Boolean> hasActiveSession(@PathVariable String patent) {
        return parkingService.hasActiveSession(patent);
    }

    @GetMapping("/hasSession")
    public ResponseEntity<Parking_time_data_transfer> userActiveSession(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return parkingService.userActiveSession(username);
    }

    @GetMapping("/getAllSessions")
    public ResponseEntity<List<ParkingTime>> getAllSessions() {
        return ResponseEntity.ok(parkingService.getAllSessiones());
    }

}
