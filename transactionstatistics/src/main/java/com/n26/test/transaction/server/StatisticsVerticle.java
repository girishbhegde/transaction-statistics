package com.n26.test.transaction.server;

import com.n26.test.transaction.service.TransactionStatisticsService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Girish on 03-05-2017.
 */
public class StatisticsVerticle extends AbstractVerticle{
    private static Logger LOGGER = LoggerFactory.getLogger(StatisticsVerticle.class);
    public static final String STATISTICS_VERTICLE = "STATISTICS_VERTICLE";
    private TransactionStatisticsService service = TransactionStatisticsService.getInstance();

    @Override
    public void start(){
        MessageConsumer<String> consumer = vertx.eventBus().consumer(STATISTICS_VERTICLE);
        consumer.handler(message -> {
            LOGGER.debug("new statistics request received");
            message.reply(Json.encode(getStatistics()));
        });
    }


    public JsonObject getStatistics(){
        return service.getStatistics();
    }
}
