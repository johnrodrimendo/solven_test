package com.affirm.backoffice.service;

import com.affirm.backoffice.model.LoanApplicationBoPainter;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public interface ApplicationPainterService {
    List<LoanApplicationBoPainter> getApplicationsByPerson(Integer personId, Locale locale) throws Exception;

    LoanApplicationBoPainter getApplicationById(int loanApplicationId, Locale locale, HttpServletRequest request) throws Exception;
}
