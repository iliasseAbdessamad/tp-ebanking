package com.ebanking.exceptions.operations;

import lombok.Getter;


@Getter
public class AmountCannotBeNegativeException extends Exception{

    private double invalidAmount;

    public AmountCannotBeNegativeException(double invalidAmount){
        super();
        this.invalidAmount = invalidAmount;
    }
}
