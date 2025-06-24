package com.ebanking.dtos.customer.response;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class CustomerResponseDTO {
    protected Long id;
    protected String name;
    protected String email;
}
