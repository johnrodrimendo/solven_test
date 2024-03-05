package com.affirm.client.dao;

import java.util.Map;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface SampleDAO {

    void insert(Map.Entry<String, String> row) throws Exception;

    void updateToUpperCase(Map.Entry<String, String> row) throws Exception;

    void delete(Map.Entry<String, String> row) throws Exception;

    Map<Integer, String> selectAll() throws Exception;

    void logTransactionMilis(long milis) throws Exception;
}
