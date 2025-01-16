package com.dailycodework.dreamshops.repository;

import com.dailycodework.dreamshops.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryName(String category, Pageable pageable);
    Long countByBrandAndName(String brand, String name);

    boolean existsByNameAndBrand(String name, String brand);
    Page<Product> findByBrand(String brand, Pageable pageable);
    Page<Product> findByCategoryAndBrand(String category, String brand, Pageable pageable);
    Page<Product> findProductsByName(String name, Pageable pageable);
    Page<Product> findProductsByBrandAndName(String brand, String name, Pageable pageable);
}
