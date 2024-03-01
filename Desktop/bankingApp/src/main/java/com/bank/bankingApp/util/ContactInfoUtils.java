package com.bank.bankingApp.util;

import com.bank.bankingApp.entity.Client;
import com.bank.bankingApp.entity.Email;
import com.bank.bankingApp.entity.PhoneNumber;
import com.bank.bankingApp.exception.ConflictException;
import com.bank.bankingApp.repository.EmailRepository;
import com.bank.bankingApp.repository.PhoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactInfoUtils {
    private final PhoneRepository phoneRepository;
    private final EmailRepository emailRepository;

    public void addContactInfo(Client client, String phoneNumber, String emailAddress) {
        log.info("Запрос на добавление контактных данных для пользователя {}, с параметрами: phoneNumber={}, emailAddress={}",
                client, phoneNumber, emailAddress);

        if (phoneNumber != null) {
            List<PhoneNumber> phoneNumberList = phoneRepository.findAll();
            phoneNumberList.stream().filter(number -> number.getNumber().equals(phoneNumber)).forEach(number -> {
                throw new ConflictException("Добавление телефонного номера не возможно! Такой номер есть уже есть в базе данных");
            });
            PhoneNumber phone = PhoneNumber.builder()
                    .owner(client)
                    .number(phoneNumber)
                    .build();
            phoneRepository.save(phone);
            log.info("Телефон пользователя с ID={} добавлен", client.getId());
        }

        if (emailAddress != null) {
            List<Email> emailList = emailRepository.findAll();
            emailList.stream().filter(email -> email.getAddress().equals(emailAddress)).forEach(email -> {
                throw new ConflictException("Добавление эл почты не возможно! Такой адрес есть уже есть в базе данных");
            });
            Email email1 = Email.builder()
                    .owner(client)
                    .address(emailAddress)
                    .build();
            emailRepository.save(email1);
            log.info("Эл почта пользователя с ID={} добавлена", client.getId());
        }
    }

    public void replaceContactInfo(Client client, String changeNumber, String phoneNumber, String changeEmail, String emailAddress) {
        log.info("Запрос на изменение контактных данных для пользователя {}, с параметрами: phoneNumber={}, emailAddress={}",
                client, phoneNumber, emailAddress);

        if (phoneNumber != null) {
            List<PhoneNumber> phoneNumberList = phoneRepository.findAll();
            phoneNumberList.stream().filter(number -> number.getNumber().equals(phoneNumber)).forEach(number -> {
                throw new ConflictException("Замена телефонного номера не возможна! Такой номер есть уже есть в базе данных");
            });
            PhoneNumber phone = PhoneNumber.builder()
                    .owner(client)
                    .number(phoneNumber)
                    .build();
            phoneRepository.deleteByNumber(changeNumber);
            phoneRepository.save(phone);
            log.info("Телефон пользователя с ID={} обновлен", client.getId());
        }

        if (emailAddress != null) {
            List<Email> emailList = emailRepository.findAll();
            emailList.stream().filter(email -> email.getAddress().equals(emailAddress)).forEach(email -> {
                throw new ConflictException("Замена эл почты не возможна! Такой адрес есть уже есть в базе данных");
            });
            Email email1 = Email.builder()
                    .owner(client)
                    .address(emailAddress)
                    .build();
            emailRepository.deleteByAddress(changeEmail);
            emailRepository.save(email1);
            log.info("Эл почта пользователя с ID={} обновлена", client.getId());
        }
    }


    public void removeContactInfo(Client client, String phoneNumber, String emailAddress) {
        log.info("Запрос на удаление контактных данных для пользователя {}, с параметрами: phoneNumber={}, emailAddress={}",
                client, phoneNumber, emailAddress);

        if (phoneNumber != null) {
            List<PhoneNumber> phoneNumberList = phoneRepository.findAllByOwner_id(client.getId());
            if (phoneNumberList.size() > 1) {
                for (PhoneNumber number : phoneNumberList) {
                    log.info("Number from database: {}", number.getNumber());
                    log.info("Phone number to delete: {}", phoneNumber);
                    if (number.getNumber().equals(phoneNumber)) {
                        phoneRepository.delete(number);
                        log.info("Phone number deleted for client ID={}", client.getId());
                    }
                }
                log.info("Телефон пользователя с ID={} удален", client.getId());

            } else {
                throw new ConflictException("Удаление телефонного номера, невозможно. В данных пользователя должен быть минимум один номер телефона");
            }
        }

        if (emailAddress != null) {
            List<Email> emailList = emailRepository.findAllByOwner_id(client.getId());
            if (emailList.size() > 1) {
                for (Email email : emailList) {
                    if (email.getAddress().equals(emailAddress)) {
                        emailRepository.delete(email);
                    }
                }
                log.info("Эл почта пользователя с ID={} удалена", client.getId());
            } else {
                throw new ConflictException("Удаление электронной почты, невозможно. В данных пользователя должен быть минимум один адрес");
            }
        }
    }
}
