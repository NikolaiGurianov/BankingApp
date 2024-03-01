package com.bank.bankingApp.service;

import com.bank.bankingApp.dto.ClientMapper;
import com.bank.bankingApp.dto.ClientResponseDto;
import com.bank.bankingApp.entity.Client;
import com.bank.bankingApp.entity.Email;
import com.bank.bankingApp.entity.PhoneNumber;
import com.bank.bankingApp.exception.ConflictException;
import com.bank.bankingApp.exception.NotFoundException;
import com.bank.bankingApp.exception.ValidException;
import com.bank.bankingApp.repository.ClientRepository;
import com.bank.bankingApp.repository.EmailRepository;
import com.bank.bankingApp.repository.PhoneRepository;
import com.bank.bankingApp.util.ContactInfoUtils;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientMapper clientMapper;

    @Mock
    private PhoneRepository phoneNumberRepository;

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private ContactInfoUtils contactInfoUtils;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientServiceImpl(clientMapper, clientRepository, phoneNumberRepository, emailRepository, contactInfoUtils);
        clientMapper = new ClientMapper(phoneNumberRepository, emailRepository, passwordEncoder);
    }

    @Test
    public void testTransferMoney_SuccessfulTransfer() {

        Client sender = Client.builder()
                .id(1L)
                .surname("Иванов")
                .name("Иван")
                .patronymic("Иванович")
                .birthDay(LocalDate.of(1990, 5, 15))
                .balance(1000.0)
                .startDeposit(500.0)
                .login("ivanov")
                .password("password123")
                .build();

        Client receiver = Client.builder()
                .id(2L)
                .surname("Петров")
                .name("Петр")
                .patronymic("Петрович")
                .birthDay(LocalDate.of(1985, 8, 22))
                .balance(1500.0)
                .startDeposit(700.0)
                .login("petrov")
                .password("securepass")
                .build();
        double amount = 145.50;
        PhoneNumber senderPhone = new PhoneNumber();
        senderPhone.setNumber("1234567890");
        senderPhone.setOwner(sender);

        PhoneNumber receiverPhone = new PhoneNumber();
        receiverPhone.setNumber("9876543210");
        receiverPhone.setOwner(receiver);

        Email senderEmail = new Email();
        senderEmail.setAddress("ivanov@example.com");
        senderEmail.setOwner(sender);

        Email receiverEmail = new Email();
        receiverEmail.setAddress("petrov@example.com");
        receiverEmail.setOwner(receiver);

        Mockito.when(clientRepository.findById(1L)).thenReturn(Optional.of(sender));
        Mockito.when(clientRepository.findById(2L)).thenReturn(Optional.of(receiver));
        Mockito.when(clientRepository.save(Mockito.any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponseDto responseDto = clientService.transferMoney(sender.getId(), receiver.getId(), amount);

        assertEquals(sender.getId(), responseDto.getId());
        assertEquals(854.5, responseDto.getBalance());
        assertEquals(1000.0 - amount, sender.getBalance());
        assertEquals(1500.0 + amount, receiver.getBalance());

        Mockito.verify(clientRepository, Mockito.times(2)).save(Mockito.any(Client.class));
    }

    @Test
    public void testTransferMoney_SenderNotFound() {
        Long senderId = 1L;
        Long receiverId = 2L;
        Double amount = 50.0;

        Mockito.when(clientRepository.findById(senderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.transferMoney(senderId, receiverId, amount));
    }

    @Test
    public void testTransferMoney_ReceiverNotFound() {
        Long senderId = 1L;
        Long receiverId = 2L;
        Double amount = 50.0;

        Client sender = new Client();
        sender.setId(senderId);

        Mockito.when(clientRepository.findById(senderId)).thenReturn(Optional.of(sender));
        Mockito.when(clientRepository.findById(receiverId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientService.transferMoney(senderId, receiverId, amount));
    }

    @Test
    public void testTransferMoney_InvalidAmount() {
        Long senderId = 1L;
        Long receiverId = 2L;
        Double amount = -50.0;

        assertThrows(ValidException.class, () -> clientService.transferMoney(senderId, receiverId, amount));
    }

    @Test
    public void testTransferMoney_InsufficientBalance() {
        Long senderId = 1L;
        Long receiverId = 2L;
        Double amount = 150.0;

        Client sender = new Client();
        sender.setId(senderId);
        sender.setBalance(100.0);

        Mockito.when(clientRepository.findById(senderId)).thenReturn(Optional.of(sender));
        Mockito.when(clientRepository.findById(receiverId)).thenReturn(Optional.of(new Client()));

        assertThrows(ConflictException.class, () -> clientService.transferMoney(senderId, receiverId, amount));
    }
}