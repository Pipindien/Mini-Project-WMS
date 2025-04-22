package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.CategoryRequest;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;
import com.product.app.entity.Category;
import com.product.app.entity.Product;
import com.product.app.repository.ProductRepository;
import com.product.app.repository.CategoryRepository;
import com.product.app.advice.exception.ProductNotFoundException;
import com.product.app.advice.exception.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private ProductRequest productRequest;

    @Mock
    private ProductResponse productResponse;

    @Mock
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveProduct() throws JsonProcessingException {
        // Arrange
        Category mockCategory = new Category();
        mockCategory.setCategoryId(1L);
        mockCategory.setCategoryType("Fruit");

        Product mockProduct = new Product();
        mockProduct.setProductId(1L);
        mockProduct.setProductName("Product1");
        mockProduct.setProductPrice(100.0);
        mockProduct.setProductRate(5.0);
        mockProduct.setCategoryId(1L);
        mockProduct.setCreatedDate(new Date());

        lenient().when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(mockCategory));
        lenient().when(productRepository.save(any(Product.class))).thenReturn(mockProduct);

        ProductRequest productRequest = ProductRequest.builder()
                .productName("Product1")
                .productPrice(100.0)
                .productRate(5.0)
                .categoryId(1L)
                .build();

        // Act
        ProductResponse result = productService.saveProduct(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Product1", result.getProductName());
        assertEquals(100.0, result.getProductPrice());
        assertEquals(5.0, result.getProductRate());
        assertEquals(1L, result.getCategoryId());
        assertEquals("Fruit", result.getProductCategory());

        verify(productRepository).save(any(Product.class));
    }


    @Test
    void getProductByProductName() throws JsonProcessingException {
        // Arrange
        when(productRepository.findProductByProductName(anyString()))
                .thenReturn(Optional.of(new Product()));

        // Act
        ProductResponse result = productService.getProductByProductName("Product1");

        // Assert
        assertNotNull(result);
        verify(productRepository).findProductByProductName("Product1");
    }

    @Test
    void getAllProducts() throws JsonProcessingException {
        // Arrange
        lenient().when(productRepository.findAllActiveProducts()).thenReturn(List.of(new Product()));

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(productRepository).findAllActiveProducts();
    }

    @Test
    void updateProduct() throws JsonProcessingException {
        // Arrange
        Product existingProduct = new Product();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        ProductRequest productRequest = ProductRequest.builder()
                .productName("Update Product1")
                .productPrice(200.0)
                .productRate(5.0)
                .categoryId(1L)
                .build();
        // Act
        ProductResponse result = productService.updateProduct(1L, productRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct() throws JsonProcessingException {
        // Arrange
        Product product = new Product();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // Act
        String result = productService.deleteProduct(1L);

        // Assert
        assertEquals("Produk berhasil dihapus (soft delete).", result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductsByCategoryId() throws JsonProcessingException {
        // Arrange
        when(productRepository.findProductsByCategoryId(anyLong())).thenReturn(List.of(new Product()));

        // Act
        List<ProductResponse> result = productService.getProductsByCategoryId(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(productRepository).findProductsByCategoryId(1L);
    }

    @Test
    void getProductById() throws JsonProcessingException {
        // Arrange
        when(productRepository.findProductByProductId(anyLong())).thenReturn(Optional.of(new Product()));

        // Act
        ProductResponse result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        verify(productRepository).findProductByProductId(1L);
    }
}
