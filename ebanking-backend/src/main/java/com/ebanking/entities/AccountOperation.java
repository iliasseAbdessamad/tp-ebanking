package com.ebanking.entities;

import com.ebanking.enums.OperationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor @AllArgsConstructor @Data @ToString @Builder
public class AccountOperation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private double amount;

    private String description;

    @Column(nullable = false)
    private OperationType operationType;

    @Column(nullable = false) @ManyToOne
    private BankAccount bankAccount;
}
