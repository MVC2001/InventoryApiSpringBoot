package com.rabbitmqapp.mytempv1.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sales")
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleId;



    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(nullable = false)
    private Integer quantity_sold;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double amount_sold;

    @Column(nullable = true)
    private String customerName;

    @Column(nullable = true)
    private String customerPhone;

    @Column(nullable = true)
    private String customerEmail;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Pending'")
    private String saleStatus = "Pending";

    @Column(nullable = false)
    private String paymentMethod;  // New payment method field
}
