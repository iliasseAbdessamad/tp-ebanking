package com.ebanking.exceptions.account;

import lombok.Getter;

@Getter
public class AccountStillFundedException extends Exception {
    private double availableBalance;

    public AccountStillFundedException(double availableBalance){
        this.availableBalance = availableBalance;
    }
}
