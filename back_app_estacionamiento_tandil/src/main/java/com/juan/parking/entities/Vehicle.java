package com.juan.parking.entities;

import com.juan.parking.entities.enums.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "vehicles",
        uniqueConstraints = @UniqueConstraint(columnNames = "patent"))
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
    @JoinColumn(name = "user_id", nullable = false)
    private List<User> user;

    @OneToMany(mappedBy = "vehicle")
    private List<ParkingTime> parkingTimes = new ArrayList<>();

    protected Vehicle() {}

    public void addUser(User user) {
        this.user.add(user);
    }
}
