package com.affirm.backoffice.strategies.bureau;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.transactional.ApplicationBureau;
import org.springframework.ui.ModelMap;

import java.util.Locale;

public interface BureauStrategy {
    void updateModelMap(Locale locale, ApplicationBureau applicationBureau, ModelMap model, LoanApplicationDAO loanApplicationDAO, PersonDAO personDAO) throws Exception;
}
