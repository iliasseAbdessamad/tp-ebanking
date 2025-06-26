package com.ebanking.services.abstracts;

import com.ebanking.dtos.bankaccount.request.DepositOperationRequestDTO;
import com.ebanking.dtos.bankaccount.request.TransfertOperationRequestDTO;
import com.ebanking.dtos.bankaccount.request.WithdrawOperationRequestDTO;
import com.ebanking.dtos.bankaccount.response.DepositOperationResponseDTO;
import com.ebanking.dtos.bankaccount.response.TransfertOperationResponseDTO;
import com.ebanking.dtos.bankaccount.response.WithdrawOperationResponseDTO;
import com.ebanking.exceptions.account.AccountNotFoundException;
import com.ebanking.exceptions.account.SavingAccountNotForWithdrawException;
import com.ebanking.exceptions.operations.*;

public interface AccountOperationService {
    DepositOperationResponseDTO deposit(String bankAccountId, DepositOperationRequestDTO requestDTO) throws AccountNotFoundException, AmountCannotBeNegativeException, NoActivatedAccountException;

    WithdrawOperationResponseDTO withdraw(String bankAccountId, WithdrawOperationRequestDTO requestDTO) throws AccountNotFoundException, AmountCannotBeNegativeException, SavingAccountNotForWithdrawException, InsufficientBalanceException, NoActivatedAccountException;

    TransfertOperationResponseDTO transfert(String accountIdSender, TransfertOperationRequestDTO requestDTO) throws AccountNotFoundException, AmountCannotBeNegativeException, InsufficientBalanceException, NoActivatedAccountException, TransfertToSameAccountException, SavingAccountCanNotTransfertToAnotherCustomerAccount;
}
