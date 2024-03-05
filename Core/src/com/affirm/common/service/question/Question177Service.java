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
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.GoToNextQuestionException;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question177Service")
public class Question177Service extends AbstractQuestionService<FormGeneric> {

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
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (loanApplication.getSelectedEntityId() == null) {
                    loanApplicationDao.selectBestLoanOffer(loanApplication.getId());
                }

                // Generate the offer if it not exists yet
                List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                if (offers == null || offers.isEmpty()) {

                    LoanOffer offer = new LoanOffer();
                    offer.setEntityId(loanApplication.getEntityId());
                    offer.setProduct(catalogService.getProduct(Product.VALIDACION_IDENTIDAD));
                    offer.setLoanOfferOrder(1);
                    loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD);

                    offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                }

                if(offers.size() == 1){
                   if(!offers.stream().anyMatch(e -> e.getSelected() != null && e.getSelected())) loanApplicationDao.registerSelectedLoanOffer(offers.get(0).getId(), null);
                }

                throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, FormGeneric form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(id);
        if(offers != null && !offers.isEmpty() && offers.stream().anyMatch(e -> e.getSelected() != null && e.getSelected())) return "DEFAULT";
        return null;
    }
}

