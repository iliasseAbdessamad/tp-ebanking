package com.ebanking.exceptions.account;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends Exception{

    String invalidIdAccount;

    public AccountNotFoundException(String invalidIdAccount){
        super();
        this.invalidIdAccount = invalidIdAccount;
    }
}
