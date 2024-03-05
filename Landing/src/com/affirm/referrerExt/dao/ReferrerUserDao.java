package com.affirm.referrerExt.dao;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.referrerExt.model.ReferrerUser;
import com.affirm.referrerExt.model.ReferrerUserStatistics;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository("referrerUserDao")
public class ReferrerUserDao extends JsonResolverDAO {

    @Autowired
    private CatalogService catalogService;

    public ReferrerUser getReferrerUser(Integer docTypeId, String docNumber) {
        JSONArray dbJson = queryForObjectTrx("select * from users.get_referrer_user(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, docTypeId),
                new SqlParameterValue(Types.VARCHAR, docNumber));

        if (dbJson == null || dbJson.length() == 0)
            return null;

        ReferrerUser referrerUser = new ReferrerUser();
        referrerUser.fillFromDb(dbJson.getJSONObject(0), catalogService);
        return referrerUser;
    }

    public ReferrerUser registerReferrerUser(Integer docTypeId, String docNumber, String email, String phoneNumber) {
        JSONObject dbJson = queryForObjectTrx("select * from users.register_referrer_user(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, docTypeId),
                new SqlParameterValue(Types.VARCHAR, docNumber),
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.VARCHAR, phoneNumber));

        ReferrerUser referrerUser = new ReferrerUser();
        referrerUser.setDocumentType(catalogService.getIdentityDocumentType(docTypeId));
        referrerUser.setDocumentNumber(docNumber);
        referrerUser.setEmail(email);
        referrerUser.setPhoneNumber(phoneNumber);
        return referrerUser;
    }

    public ReferrerUserStatistics getReferrerUserStatistics(Integer personId) {
        JSONObject dbJson = queryForObjectTrx("select users.get_referrer_user_statistics(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));

        ReferrerUserStatistics referrerUserStatistics = new ReferrerUserStatistics();
        referrerUserStatistics.fillFromDb(dbJson);
        return referrerUserStatistics;
    }

    public void updateReferralUser(ReferrerUser referrerUser) {
        updateTrx("update users.tb_referrer_user set " +
                        "name = ?," +
                        "first_surname = ?," +
                        "email = ?," +
                        "phone_number = ?," +
                        "bank_id = ?," +
                        "bank_account_number = ?," +
                        "cci = ? " +
                        "where referrer_user_id = ?",
                new SqlParameterValue(Types.VARCHAR, referrerUser.getName()),
                new SqlParameterValue(Types.VARCHAR, referrerUser.getFirstSurname()),
                new SqlParameterValue(Types.VARCHAR, referrerUser.getEmail()),
                new SqlParameterValue(Types.VARCHAR, referrerUser.getPhoneNumber()),
                new SqlParameterValue(Types.INTEGER, referrerUser.getBank() != null ? referrerUser.getBank().getId() : null),
                new SqlParameterValue(Types.VARCHAR, referrerUser.getBankAccountNumber()),
                new SqlParameterValue(Types.VARCHAR, referrerUser.getCci()),
                new SqlParameterValue(Types.INTEGER, referrerUser.getId()));
    }

}
