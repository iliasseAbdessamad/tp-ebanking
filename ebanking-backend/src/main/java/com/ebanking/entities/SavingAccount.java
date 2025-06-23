package com.ebanking.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SA")
@NoArgsConstructor @AllArgsConstructor @Data @ToString @SuperBuilder
public class SavingAccount extends BankAccount {

    private double interestRate;
}
