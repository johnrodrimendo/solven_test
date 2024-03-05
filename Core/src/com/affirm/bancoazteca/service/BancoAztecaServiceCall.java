package com.affirm.bancoazteca.service;

import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.BancoAztecaGatewayApi;
import com.affirm.bancoazteca.model.RolConsejero;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import org.json.JSONObject;

import java.util.List;

public interface BancoAztecaServiceCall {

    List<BancoAztecaCampaniaApi> getPersonCampaigns(LoanApplication loanApplication, Person person, String token) throws Exception;

    JSONObject login(LoanApplication loanApplication) throws Exception;

    RolConsejero getAdviserRole(LoanApplication loanApplication, Person person, String token) throws Exception;

    List<BancoAztecaGatewayApi>  getPersonRecoveryCampaigns(LoanApplication loanApplication, Person person, String token) throws Exception;

}
