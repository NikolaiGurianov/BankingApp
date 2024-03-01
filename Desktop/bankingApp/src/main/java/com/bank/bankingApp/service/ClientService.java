package com.bank.bankingApp.service;

import com.bank.bankingApp.dto.ClientRequestDto;
import com.bank.bankingApp.dto.ClientResponseDto;
import com.bank.bankingApp.dto.ContactInfoRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDate;
import java.util.List;

public interface ClientService extends UserDetailsService {
    ClientResponseDto addClient(ClientRequestDto newClient);

    ClientResponseDto updateContactInfo(Long id, ContactInfoRequest contactInfoRequest);

    List<ClientResponseDto> searchUser(LocalDate birthDay, String phoneNumber, String fullName, String email, int from, int size);

    ClientResponseDto transferMoney(Long senderId, Long receiverId, Double sum);

    UserDetails loadUserByUsername(String userName);
}