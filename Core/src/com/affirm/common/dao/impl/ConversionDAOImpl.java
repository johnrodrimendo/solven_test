package com.affirm.common.dao.impl;

import com.affirm.common.dao.ConversionDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.Conversion;
import org.json.JSONArray;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository("conversionDAO")
public class ConversionDAOImpl extends JsonResolverDAO implements ConversionDAO {

    @Override
    public void registerPixelConversion(Integer loanApplicationId, String pixelEntity, String instance) {
        queryForObjectTrx("select * from credit.register_pixel_conversion(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, pixelEntity),
                new SqlParameterValue(Types.VARCHAR, instance)
        );
    }

    @Override
    public List<Conversion> getConversions(Integer loanApplicationId) {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_pixel_conversion(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));

        if (jsonArray == null) {
            return new ArrayList<>();
        }

        List<Conversion> conversions = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Conversion conversion = new Conversion();
            conversion.fillFromDb(jsonArray.getJSONObject(i));
            conversions.add(conversion);
        }
        return conversions;
    }
}