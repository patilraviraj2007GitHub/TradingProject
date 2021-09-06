package com.tradestore.trading.exceptions;

import lombok.Getter;

@Getter
public enum Errors {
    MATURITY_DATE_ISSUE(100, "Maturity Date is not valid, it should be greater then today's date"),
    VERSION_ISSUE(101, "Version is lesser than existing trade versions"),
    RECORD_ISSUE(102, "Requested trade record not found"),
    VALIDATION_ISSUE(103, "Validation Error: Verify the request payload details");

    private final int errorCode;
    private final String message;

    Errors(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}
