package com.rabbitmqapp.mytempv1.Service;

import com.rabbitmqapp.mytempv1.Dto.ProductCategoryDto;
import com.rabbitmqapp.mytempv1.Entity.ProductCategory;
import com.rabbitmqapp.mytempv1.Repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public ProductCategoryDto createCategory(ProductCategoryDto categoryDto) {
        ProductCategory category = new ProductCategory();
        category.setCategoryName(categoryDto.getCategoryName());
        category.setCategoryDescription(categoryDto.getCategoryDescription());
        productCategoryRepository.save(category);
        categoryDto.setCategoryId(category.getCategoryId());
        return categoryDto;
    }

    public ProductCategoryDto getCategoryById(Long id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return new ProductCategoryDto(category.getCategoryId(), category.getCategoryName(), category.getCategoryDescription());
    }

    public List<ProductCategoryDto> getAllCategories() {
        return productCategoryRepository.findAll().stream()
                .map(category -> new ProductCategoryDto(category.getCategoryId(), category.getCategoryName(), category.getCategoryDescription()))
                .collect(Collectors.toList());
    }

    public ProductCategoryDto updateCategory(Long id, ProductCategoryDto categoryDto) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setCategoryName(categoryDto.getCategoryName());
        category.setCategoryDescription(categoryDto.getCategoryDescription());
        productCategoryRepository.save(category);
        return categoryDto;
    }

    public void deleteCategory(Long id) {
        ProductCategory category = productCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        productCategoryRepository.delete(category);
    }
}
