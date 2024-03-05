/**
 *
 */
package com.affirm.backoffice.dao;

import com.affirm.backoffice.model.CreditBoPainter;
import com.affirm.backoffice.model.CreditGatewayBoPainter;
import com.affirm.backoffice.model.CreditPendingDisbursementBoPainter;
import com.affirm.backoffice.util.PaginationWrapper;

import java.util.Date;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface CreditBODAO {

    PaginationWrapper<CreditPendingDisbursementBoPainter> getPendigDisbursement(String country, String documentNumber, Integer productId, Integer entityId, Integer employerId, Locale locale, Integer offset, Integer limit) throws Exception;

    void registerDisbursementBuffer(int creditId/*, int sysUserId, Date date, Character paymentType, Integer signatureSysUserId, String checkNumber*/) throws Exception;


    void registerDisbursementConfirmation(int creditId, Integer creditRejectionReasonId, Integer sysUserId, Date date, Character paymentType, Integer signatureSysUserId, String checkNumber) throws Exception;

    void registerDisbursementConfirmationWithComment(int creditId, Integer creditRejectionReasonId, Integer sysUserId, Date date, Character paymentType, Integer signatureSysUserId, String checkNumber, String creditRejectionReasonComment) throws Exception;

    PaginationWrapper<CreditGatewayBoPainter> getCreditCollection(String country, Locale locale, Integer offset, Integer limit) throws Exception;

    PaginationWrapper<CreditGatewayBoPainter> getCreditCollectionFilter(String country, Locale locale, Integer[] productId, Integer[] clusterId, String collector, Integer[] entityId, Integer[] trancheId, Boolean paused,
                                                                        Integer offset, Integer limit, String documentNumber, Integer employerId, Date dueDateFrom, Date dueDateTo) throws Exception;

    void registerCollectionContactResult(int creditId, Integer sysUserId, Integer contactResultId, Date date, Integer amount, String comment) throws Exception;

    void insertNegativBase(int creditId);

    String getLoanApplicationAnalystUsername(int loanApplicatonId) throws Exception;

    PaginationWrapper<CreditBoPainter> getPendigDisbursementConfirmation(String country, Locale locale, Date startDate, Date endDate, String documentNumber, Integer productId, Integer entityId, Integer employerId, Integer limit, Integer offset) throws Exception;

    PaginationWrapper<CreditBoPainter> getCreditsFilter(String country, Locale locale, Date creationFrom, Date creationTo, Integer[] productId, String[] analystId, Integer[] entityId, Integer tabId, String documentNumber, Integer employerId, Boolean isBranded, Boolean welcomeCall, int offset, int limit) throws Exception;
}
