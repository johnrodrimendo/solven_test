package com.affirm.client.dao;

import com.affirm.client.model.form.LoanApplicationStep1Form;
import com.affirm.client.model.form.LoanApplicationStep2Form;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface PersonCLDAO {

    void updatePersonalInformation(int personId, LoanApplicationStep1Form form) throws Exception;

    void updateAddressInformation(int personId, LoanApplicationStep2Form form) throws Exception;

    void updateOcupationalInformation(Integer personId, Integer ocupationaInformationNumber,
                                      Integer activityType, String ShareHolderRuc, String company, String shareholderShareholding,
                                      String shareholderResultU12M, Integer monthlyNetIncome, Integer monthlyGrossIncome,
                                      Integer voucherType, Integer belonging, Integer pensionPayer, String employmentTime, String phoneNumber) throws Exception;

    void registerVehicleDiscovery(String email, Integer vehicleId);
}
