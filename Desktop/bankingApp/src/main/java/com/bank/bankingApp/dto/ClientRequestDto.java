package com.bank.bankingApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@Schema(description = "DTO для создания нового клиента")
public class ClientRequestDto {
    @NotNull
    @Schema(description = "Фамилия клиента")
    private String surname;

    @NotNull
    @Schema(description = "Имя клиента")
    private String name;

    @NotNull
    @Schema(description = "Отчество клиента")
    private String patronymic;

    @NotNull
    @Schema(description = "Дата рождения клиента")
    private LocalDate birthDay;

    @NotNull
    @Size(min = 10, max = 10)
    @Schema(description = "Номер телефона клиента", minLength = 10, maxLength = 10)
    private String phoneNumber;

    @NotNull
    @Email
    @Schema(description = "Email клиента")
    private String email;

    @NotNull
    @Positive
    @Schema(description = "Начальный депозит клиента", minimum = "0.0")
    private Double startDeposit;

    @NotNull
    @Schema(description = "Логин клиента")
    private String login;

    @NotNull
    @Schema(description = "Пароль клиента")
    private String password;

    @NotNull
    @Schema(description = "Проверка пароля клиента")
    private String confirmPassword;

}