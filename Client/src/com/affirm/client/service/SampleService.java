package com.affirm.client.service;

import java.util.Map;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface SampleService {

    Map<Integer, String> dbOperations(int rows) throws Exception;

    void logTransactionMilis(long milis) throws Exception;

}
