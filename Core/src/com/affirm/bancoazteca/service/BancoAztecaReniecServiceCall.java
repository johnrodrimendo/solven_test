package com.affirm.bancoazteca.service;

import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.ReniecDataResponse;
import com.affirm.bancoazteca.model.ReniecLoginResponse;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import org.json.JSONObject;

import java.util.List;

public interface BancoAztecaReniecServiceCall {

    ReniecLoginResponse login(LoanApplication loanApplication) throws Exception;

    ReniecDataResponse getPersonData(LoanApplication loanApplication, Person person, String token) throws Exception;

}
