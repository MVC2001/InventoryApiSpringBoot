package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.ProductDto;
import com.rabbitmqapp.mytempv1.Entity.Product;
import com.rabbitmqapp.mytempv1.Entity.Supplier;
import com.rabbitmqapp.mytempv1.Entity.ProductCategory;
import com.rabbitmqapp.mytempv1.Repository.ProductRepository;
import com.rabbitmqapp.mytempv1.Repository.SupplierRepository;
import com.rabbitmqapp.mytempv1.Repository.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          SupplierRepository supplierRepository,
                          ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product product = new Product();
        mapDtoToEntity(productDto, product);
        product = productRepository.save(product);
        return createProductDto(product);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return createProductDto(product);
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::createProductDto)
                .toList();
    }

    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        mapDtoToEntity(productDto, product);
        product = productRepository.save(product);
        return createProductDto(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    private void mapDtoToEntity(ProductDto productDto, Product product) {
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setQuantity(productDto.getQuantity());
        product.setPrice(productDto.getPrice());

        if (productDto.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDto.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            product.setSupplier(supplier);
        }

        if (productDto.getCategoryId() != null) {
            ProductCategory category = productCategoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }
    }

    private ProductDto createProductDto(Product product) {
        return new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getCategory() != null ? product.getCategory().getCategoryId() : null,
                product.getCategory() != null ? product.getCategory().getCategoryName() : null,
                product.getDescription(),
                product.getQuantity(),
                product.getPrice(),
                product.getSupplier() != null ? product.getSupplier().getSupplierId() : null
        );
    }
}

