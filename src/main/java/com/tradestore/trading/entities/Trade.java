package com.tradestore.trading.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TBL_TRADE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CompositeTradeId.class)
public class Trade implements Serializable {
    @Id
    @Column(name = "TRADE_ID")
    private String tradeId;

    @Id
    @Column(name = "VERSION")
    @Min(1)
    @Max(1000)
    private int version;

    @Column(name = "COUNTER_PARTY_ID")
    private String counterPartyId;

    @Column(name = "BOOK_ID")
    private String bookId;

    //@Future
    @Column(name = "MATURITY_DATE")

    private Date maturityDate;

    @Column(name = "CREATED_DATE", insertable = false, columnDefinition = "Date default CURRENT_DATE()")
    private Date createdDate;

    @Column(name = "EXPIRED", insertable = false, columnDefinition = "boolean default false")
    private boolean expired;
}