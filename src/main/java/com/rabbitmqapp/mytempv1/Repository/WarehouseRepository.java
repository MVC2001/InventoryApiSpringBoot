package com.rabbitmqapp.mytempv1.Repository;

import com.rabbitmqapp.mytempv1.Entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseName(String warehouseName);
}
