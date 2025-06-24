package com.ebanking.dtos.bankaccount.request;


import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class BankAccountCreateRequestDTO {

    private double initialBalance;
    private String accountType;
    private Long customerId;
}
