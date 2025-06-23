package com.ebanking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(value = "CA")
public class CurrentAccount extends BankAccount {

    private double overDraft;
}
