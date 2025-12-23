package com.juan.parking.services;

import com.juan.parking.entities.User;
import com.juan.parking.entities.Vehicle;
import com.juan.parking.repositories.UserRepository;
import com.juan.parking.repositories.VehicleRepository;
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
        Optional<Vehicle> optional_vehicle = vehicleRepository.findById(vehicle.getId());
        Optional<User> optional_user = userRepository.findById(userId);
        Vehicle db_vehicle = null;
        User db_user = null;

        if (optional_vehicle.isPresent() && optional_user.isPresent()) {
            db_vehicle = optional_vehicle.get();
            db_user = optional_user.get();

            db_user.addVehicle(vehicle);
            db_vehicle.addUser(db_user);

            vehicleRepository.save(db_vehicle);

            return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
        }else if (optional_vehicle.isEmpty() && optional_user.isPresent()) {
            db_user = optional_user.get();
            db_user.addVehicle(vehicle);
            vehicle.addUser(db_user);
            vehicleRepository.save(vehicle);

            return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("User does not exist and couldnt add the vehicle", HttpStatus.CONFLICT);
        }

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
