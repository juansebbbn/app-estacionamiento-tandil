package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
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

    public ResponseEntity<String> addVehicle(Vehicle_data_transfer vehicle_dto, Long userId) {

        Optional<User> optional_user = userRepository.findById(userId);

        Vehicle vehicledb = new Vehicle(vehicle_dto.getPatent(), vehicle_dto.getType());

        System.out.println("Adding vehicle: " + vehicledb);

        if (optional_user.isPresent()) {
            User user = optional_user.get();

            vehicledb.addUser(user);
            user.addVehicle(vehicledb);

            vehicleRepository.save(vehicledb);

            System.out.println("Vehicle added: " + vehicledb);

            return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
        }

        System.out.println("Could not add vehicle, user not found: " + userId);

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Vehicle>> getVehicles(Long userId) {
        System.out.println("Fetching vehicles by Userid: " + userId);

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            List<Vehicle> vehicles = user.get().getVehicles();

            System.out.println("Found vehicles: " + vehicles);

            return new ResponseEntity<>(vehicles, HttpStatus.OK);
        }

        System.out.println("Could not find vehicles by Userid: " + userId);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteVehicle(Long vehicleId, Long userId) {
        System.out.println("Deleting vehicle: " + vehicleId);
        boolean exists = false;

        Optional<User> userDb = userRepository.findById(userId);
        if (userDb.isPresent()) {
            User user = userDb.get();

            for(Vehicle vehicle : user.getVehicles()) {
                System.out.println("CARID: " + vehicle.getId());
                if (vehicle.getId().equals(vehicleId)) {
                    exists = true;
                    break;
                }
            }

            Optional<Vehicle> vehicleDb = vehicleRepository.findById(vehicleId);

            if (vehicleDb.isPresent() && exists) {
                user.getVehicles().remove(vehicleDb.get());
                vehicleRepository.deleteById(vehicleId);
            }

            System.out.println("Vehicle deleted: " + vehicleId);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        System.out.println("Could not find vehicle, user not found: " + userId);

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
