package com.affirm.common.service;

import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.*;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 11/07/16.
 */
public interface CreditService {

    byte[] createOfferContract(LoanApplication loanApplication, LoanOffer loanOffer, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload) throws Exception;

    byte[] createContract(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload) throws Exception;

    byte[] createContract(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload, boolean async) throws Exception;

    void createAndSaveContractApplication(LoanApplication loanApplication, LoanOffer selectedOffer, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine) throws Exception;

    byte[] renderAssociatedFileFromPdf(Credit credit, Person person, User user, Locale locale) throws Exception;

    byte[] createCreditContract(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine, String filename, boolean toDownload) throws Exception;

    void sendDisbursementConfirmationEmailAndSms(Credit credit, Person person, User user, byte[] contractBytes) throws Exception;

    void sendDisbursementConfirmationEmailDealerEntity(Credit credit, Person person, User user, byte[] contractBytes) throws Exception;

    void sendAccreditedPaymentEmailAndSms(Credit credit, Person person, User user) throws Exception;

    String renderContract(Integer creditId, Locale locale, boolean toSign, String signature) throws Exception;

    Boolean isDisbursementActive(Date date) throws Exception;

    String renderContract(int personId, int entityId, int productId, Locale locale, boolean toSign, String signature) throws Exception;

    byte[] renderAccesoCertificadoPrepaprobacion(LoanApplication loanApplication, LoanOffer offer, Person person) throws Exception;

    byte[] createSummarySheet(HttpServletRequest request, HttpServletResponse response, Locale locale,
                              SpringTemplateEngine templateEngine, Credit credit, LoanOffer loanOffer, Person person, LoanApplication loanApplication,
                              EntityProductParams params, EntityBranding entityBranding, String signature)throws Exception;

    String getGoogleTagManagerKey(Integer creditId);

    void updateCreditConditionsAmountInstallmentsAndTea(int creditId, Double newAmount, Integer newInstallments, Double newTea, Integer entityUserId) throws Exception;

    void originateCreditByEntity(int creditId, int entityId, Date disbursedDate, SpringTemplateEngine templateEngine, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception;

    void disburseCreditByEntity(int creditId, int entityId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception;

    void generateCreditTcea(Credit credit) throws Exception;

    double generateCreditTceaWithoutIva(Credit credit) throws Exception;
}
