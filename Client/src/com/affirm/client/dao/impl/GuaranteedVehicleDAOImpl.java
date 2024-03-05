package com.affirm.client.dao.impl;

import com.affirm.client.dao.GuaranteedVehicleDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.catalog.GuaranteedVehicle;
import org.json.JSONArray;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GuaranteedVehicleDAOImpl extends JsonResolverDAO implements GuaranteedVehicleDAO {


    @Override
    public List<GuaranteedVehicle> getGuaranteedVehicles() {

        JSONArray jsonArray = queryForObjectTrx("select * from vehicle.get_ct_guaranteed_vehicle()", JSONArray.class);

        if (jsonArray == null) {
            return null;
        }

        List<GuaranteedVehicle> guaranteedVehicleList=new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            GuaranteedVehicle guaranteedVehicle = new GuaranteedVehicle();
            guaranteedVehicle.fillFromDb(jsonArray.getJSONObject(i));
            guaranteedVehicleList.add(guaranteedVehicle);
        }

        return guaranteedVehicleList;
    }

    @Override
    public void saveGuaranteedVehicles(String vehiclesJSON){
        queryForObjectTrx("select * from vehicle.register_guaranteed_vehicles(?)", String.class,
                new SqlParameterValue(Types.OTHER, vehiclesJSON));
    }
}
