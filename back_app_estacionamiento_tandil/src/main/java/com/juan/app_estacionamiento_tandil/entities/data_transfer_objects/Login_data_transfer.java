package com.juan.app_estacionamiento_tandil.entities.data_transfer_objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Login_data_transfer {
    private String username;
    private String password;
}
