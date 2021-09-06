package com.tradestore.trading.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    private ErrorResponse errorResponse;

    public BusinessException(ErrorResponse errorResponse) {
        super(errorResponse.getErrorMessage());
        this.errorResponse = errorResponse;
    }

    public BusinessException() {
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
