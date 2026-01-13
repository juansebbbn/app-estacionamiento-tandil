package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
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

import static com.juan.app_estacionamiento_tandil.entities.enums.ParkingState.ACTIVE;
import static com.juan.app_estacionamiento_tandil.entities.enums.ParkingState.FINISHED;

@Service
public class ParkingService {
    private final static BigDecimal priceperminute = BigDecimal.valueOf(10.0);
    private final static BigDecimal infringement_limit = BigDecimal.valueOf(-3000);
    private final VehicleRepository vehicleRepository;
    private final ParkingRespository parkingRespository;
    private final UserRepository userRepository;

    public ParkingService(VehicleRepository vehicleRepository, ParkingRespository parkingRespository,
                          UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.parkingRespository = parkingRespository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> startParkingSession(String patent) {
        System.out.println("Starting parking session: " + patent);

        Vehicle vehicle = vehicleRepository.findByPatent(patent);

        if(vehicle == null) {
            System.out.println("Vehicle not found.");
            return new ResponseEntity<>("Vehicle does not exist.", HttpStatus.NOT_FOUND);
        }

        ParkingTime pt = new ParkingTime(vehicle, LocalDateTime.now(), null, ACTIVE);
        parkingRespository.save(pt);
        System.out.println("Parking session started.");
        return new ResponseEntity<>("Session started.", HttpStatus.OK);
    }

    public ResponseEntity<String> finishParkingSession(String patent, Long userId) {
        System.out.println("Finishing parking session: " + patent);

        Vehicle vehicle = vehicleRepository.findByPatent(patent);

        if(vehicle == null) {
            System.out.println("Vehicle not found.");
            return new ResponseEntity<>("Vehicle not found.", HttpStatus.NOT_FOUND);
        }

        //obtain user related with that vehicle and then iterate to find who is gonna pay (with id)
        List<User> users = vehicle.getUsers();

        User payer = null;

        for (User user : users) {
            if (user.getId().equals(userId)) {
                payer = user;
            }else{
                System.out.println("User not found.");
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }
        }

        //look for all parking times session of the vehicle
        List<ParkingTime> parkingTimes = vehicle.getParkingTimes();


        for(ParkingTime pt : parkingTimes) {
            if (pt.getState() == ACTIVE) {  //if one of the is active we must desactivate it
                pt.setState(FINISHED);
                pt.setEndTime(LocalDateTime.now());

                System.out.println("Calculating and subtracting parking time amount...");

                Duration duration = Duration.between(pt.getStartTime(), LocalDateTime.now());
                BigDecimal minutes = BigDecimal.valueOf(duration.toMinutes());

                BigDecimal totalPrice = priceperminute.multiply(minutes);

                assert payer != null;


                BigDecimal userbalance = payer.getBalance();
                userbalance = userbalance.subtract(totalPrice);

            
                if (userbalance.compareTo(infringement_limit) < 0) {
                    System.out.println("Infringement limit reached, create Infringement"); //logic to make an infrigment
                }

                payer.setBalance(userbalance);

                userRepository.save(payer);
                parkingRespository.save(pt);

                System.out.println("Parking session finished: " + patent);
                System.out.println("Amount subtracted: " + totalPrice);

                return new ResponseEntity<>("Session ended.", HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Could find or end the sesion.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Boolean> hasActiveSession(String patent) {
        System.out.println("Has active session: " + patent);

        Vehicle vehicle = vehicleRepository.findByPatent(patent);

        if(vehicle == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        List<ParkingTime> ptm = vehicle.getParkingTimes();

        for(ParkingTime pt : ptm) {
            if(pt.getState() == ACTIVE) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }
}
