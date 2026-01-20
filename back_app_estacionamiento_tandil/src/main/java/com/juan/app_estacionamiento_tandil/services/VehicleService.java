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

    public ResponseEntity<String> addVehicle(Vehicle_data_transfer vehicle_dto, String username) {

        Optional<User> optional_user = userRepository.findByUsername(username);

        if (optional_user.isPresent()) {

            User user = optional_user.get();

            Optional<Vehicle> optional_vehicle = vehicleRepository.findByPatent(vehicle_dto.getPatent());

            if (optional_vehicle.isPresent()) {
                Vehicle vehicle = optional_vehicle.get();

                if(!user.getVehicles().contains(vehicle)) {
                    user.addVehicle(vehicle);
                    vehicle.addUser(user);
                    vehicleRepository.save(vehicle);
                    userRepository.save(user);
                    return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
                }

                System.out.println("Vehicle already added");

            }

            Vehicle vehicledb = new Vehicle(vehicle_dto.getPatent(), vehicle_dto.getType());

            vehicledb.addUser(user);
            user.addVehicle(vehicledb);

            System.out.println("Adding vehicle: " + vehicledb);

            vehicleRepository.save(vehicledb);

            System.out.println("Vehicle added: " + vehicledb);

            return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
        }

        System.out.println("Could not add vehicle, user not found: " + username);

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Vehicle>> getVehicles(String username) {
        System.out.println("Fetching vehicles by username: " + username);

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            List<Vehicle> vehicles = user.get().getVehicles();

            System.out.println("Found vehicles: " + vehicles);

            return new ResponseEntity<>(vehicles, HttpStatus.OK);
        }

        System.out.println("Could not find vehicles by Userid: " + username);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteVehicle(String patent, String username) {
        System.out.println("Deleting vehicle: " + patent);
        boolean exists = false;

        Optional<User> userDb = userRepository.findByUsername(username);
        if (userDb.isPresent()) {
            User user = userDb.get();

            for(Vehicle vehicle : user.getVehicles()) {
                if (vehicle.getPatent().equals(patent)) {
                    exists = true;
                    break;
                }
            }

            Optional<Vehicle> vehicleDb = vehicleRepository.findByPatent(patent);

            if (vehicleDb.isPresent() && exists) {
                Vehicle vehicle = vehicleDb.get();

                vehicle.getUsers().remove(user);
                user.getVehicles().remove(vehicle);

                vehicleRepository.save(vehicle);
                userRepository.save(user);
            }

            System.out.println("Vehicle deleted: " + patent);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        System.out.println("Could not find vehicle, user not found: " + username);

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
