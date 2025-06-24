package com.ebanking.enums;

import com.ebanking.exceptions.account.InvalidAccountStatusException;
import lombok.Getter;


public enum AccountStatus {
    CREATED("CREATE"),
    ACTIVATED("ACTIVATE"),
    DEACTIVATED("DEACTIVATE"),
    SUSPENDED("SUSPEND");

    private String label;

    AccountStatus(String label){
        this.label = label;
    }

    public static AccountStatus formLabel(String lbl) throws InvalidAccountStatusException{
        for(AccountStatus status : values()){
            if(status.label.equalsIgnoreCase(lbl)){
                return status;
            }
        }
        throw new InvalidAccountStatusException(lbl);
    }
}
