package com.juan.app_estacionamiento_tandil.controllers;

import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.services.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addVehicle(
            @RequestBody Vehicle_data_transfer vehicle,
            @AuthenticationPrincipal UserDetails currentUser) {

        String username = currentUser.getUsername();
        return vehicleService.addVehicle(vehicle, username);
    }

    @GetMapping("/get_all")
    public ResponseEntity<List<Vehicle>> getVehicles(@AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return vehicleService.getVehicles(username);
    }

    @DeleteMapping("/delete/{patent}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String patent, @AuthenticationPrincipal UserDetails currentUser) {
        String username = currentUser.getUsername();
        return vehicleService.deleteVehicle(patent, username);
    }
}
