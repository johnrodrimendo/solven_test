package com.affirm.dialogflow;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class DialogflowService {

    private static final Logger logger = Logger.getLogger(DialogflowService.class);

    private static String clientAlreadyHaveActiveLAMessage = "Ya %s con una solicitud activa, %s aquí (%s) y %s con tu solicitud";
    private static String wrongPhoneNumberMessage = "%s. Por favor %s a ingresar tu número personal";
    private static String userAlreadyHasRegisteredEmailErrorMessage = "El email ingresado no coincide con uno previamente registrado (%s). Por favor, %s este correo";
    private static String alreadyRegisteredEmailErrorMessage = "%s Por favor, %s un correo para vincular tu solicitud";
    private static String invalidPhoneFormatArgentinaMessage = "El número no es válido. Ingresálo nuevamente, no antepongas el cero (0) en código de área ni añadas el 15";
    private static String wrongDocumentDni = "El DNI debe tener 8 digitos. Por favor, vuelve a escribír tu número de documento.";
    private static String wrongDocumentCe = "El CE debe tener entre 9 a 12 dígitos y/o caracteres. Por favor, vuelve a escribír tu número de documento.";
    private static String wrongDocumentCuit = "El documento debe tener 11 digitos. Por favor, volvé a escribir tu número de documento.";

    private final LoanApplicationService loanApplicationService;
    private final CatalogService catalogService;
    private final UserService userService;
    private final UtilService utilService;
    private final PersonDAO personDAO;
    private final LoanApplicationDAO loanApplicationDAO;
    private final UserDAO userDAO;
    private final MessageSource messageSource;

    public DialogflowService(LoanApplicationService loanApplicationService, CatalogService catalogService, UserService userService, UtilService utilService, PersonDAO personDAO, LoanApplicationDAO loanApplicationDAO, UserDAO userDAO, MessageSource messageSource) {
        this.loanApplicationService = loanApplicationService;
        this.catalogService = catalogService;
        this.userService = userService;
        this.utilService = utilService;
        this.personDAO = personDAO;
        this.loanApplicationDAO = loanApplicationDAO;
        this.userDAO = userDAO;
        this.messageSource = messageSource;
    }

    public void preValidateUserHasNotActiveLoanApplication(DialogflowRequest data) throws Exception {
        validateDocumentNumber(data);

        User user = userDAO.getUserByDocument(data.getIdentityDocument(), data.getIdentityDocumentNumber());
        if (user != null) {
            LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), ProductCategory.CONSUMO);
            if (loanApplication != null) {
                throw new SqlErrorMessageException(null,
                        String.format(
                                clientAlreadyHaveActiveLAMessage,
                                data.getCountry() == CountryParam.COUNTRY_PERU ? "cuentas" : "contás",
                                loanApplicationService.generateLoanApplicationLinkEntity(loanApplication),
                                data.getCountry() == CountryParam.COUNTRY_PERU ? "ingresa" : "ingresá",
                                data.getCountry() == CountryParam.COUNTRY_PERU ? "continua" : "continuá"
                        )
                );
            }
        }
    }

    public void registerFirstInstanceLoanApplication(DialogflowRequest data) throws Exception {
        User user = userService.getOrRegisterUser(data.getIdentityDocument(), data.getIdentityDocumentNumber(), null, data.getName(), data.getLastname());

        if ((data.getBirthdate() != null && !data.getBirthdate().isEmpty()) && (CountryParam.COUNTRY_ARGENTINA == data.getCountry() || data.getIdentityDocument() == IdentityDocumentType.CE)) {
            Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), user.getPersonId(), false);
            personDAO.updateBirthday(person.getId(), utilService.parseDate(data.getBirthdate(), "dd/MM/yyyy", Configuration.getDefaultLocale()));
        }

        userDAO.registerFacebookMessengerId(user.getId(), data.getPlatformUserId());

        LoanApplication loanApplication = loanApplicationDAO.registerLoanApplication(
                user.getId(),
                DialogflowRequest.defaulAmount(data.getCountry()),
                DialogflowRequest.defaultInstallments(data.getCountry()),
                data.getLoanApplicationReason(),
                null,
                null,
                null,
                LoanApplication.ORIGIN_MESSENGER,
                null,
                null,
                null,
                data.getCountry()
        );

        List<Agent> agentsList = catalogService.getFormAssistantsAgents(null);

        loanApplicationDAO.updateProductCategory(loanApplication.getId(), ProductCategory.CONSUMO);
        loanApplicationDAO.updateFormAssistant(loanApplication.getId(), agentsList.get(new Random().nextInt(agentsList.size())).getId());

        loanApplicationDAO.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());

