    package com.juan.app_estacionamiento_tandil.entities;

    import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
    import com.juan.app_estacionamiento_tandil.entities.enums.ParkingState;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDateTime;

    @Setter
    @Getter
    @Entity
    @Table(name = "parking_times")
    public class ParkingTime {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "vehicle_id", nullable = false)
        private Vehicle vehicle;

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @Column(nullable = false)
        private LocalDateTime startTime;

        private LocalDateTime endTime;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ParkingState state;

        private Coordinate coordinate;

        public ParkingTime(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime, ParkingState state, Coordinate coordinate, User user) {
            this.vehicle = vehicle;
            this.startTime = startTime;
            this.endTime = endTime;
            this.state = state;
            this.coordinate = coordinate;
            this.user = user;
        }

        protected ParkingTime() {}
    }
