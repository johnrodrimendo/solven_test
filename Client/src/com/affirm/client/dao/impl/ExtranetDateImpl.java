package com.affirm.client.dao.impl;

import com.affirm.client.dao.ExtranetDateDAO;
import com.affirm.client.model.ExtranetDate;
import com.affirm.common.dao.JsonResolverDAO;
import org.json.JSONArray;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("extranetDateDao")
public class ExtranetDateImpl extends JsonResolverDAO implements ExtranetDateDAO {


    @Override
    public List<ExtranetDate> getExtranetDates(Date startDate, Date endDate) {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_appointment_schedules(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate));

        if (dbArray == null) {
            return null;
        }

        List<ExtranetDate> extranetDates = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExtranetDate extranetDate = new ExtranetDate();
            extranetDate.fillFromDb(dbArray.getJSONObject(i));
            extranetDates.add(extranetDate);
        }
        return extranetDates;
    }
}
