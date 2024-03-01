package com.bank.bankingApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Schema(description = "DTO ответа клиента")
public class ClientResponseDto {
    @Schema(description = "Уникальный идентификатор клиента")
    private Long id;

    @Schema(description = "Фамилия клиента")
    private String surname;

    @Schema(description = "Имя клиента")
    private String name;

    @Schema(description = "Отчество клиента")
    private String patronymic;

    @Schema(description = "Дата рождения клиента")
    private LocalDate birthDay;

    @Schema(description = "Список телефонных номеров клиента")
    private List<String> phoneNumbers;

    @Schema(description = "Список электронных адресов клиента")
    private List<String> emails;

    @Schema(description = "Баланс клиента")
    private Double balance;

    @Schema(description = "Логин клиента")
    private String login;
}
