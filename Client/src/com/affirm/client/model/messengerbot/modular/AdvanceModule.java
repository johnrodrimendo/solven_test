package com.affirm.client.model.messengerbot.modular;

import com.affirm.client.model.form.UserRegisterForm;
import com.affirm.client.model.messengerbot.SessionData;
import com.affirm.client.model.messengerbot.contract.Button;
import com.affirm.client.service.MessengerSession;
import com.affirm.client.service.SalaryAdvanceService;
import com.affirm.client.service.UserCLService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.LoanApplicationRegisterType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.impl.SyncBotService;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.Util;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 18/01/17.
 */
@Component
public class AdvanceModule implements MessengerModule {

    @Autowired
    UserDAO userDao;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserCLService userCLService;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    LoanApplicationService loanApplicationService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    MessengerSession messengerSession;
    @Autowired
    SalaryAdvanceService salaryAdvanceService;
    @Autowired
    GlobalModule globalModule;
    @Autowired
    SyncBotService syncBotService;

    Gson gson = new Gson();

    Product getProduct() {
        try { return catalogService.getProduct(Product.SALARY_ADVANCE);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Override
    public String getModuleName() {
        return SessionData.M_SALARY_ADVANCE;
    }

    @Override
    public boolean isActive() {
        return getProduct().getActive();
    }

    @Override
    public List<String> getReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        switch (data.getCurrentState()){
            case SessionData.SALARY_ADVANCE_HAS_EMAIL:
                return getPostBackReplies(senderId, text.trim(), dataArray);
            case SessionData.SALARY_ADVANCE_EMAIL:
                StringFieldValidator emailVal = new StringFieldValidator(ValidatorUtil.EMAIL, text.trim())
                        .setEmailFormatErrorMsg("bot.email.error")
                        .setMaxCharactersErrorMsg("bot.string.maxCharacters.")
                        .setMinCharactersErrorMsg("bot.string.minCharacters.");
                emailVal.validate(data.getLocale());
                if(emailVal.isHasErrors()) {
                    return errorRetry(data, emailVal.getErrors());
                }
                String email = text.trim();
                data.setCurrentAnswer(email);

                List<Employee> employees;
                employees = personDAO.getEmployeesByEmail(email, data.getLocale());

                if (employees == null || employees.isEmpty() || employees.get(0) == null) {
                    data.setCurrentState(SessionData.GLOBAL_NEW);
                    return toList(toJsonSimpleMessage(senderId, "Lo sentimos, no perteneces a una empresa asociada a este producto."));
                }

                // TODO Show to the user all the employees that he is associated, so he can chose one. For now we pick the first one
                Employee employee = employees.get(0);

                // Check there is no rejection reason
                SalaryAdvanceCalculatedAmount advancecalculated = loanApplicationDAO.calculateSalaryAdvanceAmmount(employee.getId(), employee.getEmployer().getId(), data.getLocale());
                if (advancecalculated.getRejectionReasonKey() != null) {
                    data.setCurrentState(SessionData.GLOBAL_NEW);
                    return toList(toJsonLocaleMessage(data, advancecalculated.getRejectionReasonKey(), null));
                }
                // Create an user if it doesnt exist

                //init user
                User userMessenger  = userDao.getUserByFacebookMessengerId(senderId);
                User userEmp = null;
                if(employee.getUserId() != null)
                    userEmp = userDao.getUser(employee.getUserId());

                if(userEmp != null) {//There is employee user
                    if(userMessenger != null) {//there is messenger
                        boolean userMessengerEmployeeMatch = userEmp.getId().equals(userMessenger.getId());
                        if (!userMessengerEmployeeMatch) {//different user id reject
                            return toList(toJsonSimpleMessage(senderId, "Lo siento este correo ya está asociado a otro messenger."));
                        }
                    }
                    else {//same user id register
                        userDao.registerFacebookMessengerId(userEmp.getId(), senderId);
                    }
                }
                else {//register new user or bring existing user, and asociate with employee
                    // Register the user
                    userEmp = userDao.registerUser(null, null, null, employee.getDocType().getId(), employee.getDocNumber(), null);
                    // Asociate the employee with the person
                    personDAO.updateEmployeePersonOnly(employee.getId(), userEmp.getPersonId());
                    employee.setUserId(userEmp.getId());
                    employee.setPersonId(userEmp.getPersonId());
                    //finally asociate user with messenger
                    if(userMessenger != null) {//there is messenger
                        boolean userMessengerEmployeeMatch = userEmp.getId().equals(userMessenger.getId());
                        if (!userMessengerEmployeeMatch) {//different user id reject
                            return toList(toJsonSimpleMessage(senderId, "Lo siento este correo ya está asociado a otro messenger."));
                        }
                    }
                    else {//same user id register
                        userDao.registerFacebookMessengerId(userEmp.getId(), senderId);
                    }
                }
                // Get the active loanApplication or create it
                LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), userEmp.getPersonId(), Product.SALARY_ADVANCE);
                if (loanApplication == null) {
                    loanApplication = salaryAdvanceService.registerLoanApplication(employee, "127.0.0.0", 'M', data.getLocale(), 1, CountryParam.COUNTRY_PERU);//TODO ASK FOR LOCATION
                }

                if (email != null) {
                    // Send email with the loan application link
                    salaryAdvanceService.sendConfirmationMail(loanApplication.getId(), userEmp.getId(), userEmp.getPersonId(), employee.getWorkEmail(), employee.getEmployer(), data.getLocale());
                    data.setCurrentState(SessionData.GLOBAL_NEW);
                    return toList(toJsonSimpleMessage(senderId, "Te hemos enviado un correo con el link de tu crédito."));
                }
                data.setCurrentState(SessionData.GLOBAL_NEW);
                return toList(toJsonSimpleMessage(senderId, "Error."));
            //return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_TYPE, data));
            case SessionData.SALARY_ADVANCE_DOC_TYPE:
                String key = catalogService.getIdentityDocumentTypes().stream()
                        .filter(e -> e.getName().trim().equalsIgnoreCase(text.trim()))
                        .map(e -> e.getId().toString()).findFirst().orElse(null);
                data.setCurrentAnswer(key);
                if (key != null)
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_NUMBER, data));
                else
                    return errorRetryLocale(data, "messengerbot.error.buttons");
            case SessionData.SALARY_ADVANCE_DOC_NUMBER:
                Integer docTypeId = Integer.parseInt(data.getAnswer(SessionData.SALARY_ADVANCE_DOC_TYPE));
                StringFieldValidator vDocNumber;
                //set validator
                if (docTypeId == IdentityDocumentType.DNI)
                    vDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI, text.trim())
                            .setMaxCharactersErrorMsg("bot.string.maxCharacters.")
                            .setMinCharactersErrorMsg("bot.string.minCharacters.");
                else if (docTypeId == IdentityDocumentType.CE)
                    vDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_CE, text.trim())
                            .setMaxCharactersErrorMsg("bot.string.maxCharacters.")
                            .setMinCharactersErrorMsg("bot.string.minCharacters.");
                else {
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_TYPE, data));
                }
                //validate
                if (vDocNumber.validate(data.getLocale())) {
                    if (docTypeId == IdentityDocumentType.DNI) {
                        Reniec reniec = personDAO.getReniecDBData(text.trim());
                        if (reniec == null) {
                            return errorRetryLocale(data, "bot.error.document.notfound");
                        }
                    }
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_NUMBER_SURE, data));
                }
                return errorRetry(data, vDocNumber.getErrors());
            case SessionData.SALARY_ADVANCE_DOC_NUMBER_SURE:
                return getPostBackReplies(senderId, text.trim(), dataArray);
            case SessionData.SALARY_ADVANCE_CE_BIRTHDAY:
                Date birthdate = Util.parseDate(text.trim(), "dd/MM/yyyy") != null ?
                        Util.parseDate(text.trim(), "dd/MM/yyyy") :
                        Util.parseDate(text.trim(), "dd-MM-yyyy");
                if(birthdate == null)
                    return errorRetryLocale(data, "bot.error.date");
                Integer queryId = syncBotService.callMigraciones(data.getAnswer(SessionData.SHORT_TERM_DOC_NUMBER), birthdate);
                Configuration.MIGRACION_EXECUTOR.execute(() -> {
                    try{ Thread.sleep(25000); } catch(Exception e) {}
                    boolean correct = syncBotService.migracionesSuccess(queryId);
                    data.setCurrentState(SessionData.GLOBAL_NEW);
                    try {
                        if (correct) {
                            send(endByDocument(data));//LINK!
                        } else {
                            data.setCurrentState(SessionData.SHORT_TERM_DOC_TYPE);
                            send(errorRetryLocale(data, "bot.ce.date.mismatch"));//INCORRECT DATE
                        }
                    } catch (Exception e) {
                        send(toList(toJsonLocaleMessage(data, "system.error.default")));//CONNECTION ERROR.
                    }
                });
                return toList(toJsonLocaleMessage(data, "bot.ce.sync.validation"));
        }
        return null;
    }

    @Override
    public List<String> getPostBackReplies(String senderId, String text, SessionData[] dataArray) throws Exception {
        SessionData data = dataArray[0];
        switch (data.getCurrentState()){
            case SessionData.SALARY_ADVANCE_HAS_EMAIL:
                if (isCurrentAnswerYes(text, data)){
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_EMAIL, data));
                }
                else {
                    data.setCurrentState(SessionData.SALARY_ADVANCE_DOC_TYPE);
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_TYPE, data));
                }
            case SessionData.SALARY_ADVANCE_DOC_TYPE:
                data.setAnswer(SessionData.SALARY_ADVANCE_DOC_NUMBER, "");
                boolean valid = catalogService.getIdentityDocumentTypes()
                        .stream().anyMatch(e -> e.getId().toString().trim().equals(text.trim()));
                if (valid) {
                    data.setCurrentAnswer(text.trim());
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_NUMBER, data));
                } else
                    return errorRetryLocale(data, "messengerbot.error.buttons");
            case SessionData.SALARY_ADVANCE_DOC_NUMBER_SURE:
                if (isCurrentAnswerYes(text, data)) {
                    return endByDocument(data);
                    /*
                    if(data.getAnswer(SessionData.SALARY_ADVANCE_DOC_TYPE).equals(String.valueOf(IdentityDocumentType.CE))) {
                        return toList(jsonGoTo(SessionData.SALARY_ADVANCE_CE_BIRTHDAY, data));
                    }
                    else {//dni end
                        return endByDocument(data);
                    }*/
                } else {
                    return toList(jsonGoTo(SessionData.SALARY_ADVANCE_DOC_TYPE, data));
                }
            default:
                return getGlobalModule().getPostBackReplies(senderId, text, dataArray);
        }
    }

    private List<String> endByDocument(SessionData  data) throws Exception {
        ///init
        String senderId = data.getProfile().getId();
        Integer docType = Util.intOrNull(data.getAnswer(SessionData.SALARY_ADVANCE_DOC_TYPE));
        String docNumber = data.getAnswer(SessionData.SALARY_ADVANCE_DOC_NUMBER);
        //init user
        User user = null;
        User userMessenger = userDao.getUserByFacebookMessengerId(senderId);

        //this messenger exists
        if(userMessenger != null) {
            Person person = personDAO.getPerson(catalogService, data.getLocale(), userMessenger.getPersonId(), false);
            String docNumberMessenger = person.getDocumentNumber();
            Integer docTypeMessenger = person.getDocumentType().getId();
            if(!docNumber.equals(docNumberMessenger) || !docType.equals(docTypeMessenger)){
                return toList(toJsonLocaleMessage(data, "messengerbot.document.mismatch"));
            }
            user = userMessenger;
        }

        //employee may or may not have an user
        List<Employee> employees = personDAO.getEmployeesByDocument(docType, docNumber, data.getLocale());

        if (employees == null || employees.isEmpty()) {
            data.setCurrentState(SessionData.GLOBAL_NEW);
            return toList(toJsonSimpleMessage(senderId, "Lo sentimos, no perteneces a una empresa asociada a este producto."));
        }

        Employee employee = employees.get(0);

        if(user == null) {//messenger q consulta no asociado.
            if (employee.getUserId() == null) {//no user for that employee We will create/bring it and asociate it.
                // Register the user or bring existing
                user = userDao.registerUser(null, null, null, employee.getDocType().getId(), employee.getDocNumber(), null);
                // Asociate the employee with the person
                personDAO.updateEmployeePersonOnly(employee.getId(), user.getPersonId());
                employee.setUserId(user.getId());
                employee.setPersonId(user.getPersonId());
            } else {//employee has a user continue.
                user = userDao.getUser(employee.getUserId());
                employee.setUserId(user.getId());
                employee.setPersonId(user.getPersonId());
            }
            userDao.registerFacebookMessengerId(user.getId(), senderId);
        }
        LoanApplication[] las = new LoanApplication[]{null};
        List<String> listJson = toList(jsonEndLoanApp(data, employee, user.getPersonId(), null, new boolean[]{false}, las));
        //if (user.getPhoneNumber() == null)
        //    loanApplicationDAO.registerNoAuthLinkExpiration(las[0].getId(), 60);
        return listJson;
    }

    @Override
    public String jsonGoTo(String destinyState, SessionData data) throws Exception {
        switch (destinyState){
            case SessionData.SALARY_ADVANCE_REGEN:
                return renewLink(data);
            case SessionData.SALARY_ADVANCE_HAS_EMAIL:
                User user = getUserDao().getUserByFacebookMessengerId(data.getProfile().getId());
                if(user != null) {
                    data.setUser(user);
                    Person person = personDAO.getPerson(catalogService, data.getLocale(), user.getPersonId(), true);
                    List<Employee> employees = personDAO.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), data.getLocale());

                    if (employees == null || employees.isEmpty()) {
                        data.setCurrentState(SessionData.GLOBAL_NEW);
                        return toJsonLocaleMessage(data, "bot.advance.reject.company");
                    }
                    LoanApplication loanApp = getLoanApplicationDAO().getActiveLoanApplicationByPerson(data.getLocale(), user.getPersonId(), Product.SALARY_ADVANCE);
                    if (loanApp != null) {//tiene loan app activo
                        data.setCurrentState(SessionData.GLOBAL_NEW);
                        return toJsonLocaleMessage(data, "bot.advance.active");
                    }
                }
                data.setCurrentState(destinyState);
                return jsonYesNoOptions("bot.advance.has.email", data);
            case SessionData.SALARY_ADVANCE_EMAIL:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "bot.advance.email");
            case SessionData.SALARY_ADVANCE_DOC_TYPE:
                data.setAnswer(SessionData.SALARY_ADVANCE_DOC_NUMBER, "");
                List<Button> buttonList = //new ArrayList<>();
                        catalogService.getIdentityDocumentTypes().stream().map(
                                d -> MessageFactory.newPostBackButton(d.getName(), d.getId().toString())
                        ).collect(Collectors.toList());
                data.setCurrentState(destinyState);
                return MessageFactory.newQuickReplyMessage(
                        messageSource.getMessage("messengerbot.loan.doctype.go", null, data.getLocale()),
                        buttonList).toJson(data.getProfile().getId(), gson);
            case SessionData.SALARY_ADVANCE_DOC_NUMBER:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "messengerbot.loan.docnumber.go");
            case SessionData.SALARY_ADVANCE_DOC_NUMBER_SURE:
                data.setCurrentState(destinyState);
                return jsonSureOptions(data);
            case SessionData.SALARY_ADVANCE_CE_BIRTHDAY:
                data.setCurrentState(destinyState);
                return toJsonLocaleMessage(data, "bot.birthday.go");
            default:
                throw new RuntimeException(destinyState + " was not found in jsonGoTo " + getModuleName());
        }
    }

    public String renewLink(SessionData data) throws Exception {
        User user = userDao.getUserByFacebookMessengerId(data.getProfile().getId());
        if(user == null){
            return toJsonSimpleMessage(
                    data.getProfile().getId(),
                    "Lo sentimos, tu messenger ya no está asociado a solven.");
        }
        Integer personId = user.getPersonId();
        LoanApplication active = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), personId, Product.SHORT_TERM);
        if(active == null) {
            return toJsonSimpleMessage(
                    data.getProfile().getId(),
                    "Lo sentimos, actualmente no tienes una solicitud de crédito a corto plazo activa.");
        }
        String loanLink = getLoanUrl(active, true);
        Locale locale = data.getLocale();
        String senderId = data.getProfile().getId();
        String messageEnd = "messengerbot.loan.active.withnumber.end";
        String json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.main", null, locale), Configuration.CLIENT_PRD_URL)
        ).toJson(senderId, gson);
        if (user.getPhoneNumber() == null) {// open 30 seconds (regen)
            loanApplicationDAO.registerNoAuthLinkExpiration(active.getId(), 60);
            messageEnd = "messengerbot.loan.active.nonumber.end";
            json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                    MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                    MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.loan.active.nonumber.regen", null, locale), MessengerModule.PB_REGEN_SHORT)
            ).toJson(senderId, gson);
        }
        messengerSession.clean(senderId);
        messengerSession.cleanOld();//removes old conversations
        return json;
    }

    User getUserByDocumentOrCreate(SessionData data) throws Exception {
        User user;
        Integer doctype = Integer.parseInt(data.getAnswer(SessionData.SALARY_ADVANCE_DOC_TYPE));
        String docnumber = data.getAnswer(SessionData.SALARY_ADVANCE_DOC_NUMBER);
        user = userDao.getUserByDocument(doctype, docnumber);
        if (user == null) {
            UserRegisterForm userForm = new UserRegisterForm();
            userForm.setDocNumber(docnumber);
            userForm.setDocType(doctype);
            // Register the user
            user = userCLService.registerUserFacebookMessenger(userForm, data.getProfile().getId());
        }
        //true cuando no hay otro messenger registrado en la tabla
        //boolean userAsociated = userDao.registerFacebookMessengerId(user.getId(), data.getProfile().getId());
        data.setUser(user);
        return user;
    }

    public String jsonEndLoanApp(SessionData data, Employee emp, Integer personId, LoanApplication newloanApplication, boolean[] esNuevo, LoanApplication... las) throws Exception {
        if (newloanApplication == null) {
            try {
                salaryAdvanceService.registerLoanApplication(emp, "127.0.0.0", 'M', data.getLocale(), LoanApplicationRegisterType.DNI, CountryParam.COUNTRY_PERU);
            } catch (SqlErrorMessageException se) {
                data.setCurrentState(SessionData.GLOBAL_NEW);
                return toJsonLocaleMessage(data, se.getMessageKey());
            }
        }
        newloanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), personId, Product.SALARY_ADVANCE);

        String loanLink = getLoanUrl(newloanApplication, true);
        Locale locale = data.getLocale();
        String senderId = data.getProfile().getId();
        String messageEnd = "messengerbot.loan.active.withnumber.end";
        String json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.main", null, locale), Configuration.CLIENT_PRD_URL)
        ).toJson(senderId, gson);
        /*if (data.getUser().getPhoneNumber() == null) {// open 30 seconds (regen)
            loanApplicationDAO.registerNoAuthLinkExpiration(newloanApplication.getId(), 60);
            messageEnd = "messengerbot.loan.active.nonumber.end";
            json = MessageFactory.newCTAMessage(messageSource.getMessage(messageEnd, null, locale),
                    MessageFactory.newWebUrlButton(messageSource.getMessage("messengerbot.loan.active.link", null, locale), loanLink),
                    MessageFactory.newPostBackButton(messageSource.getMessage("messengerbot.loan.active.nonumber.regen", null, locale), MessengerModule.PB_REGEN_ADVANCE)
            ).toJson(senderId, gson);
        }*/
        if (esNuevo[0]) {
            messageEnd = "messengerbot.loan.new.end";//nuevo
            //loanApplicationDAO.updateMessengerLink(newloanApplication.getId(), true);
        }
        messengerSession.clean(senderId);
        messengerSession.cleanOld();//removes old conversations
        data.setCurrentState(SessionData.GLOBAL_NEW);
        return json;
    }
