package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
import com.juan.app_estacionamiento_tandil.exceptions.ResourceNotFoundException;
import com.juan.app_estacionamiento_tandil.exceptions.VehicleOperationException;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.repositories.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    public VehicleService(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> addVehicle(Vehicle_data_transfer vehicle_dto, String username) {
        logger.info("[VEHICLE] [addVehicle] START - username={}", username);

        Optional<User> optional_user = userRepository.findByUsername(username);

        logger.info("[VEHICLE] [addVehicle] FETCH_USER - username={}", username);

        if (optional_user.isEmpty()) {
            logger.error("[VEHICLE] [addVehicle] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        User user = optional_user.get();

        Optional<Vehicle> optional_vehicle = vehicleRepository.findByPatent(vehicle_dto.getPatent());

        logger.info("[VEHICLE] [addVehicle] FETCH_VEHICLE - patent={}", vehicle_dto.getPatent());

        if (optional_vehicle.isPresent()) {
            Vehicle vehicle = optional_vehicle.get();

            if(!user.getVehicles().contains(vehicle)) {
                user.addVehicle(vehicle);
                vehicle.addUser(user);
                vehicleRepository.save(vehicle);
                userRepository.save(user);

                logger.info("[VEHICLE] [addVehicle] LINK_EXISTING - patent={}, username={}", vehicle.getPatent(), username);
                logger.info("[VEHICLE] [addVehicle] SUCCESS - patent={}, username={}", vehicle.getPatent(), username);

                return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
            }

            logger.info("[VEHICLE] [addVehicle] ALREADY_LINKED - patent={}, username={}", vehicle.getPatent(), username);
            logger.info("[VEHICLE] [addVehicle] SUCCESS - patent={}, username={}", vehicle.getPatent(), username);
            return new ResponseEntity<>("Vehicle already added", HttpStatus.OK);
        }

        Vehicle vehicledb = new Vehicle(vehicle_dto.getPatent(), vehicle_dto.getType());

        vehicledb.addUser(user);
        user.addVehicle(vehicledb);

        vehicleRepository.save(vehicledb);

        logger.info("[VEHICLE] [addVehicle] CREATE_NEW - patent={}, username={}", vehicle_dto.getPatent(), username);
        logger.info("[VEHICLE] [addVehicle] SUCCESS - patent={}, username={}", vehicle_dto.getPatent(), username);

        return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
    }

    public ResponseEntity<List<Vehicle>> getVehicles(String username) {
        logger.info("[VEHICLE] [getVehicles] START - username={}", username);

        Optional<User> user = userRepository.findByUsername(username);

        logger.info("[VEHICLE] [getVehicles] FETCH_USER - username={}", username);

        if (user.isEmpty()) {
            logger.error("[VEHICLE] [getVehicles] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        List<Vehicle> vehicles = user.get().getVehicles();

        logger.info("[VEHICLE] [getVehicles] SUCCESS - username={}, vehicle_count={}", username, vehicles.size());

        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteVehicle(String patent, String username) {
        logger.info("[VEHICLE] [deleteVehicle] START - patent={}, username={}", patent, username);

        boolean exists = false;

        Optional<User> userDb = userRepository.findByUsername(username);

        logger.info("[VEHICLE] [deleteVehicle] FETCH_USER - username={}", username);

        if (userDb.isEmpty()) {
            logger.error("[VEHICLE] [deleteVehicle] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        User user = userDb.get();

        for(Vehicle vehicle : user.getVehicles()) {
            if (vehicle.getPatent().equals(patent)) {
                exists = true;
                break;
            }
        }

        logger.info("[VEHICLE] [deleteVehicle] CHECK_LINK - patent={}, username={}, linked={}", patent, username, exists);

        Optional<Vehicle> vehicleDb = vehicleRepository.findByPatent(patent);

        logger.info("[VEHICLE] [deleteVehicle] FETCH_VEHICLE - patent={}", patent);

        if (vehicleDb.isEmpty()) {
            logger.error("[VEHICLE] [deleteVehicle] VEHICLE_NOT_FOUND - patent={}", patent);
            throw new ResourceNotFoundException("Vehicle not found: " + patent);
        }

        if (!exists) {
            logger.error("[VEHICLE] [deleteVehicle] NOT_LINKED - patent={}, username={}", patent, username);
            throw new VehicleOperationException("Vehicle is not linked to user: " + username);
        }

        Vehicle vehicle = vehicleDb.get();

        for (ParkingTime pk : vehicle.getParkingTimes()) {
            if(pk.getState().equals(ParkingState.ACTIVE)){
                logger.error("[VEHICLE] [deleteVehicle] ACTIVE_SESSION - patent={}, username={}", patent, username);
                throw new VehicleOperationException("Cannot delete vehicle because has an active session");
            }
        }

        logger.info("[VEHICLE] [deleteVehicle] REMOVE_LINKS - patent={}, username={}", patent, username);

        vehicle.getUsers().remove(user);
        user.getVehicles().remove(vehicle);

        vehicleRepository.save(vehicle);
        userRepository.save(user);

        logger.info("[VEHICLE] [deleteVehicle] SUCCESS - patent={}, username={}", patent, username);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
