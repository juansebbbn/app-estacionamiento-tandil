package com.juan.app_estacionamiento_tandil.entities.enums;

import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;

@Getter
public enum TandilZoneBoundries {
    // Corner of España Ave. and Rivadavia St.
    NORTH_WEST(-37.325861008039006, -59.1471618263273),
    // Corner of Rivadavia St. and Avellaneda Ave.
    SOUTH_WEST(-37.33671805650914, -59.14068947308078),
    // Corner of Avellaneda Ave. and Santamarina Ave.
    SOUTH_EAST(-37.331387925127125, -59.12644205307121),
    // Corner of Santamarina Ave. and España Ave.
    NORTH_EAST(-37.32048860566324,  -59.13290919727019);

    private final double latitude;
    private final double longitude;

    TandilZoneBoundries(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static List<Coordinate> getPolygonVertices() {
        return Arrays.stream(values())
                .map(v -> new Coordinate(v.latitude, v.longitude))
                .toList();
    }
}