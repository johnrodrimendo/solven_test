package com.affirm.backoffice.dao;

import com.affirm.backoffice.model.LoanApplicationSummaryBoPainter;
import com.affirm.backoffice.model.SelfEvaluationBoPainter;
import com.affirm.backoffice.util.PaginationWrapper;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by john on 28/09/16.
 */
public interface LoanApplicationBODAO {

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummaries(String country,
                                                                                    String amountFilterFrom,
                                                                                    String amountFilterTo,
                                                                                    Date creationFrom,
                                                                                    Date creationTo,
                                                                                    String documentNumber,
                                                                                    Integer entity,
                                                                                    Integer employer,
                                                                                    Integer reason,
                                                                                    String analyst,
                                                                                    Locale locale, int viewId, int offset, int limit) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsToManage(Date creationFrom,
                                                                                   Date creationTo,
                                                                                   String documentNumber,
                                                                                   Integer entity,
                                                                                   String analyst,
                                                                                   String countryId,
                                                                                   Locale locale,
                                                                                   Boolean assistedProcess,
                                                                                   Boolean isBranded,
                                                                                   Integer hoursNextContact,
                                                                                   int offset,
                                                                                   int limit) throws Exception;

    LoanApplicationSummaryBoPainter getLoanApplicationsToManageById(int loanApplicationId, Locale locale) throws Exception;

    PaginationWrapper<SelfEvaluationBoPainter> getLoanApplicationsSelfEvaluation(String country,
                                                                                 String amountFilterFrom,
                                                                                 String amountFilterTo,
                                                                                 Date creationFrom,
                                                                                 Date creationTo,
                                                                                 String documentNumber,
                                                                                 Integer reason,
                                                                                 Integer score,
                                                                                 Locale locale, int offset, int limit) throws Exception;

    List<LoanApplicationSummaryBoPainter> getLoanApplicationsToAudit(String country, Locale locale) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getPinLoanApplicationsToManage(String countryId,
                                                                                      Date creationFrom,
                                                                                      Date creationTo,
                                                                                      String documentNumber,
                                                                                      Integer entity,
                                                                                      String analyst,
                                                                                      Boolean isBranded,
                                                                                      Locale locale,
                                                                                      int offset,
                                                                                      int limit) throws Exception;

    LoanApplicationSummaryBoPainter getPinLoanApplicationToManageById(int loanApplicationId, Locale locale) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsToManagev2(Date creationFrom,
                                                                                     Date creationTo,
                                                                                     String documentNumber,
                                                                                     Integer entity,
                                                                                     String analyst,
                                                                                     String countryId,
                                                                                     Locale locale,
                                                                                     Boolean assistedProcess,
                                                                                     Boolean isBranded,
                                                                                     Integer hoursNextContact,
                                                                                     int offset,
                                                                                     int limit) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListPreApproved(Date creationFrom,
                                                                                          Date creationTo,
                                                                                          String amountFrom, String amountTo,
                                                                                          String documentNumber,
                                                                                          Integer entity,
                                                                                          Integer employer,
                                                                                          Integer reason,
                                                                                          String country,
                                                                                          String analyst,
                                                                                          Locale locale,
                                                                                          int offset,
                                                                                          int limit) throws Exception;

    LoanApplicationSummaryBoPainter getLoanApplicationsPreApprovedById(int loanApplicationId, Locale locale) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListCrossSelling(Date creationFrom,
                                                                                           Date creationTo,
                                                                                           String amountFrom, String amountTo,
                                                                                           String documentNumber,
                                                                                           Integer entity,
                                                                                           Integer employer,
                                                                                           Integer reason,
                                                                                           String country,
                                                                                           String analyst,
                                                                                           Locale locale,
                                                                                           int offset,
                                                                                           int limit) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListManagement(Integer viewId, Date creationFrom,
                                                                                         Date creationTo,
                                                                                         String amountFrom, String amountTo,
                                                                                         String documentNumber,
                                                                                         Integer entity,
                                                                                         Integer employer,
                                                                                         Integer reason,
                                                                                         String country,
                                                                                         String analyst,
                                                                                         Locale locale,
                                                                                         int offset,
                                                                                         int limit,
                                                                                         Integer hoursNextContact,
                                                                                         Integer question) throws Exception;

    LoanApplicationSummaryBoPainter getManagementLoanApplicationById(int viewId, int loanApplicationId, Locale locale) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsListEvaluation(Date creationFrom,
                                                                                         Date creationTo,
                                                                                         String amountFrom, String amountTo,
                                                                                         String documentNumber,
                                                                                         Integer entity,
                                                                                         Integer employer,
                                                                                         Integer reason,
                                                                                         String country,
                                                                                         String analyst,
                                                                                         Locale locale,
                                                                                         int offset,
                                                                                         int limit) throws Exception;

    LoanApplicationSummaryBoPainter getEvaluationLoanApplicationById(int loanApplicationId, Locale locale) throws Exception;
}