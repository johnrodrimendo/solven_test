package com.affirm.common.dao.impl;


import com.affirm.common.dao.BufferTransactionDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.BufferTransaction;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sTbn on 17/08/16.
 */

@Repository("BufferTransactionDAO")
public class BufferTransactionDAOImpl extends JsonResolverDAO implements BufferTransactionDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public List<BufferTransaction> getPendingBufferTransactions() throws Exception {

        JSONArray dbJson = queryForObjectTrx("select * from security.get_pending_buffer_transactions()", JSONArray.class);
        if (dbJson == null) {
            return null;
        }

        List<BufferTransaction> transactions = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            BufferTransaction transaction = new BufferTransaction();
            transaction.fillFromDb(dbJson.getJSONObject(i), catalogService);
            transactions.add(transaction);
        }

        return transactions;    }

    @Override
    public void pauseBufferTransactions(int bufferTransactionId, Integer sysUserId, boolean pause) throws Exception {
        queryForObjectTrx("select security.bo_pause_buffer_transaction(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, bufferTransactionId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.BOOLEAN, pause));
    }

    @Override
    public void deleteBufferTransactions(int bufferTransactionId) throws Exception {
        updateTrx("delete from security.tb_buffer_transaction where buffer_transaction_id = ?",
                new SqlParameterValue(Types.INTEGER, bufferTransactionId));
    }

}
