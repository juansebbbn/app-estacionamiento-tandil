package com.juan.app_estacionamiento_tandil.controllers;

import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.services.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addVehicle(
            @PathVariable Long userId,
            @RequestBody Vehicle_data_transfer vehicle) {
        if (userId == null) {
            System.out.println("Didnt proportionate a user id");
        }

        return vehicleService.addVehicle(vehicle, userId);
    }

    @GetMapping("/get all/{userId}")
    public ResponseEntity<List<Vehicle>> getVehicles(@PathVariable Long userId,
                                                     @RequestHeader Map<String, String> headers) {
        if (userId == null) {
            System.out.println("Didnt proportionate a user id");
        }

        return vehicleService.getVehicles(userId);
    }

    @DeleteMapping("/delete/{vehicleId}/{userId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId, @PathVariable Long userId) {
        if (vehicleId == null) {
            System.out.println("Didnt proportionate a vehicle id");
        }

        if (userId == null) {
            System.out.println("Didnt proportionate a user id");
        }

        return vehicleService.deleteVehicle(vehicleId, userId);
    }
}
