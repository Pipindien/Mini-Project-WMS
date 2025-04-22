package com.product.app.repository;

import com.product.app.entity.Product;
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
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product savedProduct;

    @BeforeEach
    void setUp() {
        Product product = Product.builder()
                .productName("Apel Fuji")
                .productRate(0.08)
                .categoryId(1L)
                .isDeleted(false)
                .build();
        savedProduct = productRepository.save(product);
    }

    @Test
    void findProductByProductName_shouldReturnProduct() {
        Optional<Product> result = productRepository.findProductByProductName("Apel Fuji");

        assertThat(result).isPresent();
        assertThat(result.get().getProductName()).isEqualTo("Apel Fuji");
    }

    @Test
    void findProductByProductId_shouldReturnProduct() {
        Optional<Product> result = productRepository.findProductByProductId(savedProduct.getProductId());

        assertThat(result).isPresent();
        assertThat(result.get().getProductId()).isEqualTo(savedProduct.getProductId());
    }

    @Test
    void findAllActiveProducts_shouldReturnList() {
        List<Product> result = productRepository.findAllActiveProducts();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getIsDeleted()).isFalse();
    }

    @Test
    void findProductsByCategoryId_shouldReturnProductList() {
        List<Product> result = productRepository.findProductsByCategoryId(1L);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
    }
}
