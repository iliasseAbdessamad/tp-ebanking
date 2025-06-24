package com.ebanking.services.concrets;

import com.ebanking.dtos.bankaccount.response.BankAccountResponseDTO;
import com.ebanking.dtos.customer.request.CustomerCreateRequestDTO;
import com.ebanking.dtos.customer.request.CustomerUpdateEmailRequestDTO;
import com.ebanking.dtos.customer.response.CustomerResponseDTO;
import com.ebanking.dtos.customer.response.CustomerWithHisAccountsResponseDTO;
import com.ebanking.entities.CurrentAccount;
import com.ebanking.entities.Customer;
import com.ebanking.entities.SavingAccount;
import com.ebanking.enums.AccountType;
import com.ebanking.exceptions.customer.CustomerDoesntExistsException;
import com.ebanking.exceptions.customer.EmailAlreadyExistsException;
import com.ebanking.exceptions.customer.InvalidEmailPatternException;
import com.ebanking.repositories.CustomerRepository;
import com.ebanking.services.abstracts.CustomerService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;
    private EntityManager entityManager;

    @Override
    public List<CustomerResponseDTO> findAllCustomers() {
        List<CustomerResponseDTO> customerResponseDTOList = this.customerRepository.findAll().stream().map(
                customer -> {
                    return CustomerResponseDTO.builder()
                            .id(customer.getId())
                            .name(customer.getName())
                            .email(customer.getEmail())
                            .build();
                }
        ).toList();

        return customerResponseDTOList;
    }

    @Override
    public CustomerResponseDTO findCustomerById(Long id) throws CustomerDoesntExistsException {
        try{
            Customer customer = this.findCustomerOrThrow(id);

            return CustomerResponseDTO.builder()
                    .id(customer.getId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();
        }
        catch(NoSuchElementException ex){
            throw new CustomerDoesntExistsException(id);
        }
    }

    @Override
    public CustomerResponseDTO addCustomer(CustomerCreateRequestDTO customerCreateRequestDTO) throws DataIntegrityViolationException {

        Customer customer = Customer.builder()
                .name(customerCreateRequestDTO.getName())
                .email(customerCreateRequestDTO.getEmail())
                .build();
        this.customerRepository.save(customer);

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }

    @Override
    public CustomerResponseDTO updateCustomerEmail(Long id, CustomerUpdateEmailRequestDTO customerUpdateEmailRequestDTO) throws CustomerDoesntExistsException , EmailAlreadyExistsException, InvalidEmailPatternException {
        try{
            Customer customer = this.findCustomerOrThrow(id);

           if(this.customerRepository.findByEmail(customerUpdateEmailRequestDTO.getNewEmail()).isPresent()){
               throw new EmailAlreadyExistsException(customerUpdateEmailRequestDTO.getNewEmail());
           }
           else{
               customer.setEmail(customerUpdateEmailRequestDTO.getNewEmail());
           }

            return CustomerResponseDTO.builder()
                    .id(id)
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();
        }
        catch(NoSuchElementException ex){
            throw new CustomerDoesntExistsException(id);
        }
    }

    @Override
    public CustomerResponseDTO deleteCustomer(Long id) throws CustomerDoesntExistsException {
        try{
            Customer customer = this.findCustomerOrThrow(id);

            CustomerResponseDTO customerResponseDTO = CustomerResponseDTO.builder()
                    .id(customer.getId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();

            //delete customer
            this.customerRepository.delete(customer);

            return customerResponseDTO;
        }
        catch(NoSuchElementException ex){
            throw new CustomerDoesntExistsException(id);
        }
    }

    @Override
    public CustomerWithHisAccountsResponseDTO getCustomerAccountsResponseDtos(Long id) throws CustomerDoesntExistsException{
        try{
            Customer customer = this.findCustomerOrThrow(id);
            List<BankAccountResponseDTO> accountsDTOlist = customer.getBankAccountList().stream().map(acc -> {
                BankAccountResponseDTO accDTO = BankAccountResponseDTO.builder()
                        .id(acc.getId())
                        .balance(acc.getBalance())
                        .createdAt(acc.getCreatedAt())
                        .customerId(id)
                        .accountStatus(acc.getAccountStatus())
                        .build();
                if(acc instanceof CurrentAccount){
                    accDTO.setAccountType(AccountType.CURRENT_ACCOUNT.getAccountType());
                }
                else if(acc instanceof SavingAccount){
                    accDTO.setAccountType(AccountType.SAVING_ACCOUNT.getAccountType());
                }

                return accDTO;
            }).toList();

            CustomerWithHisAccountsResponseDTO customerWithHisAccountsResponseDTO = new CustomerWithHisAccountsResponseDTO();
            customerWithHisAccountsResponseDTO.setId(customer.getId());
            customerWithHisAccountsResponseDTO.setName(customer.getName());
            customerWithHisAccountsResponseDTO.setEmail(customer.getEmail());
            customerWithHisAccountsResponseDTO.setCustomerBankAccountResponseDTOList(accountsDTOlist);

            return customerWithHisAccountsResponseDTO;
        }
        catch(NoSuchElementException ex){
            throw new CustomerDoesntExistsException(id);
        }
    }

    private Customer findCustomerOrThrow(Long id) throws NoSuchElementException{
        try{
            Customer customer = this.customerRepository.findById(id).orElseThrow();

            return customer;
        }
        catch(NoSuchElementException ex){
            throw new NoSuchElementException();
        }
    }
}
