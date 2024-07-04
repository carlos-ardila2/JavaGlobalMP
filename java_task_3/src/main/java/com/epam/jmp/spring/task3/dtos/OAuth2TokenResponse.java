package com.epam.jmp.spring.task3.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OAuth2TokenResponse {
    private String access_token;
    private String token_type;
    private long expires_in;
    private String scope;
}
