package com.rabbitmqapp.mytempv1.Controller;

import com.rabbitmqapp.mytempv1.Dto.SalesDto;
import com.rabbitmqapp.mytempv1.Service.SalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping
    public ResponseEntity<SalesDto> createSale(@RequestBody SalesDto salesDto) {
        try {
            SalesDto createdSale = salesService.createSale(salesDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSale);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<SalesDto>> getAllSales() {
        try {
            List<SalesDto> sales = salesService.getAllSales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesDto> getSaleById(@PathVariable Long id) {
        try {
            SalesDto sale = salesService.getSaleById(id);
            return ResponseEntity.ok(sale);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesDto> updateSale(@PathVariable Long id, @RequestBody SalesDto salesDto) {
        try {
            SalesDto updatedSale = salesService.updateSale(id, salesDto);
            return ResponseEntity.ok(updatedSale);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        try {
            salesService.deleteSale(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

