package com.affirm.tests.system.configuration.dao

import com.affirm.backoffice.dao.CreditBODAO
import com.affirm.backoffice.model.CreditBoPainter
import com.affirm.backoffice.model.CreditGatewayBoPainter
import com.affirm.backoffice.model.CreditPendingDisbursementBoPainter
import com.affirm.backoffice.util.PaginationWrapper
import com.affirm.tests.BaseBoConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CreditBODAOTest extends BaseBoConfig {

    @Autowired
    CreditBODAO creditBODAO

    static final String COUNTRY = "51"
    static final String DOCUMENT_NUMBER = "11111"
    static final Integer PRODUCT_ID = 99999
    static final Integer ENTITY_ID = 55555
    static final Integer EMPLOYER_ID = 11111
    static final Locale LOCALE = Locale.US
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final Integer CREDIT_ID = 12345
    static final Date START_DATE = new Date()
    static final Date END_DATE = new Date()
    static final Integer CREDIT_REJECTION_REASON_ID = 54321
    static final Integer SYS_USER_ID = 77777
    static final Date DATE = new Date()
    static final Character PAYMENT_TYPE = 'A'
    static final Integer SIGNATURE_SYS_USER_ID = 98765
    static final String CHECK_NUMBER = "1"
    static final Date CREATION_FROM = new Date()
    static final Date CREATION_TO = new Date()
    static final String ANALYST_ID = "78954"
    static final Integer[] PRODUCT_IDS = [PRODUCT_ID]
    static final String[] ANALYST_IDS = [ANALYST_ID]
    static final Integer[] ENTITY_IDS = [ENTITY_ID]
    static final Integer TAB_ID = 52525
    static final Boolean IS_BRANDED = true
    static final Boolean WELLCOME_CALL = false
    static final String COLLECTOR = "33333"
    static final Integer TRANCHE_ID = 44444
    static final Integer[] TRANCHE_IDS = [TRANCHE_ID]
    static final Boolean PAUSED = true
    static final Integer CLUSTER_ID = 33333
    static final Integer[] CLUSTER_IDS = [CLUSTER_ID]
    static final Date DUE_DATE_FROM = new Date()
    static final Date DUE_DATE_TO = new Date()
    static final Integer CONTACT_RESULT_ID = 33331
    static final Integer AMOUNT = 1
    static final String COMMENT = "Comment"
    static final Integer LOAN_APPLICATION_ID = 19999

    @Test
    void getPendigDisbursementFromCreditBODAO() {
        PaginationWrapper<CreditPendingDisbursementBoPainter> paginationWrapper = creditBODAO.getPendigDisbursement(
                COUNTRY, DOCUMENT_NUMBER, PRODUCT_ID, ENTITY_ID, EMPLOYER_ID, LOCALE, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void registerDisbursementBufferFromCreditBODAO() {
        creditBODAO.registerDisbursementBuffer(12345)
    }

    @Test
    void getPendigDisbursementConfirmationFromCreditBODAO() {
        PaginationWrapper<CreditBoPainter> paginationWrapper = creditBODAO.getPendigDisbursementConfirmation(
                COUNTRY, LOCALE, START_DATE, END_DATE, DOCUMENT_NUMBER, PRODUCT_ID, ENTITY_ID, EMPLOYER_ID, LIMIT, OFFSET)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void registerDisbursementConfirmationFromCreditBODAO() {
        creditBODAO.registerDisbursementConfirmation(
                CREDIT_ID, CREDIT_REJECTION_REASON_ID, SYS_USER_ID, DATE, PAYMENT_TYPE, SIGNATURE_SYS_USER_ID, CHECK_NUMBER)
    }

    @Test
    void getCreditsFilterFromCreditBODAO() {
        PaginationWrapper<CreditBoPainter> paginationWrapper = creditBODAO.getCreditsFilter(
                COUNTRY, LOCALE, CREATION_FROM, CREATION_TO, PRODUCT_IDS, ANALYST_IDS, ENTITY_IDS, TAB_ID, DOCUMENT_NUMBER,
                EMPLOYER_ID, IS_BRANDED, WELLCOME_CALL, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getCreditCollectionFromCreditBODAO() {
        PaginationWrapper<CreditGatewayBoPainter> paginationWrapper = creditBODAO.getCreditCollection(COUNTRY, LOCALE, OFFSET, LIMIT)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void getCreditCollectionFilterFromCreditBODAO() {
        PaginationWrapper<CreditGatewayBoPainter> paginationWrapper = creditBODAO.getCreditCollectionFilter(
                COUNTRY, LOCALE, PRODUCT_IDS, CLUSTER_IDS, COLLECTOR, ENTITY_IDS, TRANCHE_IDS, PAUSED, OFFSET, LIMIT,
                DOCUMENT_NUMBER, EMPLOYER_ID, DUE_DATE_FROM, DUE_DATE_TO)
        Assert.assertNull(paginationWrapper)
    }

    @Test
    void registerCollectionContactResultFromCreditBODAO() {
        creditBODAO.registerCollectionContactResult(CREDIT_ID, SYS_USER_ID, CONTACT_RESULT_ID, DATE, AMOUNT, COMMENT)
    }

    @Test
    void insertNegativBaseFromCreditBODAO() {
        creditBODAO.insertNegativBase(CREDIT_ID)
    }

    @Test
    void getLoanApplicationAnalystUsernameFromCreditBODAO() {
        String analystUsername = creditBODAO.getLoanApplicationAnalystUsername(LOAN_APPLICATION_ID)
        Assert.assertNull(analystUsername)
    }
}
