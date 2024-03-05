package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.OffersBanBifForm;
import com.affirm.common.model.form.Question181Form;
import com.affirm.common.model.transactional.BanbifPreApprovedBase;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question181Service")
public class Question181Service extends AbstractQuestionService<Question181Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;

    private static final int REJECT_OFFER = -1;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question181Form form = new Question181Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(loanApplication.getPersonId(), false, locale);
                JSONObject data = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
                BanbifPreApprovedBase approvedBase = new Gson().fromJson(data.toString(),BanbifPreApprovedBase.class);

                // Generate the offer if it not exists yet
                List<LoanOffer> offers = loanApplicationDao.getAllLoanOffers(loanApplication.getId());
                if(offers == null || offers.isEmpty()){
                    if(approvedBase.getPlaza1() != null) {
                        LoanOffer offer = new LoanOffer();
                        offer.setEntityId(Entity.BANBIF);
                        offer.setInstallments(approvedBase.getPlaza1());
                        offer.setAmmount(approvedBase.getMonto1());
                        offer.setInstallmentAmmount(approvedBase.getCuota1());
                        offer.setEffectiveAnnualCostRate(approvedBase.getTcea1());
                        offer.setProduct(catalogService.getProduct(Product.TARJETA_CREDITO));
                        offer.setLoanOfferOrder(1);
                        loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO);
                    }
                    if(approvedBase.getPlaza2() != null) {
                        LoanOffer offer = new LoanOffer();
                        offer.setEntityId(Entity.BANBIF);
                        offer.setInstallments(approvedBase.getPlaza2());
                        offer.setAmmount(approvedBase.getMonto2());
                        offer.setInstallmentAmmount(approvedBase.getCuota2());
                        offer.setEffectiveAnnualCostRate(approvedBase.getTcea2());
                        offer.setProduct(catalogService.getProduct(Product.TARJETA_CREDITO));
                        offer.setLoanOfferOrder(2);
                        loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO);
                    }
                    if(approvedBase.getPlaza3() != null) {
                        LoanOffer offer = new LoanOffer();
                        offer.setEntityId(Entity.BANBIF);
                        offer.setInstallments(approvedBase.getPlaza3());
                        offer.setAmmount(approvedBase.getMonto3());
                        offer.setInstallmentAmmount(approvedBase.getCuota3());
                        offer.setEffectiveAnnualCostRate(approvedBase.getTcea3());
                        offer.setProduct(catalogService.getProduct(Product.TARJETA_CREDITO));
                        offer.setLoanOfferOrder(3);
                        loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO);
                    }
                    offers = loanApplicationDao.getAllLoanOffers(loanApplication.getId());

                }

                attributes.put("maxTcea", offers.stream().mapToDouble(o -> o.getEffectiveAnnualCostRate()).max().orElse(0.0));
                attributes.put("approvedBase", approvedBase);
                attributes.put("offers", offers);
                attributes.put("personName", person.getName());
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                attributes.put("showAgent", true);
                attributes.put("showGoBack", false);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question181Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if(form.getOfferId() == REJECT_OFFER)
                    return "REJECT";
                else
                    return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question181Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question181Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getOfferId() != REJECT_OFFER) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_TC_PAYMENT_DAY.getKey(), form.getFirstDueDay());
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());

                    // Generate the offer if it not exists yet
                    List<LoanOffer> offers = loanApplicationDao.getAllLoanOffers(loanApplication.getId());
                    if(offers != null && !offers.isEmpty() && offers.stream().anyMatch(e -> e.getId().intValue() == form.getOfferId().intValue())){
                        loanApplicationDao.registerSelectedLoanOffer(form.getOfferId(), null);
                    }
                    else{
                        throw new Exception("Offer selected is invalid");
                    }
                }
                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        throw new Exception("No method configured");
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

}

