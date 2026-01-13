package com.juan.app_estacionamiento_tandil.entities.data_transfer_objects;

import com.juan.app_estacionamiento_tandil.entities.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Setter
public class Vehicle_data_transfer {
    @NotBlank
    private String patent;
    @NotBlank
    private VehicleType type;
}
