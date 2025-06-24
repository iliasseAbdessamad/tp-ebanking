package com.ebanking.dtos.bankaccount.response;

import com.ebanking.enums.AccountStatus;
import lombok.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class BankAccountResponseDTO {
    protected String id;
    protected double balance;
    protected Date createdAt;
    protected Long customerId;
    protected AccountStatus accountStatus;
    protected String accountType;
}
