package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Parking_time_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
import com.juan.app_estacionamiento_tandil.repositories.ParkingRespository;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.repositories.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.juan.app_estacionamiento_tandil.entities.enums.ParkingState.ACTIVE;
import static com.juan.app_estacionamiento_tandil.entities.enums.ParkingState.FINISHED;

@Service
public class ParkingService {
    private final VehicleRepository vehicleRepository;
    private final ParkingRespository parkingRespository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ParkingService.class);

    public ParkingService(VehicleRepository vehicleRepository,
                          ParkingRespository parkingRespository,
                          UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.parkingRespository = parkingRespository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Parking_time_data_transfer> startParkingSession(String patent, String username, Coordinate coordinate) {
        logger.info("Starting startParkingSession()");

        Optional<User> userdb = userRepository.findByUsername(username);

        logger.info("Fetching user at StartParkingSession() {}", username);

        if (userdb.isPresent()) {
            User user = userdb.get();

            Optional<Vehicle> vehdb = vehicleRepository.findByPatent(patent);

            if(vehdb.isPresent()) {
                Vehicle vehicle = vehdb.get();

                ParkingTime pk = new ParkingTime(vehicle, LocalDateTime.now(), null, ParkingState.ACTIVE, coordinate, user);

                parkingRespository.save(pk);

                logger.info("Parking session started");

                Parking_time_data_transfer ptdt = new Parking_time_data_transfer(pk.getId(), pk.getStartTime(), pk.getEndTime(), pk.getCoordinate(), pk.getVehicle().getPatent());

                logger.info("Method finished StartParkingSession() ");

                return ResponseEntity.status(HttpStatus.CREATED).body(ptdt);
            }
        }

        logger.info("Could not create parking session, no user or vehicle found");
        logger.info("Method finished StartParkingSession() ");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<String> finishParkingSession(Long parkingId, String username) {
        logger.info("Starting finishParkingSession()");

        Optional<ParkingTime> ptdb = parkingRespository.findById(parkingId);

        logger.info("Fetching parking time at finishParkingSession(), parking id: {}", parkingId);

        if(ptdb.isPresent()) {
            ParkingTime pk = ptdb.get();

            Optional<User> userdb = userRepository.findByUsername(username);

            logger.info("Fetching user at finishParkingSession() {}", username);

            if(userdb.isPresent()) {
                User user = userdb.get();

                pk.setState(FINISHED);

                pk.setEndTime(LocalDateTime.now());

                Duration duration = Duration.between(pk.getStartTime(), pk.getEndTime());

                Long minutes = duration.toMinutes();

                Long FEE = 100L;
                long totalCharge = minutes * FEE;

                BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(totalCharge));

                user.setBalance(newBalance);

                logger.info("State changed to FINISHED and updated user balance");

                userRepository.save(user);
                parkingRespository.save(pk);

                logger.info("Parking session finished");
                logger.info("Method finished finishParkingSession() ");

                return ResponseEntity.status(HttpStatus.CREATED).body("Parking session finished");
            }
        }

        logger.info("Could not finish parking session, no user or vehicle found");
        logger.info("Method finished finishParkingSession() ");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not finish parking session");
    }

    public List<ParkingTime> getAllSessions() {
        logger.info("Starting getAllSessions()");
        logger.info("Fetching parking time at getAllSessions()");
        logger.info("Method finished GetAllSessions()");
        return parkingRespository.findAll();
    }

    public ResponseEntity<Parking_time_data_transfer> userActiveSession(String username) {
        logger.info("Starting UserActiveSession()");

        Optional<User> userdb = userRepository.findByUsername(username);

        logger.info("Fetching user at userActiveSession() {}", username);

        if(userdb.isPresent()) {
            User user = userdb.get();

            logger.info("Looking if user has any active session at userActiveSession() {}", username);

            for (Vehicle vehicle : user.getVehicles()) {
                for (ParkingTime parkingTime : vehicle.getParkingTimes()) {
                    if(parkingTime.getUser().getUsername().equals(username) && parkingTime.getState() == ACTIVE) {
                        Parking_time_data_transfer pk = new Parking_time_data_transfer(parkingTime.getId(), LocalDateTime.now(), null, parkingTime.getCoordinate(), parkingTime.getVehicle().getPatent());
                        logger.info("User has an active session at userActiveSession() {}, active session id: ", parkingTime.getId() );
                        return ResponseEntity.status(HttpStatus.CREATED).body(pk);
                    }
                }
            }
        }

        logger.info("Could not find active parking session");
        logger.info("Method finished userActiveSession() ");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<Boolean> hasActiveSession(String patent) {
        logger.info("Starting hasActiveSession()");
        logger.info("Fetching parking time at hasActiveSession() {}", patent);
        Optional<ParkingTime> pkdb = parkingRespository.findByPatent(patent);
        logger.info("Method finished hasActiveSession()");
        return pkdb.isPresent() ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

}
