package com.tradestore.trading.service;

import com.tradestore.trading.dto.TradeDto;
import com.tradestore.trading.entities.Trade;
import com.tradestore.trading.exceptions.Errors;
import com.tradestore.trading.exceptions.InvalidTradeException;
import com.tradestore.trading.repository.TradeRepository;
import com.tradestore.trading.services.TradeServiceImpl;
import jdk.jfr.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTests {


    TradeServiceImpl tradeService;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        tradeService = new TradeServiceImpl(tradeRepository, modelMapper);
    }

    @Test
    @Description("Retrieve all Trades")
    public void getAllTrades() {
        var date1 = new Date();
        List<TradeDto> tradeDtoList = new ArrayList<>();
        var tradeDto1 = new TradeDto("T9", 1, "CP-1", "B1", date1, date1, false);
        var tradeDto2 = new TradeDto("T9", 2, "CP-1", "B1", date1, date1, false);
        tradeDtoList.add(tradeDto1);
        tradeDtoList.add(tradeDto2);

        List<Trade> tradeList = new ArrayList<>();
        var trade1 = new Trade("T9", 1, "CP-1", "B1", date1, date1, false);
        var trade2 = new Trade("T9", 2, "CP-1", "B1", date1, date1, false);
        tradeList.add(trade1);
        tradeList.add(trade2);
        when(tradeRepository.findAll()).thenReturn(tradeList);
        when(modelMapper.map(any(), any())).thenReturn(tradeDtoList);

        List<TradeDto> trades = tradeService.getAllTrades();
        assertEquals(trades.size(), tradeDtoList.size());
    }

    @Test
    @Description("Retrieve specific Trade")
    public void getSingleTrades() {
        var date1 = new Date();
        var tradeDto1 = new TradeDto("T9", 1, "CP-1", "B1", date1, date1, false);

        List<Trade> tradeList = new ArrayList<>();
        var trade1 = new Trade("T9", 1, "CP-1", "B1", date1, date1, false);
        tradeList.add(trade1);

        when(tradeRepository.findById(any())).thenReturn(tradeList.stream().findFirst());
        when(modelMapper.map(any(), any())).thenReturn(tradeDto1);

        var trades = tradeService.getTradesByCompositeTradeVersionId("T9", 1);
        assertEquals(trades.getTradeId(), tradeDto1.getTradeId());
    }

    @Test
    @Description("Adding new Trade")
    public void add_Single_Trade_Should_be_Successful() {
        var date1 = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DATE, 2);
        var date2 = c.getTime();

        var tradeDto2 = new TradeDto("10", 2, "CP-1", "B1", date2, date1, false);

        List<Trade> tradeList = new ArrayList<>();
        var trade2 = new Trade("T10", 2, "CP-1", "B1", date2, date1, false);

        Mockito.lenient().when(tradeRepository.findById(any())).thenReturn(Optional.empty());
        Mockito.lenient().when(tradeRepository.findFirstByTradeIdOrderByVersionDesc(any())).thenReturn(null);
        Mockito.lenient().when(modelMapper.map(tradeDto2, Trade.class)).thenReturn(trade2);
        Mockito.lenient().when(tradeRepository.save(any())).thenReturn(trade2);
        Mockito.lenient().when(modelMapper.map(trade2,TradeDto.class)).thenReturn(tradeDto2);
        var trade = tradeService.createOrUpdateTrade(tradeDto2);
        assertEquals(trade.getTradeId(), tradeDto2.getTradeId());
    }

    @Test
    @Description("Updating trade trade ")
    public void update_Single_Trade_Should_be_Successful() {
        var date1 = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DATE, 2);
        var date2 = c.getTime();

        var tradeDto1 = new TradeDto("T9", 1, "CP-1", "B1", date2, date1, false);

        List<Trade> tradeList = new ArrayList<>();
        var trade1 = new Trade("T9", 1, "CP-1", "B1", date2, date1, false);
        tradeList.add(trade1);

        Mockito.lenient().when(tradeRepository.findById(any())).thenReturn(Optional.of(trade1));
        Mockito.lenient().when(tradeRepository.findFirstByTradeIdOrderByVersionDesc(any())).thenReturn(trade1);
        Mockito.lenient().when(modelMapper.map(any(), any())).thenReturn(tradeDto1);
        Mockito.lenient().when(tradeRepository.save(any())).thenReturn(trade1);
        var trades = tradeService.createOrUpdateTrade(tradeDto1);
        assertEquals(trades.getTradeId(), tradeDto1.getTradeId());
    }

    @Test()
    @Description("Check while adding lower version, it should fail with Exception")
    public void add_Single_Trade_With_Lower_Version_Should_Fail() {
        var date1 = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DATE, 2);
        var date2 = c.getTime();

        var tradeDto1 = new TradeDto("T9", 1, "CP-1", "B1", date2, date1, false);

        List<Trade> tradeList = new ArrayList<>();
        var trade2 = new Trade("T9", 2, "CP-1", "B1", date2, date1, false);
        tradeList.add(trade2);
        Mockito.lenient().when(tradeRepository.findById(any())).thenReturn(tradeList.stream().findFirst());
        Mockito.lenient().when(tradeRepository.findFirstByTradeIdOrderByVersionDesc(any())).thenReturn(trade2);
        Mockito.lenient().when(modelMapper.map(any(), any())).thenReturn(tradeDto1);
        assertThatThrownBy(() -> {
            tradeService.createOrUpdateTrade(tradeDto1);
        }).isInstanceOf(InvalidTradeException.class).hasMessage(Errors.VERSION_ISSUE.getMessage());
    }

    @Test()
    @Description("Check Maturity Date date is future date it should fail with Exception if date is in past")
    public void add_Single_Trade_With_Past_Maturity_Date_Should_Fail() {
        var date1 = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date1);
        c.add(Calendar.DATE, -2);
        var date2 = c.getTime();

        var tradeDto1 = new TradeDto("T9", 1, "CP-1", "B1", date2, date1, false);

        List<Trade> tradeList = new ArrayList<>();
        var trade2 = new Trade("T9", 2, "CP-1", "B1", date2, date1, false);
        tradeList.add(trade2);
        Mockito.lenient().when(tradeRepository.findById(any())).thenReturn(tradeList.stream().findFirst());
        Mockito.lenient().when(tradeRepository.findFirstByTradeIdOrderByVersionDesc(any())).thenReturn(trade2);
        Mockito.lenient().when(modelMapper.map(any(), any())).thenReturn(tradeDto1);
        assertThatThrownBy(() -> {
            tradeService.createOrUpdateTrade(tradeDto1);
        }).isInstanceOf(InvalidTradeException.class).hasMessage(Errors.MATURITY_DATE_ISSUE.getMessage());
    }
}
