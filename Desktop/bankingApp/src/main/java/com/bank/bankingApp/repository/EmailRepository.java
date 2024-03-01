package com.bank.bankingApp.repository;

import com.bank.bankingApp.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findAllByOwner_id(long id);

    void deleteByAddress(String address);
}