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
public class ProductDto {
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private String description;
    private Integer quantity;
    private Double price;
    private Long supplierId;


}
