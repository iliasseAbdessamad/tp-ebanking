package com.ebanking.dtos.bankaccount.request;

import lombok.*;
import lombok.experimental.SuperBuilder;



public abstract class AbstractOperationRequestDTO {
    protected double amount;
    protected String description;

    public double getAmount(){
        return this.amount;
    }
    public void setAmount(double amount){
        this.amount = amount;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

}
