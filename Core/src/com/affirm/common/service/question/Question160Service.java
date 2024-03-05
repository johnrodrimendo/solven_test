package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.AddressForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question160Service")
public class Question160Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private RccDAO rccDAO;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private UserDAO userDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();

        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

                String personTittle;
                if (person.getGender() == null) {
                    personTittle = "Estimado(a)";
                } else if (person.getGender() == 'M') {
                    personTittle = "Estimado";
                } else {
                    personTittle = "Estimada";
                }

                String message = "por el momento no cuentas con este producto aprobado mediante este canal, pero puedes obtener tu Tarjeta de Crédito BanBif otorgando como garantía un depósito a plazo en cualquiera de nuestras oficinas. Mayor información";
                LoanApplicationEvaluation evaluation = loanApplicationDao.getLastEvaluation(loanApplication.getId(), locale);
                LoanApplicationEvaluation evaluationWithPolicy = null;
                if(evaluation != null){
                    evaluationWithPolicy = loanApplicationDao.getEvaluations(loanApplication.getId(), locale).stream().filter(e -> e.getId().equals(evaluation.getId())).findFirst().orElse(null);
                }
                if(evaluationWithPolicy != null && evaluationWithPolicy.getPolicy() != null && evaluationWithPolicy.getPolicy().getPolicyId() == Policy.BANBIF_OTRA_NACIONALIDAD){
                    message = "te recomendamos acercarte a nuestras oficinas para que nuestros ejecutivos te orienten sobre las opciones que podamos tener para ti.";
                }
                else if(evaluationWithPolicy != null && evaluationWithPolicy.getPolicy() != null && evaluationWithPolicy.getPolicy().getPolicyId() == Policy.BANBIF_RESULTADO_CUESTIONARIO){
                    message = "has superado la cantidad máxima de intentos para validar tu identidad. Te recomendamos acercarte a  nuestras oficinas para que nuestros ejecutivos te orienten sobre las opciones que podamos tener para tí.";
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("showAgent", true);
                attributes.put("showGoBack", false);
                attributes.put("personName", person.getName() != null ? person.getName() : "");
                attributes.put("personTittle", personTittle);
                attributes.put("message", message);
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
                LoanApplication loanApplication = null;
                AddressForm form = null;
                switch (path) {
                    case "leadSavingAccount":
                        loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                        User user = userDao.getUser(loanApplication.getUserId());

//                        sendSavingAccountEmail(loanApplication, user, person);
                        return AjaxResponse.redirect("https://www.banbif.com.pe/personas/Cuenta-Digital-moneda?utm_source=Referral&utm_medium=Solven_Rechazo&utm_campaign=Captaci%C3%B3n_CAD");
                }
                break;
        }
        throw new Exception("No method configured");
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                LoanApplicationPreliminaryEvaluation loanPreliminaryEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), locale, entityBranding != null && entityBranding.getBranded() ? entityBranding : null, true);
                LoanApplicationPreliminaryEvaluation fullLoanPreliminaryEvaluation = null;
                if (loanPreliminaryEvaluation != null) {
                    int lastPreliminaryId = loanPreliminaryEvaluation.getId();
                    fullLoanPreliminaryEvaluation = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), locale).stream()
                            .filter(p -> p.getId().equals(lastPreliminaryId))
                            .findFirst().orElse(null);
                }
                if(fullLoanPreliminaryEvaluation != null && fullLoanPreliminaryEvaluation.getHardFilter() != null && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.BASE_BANBIF){
                    Date baseValidUntil = rccDAO.getBanbifBaseValidUntil();
                    Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
                    if(currentDate.after(baseValidUntil)){
                        return "REJECT_MESSAGE";
                    }
                }
                break;
        }
        return null;
    }

    public void sendSavingAccountEmail(LoanApplication loanApplication, User user, Person person) throws Exception{
        JSONObject entityCData = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
        String entityCustomDataValueS = entityCData != null ? entityCData.toString() : null;
        BanbifPreApprovedBase banbifPreApprovedBaseR =  new Gson().fromJson(entityCustomDataValueS, BanbifPreApprovedBase.class);
        String personName = person != null && person.getName() != null ? person.getName() : "";
        String personLastname = person != null && person.getFirstSurname() != null ? person.getFirstSurname() : "";
        if(banbifPreApprovedBaseR != null){
            if(banbifPreApprovedBaseR.getNombres() != null && !banbifPreApprovedBaseR.getNombres().trim().isEmpty()) personName = banbifPreApprovedBaseR.getNombres().trim();
            if(banbifPreApprovedBaseR.getApellidos() != null && !banbifPreApprovedBaseR.getApellidos().trim().isEmpty()) personLastname = banbifPreApprovedBaseR.getApellidos().trim().split(" ")[0];
        }
        String subject = "Interesado solicitud Solven - Tarjeta de Crédito con garantia líquida";
        String bodyMessage = "Esta persona esta interesada: <br>"+
                "Nombre: {{name}} <br> Apellido: {{lastname}} <br> Tipo Doc.: {{docType}} <br> Número Doc.: {{docNumber}} <br> Email: {{email}} <br> Teléfono: {{phone}} <br> ";
        bodyMessage = bodyMessage
                .replace("{{name}}", personName)
                .replace("{{lastname}}", personLastname)
                .replace("{{phone}}", user.getPhoneNumber())
                .replace("{{email}}", user.getEmail())
                .replace("{{docType}}", person.getDocumentType().getName())
                .replace("{{docNumber}}", person.getDocumentNumber());

        awsSesEmailService.sendRawEmail(
                null,
                "notificaciones@solven.pe",
                null,
                "tarjetas@banbif.com.pe",
                new String[]{"DFLORES@banbif.com.pe"},
                subject,
                bodyMessage,
                bodyMessage,
                null,
                null, null, null, null);
    }
}

