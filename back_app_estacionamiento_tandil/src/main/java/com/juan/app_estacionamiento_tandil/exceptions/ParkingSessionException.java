package com.juan.app_estacionamiento_tandil.exceptions;

public class ParkingSessionException extends RuntimeException {
    public ParkingSessionException(String message) {
        super(message);
    }

    public ParkingSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
