package com.ebanking.exceptions.account;

public class InvalidAccountStatusException extends Exception{
    private String invalidStatus;

    public InvalidAccountStatusException(String invalidStatus){
        super();
        this.invalidStatus = invalidStatus;
    }
}
