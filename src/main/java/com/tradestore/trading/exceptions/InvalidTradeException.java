package com.tradestore.trading.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidTradeException extends RuntimeException {
    private String message;

    public InvalidTradeException(String message) {
        super(message);
        this.message = message;
    }

    public InvalidTradeException() {
    }
}
