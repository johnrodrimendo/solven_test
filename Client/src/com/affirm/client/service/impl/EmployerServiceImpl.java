package com.affirm.client.service.impl;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.form.RegisterEmployeeForm;
import com.affirm.client.service.EmailCLService;
import com.affirm.client.service.EmployerService;
import com.affirm.common.dao.EmployerDAO;
import com.affirm.common.model.catalog.EntityProduct;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.catalog.InteractionType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.EmployerPaymentDay;
import com.affirm.common.model.transactional.MultiCreditPayment;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service
public class EmployerServiceImpl implements EmployerService {

    private static Logger logger = Logger.getLogger(EmployerServiceImpl.class);

    @Autowired
    private EmployerCLDAO employerClDao;
    @Autowired
    private EmployerDAO employerDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private ExtranetCompanyServiceImpl extranetCompanyService;
    @Autowired
    private EmailCLService emailCLService;

    @Override
    public EmployerPaymentDay getEmployerCurrentPaymentDay(Integer employerId) {
        LocalDate actualDate = LocalDate.now();
        List<EmployerPaymentDay> paymentDays = employerClDao.getEmployerPaymentDays(employerId);
        if (paymentDays == null)
            return null;
        return paymentDays.stream()
                .filter(p -> actualDate.getMonthValue() == p.getMonth().getMonthValue() && actualDate.getYear() == p.getMonth().getYear()).findFirst().orElse(null);
    }

    @Override
    public JSONArray processEmployeeForm(int employerId, RegisterEmployeeForm... employees) throws Exception {

        boolean customMaxAmountActivated = extranetCompanyService.isCustomMaxAmountActivated();

        JSONArray employeesArr = new JSONArray();
        for (RegisterEmployeeForm employee : employees) {
            JSONObject jsEmployee = new JSONObject();
            jsEmployee.put("docType", employee.getDocType());
            jsEmployee.put("docNumber", employee.getDocNumber());
            jsEmployee.put("personName", employee.getName());
            jsEmployee.put("firstSurname", employee.getFirstSurname());
            jsEmployee.put("lastSurname", employee.getLastSurname());
            jsEmployee.put("employmentStartDate",
                    employee.getEmploymentStartDate() != null ? employee.getEmploymentStartDate().getTime() : null);
            jsEmployee.put("contractType", employee.getContractType());
            jsEmployee.put("contractEndDate",
                    employee.getContractEndDate() != null ? new SimpleDateFormat("dd/MM/yyyy").parse(employee.getContractEndDate()).getTime() : null);
            jsEmployee.put("email", employee.getEmail());
            jsEmployee.put("phoneNumber", employee.getPhoneNumber());
            jsEmployee.put("address", employee.getAddress());
            jsEmployee.put("fixedGrossIncome", employee.getFixedGrossIncome());
            jsEmployee.put("variableGrossIncome", employee.getVariableGrossIncome());
            jsEmployee.put("monthlyDeduction", employee.getMonthlyDeduction());
            jsEmployee.put("bank", employee.getBank());
            jsEmployee.put("accountNumber", employee.getAccountNumber());
            jsEmployee.put("accountNumberCci", employee.getAccountNumberCci());
            jsEmployee.put("salaryGarnishment", employee.getSalaryGarnishment());
            jsEmployee.put("unpaidLeave", employee.getUnpaidLeave());
            if (customMaxAmountActivated)
                jsEmployee.put("customMaxAmount", employee.getCustomMaxAmount());
            employeesArr.put(jsEmployee);
        }

        return employeesArr;
    }

    @Override
    public List<Employee> registerEmployees(Integer employerId, Integer employerUserId, boolean disablePrevious, Locale locale, RegisterEmployeeForm... employees) throws Exception {

        // Register in DB
        List<Employee> newEmployees = employerClDao.insertEmployerEmployees(employerId, processEmployeeForm(employerId, employees).toString(), employerUserId, disablePrevious, locale);

        // Send welcome email
        if (newEmployees != null && !newEmployees.isEmpty()) {
            for (Employee employee : newEmployees) {
                sendWelcomeEmails(employee);
            }
        }

        return newEmployees;
    }

