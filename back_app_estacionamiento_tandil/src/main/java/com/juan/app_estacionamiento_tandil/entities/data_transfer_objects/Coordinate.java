package com.juan.app_estacionamiento_tandil.entities.data_transfer_objects;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Embeddable
@Setter
public class Coordinate {

    private double latitude;
    private double longitude;

    public Coordinate() {}

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}