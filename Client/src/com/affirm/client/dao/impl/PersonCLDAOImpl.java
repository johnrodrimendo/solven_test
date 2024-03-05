package com.affirm.client.dao.impl;

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.model.form.LoanApplicationStep1Form;
import com.affirm.client.model.form.LoanApplicationStep2Form;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.text.SimpleDateFormat;

/**
 * Created by jrodriguez on 26/09/16.
 */

@Repository
public class PersonCLDAOImpl extends JsonResolverDAO implements PersonCLDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public void updatePersonalInformation(int personId, LoanApplicationStep1Form form) throws Exception {
        queryForObjectTrx("select * from person.upd_personal_information(?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, form.getName()),
                new SqlParameterValue(Types.VARCHAR, form.getFirstSurname()),
                new SqlParameterValue(Types.VARCHAR, form.getLastSurname()),
                new SqlParameterValue(Types.INTEGER, form.getDocType()),
                new SqlParameterValue(Types.VARCHAR, form.getDocNumber()),
                new SqlParameterValue(Types.DATE, form.getBirthday() != null ? new SimpleDateFormat("dd/MM/yyyy").parse(form.getBirthday()) : null),
                new SqlParameterValue(Types.CHAR, form.getGender()),
                new SqlParameterValue(Types.VARCHAR, form.getCountryCode()),
                new SqlParameterValue(Types.VARCHAR, form.getPhoneNumber()),
                new SqlParameterValue(Types.VARCHAR, form.getEmail()),
                new SqlParameterValue(Types.INTEGER, form.getMaritalStatus()),
                new SqlParameterValue(Types.INTEGER, form.getCompanionDocType()),
                new SqlParameterValue(Types.VARCHAR, form.getCompanionDocNumber()),
                new SqlParameterValue(Types.VARCHAR, form.getCompanionName()),
                new SqlParameterValue(Types.VARCHAR, form.getCompanionFirstSurname()),
                new SqlParameterValue(Types.VARCHAR, form.getCompanionLastSurname()),
                new SqlParameterValue(Types.INTEGER, form.getNationality()),
                new SqlParameterValue(Types.VARCHAR, form.getCityCode()),
                new SqlParameterValue(Types.VARCHAR, form.getLandline()),
                new SqlParameterValue(Types.BOOLEAN, form.getPep()));
    }

    @Override
    public void updateAddressInformation(int personId, LoanApplicationStep2Form form) throws Exception {
        queryForObjectTrx("select * from person.upd_address_information(?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.VARCHAR, form.getDepartment() + form.getProvince() + form.getDistrict()),
                new SqlParameterValue(Types.INTEGER, form.getStreetType()),
                new SqlParameterValue(Types.VARCHAR, form.getStreetName()),
                new SqlParameterValue(Types.VARCHAR, form.getStreetNumber()),
                new SqlParameterValue(Types.VARCHAR, form.getInterior()),
                new SqlParameterValue(Types.VARCHAR, form.getDetail()));
    }

    @Override
    public void updateOcupationalInformation(Integer personId, Integer ocupationaInformationNumber,
                                             Integer activityType, String ShareHolderRuc, String company, String shareholderShareholding,
                                             String shareholderResultU12M, Integer monthlyNetIncome, Integer monthlyGrossIncome,
                                             Integer voucherType, Integer belonging, Integer pensionPayer, String employmentTime, String phoneNumber) throws Exception {
        queryForObjectTrx("select * from person.upd_ocupational_information(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.SMALLINT, ocupationaInformationNumber),
                new SqlParameterValue(Types.INTEGER, activityType),
                new SqlParameterValue(Types.VARCHAR, ShareHolderRuc),
                new SqlParameterValue(Types.VARCHAR, company),
                new SqlParameterValue(Types.VARCHAR, shareholderShareholding),
                new SqlParameterValue(Types.VARCHAR, shareholderResultU12M != null ? shareholderResultU12M + "" : null),
                new SqlParameterValue(Types.INTEGER, monthlyNetIncome),
                new SqlParameterValue(Types.INTEGER, monthlyGrossIncome),
                new SqlParameterValue(Types.INTEGER, voucherType),
                new SqlParameterValue(Types.INTEGER, belonging),
                new SqlParameterValue(Types.INTEGER, pensionPayer),
                new SqlParameterValue(Types.VARCHAR, employmentTime),
                new SqlParameterValue(Types.VARCHAR, phoneNumber));
    }

    @Override
    public void registerVehicleDiscovery(String email, Integer vehicleId){
        queryForObjectTrx("select * from vehicle.register_vechicle_unlocking(?, ?)", String.class,
                new SqlParameterValue(Types.VARCHAR, email),
                new SqlParameterValue(Types.INTEGER, vehicleId));
    }

}
