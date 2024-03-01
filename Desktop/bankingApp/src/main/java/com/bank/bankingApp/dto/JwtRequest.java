package com.bank.bankingApp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JwtRequest {
    @NotEmpty
    private String login;
    @NotEmpty
    private String password;
}