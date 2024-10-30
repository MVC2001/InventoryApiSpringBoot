package com.rabbitmqapp.mytempv1.Repository;

import com.rabbitmqapp.mytempv1.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findBySupplierName(String supplierName);
}