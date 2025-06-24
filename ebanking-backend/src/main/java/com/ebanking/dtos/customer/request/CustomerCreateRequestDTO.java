package com.ebanking.dtos.customer.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder @ToString
public class CustomerCreateRequestDTO {
    String name;
    String email;
}
