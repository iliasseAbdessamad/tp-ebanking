package com.ebanking.entities;


import com.ebanking.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) @DiscriminatorColumn(name = "type", length = 2)
@NoArgsConstructor @AllArgsConstructor @Data @ToString @SuperBuilder
public class BankAccount {

    @Id
    protected String id;

    @Column(nullable = false)
    protected Date createdAt;

    @Column(nullable = false)
    protected double balance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected AccountStatus accountStatus;

    @ManyToOne
    protected Customer customer;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    protected List<AccountOperation> accountOperations;
}
