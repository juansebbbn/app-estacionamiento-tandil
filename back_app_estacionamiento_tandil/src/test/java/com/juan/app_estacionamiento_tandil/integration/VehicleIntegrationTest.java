package com.juan.app_estacionamiento_tandil.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Register_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.VehicleType;
import com.juan.app_estacionamiento_tandil.repositories.ParkingRespository;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.repositories.VehicleRepository;
import com.juan.app_estacionamiento_tandil.services.AuthService;
import com.juan.app_estacionamiento_tandil.services.JwtService;
import com.juan.app_estacionamiento_tandil.services.VehicleService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class VehicleIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private String tokenValido;

    private Vehicle vehicleTest;

    private User userTest;

    @BeforeEach
    void setUp() {
        //user

        Register_data_transfer reg = new Register_data_transfer("juanse", "password", "4324234");

        authService.register(reg);

        Optional<User> user = userRepository.findByUsername("juanse");

        user.ifPresent(value -> this.userTest = value);


        //vehicle

//        Vehicle_data_transfer vdto = new Vehicle_data_transfer("ABC123", VehicleType.A);
//
//        vehicleService.addVehicle(vdto, "juanse");
//
//        Optional<Vehicle> vehicle = vehicleRepository.findByPatent("ABC123");
//
//        vehicle.ifPresent(value -> this.vehicleTest = value);

        //token

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("juanse")
                .password("password")
                .authorities("USER")
                .build();

        tokenValido = jwtService.generateToken(userDetails);
        tokenValido = "Bearer " + tokenValido;
    }

    @Test
    void vehicleIsPersisted() throws Exception {
        Vehicle_data_transfer vdto = new Vehicle_data_transfer("ABC123", VehicleType.A);

        mockMvc.perform(post("/api/vehicle/add")
                        .header("Authorization", tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vdto)))
                .andExpect(status().isOk());

        Optional<Vehicle> v = vehicleRepository.findByPatent("ABC123");
        assertTrue(v.isPresent(), "Vehicle should exists");
    }

    @Test
    void checkIfReturnsAllVehicles() throws Exception {
        Vehicle v1 = new Vehicle("ABC123", VehicleType.A);
        Vehicle v2 = new Vehicle("ABC124", VehicleType.A);

        vehicleRepository.save(v1);
        vehicleRepository.save(v2);

        ArrayList<Vehicle> vehicles_mock = new ArrayList<>();

        vehicles_mock.add(v1);
        vehicles_mock.add(v2);

        mockMvc.perform(get("/api/vehicle/get_all")
                        .header("Authorization", tokenValido))
                .andExpect(status().isOk());

        ArrayList<Vehicle> vehicles_db = (ArrayList<Vehicle>) vehicleRepository.findAll();
        assertEquals(vehicles_mock, vehicles_db, "List should be equals");
    }
}
