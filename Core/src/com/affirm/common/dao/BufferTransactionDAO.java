package com.affirm.common.dao;

import com.affirm.common.model.transactional.BufferTransaction;

import java.util.List;

/**
 * Created by sTbn on 17/08/16.
 */
public interface BufferTransactionDAO {

    List<BufferTransaction> getPendingBufferTransactions() throws Exception;

    void pauseBufferTransactions(int bufferTransactionId, Integer sysUserId, boolean pause) throws Exception;
    void deleteBufferTransactions(int bufferTransactionId) throws Exception;


}
