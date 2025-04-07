package com.product.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;
import com.product.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest productRequest) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(productRequest));
    }

    @GetMapping("/{productName}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productName) throws JsonProcessingException {
        return ResponseEntity.ok(productService.getProductByProductName(productName));
    }
    @PutMapping("/update/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId,
                                                         @RequestBody ProductRequest productRequest) throws JsonProcessingException {
        ProductResponse updatedProduct = productService.updateProduct(productId, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) throws JsonProcessingException {
        String message = productService.deleteProduct(productId);
        return ResponseEntity.ok(message);
    }
}
