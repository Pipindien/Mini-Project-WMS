package com.product.app.repository;

import com.product.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    @Query("SELECT p FROM Product p WHERE p.productName = :productName AND p.isDeleted = false")
    Optional<Product> findProductByProductName(@Param("productName") String productName);

    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findProductByProductId(@Param("productId") Long productId);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId AND p.isDeleted = false")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);
}
