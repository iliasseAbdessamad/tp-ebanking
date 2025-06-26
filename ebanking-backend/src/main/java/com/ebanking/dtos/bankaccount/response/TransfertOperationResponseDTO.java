package com.ebanking.dtos.bankaccount.response;

import lombok.*;

import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Setter @Getter @Builder @ToString
public class TransfertOperationResponseDTO {
    private String accountIdSender;
    private String accountIdReceiver;
    private String operationType;
    private Date operationDate;
    private double amount;
    private double balanceBeforeOperation;
    private double balanceAfterOperation;
    private Double overDraftIfExists;
}
