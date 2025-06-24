package com.ebanking.exceptions.customer;

import lombok.Getter;

@Getter
public class EmailAlreadyExistsException extends  Exception {
    private String email;

    public EmailAlreadyExistsException(String email){
        super();
        this.email = email;
    }
}
