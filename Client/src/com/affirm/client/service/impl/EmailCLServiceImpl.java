package com.affirm.client.service.impl;

import com.affirm.client.model.form.AdvanceContactForm;
import com.affirm.client.model.form.CompanyContactForm;
import com.affirm.client.model.form.ContactForm;
import com.affirm.client.model.form.ProcessContactForm;
import com.affirm.client.service.EmailCLService;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by jrodriguez on 27/09/16.
 */
@Service
public class EmailCLServiceImpl implements EmailCLService {
    private static Logger logger = Logger.getLogger(EmailCLServiceImpl.class);

    @Autowired
    AwsSesEmailService awsSesEmailService;
    @Autowired
    LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    InteractionService interactionService;
    @Autowired
    EntityExtranetService entityExtranetService;

    @Override
    public void sendContactMail(ContactForm contactForm) {
        awsSesEmailService.sendEmail(
                Configuration.EMAIL_CONTACT_FROM(),//from
                Configuration.EMAIL_CONTACT_TO(),//to
                null,
                contactForm.getContactSubject(),//subject
                null,
                "De: " + contactForm.getContactFullName() + "<br/>" + "Teléfono: " + contactForm.getContactTelephone() + "<br/>" + "Email: " + contactForm.getContactEmail() + "<br/>" + "Asunto: " + contactForm.getContactSubject() + "<br/>" + "Mensaje: " + contactForm.getContactMessage(),//message
                null
        );
    }

    @Override
    public void sendProcessContactMail(ProcessContactForm processContactForm) {
        awsSesEmailService.sendEmail(
                Configuration.EMAIL_CONTACT_FROM(),//from
                Configuration.EMAIL_CONTACT_TO(),//to
                null,
                "Desde el proceso",//subject
                null,
                "De: " + processContactForm.getContactFullName() + "<br/>" + "Teléfono: " + processContactForm.getContactTelephone() + "<br/>" + "Email: " + processContactForm.getContactEmail() + "<br/>" + "Asunto: " + processContactForm.getContactSubject() + "<br/>" + "Mensaje: " + processContactForm.getContactMessage(),//message
                null
        );
    }

