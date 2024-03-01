package com.bank.bankingApp.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Сведения об ошибке")
public record ApiError(@Schema(description = "Список стектрейсов или описания ошибок") List<String> errors,
                       @Schema(description = "Сообщение об ошибке") String message,
                       @Schema(description = "Общее описание причины ошибки") String reason,
                       @Schema(description = "Код статуса HTTP-ответа") HttpStatus status,
                       @Schema(description = "Дата и время когда произошла ошибка (в формате \"yyyy-MM-dd HH:mm:ss\")") LocalDateTime timestamp) {
}