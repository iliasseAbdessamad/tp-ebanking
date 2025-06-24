package com.ebanking.dtos.bankaccount.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class BankAccountUpdateStatusRequestDTO {
    private String status;
}
