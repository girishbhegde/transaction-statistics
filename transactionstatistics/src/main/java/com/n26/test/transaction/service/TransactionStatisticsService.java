package com.n26.test.transaction.service;

import com.n26.test.transaction.exception.TransactionRunTimeException;
import com.n26.test.transaction.model.Transaction;
import com.n26.test.transaction.model.TransactionData;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

/**
 * Created by Girish on 02-05-2017.
 */
public class TransactionStatisticsService {
    private static Logger LOGGER = LoggerFactory.getLogger(TransactionStatisticsService.class);
    private static TransactionStatisticsService statisticsService = new TransactionStatisticsService();
    private volatile LinkedList<TransactionData> dataLinkedList = new LinkedList<TransactionData>();
    public int size = 60;
    private volatile double sum;
    private volatile double min = Double.MAX_VALUE;
    private volatile long minSetTime = 0;
    private volatile double max = Double.MIN_VALUE;
    private volatile long maxSetTime = 0;
    private volatile long count;

    public TransactionStatisticsService(){
        init();
    }

    private void init(){
        //initialize the data list with 60 elements one per second
        for(int i=0; i<size; i++){
            dataLinkedList.addFirst(new TransactionData());
        }
    }

    /**
     * Insert new data node at beginning (index 0)
     * @param data
     */
    public void insertNew(TransactionData data){
        dataLinkedList.addFirst(data);
        TransactionData prev = dataLinkedList.pollLast();
        sum-=prev.getSum();
        count-=prev.getCount();
    }

    /**
     * post a transaction
     * @param txn
     * @param index
     */
    public void postTransaction(Transaction txn, int index){
        LOGGER.debug("post transaction at queue index {}", index);
        if(index<size) {
            TransactionData data = dataLinkedList.get(index);
            if(data == null){
                LOGGER.debug("Error in post transaction. Null data received", index);
                throw new TransactionRunTimeException("Data is null");
            }
            data.incrementCount(1);
            count++;
            data.incrementSum(txn.getAmount());
            sum+=txn.getAmount();
            if(data.getMin()>txn.getAmount()){
                data.setMin(txn.getAmount());
                if(data.getMin() < min){
                    LOGGER.debug("setting min to {}", data.getMin());
                    min = data.getMin();
                    minSetTime = System.currentTimeMillis();
                }
            }
            if(data.getMax()<txn.getAmount()){
                data.setMax(txn.getAmount());
                if(data.getMax() > max){
                    LOGGER.debug("setting max to {}", data.getMax());
                    max = data.getMax();
                    maxSetTime = System.currentTimeMillis();
                }
            }
        }
    }

    /**
     * get the statistics for last 60 secs
     * @return
     */
    public JsonObject getStatistics() {
        LOGGER.debug("get statistics called");
        JsonObject response = new JsonObject();
        response.put("count", count);
        response.put("sum", sum);
        double avg = 0;
        if(count!=0){
            avg = sum/count;
        }
        response.put("avg", avg);

        double maxVal = 0;
        if(max != Double.MIN_VALUE){
            maxVal = max;
        }
        response.put("max", maxVal);

        double minVal = 0;
        if(min != Double.MAX_VALUE){
            minVal = min;
        }
        response.put("min", minVal);
        return response;
    }

    public static TransactionStatisticsService getInstance() {
        return statisticsService;
    }

    public long getMinSetTime() {
        return minSetTime;
    }

    public long getMaxSetTime() {
        return maxSetTime;
    }

    /**
     * update min from data among list elements
     */
    public void updateMinOnly(){
        double tempMin = Double.MAX_VALUE;
        long tempMinSetTime = 0;
        for(int i=0; i<dataLinkedList.size(); i++){
            if(dataLinkedList.get(i).getMin() < tempMin){
                tempMin = dataLinkedList.get(i).getMin();
                tempMinSetTime = System.currentTimeMillis() - i*1000;
            }
        }
        min = tempMin;
        minSetTime = tempMinSetTime;
    }

    /**
     * update max from data among list elements
     */
    public void updateMaxOnly(){
        double tempMax = Double.MIN_VALUE;
        long tempMaxSetTime = 0;
        for(int i=0; i<dataLinkedList.size(); i++){
            if(dataLinkedList.get(i).getMax() > tempMax){
                tempMax = dataLinkedList.get(i).getMax();
                tempMaxSetTime = System.currentTimeMillis() - i*1000;
            }
        }
        max = tempMax;
        maxSetTime = tempMaxSetTime;
    }

    /**
     * update min and max from data among list elements
     */
    public void updateMinMax(){
        double tempMin = Double.MAX_VALUE;
        double tempMax = Double.MIN_VALUE;
        long tempMinSetTime = 0;
        long tempMaxSetTime = 0;
        for(int i=0; i<dataLinkedList.size(); i++){
            if(dataLinkedList.get(i).getMin() < tempMin){
                tempMin = dataLinkedList.get(i).getMin();
                tempMinSetTime = System.currentTimeMillis() - i*1000;
            }
            if(dataLinkedList.get(i).getMax() > tempMax){
                tempMax = dataLinkedList.get(i).getMax();
                tempMaxSetTime = System.currentTimeMillis() - i*1000;
            }
        }
        min = tempMin;
        minSetTime = tempMinSetTime;
        max = tempMax;
        maxSetTime = tempMaxSetTime;
    }
}
