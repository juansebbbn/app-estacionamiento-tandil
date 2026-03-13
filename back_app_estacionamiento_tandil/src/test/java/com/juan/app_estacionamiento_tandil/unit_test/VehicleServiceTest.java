package com.juan.app_estacionamiento_tandil.unit_test;

import com.juan.app_estacionamiento_tandil.entities.ParkingTime;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.Vehicle;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Vehicle_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
import com.juan.app_estacionamiento_tandil.entities.enums.VehicleType;
import com.juan.app_estacionamiento_tandil.exceptions.ResourceNotFoundException;
import com.juan.app_estacionamiento_tandil.exceptions.VehicleOperationException;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.repositories.VehicleRepository;
import com.juan.app_estacionamiento_tandil.services.VehicleService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private User testUser;
    private Vehicle testVehicle;
    private Vehicle_data_transfer vehicleDTO;
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
                .balance(BigDecimal.valueOf(100.0))
                .vehicles(new ArrayList<>())
                .build();

        // Create test vehicle
        testVehicle = new Vehicle("ABC123", VehicleType.A);
        testVehicle.setId(1L);
        testVehicle.setUsers(new ArrayList<>());
        testVehicle.setParkingTimes(new ArrayList<>());

        // Create vehicle DTO
        vehicleDTO = new Vehicle_data_transfer("ABC123", VehicleType.A);

        // Create active parking time
        activeParkingTime = new ParkingTime(
                testVehicle,
                LocalDateTime.now().minusHours(1),
                null,
                ParkingState.ACTIVE,
                null,
                testUser
        );
        activeParkingTime.setId(1L);

        // Create finished parking time
        finishedParkingTime = new ParkingTime(
                testVehicle,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1),
                ParkingState.FINISHED,
                null,
                testUser
        );
        finishedParkingTime.setId(2L);
    }

    @Test
    void addVehicle_NewVehicle_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        // When
        ResponseEntity<String> response = vehicleService.addVehicle(vehicleDTO, "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Vehicle added", response.getBody());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void addVehicle_ExistingVehicleNotLinkedToUser_Success() {
        // Given
        testVehicle.setUsers(new ArrayList<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<String> response = vehicleService.addVehicle(vehicleDTO, "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Vehicle added", response.getBody());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository).save(testVehicle);
        verify(userRepository).save(testUser);
        assertTrue(testUser.getVehicles().contains(testVehicle));
        assertTrue(testVehicle.getUsers().contains(testUser));
    }

    @Test
    void addVehicle_ExistingVehicleAlreadyLinkedToUser_ReturnsAlreadyAdded() {
        // Given
        testUser.addVehicle(testVehicle);
        testVehicle.addUser(testUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));

        // When
        ResponseEntity<String> response = vehicleService.addVehicle(vehicleDTO, "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Vehicle already added", response.getBody());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getVehicles_UserExists_ReturnsVehicleList() {
        // Given
        testUser.addVehicle(testVehicle);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<List<Vehicle>> response = vehicleService.getVehicles("testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC123", response.getBody().get(0).getPatent());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getVehicles_UserExistsWithNoVehicles_ReturnsEmptyList() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<List<Vehicle>> response = vehicleService.getVehicles("testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void deleteVehicle_VehicleHasOnlyFinishedParkingSessions_Success() {
        // Given
        testUser.addVehicle(testVehicle);
        testVehicle.addUser(testUser);
        testVehicle.getParkingTimes().add(finishedParkingTime);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<Void> response = vehicleService.deleteVehicle("ABC123", "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository).save(testVehicle);
        verify(userRepository).save(testUser);
        assertFalse(testUser.getVehicles().contains(testVehicle));
        assertFalse(testVehicle.getUsers().contains(testUser));
    }

    @Test
    void deleteVehicle_VehicleHasNoParkingSessions_Success() {
        // Given
        testUser.addVehicle(testVehicle);
        testVehicle.addUser(testUser);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<Void> response = vehicleService.deleteVehicle("ABC123", "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository).save(testVehicle);
        verify(userRepository).save(testUser);
        assertFalse(testUser.getVehicles().contains(testVehicle));
        assertFalse(testVehicle.getUsers().contains(testUser));
    }

    @Test
    void deleteVehicle_MultipleVehicles_DeleteOnlyCorrectOne() {
        // Given
        Vehicle vehicle2 = new Vehicle("XYZ789", VehicleType.M);
        vehicle2.setId(2L);
        vehicle2.setUsers(new ArrayList<>());
        vehicle2.setParkingTimes(new ArrayList<>());
        
        testUser.addVehicle(testVehicle);
        testUser.addVehicle(vehicle2);
        testVehicle.addUser(testUser);
        vehicle2.addUser(testUser);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<Void> response = vehicleService.deleteVehicle("ABC123", "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository).save(testVehicle);
        verify(userRepository).save(testUser);
        
        assertFalse(testUser.getVehicles().contains(testVehicle));
        assertTrue(testUser.getVehicles().contains(vehicle2));
        assertFalse(testVehicle.getUsers().contains(testUser));
        assertTrue(vehicle2.getUsers().contains(testUser));
    }

    @Test
    void addVehicle_MultipleUsersLinkToSameVehicle_Success() {
        // Given
        User user2 = User.builder()
                .id(2L)
                .username("testuser2")
                .password("password")
                .dni("87654321")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(50.0))
                .vehicles(new ArrayList<>())
                .build();
        
        testVehicle.addUser(user2);
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<String> response = vehicleService.addVehicle(vehicleDTO, "testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Vehicle added", response.getBody());
        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository).save(testVehicle);
        verify(userRepository).save(testUser);
        assertTrue(testUser.getVehicles().contains(testVehicle));
        assertTrue(testVehicle.getUsers().contains(testUser));
        assertTrue(testVehicle.getUsers().contains(user2));
        assertEquals(2, testVehicle.getUsers().size());
    }

    @Test
    void deleteVehicle_VehicleNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.deleteVehicle("ABC123", "testuser");
        });

        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteVehicle_VehicleNotLinkedToUser_ThrowsException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));

        assertThrows(VehicleOperationException.class, () -> {
            vehicleService.deleteVehicle("ABC123", "testuser");
        });

        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteVehicle_VehicleHasActiveParkingSession_ThrowsException() {
        // Given
        testUser.addVehicle(testVehicle);
        testVehicle.addUser(testUser);
        testVehicle.getParkingTimes().add(activeParkingTime);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(vehicleRepository.findByPatent("ABC123")).thenReturn(Optional.of(testVehicle));

        // When & Then
        assertThrows(VehicleOperationException.class, () -> {
            vehicleService.deleteVehicle("ABC123", "testuser");
        });

        verify(userRepository).findByUsername("testuser");
        verify(vehicleRepository).findByPatent("ABC123");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void getVehicles_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.getVehicles("nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void deleteVehicle_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.deleteVehicle("ABC123", "nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
        verify(vehicleRepository, never()).findByPatent(anyString());
    }

    @Test
    void addVehicle_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.addVehicle(vehicleDTO, "nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}
