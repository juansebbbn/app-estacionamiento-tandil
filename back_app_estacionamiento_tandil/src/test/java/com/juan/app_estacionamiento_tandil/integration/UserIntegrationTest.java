package com.juan.app_estacionamiento_tandil.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Register_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.VehicleType;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

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

        Vehicle_data_transfer vdto = new Vehicle_data_transfer("ABC123", VehicleType.A);

        vehicleService.addVehicle(vdto, "juanse");

        Optional<Vehicle> vehicle = vehicleRepository.findByPatent("ABC123");

        vehicle.ifPresent(value -> this.vehicleTest = value);

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
    void checkIfReturnsUserCorrectly() throws Exception{
        mockMvc.perform(get("/api/users/getuser")
                        .header("Authorization", tokenValido))
                .andExpect(status().isOk());

        Optional<User> user = userRepository.findByUsername("juanse");

        user.ifPresent(value -> assertEquals(value, userTest));
    }
}
