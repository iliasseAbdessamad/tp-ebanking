package com.ebanking.dtos.bankaccount.response;

import com.ebanking.enums.AccountStatus;
import lombok.*;
import java.util.Date;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class BankAccountResponseDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private Long customerId;
    private AccountStatus accountStatus;
    private String accountType;
}
