package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.app.advice.exception.CategoryNotFoundException;
import com.product.app.advice.exception.ProductNotFoundException;
import com.product.app.constant.GeneralConstant;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;
import com.product.app.entity.Category;
import com.product.app.entity.Product;
import com.product.app.repository.CategoryRepository;
import com.product.app.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        try {
        Category category = categoryRepository.findCategoryByCategoryType(productRequest.getProductCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .productPrice(productRequest.getProductPrice())
                .productRate(productRequest.getProductRate())
                .categoryId(category.getCategoryId())
                .createdDate(new Date())
                .isDeleted(false)
                .build();
        Product savedProduct = productRepository.save(product);

        ProductResponse response = ProductResponse.builder()
                .productId(savedProduct.getProductId())
                .productName(savedProduct.getProductName())
                .productPrice(savedProduct.getProductPrice())
                .productRate(savedProduct.getProductRate())
                .categoryId(savedProduct.getCategoryId())
                .createdDate(savedProduct.getCreatedDate())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_SAVE,
                mapper.writeValueAsString(productRequest), mapper.writeValueAsString(response),
                "Insert Product Save");

        return response;
        } catch (CategoryNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_SAVE,
                    mapper.writeValueAsString(productRequest), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Save Product");
            throw ex;
        }
    }

    public ProductResponse getProductByProductName(String productName) throws JsonProcessingException {
        try {
            Optional<Product> product = productRepository.findProductByProductName(productName);

            if (product.isEmpty()) {
                throw new ProductNotFoundException("Product Not Found");
            }

            ProductResponse productResponse = ProductResponse.builder()
                    .productId(product.get().getProductId())
                    .productName(product.get().getProductName())
                    .productPrice(product.get().getProductPrice())
                    .productRate(product.get().getProductRate())
                    .categoryId(product.get().getCategoryId())
                    .createdDate(product.get().getCreatedDate())
                    .build();

            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_GET_PRODUCT_NAME,
                    mapper.writeValueAsString(productName), mapper.writeValueAsString(productResponse),
                    "Get Product Name Success");

            return productResponse;
        } catch (ProductNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_GET_PRODUCT_NAME,
                    mapper.writeValueAsString(productName), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Get Product Name");
            throw ex;
        }
    }

    public List<ProductResponse> getAllProducts() throws JsonProcessingException {
        List<Product> products = productRepository.findAllActiveProducts();

        if (products.isEmpty()) {
            String message = "No Products Found";
            auditTrailsService.logsAuditTrails(
                    GeneralConstant.LOG_ACVITIY_GET_ALL_PRODUCT,
                    mapper.writeValueAsString("Get All Products"),
                    mapper.writeValueAsString(message),
                    "Failed Get All Products"
            );
            throw new ProductNotFoundException(message);
        }

        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products) {
            ProductResponse response = ProductResponse.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productPrice(product.getProductPrice())
                    .productRate(product.getProductRate())
                    .categoryId(product.getCategoryId())
                    .createdDate(product.getCreatedDate())
                    .build();
            productResponses.add(response);
        }

        auditTrailsService.logsAuditTrails(
                GeneralConstant.LOG_ACVITIY_GET_ALL_PRODUCT,
                mapper.writeValueAsString("Get All Products"),
                mapper.writeValueAsString(productResponses),
                "Get All Products Success"
        );

        return productResponses;
    }


    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) throws JsonProcessingException {
        try {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

        Category category = categoryRepository.findCategoryByCategoryType(productRequest.getProductCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Category Not Found"));

        ProductResponse oldData = ProductResponse.builder()
                .productId(existingProduct.getProductId())
                .productName(existingProduct.getProductName())
                .productPrice(existingProduct.getProductPrice())
                .productRate(existingProduct.getProductRate())
                .categoryId(existingProduct.getCategoryId())
                .createdDate(existingProduct.getCreatedDate())
                .build();

        existingProduct.setProductName(productRequest.getProductName());
        existingProduct.setProductPrice(productRequest.getProductPrice());
        existingProduct.setProductRate(productRequest.getProductRate());
        existingProduct.setCategoryId(category.getCategoryId());
        existingProduct.setIsDeleted(false);
        Product updatedProduct = productRepository.save(existingProduct);

        ProductResponse response = ProductResponse.builder()
                .productId(updatedProduct.getProductId())
                .productName(updatedProduct.getProductName())
                .productPrice(updatedProduct.getProductPrice())
                .productRate(updatedProduct.getProductRate())
                .categoryId(updatedProduct.getCategoryId())
                .createdDate(updatedProduct.getCreatedDate())
                .build();

        auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_UPDATE,
                mapper.writeValueAsString(oldData), mapper.writeValueAsString(response),
                "Update Product Success");

        return response;
        } catch (ProductNotFoundException | CategoryNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_UPDATE,
                    mapper.writeValueAsString(productRequest), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Update Product");
            throw ex;
        }
    }

    public String deleteProduct(Long productId) throws JsonProcessingException {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Product Not Found"));

            ProductResponse oldData = ProductResponse.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productPrice(product.getProductPrice())
                    .productRate(product.getProductRate())
                    .categoryId(product.getCategoryId())
                    .createdDate(product.getCreatedDate())
                    .build();

            product.setIsDeleted(true);
            productRepository.save(product);

            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE,
                    mapper.writeValueAsString(oldData), "", "Soft Delete Product Success");

            return "Produk berhasil dihapus (soft delete).";
        } catch (ProductNotFoundException ex) {
            auditTrailsService.logsAuditTrails(GeneralConstant.LOG_ACVITIY_DELETE,
                    mapper.writeValueAsString(productId), mapper.writeValueAsString(ex.getMessage()),
                    "Failed Delete Product");
            throw ex;
        }
    }

}
