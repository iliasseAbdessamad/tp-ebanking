package com.ebanking.enums;


import lombok.Getter;

@Getter
public enum AccountType {
    CURRENT_ACCOUNT("CA"),
    SAVING_ACCOUNT("SA");

    private AccountType(String accountType){
        this.accountType = accountType;
    }
    private String accountType;
}
