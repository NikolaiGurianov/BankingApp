package com.bank.bankingApp.service;

import com.bank.bankingApp.dto.JwtRequest;
import com.bank.bankingApp.dto.JwtResponse;
import com.bank.bankingApp.exception.UnauthorizedException;
import com.bank.bankingApp.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final JwtTokenUtils jwtTokenUtils;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final ClientService clientService;

    public JwtResponse createAuthToken(JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Ошибка аутентификации");
        }
        UserDetails userDetails = clientService.loadUserByUsername(authRequest.getLogin());
        String token = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponse(token);
    }
}