package com.ebanking.entities;


import com.ebanking.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name = "type", length = 2)
@NoArgsConstructor @AllArgsConstructor @Data @ToString @Builder
public class BankAccount {

    @Id
    private String id;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private AccountStatus accountStatus;

    @Column(nullable = false) @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations;
}
