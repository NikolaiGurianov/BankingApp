package com.bank.bankingApp.controller;

import com.bank.bankingApp.dto.*;
import com.bank.bankingApp.service.AuthService;
import com.bank.bankingApp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Banking Application", description = "API для управления банковских операций клиентов")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Операция выполнена"),
        @ApiResponse(responseCode = "201", description = "Пользователь создан"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
        @ApiResponse(responseCode = "404", description = "Ошибка при поиске данных"),
        @ApiResponse(responseCode = "409", description = "Ошибка с данными при создании пользователя")
})
public class ClientController {
    private final ClientService clientService;
    private final AuthService authService;

    @Operation(summary = "Создание нового пользователя",
            description = "У пользователя также есть телефон и email. Телефон и или email должен быть минимум один." +
                    " На “банковском счету” должна быть какая-то изначальная сумма." +
                    " Также у пользователя должна быть указана дата рождения и ФИО.")
    @PostMapping("/registration")
    public ResponseEntity<ClientResponseDto> addNewClient(@RequestBody @Valid ClientRequestDto newClient) {
        log.debug("Получен запрос на добавление нового клиента" + newClient.toString());
        return new ResponseEntity<>(clientService.addClient(newClient), HttpStatus.CREATED);
    }

    @Operation(summary = "Создание токена пользователя",
            description = "Процесс авторизации пользователя")
    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> createAuthenticatedToken(@RequestBody @Valid JwtRequest request) {
        log.debug("Получен запрос на создание токена");
        return new ResponseEntity<>(authService.createAuthToken(request), HttpStatus.CREATED);
    }


    @Operation(summary = "Перевод денежных средств",
            description = "Выполнение перевода со счета аутентифицированного пользователя, на счёт другого пользователя.")
    @PostMapping("/transfer/{userId}")
    public ResponseEntity<ClientResponseDto> transferMoney(@PathVariable Long userId,
                                                           @RequestBody @Valid TransferRequestDto transferRequestDto) {

        log.debug("Получен запрос на перевод денежных средств");
        return new ResponseEntity<>(clientService.transferMoney(userId, transferRequestDto.getReceiverId(), transferRequestDto.getSum()), HttpStatus.OK);
    }


    @Operation(summary = "Удаление контактной информации",
            description = "Удаление телефона/эл.почты, если они у него не единственные")
    @DeleteMapping("/delete/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeClientContactInformation(@PathVariable Long userId,
                                               @RequestBody @Valid ContactInfoRequest contactInfoRequest) {
        log.debug("Получен запрос на удаление контактных данных");

        clientService.updateContactInfo(userId, contactInfoRequest);
    }

    @Operation(summary = "Добавление контактной информации",
            description = "Пользователь может добавить свои номер телефона и/или email," +
                    " если они еще не заняты другими пользователями.")
    @PostMapping("/add/{userId}")
    public ResponseEntity<ClientResponseDto> addClientContactInformation(@PathVariable Long userId,
                                                                         @RequestBody @Valid ContactInfoRequest contactInfoRequest) {
        log.debug("Получен запрос на добавление контактных данных");

        return new ResponseEntity<>(clientService.updateContactInfo(userId, contactInfoRequest),
                HttpStatus.OK);
    }

    @Operation(summary = "Изменение контактной информации",
            description = "Пользователь может сменить свои номер телефона и/или email," +
                    " если они еще не заняты другими пользователями.")
    @PatchMapping("/update/{userId}")
    public ResponseEntity<ClientResponseDto> updateClientContactInformation(@PathVariable Long userId,
                                                                            @RequestBody @Valid ContactInfoRequest contactInfoRequest) {
        log.debug("Получен запрос на смену контактных данных");

        return new ResponseEntity<>(clientService.updateContactInfo(userId, contactInfoRequest),
                HttpStatus.OK);
    }

    @Operation(summary = "Поиск пользователей ",
            description = "Поиск выполняется по параметрам или без них, с применением сортировки")
    @GetMapping("/search")
    public ResponseEntity<List<ClientResponseDto>> getClients(@RequestParam(required = false) LocalDate birthDay,
                                                              @RequestParam(required = false) String phoneNumber,
                                                              @RequestParam(required = false) String fullName,
                                                              @RequestParam(required = false) String email,
                                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Получен запрос на поиск клиентов");

        return new ResponseEntity<>(
                clientService.searchUser(birthDay, phoneNumber, fullName, email, from, size), HttpStatus.OK);
    }


}
