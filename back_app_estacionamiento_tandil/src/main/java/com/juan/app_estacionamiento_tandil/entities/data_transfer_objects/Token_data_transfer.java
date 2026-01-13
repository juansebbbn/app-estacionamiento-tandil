package com.juan.app_estacionamiento_tandil.entities.data_transfer_objects;

import lombok.Builder;

import java.util.List;

@Builder
public record Token_data_transfer(
        String accessToken,
        String refreshToken,
        String username,
        List<String> roles
) {}