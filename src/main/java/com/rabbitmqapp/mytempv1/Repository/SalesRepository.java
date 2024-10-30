package com.rabbitmqapp.mytempv1.Repository;

import com.rabbitmqapp.mytempv1.Entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesRepository  extends JpaRepository<Sales, Long> {

}