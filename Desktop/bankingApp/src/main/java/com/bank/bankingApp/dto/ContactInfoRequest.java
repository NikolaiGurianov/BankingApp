package com.bank.bankingApp.dto;

import com.bank.bankingApp.util.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import lombok.Getter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "Контактные данные для удаления/добавления/изменения")
public class ContactInfoRequest {
    @Nullable
    @Size(min = 10, max = 10)
    @Schema(description = "Телефонный номер для добавления или удаления", nullable = true)
    private String phone;

    @Nullable
    @Size(min = 10, max = 10)
    @Schema(description = "Изменяемый телефонный номер", nullable = true)
    private String changePhone;

    @Nullable
    @Email
    @Schema(description = "Адрес электронной почты для добавления или удаления", nullable = true)
    private String email;

    @Nullable
    @Email
    @Schema(description = "Изменяемый адрес электронной почты", nullable = true)
    private String changeEmail;

    @NotNull
    @Schema(description = "Статус операции: ADD (добавление), REPLACE (замена), DELETE (удаление)")
    private Status status;
}


