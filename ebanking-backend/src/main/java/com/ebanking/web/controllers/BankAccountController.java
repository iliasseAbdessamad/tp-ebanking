package com.ebanking.web.controllers;

import com.ebanking.dtos.ResponseErrorDTO;
import com.ebanking.dtos.bankaccount.request.BankAccountCreateRequestDTO;
import com.ebanking.dtos.bankaccount.request.BankAccountUpdateStatusRequestDTO;
import com.ebanking.dtos.bankaccount.response.BankAccountResponseDTO;
import com.ebanking.enums.AccountType;
import com.ebanking.exceptions.account.*;
import com.ebanking.exceptions.customer.CustomerDoesntExistsException;
import com.ebanking.exceptions.operations.AmountCannotBeNegativeException;
import com.ebanking.services.abstracts.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
public class BankAccountController {

    private BankAccountService bankAccountService;


    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccountResponseDTO>> allAccounts(){
        List<BankAccountResponseDTO> bankAccountResponseDTOList = this.bankAccountService.findAllBankAccounts();

        return ResponseEntity.status(HttpStatus.OK).body(bankAccountResponseDTOList);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<?> getAccount(@PathVariable String id){
        try{
            BankAccountResponseDTO responseDTO = this.bankAccountService.findBankAccountById(id);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch(AccountNotFoundException ex){
            ResponseErrorDTO error = this.responseError(
                    "Compte introuvable",
                    "Il n'existe aucun compte bankcaire qui sont id est " + ex.getInvalidIdAccount(),
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@RequestBody BankAccountCreateRequestDTO requestDTO){
        try{
            BankAccountResponseDTO responseDTO = this.bankAccountService.createBankAccount(requestDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        }
        catch(CustomerDoesntExistsException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération d'ajout du compte",
                    "Il n'existe aucun client qui sont id est " + ex.getInvalidCustomerId(),
                    HttpStatus.NOT_FOUND
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        catch(AmountCannotBeNegativeException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération d'ajout du compte",
                    "Un compte ne peut être initialisé avec un montant négatif",
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(InvalidAccountTypeException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération d'ajout du compte",
                    "Le type de compte est invalide, veuillez spécifier " +
                            "une des valeurs " +
                            "suivantes : " + AccountType.SAVING_ACCOUNT.getAccountType() + " ou " +
                            "" + AccountType.CURRENT_ACCOUNT.getAccountType(),
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/accounts/{id}/status")
    public ResponseEntity<?> updateAccountStatus(@PathVariable String id, @RequestBody BankAccountUpdateStatusRequestDTO requestDTO){
        try{
            BankAccountResponseDTO responseDTO = this.bankAccountService.updateBankAccountStatus(id, requestDTO);

            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch(AccountNotFoundException ex){
            ResponseErrorDTO error = this.responseError(
                    "Compte introuvable",
                    "Il n'existe aucun compte bankcaire qui sont id est " + ex.getInvalidIdAccount(),
                    HttpStatus.NOT_FOUND
            );

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        catch (AccountStillFundedException ex) {
            ResponseErrorDTO error = this.responseError(
                    "Clôture refusée",
                    "Impossible de supprimer un compte qui contient encore du solde ('"+ex.getAvailableBalance()+"')",
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch (InvalidAccountStatusException ex) {
            ResponseErrorDTO error = this.responseError(
                    "Status invalide",
                    "Le status que vous avez fourni est invalide",
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch (ExistingAccountCannotBeCreatedTwiceException ex) {
            ResponseErrorDTO error = this.responseError(
                    "Status invalide",
                    "Impossible de creer un compte bancaire qui existe déjà",
                    HttpStatus.BAD_REQUEST
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    private  ResponseErrorDTO responseError(String title, String errorMessage, HttpStatus status){
        return new ResponseErrorDTO(title, errorMessage, status);
    }
}
