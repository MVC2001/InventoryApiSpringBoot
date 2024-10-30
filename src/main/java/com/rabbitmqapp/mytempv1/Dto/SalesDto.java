package com.rabbitmqapp.mytempv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesDto {
    private Long saleId;
    private Long categoryId;
    private String categoryName;
    private Integer quantity_sold;
    private Double price;
    private Double amount_sold;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String saleStatus;
    private String paymentMethod;
}


