package com.ebanking.services.concrets;

import com.ebanking.dtos.bankaccount.request.DepositOperationRequestDTO;
import com.ebanking.dtos.bankaccount.request.TransfertOperationRequestDTO;
import com.ebanking.dtos.bankaccount.request.WithdrawOperationRequestDTO;
import com.ebanking.dtos.bankaccount.response.DepositOperationResponseDTO;
import com.ebanking.dtos.bankaccount.response.TransfertOperationResponseDTO;
import com.ebanking.dtos.bankaccount.response.WithdrawOperationResponseDTO;
import com.ebanking.entities.AccountOperation;
import com.ebanking.entities.BankAccount;
import com.ebanking.entities.CurrentAccount;
import com.ebanking.entities.SavingAccount;
import com.ebanking.enums.AccountStatus;
import com.ebanking.enums.OperationType;
import com.ebanking.exceptions.account.AccountNotFoundException;
import com.ebanking.exceptions.account.SavingAccountNotForWithdrawException;
import com.ebanking.exceptions.operations.*;
import com.ebanking.repositories.BankAccountRepository;
import com.ebanking.repositories.OperationRepository;
import com.ebanking.services.abstracts.AccountOperationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.NoSuchElementException;

@Transactional
@Service
@AllArgsConstructor
public class AccountOperationServiceImpl implements AccountOperationService {

    BankAccountRepository bankAccountRepository;
    OperationRepository operationRepository;

    @Override
    public DepositOperationResponseDTO deposit(String bankAccountId, DepositOperationRequestDTO requestDTO) throws AccountNotFoundException, AmountCannotBeNegativeException, NoActivatedAccountException {

        try{
            this.throwIfAmountIsNegatif(requestDTO.getAmount());
            BankAccount bankAccount = this.bankAccountRepository.findByIdWithLockForUpdate(bankAccountId).orElseThrow();
            this.throwIfAccountNotActived(bankAccount);

            double oldBalance = bankAccount.getBalance();
            if(bankAccount instanceof SavingAccount){
                bankAccount.setBalance(oldBalance + requestDTO.getAmount());
            }
            else {
                double amount = requestDTO.getAmount();
                if(oldBalance > 0){
                    bankAccount.setBalance(oldBalance + requestDTO.getAmount());
                }
                else{
                    if((int)((CurrentAccount)bankAccount).getOverDraft() == 800){
                        bankAccount.setBalance(oldBalance + amount);
                        ((CurrentAccount)bankAccount).setOverDraft(800);
                    }
                    else if(((CurrentAccount)bankAccount).getOverDraft() < 800){
                        double distance = 800 - ((CurrentAccount)bankAccount).getOverDraft();
                        double bankGain = (distance * (2d / 100));
                        double availableAmount = amount - bankGain;

                        if(availableAmount >= distance){
                            ((CurrentAccount)bankAccount).setOverDraft(800);
                            if(availableAmount > distance){
                                bankAccount.setBalance(availableAmount - distance);
                            }
                        }
                        else{
                            bankGain = amount * (2d / 100);
                            double oldOverDraft = ((CurrentAccount)bankAccount).getOverDraft();
                            ((CurrentAccount)bankAccount).setOverDraft(oldOverDraft + (amount - bankGain));
                        }
                    }
                }
            }

            AccountOperation operation = AccountOperation.builder()
                    .operationType(OperationType.DEPOSIT)
                    .date(new Date())
                    .description(requestDTO.getDescription())
                    .bankAccount(bankAccount)
                    .build();

            this.operationRepository.save(operation);

            return DepositOperationResponseDTO.builder()
                    .accountId(bankAccountId)
                    .operationType(OperationType.DEPOSIT.name())
                    .operationDate(operation.getDate())
                    .amount(requestDTO.getAmount())
                    .balanceBeforeOperation(oldBalance)
                    .balanceAfterOperation(bankAccount.getBalance())
                    .overDraftIfExists(bankAccount instanceof CurrentAccount ? ((CurrentAccount)bankAccount).getOverDraft() : null)
                    .build();
        }
        catch(NoSuchElementException ex){
            throw new AccountNotFoundException(bankAccountId);
        }
        catch(IllegalArgumentException ex){
            throw new AmountCannotBeNegativeException(requestDTO.getAmount());
        }
    }

