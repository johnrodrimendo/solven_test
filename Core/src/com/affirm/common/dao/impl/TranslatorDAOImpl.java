package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.TranslatorDAO;
import org.json.JSONObject;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/**
 * Created by miberico on 12/04/17.
 */
@Repository
public class TranslatorDAOImpl extends JsonResolverDAO implements TranslatorDAO{

    //BANTOTAL
    public static final int BANTOTAL_PRODUCT_ID = 47;
    public static final int BANTOTAL_COUNTRY_ID = 48;
    public static final int BANTOTAL_DOCUMENT_TYPE_ID = 49;
    public static final int BANTOTAL_MARITAL_STATUS_ID = 50;
    public static final int BANTOTAL_DEPARTMENT_ID = 51;
    public static final int BANTOTAL_OCCUPATION_ID = 52;
    public static final int BANTOTAL_PROVINCE_ID = 53;
    public static final int BANTOTAL_ZONE_ID = 54;
    public static final int BANTOTAL_STREET_TYPE_ID = 55;


    @Override
    public String translate(Integer entityId, Integer tableId, String valorSolven, String valorEntidad) throws Exception{
        return queryForObjectTrx("select * from support.get_transalation_value(?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, tableId),
                new SqlParameterValue(Types.VARCHAR, valorSolven),
                new SqlParameterValue(Types.VARCHAR, valorEntidad));
    }

    @Override
    public JSONObject translateLocality(Integer entityId, Long localityId) {
        return queryForObjectTrx("select * from support.get_locality_translation(?,?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.BIGINT, localityId));
    }

}
