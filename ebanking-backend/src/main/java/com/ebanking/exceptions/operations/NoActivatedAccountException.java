package com.ebanking.exceptions.operations;

import lombok.Getter;

@Getter
public class NoActivatedAccountException extends Exception {

    private String accountStatus;
    public NoActivatedAccountException(String accountStatus) {
        super();

        this.accountStatus = accountStatus;
    }
}
