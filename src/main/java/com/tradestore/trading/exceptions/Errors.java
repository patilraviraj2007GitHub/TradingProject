package com.tradestore.trading.exceptions;

import lombok.Getter;

@Getter
public enum Errors {
    VERSION_ISSUE("Version is lesser than existing trade versions"),
    MATURITY_DATE_ISSUE("Maturity Date is not valid, it should be greater then today's date"),
    RECORD_ISSUE("Request trade record not found");

    private final String message;

    Errors(String message) {
        this.message = message;
    }

}