/*
    private LoanApplication createLoanApp(SessionData data, boolean[] esNuevo) throws Exception {
        LoanApplication newloanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(data.getLocale(), data.getUser().getPersonId(), Product.SALARY_ADVANCE);
        if (newloanApplication != null)
            return newloanApplication;
        Integer userId = data.getUser().getId();
        Integer ammount = null;//TODO CHECK
        Integer reason = LoanApplicationReason.ADELANTO;
        Integer productId;
        Integer declaredClusterId = 7;//default value
        //set days or months
        Integer monthsOr1 = 1;//default value
        Integer daysOrNull = null;//default value
        //if (true) {
        //set days
        daysOrNull = null;//TODO CHECK
        productId = Product.SALARY_ADVANCE;
        //  } else {
        //    // set months
        //    monthsOr1 = Integer.parseInt(data.getAnswer(SessionData.TERM));
        //    productId = Product.TRADITIONAL;
        }
        newloanApplication = loanApplicationDAO.registerLoanApplication(
                userId,
                ammount,
                monthsOr1,
                reason,
                productId,
                daysOrNull,
                declaredClusterId,
                'M', null);

        // Solucion un poco fea :(
       // newloanApplication = loanApplicationDAO.getLoanApplication(newloanApplication.getId(), data.getLocale());

        // Check if there is a preliminaryEvaluation
        //LoanApplicationPreliminaryEvaluation preEvaluation = loanApplicationService
                .getLastPreliminaryEvaluation(newloanApplication.getId(), data.getLocale());
        //if (preEvaluation == null) {
            // Call the start preliminary evaluation
          //  loanApplicationDAO.startPreliminaryEvaluation(newloanApplication.getId());

            // Execute the evaluatino
        //    loanApplicationDAO.executePreliminaryEvaluation(newloanApplication.getId());
        //    esNuevo[0] = true;
        //}//TODO CHECK
        return newloanApplication;
    }
*/
    @Override
    public Gson getGson() {
        return gson;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    @Override
    public UserDAO getUserDao() {
        return userDao;
    }

    @Override
    public LoanApplicationDAO getLoanApplicationDAO() {
        return loanApplicationDAO;
    }

    @Override
    public LoanApplicationService getLoanApplicationService() {
        return loanApplicationService;
    }

    @Override
    public boolean isProduct() {
        return getProduct() != null;
    }

    @Override
    public String[] options() {
        return (toString()+";adelanto;advance").split(";");
    }

    @Override
    public UserCLService getUserCLService() {
        return userCLService;
    }

    @Override
    public String toString() {
        return getProduct().getShortName();
    }

    @Override
    public MessengerModule getGlobalModule() {
        return globalModule;
    }
}
