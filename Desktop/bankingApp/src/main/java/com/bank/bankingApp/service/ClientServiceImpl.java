package com.bank.bankingApp.service;

import com.bank.bankingApp.dto.*;
import com.bank.bankingApp.exception.ConflictException;
import com.bank.bankingApp.exception.NotFoundException;
import com.bank.bankingApp.exception.ValidException;
import com.bank.bankingApp.entity.Email;
import com.bank.bankingApp.entity.PhoneNumber;
import com.bank.bankingApp.repository.ClientRepository;
import com.bank.bankingApp.repository.EmailRepository;
import com.bank.bankingApp.repository.PhoneRepository;
import com.bank.bankingApp.entity.Client;
import com.bank.bankingApp.util.ContactInfoUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bank.bankingApp.util.Constant.SORT_BY_ID_ASC;

@Data
@Service
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class ClientServiceImpl implements ClientService {
    private final ClientMapper clientMapper;
    private final ClientRepository clientRepository;
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;
    private final ContactInfoUtils contactInfoUtils;

    @Override
    public ClientResponseDto addClient(ClientRequestDto newClient) {
        log.info("Запрос на добавление нового пользователя {}", newClient);

        if (newClient == null) throw new ValidException("Нет данных для создания пользователя");
        if (!newClient.getPassword().equals(newClient.getConfirmPassword()))
            throw new ValidException("Пароли не совпадают");
        if (clientRepository.findByLogin(newClient.getLogin()).isPresent())
            throw new ValidException("Пользователь с таким логином уже существует");

        Client client = clientMapper.toClient(newClient);
        try {
            clientRepository.save(client);
            phoneRepository.save(PhoneNumber.builder().number(newClient.getPhoneNumber()).owner(client).build());
            emailRepository.save(Email.builder().address(newClient.getEmail()).owner(client).build());
            log.info("Сохранение нового пользователя прошло успешно");
        } catch (Exception e) {
            log.debug("проблема с созданием пользователя", e);
            throw new ConflictException("Проблема с данными, такие данные уже есть в базе");
        }
        return clientMapper.toResponseDto(client);
    }

    @Transactional
    @Override
    public ClientResponseDto updateContactInfo(Long id, ContactInfoRequest contactInfoRequest) {
        log.info("Запрос на обновление контактных данных для пользователя с ID={}, с параметрами: {}",
                id, contactInfoRequest);

        Client client = clientRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Произошла ошибка! Пользователь не найден"));

        switch (contactInfoRequest.getStatus()) {
            case ADD ->
                    contactInfoUtils.addContactInfo(client, contactInfoRequest.getPhone(), contactInfoRequest.getEmail());
            case REPLACE -> contactInfoUtils.replaceContactInfo(client, contactInfoRequest.getChangePhone(),
                    contactInfoRequest.getPhone(), contactInfoRequest.getChangeEmail(), contactInfoRequest.getEmail());
            case DELETE ->
                    contactInfoUtils.removeContactInfo(client, contactInfoRequest.getPhone(), contactInfoRequest.getEmail());
            default -> throw new IllegalArgumentException("Некорректный статус");
        }

        ClientResponseDto responseClient = clientMapper.toResponseDto(client);

        log.info("Запрос выполнен. Актуальная информация о пользователе: {}", responseClient);

        return responseClient;
    }

    @Override
    public List<ClientResponseDto> searchUser(LocalDate birthDay, String phoneNumber, String fullName, String email,
                                              int from, int size) {
        log.info("Запрос на получение событий с параметрами: birthDay={}, phoneNumber={}, fullName={}, email={},  from={}, size={}",
                birthDay, phoneNumber, fullName, email, from, size);

        String[] parts = (fullName != null) ? fullName.split("\\s+") : new String[0];
        String surname = (parts.length > 0) ? parts[0] : null;
        String name = (parts.length > 1) ? parts[1] : null;
        String patronymic = (parts.length > 2) ? parts[2] : null;

        List<Client> clientList = clientRepository.findAllByParameters(surname, name, patronymic, birthDay,
                phoneNumber, email, PageRequest.of(from / size, size, SORT_BY_ID_ASC));

        List<ClientResponseDto> clientResponseDtoList = clientList.stream()
                .map(clientMapper::toResponseDto)
                .collect(Collectors.toList());

        log.info("Запрос выполнен. Получено {} пользователей", clientResponseDtoList.size());

        return clientResponseDtoList;
    }

    @Override
    @Transactional
    public ClientResponseDto transferMoney(Long senderId, Long receiverId, Double sum) {
        log.info("Запрос на перевод денежных средств от отправителя с ID={} пользователю с ID={}, на сумму {} рублей.",
                senderId, receiverId, sum);

        if (sum <= 0) {
            throw new ValidException("Сумма перевода должна быть положительной");
        }
        Client sender = clientRepository.findById(senderId).orElseThrow(() -> new NotFoundException("Отправитель не найден"));
        Client receiver = clientRepository.findById(receiverId).orElseThrow(() -> new NotFoundException("Получатель не найден"));

        if (sender.getId().equals(receiver.getId())) {
            throw new ValidException("Отправитель и получатель не могут быть одним и тем же пользователем");
        }

        double senderBalance = sender.getBalance() - sum;
        if (senderBalance > 0) {
            sender.setBalance(senderBalance);
            receiver.setBalance(receiver.getBalance() + sum);
            sender = clientRepository.save(sender);
            clientRepository.save(receiver);

            log.info("Запрос выполнен. Текущий баланс отправителя {} рублей", sender.getBalance());
            log.info("Текущий баланс получателя {} рублей", receiver.getBalance());
            return clientMapper.toResponseDto(sender);

        } else {
            throw new ConflictException("Недостаточно средств на счете отправителя");
        }
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Client client = clientRepository.findByLogin(userName).orElseThrow(()
                -> new NotFoundException("Пользователь {} не найден", userName));
        return new User(client.getLogin(), client.getPassword(), Collections.emptyList());
    }

    @Scheduled(initialDelay = 1, fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void increaseBalances() {
        List<Client> clients = clientRepository.findAll();
        for (Client client : clients) {
            double currentBalance = client.getBalance();
            double initialDeposit = client.getStartDeposit();
            if (currentBalance > 0) {
                double increaseAmount = currentBalance * 0.05;
                if (currentBalance + increaseAmount <= initialDeposit * 2.07) {
                    client.setBalance(currentBalance + increaseAmount);
                    clientRepository.save(client);
                    log.info("Баланс клиента {} увеличен", client);
                } else log.debug("Лимит денежного вознаграждения достигнут пользователя {}", client);
            }
        }
        log.info("Балансы пользователей успешно увеличены");
    }
}