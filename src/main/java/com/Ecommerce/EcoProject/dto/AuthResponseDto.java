package com.Ecommerce.EcoProject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String tokenType="Bearer";

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;

    }
}
