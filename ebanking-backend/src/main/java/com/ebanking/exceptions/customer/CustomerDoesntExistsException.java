package com.ebanking.exceptions.customer;


import lombok.Getter;

@Getter
public class CustomerDoesntExistsException extends Exception{
    private Long invalidCustomerId;

    public CustomerDoesntExistsException(Long invalidCustomerId){
        super();
        this.invalidCustomerId = invalidCustomerId;
    }
}
