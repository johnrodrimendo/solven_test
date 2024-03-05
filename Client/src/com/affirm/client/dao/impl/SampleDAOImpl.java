package com.affirm.client.dao.impl;

import com.affirm.client.dao.SampleDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jrodriguez on 26/09/16.
 */

@Repository
public class SampleDAOImpl extends JsonResolverDAO implements SampleDAO {


    @Override
    public void insert(Map.Entry<String, String> row) throws Exception {
         updateTrx("insert into public.tb_sample (sample_id, description) values (?, ?)",
                 new SqlParameterValue(Types.INTEGER, row.getKey()),
                 new SqlParameterValue(Types.VARCHAR, row.getValue()));
    }

    @Override
    public void updateToUpperCase(Map.Entry<String, String> row) throws Exception {
        String newVal = new String(row.getValue()).toUpperCase();
        updateTrx("UPDATE public.tb_sample set description = ? where sample_id = ?",
                new SqlParameterValue(Types.VARCHAR, newVal),
                new SqlParameterValue(Types.INTEGER, row.getKey()));
    }

    @Override
    public void delete(Map.Entry<String, String> row) throws Exception {
        updateTrx("delete from public.tb_sample where sample_id = ?",
                new SqlParameterValue(Types.INTEGER, row.getKey()));
    }

    @Override
    public Map<Integer, String> selectAll() throws Exception {
        JSONArray jArray = queryForObjectTrx("SELECT * " +
                "FROM (SELECT array_to_json(array_agg(row_to_json(tx))) " +
                "     FROM (" +
                "            SELECT * " +
                "            FROM public.tb_sample order by 1 " +
                "          ) AS tx) as tb", JSONArray.class);
        Map<Integer, String> data = new HashMap<>();
        if (jArray != null) {
            for (int i=0;i<jArray.length();i++){
                data.put(JsonUtil.getIntFromJson(jArray.getJSONObject(i), "sample_id", null), jArray.getJSONObject(i).getString("description"));
            }
        }
        return data;
    }

    @Override
    public void logTransactionMilis(long milis) throws Exception {
         updateTrx("INSERT INTO support.lg_requests (transaction_milis) VALUES (?)",
                 new SqlParameterValue(Types.INTEGER, milis));
    }

}
