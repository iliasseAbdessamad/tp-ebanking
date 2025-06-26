package com.ebanking.dtos.bankaccount.response;


import com.ebanking.dtos.OperationDTO;
import com.ebanking.entities.BankAccount;
import lombok.*;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class OperationsHistoryResponseDTO {
    private BankAccountResponseDTO account;
    private int currentPage;
    private int totalPages;
    private int pageSize;
    List<OperationDTO> operations;
}
