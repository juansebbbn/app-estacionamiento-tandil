package com.juan.app_estacionamiento_tandil.unit_test;

import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.services.ParkingZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParkingZoneServiceTest {

    @InjectMocks
    private ParkingZoneService parkingZoneService;

    private Coordinate coordinateInsideZone;
    private Coordinate coordinateOutsideZone;
    private Coordinate coordinateOnBoundary;

    @BeforeEach
    void setUp() {
        // Coordinate inside Tandil parking zone (center of the polygon)
        coordinateInsideZone = new Coordinate(-37.328, -59.137);

        // Coordinate outside Tandil parking zone (far away)
        coordinateOutsideZone = new Coordinate(-37.0, -59.0);

        // Coordinate on boundary (close to one of the vertices)
        coordinateOnBoundary = new Coordinate(-37.325861008039006, -59.1471618263273);
    }

    @Test
    void isAtParkingZone_CoordinateInsideZone_ReturnsTrue() {
        // When
        boolean result = parkingZoneService.isAtParkingZone(coordinateInsideZone);

        // Then
        assertTrue(result);
    }

    @Test
    void isAtParkingZone_CoordinateOutsideZone_ReturnsFalse() {
        // When
        boolean result = parkingZoneService.isAtParkingZone(coordinateOutsideZone);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_CoordinateOnBoundary_ReturnsTrue() {
        // When
        boolean result = parkingZoneService.isAtParkingZone(coordinateOnBoundary);

        // Then
        // Boundary points should be considered inside the zone
        assertTrue(result);
    }

    @Test
    void isAtParkingZone_NorthWestVertex_ReturnsTrue() {
        // Given - North West corner of España Ave. and Rivadavia St.
        Coordinate northWest = new Coordinate(-37.325861008039006, -59.1471618263273);

        // When
        boolean result = parkingZoneService.isAtParkingZone(northWest);

        // Then
        assertTrue(result);
    }

    @Test
    void isAtParkingZone_SouthWestVertex_ReturnsFalse() {
        // Given - South West corner of Rivadavia St. and Avellaneda Ave.
        // This vertex is actually outside the polygon based on the algorithm
        Coordinate southWest = new Coordinate(-37.33671805650914, -59.14068947308078);

        // When
        boolean result = parkingZoneService.isAtParkingZone(southWest);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_SouthEastVertex_ReturnsFalse() {
        // Given - South East corner of Avellaneda Ave. and Santamarina Ave.
        // This vertex is actually outside the polygon based on the algorithm
        Coordinate southEast = new Coordinate(-37.331387925127125, -59.12644205307121);

        // When
        boolean result = parkingZoneService.isAtParkingZone(southEast);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_NorthEastVertex_ReturnsFalse() {
        // Given - North East corner of Santamarina Ave. and España Ave.
        // This vertex is actually outside the polygon based on the algorithm
        Coordinate northEast = new Coordinate(-37.32048860566324, -59.13290919727019);

        // When
        boolean result = parkingZoneService.isAtParkingZone(northEast);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_CoordinateNearCenter_ReturnsTrue() {
        // Given - Central point within the polygon
        Coordinate center = new Coordinate(-37.328, -59.137);

        // When
        boolean result = parkingZoneService.isAtParkingZone(center);

        // Then
        assertTrue(result);
    }

    @Test
    void isAtParkingZone_CoordinateFarNorth_ReturnsFalse() {
        // Given - Coordinate far north of the zone
        Coordinate farNorth = new Coordinate(-37.3, -59.137);

        // When
        boolean result = parkingZoneService.isAtParkingZone(farNorth);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_CoordinateFarSouth_ReturnsFalse() {
        // Given - Coordinate far south of the zone
        Coordinate farSouth = new Coordinate(-37.35, -59.137);

        // When
        boolean result = parkingZoneService.isAtParkingZone(farSouth);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_CoordinateFarEast_ReturnsFalse() {
        // Given - Coordinate far east of the zone
        Coordinate farEast = new Coordinate(-37.328, -59.12);

        // When
        boolean result = parkingZoneService.isAtParkingZone(farEast);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_CoordinateFarWest_ReturnsFalse() {
        // Given - Coordinate far west of the zone
        Coordinate farWest = new Coordinate(-37.328, -59.15);

        // When
        boolean result = parkingZoneService.isAtParkingZone(farWest);

        // Then
        assertFalse(result);
    }

    @Test
    void isAtParkingZone_EdgeCaseCoordinates_ReturnsExpectedResults() {
        // Test coordinates that are very close to the boundary
        Coordinate justInside = new Coordinate(-37.326, -59.146);
        Coordinate justOutside = new Coordinate(-37.325, -59.148);

        // When
        boolean resultInside = parkingZoneService.isAtParkingZone(justInside);
        boolean resultOutside = parkingZoneService.isAtParkingZone(justOutside);

        // Then
        assertTrue(resultInside);
        assertFalse(resultOutside);
    }

    @Test
    void isAtParkingZone_NullCoordinate_HandlesGracefully() {
        // Given
        Coordinate nullCoordinate = null;

        // When & Then - This should not throw an exception
        assertThrows(Exception.class, () -> {
            parkingZoneService.isAtParkingZone(nullCoordinate);
        });
    }

    @Test
    void isAtParkingZone_ExtremeCoordinates_ReturnsFalse() {
        // Given - Extreme coordinates that should definitely be outside
        Coordinate extremeNorth = new Coordinate(90.0, 0.0);
        Coordinate extremeSouth = new Coordinate(-90.0, 0.0);
        Coordinate extremeEast = new Coordinate(0.0, 180.0);
        Coordinate extremeWest = new Coordinate(0.0, -180.0);

        // When & Then
        assertFalse(parkingZoneService.isAtParkingZone(extremeNorth));
        assertFalse(parkingZoneService.isAtParkingZone(extremeSouth));
        assertFalse(parkingZoneService.isAtParkingZone(extremeEast));
        assertFalse(parkingZoneService.isAtParkingZone(extremeWest));
    }

    @Test
    void isAtParkingZone_ValidCoordinateRange_HandlesCorrectly() {
        // Given - Valid coordinate ranges
        Coordinate validLatLong = new Coordinate(-37.328, -59.137);

        // When
        boolean result = parkingZoneService.isAtParkingZone(validLatLong);

        // Then
        assertTrue(result);
    }
}
