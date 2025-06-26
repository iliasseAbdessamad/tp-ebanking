package com.ebanking.services.abstracts;

import com.ebanking.dtos.bankaccount.request.BankAccountCreateRequestDTO;
import com.ebanking.dtos.bankaccount.request.BankAccountUpdateStatusRequestDTO;
import com.ebanking.dtos.bankaccount.response.BankAccountResponseDTO;
import com.ebanking.dtos.bankaccount.response.OperationsHistoryResponseDTO;
import com.ebanking.exceptions.account.*;
import com.ebanking.exceptions.customer.CustomerDoesntExistsException;
import com.ebanking.exceptions.operations.AmountCannotBeNegativeException;
import java.util.List;

public interface BankAccountService {

    BankAccountResponseDTO createBankAccount(BankAccountCreateRequestDTO bankAccountCreateRequestDTO) throws AmountCannotBeNegativeException, CustomerDoesntExistsException, InvalidAccountTypeException;
    BankAccountResponseDTO findBankAccountById(String id) throws AccountNotFoundException;
    List<BankAccountResponseDTO> findAllBankAccounts();
    BankAccountResponseDTO updateBankAccountStatus(String id, BankAccountUpdateStatusRequestDTO bankAccountUpdateStatusRequestDTO) throws AccountNotFoundException, AccountStillFundedException, InvalidAccountStatusException, ExistingAccountCannotBeCreatedTwiceException;
    OperationsHistoryResponseDTO getPaginatedOperationsForAccount(String id, int page, int size) throws AccountNotFoundException;

}