//        Only if Argentina, start running the evaluation, so the bots doesnt take too long
        if (data.getCountry() == CountryParam.COUNTRY_ARGENTINA) {
            loanApplicationService.runEvaluationBot(loanApplication.getId(), false);
        }

        loanApplication = loanApplicationDAO.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());
        loanApplicationDAO.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUN_IOVATION);
    }

    public void updateLoanApplicationEmail(DialogflowRequest data) throws Exception {
        StringFieldValidator validator = new StringFieldValidator(ValidatorUtil.EMAIL, data.getEmail()).setRequired(true);
        if (!validator.validate(Configuration.getDefaultLocale())) {
            throw new SqlErrorMessageException(null, validator.getErrors());
        }

        User user = userDAO.getUserByDocument(data.getIdentityDocument(), data.getIdentityDocumentNumber());

        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(data.getEmail())) {
            throw new SqlErrorMessageException(null,
                    String.format(
                            userAlreadyHasRegisteredEmailErrorMessage,
                            user.getEmail(),
                            data.getCountry() == CountryParam.COUNTRY_PERU ? "ingresa" : "ingresá"
                    )
            );
        } else if (user.getEmail() == null) {
            LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), ProductCategory.CONSUMO);

            try {
                int emailId = userDAO.registerEmailChange(loanApplication.getUserId(), data.getEmail().toLowerCase());
                userDAO.validateEmailChange(loanApplication.getUserId(), emailId);
            } catch (SqlErrorMessageException e) {
                String errorMessage = messageSource.getMessage(e.getMessageKey(), null, Configuration.getDefaultLocale());
                throw new SqlErrorMessageException(null,
                        String.format(
                                alreadyRegisteredEmailErrorMessage,
                                errorMessage,
                                data.getCountry() == CountryParam.COUNTRY_PERU ? "ingresa" : "ingresá"
                        )
                );
            }

        }
    }

    public String updateLoanApplicationPhoneAndReturnLink(DialogflowRequest data) throws Exception {
        if (CountryParam.COUNTRY_PERU == data.getCountry()) {
            StringFieldValidator validator = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE, data.getPhoneNumber()).setRequired(true);
            if (!validator.validate(Configuration.getDefaultLocale())) {
                throw new SqlErrorMessageException(null, validator.getErrors());
            }
        } else if (CountryParam.COUNTRY_ARGENTINA == data.getCountry()) {
            String argentinaPhoneValidatorRegex = "^((\\(\\d{2}\\) ?\\d{4})|(\\(\\d{3}\\) ?\\d{3})|(\\(\\d{4}\\) ?\\d{2}))( |\\-|)\\d{4}$";
            StringFieldValidator phoneValidator = new StringFieldValidator().setRequired(true).setMaxCharacters(15).setMinCharacters(10).setValidRegex(argentinaPhoneValidatorRegex);
            StringFieldValidator validator = new StringFieldValidator(phoneValidator, data.getPhoneNumber());

            if (!validator.validate(Configuration.getDefaultLocale())) {
                throw new SqlErrorMessageException(null, invalidPhoneFormatArgentinaMessage);
            } else {

                data.setPhoneNumber(data.getPhoneNumber().trim());

                if (data.getPhoneNumber().indexOf(' ') > -1) {
                    data.setAreaCode(data.getPhoneNumber().substring(1, data.getPhoneNumber().indexOf(' ') - 1));
                    data.setPhoneNumber(data.getPhoneNumber().substring(data.getPhoneNumber().indexOf(' ') + 1));
                } else {
                    data.setAreaCode(data.getPhoneNumber().substring(1, data.getPhoneNumber().indexOf(')')));
                    data.setPhoneNumber(data.getPhoneNumber().substring(data.getPhoneNumber().indexOf(')') + 1));
                }

                if (data.getPhoneNumber().indexOf('-') == -1) {
                    String lastFourCharacters = data.getPhoneNumber().substring(data.getPhoneNumber().length() - 4);
                    String firstNumbers = data.getPhoneNumber().substring(0, data.getPhoneNumber().indexOf(lastFourCharacters));
                    data.setPhoneNumber(firstNumbers + '-' + lastFourCharacters);
                }

                logger.debug(data.getAreaCode());
                logger.debug(data.getPhoneNumber());

            }
        }

        User user = userDAO.getUserByDocument(data.getIdentityDocument(), data.getIdentityDocumentNumber());
        LoanApplication loanApplication = loanApplicationDAO.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), ProductCategory.CONSUMO);

        String phoneNumber = data.getAreaCode() != null ? "(" + data.getAreaCode() + ") " + data.getPhoneNumber() : data.getPhoneNumber();

        try {
            userDAO.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", phoneNumber);
        } catch (SqlErrorMessageException e) {
            String errorMessage = messageSource.getMessage(e.getMessageKey(), null, Configuration.getDefaultLocale());
            throw new SqlErrorMessageException(null,
                    String.format(
                            wrongPhoneNumberMessage,
                            errorMessage,
                            data.getCountry() == CountryParam.COUNTRY_PERU ? "vuelve" : "volvé"
                    )
            );
        }

        return loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
    }

    private void validateDocumentNumber(DialogflowRequest data) {
        if (CountryParam.COUNTRY_PERU == data.getCountry()) {
            if (data.getIdentityDocument() == IdentityDocumentType.DNI) {
                StringFieldValidator dniValidator = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI);
                StringFieldValidator validator = new StringFieldValidator(dniValidator, data.getIdentityDocumentNumber());

                if (!validator.validate(Configuration.getDefaultLocale())) {
                    throw new SqlErrorMessageException(null, wrongDocumentDni);
                }
            } else if (data.getIdentityDocument() == IdentityDocumentType.CE) {
                StringFieldValidator ceValidator = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_CE);
                ceValidator.setMaxCharacters(12);
                StringFieldValidator validator = new StringFieldValidator(ceValidator, data.getIdentityDocumentNumber());

                if (!validator.validate(Configuration.getDefaultLocale())) {
                    throw new SqlErrorMessageException(null, wrongDocumentCe);
                }
            }
        } else if (CountryParam.COUNTRY_ARGENTINA == data.getCountry()) {
            StringFieldValidator cuitValidator = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_CUIT_PERSONAL);
            StringFieldValidator validator = new StringFieldValidator(cuitValidator, data.getIdentityDocumentNumber());

            if (!validator.validate(Configuration.getDefaultLocale())) {
                throw new SqlErrorMessageException(null, wrongDocumentCuit);
            }
        }
    }

}
