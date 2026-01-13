package com.juan.app_estacionamiento_tandil.entities.data_transfer_objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Register_data_transfer {
    @NotBlank
    @Size(min = 8)
    private String username;
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotBlank
    private String dni;
}
