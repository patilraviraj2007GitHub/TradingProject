package com.tradestore.trading.exceptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Value(value = "${trade.unhandled.exception.message}")
    private String unhandledMessage;

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity invalidTradeException(BusinessException businessException) {
        return new ResponseEntity<Object>(businessException.getErrorResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> nonHandledException(Exception exception) {
        return new ResponseEntity<>(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}