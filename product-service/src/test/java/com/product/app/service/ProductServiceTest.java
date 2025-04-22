package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;
import com.product.app.entity.Category;
import com.product.app.entity.Product;
import com.product.app.repository.ProductRepository;
import com.product.app.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuditTrailsService auditTrailsService;

    private final Date now = new Date();

    @Test
    void saveProduct_success() throws JsonProcessingException {
        // Arrange
        Category mockCategory = Category.builder()
                .categoryId(1L)
                .categoryType("Fruit")
                .createdDate(now)
                .build();

        Product mockSavedProduct = Product.builder()
                .productId(1L)
                .productName("Product1")
                .productPrice(100.0)
                .productRate(5.0)
                .categoryId(1L)
                .createdDate(now)
                .isDeleted(false)
                .build();

        ProductRequest productRequest = ProductRequest.builder()
                .productName("Product1")
                .productPrice(100.0)
                .productRate(5.0)
                .categoryId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(any(Product.class))).thenReturn(mockSavedProduct);

        // Act
        ProductResponse result = productService.saveProduct(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Product1", result.getProductName());
        assertEquals(100.0, result.getProductPrice());
        assertEquals(5.0, result.getProductRate());
        assertEquals(1L, result.getCategoryId());
        assertEquals(now, result.getCreatedDate());

        verify(productRepository).save(any(Product.class));
        verify(auditTrailsService).logsAuditTrails(anyString(), anyString(), anyString(), eq("Insert Product Save"));
    }

    @Test
    void getProductByProductName_success() throws JsonProcessingException {
        // Arrange
        Product mockProduct = Product.builder()
                .productId(1L)
                .productName("Product1")
                .productPrice(100.0)
                .productRate(4.5)
                .categoryId(1L)
                .createdDate(now)
                .build();

        Category mockCategory = Category.builder()
                .categoryId(1L)
                .categoryType("Fruit")
                .build();

        when(productRepository.findProductByProductName("Product1")).thenReturn(Optional.of(mockProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        // Act
        ProductResponse result = productService.getProductByProductName("Product1");

        // Assert
        assertNotNull(result);
        assertEquals("Product1", result.getProductName());
        assertEquals("Fruit", result.getProductCategory());
        verify(productRepository).findProductByProductName("Product1");
    }

    @Test
    void getAllProducts_success() throws JsonProcessingException {
        Product mockProduct = Product.builder()
                .productId(1L)
                .productName("Product1")
                .productPrice(50.0)
                .productRate(4.0)
                .categoryId(2L)
                .createdDate(now)
                .build();

        Category mockCategory = Category.builder()
                .categoryId(2L)
                .categoryType("Snack")
                .build();

        when(productRepository.findAllActiveProducts()).thenReturn(List.of(mockProduct));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(mockCategory));

        List<ProductResponse> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Snack", result.get(0).getProductCategory());
    }

    @Test
    void updateProduct_success() throws JsonProcessingException {
        Product existingProduct = Product.builder()
                .productId(1L)
                .productName("Old Product")
                .productPrice(90.0)
                .productRate(4.0)
                .categoryId(1L)
                .createdDate(now)
                .build();

        Category mockCategory = Category.builder()
                .categoryId(2L)
                .categoryType("Snack")
                .build();

        ProductRequest updateRequest = ProductRequest.builder()
                .productName("Updated Product")
                .productPrice(150.0)
                .productRate(5.0)
                .categoryId(2L)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        ProductResponse response = productService.updateProduct(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Product", response.getProductName());
        assertEquals(150.0, response.getProductPrice());
        assertEquals(5.0, response.getProductRate());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_success() throws JsonProcessingException {
        Product product = Product.builder()
                .productId(1L)
                .productName("ProductToDelete")
                .productPrice(100.0)
                .productRate(3.0)
                .categoryId(1L)
                .createdDate(now)
                .isDeleted(false)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        String result = productService.deleteProduct(1L);

        assertEquals("Produk berhasil dihapus (soft delete).", result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductsByCategoryId_success() throws JsonProcessingException {
        Product product = Product.builder()
                .productId(1L)
                .productName("Product1")
                .productPrice(120.0)
                .productRate(4.0)
                .categoryId(5L)
                .createdDate(now)
                .build();

        Category category = Category.builder()
                .categoryId(5L)
                .categoryType("Drinks")
                .build();

        when(productRepository.findProductsByCategoryId(5L)).thenReturn(List.of(product));
        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));

        List<ProductResponse> result = productService.getProductsByCategoryId(5L);

        assertFalse(result.isEmpty());
        assertEquals("Drinks", result.get(0).getProductCategory());
    }

    @Test
    void getProductById_success() throws JsonProcessingException {
        Product product = Product.builder()
                .productId(2L)
                .productName("SingleProduct")
                .productPrice(300.0)
                .productRate(5.0)
                .categoryId(6L)
                .createdDate(now)
                .build();

        Category category = Category.builder()
                .categoryId(6L)
                .categoryType("Electronics")
                .build();

        when(productRepository.findProductByProductId(2L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(6L)).thenReturn(Optional.of(category));

        ProductResponse result = productService.getProductById(2L);

        assertNotNull(result);
        assertEquals("Electronics", result.getProductCategory());
        verify(productRepository).findProductByProductId(2L);
    }
}
