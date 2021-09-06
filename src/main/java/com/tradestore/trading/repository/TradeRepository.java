package com.tradestore.trading.repository;

import com.tradestore.trading.entities.CompositeTradeId;
import com.tradestore.trading.entities.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, CompositeTradeId> {
    Trade findFirstByTradeIdOrderByVersionDesc(String tradeId);

    List<Trade> findAllByExpiredFalse();
}
