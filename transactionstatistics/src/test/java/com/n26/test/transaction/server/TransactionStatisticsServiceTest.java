package com.n26.test.transaction.server;

import static org.junit.Assert.*;

import com.n26.test.transaction.model.Transaction;
import com.n26.test.transaction.service.TransactionStatisticsService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Girish on 02-05-2017.
 */
public class TransactionStatisticsServiceTest {
    private TransactionStatisticsService testClass;

    @Before
    public void init(){
        testClass = TransactionStatisticsService.getInstance();
    }

    @Test
    public void testInit(){
        assertTrue(0 == testClass.getStatistics().getDouble("min").doubleValue());
        assertTrue(0 == testClass.getStatistics().getDouble("max").doubleValue());
    }

    @Test
    public void testPostTransaction(){
        Transaction txn = new Transaction();
        txn.setAmount(100);
        long time = System.currentTimeMillis();
        txn.setTimeStamp(time);
        int index = (int)((time - txn.getTimeStamp())/1000);
        System.out.println(index);
        testClass.postTransaction(txn, index);
        assertTrue(100 == testClass.getStatistics().getDouble("min").doubleValue());
        assertTrue(100 == testClass.getStatistics().getDouble("max").doubleValue());
        assertTrue(100 == testClass.getStatistics().getDouble("sum").doubleValue());
        assertTrue(100 == testClass.getStatistics().getDouble("avg").doubleValue());
        assertTrue(1 == testClass.getStatistics().getDouble("count").doubleValue());
    }
}
