package com.ebanking.exceptions.account;

import lombok.Getter;

@Getter
public class ExistingAccountCannotBeCreatedTwiceException extends Exception{

    public ExistingAccountCannotBeCreatedTwiceException(){
        super();
    }
}
