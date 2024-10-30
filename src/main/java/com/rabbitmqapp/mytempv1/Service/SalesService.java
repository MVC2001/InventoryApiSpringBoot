package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.SalesDto;
import com.rabbitmqapp.mytempv1.Entity.Product;
import com.rabbitmqapp.mytempv1.Entity.ProductCategory;
import com.rabbitmqapp.mytempv1.Entity.Sales;
import com.rabbitmqapp.mytempv1.Repository.ProductCategoryRepository;
import com.rabbitmqapp.mytempv1.Repository.ProductRepository;
import com.rabbitmqapp.mytempv1.Repository.SalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesService {
    private final SalesRepository salesRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SalesService(SalesRepository salesRepository,
                        ProductCategoryRepository productCategoryRepository,
                        ProductRepository productRepository) {
        this.salesRepository = salesRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
    }

    public SalesDto createSale(SalesDto salesDto) {
        // Fetch the product category based on categoryId
        ProductCategory category = productCategoryRepository.findById(salesDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Fetch the product based on the category
        List<Product> products = productRepository.findByCategory_CategoryId(category.getCategoryId());

        // Assuming we're selling the first product in the list (you might want to change this logic)
        if (products.isEmpty()) {
            throw new RuntimeException("No product found in this category");
        }

        Product product = products.get(0); // Adjust this based on your business logic

        // Check if the quantity sold is valid
        if (salesDto.getQuantity_sold() > product.getQuantity()) {
            throw new RuntimeException("Insufficient product quantity");
        }

        // Update the product quantity
        product.setQuantity(product.getQuantity() - salesDto.getQuantity_sold());
        productRepository.save(product);

        // Create the sale
        Sales sale = new Sales();
        mapDtoToEntity(salesDto, sale);
        sale.setAmount_sold(salesDto.getAmount_sold());

        sale = salesRepository.save(sale);
        return createSalesDto(sale);
    }

    public List<SalesDto> getAllSales() {
        List<Sales> salesList = salesRepository.findAll();
        return salesList.stream().map(this::createSalesDto).toList();
    }

    public SalesDto getSaleById(Long id) {
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        return createSalesDto(sale);
    }

    public SalesDto updateSale(Long id, SalesDto salesDto) {
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        mapDtoToEntity(salesDto, sale);
        sale.setAmount_sold(salesDto.getAmount_sold());
        sale = salesRepository.save(sale);
        return createSalesDto(sale);
    }

    public void deleteSale(Long id) {
        Sales sale = salesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
        salesRepository.delete(sale);
    }

    private void mapDtoToEntity(SalesDto salesDto, Sales sale) {
        sale.setQuantity_sold(salesDto.getQuantity_sold());
        sale.setPrice(salesDto.getPrice());
        sale.setCustomerName(salesDto.getCustomerName());
        sale.setCustomerPhone(salesDto.getCustomerPhone());
        sale.setCustomerEmail(salesDto.getCustomerEmail());
        sale.setSaleStatus(salesDto.getSaleStatus());
        sale.setPaymentMethod(salesDto.getPaymentMethod());

        // Fetch the category by ID
        if (salesDto.getCategoryId() != null) {
            ProductCategory category = productCategoryRepository.findById(salesDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            sale.setCategory(category);
        }
    }

    private SalesDto createSalesDto(Sales sale) {
        return new SalesDto(
                sale.getSaleId(),
                sale.getCategory() != null ? sale.getCategory().getCategoryId() : null,
                sale.getCategory() != null ? sale.getCategory().getCategoryName() : null,
                sale.getQuantity_sold(),
                sale.getPrice(),
                sale.getAmount_sold(),
                sale.getCustomerName(),
                sale.getCustomerPhone(),
                sale.getCustomerEmail(),
                sale.getSaleStatus(),
                sale.getPaymentMethod()
        );
    }
}

