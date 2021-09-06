package com.tradestore.trading.controller;

import com.tradestore.trading.dto.TradeDto;
import com.tradestore.trading.exceptions.InvalidTradeException;
import com.tradestore.trading.exceptions.RecordNotFoundException;
import com.tradestore.trading.services.TradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {
    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @GetMapping()
    public ResponseEntity<List<TradeDto>> getAllTrades() {
        List<TradeDto> list = tradeService.getAllTrades();
        logger.info("Retrieving all trades, Total Trades:{}", list.size());
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{tradeId}/{version}")
    public ResponseEntity<TradeDto> getTradesByTradeVersionId(@PathVariable Map<String, Integer> pathVariables) throws RecordNotFoundException {
        String tradeId = String.valueOf(pathVariables.get("tradeId"));
        int version = Integer.parseInt(String.valueOf(pathVariables.get("version")));
        logger.info("Retrieving Trade with TradeId: {} and Version:{}", tradeId, version);
        TradeDto tradeDetails = tradeService.getTradesByCompositeTradeVersionId(tradeId, version);
        return new ResponseEntity<>(tradeDetails, new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<TradeDto> createOrUpdateTrade(@RequestBody TradeDto trade) throws InvalidTradeException {
        TradeDto updated = tradeService.createOrUpdateTrade(trade);
        logger.info("Trade Created/Updated  TradeId: {} and Version:{}", updated.getTradeId(), updated.getVersion());
        return new ResponseEntity<>(updated, new HttpHeaders(), HttpStatus.OK);

    }
}
