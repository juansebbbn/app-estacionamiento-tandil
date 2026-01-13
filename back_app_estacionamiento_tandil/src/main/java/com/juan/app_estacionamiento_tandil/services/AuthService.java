package com.juan.app_estacionamiento_tandil.services;

import com.juan.app_estacionamiento_tandil.entities.Role;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Login_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Register_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Token_data_transfer;
import com.juan.app_estacionamiento_tandil.entities.enums.ERole;
import com.juan.app_estacionamiento_tandil.repositories.RoleRepository;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
    
    public Token_data_transfer register(Register_data_transfer dto) {
        // 1. Create new user entity
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setDni(dto.getDni());
        user.setSignInReg(LocalDateTime.now());
        user.setBalance(BigDecimal.ZERO);

        // 2. Encrypt password before saving
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 3. Assign default role (ROLE_USER)
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
        user.addRole(userRole);

        userRepository.save(user);

        // 4. Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return Token_data_transfer.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .roles(List.of("ROLE_USER"))
                .build();
    }

    public Token_data_transfer login(Login_data_transfer dto) {
        // 1. Authenticate user (this checks password automatically)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        // 2. Fetch user from DB
        User user = (User) userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return Token_data_transfer.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .build();
    }

    public Token_data_transfer refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token header");
        }

        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            User user = (User) userRepository.findByUsername(username).orElseThrow();

            // Validate if the refresh token is still valid
            if (jwtService.isTokenValid(refreshToken, user)) {
                String newAccessToken = jwtService.generateToken(user);

                return Token_data_transfer.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken) // Keep same refresh token or rotate
                        .username(user.getUsername())
                        .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                        .build();
            }
        }
        throw new RuntimeException("Token refresh failed");
    }
}

