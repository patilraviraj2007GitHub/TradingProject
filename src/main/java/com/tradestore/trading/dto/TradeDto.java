package com.tradestore.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDto {

    @NotNull
    @NotBlank
    private String tradeId;

    @NotNull
    @Min(value = 1)
    private int version;

    @NotNull
    @NotBlank
    private String counterPartyId;

    @NotNull
    @NotBlank
    private String bookId;

    @NotNull
    private Date maturityDate;

    private Date createdDate;

    private boolean expired;
}