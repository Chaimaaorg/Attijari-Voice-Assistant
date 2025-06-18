package com.attijari.bankingservices.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String message;



}
