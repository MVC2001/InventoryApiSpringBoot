package com.rabbitmqapp.mytempv1.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {
    private Long warehouseId;      // ID of the warehouse
    private String warehouseName;  // Name of the warehouse
    private String location;       // Location of the warehouse
    private Integer capacity;      // Capacity of the warehouse
}
