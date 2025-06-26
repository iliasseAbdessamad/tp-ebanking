package com.ebanking.repositories;

import com.ebanking.entities.AccountOperation;
import com.ebanking.entities.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    /*
        Permet de gerer le verrou au niveau de la base de données,
        donc même si on a plusieurs instances de l'appalication
        (multi-serveurs), la concurrence est correctement gérée.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ba FROM BankAccount ba WHERE ba.id = :id")
    Optional<BankAccount> findByIdWithLockForUpdate(@Param("id") String id);
}
