package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.BankAccountOfferData;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.Question173Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question173Service")
public class Question173Service extends AbstractQuestionService<Question173Form> {

    private static final int REJECT_OFFER = -1;

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private PersonDAO personDAO;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question173Form form = new Question173Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (loanApplication.getSelectedEntityId() == null) {
                    loanApplicationDao.selectBestLoanOffer(loanApplication.getId());
                }

                // Generate the offer if it not exists yet
                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                if (offers == null || offers.isEmpty()) {

                    Character customLoanOffer = null;
                    if(loanApplication.getAuxData() != null) customLoanOffer = loanApplication.getAuxData().getBankAccountCustomOffer();
                    int order = 1;

                    //FIRTS OFFER
                    LoanOffer offer = new LoanOffer();
                    offer.setEntityId(Entity.AZTECA);
                    offer.setProduct(catalogService.getProduct(Product.SAVINGS_ACCOUNT));
                    offer.setLoanOfferOrder(order);

                    //ENTITY CUSTOM DATA
                    BankAccountOfferData bankAccountOfferData = new BankAccountOfferData();
                    bankAccountOfferData.setType(BankAccountOfferData.TRADITIONAL_TYPE);
                    bankAccountOfferData.setAnnualInterest(0.0);
                    bankAccountOfferData.setPhysicalAccountType(true);
                    bankAccountOfferData.setVirtualAccountType(true);
                    bankAccountOfferData.setMonthlyWithdrawalATM(10);
                    bankAccountOfferData.setMonthlyWithdrawalAgency(-1);
                    bankAccountOfferData.setMonthlyMaintenanceAmount(0.0);
                    bankAccountOfferData.setCurrency("Soles");
                    bankAccountOfferData.setAllowedOperations(10);
                    bankAccountOfferData.setCurrencyId(Currency.PEN);
                    bankAccountOfferData.setTrea(1.0);

                    //OFERTA 1
                    offer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.BANK_ACCOUNT_DATA.getKey(), new JSONObject(new Gson().toJson(bankAccountOfferData)));
                    //VALIDATE IF CUSTOM OR NOT
                    if(customLoanOffer == null || Arrays.asList(BankAccountOfferData.TRADITIONAL_TYPE).contains(customLoanOffer)) {
                        loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO);
                        order += 1;
                    }

                    offer.setLoanOfferOrder(order);
                    bankAccountOfferData.setType(BankAccountOfferData.HIGH_PROFITABILITY_TYPE);
                    bankAccountOfferData.setAnnualInterest(6.0);
                    bankAccountOfferData.setPhysicalAccountType(true);
                    bankAccountOfferData.setVirtualAccountType(true);
                    bankAccountOfferData.setMonthlyWithdrawalATM(-1);
                    bankAccountOfferData.setMonthlyWithdrawalAgency(3);
                    bankAccountOfferData.setMonthlyMaintenanceAmount(10.0);
                    bankAccountOfferData.setCurrency("Soles");
                    bankAccountOfferData.setAllowedOperations(2);
                    bankAccountOfferData.setCurrencyId(Currency.PEN);
                    bankAccountOfferData.setTrea(1.0);


                    //OFERTA 2
                    offer.getEntityCustomData().put(LoanOffer.EntityCustomDataKeys.BANK_ACCOUNT_DATA.getKey(), new JSONObject(new Gson().toJson(bankAccountOfferData)));
                    //VALIDATE IF CUSTOM OR NOT
                    if(customLoanOffer == null  || Arrays.asList(BankAccountOfferData.HIGH_PROFITABILITY_TYPE).contains(customLoanOffer)) {
                        loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO);
                        order += 1;
                    }

                    offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                }

                attributes.put("offers", offers);
                attributes.put("person", personDAO.getPerson(loanApplication.getPersonId(), false,locale));
                attributes.put("form", form);
                attributes.put("showGoBack", false);
                attributes.put("showAgent", true);

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question173Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return (form.getOfferId() == REJECT_OFFER) ? "REJECT" : "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question173Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question173Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getOfferId() != REJECT_OFFER) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                    // Generate the offer if it not exists yet
                    List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                    if (offers != null && !offers.isEmpty() && offers.stream().filter(e -> e.getId().equals(form.getOfferId())).findFirst().orElse(null) != null) {
                        loanApplicationDao.registerSelectedLoanOffer(form.getOfferId(), null);
                    }

                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }
}

