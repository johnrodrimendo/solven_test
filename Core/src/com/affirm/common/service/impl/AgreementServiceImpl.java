package com.affirm.common.service.impl;

import com.affirm.abaco.client.AbacoClient;
import com.affirm.abaco.client.EMensaje;
import com.affirm.abaco.client.ERptaCredito;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.equifax.ws.CuotaHistorica;
import com.affirm.equifax.ws.DirectorioPersona;
import com.affirm.equifax.ws.ReporteCrediticio;
import com.affirm.solven.client.SolvenClient;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Service("agreementService")
public class AgreementServiceImpl implements AgreementService {

    private static final Logger logger = Logger.getLogger(AgreementServiceImpl.class);

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
    private UserDAO userDao;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private AbacoClient abacoClient;
    @Autowired
    private SolvenClient solvenClient;
    @Autowired
    private CountryContextService countryContextService;

//    @Override
//    public LoanApplication registerLoanApplication(Employee employee, String clientIp, char origin, Locale locale, Integer registerType, int loanApplicationReasonId, HttpServletRequest request) throws Exception {
//
////        EmployerPaymentDay paymentDay = employerService.getEmployerCurrentPaymentDay(employee.getEmployer().getId());
////        int shortTermDays = Math.toIntExact(Duration.between(LocalDate.now().atTime(0, 0), paymentDay.getPaymentDay().atTime(0, 0)).toDays());
//
//        Product agreementProduct = catalogService.getProduct(Product.AGREEMENT);
//        SalaryAdvanceCalculatedAmount advance = loanApplicationDao.calculateAgreementAmmount(employee.getId(), employee.getEmployer().getId(), locale);
//        if (advance.getRejectionReasonKey() != null) {
//            throw new SqlErrorMessageException(advance.getRejectionReasonKey(), null);
//        }
//
//        // Register the Loan Application
//        LoanApplication loanApplication = loanApplicationDao.registerLoanApplication(
//                employee.getUserId(), advance.getMaxAmount().intValue(),
//                agreementProduct.getMaxInstallments(), loanApplicationReasonId, Product.AGREEMENT, null, Cluster.A, origin, employee.getEmployer().getId(), registerType,
//                null, countryContextService.getCountryParamsByRequest(request).getId());
//
//        // Register the ip location
//        userService.registerIpUbication(clientIp, loanApplication.getId());
//
//        return loanApplication;
//    }

