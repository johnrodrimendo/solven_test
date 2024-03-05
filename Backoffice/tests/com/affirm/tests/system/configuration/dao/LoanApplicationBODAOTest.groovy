package com.affirm.tests.system.configuration.dao

import com.affirm.backoffice.dao.LoanApplicationBODAO
import com.affirm.backoffice.model.LoanApplicationSummaryBoPainter
import com.affirm.backoffice.model.SelfEvaluationBoPainter
import com.affirm.backoffice.util.PaginationWrapper
import com.affirm.tests.BaseBoConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class LoanApplicationBODAOTest extends BaseBoConfig {

    @Autowired
    LoanApplicationBODAO loanApplicationBODAO

    static final String COUNTRY = "51"
    static final String AMOUNT_FILTER_FROM = "1"
    static final String AMOUNT_FILTER_TO = "5"
    static final Date CREATION_FROM = new Date()
    static final Date CREATION_TO = new Date()
    static final String DOCUMENT_NUMBER = "51"
    static final Integer ENTITY = 12345
    static final Integer EMPLOYER = 54321
    static final Integer REASON = 33333
    static final String ANALYST = "51111"
    static final Locale LOCALE = Locale.US
    static final Integer VIEW_ID = 78965
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final String COUNTRY_ID = "51"
    static final Boolean ASSISTED_PROCESS = true
    static final Boolean IS_BRANDED = true
    static final Integer HOURS_NEXT_CONTACT = 9
    static final Integer LOADN_APPLICATION_ID = 19999
    static final Integer SCORE = 44444
    static final String AMOUNT_FROM = "5"
    static final String AMOUNT_TO = "10"

    @Test
    void getPendigDisbursementFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsSummaries(
                COUNTRY, AMOUNT_FILTER_FROM, AMOUNT_FILTER_TO, CREATION_FROM, CREATION_TO, DOCUMENT_NUMBER, ENTITY, EMPLOYER, REASON,
                ANALYST, LOCALE, VIEW_ID, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getLoanApplicationsToManageFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsToManage(
                CREATION_FROM, CREATION_TO, DOCUMENT_NUMBER, ENTITY, ANALYST, COUNTRY_ID, LOCALE, ASSISTED_PROCESS, IS_BRANDED,
                HOURS_NEXT_CONTACT, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getLoanApplicationsToManageByIdFromLoanApplicationBODAO() {
        LoanApplicationSummaryBoPainter loanApplicationSummaryBoPainter =
                loanApplicationBODAO.getLoanApplicationsToManageById(LOADN_APPLICATION_ID, LOCALE)
        Assert.assertNull(loanApplicationSummaryBoPainter)
    }

    @Test
    void getLoanApplicationsSelfEvaluationFromLoanApplicationBODAO() {
        PaginationWrapper<SelfEvaluationBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsSelfEvaluation(
                COUNTRY, AMOUNT_FILTER_FROM, AMOUNT_FILTER_TO, CREATION_FROM, CREATION_TO, DOCUMENT_NUMBER, REASON, SCORE, LOCALE, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getLoanApplicationsToAuditFromLoanApplicationBODAO() {
        List<LoanApplicationSummaryBoPainter> list = loanApplicationBODAO.getLoanApplicationsToAudit(COUNTRY, LOCALE)
        Assert.assertNull(list)
    }

    @Test
    void getPinLoanApplicationsToManageFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getPinLoanApplicationsToManage(
                COUNTRY_ID, CREATION_FROM, CREATION_TO, DOCUMENT_NUMBER, ENTITY, ANALYST, IS_BRANDED, LOCALE, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getPinLoanApplicationToManageByIdFromLoanApplicationBODAO() {
        LoanApplicationSummaryBoPainter loanApplicationSummaryBoPainter =
                loanApplicationBODAO.getPinLoanApplicationToManageById(LOADN_APPLICATION_ID, LOCALE)
        Assert.assertNull(loanApplicationSummaryBoPainter)
    }

    @Test
    void getLoanApplicationsToManagev2FromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsToManagev2(
                CREATION_FROM, CREATION_TO, DOCUMENT_NUMBER, ENTITY, ANALYST, COUNTRY_ID, LOCALE, ASSISTED_PROCESS, IS_BRANDED,
                HOURS_NEXT_CONTACT, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getLoanApplicationsListPreApprovedFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsListPreApproved(
                CREATION_FROM, CREATION_TO, AMOUNT_FROM, AMOUNT_TO, DOCUMENT_NUMBER, ENTITY, EMPLOYER, REASON, COUNTRY, ANALYST, LOCALE,
                OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getLoanApplicationsPreApprovedByIdFromLoanApplicationBODAO() {
        LoanApplicationSummaryBoPainter loanApplicationSummaryBoPainter =
                loanApplicationBODAO.getLoanApplicationsPreApprovedById(LOADN_APPLICATION_ID, LOCALE)
        Assert.assertNull(loanApplicationSummaryBoPainter)
    }

    @Test
    void getLoanApplicationsListCrossSellingFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsListCrossSelling(
                CREATION_FROM, CREATION_TO, AMOUNT_FROM, AMOUNT_TO, DOCUMENT_NUMBER, ENTITY, EMPLOYER, REASON, COUNTRY, ANALYST, LOCALE,
                OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getLoanApplicationsListManagementFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper = loanApplicationBODAO.getLoanApplicationsListManagement(
                VIEW_ID, CREATION_FROM, CREATION_TO, AMOUNT_FROM, AMOUNT_TO, DOCUMENT_NUMBER, ENTITY, EMPLOYER, REASON, COUNTRY, ANALYST,
                LOCALE, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getManagementLoanApplicationByIdFromLoanApplicationBODAO() {
        LoanApplicationSummaryBoPainter loanApplicationSummaryBoPainter =
                loanApplicationBODAO.getManagementLoanApplicationById(VIEW_ID, LOADN_APPLICATION_ID, LOCALE)
        Assert.assertNull(loanApplicationSummaryBoPainter)
    }

    @Test
    void getLoanApplicationsListEvaluationFromLoanApplicationBODAO() {
        PaginationWrapper<LoanApplicationSummaryBoPainter> paginationWrapper =
                loanApplicationBODAO.getLoanApplicationsListEvaluation(CREATION_FROM, CREATION_TO, AMOUNT_FROM, AMOUNT_TO, DOCUMENT_NUMBER,
                        ENTITY, EMPLOYER, REASON, COUNTRY, ANALYST, LOCALE, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getEvaluationLoanApplicationByIdFromLoanApplicationBODAO() {
        LoanApplicationSummaryBoPainter loanApplicationSummaryBoPainter =
                loanApplicationBODAO.getEvaluationLoanApplicationById(LOADN_APPLICATION_ID, LOCALE)
        Assert.assertNull(loanApplicationSummaryBoPainter)
    }
}
