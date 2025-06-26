package com.ebanking.web.controllers;

import com.ebanking.dtos.ResponseErrorDTO;
import com.ebanking.dtos.bankaccount.request.DepositOperationRequestDTO;
import com.ebanking.dtos.bankaccount.request.TransfertOperationRequestDTO;
import com.ebanking.dtos.bankaccount.request.WithdrawOperationRequestDTO;
import com.ebanking.dtos.bankaccount.response.DepositOperationResponseDTO;
import com.ebanking.dtos.bankaccount.response.TransfertOperationResponseDTO;
import com.ebanking.dtos.bankaccount.response.WithdrawOperationResponseDTO;
import com.ebanking.exceptions.account.AccountNotFoundException;
import com.ebanking.exceptions.account.SavingAccountNotForWithdrawException;
import com.ebanking.exceptions.operations.*;
import com.ebanking.services.abstracts.AccountOperationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
public class AccountOperationController {

    AccountOperationService operationService;

    @PostMapping("/accounts/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String id, @RequestBody DepositOperationRequestDTO requestDTO){
        try{
            DepositOperationResponseDTO responseDTO = this.operationService.deposit(id, requestDTO);
            return ResponseEntity.ok().body(responseDTO);
        }
        catch(AccountNotFoundException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Il n'existe aucun compte bancaire qui sont id est " + ex.getInvalidIdAccount(),
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        catch(AmountCannotBeNegativeException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Le montant à déposer doit être positif",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(NoActivatedAccountException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Votre compte n'est pas encore activé (etats : '"+ex.getAccountStatus()+"')",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/accounts/{id}/transfert")
    public ResponseEntity transfert(@PathVariable String id, @RequestBody TransfertOperationRequestDTO requestDTO){
        try{
            TransfertOperationResponseDTO responseDTO = this.operationService.transfert(id, requestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        }
        catch(AccountNotFoundException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Il n'existe aucun compte qui son id est : '"+ex.getInvalidIdAccount()+"'",
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        catch(AmountCannotBeNegativeException  ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Le montant doit être positif",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(InsufficientBalanceException  ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Votre sodle est insuffisant",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(NoActivatedAccountException  ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Le compte bancaire doit être activé pour cette opération",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(TransfertToSameAccountException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Vous ne pouvez pas transferer de l'argent au même compte banciare",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(SavingAccountCanNotTransfertToAnotherCustomerAccount ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Un compte d'épargne ne peut transferer de l'argent qu'à un de vos autres comptes",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/accounts/{id}/withdraw")
    public ResponseEntity widthdraw(@PathVariable String id, @RequestBody WithdrawOperationRequestDTO requestDTO){
        try{
            WithdrawOperationResponseDTO responseDTO = this.operationService.withdraw(id, requestDTO);
            return ResponseEntity.ok().body(responseDTO);
        }
        catch(AccountNotFoundException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Il n'existe aucun compte bancaire qui son id est '"+ex.getInvalidIdAccount()+"'",
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        catch(NoActivatedAccountException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Votre compte bancaire n'est pas activé (etat : '"+ex.getAccountStatus()+"')",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(InsufficientBalanceException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Votre solde est insuffisant",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(AmountCannotBeNegativeException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Le motant à retirer doit être positif",
                    HttpStatus.BAD_REQUEST
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        catch(SavingAccountNotForWithdrawException ex){
            ResponseErrorDTO error = this.responseError(
                    "Echoue de l'opération",
                    "Vous ne pouvez pas retirer de l'argent depuis un compte d'épargne",
                    HttpStatus.NOT_FOUND
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    private  ResponseErrorDTO responseError(String title, String errorMessage, HttpStatus status){
        return new ResponseErrorDTO(title, errorMessage, status);
    }
}
