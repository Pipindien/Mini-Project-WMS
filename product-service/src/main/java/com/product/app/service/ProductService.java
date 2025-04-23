package com.product.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse saveProduct(ProductRequest productRequest) throws JsonProcessingException;

    ProductResponse getProductByProductName(String productName) throws JsonProcessingException;

    List<ProductResponse> getAllProducts() throws JsonProcessingException;

    ProductResponse updateProduct(Long productId, ProductRequest productRequest) throws JsonProcessingException;

    String deleteProduct(Long productId) throws JsonProcessingException;

    List<ProductResponse> getProductsByCategoryId(Long categoryId) throws JsonProcessingException;

    ProductResponse getProductById(Long productId) throws JsonProcessingException;

}
