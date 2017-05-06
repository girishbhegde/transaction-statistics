package com.n26.test.transaction.server;

import com.n26.test.transaction.exception.TransactionRunTimeException;
import com.n26.test.transaction.model.Transaction;
import com.n26.test.transaction.service.TransactionStatisticsService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Girish on 03-05-2017.
 */
public class TransactionVerticle extends AbstractVerticle{
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionVerticle.class);

    public static final String TRANSACTION_VERTICLE = "TRANSACTION_VERTICLE";
    private TransactionStatisticsService service = TransactionStatisticsService.getInstance();

    @Override
    public void start(){
        MessageConsumer<String> consumer = vertx.eventBus().consumer(TRANSACTION_VERTICLE);
        consumer.handler(message -> {
            String status = "NO_CONTENT";
            LOGGER.info("new transaction received");
            String jsonInput = message.body();
            Transaction txn = Json.decodeValue(jsonInput, Transaction.class);

            try {
                if(postTransaction(txn)){
                    status = "SUCCESS";
                }
            }catch (TransactionRunTimeException e){
                LOGGER.error("Exception: {}", e.getMessage(), e);
                message.fail(500, e.getMessage());
                return;
            }
            message.reply(status);
        });
    }

    public boolean postTransaction(Transaction txn){
        long currentTime =System.currentTimeMillis();
        int index;
        //if data is older than 60 seconds return 204
        //if data is with future timestamp return 204
        //if valid data, find index and insert
        //index is number from 0 to 59 with 0 as latest data
        if(currentTime - txn.getTimeStamp() >= service.size * 1000){
            LOGGER.warn("Old data");
            return false;
        }else if(currentTime - txn.getTimeStamp() < 0){
            LOGGER.warn("Invalid data");
            return false;
        }
        else{
            index = (int)((currentTime - txn.getTimeStamp())/1000);
        }
        service.postTransaction(txn, index);
        return true;
    }
}
