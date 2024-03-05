package com.affirm.warmi.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.warmi.model.ProcessDetail;
import org.json.JSONObject;

public interface WarmiService {

    JSONObject runProcess(LoanApplication loanApplication, Person person);

    void saveResult(ProcessDetail result) throws Exception;

}
