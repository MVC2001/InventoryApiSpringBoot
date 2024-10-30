package com.rabbitmqapp.mytempv1.Controller;

import com.rabbitmqapp.mytempv1.Dto.WarehouseDto;
import com.rabbitmqapp.mytempv1.Service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseDto> createWarehouse(@RequestBody WarehouseDto warehouseDto) {
        try {
            WarehouseDto createdWarehouse = warehouseService.createWarehouse(warehouseDto);
            return new ResponseEntity<>(createdWarehouse, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> getWarehouse(@PathVariable Long id) {
        try {
            WarehouseDto warehouseDto = warehouseService.getWarehouse(id);
            return warehouseDto != null ?
                    new ResponseEntity<>(warehouseDto, HttpStatus.OK) :
                    new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<WarehouseDto>> getAllWarehouses() {
        try {
            List<WarehouseDto> warehouseDtos = warehouseService.getAllWarehouses();
            return new ResponseEntity<>(warehouseDtos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDto> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDto warehouseDto) {
        try {
            WarehouseDto updatedWarehouse = warehouseService.updateWarehouse(id, warehouseDto);
            return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 for not found
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 for other errors
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        try {
            warehouseService.deleteWarehouse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
