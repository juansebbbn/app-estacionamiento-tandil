package com.juan.app_estacionamiento_tandil.exceptions;

public class VehicleOperationException extends RuntimeException {
    public VehicleOperationException(String message) {
        super(message);
    }

    public VehicleOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
