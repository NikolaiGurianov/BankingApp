package com.bank.bankingApp.dto;

import com.bank.bankingApp.entity.Email;
import com.bank.bankingApp.entity.PhoneNumber;
import com.bank.bankingApp.repository.EmailRepository;
import com.bank.bankingApp.repository.PhoneRepository;
import com.bank.bankingApp.entity.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ClientMapper {
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final PasswordEncoder passwordEncoder;


    public Client toClient(ClientRequestDto request) {
        return Client.builder()
                .surname(request.getSurname())
                .name(request.getName())
                .patronymic(request.getPatronymic())
                .birthDay(request.getBirthDay())
                .balance(request.getStartDeposit())
                .startDeposit(request.getStartDeposit())
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    public ClientResponseDto toResponseDto(Client client) {
        ClientResponseDto responseDto = ClientResponseDto.builder()
                .id(client.getId())
                .surname(client.getSurname())
                .name(client.getName())
                .patronymic(client.getPatronymic())
                .birthDay(client.getBirthDay())
                .balance(client.getBalance())
                .login(client.getLogin())
                .build();

        List<PhoneNumber> phoneNumberList = phoneRepository.findAllByOwner_id(client.getId());
        responseDto.setPhoneNumbers(phoneNumberList.stream()
                .map(PhoneNumber::getNumber)
                .collect(Collectors.toList()));

        List<Email> emailList = emailRepository.findAllByOwner_id(client.getId());
        responseDto.setEmails(emailList.stream()
                .map(Email::getAddress)
                .collect(Collectors.toList()));

        return responseDto;
    }
}