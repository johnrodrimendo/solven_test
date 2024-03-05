package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.OffersBanBifForm;
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

@Service("question158Service")
public class Question158Service extends AbstractQuestionService<OffersBanBifForm> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;

    private static final char REJECT_OFFER = 'R';

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        OffersBanBifForm form = new OffersBanBifForm();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(loanApplication.getPersonId(), false, locale);

                // Generate the offer if it not exists yet
                List<LoanOffer> offers = loanApplicationDao.getAllLoanOffers(loanApplication.getId());
                if(offers == null || offers.isEmpty()){
                    LoanOffer offer = new LoanOffer();
                    offer.setEntityId(Entity.BANBIF);
                    offer.setProduct(catalogService.getProduct(Product.TARJETA_CREDITO));
                    offer.setLoanOfferOrder(1);
                    loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO);

                    LoanOffer offerZeroMembership = new LoanOffer();
                    offerZeroMembership.setEntityId(Entity.BANBIF);
                    offerZeroMembership.setProduct(catalogService.getProduct(Product.TARJETA_CREDITO));
                    offerZeroMembership.setLoanOfferOrder(2);
                    loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offerZeroMembership, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA);

                    offers = loanApplicationDao.getAllLoanOffers(loanApplication.getId());

                }

                LoanOffer principalOffer = offers.stream().filter(e -> e.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO).findFirst().orElse(null);
                LoanOffer zeroMembershipOffer = offers.stream().filter(e -> e.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA).findFirst().orElse(null);
                attributes.put("principalOffer", principalOffer);
                attributes.put("zeroMembershipOffer", zeroMembershipOffer);
                boolean compraDeDeudaActiva = false;
                JSONObject data = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
                if(data != null){
                    BanbifPreApprovedBase approvedBase = new Gson().fromJson(data.toString(),BanbifPreApprovedBase.class);
                    if(approvedBase != null){
                        if(approvedBase.getCompraDeDeuda() != null && approvedBase.getCompraDeDeuda().equalsIgnoreCase("SI")) compraDeDeudaActiva = true;
                        attributes.put("welcomeBonus", approvedBase.getBonoBienvenida().toString());
                        attributes.put("lineValue", approvedBase.getLinea());
                        if(approvedBase.getPlastico() != null && Arrays.asList(BanbifPreApprovedBase.BANBIF_GOLD_CARD,BanbifPreApprovedBase.BANBIF_CLASSIC_CARD,BanbifPreApprovedBase.BANBIF_INFINITE_CARD,BanbifPreApprovedBase.BANBIF_PLATINUM_CARD,BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD,BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD).contains(approvedBase.getPlastico())){
                            attributes.put("cardType", approvedBase.getPlastico());
                            attributes.put("TCEA", getTCEAValue(approvedBase.getPlastico()));
                        }
                        else {
                            attributes.put("cardType", BanbifPreApprovedBase.BANBIF_CLASSIC_CARD);
                            attributes.put("TCEA", getTCEAValue(BanbifPreApprovedBase.BANBIF_CLASSIC_CARD));
                        }
                        attributes.put("TCEA_ZERO_MEMBERSHIP", getTCEAValue(BanbifPreApprovedBase.BANBIF_ZERO_MEMBERSHIP_CARD));
                    }
                }
                attributes.put("abTestingValue", loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey()));
                attributes.put("personName", person.getName());
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                attributes.put("compraDeDeudaActiva", compraDeDeudaActiva);
                attributes.put("showAgent", true);
                attributes.put("showGoBack", false);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, OffersBanBifForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if(form.getLoanApplicationType() == REJECT_OFFER)
                    return "REJECT";
                if(form.getLoanApplicationType() == 'T')
                    return "PHONE_ASSISTANCE";
                else
                    return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, OffersBanBifForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:

                ((OffersBanBifForm.Validator) form.getValidator()).configValidator();
                if (form.getLoanApplicationType() != null && form.getLoanApplicationType().charValue() == REJECT_OFFER) {
                    ((OffersBanBifForm.Validator) form.getValidator()).offerId.setRequired(false);
                }
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, OffersBanBifForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getLoanApplicationType() != REJECT_OFFER) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey(), form.getLoanApplicationType()));

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

    private String getTCEAValue(String cardType){
        switch (cardType){
            case BanbifPreApprovedBase.BANBIF_CLASSIC_CARD:
                return BanbifPreApprovedBase.BANBIF_CLASSIC_CARD_TCEA;
            case BanbifPreApprovedBase.BANBIF_GOLD_CARD:
                return BanbifPreApprovedBase.BANBIF_GOLD_CARD_TCEA;
            case BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD:
                return BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD_TCEA;
            case BanbifPreApprovedBase.BANBIF_PLATINUM_CARD:
                return BanbifPreApprovedBase.BANBIF_PLATINUM_CARD_TCEA;
            case BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD:
                return BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD_TCEA;
            case BanbifPreApprovedBase.BANBIF_INFINITE_CARD:
                return BanbifPreApprovedBase.BANBIF_INFINITE_CARD_TCEA;
            case BanbifPreApprovedBase.BANBIF_ZERO_MEMBERSHIP_CARD:
                return BanbifPreApprovedBase.BANBIF_ZERO_MEMBERSHIP_CARD_TCEA;
        }
        return null;
    }
}

