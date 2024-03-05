package com.affirm.client.service;

import com.affirm.client.model.form.AdvanceContactForm;
import com.affirm.client.model.form.CompanyContactForm;
import com.affirm.client.model.form.ContactForm;
import com.affirm.client.model.form.ProcessContactForm;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import org.json.JSONArray;

import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface EmailCLService {
    void sendContactMail(ContactForm contactForm) throws Exception;

    void sendProcessContactMail(ProcessContactForm processContactForm);

    void sendCompanyContactMail(CompanyContactForm form);

    void sendAdvanceContactMail(AdvanceContactForm contactForm, int prooductId);

    boolean sendRejectionMail(Credit credit, Locale locale) throws Exception;

    boolean sendRejectionMailEvaluation(Integer loanApplicationId, Locale locale) throws Exception;

    void sendPasswordEmployersMail(String employerName, String employerRuc, JSONArray users);
}
