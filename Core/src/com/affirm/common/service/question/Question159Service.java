package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.WaitingApprovalBanBifForm;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question159Service")
public class Question159Service extends AbstractQuestionService<WaitingApprovalBanBifForm> {

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
        WaitingApprovalBanBifForm form = new WaitingApprovalBanBifForm();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                if (loanApplication.getStatus().getId() == LoanApplicationStatus.EVAL_APPROVED) {
                    loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.WAITING_APPROVAL, null);
                }
                if (loanApplication.getCreditId() == null) {
                    loanApplicationService.approveLoanApplication(loanApplication.getId(), null, null, null, null, locale);
                }

                String banBifLoanApplicationType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey());

                if (LoanApplication.BANBIF_LOAN_APPLICATION_TYPE_TELEPHONE.equals(banBifLoanApplicationType)) {
                    User user = userService.getUser(loanApplication.getUserId());
                    attributes.put("phoneNumber", user.getPhoneNumber());
                }

                attributes.put("entityProductParamId", loanApplication.getSelectedEntityProductParameterId());
                attributes.put("banBifLoanApplicationType", banBifLoanApplicationType);
                attributes.put("form", form);
                attributes.put("showGoBack", false);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, WaitingApprovalBanBifForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, WaitingApprovalBanBifForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:

                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, WaitingApprovalBanBifForm form, Locale locale) throws Exception {
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

