package com.product.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.product.app.advice.exception.ProductNotFoundException;
import com.product.app.dto.ProductRequest;
import com.product.app.dto.ProductResponse;
import com.product.app.entity.Product;
import com.product.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        try {
            List<ProductResponse> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (JsonProcessingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
