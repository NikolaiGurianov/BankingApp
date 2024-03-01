package com.bank.bankingApp.repository;

import com.bank.bankingApp.entity.Client;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String userName);

    @Query("SELECT c FROM Client c " +
            "WHERE (cast(:surname as string) IS NULL OR LOWER(c.surname) LIKE LOWER(CONCAT('%', cast(:surname as string), '%'))) " +
            "AND (cast(:name as string) IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', cast(:name as string), '%'))) " +
            "AND (cast(:patronymic as string) IS NULL OR LOWER(c.patronymic) LIKE LOWER(CONCAT('%', cast(:patronymic as string), '%'))) " +
            "AND (cast(:birthDay as LocalDate) IS NULL OR c.birthDay >= cast(:birthDay as LocalDate)) " +
            "AND (cast(:phoneNumber as string) IS NULL OR EXISTS (SELECT pn FROM PhoneNumber pn WHERE pn.owner = c AND pn.number = cast(:phoneNumber as string))) " +
            "AND (cast(:email as string) IS NULL OR EXISTS (SELECT e FROM Email e WHERE e.owner = c AND e.address = cast(:email as string)))")
    List<Client> findAllByParameters(String surname, String name, String patronymic, LocalDate birthDay, String phoneNumber,
                                     String email, Pageable pageable);
}