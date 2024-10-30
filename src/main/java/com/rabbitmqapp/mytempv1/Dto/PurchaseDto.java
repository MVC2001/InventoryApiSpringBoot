package com.rabbitmqapp.mytempv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {
    private Long purchaseId;
    private Long categoryId;
    private String categoryName;
    private Long supplierId;
    private String supplierName;
    private Integer quantity;
    private Double totalPrice;
    private LocalDate purchaseDate;
}
