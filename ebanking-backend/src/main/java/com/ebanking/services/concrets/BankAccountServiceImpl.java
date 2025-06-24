package com.ebanking.services.concrets;

import com.ebanking.dtos.bankaccount.request.BankAccountCreateRequestDTO;
import com.ebanking.dtos.bankaccount.request.BankAccountUpdateStatusRequestDTO;
import com.ebanking.dtos.bankaccount.response.BankAccountResponseDTO;
import com.ebanking.entities.*;
import com.ebanking.enums.AccountStatus;
import com.ebanking.enums.AccountType;
import com.ebanking.exceptions.account.*;
import com.ebanking.exceptions.customer.CustomerDoesntExistsException;
import com.ebanking.exceptions.operations.AmountCannotBeNegativeException;
import com.ebanking.repositories.BankAccountRepository;
import com.ebanking.repositories.CustomerRepository;
import com.ebanking.services.abstracts.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Transactional
@Service
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;


    @Override
    public BankAccountResponseDTO createBankAccount(BankAccountCreateRequestDTO bankAccountCreateRequestDTO) throws AmountCannotBeNegativeException, CustomerDoesntExistsException, InvalidAccountTypeException {

        Long idCustomer = bankAccountCreateRequestDTO.getCustomerId();
        try{
            //checks if the customer exists
            Customer customer = this.customerRepository.findById(idCustomer).orElseThrow();

            //checks if the balance is a positive value
            double initialBalance = bankAccountCreateRequestDTO.getInitialBalance();
            this.checkAmountPositiveAndThrowIfNegatif(initialBalance);

            //Maps the bankAccountCreateRequestDTO to BankAccount entity
            BankAccount bankAccount = null;
            if(bankAccountCreateRequestDTO.getAccountType().equalsIgnoreCase(AccountType.CURRENT_ACCOUNT.getAccountType())){
                bankAccount = CurrentAccount.builder()
                        .id(UUID.randomUUID().toString())
                        .createdAt(new Date())
                        .balance(bankAccountCreateRequestDTO.getInitialBalance())
                        .accountStatus(AccountStatus.CREATED)
                        .overDraft(1000)
                        .customer(customer)
                        .build();
            }
            else if(bankAccountCreateRequestDTO.getAccountType().equalsIgnoreCase(AccountType.SAVING_ACCOUNT.getAccountType())){
                bankAccount = SavingAccount.builder()
                        .id(UUID.randomUUID().toString())
                        .createdAt(new Date())
                        .balance(bankAccountCreateRequestDTO.getInitialBalance())
                        .accountStatus(AccountStatus.CREATED)
                        .interestRate(4.5)
                        .customer(customer)
                        .build();
            }
            else{
                throw new InvalidAccountTypeException(bankAccountCreateRequestDTO.getAccountType());
            }

            /*
                save bankAccount (here invoking the save() method is obligatory if we want to save the entity
                in the database, because this entity is not a manager entity which means that we didn't get this
                entity using the EntityManager)
             */
            this.bankAccountRepository.save(bankAccount);

            //Maps the bankAccount entity to a BankAccountCreatedResponseDTO object
            BankAccountResponseDTO bankAccountResponseDTO = BankAccountResponseDTO.builder()
                    .id(bankAccount.getId())
                    .createdAt(bankAccount.getCreatedAt())
                    .balance(bankAccount.getBalance())
                    .accountStatus(bankAccount.getAccountStatus())
                    .accountType(bankAccountCreateRequestDTO.getAccountType().toUpperCase())
                    .customerId(bankAccount.getCustomer().getId())
                    .build();

            return  bankAccountResponseDTO;
        }
        catch(NoSuchElementException ex){
            throw new CustomerDoesntExistsException(idCustomer);
        }
    }

    @Override
    public BankAccountResponseDTO findBankAccountById(String id) throws AccountNotFoundException {
        try{
            BankAccount bankAccount = this.bankAccountRepository.findById(id).orElseThrow();

            //Maps bankAccount Entity to BankAccountResponseDTO
            BankAccountResponseDTO bankAccountResponseDTO = BankAccountResponseDTO.builder()
                    .id(bankAccount.getId())
                    .createdAt(bankAccount.getCreatedAt())
                    .balance(bankAccount.getBalance())
                    .accountStatus(bankAccount.getAccountStatus())
                    .customerId(bankAccount.getCustomer().getId())
                    .build();
            if(bankAccount instanceof  CurrentAccount){
                bankAccountResponseDTO.setAccountType(AccountType.CURRENT_ACCOUNT.getAccountType());
            }
            else if(bankAccount instanceof  SavingAccount){
                bankAccountResponseDTO.setAccountType(AccountType.SAVING_ACCOUNT.getAccountType());
            }

            return bankAccountResponseDTO;
        }
        catch(NoSuchElementException ex){
            throw new AccountNotFoundException(id);
        }
    }

    @Override
    public BankAccountResponseDTO updateBankAccountStatus(String id, BankAccountUpdateStatusRequestDTO bankAccountUpdateStatusRequestDTO) throws AccountNotFoundException, AccountStillFundedException, InvalidAccountStatusException, ExistingAccountCannotBeCreatedTwiceException {
        try{
            BankAccount bankAccount = this.bankAccountRepository.findByIdWithLockForUpdate(id).orElseThrow();

            String status = bankAccountUpdateStatusRequestDTO.getStatus().toUpperCase();

            //throws InvalidAccountStatusException if status is not a valid AccountStatus
            AccountStatus accStatus = AccountStatus.formLabel(status);
            if(accStatus.equals(AccountStatus.DEACTIVATED)){
                if(new BigDecimal(bankAccount.getBalance()).compareTo(new BigDecimal(0.0d)) > 0.0d){
                    throw new AccountStillFundedException(bankAccount.getBalance());
                }
            }

            if(accStatus.equals(AccountStatus.CREATED)){
                throw new IllegalArgumentException();
            }

            //updates the bank account status
            bankAccount.setAccountStatus(accStatus);

            //Maps bankAccount entity to BankAccountResponseDTO object
            BankAccountResponseDTO bankAccountResponseDTO = BankAccountResponseDTO.builder()
                    .id(bankAccount.getId())
                    .createdAt(bankAccount.getCreatedAt())
                    .balance(bankAccount.getBalance())
                    .accountStatus(bankAccount.getAccountStatus())
                    .customerId(bankAccount.getCustomer().getId())
                    .build();
            if(bankAccount instanceof CurrentAccount){
                bankAccountResponseDTO.setAccountType(AccountType.CURRENT_ACCOUNT.getAccountType());
            }
            else if(bankAccount instanceof SavingAccount){
                bankAccountResponseDTO.setAccountType(AccountType.SAVING_ACCOUNT.getAccountType());
            }

            return  bankAccountResponseDTO;
        }
        catch(NoSuchElementException ex){
            throw new AccountNotFoundException(id);
        }
        catch(IllegalArgumentException ex){
            throw new ExistingAccountCannotBeCreatedTwiceException();
        }
    }

    @Override
    public List<BankAccountResponseDTO> findAllBankAccounts() {
        List<BankAccountResponseDTO> bankAccountResponseDTOS = this.bankAccountRepository.findAll().stream().map(
                bankAccount -> {
                    BankAccountResponseDTO brDTO = BankAccountResponseDTO.builder()
                            .id(bankAccount.getId())
                            .createdAt(bankAccount.getCreatedAt())
                            .balance(bankAccount.getBalance())
                            .accountStatus(bankAccount.getAccountStatus())
                            .customerId(bankAccount.getCustomer().getId())
                            .build();
                    if(bankAccount instanceof  CurrentAccount){
                        brDTO.setAccountType(AccountType.CURRENT_ACCOUNT.getAccountType());
                    }
                    else if(bankAccount instanceof  SavingAccount){
                        brDTO.setAccountType(AccountType.SAVING_ACCOUNT.getAccountType());
                    }

                    return brDTO;
                }
        ).toList();

        return bankAccountResponseDTOS;
    }

    private void checkAmountPositiveAndThrowIfNegatif(double amount) throws AmountCannotBeNegativeException {
        if(amount < 0){
            throw new AmountCannotBeNegativeException(amount);
        }
    }
}
