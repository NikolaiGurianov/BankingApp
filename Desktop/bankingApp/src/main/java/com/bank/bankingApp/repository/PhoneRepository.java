package com.bank.bankingApp.repository;

import com.bank.bankingApp.entity.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PhoneRepository extends JpaRepository<PhoneNumber, Long> {
    List<PhoneNumber> findAllByOwner_id(long id);

    void deleteByNumber(String number);
}