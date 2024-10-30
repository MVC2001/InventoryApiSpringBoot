package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.PurchaseDto;
import com.rabbitmqapp.mytempv1.Entity.Purchase;
import com.rabbitmqapp.mytempv1.Entity.ProductCategory;
import com.rabbitmqapp.mytempv1.Entity.Supplier;
import com.rabbitmqapp.mytempv1.Repository.PurchaseRepository;
import com.rabbitmqapp.mytempv1.Repository.ProductCategoryRepository;
import com.rabbitmqapp.mytempv1.Repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final SupplierRepository supplierRepository;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           ProductCategoryRepository productCategoryRepository,
                           SupplierRepository supplierRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Purchase createPurchase(PurchaseDto purchaseDto) {
        Purchase purchase = new Purchase();

        ProductCategory category = productCategoryRepository.findById(purchaseDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Supplier supplier = supplierRepository.findById(purchaseDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        purchase.setCategory(category);
        purchase.setSupplier(supplier);
        purchase.setQuantity(purchaseDto.getQuantity());
        purchase.setTotalPrice(purchaseDto.getTotalPrice());

        // Convert LocalDate to Date
        purchase.setPurchaseDate(Date.from(purchaseDto.getPurchaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return purchaseRepository.save(purchase);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseDto> getPurchaseById(Long id) {
        return purchaseRepository.findById(id).map(purchase -> {
            PurchaseDto dto = new PurchaseDto();
            dto.setPurchaseId(purchase.getPurchaseId());
            dto.setQuantity(purchase.getQuantity());
            dto.setTotalPrice(purchase.getTotalPrice());

            dto.setPurchaseDate(purchase.getPurchaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            dto.setCategoryId(purchase.getCategory().getCategoryId());
            dto.setCategoryName(purchase.getCategory().getCategoryName());
            dto.setSupplierId(purchase.getSupplier().getSupplierId());
            dto.setSupplierName(purchase.getSupplier().getSupplierName());
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public List<PurchaseDto> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(purchase -> {
                    PurchaseDto dto = new PurchaseDto();
                    dto.setPurchaseId(purchase.getPurchaseId());
                    dto.setQuantity(purchase.getQuantity());
                    dto.setTotalPrice(purchase.getTotalPrice());

                    dto.setPurchaseDate(purchase.getPurchaseDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    dto.setCategoryId(purchase.getCategory().getCategoryId());
                    dto.setCategoryName(purchase.getCategory().getCategoryName());
                    dto.setSupplierId(purchase.getSupplier().getSupplierId());
                    dto.setSupplierName(purchase.getSupplier().getSupplierName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Purchase updatePurchase(Long id, PurchaseDto purchaseDto) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        ProductCategory category = productCategoryRepository.findById(purchaseDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Supplier supplier = supplierRepository.findById(purchaseDto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        purchase.setCategory(category);
        purchase.setSupplier(supplier);
        purchase.setQuantity(purchaseDto.getQuantity());
        purchase.setTotalPrice(purchaseDto.getTotalPrice());
        // Convert LocalDate to Date
        purchase.setPurchaseDate(Date.from(purchaseDto.getPurchaseDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return purchaseRepository.save(purchase);
    }


    @Transactional
    public void deletePurchase(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new RuntimeException("Purchase not found");
        }
        purchaseRepository.deleteById(id);
    }
}
