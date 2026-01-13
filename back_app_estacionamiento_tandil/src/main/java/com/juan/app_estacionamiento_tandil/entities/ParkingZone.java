package com.juan.app_estacionamiento_tandil.entities;

import com.juan.app_estacionamiento_tandil.entities.data_transfer_objects.Coordinate;
import com.juan.app_estacionamiento_tandil.entities.enums.TandilZoneBoundries;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class ParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    private List<Coordinate> vertices;

    public ParkingZone() {}

    public ParkingZone(String name) {
        this.name = name;
    }

    public static boolean isUserInParkingZone(double userLat, double userLng) {
        List<Coordinate> vertices = TandilZoneBoundries.getPolygonVertices();
        int nvert = vertices.size();
        boolean isInside = false;


        System.out.println("user lat: " + userLat);

        for (int i = 0, j = nvert - 1; i < nvert; j = i++) {

            if (((vertices.get(i).getLatitude() > userLat) != (vertices.get(j).getLatitude() > userLat))) {

                System.out.println("entre " + vertices.get(i).getLatitude() + " " + vertices.get(j).getLatitude());

                double intersectLng = (vertices.get(j).getLongitude() - vertices.get(i).getLongitude()) * (userLat - vertices.get(i).getLatitude()) /
                        (vertices.get(j).getLatitude() - vertices.get(i).getLatitude()) +
                        vertices.get(i).getLongitude();


                if (userLng < intersectLng) {
                    isInside = !isInside;
                }
            }
        }
        return isInside;
    }
}