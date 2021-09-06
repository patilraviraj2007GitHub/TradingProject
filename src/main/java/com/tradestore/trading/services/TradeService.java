package com.tradestore.trading.services;

import com.tradestore.trading.dto.TradeDto;
import com.tradestore.trading.exceptions.InvalidTradeException;
import com.tradestore.trading.exceptions.RecordNotFoundException;

import java.util.List;

public interface TradeService {
    List<TradeDto> getAllTrades();
    TradeDto createOrUpdateTrade(TradeDto incomingTrade) throws InvalidTradeException;
    TradeDto getTradesByCompositeTradeVersionId(String tradeId, int version) throws RecordNotFoundException;
}
