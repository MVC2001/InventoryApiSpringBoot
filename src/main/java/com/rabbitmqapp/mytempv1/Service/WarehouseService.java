package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.WarehouseDto;
import com.rabbitmqapp.mytempv1.Entity.Warehouse;
import com.rabbitmqapp.mytempv1.Repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    public WarehouseDto createWarehouse(WarehouseDto warehouseDto) {
        try {
            Warehouse warehouse = new Warehouse();
            warehouse.setWarehouseName(warehouseDto.getWarehouseName());
            warehouse.setLocation(warehouseDto.getLocation());
            warehouse.setCapacity(warehouseDto.getCapacity());
            warehouse.setProducts(new HashSet<>()); // Initialize products set

            Warehouse savedWarehouse = warehouseRepository.save(warehouse);
            return mapToDto(savedWarehouse);
        } catch (Exception e) {
            // Log the error message
            throw new RuntimeException("Error creating warehouse: " + e.getMessage());
        }
    }

    public WarehouseDto getWarehouse(Long warehouseId) {
        try {
            Optional<Warehouse> warehouse = warehouseRepository.findById(warehouseId);
            return warehouse.map(this::mapToDto).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving warehouse: " + e.getMessage());
        }
    }

    public List<WarehouseDto> getAllWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            return warehouses.stream().map(this::mapToDto).toList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving warehouses: " + e.getMessage());
        }
    }

    public WarehouseDto updateWarehouse(Long warehouseId, WarehouseDto warehouseDto) {
        Optional<Warehouse> existingWarehouse = warehouseRepository.findById(warehouseId);
        if (existingWarehouse.isPresent()) {
            Warehouse warehouse = existingWarehouse.get();
            warehouse.setWarehouseName(warehouseDto.getWarehouseName());
            warehouse.setLocation(warehouseDto.getLocation());
            warehouse.setCapacity(warehouseDto.getCapacity());

            Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
            return mapToDto(updatedWarehouse);
        } else {
            throw new EntityNotFoundException("Warehouse not found with ID: " + warehouseId);
        }
    }

    public void deleteWarehouse(Long warehouseId) {
        try {
            warehouseRepository.deleteById(warehouseId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting warehouse: " + e.getMessage());
        }
    }

    private WarehouseDto mapToDto(Warehouse warehouse) {
        return new WarehouseDto(
                warehouse.getWarehouseId(),
                warehouse.getWarehouseName(),
                warehouse.getLocation(),
                warehouse.getCapacity()
        );
    }
}
