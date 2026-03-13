package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Parking_time_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
import com.juan.app_estacionamiento_tandil.exceptions.ParkingSessionException;
import com.juan.app_estacionamiento_tandil.exceptions.ResourceNotFoundException;
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
        logger.info("[PARKING] [startParkingSession] START - patent={}, username={}", patent, username);

        Optional<User> userdb = userRepository.findByUsername(username);

        logger.info("[PARKING] [startParkingSession] FETCH_USER - username={}", username);

        if (userdb.isEmpty()) {
            logger.error("[PARKING] [startParkingSession] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        User user = userdb.get();

        Optional<Vehicle> vehdb = vehicleRepository.findByPatent(patent);

        logger.info("[PARKING] [startParkingSession] FETCH_VEHICLE - patent={}", patent);

        if(vehdb.isEmpty()) {
            logger.error("[PARKING] [startParkingSession] VEHICLE_NOT_FOUND - patent={}", patent);
            throw new ResourceNotFoundException("Vehicle not found: " + patent);
        }

        Vehicle vehicle = vehdb.get();

        ParkingTime pk = new ParkingTime(vehicle, LocalDateTime.now(), null, ParkingState.ACTIVE, coordinate, user);

        parkingRespository.save(pk);

        logger.info("[PARKING] [startParkingSession] SUCCESS - patent={}, username={}, parking_id={}", patent, username, pk.getId());

        Parking_time_data_transfer ptdt = new Parking_time_data_transfer(pk.getId(), pk.getStartTime(), pk.getEndTime(), pk.getCoordinate(), pk.getVehicle().getPatent());

        return ResponseEntity.status(HttpStatus.CREATED).body(ptdt);
    }

    public ResponseEntity<String> finishParkingSession(Long parkingId, String username) {
        logger.info("[PARKING] [finishParkingSession] START - parking_id={}, username={}", parkingId, username);

        Optional<ParkingTime> ptdb = parkingRespository.findById(parkingId);

        logger.info("[PARKING] [finishParkingSession] FETCH_PARKING - parking_id={}", parkingId);

        if(ptdb.isEmpty()) {
            logger.error("[PARKING] [finishParkingSession] PARKING_NOT_FOUND - parking_id={}", parkingId);
            throw new ResourceNotFoundException("Parking session not found: " + parkingId);
        }

        ParkingTime pk = ptdb.get();

        Optional<User> userdb = userRepository.findByUsername(username);

        logger.info("[PARKING] [finishParkingSession] FETCH_USER - username={}", username);

        if(userdb.isEmpty()) {
            logger.error("[PARKING] [finishParkingSession] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        User user = userdb.get();

        pk.setState(FINISHED);
        pk.setEndTime(LocalDateTime.now());

        Duration duration = Duration.between(pk.getStartTime(), pk.getEndTime());
        Long minutes = duration.toMinutes();
        Long FEE = 100L;
        long totalCharge = minutes * FEE;

        BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(totalCharge));

        user.setBalance(newBalance);

        logger.info("[PARKING] [finishParkingSession] CALCULATE_FEE - minutes={}, total_charge={}", minutes, totalCharge);

        userRepository.save(user);
        parkingRespository.save(pk);

        logger.info("[PARKING] [finishParkingSession] SUCCESS - parking_id={}, username={}, final_balance={}", 
            parkingId, username, newBalance);

        return ResponseEntity.status(HttpStatus.CREATED).body("Parking session finished");
    }

    public List<ParkingTime> getAllSessions() {
        logger.info("[PARKING] [getAllSessions] START");
        
        List<ParkingTime> sessions = parkingRespository.findAll();
        
        logger.info("[PARKING] [getAllSessions] SUCCESS - session_count={}", sessions.size());
        return sessions;
    }

    public ResponseEntity<Parking_time_data_transfer> userActiveSession(String username) {
        logger.info("[PARKING] [userActiveSession] START - username={}", username);

        Optional<User> userdb = userRepository.findByUsername(username);

        logger.info("[PARKING] [userActiveSession] FETCH_USER - username={}", username);

        if(userdb.isEmpty()) {
            logger.error("[PARKING] [userActiveSession] USER_NOT_FOUND - username={}", username);
            throw new ResourceNotFoundException("User not found: " + username);
        }

        User user = userdb.get();

        logger.info("[PARKING] [userActiveSession] SEARCH_ACTIVE - username={}", username);

        for (Vehicle vehicle : user.getVehicles()) {
            for (ParkingTime parkingTime : vehicle.getParkingTimes()) {
                if(parkingTime.getUser().getUsername().equals(username) && parkingTime.getState() == ACTIVE) {
                    Parking_time_data_transfer pk = new Parking_time_data_transfer(parkingTime.getId(), LocalDateTime.now(), null, parkingTime.getCoordinate(), parkingTime.getVehicle().getPatent());
                    logger.info("[PARKING] [userActiveSession] SUCCESS - username={}, active_session_id={}", username, parkingTime.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(pk);
                }
            }
        }

        logger.info("[PARKING] [userActiveSession] NO_ACTIVE_SESSION - username={}", username);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<Boolean> hasActiveSession(String patent) {
        logger.info("[PARKING] [hasActiveSession] START - patent={}", patent);
        
        Optional<ParkingTime> pkdb = parkingRespository.findByPatent(patent);
        
        boolean hasActive = pkdb.isPresent() && pkdb.get().getState() == ACTIVE;
        
        logger.info("[PARKING] [hasActiveSession] SUCCESS - patent={}, has_active={}", patent, hasActive);
        
        return hasActive ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

}
