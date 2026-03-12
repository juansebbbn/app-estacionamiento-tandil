package com.juan.app_estacionamiento_tandil.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Register_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //deploys spring context
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ParkingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParkingRespository parkingRepository;

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
    void mustInitParkingSessionWithJwtAndRequireData() throws Exception {
        //data
        Coordinate coord = new Coordinate();
        coord.setLatitude(-37.3276);
        coord.setLongitude(-59.1367);

        //request
        mockMvc.perform(post("/api/parking/start/{patent}", "ABC123")
                        .header("Authorization", tokenValido) // Esto inyecta el @AuthenticationPrincipal
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coord)))
                .andExpect(status().is(201));

        //validate result
        var registro = parkingRepository.findByPatent("ABC123");
        assertTrue(registro.isPresent(), "Vehicle should exist in H2");
    }

    //para que termine la parking session debe haber una session, pero hasta donde tengo entendido cada funcion hace rollback en la db cuando temina
    //pero necesito que exista la parking session para finalizarla.
    @Test
    void mustFinishParkingSession() throws Exception {
        Coordinate coord = new Coordinate();
        coord.setLatitude(-37.3276);
        coord.setLongitude(-59.1367);

        ParkingTime sesionActiva = new ParkingTime(vehicleTest, LocalDateTime.now(), null, ParkingState.ACTIVE, coord, userTest);
        parkingRepository.save(sesionActiva);

        // WHEN: Llamamos al endpoint de finalizar
        mockMvc.perform(post("/api/parking/finish/{parkingId}", sesionActiva.getId())
                        .header("Authorization", tokenValido))
                .andExpect(status().is(201));

        var registro = parkingRepository.findById(sesionActiva.getId());
        registro.ifPresent(sess -> assertSame(ParkingState.FINISHED, sess.getState(), "State should be finished"));
    }

    @Test
    void mustReturnAllSessiones() throws Exception{
        Coordinate coord = new Coordinate();
        coord.setLatitude(-37.3276);
        coord.setLongitude(-59.1367);

        ParkingTime s1 = new ParkingTime(vehicleTest, LocalDateTime.now(), null, ParkingState.ACTIVE, coord, userTest);
        ParkingTime s2 = new ParkingTime(vehicleTest, LocalDateTime.now(), null, ParkingState.ACTIVE, coord, userTest);

        parkingRepository.save(s1);
        parkingRepository.save(s2);

        ArrayList<ParkingTime> mocked_list = new ArrayList<>();
        mocked_list.add(s1);
        mocked_list.add(s2);

        mockMvc.perform(get("/api/parking/getAllSessions")
                        .header("Authorization", tokenValido))
                .andExpect(status().isOk()); //get returns 200, not 201.

        ArrayList<ParkingTime> llpk = (ArrayList<ParkingTime>) parkingRepository.findAll();
        assertEquals(llpk, mocked_list, "The lists should be equals");
    }



}