package com.ebanking.dtos.bankaccount.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TransfertOperationRequestDTO extends AbstractOperationRequestDTO{
    private String idAccountReceiver;
}
