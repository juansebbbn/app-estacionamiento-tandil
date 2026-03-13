package com.juan.app_estacionamiento_tandil.unit_test;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Parking_time_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
import com.juan.app_estacionamiento_tandil.entities.enums.VehicleType;
import com.juan.app_estacionamiento_tandil.exceptions.ResourceNotFoundException;
import com.juan.app_estacionamiento_tandil.repositories.ParkingRespository;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.repositories.VehicleRepository;
import com.juan.app_estacionamiento_tandil.services.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingRespository parkingRespository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParkingService parkingService;

    private User testUser;
    private Vehicle testVehicle;
    private Coordinate testCoordinate;
    private ParkingTime activeParkingTime;
    private ParkingTime finishedParkingTime;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .dni("12345678")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(1000.0))
                .vehicles(new ArrayList<>())
                .build();

        // Create test vehicle
        testVehicle = new Vehicle("ABC123", VehicleType.A);
        testVehicle.setId(1L);
        testVehicle.setUsers(new ArrayList<>());
        testVehicle.setParkingTimes(new ArrayList<>());

        // Create test coordinate
        testCoordinate = new Coordinate(-37.321, -59.133);

        // Create active parking time
        activeParkingTime = new ParkingTime(
                testVehicle,
                LocalDateTime.now().minusHours(1),
                null,
                ParkingState.ACTIVE,
                testCoordinate,
                testUser
        );
        activeParkingTime.setId(1L);

        // Create finished parking time
        finishedParkingTime = new ParkingTime(
                testVehicle,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                ParkingState.FINISHED,
                testCoordinate,
                testUser
        );
        finishedParkingTime.setId(2L);
    }

    @Test
    void startParkingSession_UserAndVehicleExist_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));
        when(parkingRespository.save(any(ParkingTime.class))).thenAnswer(invocation -> {
            ParkingTime saved = invocation.getArgument(0);
            saved.setId(1L); // Simulate database assigning an ID
            return saved;
        });

        // When
        ResponseEntity<Parking_time_data_transfer> response = parkingService.startParkingSession("ABC123", "testuser", testCoordinate);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ABC123", response.getBody().getPatent());
        assertNotNull(response.getBody().getStartTime());
        assertEquals(testCoordinate, response.getBody().getCoordinate());
        
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(parkingRespository).save(any(ParkingTime.class));
    }

    @Test
    void startParkingSession_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            parkingService.startParkingSession("ABC123", "nonexistent", testCoordinate);
        });

        verify(userRepository).findByUsername("nonexistent");
        verify(vehicleRepository, never()).findByPatent(anyString());
        verify(parkingRespository, never()).save(any(ParkingTime.class));
    }

    @Test
    void startParkingSession_VehicleNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("XYZ789")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            parkingService.startParkingSession("XYZ789", "testuser", testCoordinate);
        });

        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("XYZ789");
        verify(parkingRespository, never()).save(any(ParkingTime.class));
    }

    @Test
    void finishParkingSession_ParkingNotFound_ThrowsException() {
        // Given
        when(parkingRespository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            parkingService.finishParkingSession(999L, "testuser");
        });

        verify(parkingRespository).findById(999L);
        verify(userRepository, never()).findByUsername(anyString());
        verify(parkingRespository, never()).save(any(ParkingTime.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void finishParkingSession_UserNotFound_ThrowsException() {
        // Given
        when(parkingRespository.findById(1L)).thenReturn(Optional.of(activeParkingTime));
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            parkingService.finishParkingSession(1L, "nonexistent");
        });

        // Validar el mensaje
        assertEquals("User not found: nonexistent", exception.getMessage());

        // Verificaciones de Mockito
        verify(userRepository).findByUsername("nonexistent");
        verify(parkingRespository, never()).save(any(ParkingTime.class));
    }

    @Test
    void finishParkingSession_CalculatesCorrectFee() {
        // Given
        // Create parking session 1 hour ago
        ParkingTime oneHourSession = new ParkingTime(
                testVehicle,
                LocalDateTime.now().minusHours(1),
                null,
                ParkingState.ACTIVE,
                testCoordinate,
                testUser
        );
        oneHourSession.setId(3L);

        when(parkingRespository.findById(3L)).thenReturn(Optional.of(oneHourSession));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(parkingRespository.save(any(ParkingTime.class))).thenReturn(oneHourSession);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<String> response = parkingService.finishParkingSession(3L, "testuser");

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        
        // 60 minutes * 100 fee = 6000 should be deducted
        BigDecimal expectedBalance = BigDecimal.valueOf(1000.0).subtract(BigDecimal.valueOf(6000));
        assertEquals(expectedBalance, testUser.getBalance());
    }

    @Test
    void getAllSessions_ReturnsAllSessions() {
        // Given
        List<ParkingTime> allSessions = List.of(activeParkingTime, finishedParkingTime);
        when(parkingRespository.findAll()).thenReturn(allSessions);

        // When
        List<ParkingTime> result = parkingService.getAllSessions();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(activeParkingTime));
        assertTrue(result.contains(finishedParkingTime));
        
        verify(parkingRespository).findAll();
    }

    @Test
    void getAllSessions_EmptyList_ReturnsEmpty() {
        // Given
        when(parkingRespository.findAll()).thenReturn(new ArrayList<>());

        // When
        List<ParkingTime> result = parkingService.getAllSessions();

        // Then
        assertTrue(result.isEmpty());
        
        verify(parkingRespository).findAll();
    }

    @Test
    void userActiveSession_UserHasActiveSession_ReturnsSession() {
        // Given
        testUser.addVehicle(testVehicle);
        testVehicle.addUser(testUser);
        testVehicle.getParkingTimes().add(activeParkingTime);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<Parking_time_data_transfer> response = parkingService.userActiveSession("testuser");

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ABC123", response.getBody().getPatent());
        assertNotNull(response.getBody().getCoordinate());
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void userActiveSession_UserHasNoActiveSession_ReturnsNotFound() {
        // Given
        testUser.addVehicle(testVehicle);
        testVehicle.addUser(testUser);
        testVehicle.getParkingTimes().add(finishedParkingTime); // Only finished session
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<Parking_time_data_transfer> response = parkingService.userActiveSession("testuser");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void userActiveSession_UserNotFound_ReturnsNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            parkingService.userActiveSession("nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void userActiveSession_UserHasNoVehicles_ReturnsNotFound() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<Parking_time_data_transfer> response = parkingService.userActiveSession("testuser");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void hasActiveSession_VehicleHasActiveSession_ReturnsTrue() {
        // Given
        when(parkingRespository.findByPatent("ABC123")).thenReturn(Optional.of(activeParkingTime));

        // When
        ResponseEntity<Boolean> response = parkingService.hasActiveSession("ABC123");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        
        verify(parkingRespository).findByPatent("ABC123");
    }

    @Test
    void hasActiveSession_VehicleHasNoActiveSession_ReturnsFalse() {
        // Given
        when(parkingRespository.findByPatent("XYZ789")).thenReturn(Optional.empty());

        // When
        ResponseEntity<Boolean> response = parkingService.hasActiveSession("XYZ789");

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody());
        
        verify(parkingRespository).findByPatent("XYZ789");
    }

    @Test
    void userActiveSession_MultipleVehicles_FindsCorrectActiveSession() {
        // Given
        Vehicle vehicle2 = new Vehicle("XYZ789", VehicleType.M);
        vehicle2.setId(2L);
        vehicle2.setUsers(new ArrayList<>());
        vehicle2.setParkingTimes(new ArrayList<>());
        
        testUser.addVehicle(testVehicle);
        testUser.addVehicle(vehicle2);
        testVehicle.addUser(testUser);
        vehicle2.addUser(testUser);
        
        // Only first vehicle has active session
        testVehicle.getParkingTimes().add(activeParkingTime);
        vehicle2.getParkingTimes().add(finishedParkingTime);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<Parking_time_data_transfer> response = parkingService.userActiveSession("testuser");

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ABC123", response.getBody().getPatent()); // Should find the active one
        
        verify(userRepository).findByUsername("testuser");
    }
}