    @Override
    public void confirmDisbursement(int creditId, HttpServletRequest request, HttpServletResponse response, Locale locale, SpringTemplateEngine templateEngine) throws Exception {
        Credit credit = creditDao.getCreditByID(creditId, locale, true, Credit.class);
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), true);
        User user = userDao.getUser(person.getUserId());

        byte[] contractBytes = creditService.createContract(creditId, request, response, locale, templateEngine, null, false);

        credit = creditDao.getCreditByID(creditId, locale, true, Credit.class);
        creditService.sendDisbursementConfirmationEmailAndSms(credit, person, user, contractBytes);
    }

    @Override
    public void createAssociatedCredit(Person person, Credit credit) throws Exception {
        ERptaCredito socioResponse = getAsociatedByEmployeeEntity(person.getDocumentType().getId(), person.getDocumentNumber(), credit.getEntity().getId(), credit.getLoanApplicationId());
        switch (credit.getEntity().getId()) {
            case Entity.ABACO:
                credit = creditDao.getCreditByID(credit.getId(), Configuration.getDefaultLocale(), false, Credit.class);

                creditDao.generateOriginalSchedule(credit);
                List<OriginalSchedule> originalSchedule = creditDao.getOriginalSchedule(credit.getId());
                credit.setOriginalSchedule(originalSchedule);

                PersonBankAccountInformation accountInfo = personDao.getPersonBankAccountInformationByCredit(Configuration.getDefaultLocale(), credit.getPersonId(), credit.getId());
                credit.setBank(accountInfo.getBank());
                credit.setBankAccount(accountInfo.getBankAccount());
                credit.setBankAccountType(accountInfo.getBankAccountType());

                ERptaCredito abacoCredit = abacoClient.crearCredito(socioResponse.getIdSocio(), socioResponse.getIdCredito(), socioResponse.getSaldoCredito(), credit, credit.getLoanApplicationId(), null);

                if (abacoCredit.isExitoso()) {
                    creditDao.updateSignatureDate(credit.getId(), new Date());
                    creditDao.updateCrediCodeByCreditId(credit.getId(), abacoCredit.getCodigoOperacion()/*RandomStringUtils.randomAlphanumeric(5)*/);
                } else {
                    throw new SqlErrorMessageException(null, "Error " + abacoCredit.getCodigoMensaje());
                }
                break;
            case Entity.AFFIRM:
                com.affirm.solven.client.ERptaCredito solvenCredit = solvenClient.crearCredito(socioResponse.getIdSocio(), socioResponse.getIdCredito(), socioResponse.getSaldoCredito(), credit);
                creditDao.updateCrediCodeByCreditId(credit.getId(), solvenCredit.getIdCredito());
                break;
        }
    }

    @Override
    public ERptaCredito getAsociatedByEmployeeEntity(int docType, String docNumber, int entityId, Integer loanApplicationId) throws Exception {
        switch (entityId) {
            case Entity.ABACO:
                ERptaCredito socioAbaco = abacoClient.esSocioAbaco(docType, docNumber, loanApplicationId);
//                if(socioAbaco != null && socioAbaco.isExitoso() && socioAbaco.getIdSocio() != null && !socioAbaco.getIdSocio().isEmpty()){
//                    int personId = personDao.getPersonIdByDocument(docType, docNumber);
//                    personDao.registerAssociated(personId, entityId, socioAbaco.getIdSocio(), null);
//                    personDao.validateAssociated(personId, entityId, true);
//                }
                // TODO Check if there is an error to throw
                return socioAbaco;
//            case Entity.AFFIRM:
//                ERptaCredito socioSolven = solvenClient.esSocio(docType, docNumber);
//                // TODO Check if there is an error to throw
//                return socioSolven;
        }
        return null;
    }

    @Override
    public void sendAssociatedToEntity(int personId, int entityId, Locale locale, Integer loanApplicationId) throws Exception {

        Person person = personDao.getPerson(catalogService, locale, personId, false);
        User user = userDao.getUser(person.getUserId());
        user.setPerson(person);
        ERptaCredito associatedResponse = getAsociatedByEmployeeEntity(person.getDocumentType().getId(), person.getDocumentNumber(), entityId, loanApplicationId);
        PersonAssociated associated = personDao.getAssociated(personId, entityId);
        ReporteCrediticio equifaxResult = personDao.getBureauResult(person.getId(), ReporteCrediticio.class);
        DirectorioPersona directorioPersona = null;
        CuotaHistorica cuotaHistorica = null;
        if (equifaxResult != null) {
            directorioPersona = equifaxResult.getModulos().getModulo().stream()
                    .filter(m -> m.getCodigo().equals("602") && m.getData().getAny() instanceof DirectorioPersona)
                    .map(m -> (DirectorioPersona) m.getData().getAny()).findAny()
                    .orElse(null);
            cuotaHistorica = equifaxResult.getModulos().getModulo().stream()
                    .filter(m -> m.getCodigo().equals("798") && m.getData().getAny() instanceof CuotaHistorica)
                    .map(m -> (CuotaHistorica) m.getData().getAny()).findAny()
                    .orElse(null);
        }
        PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, person.getId());
        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, person.getId());
        PersonOcupationalInformation principalOcupation = null;
        if (ocupations != null)
            principalOcupation = ocupations.stream().filter(o -> o.getActivityType() != null && o.getActivityType().getId() == ActivityType.DEPENDENT).findAny().orElse(null);

        if (associatedResponse == null) {
            EMensaje abacoResponse = sendAssociatedToEntity(null, user, directorioPersona, principalOcupation, cuotaHistorica, contactInformation, entityId, loanApplicationId);
            personDao.registerAssociated(personId, entityId, abacoResponse.getIdSocio(), null);
        }
        // If doesnt exist in our DB, update it in Abaco, create it in our DB and activate the flag
        else if (associated == null) {
            EMensaje abacoResponse = sendAssociatedToEntity(associatedResponse.getIdSocio(), user, directorioPersona, principalOcupation, cuotaHistorica, contactInformation, entityId, loanApplicationId);
            personDao.registerAssociated(personId, entityId, abacoResponse.getIdSocio(), null);
            personDao.validateAssociated(personId, entityId, true);
        }
        // If exists in both sides, update the associated in abaco
        else {
            sendAssociatedToEntity(associatedResponse.getIdSocio(), user, directorioPersona, principalOcupation, cuotaHistorica, contactInformation, entityId, loanApplicationId);
        }
    }

    private EMensaje sendAssociatedToEntity(String associatedCode, User user, DirectorioPersona directorioPersona, PersonOcupationalInformation principalOcupation, CuotaHistorica cuotaHistorica, PersonContactInformation contactInformation, int entityId, Integer loanApplicationId) throws Exception {
        switch (entityId) {
            case Entity.ABACO:
                EMensaje abacoResponse = abacoClient.enviarSocioAbaco(associatedCode, user, directorioPersona, principalOcupation, cuotaHistorica, contactInformation, loanApplicationId);
                logger.debug("Abaco response: " + abacoResponse.getCodigoMensaje() + " -> " + abacoResponse.getMensaje());
                if (!abacoResponse.isExitoso()) {
                    throw new Exception("Error sending associated to Abaco");
                }
                return abacoResponse;
            case Entity.AFFIRM:
                EMensaje solvenResponse = new EMensaje();
                solvenResponse.setCodigoMensaje(0);
                solvenResponse.setIdSocio(RandomStringUtils.random(5));
                solvenResponse.setNroCuentaAportacion(RandomStringUtils.random(5));
                return solvenResponse;
        }
        return null;
    }
}