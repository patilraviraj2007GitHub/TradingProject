package com.tradestore.trading.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompositeTradeId implements Serializable {

    @Column(name = "TRADE_ID")
    private String tradeId;

    @Column(name = "VERSION")
    private int version;
}