package com.affirm.backoffice.service;

import com.affirm.backoffice.model.LoanApplicationSummaryBoPainter;
import com.affirm.backoffice.util.PaginationWrapper;

import java.util.Date;
import java.util.Locale;

/**
 * Created by dev5 on 30/10/17.
 */
public interface LoanAplicationBoService {

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesToManage(String country,
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
                                                                                            int limit) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesToManagev2(String country,
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
                                                                                              int limit) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesListManagement(Integer view, Date creationFrom,
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
                                                                                                  Integer hoursNextContactFilter,
                                                                                                  Integer question) throws Exception;

    PaginationWrapper<LoanApplicationSummaryBoPainter> getLoanApplicationsSummariesListEvaluation(Date creationFrom,
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
}
