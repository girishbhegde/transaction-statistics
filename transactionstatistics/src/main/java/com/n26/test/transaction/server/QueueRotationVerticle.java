package com.n26.test.transaction.server;

import com.n26.test.transaction.model.TransactionData;
import com.n26.test.transaction.service.TransactionStatisticsService;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Girish on 03-05-2017.
 */
public class QueueRotationVerticle extends AbstractVerticle{
    private static Logger LOGGER = LoggerFactory.getLogger(QueueRotationVerticle.class);

    private TransactionStatisticsService service = TransactionStatisticsService.getInstance();

    @Override
    public void start(){
        //start a periodic job that is triggered at every 1 sec
        vertx.setPeriodic(1000, handle->{
            LOGGER.info("queue rotation started");
            //insert a new node into list
            service.insertNew(new TransactionData());
            updateMinMax();
        });
    }

    private void updateMinMax() {
        if(System.currentTimeMillis() - service.getMinSetTime() > service.size * 1000 && System.currentTimeMillis() - service.getMaxSetTime() > service.size * 1000){
            LOGGER.debug("updating min and max");
            service.updateMinMax();
        } else if(System.currentTimeMillis() - service.getMinSetTime() > service.size * 1000){
            LOGGER.debug("updating min");
            service.updateMinOnly();
        } else if(System.currentTimeMillis() - service.getMaxSetTime() > service.size * 1000){
            LOGGER.debug("updating max");
            service.updateMaxOnly();
        }
    }
}
