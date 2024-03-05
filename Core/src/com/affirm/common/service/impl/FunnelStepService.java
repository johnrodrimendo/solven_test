package com.affirm.common.service.impl;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PreliminaryEvaluationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.FunnelStep;
import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProcessQuestionCategory;
import com.affirm.common.model.transactional.*;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("funnelStepService")
public class FunnelStepService {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private CreditDAO creditDAO;

    public void registerStep(LoanApplication loanApplication) throws Exception {
        int initialCount = loanApplication.getFunnelSteps() != null ? loanApplication.getFunnelSteps().size() : 0;
        List<LoanApplicationPreliminaryEvaluation> preEvaluations = null;
        User user = null;

        // Step 1. If loan exists
        if (!hasStep(loanApplication, FunnelStep.REGISTERED)) {
            addStep(loanApplication, FunnelStep.REGISTERED);
        }
        // Step 2. If its status pre-approved or it have an approved preliminary evaluation()
        if (!hasStep(loanApplication, FunnelStep.PRE_EVALUATION_APPROVED)) {
            if (loanApplication.getStatus().getId() == LoanApplicationStatus.PRE_EVAL_APPROVED) {
                addStep(loanApplication, FunnelStep.PRE_EVALUATION_APPROVED);
            } else {
                if(loanApplicationDao.hasStatusInLog(loanApplication.getId(), LoanApplicationStatus.PRE_EVAL_APPROVED)){
                    addStep(loanApplication, FunnelStep.PRE_EVALUATION_APPROVED);
                }
            }
        }
        // Step 3. If its status pre-approved or it have an approved preliminary evaluation()
        if (!hasStep(loanApplication, FunnelStep.PIN_VALIDATED)) {
            if (hasStep(loanApplication, FunnelStep.PRE_EVALUATION_APPROVED)) {
                if (loanApplication.getAuxData().getPhoneValidated() != null && loanApplication.getAuxData().getPhoneValidated()) {
                    addStep(loanApplication, FunnelStep.PIN_VALIDATED);
                }
            }
        }
        // Step 4. If it have identity validation before offer. ??
        if (!hasStep(loanApplication, FunnelStep.APPROVED_VALIDATION)) {

        }
        // Step 5. If it have run a evaluation
        if (!hasStep(loanApplication, FunnelStep.REQUEST_COMPLETE)) {
            if (hasStep(loanApplication, FunnelStep.PRE_EVALUATION_APPROVED)) {
                if (loanApplication.getQuestionSequence().stream().anyMatch(q -> q.getId() == ProcessQuestion.Question.Constants.RUNNING_EVALUATION)) {
                    addStep(loanApplication, FunnelStep.REQUEST_COMPLETE);
                }
            }
        }
        // Step 6. If it has an offer (this also validate the banbif offer question)
        if (!hasStep(loanApplication, FunnelStep.REQUEST_WITH_OFFER)) {
            if (loanApplication.getQuestionSequence().stream().anyMatch(q -> q.getId() == ProcessQuestion.Question.Constants.OFFER || q.getId() == ProcessQuestion.Question.Constants.BANBIF_OFFERS || q.getId() == ProcessQuestion.Question.Constants.BANK_ACCOUNT_OFFERS)) {
                addStep(loanApplication, FunnelStep.REQUEST_WITH_OFFER);
            }
        }
        // Step 7. If an offer has been selected
        if (!hasStep(loanApplication, FunnelStep.ACCEPTED_OFFER)) {
            List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
            if (offers != null && offers.stream().anyMatch(o -> o.getSelected() != null && o.getSelected())) {
                addStep(loanApplication, FunnelStep.ACCEPTED_OFFER);
            }
        }
        // Step 8. If it's in a validation question (beetwen offer and final question)
        if (!hasStep(loanApplication, FunnelStep.VALIDATION)) {
            if (loanApplication.getCurrentProcessQuestion().getCategory().getId() == ProcessQuestionCategory.VERIFICATION) {
                addStep(loanApplication, FunnelStep.VALIDATION);
            }
        }
        // Step 9. If it's in the contract signature question
        if (!hasStep(loanApplication, FunnelStep.SIGNATURE)) {
            if (loanApplication.getQuestionSequence().stream().anyMatch(q -> q.getId() == ProcessQuestion.Question.Constants.CONTRACT_SIGNATURE)) {
                addStep(loanApplication, FunnelStep.SIGNATURE);
            }
        }
        // Step 10.
        if (!hasStep(loanApplication, FunnelStep.VERIFICATION)) {
        }
        // Step 11. If it's in the contract signature question (if has passed through question 62)
        if (!hasStep(loanApplication, FunnelStep.APPROBATION)) {
            if (loanApplication.getQuestionSequence().stream().anyMatch(q -> q.getId() == ProcessQuestion.Question.Constants.WAITING_APPROVAL)) {
                addStep(loanApplication, FunnelStep.APPROBATION);
            }
        }
        // Step 12. If it's in the contract signature question (if has passed through question 62)
        if (!hasStep(loanApplication, FunnelStep.DISBURSEMENT)) {
            if (loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if (credit != null && (credit.getStatus().getId() == CreditStatus.ORIGINATED || credit.getStatus().getId() == CreditStatus.ORIGINATED_DISBURSED)) {
                    addStep(loanApplication, FunnelStep.DISBURSEMENT);
                }
            }
        }
        // Step 13. If it's in the contract signature question (if has passed through question 62)
        if (!hasStep(loanApplication, FunnelStep.DISBURSED)) {
            if (loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if (credit != null && credit.getStatus().getId() == CreditStatus.ORIGINATED_DISBURSED) {
                    addStep(loanApplication, FunnelStep.DISBURSED);
                }
            }
        }
        // Step 17. If it's credit and has the commitment generated
        if (!hasStep(loanApplication, FunnelStep.COMMITMENT_GENERATED)) {
            if (loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if (credit != null && credit.getStatus().getId() == CreditStatus.PENDING_PAYMENT) {
                    addStep(loanApplication, FunnelStep.COMMITMENT_GENERATED);
                }
            }
        }
        // Step 18. If it's credit and the commitment is payed
        if (!hasStep(loanApplication, FunnelStep.COMMITMENT_PAID)) {
            if (loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if (credit != null && credit.getStatus().getId() == CreditStatus.PAYED) {
                    addStep(loanApplication, FunnelStep.COMMITMENT_PAID);
                }
            }
        }
        // Step 19. If it's credit and the commitment is pending for confirmation
        if (!hasStep(loanApplication, FunnelStep.COMMITMENT_PENDING_CONFIRMATION)) {
            if (loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if (credit != null && credit.getStatus().getId() == CreditStatus.PENDING_CONFIRMATION_BT) {
                    addStep(loanApplication, FunnelStep.COMMITMENT_PENDING_CONFIRMATION);
                }
            }
        }
        // Step 20. If it's credit and the commitment is informed
        if (!hasStep(loanApplication, FunnelStep.COMMITMENT_PAYMENT_INFORMED)) {
            if (loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if (credit != null && credit.getStatus().getId() == CreditStatus.PAYED_INFORMED) {
                    addStep(loanApplication, FunnelStep.COMMITMENT_PAYMENT_INFORMED);
                }
            }
        }
        // Step 22. If loan exists,
        if (!hasStep(loanApplication, FunnelStep.REGISTERED_ALFIN)) {
            if(hasStep(loanApplication, FunnelStep.PRE_EVALUATION_APPROVED)){
                user = userDAO.getUser(loanApplication.getUserId());
                if(user.getEmail() != null && user.getPhoneNumber() != null)
                    addStep(loanApplication, FunnelStep.REGISTERED_ALFIN);
            }
        }

        // If the size of the list of steps has changed, then update the loan
        if (initialCount != loanApplication.getFunnelSteps().size()) {
            loanApplicationDao.updateFunnelStep(loanApplication.getId(), loanApplication.getFunnelSteps());
        }
    }

    private boolean hasStep(LoanApplication loanApplication, int stepId) {
        if (loanApplication.getFunnelSteps() != null) {
            return loanApplication.getFunnelSteps().stream().anyMatch(s -> s.getStepId() == stepId);
        }
        return false;
    }

    private void addStep(LoanApplication loanApplication, int stepId) {
        if (loanApplication.getFunnelSteps() == null)
            loanApplication.setFunnelSteps(new ArrayList<>());
        LoanApplicationFunnelStep step = new LoanApplicationFunnelStep();
        step.setStepId(stepId);
        step.setDate(new Date());
        loanApplication.getFunnelSteps().add(step);
    }

}
