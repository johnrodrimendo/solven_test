package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.OriginatorDAO;
import com.affirm.common.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class OriginatorDAOImpl extends JsonResolverDAO implements OriginatorDAO {



    @Autowired
    private CatalogService catalogService;

    @Override
    public void registerAffiliator(String name, String ruc, String email, String phoneNumber, String ubigeoId, Integer bankId, String accountNumber, String generatedPassword, String encryptedPassword) throws Exception {
        queryForObjectTrx("select * from originator.register_affiliator(?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.VARCHAR, ubigeoId),
                new SqlParameterValue(Types.INTEGER, bankId),
                new SqlParameterValue(Types.VARCHAR, accountNumber),
                new SqlParameterValue(Types.VARCHAR, generatedPassword),
                new SqlParameterValue(Types.VARCHAR, encryptedPassword));
    }


}
