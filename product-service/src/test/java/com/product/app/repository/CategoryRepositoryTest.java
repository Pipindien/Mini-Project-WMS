package com.product.app.repository;

import com.product.app.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category savedCategory;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setCategoryType("Fruit");
        savedCategory = categoryRepository.save(category);
    }

    @Test
    void findCategoryByCategoryType_shouldReturnCategory() {
        Optional<Category> result = categoryRepository.findCategoryByCategoryType("Fruit");

        assertThat(result).isPresent();
        assertThat(result.get().getCategoryType()).isEqualTo("Fruit");
    }

    @Test
    void findAll_shouldReturnCategoryList() {
        List<Category> result = categoryRepository.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategoryType()).isEqualTo(savedCategory.getCategoryType());
    }
}
