package com.affirm.client.service.impl;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.service.EmployerService;
import com.affirm.client.service.SalaryAdvanceService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */

@Service("salaryAdvanceService")
public class SalaryAdvanceServiceImpl implements SalaryAdvanceService {

    private static final Logger logger = Logger.getLogger(SalaryAdvanceServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private UserService userService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private EmployerCLDAO employerDao;
    @Autowired
    private EmployerService employerService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private PersonCLDAO personClDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CountryContextService countryContextService;

    @Override
    public LoanApplication registerLoanApplication(Employee employee, String clientIp, char origin, Locale locale, Integer registerType, int countryId) throws Exception {

        EmployerPaymentDay paymentDay = employerService.getEmployerCurrentPaymentDay(employee.getEmployer().getId());
        int shortTermDays = Math.toIntExact(Duration.between(LocalDate.now().atTime(0, 0), paymentDay.getPaymentDay().atTime(0, 0)).toDays());

        // Register the Loan Application
        SalaryAdvanceCalculatedAmount advance = loanApplicationDao.calculateSalaryAdvanceAmmount(employee.getId(), employee.getEmployer().getId(), locale);
        if (advance.getRejectionReasonKey() != null) {
            throw new SqlErrorMessageException(null, messageSource.getMessage(advance.getRejectionReasonKey(), new String[]{employee.getEmployer().getDaysAfterEndOfMonth() + "", employee.getEmployer().getDaysBeforeEndOfMonth() + ""}, locale));
        }
        LoanApplication loanApplication = loanApplicationDao.registerLoanApplication(
                employee.getUserId(), advance.getMaxAmount().intValue(),
                1, LoanApplicationReason.ADELANTO, Product.SALARY_ADVANCE, shortTermDays, Cluster.A, origin, employee.getEmployer().getId(), registerType, null,
                countryId);
        loanApplicationDao.updateProductCategory(loanApplication.getId(), ProductCategory.ADELANTO);
        // Register the ip location
        userService.registerIpUbication(clientIp, loanApplication.getId());
        return loanApplication;
    }

    @Override
    public void sendConfirmationMail(int loanApplicationId, int userId, int personId, String email, Employer employer, Locale locale) {
        new Thread(() -> {
            try {
                // Generate the altered loanApplicationToken
                Map<String, Object> params = new HashMap<>();
                params.put("email", email);
                params.put("confirmationlink", true);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
                String loanApplicationToken = loanApplicationService.generateLoanApplicationToken(userId, personId, loanApplicationId, params);

                // Send the email to client
                Person person = personDao.getPerson(catalogService, locale, personId, false);
                JSONObject jsonVars = new JSONObject();
                jsonVars.put("LINK", Configuration.getClientDomain() + "/salaryadvanceloanapplication/" + loanApplicationToken);
                jsonVars.put("CLIENT_NAME", person.getFirstName());
                jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COMMERCIAL);
                jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COMMERCIAL);
                jsonVars.put("EMPLOYER", employer.getName());

                PersonInteraction interaction = new PersonInteraction();
                interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.VALIDATION_SALARYADVANCE__LOANAPPLICATION_MAIL, loanApplication.getCountryId()));
                interaction.setDestination(email);
                interaction.setLoanApplicationId(loanApplicationId);
                interaction.setPersonId(personId);

                interactionService.sendPersonInteraction(interaction, jsonVars, null);
            } catch (Exception ex) {
                ErrorServiceImpl.onErrorStatic(ex);
            }
        }).start();
    }

    @Override
    public void sendValidationMail(int loanApplicationId, int userId, int personId, Employer employer, Integer emailId, String email, Locale locale) {
        new Thread(() -> {
            try {
                // Generate the altered loanApplicationToken
                Map<String, Object> params = new HashMap<>();
                params.put("email", email);
                params.put("validationlink", true);
                if (emailId != null)
                    params.put("emailid", emailId);
                String loanApplicationToken = loanApplicationService.generateLoanApplicationToken(userId, personId, loanApplicationId, params);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

                // Send the email to client
                Person person = personDao.getPerson(catalogService, locale, personId, false);
                JSONObject jsonVars = new JSONObject();
                jsonVars.put("LINK", Configuration.getClientDomain() + "/salaryadvanceloanapplication/" + loanApplicationToken + "/offer/validate/email");
                jsonVars.put("CLIENT_NAME", person.getFirstName());
                jsonVars.put("EMPLOYER", employer.getName());
                jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COMMERCIAL);
                jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COMMERCIAL);

                PersonInteraction interaction = new PersonInteraction();
                interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.CONFIRMACION_SALARYADVANCE__LOANAPPLICATION_MAIL, loanApplication.getCountryId()));
                interaction.setDestination(email);
                interaction.setLoanApplicationId(loanApplicationId);
                interaction.setPersonId(personId);

                interactionService.sendPersonInteraction(interaction, jsonVars, null);
            } catch (Exception ex) {
                ErrorServiceImpl.onErrorStatic(ex);
            }
        }).start();
    }
}