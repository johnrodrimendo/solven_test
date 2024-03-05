package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.WaitingApprovalBanBifForm;
import com.affirm.common.model.transactional.BanbifPreApprovedBase;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.FormGeneric;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question162Service")
public class Question162Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                String banBifLoanApplicationType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey());

                if (!loanApplication.getStatus().getId().equals(LoanApplicationStatus.LEAD_REFERRED)) {
                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.LEAD_REFERRED, null);
//                    loanApplicationService.sendBanBifTCLead("GOOD_SCORE", loanApplication.getId(),locale);
                }

                BanbifPreApprovedBase banbifPreApprovedBase = new BanbifPreApprovedBase();
                if(loanApplication.getEntityCustomData() != null && loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
                    banbifPreApprovedBase = new Gson().fromJson(loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey()).toString(),BanbifPreApprovedBase.class);
                }

                Boolean showBoxOffer = null;
                if(loanApplication.getEntityId() == Entity.BANBIF && loanApplication.getProductCategoryId() == ProductCategory.TARJETA_CREDITO){
                    showBoxOffer = true;
                }else{
                    showBoxOffer = false;
                }

                Boolean showImgOffer = null;
                if(banbifPreApprovedBase != null && banbifPreApprovedBase.getPld() != null){
                    if(banbifPreApprovedBase.getPld() == BanbifPreApprovedBase.HAS_OFFER)
                        showImgOffer = true;

                    if(banbifPreApprovedBase.getPld() == BanbifPreApprovedBase.HAS_NO_OFFER)
                        showImgOffer = false;
                }
                attributes.put("loanApplication", loanApplication);
                attributes.put("showBoxOffer", showBoxOffer);
                attributes.put("showImgOffer", showImgOffer);
                attributes.put("showAgent", true);
                attributes.put("banBifLoanApplicationType", banBifLoanApplicationType);
                attributes.put("showGoBack", false);
                break;
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

