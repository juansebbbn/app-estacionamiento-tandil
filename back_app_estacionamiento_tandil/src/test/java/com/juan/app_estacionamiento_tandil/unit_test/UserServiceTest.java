package com.juan.app_estacionamiento_tandil.unit_test;

import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.exceptions.ResourceNotFoundException;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import com.juan.app_estacionamiento_tandil.services.UserService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedpassword")
                .dni("12345678")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(1000.0))
                .build();
    }

    @Test
    void getUserByUsername_UserExists_ReturnsUser() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<User> response = userService.getUserByUsername("testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("12345678", response.getBody().getDni());
        assertEquals(BigDecimal.valueOf(1000.0), response.getBody().getBalance());
        
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_UserDoesNotExist_ThrowsException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername("nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void getUserByUsername_NullUsername_ThrowsException() {
        // Given
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername(null);
        });

        verify(userRepository).findByUsername(null);
    }

    @Test
    void getUserByUsername_EmptyUsername_ThrowsException() {
        // Given
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername("");
        });

        verify(userRepository).findByUsername("");
    }

    @Test
    void getUserByUsername_UsernameWithSpecialCharacters_ThrowsException() {
        // Given
        String specialUsername = "user@domain.com";
        when(userRepository.findByUsername(specialUsername)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByUsername(specialUsername);
        });

        verify(userRepository).findByUsername(specialUsername);
    }

    @Test
    void getUserByUsername_UsernameWithNumbers_ReturnsUserIfExists() {
        // Given
        User userWithNumbers = User.builder()
                .id(2L)
                .username("user123")
                .password("password")
                .dni("87654321")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(500.0))
                .build();
        
        when(userRepository.findByUsername("user123")).thenReturn(Optional.of(userWithNumbers));

        // When
        ResponseEntity<User> response = userService.getUserByUsername("user123");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user123", response.getBody().getUsername());
        assertEquals("87654321", response.getBody().getDni());
        assertEquals(BigDecimal.valueOf(500.0), response.getBody().getBalance());
        
        verify(userRepository).findByUsername("user123");
    }

    @Test
    void getUserByUsername_UserWithZeroBalance_ReturnsUser() {
        // Given
        User userWithZeroBalance = User.builder()
                .id(3L)
                .username("zerobalance")
                .password("password")
                .dni("11111111")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .build();
        
        when(userRepository.findByUsername("zerobalance")).thenReturn(Optional.of(userWithZeroBalance));

        // When
        ResponseEntity<User> response = userService.getUserByUsername("zerobalance");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("zerobalance", response.getBody().getUsername());
        assertEquals(BigDecimal.ZERO, response.getBody().getBalance());
        
        verify(userRepository).findByUsername("zerobalance");
    }

    @Test
    void getUserByUsername_UserWithNegativeBalance_ReturnsUser() {
        // Given
        User userWithNegativeBalance = User.builder()
                .id(4L)
                .username("negativebalance")
                .password("password")
                .dni("22222222")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(-100.0))
                .build();
        
        when(userRepository.findByUsername("negativebalance")).thenReturn(Optional.of(userWithNegativeBalance));

        // When
        ResponseEntity<User> response = userService.getUserByUsername("negativebalance");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("negativebalance", response.getBody().getUsername());
        assertEquals(BigDecimal.valueOf(-100.0), response.getBody().getBalance());
        
        verify(userRepository).findByUsername("negativebalance");
    }

    @Test
    void getUserByUsername_RepositoryThrowsException_PropagatesException() {
        // Given
        when(userRepository.findByUsername("erroruser")).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByUsername("erroruser");
        });
        
        verify(userRepository).findByUsername("erroruser");
    }

    @Test
    void getUserByUsername_CaseSensitiveUsername_ReturnsCorrectResult() {
        // Given
        User upperCaseUser = User.builder()
                .id(5L)
                .username("TestUser")
                .password("password")
                .dni("33333333")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(200.0))
                .build();
        
        when(userRepository.findByUsername("TestUser")).thenReturn(Optional.of(upperCaseUser));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        ResponseEntity<User> responseUpperCase = userService.getUserByUsername("TestUser");
        ResponseEntity<User> responseLowerCase = userService.getUserByUsername("testuser");

        // Then
        assertEquals(HttpStatus.OK, responseUpperCase.getStatusCode());
        assertEquals("TestUser", responseUpperCase.getBody().getUsername());
        
        assertEquals(HttpStatus.OK, responseLowerCase.getStatusCode());
        assertEquals("testuser", responseLowerCase.getBody().getUsername());
        
        verify(userRepository).findByUsername("TestUser");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_LongUsername_ReturnsUserIfExists() {
        // Given
        String longUsername = "verylongusernamethatmightbeusedinsomesystems";
        User userWithLongUsername = User.builder()
                .id(6L)
                .username(longUsername)
                .password("password")
                .dni("44444444")
                .signInReg(LocalDateTime.now())
                .balance(BigDecimal.valueOf(750.0))
                .build();
        
        when(userRepository.findByUsername(longUsername)).thenReturn(Optional.of(userWithLongUsername));

        // When
        ResponseEntity<User> response = userService.getUserByUsername(longUsername);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(longUsername, response.getBody().getUsername());
        
        verify(userRepository).findByUsername(longUsername);
    }

    @Test
    void getUserByUsername_VerifyRepositoryInteraction() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        userService.getUserByUsername("testuser");

        // Then
        verify(userRepository, times(1)).findByUsername("testuser");
        verifyNoMoreInteractions(userRepository);
    }
}
