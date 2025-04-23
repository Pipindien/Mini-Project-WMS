package com.transaction.app.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.transaction.app.advice.dto.ErrorResponse;
import com.transaction.app.advice.exception.*;
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

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGoalNotFoundException(GoalNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Goal Not Found", ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Product Not Found", ex.getMessage());
    }

    @ExceptionHandler(InvalidProductPriceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductPriceException(InvalidProductPriceException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Harga produk tidak valid", ex.getMessage());
    }

    @ExceptionHandler(InvalidTransactionAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionAmountException(InvalidTransactionAmountException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Dana tidak cukup untuk membeli minimal 1 unit produk.", ex.getMessage());
    }

    @ExceptionHandler(TrxNumberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrxNumberNotFoundException(TrxNumberNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Trx Number Not Found", ex.getMessage());
    }

    @ExceptionHandler(PhoneNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePhoneNotFoundException(PhoneNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Phone Number Not Found", ex.getMessage());
    }


    @ExceptionHandler(LotException.class)
    public ResponseEntity<ErrorResponse> handleLotException(LotException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Lot Not Found", ex.getMessage());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        logger.error("JSON processing error: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "JSON Processing Error", ex.getOriginalMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Argument", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        logger.error("Unhandled exception:", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("General exception caught:", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected Error", ex.getMessage());
    }
}
