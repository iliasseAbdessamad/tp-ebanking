package com.ebanking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue(value = "CA")
@NoArgsConstructor @AllArgsConstructor @Data @ToString @SuperBuilder
public class CurrentAccount extends BankAccount {

    private double overDraft;
}
