package com.ebanking.repositories;


import com.ebanking.entities.Customer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /*
        Permet de gerer le verrou au niveau de la base de données,
        donc même si on a plusieurs instances de l'appalication
        (multi-serveurs), la concurrence est correctement gérée.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Optional<Customer> findByIdWithLockForUpdate(@Param("id") String id);

    Optional<Customer> findByEmail(String email);
}
