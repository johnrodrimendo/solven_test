package com.affirm.common.service.impl;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.Item;
import com.affirm.common.model.Transaction;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.GoogleAnalyticsService;
import com.affirm.common.service.KreditiwebService;
import com.affirm.common.service.LoanNotifierService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("loanNotifierService")
public class LoanNotifierServiceImpl implements LoanNotifierService {

    @Autowired
    LoanApplicationDAO loanApplicationDAO;

    @Autowired
    GoogleAnalyticsService googleAnalyticsService;
    @Autowired
    CreditDAO creditDAO;
    @Autowired
    KreditiwebService kreditiwebService;

    @Override
    public void notifyDisbursement(int loanApplicationId, Locale locale) throws Exception {
        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, locale);
        this.notifyDisbursement(loanApplication);
    }

    @Override
    public void notifyDisbursement(LoanApplication loanApplication) {
        Item item = new Item();

        item.setName(loanApplication.getProduct().getName());
        item.setCategory(loanApplication.getProduct().getProductCategory().getCategory());
        Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
        double amount = credit.getAmount() != null ? credit.getAmount() : 0.0;
        double revenue = credit.getEntityCommision() != null ? credit.getEntityCommision() : 0.0;
        double tax = credit.getEntityTax() != null ? credit.getEntityTax() : 0.0;

        item.setPrice(0.0);
        item.setTax(0.0);
        item.setRevenue(revenue);
        item.setCode(loanApplication.getCode());
        item.setSource(loanApplication.getSource());
        item.setMedium(loanApplication.getMedium());
        item.setCampaign(loanApplication.getCampaign());

        Transaction transaction = new Transaction();

        transaction.setId(loanApplication.getId().toString());
        transaction.setItem(item);
        transaction.setRevenue(revenue);
        transaction.setTax(0.0);
        transaction.setAffiliation("SOLVEN PE");
        transaction.setSource(loanApplication.getSource());
        transaction.setMedium(loanApplication.getMedium());
        transaction.setCampaign(loanApplication.getCampaign());

        String gaClientId = loanApplication.getGaClientId();

        if (gaClientId == null) {
            gaClientId = loanApplication.getCode();
        }

        googleAnalyticsService.Configure(
                loanApplication.getIpAddress(),
                gaClientId,
                loanApplication.getUserAgent(),
                loanApplication.getSource(),
                loanApplication.getMedium(),
                loanApplication.getCampaign(),
                loanApplication.getCountryId());

        if (loanApplication.getSource() != null && loanApplication.getSource().equals(Configuration.KREDITIWEB_SOURCE) &&
                loanApplication.getMedium() != null && loanApplication.getMedium().equals(Configuration.KREDITIWEB_MEDIUM) &&
                loanApplication.getCampaign() != null && loanApplication.getCampaign().equals(Configuration.KREDITIWEB_CAMPAIGN)) {
            kreditiwebService.notifyDisbursement();
        }

        googleAnalyticsService.sendTransaction(transaction);
        googleAnalyticsService.sendPageView("desembolso");
    }

    @Override
    public void notifyRejection(LoanApplication loanApplication, Credit credit) {
        String goalName;
        Integer rejectionExpirationDays = null;
        if (loanApplication.getRejectionReason() != null) {
            rejectionExpirationDays = loanApplication.getRejectionReason().getExpirationDays();
        } else if (credit != null && credit.getRejectionReason() != null) {
            rejectionExpirationDays = credit.getRejectionReason().getExpirationDays();
        }

        if (rejectionExpirationDays == null || rejectionExpirationDays <= 90) {
            goalName = "rechazo_leve_90";
        } else if (rejectionExpirationDays <= 180) {
            goalName = "rechazo_leve_180";
        } else {
            goalName = "rechazo_grave";
        }

        googleAnalyticsService.Configure(
                loanApplication.getIpAddress(),
                loanApplication.getGaClientId(),
                loanApplication.getUserAgent(),
                loanApplication.getCountryId());
        googleAnalyticsService.sendEvent(goalName);
    }

}
