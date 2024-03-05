/**
 *
 */
package com.affirm.common.service;

import com.affirm.common.model.catalog.ApprovalValidation;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationApprovalValidation;

import java.util.List;

/**
 * @author jrodriguez
 */
public interface LoanApplicationApprovalValidationService {

    boolean validateAndUpdate(int loanApplicationId, int approvalValidationId) throws Exception;

    boolean validateAndUpdate(int loanApplicationId, int approvalValidationId, Boolean forcedValidated) throws Exception;

    boolean validateAndUpdate(int loanApplicationId, int approvalValidationId, Boolean forcedValidated, Integer forcedRejectionReasonId) throws Exception;

    List<ApprovalValidation> getApprovalValidationIds(LoanApplication loanApplication, EntityProductParams entityProductParams);

    boolean loanHasAllValidations(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception;

    boolean loanHasAnyValidationsForManualApproval(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception;

    boolean loanHasAnyFailedValidations(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception;

    boolean existsApprovalValidationInLoan(LoanApplication loanApplication, Integer approvalValidationId, EntityProductParams entityProductParams) throws Exception;

    LoanApplicationApprovalValidation approvalValidationInLoan(LoanApplication loanApplication, Integer approvalValidationId) throws Exception;

    boolean loanHasAnyRejectedValidations(LoanApplication loanApplication, EntityProductParams entityProductParams) throws Exception;

    boolean loanHasAnyValidationsWithCustomStatus(LoanApplication loanApplication, Character status, EntityProductParams entityProductParams) throws Exception;
}
