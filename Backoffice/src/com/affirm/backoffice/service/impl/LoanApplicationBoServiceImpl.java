package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.dao.LoanApplicationBODAO;
import com.affirm.backoffice.model.LoanApplicationSummaryBoPainter;
import com.affirm.backoffice.service.LoanAplicationBoService;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dev5 on 30/10/17.
 */
@Component
public class LoanApplicationBoServiceImpl implements LoanAplicationBoService {

    private final UtilService utilService;

    private final LoanApplicationBODAO loanApplicationBoDao;
    private final LoanApplicationDAO loanApplicationDAO;
    private final UserDAO userDAO;
    private final CreditDAO creditDAO;

    public LoanApplicationBoServiceImpl(UtilService utilService, LoanApplicationBODAO loanApplicationBoDao, LoanApplicationDAO loanApplicationDAO, UserDAO userDAO, CreditDAO creditDAO) {
        this.utilService = utilService;
        this.loanApplicationBoDao = loanApplicationBoDao;
        this.loanApplicationDAO = loanApplicationDAO;
        this.userDAO = userDAO;
        this.creditDAO = creditDAO;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesToManage(String country,
                                                                                                   Date creationFrom,
                                                                                                   Date creationTo,
                                                                                                   String documentNumber,
                                                                                                   Integer entity,
                                                                                                   String analyst,
                                                                                                   Boolean assistedProcess,
                                                                                                   Boolean isBranded,
                                                                                                   Integer hoursNextContact,
                                                                                                   Locale locale,
                                                                                                   int offset,
                                                                                                   int limit) throws Exception {

        PaginationWrapper<LoanApplicationSummaryBoPainter> wraper = loanApplicationBoDao.getLoanApplicationsToManage(
                creationFrom,
                creationTo,
                documentNumber,
                entity,
                analyst,
                country,
                locale,
                assistedProcess,
                isBranded,
                hoursNextContact,
                offset,
                limit);

        if (wraper != null) {
            wraper.setResults(wraper.getResults().stream().filter(e -> e.getPhoneNumber() != null && !e.getPhoneNumber().isEmpty()).collect(Collectors.toList()));
            for (int i = 0; i < wraper.getResults().size(); i++) {
                wraper.getResults().get(i).setRowNumber(i + 1);
            }
        }
        return wraper;
    }

    private boolean post48Hours(Integer loanApplicationId) {
        LoanApplication loanApplication = null;
        try {
            loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
            List<ProcessQuestionSequence> processQuestionList = loanApplication.getQuestionSequence();
            Collections.sort(processQuestionList, new Comparator<ProcessQuestionSequence>() {
                public int compare(ProcessQuestionSequence o1, ProcessQuestionSequence o2) {
                    return o1.getDate().before(o2.getDate()) ? 1 : -1;
                }
            });
            Date now = new Date();
            return now.after(utilService.addDays(processQuestionList.get(0).getDate(), 2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesToManagev2(String country,
                                                                                                     Date creationFrom,
                                                                                                     Date creationTo,
                                                                                                     String documentNumber,
                                                                                                     Integer entity,
                                                                                                     String analyst,
                                                                                                     Boolean assistedProcess,
                                                                                                     Boolean isBranded,
                                                                                                     Integer hoursNextContact,
                                                                                                     Locale locale,
                                                                                                     int offset,
                                                                                                     int limit) throws Exception {

        PaginationWrapper<LoanApplicationSummaryBoPainter> wraper = loanApplicationBoDao.getLoanApplicationsToManagev2(
                creationFrom,
                creationTo,
                documentNumber,
                entity,
                analyst,
                country,
                locale,
                assistedProcess,
                isBranded,
                hoursNextContact,
                offset,
                limit);

        if (wraper != null) {
            wraper.setResults(wraper.getResults().stream().filter(e -> e.getPhoneNumber() != null && !e.getPhoneNumber().isEmpty()).collect(Collectors.toList()));
            for (int i = 0; i < wraper.getResults().size(); i++) {
                wraper.getResults().get(i).setRowNumber(i + 1);
            }
        }
        return wraper;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesListManagement(Integer view, Date creationFrom, Date creationTo, String amountFrom, String amountTo, String documentNumber, Integer entity, Integer employer, Integer reason, String country, String analyst, Locale locale, int offset, int limit, Integer hoursNextContact, Integer question) throws Exception {
        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = loanApplicationBoDao.getLoanApplicationsListManagement(
                view,
                creationFrom,
                creationTo,
                amountFrom,
                amountTo,
                documentNumber,
                entity,
                employer,
                reason,
                country,
                analyst,
                locale,
                offset,
                limit,
                hoursNextContact,
                question);

        if (wrapper != null && wrapper.getResults() != null)
            for (LoanApplicationSummaryBoPainter painter : wrapper.getResults()) {
                if (painter.getCreditId() != null)
                    painter.setCredit(creditDAO.getCreditByID(painter.getCreditId(), locale, false, Credit.class));
            }

        return wrapper;
    }

    @Override
    public PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesListEvaluation(Date creationFrom, Date creationTo, String amountFrom, String amountTo, String documentNumber, Integer entity, Integer employer, Integer reason, String country, String analyst, Locale locale, int offset, int limit) throws Exception {
        PaginationWrapper<LoanApplicationSummaryBoPainter> wrapper = loanApplicationBoDao.getLoanApplicationsListEvaluation(
                creationFrom,
                creationTo,
                amountFrom,
                amountTo,
                documentNumber,
                entity,
                employer,
                reason,
                country,
                analyst,
                locale,
                offset,
                limit);

        if (wrapper != null && wrapper.getResults() != null)
            for (LoanApplicationSummaryBoPainter painter : wrapper.getResults()) {
                if (painter.getCreditId() != null)
                    painter.setCredit(creditDAO.getCreditByID(painter.getCreditId(), locale, false, Credit.class));
            }

        return wrapper;
    }
}
