package com.product.app.repository;

import com.product.app.entity.Category;
import com.product.app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByCategoryType(String categoryType);
    @Query("SELECT c FROM Category c WHERE c.isDeleted = false")
    List<Category> findAll();

}
