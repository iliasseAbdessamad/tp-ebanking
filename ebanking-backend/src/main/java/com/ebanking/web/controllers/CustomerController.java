package com.ebanking.web.controllers;

import com.ebanking.dtos.ResponseErrorDTO;
import com.ebanking.dtos.bankaccount.request.BankAccountCreateRequestDTO;
import com.ebanking.dtos.bankaccount.request.BankAccountUpdateStatusRequestDTO;
import com.ebanking.dtos.bankaccount.response.BankAccountResponseDTO;
import com.ebanking.dtos.customer.request.CustomerCreateRequestDTO;
import com.ebanking.dtos.customer.request.CustomerUpdateEmailRequestDTO;
import com.ebanking.dtos.customer.response.CustomerResponseDTO;
import com.ebanking.dtos.customer.response.CustomerWithHisAccountsResponseDTO;
import com.ebanking.enums.AccountType;
import com.ebanking.exceptions.account.*;
import com.ebanking.exceptions.customer.CustomerDoesntExistsException;
import com.ebanking.exceptions.customer.EmailAlreadyExistsException;
import com.ebanking.exceptions.operations.AmountCannotBeNegativeException;
import com.ebanking.services.abstracts.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class CustomerController {

    CustomerService customerService;



    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponseDTO>> findAllCustomers(){
        List<CustomerResponseDTO> responseDTO = this.customerService.findAllCustomers();

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable Long id){
        try{
            CustomerResponseDTO responseDTO = this.customerService.findCustomerById(id);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch(CustomerDoesntExistsException ex){
            ResponseErrorDTO error = this.responseError(
                    "Client introuvable",
                    "Il n'existe aucun client qui sont id est " + ex.getInvalidCustomerId(),
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/customers/{id}/accounts")
    public ResponseEntity<?> getCustomerAndHisAccounts(@PathVariable Long id){
        try{
            CustomerWithHisAccountsResponseDTO responseDTO = this.customerService.getCustomerAccountsResponseDtos(id);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }
        catch(CustomerDoesntExistsException ex){
            ResponseErrorDTO error = this.responseError(
                    "Ce client n'existe pas",
                    "L'id que vous avez founrni ('"+ex.getInvalidCustomerId()+"') n'identifie aucun client",
                    HttpStatus.NOT_FOUND
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerCreateRequestDTO requestDTO){
        try{
            CustomerResponseDTO responseDTO = this.customerService.addCustomer(requestDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }
        catch(DataIntegrityViolationException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération d'ajout d'un client",
                    "L'adresse email que vous avez fourni existe déjà !",
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/customers/{id}/email")
    public ResponseEntity<?> updateCustomerEmail(@PathVariable Long id, @RequestBody CustomerUpdateEmailRequestDTO requestDTO){
        try{
            CustomerResponseDTO responseDTO = this.customerService.updateCustomerEmail(id, requestDTO);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch (CustomerDoesntExistsException ex) {
            ResponseErrorDTO error = this.responseError(
                    "Echoue de la mise à jour de l'email",
                    "Il n'existe aucun client qui sont id égale à " + id,
                    HttpStatus.NOT_FOUND
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        catch (EmailAlreadyExistsException ex) {
            ResponseErrorDTO error = this.responseError(
                    "Echoue de la mise à jour de l'email",
                    "L'email que vous avez fourni existe déjà ('"+ ex.getEmail() +"')",
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id){
        try{
            CustomerResponseDTO responseDTO = this.customerService.deleteCustomer(id);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch(CustomerDoesntExistsException ex){
            ResponseErrorDTO error = this.responseError(
                    "Client introuvable",
                    "Il n'existe aucun client qui sont id est " + ex.getInvalidCustomerId(),
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    private  ResponseErrorDTO responseError(String title, String errorMessage, HttpStatus status){
        return new ResponseErrorDTO(title, errorMessage, status);
    }
}
