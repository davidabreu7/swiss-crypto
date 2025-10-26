package com.swisspost.swisscrypto.scheduler;

import com.swisspost.swisscrypto.service.PriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PriceUpdateScheduler {

    private static final Logger log = LoggerFactory.getLogger(PriceUpdateScheduler.class);

    private final PriceUpdateService priceUpdateService;

    public PriceUpdateScheduler(PriceUpdateService priceUpdateService) {
        this.priceUpdateService = priceUpdateService;
    }

    @Scheduled(fixedDelayString = "${swiss-crypto.price-update.interval:900000}")
    public void scheduledPriceUpdate() {
        log.info("Starting scheduled price update");

        try {
            priceUpdateService.updateAllAssetPrices();
            log.info("Scheduled price update completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled price update", e);
        }
    }
}