    @Override
    public WithdrawOperationResponseDTO withdraw(String bankAccountId, WithdrawOperationRequestDTO requestDTO) throws AccountNotFoundException, AmountCannotBeNegativeException, SavingAccountNotForWithdrawException, InsufficientBalanceException, NoActivatedAccountException {
        try{
            this.throwIfAmountIsNegatif(requestDTO.getAmount());
            BankAccount bankAccount = this.bankAccountRepository.findByIdWithLockForUpdate(bankAccountId).orElseThrow();
            this.throwIfAccountNotActived(bankAccount);

            double oldBalance = bankAccount.getBalance();

            AccountOperation operation = this.widthdrawNotForTransfert(bankAccount, requestDTO);

            return WithdrawOperationResponseDTO.builder()
                    .accountId(bankAccountId)
                    .operationType(OperationType.DEPOSIT.name())
                    .operationDate(operation.getDate())
                    .amount(requestDTO.getAmount())
                    .balanceBeforeOperation(oldBalance)
                    .balanceAfterOperation(bankAccount.getBalance())
                    .overDraftIfExists(bankAccount instanceof CurrentAccount ? ((CurrentAccount)bankAccount).getOverDraft() : null)
                    .build();


        }
        catch(NoSuchElementException ex){
            throw new AccountNotFoundException(bankAccountId);
        }
        catch(IllegalArgumentException ex){
            throw new AmountCannotBeNegativeException(requestDTO.getAmount());
        }
    }

    @Override
    public TransfertOperationResponseDTO transfert(String accountIdSender, TransfertOperationRequestDTO requestDTO) throws AccountNotFoundException, AmountCannotBeNegativeException, InsufficientBalanceException, NoActivatedAccountException, TransfertToSameAccountException, SavingAccountCanNotTransfertToAnotherCustomerAccount {

        try{
            String accountIdReceiver = requestDTO.getIdAccountReceiver();

            if(accountIdSender.equals(accountIdReceiver)){
                throw new TransfertToSameAccountException();
            }

            double amount = requestDTO.getAmount();
            BankAccount accountSender = null;
            BankAccount accountReceiver = null;

            this.throwIfAmountIsNegatif(amount);

            //checks if the sender exists
            try{
                accountSender = this.bankAccountRepository.findByIdWithLockForUpdate(accountIdSender).orElseThrow();
                this.throwIfAccountNotActived(accountSender);
            }
            catch(NoSuchElementException ex){
                throw new AccountNotFoundException(accountIdSender);
            }

            //checks if the sender exists
            try{
                accountReceiver = this.bankAccountRepository.findByIdWithLockForUpdate(accountIdReceiver).orElseThrow();
                this.throwIfAccountNotActived(accountReceiver);
            }
            catch(NoSuchElementException ex){
                throw new AccountNotFoundException(accountIdReceiver);
            }

            double balance = accountSender.getBalance();

            this.widthdrawForTransfert(accountSender, accountReceiver, amount);
            this.depositForTransfert(accountReceiver, amount);

            AccountOperation operation = AccountOperation.builder()
                    .operationType(OperationType.TRANSFERT)
                    .date(new Date())
                    .description(requestDTO.getDescription())
                    .bankAccount(accountSender)
                    .build();

            this.operationRepository.save(operation);

            return TransfertOperationResponseDTO.builder()
                    .accountIdSender(accountIdSender)
                    .accountIdReceiver(accountIdReceiver)
                    .operationDate(operation.getDate())
                    .operationType(operation.getOperationType().name())
                    .balanceBeforeOperation(balance)
                    .balanceAfterOperation(accountSender.getBalance())
                    .amount(amount)
                    .overDraftIfExists(accountSender instanceof CurrentAccount ? ((CurrentAccount)accountSender).getOverDraft() : null)
                    .build();
        }
        catch(IllegalArgumentException  ex){
            throw new AmountCannotBeNegativeException(requestDTO.getAmount());
        }
        catch(NoActivatedAccountException ex){
            throw new NoActivatedAccountException(ex.getAccountStatus());
        }
    }


