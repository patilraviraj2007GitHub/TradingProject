package com.tradestore.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDto {

    @NotNull
    private String tradeId;

    @NotNull
    private int version;

    @NotNull
    private String counterPartyId;

    @NotNull
    private String bookId;

    @NotNull
    private Date maturityDate;

    private Date createdDate;

    private boolean expired;
}