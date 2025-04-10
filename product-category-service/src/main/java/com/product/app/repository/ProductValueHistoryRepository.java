package com.product.app.repository;

import com.product.app.entity.ProductValueHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductValueHistoryRepository extends JpaRepository<ProductValueHistory, Long> {
    @Query("SELECT pvh FROM ProductValueHistory pvh WHERE pvh.product.productId = :productId")
    List<ProductValueHistory> findByProductId(@Param("productId") Long productId);

}
