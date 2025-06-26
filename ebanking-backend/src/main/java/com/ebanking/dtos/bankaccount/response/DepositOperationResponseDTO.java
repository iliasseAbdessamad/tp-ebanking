package com.ebanking.dtos.bankaccount.response;

import com.ebanking.entities.BankAccount;
import lombok.*;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Setter @Getter @Builder @ToString
public class DepositOperationResponseDTO {
    private String accountId;
    private String operationType;
    private Date operationDate;
    private double amount;
    private double balanceBeforeOperation;
    private double balanceAfterOperation;
    private Double overDraftIfExists;
}
