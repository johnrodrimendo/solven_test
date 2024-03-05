package com.affirm.client.service.impl;

import com.affirm.client.dao.SampleDAO;
import com.affirm.client.service.SampleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service
public class SampleServiceImpl implements SampleService {

    private static Logger logger = Logger.getLogger(SampleServiceImpl.class);

    @Autowired
    SampleDAO sampleDAO;

    @Override
    public Map<Integer, String> dbOperations(int rows) throws Exception {
        int i = 0;
        long start, end, end2, start2;
        start = System.currentTimeMillis();
        System.out.println("start:" + start);

        Map<Integer, String> map = sampleDAO.selectAll();
        end = System.currentTimeMillis();
        System.out.println("end:" + end);
        System.out.println("diff:" + (end - start));//0.8

        System.out.println("start2:" + start);
        start2 = System.currentTimeMillis();
        for (Map.Entry entry: map.entrySet()) {
            if(rows == i) {
                System.out.println( "rows igual a i:" + i);
                break;
            }
            sampleDAO.updateToUpperCase(entry);
            sampleDAO.delete(entry);
            sampleDAO.insert(entry);
            ++i;

        }
        end2 = System.currentTimeMillis();
        System.out.println("end2:" + end2);
        System.out.println("diff2:" + (end2 - start2));//1.4

        return map;
    }

    @Override
    public void logTransactionMilis(long milis) throws Exception {
        sampleDAO.logTransactionMilis(milis);
    }

}