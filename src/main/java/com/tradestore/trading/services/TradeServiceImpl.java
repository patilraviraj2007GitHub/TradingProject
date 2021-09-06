package com.tradestore.trading.services;

import com.tradestore.trading.dto.TradeDto;
import com.tradestore.trading.entities.CompositeTradeId;
import com.tradestore.trading.entities.Trade;
import com.tradestore.trading.exceptions.Errors;
import com.tradestore.trading.exceptions.InvalidTradeException;
import com.tradestore.trading.exceptions.RecordNotFoundException;
import com.tradestore.trading.repository.TradeRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);
    final TradeRepository tradeRepository;
    private final ModelMapper mapper;

    public TradeServiceImpl(TradeRepository tradeRepository, ModelMapper mapper) {
        this.tradeRepository = tradeRepository;
        this.mapper = mapper;
    }

    public List<TradeDto> getAllTrades() {
        List<Trade> tradeList = tradeRepository.findAll();
        if (tradeList != null) {
            if (tradeList.size() > 0) {
                return mapList(tradeList, TradeDto.class);
            }
        }
        logger.info("No trades found!!");
        return new ArrayList<>();
    }

    public TradeDto createOrUpdateTrade(TradeDto incomingTrade) throws InvalidTradeException {
        if (incomingTrade.getMaturityDate().after(new Date())) {
            if (!checkHigherVersion(incomingTrade)) {
                return insertOrUpdate(incomingTrade);
            } else {
                logger.error(Errors.VERSION_ISSUE.getMessage());
                throw new InvalidTradeException(Errors.VERSION_ISSUE.getMessage());
            }
        } else {
            logger.error(Errors.MATURITY_DATE_ISSUE.getMessage());
            throw new InvalidTradeException(Errors.MATURITY_DATE_ISSUE.getMessage());
        }
    }

    private TradeDto insertOrUpdate(TradeDto incomingTrade) {
        var compositeKey = new CompositeTradeId(incomingTrade.getTradeId(), incomingTrade.getVersion());
        var existingTrades = tradeRepository.findById(compositeKey);
        if (existingTrades.isPresent()) {
            logger.info("Trade already exists, overwriting with new details!!");
            Trade tradeBeingUpdated = existingTrades.get();
            tradeBeingUpdated.setBookId(incomingTrade.getBookId());
            tradeBeingUpdated.setCounterPartyId(incomingTrade.getCounterPartyId());
            tradeBeingUpdated.setMaturityDate(incomingTrade.getMaturityDate());
            return mapToDTO(tradeRepository.save(tradeBeingUpdated));
        } else {
            logger.info("Its New Trade, adding it to store!!");
            var trade = mapToEntity(incomingTrade);
            var savedTrade = tradeRepository.save(trade);
            return mapToDTO(savedTrade);
        }
    }

    private boolean checkHigherVersion(TradeDto incomingTrade) {
        var existingTradeMaxVersion = tradeRepository.findFirstByTradeIdOrderByVersionDesc(incomingTrade.getTradeId());
        if (existingTradeMaxVersion == null) return false;
        return existingTradeMaxVersion.getVersion() > incomingTrade.getVersion();
    }

    public TradeDto getTradesByCompositeTradeVersionId(String tradeId, int version) throws RecordNotFoundException {
        var compositeTradeVersionId = new CompositeTradeId(tradeId, version);
        Optional<Trade> existingTrades = tradeRepository.findById(compositeTradeVersionId);
        if (existingTrades.isPresent()) {
            Trade trade = existingTrades.get();
            return mapToDTO(trade);
        }
        throw new RecordNotFoundException(Errors.RECORD_ISSUE.getMessage());
    }

    public void updateTradeAsExpired() {
        var trades = tradeRepository.findAllByExpiredFalse();
        if (trades == null) return;

        for (Trade trade : trades) {
            if (!trade.getMaturityDate().after(new Date())) {
                trade.setExpired(true);
                tradeRepository.save(trade);
                logger.info("Trade is marked as expired, Trade:{},Version:{}", trade.getTradeId(), trade.getVersion());
            }
        }
    }

    private TradeDto mapToDTO(Trade tradeEntity) {
        return mapper.map(tradeEntity, TradeDto.class);
    }

    private Trade mapToEntity(TradeDto tradeDTO) {
        return mapper.map(tradeDTO, Trade.class);
    }

    private <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(element -> mapper.map(element, targetClass))
                .collect(Collectors.toList());
    }
}
