package com.ebanking.dtos;

import com.ebanking.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder @Data @NoArgsConstructor @AllArgsConstructor
public class OperationDTO {
    private Long idAccount;
    private Date date;
    private double amount;
    private String description;
    private String operationType;
}
