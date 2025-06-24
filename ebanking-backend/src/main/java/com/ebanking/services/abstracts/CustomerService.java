package com.ebanking.services.abstracts;

import com.ebanking.dtos.customer.request.CustomerCreateRequestDTO;
import com.ebanking.dtos.customer.request.CustomerUpdateEmailRequestDTO;
import com.ebanking.dtos.customer.response.CustomerResponseDTO;
import com.ebanking.dtos.customer.response.CustomerWithHisAccountsResponseDTO;
import com.ebanking.exceptions.customer.CustomerDoesntExistsException;
import com.ebanking.exceptions.customer.EmailAlreadyExistsException;
import com.ebanking.exceptions.customer.InvalidEmailPatternException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public interface CustomerService {

    List<CustomerResponseDTO> findAllCustomers();
    CustomerResponseDTO findCustomerById(Long id) throws CustomerDoesntExistsException;
    CustomerResponseDTO addCustomer(CustomerCreateRequestDTO customerCreateRequestDTO) throws DataIntegrityViolationException;
    CustomerResponseDTO updateCustomerEmail(Long id, CustomerUpdateEmailRequestDTO customerUpdateEmailRequestDTO) throws CustomerDoesntExistsException, EmailAlreadyExistsException, InvalidEmailPatternException;
    CustomerResponseDTO deleteCustomer(Long id) throws CustomerDoesntExistsException;
    CustomerWithHisAccountsResponseDTO getCustomerAccountsResponseDtos(Long id) throws CustomerDoesntExistsException;
}