    private void throwIfAccountNotActived(BankAccount account) throws NoActivatedAccountException{
        if(account.getAccountStatus() != AccountStatus.ACTIVATED){
            throw new NoActivatedAccountException(account.getAccountStatus().name());
        }
    }

    private void throwIfAmountIsNegatif(double amount) throws IllegalArgumentException {
        if(amount < 0){
            throw new IllegalArgumentException();
        }
    }

    private AccountOperation widthdrawNotForTransfert(BankAccount bankAccount, WithdrawOperationRequestDTO requestDTO) throws SavingAccountNotForWithdrawException, InsufficientBalanceException {
        if(bankAccount instanceof SavingAccount){
            throw new SavingAccountNotForWithdrawException();
        }
        else{
            CurrentAccount currentAccount = (CurrentAccount)bankAccount;
            double balance = bankAccount.getBalance();
            double overdraft = currentAccount.getOverDraft();
            double amount = requestDTO.getAmount();

            if(balance >= amount){
                currentAccount.setBalance(balance - amount);
            }
            else{
                if(balance + currentAccount.getOverDraft() < amount){
                    throw new InsufficientBalanceException();
                }
                else{
                    balance = balance - amount;
                    bankAccount.setBalance(0d);
                    currentAccount.setOverDraft(overdraft + balance);
                }
            }
            AccountOperation operation = AccountOperation.builder()
                    .operationType(OperationType.DEPOSIT)
                    .date(new Date())
                    .description(requestDTO.getDescription())
                    .bankAccount(bankAccount)
                    .build();

            this.operationRepository.save(operation);

            return operation;
        }
    }

    private void depositForTransfert(BankAccount accountReceiver, double amount) {

        double oldBalance = accountReceiver.getBalance();

        if(accountReceiver instanceof SavingAccount){
            accountReceiver.setBalance(amount + oldBalance);
        }
        else {
            if(oldBalance > 0){
                accountReceiver.setBalance(oldBalance + accountReceiver.getBalance());
            }
            else{
                if((int)((CurrentAccount)accountReceiver).getOverDraft() == 800){
                    accountReceiver.setBalance(oldBalance + amount);
                    ((CurrentAccount)accountReceiver).setOverDraft(800);
                }
                else if(((CurrentAccount)accountReceiver).getOverDraft() < 800){
                    double distance = 800 - ((CurrentAccount)accountReceiver).getOverDraft();
                    double bankGain = (distance * (2d / 100));
                    double availableAmount = amount - bankGain;

                    if(availableAmount >= distance){
                        ((CurrentAccount)accountReceiver).setOverDraft(800);
                        if(availableAmount > distance){
                            accountReceiver.setBalance(availableAmount - distance);
                        }
                    }
                    else{
                        bankGain = amount * (2d / 100);
                        double oldOverDraft = ((CurrentAccount)accountReceiver).getOverDraft();
                        ((CurrentAccount)accountReceiver).setOverDraft(oldOverDraft + (amount - bankGain));
                    }
                }
            }
        }

    }

    private void widthdrawForTransfert(BankAccount bankAccountSender, BankAccount bankAccountReceiver, double amount) throws InsufficientBalanceException, SavingAccountCanNotTransfertToAnotherCustomerAccount {

        if(bankAccountSender instanceof SavingAccount){
            if(bankAccountSender.getCustomer().getId() != bankAccountReceiver.getCustomer().getId()){
                throw new SavingAccountCanNotTransfertToAnotherCustomerAccount();
            }
        }
        double balance = bankAccountSender.getBalance();

        if(bankAccountSender instanceof CurrentAccount){
            CurrentAccount currentAccount = (CurrentAccount)bankAccountSender;
            double overdraft = currentAccount.getOverDraft();

            if(balance >= amount){
                currentAccount.setBalance(balance - amount);
            }
            else{
                if(balance + currentAccount.getOverDraft() < amount){
                    throw new InsufficientBalanceException();
                }
                else{
                    balance = balance - amount;
                    bankAccountSender.setBalance(0d);
                    currentAccount.setOverDraft(overdraft + balance);
                }
            }
        }
        else{
            if(balance >= amount){
                bankAccountSender.setBalance(balance - amount);
            }
            else{
                throw new InsufficientBalanceException();
            }
        }
    }
}
