package com.tradestore.trading.services;

import com.tradestore.trading.dto.TradeDto;
import com.tradestore.trading.exceptions.BusinessException;

import java.util.List;

public interface TradeService {
    List<TradeDto> getAllTrades();
    TradeDto createOrUpdateTrade(TradeDto incomingTrade) throws BusinessException;
    TradeDto getTradesByCompositeTradeVersionId(String tradeId, int version) throws BusinessException;
}
