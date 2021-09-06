package com.tradestore.trading.schedulerjob;

import com.tradestore.trading.services.TradeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfiguration.class);
    final TradeServiceImpl tradeService;

    public SchedulingConfiguration(TradeServiceImpl tradeService) {
        this.tradeService = tradeService;
    }

    @Scheduled(initialDelay = 10000L, fixedDelayString = "${tradeExpiredJob.schedule}")
    void markTradesExpired() {
        logger.info("Checking for expired Trades: {}", new Date());
        tradeService.updateTradeAsExpired();
    }
}
