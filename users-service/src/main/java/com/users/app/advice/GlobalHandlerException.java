package com.users.app.advice;

import com.users.app.advice.dto.ErrorResponse;
import com.users.app.advice.exception.*;
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

    @ExceptionHandler(PhoneAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handlePhoneAlreadyRegisteredException(PhoneAlreadyRegisteredException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Phone Already Register", ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyRegisteredException(EmailAlreadyRegisteredException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Email Already Register", ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Username Not Found", ex.getMessage());
    }

    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ErrorResponse> handlePasswordInvalidException(PasswordInvalidException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Password Invalid", ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Token Expired", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        logger.error("Unhandled exception:", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }
}
