package com.affirm.strategy;

import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.*;

import java.util.List;
import java.util.Locale;

public interface ContractStrategy {
    void fillPdf(Person person,
                 Credit credit,
                 LoanOffer loanOffer,
                 EntityProductParams params,
                 User user,
                 String signature,
                 Person partner,
                 PersonContactInformation contactInfo,
                 List<PersonOcupationalInformation> ocupations,
                 PersonOcupationalInformation principalOcupation,
                 PersonBankAccountInformation personBankAccountInformation,
                 LoanApplication loanApplication,
                 Locale locale,
                 SunatResult sunatResult) throws Exception;
}