    @Override
    public void sendCompanyContactMail(CompanyContactForm form) {
        awsSesEmailService.sendEmail(
                Configuration.EMAIL_CONTACT_FROM(),//from
                Configuration.EMAIL_CONTACT_TO(),//to
                null,
                "Contacto de empresa",//subject
                null,
                "De: " + form.getContactFullName() + "<br/>" +
                        "Email: " + form.getEmail() + "<br/>" +
                        "Motivo: " + form.getReason() + "<br/>" +
                        "Ocupación: " + form.getOcupation() + "<br/>" +
                        "Numero telefónico: " + form.getPhoneNumber() + "<br/>" +
                        "Mensaje: " + form.getMessage(),//message
                null
        );
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
    public void sendAdvanceContactMail(AdvanceContactForm contactForm, int productId) {
        String productName = productId == Product.SALARY_ADVANCE ? "adelanto de sueldo" : "crédito por convenio";
        String annex = contactForm.getAnnexTelephone() != null ? "Anexo: " + contactForm.getAnnexTelephone() + "\n" : "";
        String body = contactForm.getContactFullName() + " de " + contactForm.getContactCompany() + " (" + contactForm.getContactNumberEmployee().replace('-', '.')
                + " colaboradores) quiere que su empresa cuente con el producto " + productName + ".\n" +
                "Teléfono: " + contactForm.getContactTelephone() + "\n" +
                annex +
                "<br/>Correo: " + contactForm.getContactEmail() + "\n";

        awsSesEmailService.sendEmail(
                Configuration.EMAIL_CONTACT_FROM(),//from
                Configuration.EMAIL_CONTACT_TO(),//to
                null,
                productId == Product.SALARY_ADVANCE ? "-Contacto, Producto Adelanto-" : "Contacto, Producto Convenio-",//subject
                null,
                body,//message
                null
        );
    }

    @Override
    public boolean sendRejectionMail(Credit credit, Locale locale) throws Exception {
        //GET REASON EMAIL VAR
        String rejectionReasonMail = null;
        if (credit.getRejectionReason() != null) {
            rejectionReasonMail = credit.getRejectionReason().getReasonMail();
        } else {
            return false;
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
        InteractionContent interactionContent = null;
        if(credit.getEntity().getId() == Entity.AZTECA){
             interactionContent = catalogService.getInteractionContent(InteractionContent.REJECTED_MAIL_AZTECA, loanApplication.getCountryId());
        }else{
             interactionContent = catalogService.getInteractionContent(InteractionContent.REJECTION_MAIL, loanApplication.getCountryId());
        }

        Person person = personDAO.getPerson(catalogService, locale, credit.getPersonId(), false);
        User user = userDAO.getUser(person.getUserId());

        //Create interaction:
        PersonInteraction personInteraction = new PersonInteraction();
        personInteraction.setInteractionType(interactionContent.getType());
        personInteraction.setDestination(user.getEmail());
        personInteraction.setCreditId(credit.getId());
        personInteraction.setCreditCode(credit.getCode());
        personInteraction.setInteractionContent(interactionContent);
        personInteraction.setPersonId(credit.getPersonId());
        personInteraction.setLoanApplicationId(credit.getLoanApplicationId());

        //PREPARE MAIL TEXT AND MAIL TEXT VARS
        JSONObject json = new JSONObject();
        json.put("CLIENT_NAME", person.getFirstName());
        json.put("REJECT_MAIL_REASON", rejectionReasonMail);
        json.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        json.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        if(credit.getEntity().getId() == Entity.AZTECA) {
            String link =
                    (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) ? loanApplicationService.generateLoanApplicationLinkEntityMailing(loanApplication, "?utm_source=mailing&utm_medium=email_mkt&utm_campaign=fup_banbif")
                            : loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
            if (loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && Arrays.asList(ProductCategory.CONSUMO, ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId())) {
                if (loanApplication.getProductCategoryId() == ProductCategory.CONSUMO)
                    link = "https://www.alfinbanco.pe/prestamos";
                else if (loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA)
                    link = "https://www.alfinbanco.pe/ahorro";
            }
            json.put("LINK", link);
        }
        //REGISTER AND SEND INTERACTION ON DB
        interactionService.sendPersonInteraction(personInteraction, json, null);
        return true;
    }

    @Override
    public boolean sendRejectionMailEvaluation(Integer loanApplicationId, Locale locale) throws Exception {
        //GET REASON EMAIL VAR
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        String rejectionReasonMail = null;
        if (loanApplication.getRejectionReason() != null) {
            rejectionReasonMail = loanApplication.getRejectionReason().getReasonMail();
        } else {
            return false;
        }

        InteractionContent interactionContent = null;
        if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA){
            interactionContent = catalogService.getInteractionContent(InteractionContent.REJECTED_MAIL_AZTECA, loanApplication.getCountryId());
        } else {
            return false;
        }

        Person person = personDAO.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
        User user = userDAO.getUser(person.getUserId());

        //Create interaction:
        PersonInteraction personInteraction = new PersonInteraction();
        personInteraction.setInteractionType(interactionContent.getType());
        personInteraction.setDestination(user.getEmail());
        personInteraction.setLoanApplicationId(loanApplication.getId());
        personInteraction.setLoanApplicationCode(loanApplication.getCode());
        personInteraction.setInteractionContent(interactionContent);
        personInteraction.setPersonId(loanApplication.getPersonId());

        //PREPARE MAIL TEXT AND MAIL TEXT VARS
        JSONObject json = new JSONObject();
        json.put("CLIENT_NAME", person.getFirstName());
        json.put("REJECT_MAIL_REASON", rejectionReasonMail);
        json.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        json.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        if(loanApplication.getEntityId() == Entity.AZTECA) {
            String link =
                    (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) ? loanApplicationService.generateLoanApplicationLinkEntityMailing(loanApplication, "?utm_source=mailing&utm_medium=email_mkt&utm_campaign=fup_banbif")
                            : loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
            if (loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && Arrays.asList(ProductCategory.CONSUMO, ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId())) {
                if (loanApplication.getProductCategoryId() == ProductCategory.CONSUMO)
                    link = "https://www.alfinbanco.pe/prestamos";
                else if (loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA)
                    link = "https://www.alfinbanco.pe/ahorro";
            }
            json.put("LINK", link);
        }
        //REGISTER AND SEND INTERACTION ON DB
        interactionService.sendPersonInteraction(personInteraction, json, null);
        return true;
    }
}
