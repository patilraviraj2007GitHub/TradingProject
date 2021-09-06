package com.tradestore.trading.repository;

import com.tradestore.trading.entities.CompositeTradeId;
import com.tradestore.trading.entities.Trade;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
/***
 * Integration Tests
 */
public class TradeRepositoryTests {
    @Autowired
    private TradeRepository tradeRepository;
    private Trade trade;
    private Trade tradeV1;
    private Trade tradeV2;
    private Trade tradeToBeUpdated;
    private CompositeTradeId compositeTradeId;

    @BeforeEach
    public void setUp() {
        var date1 = new Date();
        trade = new Trade("T9", 1, "CP-1", "B1", date1, date1, false);
        tradeV1 = new Trade("T9", 1, "CP-1", "B1", date1, date1, false);
        tradeV2 = new Trade("T9", 2, "CP-1", "B1", date1, date1, false);
        tradeToBeUpdated = new Trade("T9", 1, "CP-2", "B1", date1, date1, false);
        compositeTradeId = new CompositeTradeId("T9", 1);
    }

    @Test
    @Description("Add New Trade ")
    public void addNewTradeAndRetrieveToVerify_Should_Be_Successful() {
        tradeRepository.save(trade);
        Trade retrieveTrade = tradeRepository.findById(compositeTradeId).get();
        assertEquals(trade.getTradeId(), retrieveTrade.getTradeId());
        assertEquals(1, retrieveTrade.getVersion());
    }

    @Test
    @Description("Find Higher Version of specific Trade ")
    public void findFirstByTradeIdOrderByVersionDesc_Should_Be_Successful() {
        tradeRepository.save(tradeV1);
        tradeRepository.save(tradeV2);
        Trade retrieveTrade = tradeRepository.findFirstByTradeIdOrderByVersionDesc(tradeV1.getTradeId());
        assertEquals(trade.getTradeId(), retrieveTrade.getTradeId());
        assertEquals(2, retrieveTrade.getVersion());
    }

    @Test
    @Description("Find all expired Trades ")
    public void findAllByExpiredFalse_Should_Be_Successful() {
        tradeRepository.save(trade);
        List<Trade> retrieveTrades = tradeRepository.findAllByExpiredFalse();
        assertEquals(9, retrieveTrades.size());
        for (Trade trade : retrieveTrades) {
            assertEquals(false, trade.isExpired());
        }
    }


}
