package com.ebanking.repositories;

import com.ebanking.entities.AccountOperation;
import com.ebanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<AccountOperation, Long> {
}
