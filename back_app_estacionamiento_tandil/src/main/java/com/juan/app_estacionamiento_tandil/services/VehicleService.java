package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.repositories.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> addVehicle(Vehicle vehicle, Long userId) {
        Optional<User> optional_user = userRepository.findById(userId);

        //continue from here. here is the error.

        if (optional_user.isPresent()) {
            User user = optional_user.get();

            vehicle.addUser(user);
            user.addVehicle(vehicle);

            vehicleRepository.save(vehicle);
            return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
        }

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Vehicle>> getVehicles(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Vehicle> vehicles = user.get().getVehicles();
            return new ResponseEntity<>(vehicles, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteVehicle(Long vehicleId) {
        vehicleRepository.deleteById(vehicleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
