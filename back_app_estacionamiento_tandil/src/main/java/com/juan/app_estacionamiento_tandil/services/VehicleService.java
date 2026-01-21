package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
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
        logger.info("Starting method addVehicle()");

        Optional<User> optional_user = userRepository.findByUsername(username);

        logger.info("Fetching user: {}", username);

        if (optional_user.isPresent()) {

            User user = optional_user.get();

            Optional<Vehicle> optional_vehicle = vehicleRepository.findByPatent(vehicle_dto.getPatent());

            logger.info("Fetching vehicle: {}", vehicle_dto.getPatent());

            if (optional_vehicle.isPresent()) {
                Vehicle vehicle = optional_vehicle.get();

                if(!user.getVehicles().contains(vehicle)) {
                    user.addVehicle(vehicle);
                    vehicle.addUser(user);
                    vehicleRepository.save(vehicle);
                    userRepository.save(user);

                    logger.info("Vehicle added");
                    logger.info("Method  addVehicle() finished");

                    return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
                }

                logger.info("Vehicle already added");
                logger.info("Method  addVehicle() finished (:");
                return new ResponseEntity<>("Vehicle already added", HttpStatus.OK);
            }

            Vehicle vehicledb = new Vehicle(vehicle_dto.getPatent(), vehicle_dto.getType());

            vehicledb.addUser(user);
            user.addVehicle(vehicledb);

            vehicleRepository.save(vehicledb);

            logger.info("Vehicle added.");
            logger.info("Method  addVehicle() finished.");

            return new ResponseEntity<>("Vehicle added", HttpStatus.OK);
        }

        logger.info("Could not linked vehicle");
        logger.info("Method addVehicle() finished");

        return new ResponseEntity<>("Could not linked vehicle", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<Vehicle>> getVehicles(String username) {
        logger.info("Starting method getVehicles()");

        Optional<User> user = userRepository.findByUsername(username);

        logger.info("Fetching user at getVehicles() : {}", username);

        if (user.isPresent()) {
            List<Vehicle> vehicles = user.get().getVehicles();

            logger.info("Method getVehicles() finished");

            return new ResponseEntity<>(vehicles, HttpStatus.OK);
        }


        logger.info("Could not find vehicle");
        logger.info("Method getVehicles() finished");

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteVehicle(String patent, String username) {
        logger.info("Starting method deleteVehicle()");

        boolean exists = false;

        Optional<User> userDb = userRepository.findByUsername(username);

        logger.info("Fetching user at deleteVehicle() : {}", username);

        if (userDb.isPresent()) {
            User user = userDb.get();

            for(Vehicle vehicle : user.getVehicles()) {
                if (vehicle.getPatent().equals(patent)) {
                    exists = true;
                    break;
                }
            }

            logger.info("Looking if user is linked with that vehicle");

            Optional<Vehicle> vehicleDb = vehicleRepository.findByPatent(patent);

            logger.info("Fetching vehicle data");

            if (vehicleDb.isPresent() && exists) {
                Vehicle vehicle = vehicleDb.get();

                for (ParkingTime pk : vehicle.getParkingTimes()) {
                    if(pk.getState().equals(ParkingState.ACTIVE)){
                        logger.info("Can not delete vehicle because has an active session");
                        //can not delete this vehicle because has an active session
                        return new ResponseEntity<>(HttpStatus.CONFLICT);
                    }
                }

                logger.info("Deleting vehicle");

                vehicle.getUsers().remove(user);
                user.getVehicles().remove(vehicle);

                vehicleRepository.save(vehicle);
                userRepository.save(user);
            }

            logger.info("Vehicle deleted");
            logger.info("Method deleteVehicle() finished");

            return new ResponseEntity<>(HttpStatus.OK);
        }

        logger.info("Could not delete vehicle");
        logger.info("Method deleteVehicle() finished");

        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
