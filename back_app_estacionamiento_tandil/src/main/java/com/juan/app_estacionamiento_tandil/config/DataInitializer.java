package com.juan.app_estacionamiento_tandil.config;

import com.juan.app_estacionamiento_tandil.entities.Role;
import com.juan.app_estacionamiento_tandil.entities.User;
import com.juan.app_estacionamiento_tandil.entities.enums.ERole;
import com.juan.app_estacionamiento_tandil.repositories.RoleRepository;
import com.juan.app_estacionamiento_tandil.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserRepository userRepository;

    public DataInitializer(PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(ERole.ROLE_USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(ERole.ROLE_INSPECTOR).isEmpty()) {
            Role inspectorRole = new Role();
            inspectorRole.setName(ERole.ROLE_INSPECTOR);
            roleRepository.save(inspectorRole);
        }

        Optional<Role> inspector_rol = roleRepository.findByName(ERole.ROLE_INSPECTOR);

        if (inspector_rol.isPresent()) {
            Role inspectorRole = inspector_rol.get();
            Set<Role> roles = new HashSet<>();
            roles.add(inspectorRole);

            User user = new User();

            user.setUsername("INSPECTOR");
            user.setPassword(passwordEncoder.encode("123"));
            user.setId(1000L);
            user.setDni("432124");
            user.setSignInReg(LocalDateTime.now());
            user.setRoles(roles);


            userRepository.save(user);
        }

    }
}