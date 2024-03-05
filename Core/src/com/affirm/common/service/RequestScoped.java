package com.affirm.common.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.SelfEvaluation;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScoped implements Serializable {

    private LoanApplication loanApplicationLite;

    private SelfEvaluation selfEvaluation;

    public LoanApplication getLoanApplicationLite() {
        return loanApplicationLite;
    }

    public void setLoanApplicationLite(LoanApplication loanApplicationLite) {
        this.loanApplicationLite = loanApplicationLite;
    }

    public SelfEvaluation getSelfEvaluation() {
        return selfEvaluation;
    }

    public void setSelfEvaluation(SelfEvaluation selfEvaluation) {
        this.selfEvaluation = selfEvaluation;
    }
}
