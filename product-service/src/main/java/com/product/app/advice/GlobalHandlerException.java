package com.product.app.advice;

import com.product.app.advice.dto.ErrorResponse;
import com.product.app.advice.exception.CategoryNotFoundException;
import com.product.app.advice.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GlobalHandlerException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalHandlerException.class);

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                error,
                message,
                new Date()
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Category Not Found", ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Product Not Found", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        logger.error("Unhandled exception:", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }
}
