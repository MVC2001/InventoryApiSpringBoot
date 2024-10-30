package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.SupplierDto;
import com.rabbitmqapp.mytempv1.Entity.Supplier;
import com.rabbitmqapp.mytempv1.Repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, ModelMapper modelMapper) {
        this.supplierRepository = supplierRepository;
        this.modelMapper = modelMapper;
    }

    public SupplierDto createSupplier(SupplierDto supplierDto) {
        try {
            Supplier supplier = modelMapper.map(supplierDto, Supplier.class);
            Supplier savedSupplier = supplierRepository.save(supplier);
            return modelMapper.map(savedSupplier, SupplierDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create supplier: " + e.getMessage());
        }
    }

    public SupplierDto getSupplierById(Long id) {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            return modelMapper.map(supplier, SupplierDto.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error fetching supplier: " + e.getMessage());
        }
    }

    public List<SupplierDto> getAllSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAll();
            return suppliers.stream()
                    .map(supplier -> modelMapper.map(supplier, SupplierDto.class))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching suppliers: " + e.getMessage());
        }
    }

    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        try {
            Supplier supplier = supplierRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            supplier.setSupplierName(supplierDto.getSupplierName());
            supplier.setContactPerson(supplierDto.getContactPerson());
            supplier.setEmail(supplierDto.getEmail());
            Supplier updatedSupplier = supplierRepository.save(supplier);
            return modelMapper.map(updatedSupplier, SupplierDto.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error updating supplier: " + e.getMessage());
        }
    }

    public void deleteSupplier(Long id) {
        try {
            if (!supplierRepository.existsById(id)) {
                throw new RuntimeException("Supplier not found");
            }
            supplierRepository.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error deleting supplier: " + e.getMessage());
        }
    }
}
