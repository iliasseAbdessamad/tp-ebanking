package com.ebanking.exceptions.account;

import lombok.Getter;

@Getter
public class InvalidAccountTypeException extends Exception {

    private String invalidAccountType;

    public InvalidAccountTypeException(String invalidAccountType){
        this.invalidAccountType = invalidAccountType;
    }
}
