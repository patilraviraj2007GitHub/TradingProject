package com.tradestore.trading.controller;

import com.tradestore.trading.TestHelper;
import com.tradestore.trading.dto.TradeDto;
import com.tradestore.trading.exceptions.Errors;
import com.tradestore.trading.exceptions.InvalidTradeException;
import com.tradestore.trading.exceptions.RecordNotFoundException;
import com.tradestore.trading.services.TradeService;
import jdk.jfr.Description;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TradeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TradeService tradeService;

    private TradeDto tradeDto;
    private List<TradeDto> tradeDtoList;
    private RecordNotFoundException recordNotFoundException;
    private InvalidTradeException invalidTradeExceptionMaturityDateIssue;
    private InvalidTradeException invalidTradeExceptionLowerVersionIssue;

    @InjectMocks
    private TradeController tradeController;

    @BeforeEach
    public void setup(){
        var date1 = new Date();
        tradeDto = new TradeDto("T1",1,"CP-1","B1",date1 ,date1,false);
        tradeDtoList = new ArrayList<>();
        tradeDtoList.add(tradeDto);
        recordNotFoundException = new RecordNotFoundException(Errors.RECORD_ISSUE.getMessage());
        invalidTradeExceptionMaturityDateIssue = new InvalidTradeException(Errors.MATURITY_DATE_ISSUE.getMessage());
        invalidTradeExceptionLowerVersionIssue = new InvalidTradeException(Errors.VERSION_ISSUE.getMessage());
        mockMvc = MockMvcBuilders.standaloneSetup(tradeController).build();
    }

    @SneakyThrows
    @Test
    @Description("Find all Trades ")
    public void getAllTrades() {
        when(tradeService.getAllTrades()).thenReturn(tradeDtoList);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/trades").
                        contentType(MediaType.APPLICATION_JSON).
                        content(TestHelper.asJsonString(tradeDto))).
                andDo(MockMvcResultHandlers.print());
        verify(tradeService).getAllTrades();
        verify(tradeService,times(1)).getAllTrades();
    }

    @Test
    @Description("Find specific Trade")
    public void getSingleTradeDetails() throws Exception {
        when(tradeService.getTradesByCompositeTradeVersionId(tradeDto.getTradeId(),tradeDto.getVersion())).thenReturn(tradeDto);
        mockMvc.perform(get("/api/v1/trades/T1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(tradeDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Description("Create or Update Trade")
    public void createOrUpdateTrade() throws Exception{
        when(tradeService.createOrUpdateTrade(any())).thenReturn(tradeDto);
        mockMvc.perform(post("/api/v1/trades").
                        contentType(MediaType.APPLICATION_JSON).
                        content(TestHelper.asJsonString(tradeDto))).
                andExpect(status().isOk());
        verify(tradeService,times(1)).createOrUpdateTrade(any());
    }

    @Test
    @Description("Retrieving specific Trade, but Not found exception thrown")
    public void getSingleTradeDetails_Failed_As_No_Record_Found() throws Exception {
        when(tradeService.getTradesByCompositeTradeVersionId(tradeDto.getTradeId(),tradeDto.getVersion())).thenThrow(recordNotFoundException);
        mockMvc.perform(get("/api/v1/trades/T1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestHelper.asJsonString(tradeDto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Description("Trying to add or update trade with non future maturity date, throwing exception")
    public void createOrUpdateTrade_Trade_Having_Past_MaturityDate_Should_Throw_Error() throws Exception{
        when(tradeService.createOrUpdateTrade(any())).thenThrow(invalidTradeExceptionMaturityDateIssue);
        mockMvc.perform(post("/api/v1/trades").
                        contentType(MediaType.APPLICATION_JSON).
                        content(TestHelper.asJsonString(tradeDto))).
                andExpect(status().isBadRequest());
        verify(tradeService,times(1)).createOrUpdateTrade(any());
    }

    @Test
    @Description("Trying to add or update trade with lower version, throwing exception")
    public void createOrUpdateTrade_Trade_Having_Lower_Version_Should_Throw_Error() throws Exception{
        when(tradeService.createOrUpdateTrade(any())).thenThrow(invalidTradeExceptionLowerVersionIssue);
        mockMvc.perform(post("/api/v1/trades").
                        contentType(MediaType.APPLICATION_JSON).
                        content(TestHelper.asJsonString(tradeDto))).
                andExpect(status().isBadRequest());
        verify(tradeService,times(1)).createOrUpdateTrade(any());
    }
}
