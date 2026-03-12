package com.juan.app_estacionamiento_tandil.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.juan.app_estacionamiento_tandil.entities.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "vehicles",
        uniqueConstraints = @UniqueConstraint(columnNames = "patent"))
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParkingTime> parkingTimes = new ArrayList<>();

    public Vehicle(String patent, VehicleType type) {
        this.patent = patent;
        this.type = type;
    }

    protected Vehicle() {}

    public void addUser(User user) {
        this.users.add(user);
    }
}