    @Override
    public void registerMultiPayment(MultiCreditPayment multiCreditPayment, int creditId, double paymentAmount) throws Exception {
        JSONObject json = new JSONObject();
        json.put("tipo", "DD");
        json.put("codSucursal", multiCreditPayment.getSubsidiary());
        json.put("codMoneda", multiCreditPayment.getCurrency());
        json.put("numCuenta", multiCreditPayment.getAccountNumber());
        json.put("codIdDepositante", multiCreditPayment.getDepositorId());
        json.put("nomDepositante", multiCreditPayment.getDepositorName());
        json.put("infoRetorno", multiCreditPayment.getReturningInfo());
        json.put("fecEmisionCupon", multiCreditPayment.getPaymentDate());
        json.put("fecVencCupon", multiCreditPayment.getExpirationDate());
        json.put("montoCupon", paymentAmount);
        json.put("montoMora", multiCreditPayment.getArrearsAmount());
        json.put("montoMinimo", multiCreditPayment.getMinAmount());
        json.put("tipoRegistroAct", multiCreditPayment.getRegisterType());
        json.put("nroDocPago", multiCreditPayment.getPaymentDocNumber());
        json.put("nroDocIdentidad", multiCreditPayment.getIdentificationDocNumber());

        // Registr multipayment
        Integer paymentId = employerDao.registerMultiCreditPayment(multiCreditPayment.getId(), creditId, json.toString());

        // Confirm the payment
        JSONArray arrayIds = new JSONArray();
        JSONObject jsonId = new JSONObject();
        jsonId.put("id", paymentId);
        arrayIds.put(jsonId);
        employerDao.registerCreditPaymentConfirmation(arrayIds, null, true);
    }

    private void sendWelcomeEmails(Employee employee) throws Exception {
        if (employee.getWorkEmail() != null) {
            EntityProduct salaryAdvanceEntiy = catalogService.getEntityProductsByProduct(Product.SALARY_ADVANCE).stream().filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == employee.getEmployer().getId()).findFirst().orElse(null);
            EntityProduct agreementEntiy = catalogService.getEntityProductsByProduct(Product.AGREEMENT).stream().filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == employee.getEmployer().getId()).findFirst().orElse(null);

            JSONObject json = new JSONObject();
            json.put("CLIENT_NAME_SUBJECT", employee.getName() != null ? employee.getName() : "Hola");
            json.put("CLIENT_NAME", employee.getName() != null ? " " + employee.getName() : "");
            json.put("EMPLOYER", employee.getEmployer().getName());
            json.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COMMERCIAL);
            json.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COMMERCIAL);

            if (salaryAdvanceEntiy != null) {
                //Create interaction:
                PersonInteraction personInteraction = new PersonInteraction();
                personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                personInteraction.setDestination(employee.getWorkEmail());
                personInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.EMPLOYEE_SALARY_ADVANCE_WELCOME_MAIL, employee.getDocType().getCountryId()));

                //REGISTER INTERACTION ON DB
                interactionService.sendPersonInteraction(personInteraction, json, null);
            }
            if (agreementEntiy != null) {
                json.put("ENTITY", catalogService.getEntity(agreementEntiy.getEntityId()).getFullName());

                //Create interaction:
                PersonInteraction personInteraction = new PersonInteraction();
                personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                personInteraction.setDestination(employee.getWorkEmail());
                personInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.EMPLOYEE_AGREEMENT_WELCOME_MAIL, employee.getDocType().getCountryId()));

                //REGISTER INTERACTION ON DB
                interactionService.sendPersonInteraction(personInteraction, json, null);
            }
        }
    }

    @Override
    public void registerEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId, Integer cutoffDay, Integer paymentDay, Double tea, JSONArray users) throws Exception {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";

        for (int i = 0; i < users.length(); i++) {
            String password = RandomStringUtils.random(15, characters);
            String hashPassword = CryptoUtil.hashPassword(password);

            users.getJSONObject(i).put("password", hashPassword);
            users.getJSONObject(i).put("passwordBeforeHash", password);
        }

        employerDao.registerEmployer(entityId, name, ruc, address, phone, professionId, cutoffDay, paymentDay, tea, users);

        // Send password to Zoe
        emailCLService.sendPasswordEmployersMail(name, ruc, users);
    }
}
