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
    private final Long FEE = 100L;
    private final VehicleRepository vehicleRepository;
    private final ParkingRespository parkingRespository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ParkingService(VehicleRepository vehicleRepository,
                          ParkingRespository parkingRespository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.vehicleRepository = vehicleRepository;
        this.parkingRespository = parkingRespository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public ResponseEntity<Parking_time_data_transfer> startParkingSession(String patent, String username, Coordinate coordinate) {
        Optional<User> userdb = userRepository.findByUsername(username);

        System.out.println("service startParkingSession for username: " + username);

        if (userdb.isPresent()) {
            User user = userdb.get();

            System.out.println("hay usuario " + user.getUsername());

            Optional<Vehicle> vehdb = vehicleRepository.findByPatent(patent);

            if(vehdb.isPresent()) {

                Vehicle vehicle = vehdb.get();

                ParkingTime pk = new ParkingTime(vehicle, LocalDateTime.now(), null, ParkingState.ACTIVE, coordinate, user);

                parkingRespository.save(pk);

                Parking_time_data_transfer ptdt = new Parking_time_data_transfer(pk.getId(), pk.getStartTime(), pk.getEndTime(), pk.getCoordinate(), pk.getVehicle().getPatent());

                return ResponseEntity.status(HttpStatus.CREATED).body(ptdt);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    public ResponseEntity<String> finishParkingSession(Long parkingId, String username) {
        Optional<ParkingTime> ptdb = parkingRespository.findById(parkingId);

        if(ptdb.isPresent()) {
            ParkingTime pk = ptdb.get();

            Optional<User> userdb = userRepository.findByUsername(username);

            if(userdb.isPresent()) {
                User user = userdb.get();

                pk.setState(FINISHED);

                pk.setEndTime(LocalDateTime.now());

                Duration duration = Duration.between(pk.getStartTime(), pk.getEndTime());

                Long minutes = duration.toMinutes();

                long totalCharge = minutes * FEE;

                BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(totalCharge));

                user.setBalance(newBalance);

                userRepository.save(user);
                parkingRespository.save(pk);

                return ResponseEntity.status(HttpStatus.CREATED).body("Parking session finished");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not finish parking session");
    }

    public ResponseEntity<Boolean> hasActiveSession(String patent) {
        Optional<ParkingTime> pkdb = parkingRespository.findByPatent(patent);
        return pkdb.isPresent() ? ResponseEntity.ok(true) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    public List<ParkingTime> getAllSessiones() {
        return parkingRespository.findAll();
    }

    public ResponseEntity<Parking_time_data_transfer> userActiveSession(String username) {
        Optional<User> userdb = userRepository.findByUsername(username);

        if(userdb.isPresent()) {
            User user = userdb.get();
            for (Vehicle vehicle : user.getVehicles()) {
                for (ParkingTime parkingTime : vehicle.getParkingTimes()) {
                    if(parkingTime.getUser().getUsername().equals(username) && parkingTime.getState() == ACTIVE) {
                        Parking_time_data_transfer pk = new Parking_time_data_transfer(parkingTime.getId(), LocalDateTime.now(), null, parkingTime.getCoordinate(), parkingTime.getVehicle().getPatent());
                        return ResponseEntity.status(HttpStatus.CREATED).body(pk);
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
