package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.service.EmailBoService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by jarmando on 27/02/17.
 */
@Service
public class EmailBoServiceImpl implements EmailBoService {

    @Autowired
    AwsSesEmailService awsSesEmailService;
    @Autowired
    UserDAO userDAO;
    @Autowired
    CatalogService catalogService;
    @Autowired
    InteractionService interactionService;
    @Autowired
    CreditDAO creditDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    MessageSource messageSource;
    @Autowired
    LoanApplicationDAO loanApplicationDao;
    @Autowired
    LoanApplicationService loanApplicationService;

    public boolean sendRejectionMail(LoanApplication la, Locale locale) throws Exception {
        //GET REASON EMAIL VAR
        String rejectionReasonMail = null;
        if (la.getRejectionPolicyKey() != null) {//FILTRO
            rejectionReasonMail = messageSource.getMessage(la.getRejectionPolicyKey() + ".mail", null, locale);
        }
        if (la.getRejectionReason() != null) {//MANUAL
            rejectionReasonMail = la.getRejectionReason().getReasonMail();
        }
        if (rejectionReasonMail == null) {
            if (la.getCreditId() != null) {
                Credit credit = creditDAO.getCreditByID(la.getCreditId(), locale, false, Credit.class);
                return sendRejectionMail(credit, locale);
            }
            return false;
        }

        //PREPARE MAIL TEXT AND MAIL TEXT VARS
        InteractionContent interactionContent = catalogService.getInteractionContent(InteractionContent.REJECTION_MAIL, la.getCountryId());
        JSONObject json = new JSONObject();
        Person person = personDAO.getPerson(catalogService, locale, la.getPersonId(), false);
        json.put("CLIENT_NAME", person.getFirstName());
        json.put("REJECT_MAIL_REASON", rejectionReasonMail);
        json.put("AGENT_FULLNAME", la.getAgent() != null ? la.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        json.put("AGENT_IMAGE_URL", la.getAgent() != null ? la.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        //GET THE USER MAIL
        Integer userId = la.getUserId();
        User user = userDAO.getUser(userId);
        String mailUser = user.getEmail();

        //Create interaction:
        PersonInteraction personInteraction = new PersonInteraction();
        personInteraction.setInteractionType(interactionContent.getType());
        personInteraction.setDestination(mailUser);
        personInteraction.setCreditId(la.getCreditId());
        personInteraction.setInteractionContent(interactionContent);
        personInteraction.setPersonId(la.getPersonId());
        personInteraction.setLoanApplicationId(la.getId());

        //REGISTER INTERACTION ON DB
        interactionService.sendPersonInteraction(personInteraction, json, null);
        return true;
    }

    public boolean sendRejectionMail(Credit credit, Locale locale) throws Exception {
        //GET REASON EMAIL VAR
        String rejectionReasonMail = null;
        if (credit.getRejectionReason() != null) {
            rejectionReasonMail = credit.getRejectionReason().getReasonMail();
        } else {
            return false;
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

        //PREPARE MAIL TEXT AND MAIL TEXT VARS
        InteractionContent interactionContent = catalogService.getInteractionContent(InteractionContent.REJECTION_MAIL, loanApplication.getCountryId());
        JSONObject json = new JSONObject();
        Person person = personDAO.getPerson(catalogService, locale, credit.getPersonId(), false);
        json.put("CLIENT_NAME", person.getFirstName());
        json.put("REJECT_MAIL_REASON", rejectionReasonMail);
        json.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        json.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        //GET THE USER MAIL
        Integer userId = credit.getUserId();
        User user = userDAO.getUser(userId);
        String mailUser = user.getEmail();

        //Create interaction:
        PersonInteraction personInteraction = new PersonInteraction();
        personInteraction.setInteractionType(interactionContent.getType());
        personInteraction.setDestination(mailUser);
        personInteraction.setCreditId(credit.getId());
        personInteraction.setCreditCode(credit.getCode());
        personInteraction.setInteractionContent(interactionContent);
        personInteraction.setPersonId(credit.getPersonId());
        personInteraction.setLoanApplicationId(credit.getLoanApplicationId());

        //REGISTER AND SEND INTERACTION ON DB
        interactionService.sendPersonInteraction(personInteraction, json, null);
        return true;
    }

    @Override
    public void sendPasswordEmployersMail(String employerName, String employerRuc, JSONArray users) {
        StringBuilder html = new StringBuilder();

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String toEmail = Configuration.hostEnvIsLocal() ? Configuration.EMAIL_DEV_TO : user.getString("email");

            html.append("Email: ").append(user.getString("email")).append(" , Password: '").append(user.getString("passwordBeforeHash")).append("'<br/>");

            awsSesEmailService.sendEmail(
                    Configuration.EMAIL_CONTACT_FROM(),//from
                    toEmail,//to
                    null,
                    "Passwords Extranet Funcionario",//subject
                    null,
                    html.toString(),//message
                    null
            );
        }
    }

    @Override
    public void sendLinkReturningAssistedProcess(LoanApplication loanApplication, Locale locale) throws Exception {
        InteractionContent interactionContent = catalogService.getInteractionContent(InteractionContent.RETURN_ASSISTED_PROCESS, loanApplication.getCountryId());
        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        JSONObject json = new JSONObject();

        Integer userId = loanApplication.getUserId();
        User user = userDAO.getUser(userId);
        String mailUser = user.getEmail();

        PersonInteraction personInteraction = new PersonInteraction();
        personInteraction.setInteractionType(interactionContent.getType());
        personInteraction.setDestination(mailUser);
        personInteraction.setInteractionContent(interactionContent);
        personInteraction.setPersonId(loanApplication.getPersonId());
        personInteraction.setLoanApplicationId(loanApplication.getId());

        json.put("CLIENT_NAME", person.getFirstName());
        json.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        json.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        json.put("LINK", loanApplicationService.generateLoanApplicationLinkEntity(loanApplication));

        //REGISTER AND SEND INTERACTION ON DB
        interactionService.sendPersonInteraction(personInteraction, json, null);
    }
}