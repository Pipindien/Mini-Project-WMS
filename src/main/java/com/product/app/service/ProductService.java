package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.constant.GeneralConstant;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;
import com.product.app.entity.Category;
import com.product.app.entity.Product;
import com.product.app.repository.CategoryRepository;
import com.product.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditTrailsService auditTrailsService;

    @Autowired
    private CategoryRepository categoryRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public ProductResponse saveProduct(ProductRequest productRequest) throws JsonProcessingException {
        // Ambil categoryId berdasarkan kategori yang dipilih oleh pengguna
        Category category = categoryRepository.findCategoryByType(productRequest.getProductCategory())
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .productValue(productRequest.getProductValue())
                .categoryId(category.getCategoryId())  // Menyimpan ID kategori langsung
                .createdDate(new Date())
                .build();

        Product savedProduct = productRepository.save(product);

        ProductResponse response = ProductResponse.builder()
                .productId(savedProduct.getProductId())
                .productName(savedProduct.getProductName())
                .productValue(savedProduct.getProductValue())
                .categoryId(savedProduct.getCategoryId())  // Menampilkan ID kategori
                .createdDate(savedProduct.getCreatedDate())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_SAVE,
                mapper.writeValueAsString(productRequest), mapper.writeValueAsString(response),
                "Insert Product Save");

        return response;
    }

    public ProductResponse getProductByProductName(String productName) throws JsonProcessingException {
        Optional<Product> product = productRepository.findProductByProductName(productName);

        if (product.isEmpty()) {
            throw new RuntimeException("Produk tidak ditemukan");
        }

        ProductResponse productResponse = ProductResponse.builder()
                .productId(product.get().getProductId())
                .productName(product.get().getProductName())
                .productValue(product.get().getProductValue())
                .categoryId(product.get().getCategoryId())  // Menampilkan ID kategori
                .createdDate(product.get().getCreatedDate())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_GET_PRODUCT_NAME,
                mapper.writeValueAsString(""), mapper.writeValueAsString(productResponse),
                "Get Product Name Success");

        return productResponse;
    }

    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) throws JsonProcessingException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        // Ambil categoryId berdasarkan kategori yang dipilih oleh pengguna
        Category category = categoryRepository.findCategoryByType(productRequest.getProductCategory())
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        ProductResponse oldData = ProductResponse.builder()
                .productId(existingProduct.getProductId())
                .productName(existingProduct.getProductName())
                .productValue(existingProduct.getProductValue())
                .categoryId(existingProduct.getCategoryId())  // Menampilkan ID kategori lama
                .createdDate(existingProduct.getCreatedDate())
                .build();

        // Update produk
        existingProduct.setProductName(productRequest.getProductName());
        existingProduct.setProductValue(productRequest.getProductValue());
        existingProduct.setCategoryId(category.getCategoryId());  // Mengubah ke ID kategori baru

        Product updatedProduct = productRepository.save(existingProduct);

        ProductResponse response = ProductResponse.builder()
                .productId(updatedProduct.getProductId())
                .productName(updatedProduct.getProductName())
                .productValue(updatedProduct.getProductValue())
                .categoryId(updatedProduct.getCategoryId())  // Menampilkan ID kategori baru
                .createdDate(updatedProduct.getCreatedDate())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_UPDATE,
                mapper.writeValueAsString(oldData), mapper.writeValueAsString(response),
                "Update Product Success");

        return response;
    }

    public String deleteProduct(Long productId) throws JsonProcessingException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        ProductResponse oldData = ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productValue(product.getProductValue())
                .categoryId(product.getCategoryId())  // Menampilkan ID kategori yang akan dihapus
                .createdDate(product.getCreatedDate())
                .build();

        productRepository.delete(product);

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE,
                mapper.writeValueAsString(oldData), "", "Delete Product Success");

        return "Produk berhasil dihapus.";
    }
}
