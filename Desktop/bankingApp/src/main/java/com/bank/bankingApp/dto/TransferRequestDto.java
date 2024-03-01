package com.bank.bankingApp.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(description = "DTO для перевода денежных средств")
public class TransferRequestDto {
    @NotNull
    @Schema(description = "Идентификатор получателя")
    private Long receiverId;

    @NotNull
    @Positive
    @Schema(description = "Сумма перевода")
    private Double sum;
}