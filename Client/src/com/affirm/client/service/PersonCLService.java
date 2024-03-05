package com.affirm.client.service;

import com.affirm.client.model.form.LoanApplicationStep3Form;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface PersonCLService {


    void registerOcupationalInformation(int personId, LoanApplicationStep3Form form) throws Exception;
}
