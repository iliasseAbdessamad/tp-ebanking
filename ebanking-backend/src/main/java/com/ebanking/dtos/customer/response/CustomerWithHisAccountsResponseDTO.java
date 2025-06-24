package com.ebanking.dtos.customer.response;

import com.ebanking.dtos.bankaccount.response.BankAccountResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class CustomerWithHisAccountsResponseDTO extends CustomerResponseDTO{
    private List<BankAccountResponseDTO> customerBankAccountResponseDTOList;
}
