package com.affirm.companyExt.controller;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.EmployeeCompanyExtranetPainter;
import com.affirm.client.model.EmployeeCredits;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.client.model.form.RegisterEmployeeForm;
import com.affirm.client.service.EmployerService;
import com.affirm.client.service.ExtranetCompanyService;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.security.model.CompanyEmailToken;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import jxl.*;
import jxl.format.Alignment;
import jxl.read.biff.BiffException;
import jxl.write.Number;
import jxl.write.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.Boolean;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import jxl.Sheet;
//import jxl.Workbook;

/**
 * @author renzodiaz
 */
@Controller
@Scope("request")
public class CompanyExtranetController {

    private static final Logger logger = Logger.getLogger(CompanyExtranetController.class);

    @Autowired
    private ExtranetCompanyService extranetCompanyService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private EmployerCLDAO employerClDao;
    @Autowired
    private EmployerDAO employerDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EmployerService employerService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private AgreementService agreementService;
    @Autowired
    private DebtConsolidationService debtConsolidationService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private ErrorService errorService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String extranet(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String companyExtranetLogin(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

//        if (SecurityUtils.getSubject().isAuthenticated()) {
//            return "redirect://dashboard";
//        }

        return "/companyExtranet/extranetLogin";

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object doCompanyExtranetLogin(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("email") String email,
            @RequestParam("password") String password) throws Exception {
        // Try Catch just to catch the NoUserFoundException for appropiate error message
        try {
            CompanyEmailToken token = new CompanyEmailToken(email.toLowerCase().trim(), password, locale, request);
            extranetCompanyService.login(token, request);
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            return AjaxResponse.errorMessage("El usuario y/o la contraseña son incorrectos.");
        }

        if (extranetCompanyService.validateAgreementProduct(extranetCompanyService.getLoggedUserEmployer().getActiveCompany().getId()))
            return AjaxResponse.redirect(request.getContextPath() + "/dashboard");
        else
            return AjaxResponse.redirect(request.getContextPath() + "/planilla/activa");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object logout(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        employerClDao.registerSessionLogout(((LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal()).getSessionId(), new Date());
        SecurityUtils.getSubject().logout();
        return AjaxResponse.redirect(request.getContextPath());
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object companyExtranetDashboard(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        EmployerCreditStats stats = employerDao.getEmployerCreditStats(user.getActiveCompany().getId());
        model.addAttribute("stats", stats);
        model.addAttribute("paymentDay", employerService.getEmployerCurrentPaymentDay(user.getActiveCompany().getId()));

        List<EntityProduct> entities = catalogService.getEntityProductsByProduct(Product.SALARY_ADVANCE);
        EntityProduct employerEntity = null;
        if (entities != null) {
            employerEntity = entities.stream().filter(e -> e.getEmployer() != null && e.getEmployer().getId() == user.getActiveCompany().getId().intValue()).findAny().orElse(null);
        }

        model.addAttribute("hasSalaryAdvance", employerEntity);
        return "/companyExtranet/extranetDashboard";
    }

    @RequestMapping(value = "/currentPaymentDay", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateCurrentPaymentDay(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("paymentDate") String paymentDate) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        employerClDao.updateEmployerPaymentDay(user.getActiveCompany().getId(), new Date(), new SimpleDateFormat("dd/MM/yyyy").parse(paymentDate));
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/planilla/{active:" +
            Active.ACTIVO + "|" +
            Active.INACTIVO + "}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetCompanyActivePaysheet(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("active") String active) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        List<EntityProduct> entitiesOfEmployer = catalogService.getEntityProducts().stream()
                .filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == user.getActiveCompany().getId()).collect(Collectors.toList());

        boolean isActive = false;
        switch (active) {
            case "activa":
                isActive = true;
                break;
            case "inactiva":
                isActive = false;
                break;
        }

        model.addAttribute("employerAssociated", entitiesOfEmployer != null && !entitiesOfEmployer.isEmpty());
        model.addAttribute("activePaysheet", active);
        model.addAttribute("employees", getPaysheetEmployees(locale, null, isActive));
        model.addAttribute("employeeform", new RegisterEmployeeForm());
        model.addAttribute("banksCatalog", catalogService.getBanks(false));
        model.addAttribute("customMaxAmountActivated", extranetCompanyService.isCustomMaxAmountActivated());
        return "/companyExtranet/extranetPaysheet";
    }

    @RequestMapping(value = "/adelantos", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetCompanySalaryAdvances(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        List<EmployeeCredits> list = employerClDao.getEmployeeCreditsByEmployer(user.getActiveCompany().getId(), null, CreditStatus.ACTIVE, locale);
        if (list != null) {
            EmployeeCredits total = new EmployeeCredits();
            for (EmployeeCredits ecList : list) {
                total.setCredits(total.getCredits() + ecList.getCredits());
                total.setTotalCreditAmount(total.getTotalCreditAmount() + ecList.getTotalCreditAmount());
                total.setTotalCreditAmountToPay(total.getTotalCreditAmountToPay() + ecList.getTotalCreditAmountToPay());
                total.setTotalPendingAmount(total.getTotalPendingAmount() + ecList.getTotalPendingAmount());
            }
            model.addAttribute("totals", total);
        }
        model.addAttribute("credits", list);
        return "/companyExtranet/extranetSalaryAdvances";
    }

    @RequestMapping(value = "/desembolsos", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetCompanyDisbursements(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        List<EmployeeCredits> list = employerClDao.getEmployeeCreditsByEmployer(user.getActiveCompany().getId(), null, CreditStatus.ACTIVE, locale);
        if (list != null) {
            EmployeeCredits total = new EmployeeCredits();
            for (EmployeeCredits ecList : list) {
                total.setCredits(total.getCredits() + ecList.getCredits());
                total.setTotalCreditAmount(total.getTotalCreditAmount() + ecList.getTotalCreditAmount());
                total.setTotalCreditAmountToPay(total.getTotalCreditAmountToPay() + ecList.getTotalCreditAmountToPay());
                total.setTotalPendingAmount(total.getTotalPendingAmount() + ecList.getTotalPendingAmount());
            }
            model.addAttribute("totals", total);
        }
        model.addAttribute("credits", list);
        return "/companyExtranet/extranetDisbursements";
    }

    @RequestMapping(value = "/imputador", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getImputator(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        SalaryAdvancePayment payment = employerDao.getEmployerPendingSalaryAdvancePayments(user.getActiveCompany().getId(), locale);
        if (payment == null)
            return "/companyExtranet/extranetSalaryAdvances";

        if (payment.getCreditIds() != null && payment.getCreditIds().length > 0) {
            payment.setCredits(creditDao.getCreditsByIds(payment.getCreditIds(), locale, Credit.class));
        }
        model.addAttribute("payment", payment);
        return "/companyExtranet/extranetImputator";
    }

    @RequestMapping(value = "/historicLoans", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getHistoricLoans(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("credits", creditDao.getCreditByEmployer(user.getActiveCompany().getId(), new Date(), locale));
        return "/companyExtranet/extranetHistoricLoans";
    }

    @RequestMapping(value = "/historicLoans/filter", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showCreditsFilter(
            ModelMap model, Locale locale,
            @RequestParam("collectionManagement") boolean collectionManagement) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        Date period = null;
        if (collectionManagement) period = new Date();
        model.addAttribute("credits", creditDao.getCreditByEmployer(user.getActiveCompany().getId(), period, locale));
        return new ModelAndView("/companyExtranet/extranetHistoricLoans :: list");
    }

    @RequestMapping(value = "/historicLoans/excel/{active}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportHistoricLoansExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("active") String active) throws Exception {

        Date period = null;
        switch (active) {
            case "true":
                period = new Date();
                break;
            case "false":
                period = null;
                break;
        }

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        List<Credit> credits = creditDao.getCreditByEmployer(user.getActiveCompany().getId(), period, locale);

        WritableWorkbook paysheetBook = null;

        try {

            response.setHeader("Content-disposition", "attachment; filename= " + user.getActiveCompany().getName() + "-" + utilService.dateCustomFormat(new Date(), "dd-MM-YYYY", locale) + ".xls");
            response.setContentType("application/vnd.ms-excel");

            ServletOutputStream outputStream = response.getOutputStream();
            paysheetBook = Workbook.createWorkbook(outputStream);

            WritableSheet excelSheet = paysheetBook.createSheet("Historico de créditos", 0);

            // Format for Head

            // Fonts
            WritableFont headFontFormat = new WritableFont(WritableFont.ARIAL, 12);
            headFontFormat.setBoldStyle(WritableFont.BOLD);

            WritableFont bodyFontFormat = new WritableFont(WritableFont.ARIAL, 11);

            // Cells
            WritableCellFormat headCellFormat = new WritableCellFormat(headFontFormat);
            headCellFormat.setWrap(true);
            headCellFormat.setAlignment(Alignment.CENTRE);

            WritableCellFormat leftCellFormat = new WritableCellFormat(bodyFontFormat);
            leftCellFormat.setAlignment(Alignment.LEFT);

            WritableCellFormat rightCellFormat = new WritableCellFormat(bodyFontFormat);
            rightCellFormat.setAlignment(Alignment.RIGHT);

            WritableCellFormat centerCellFormat = new WritableCellFormat(bodyFontFormat);
            centerCellFormat.setAlignment(Alignment.CENTRE);

            CellView headCellView = new CellView();
            headCellView.setAutosize(true);

            // Add labels to sheet
            Label label = new Label(0, 0, "Fecha de Activación", headCellFormat);
            excelSheet.setColumnView(0, headCellView);
            excelSheet.addCell(label);

            label = new Label(1, 0, "Crédito", headCellFormat);
            excelSheet.setColumnView(1, headCellView);
            excelSheet.addCell(label);

            label = new Label(2, 0, "Tipo de Documento", headCellFormat);
            excelSheet.setColumnView(2, headCellView);
            excelSheet.addCell(label);

            label = new Label(3, 0, "Número de Documento", headCellFormat);
            excelSheet.setColumnView(3, headCellView);
            excelSheet.addCell(label);

            label = new Label(4, 0, "Nombre Completo", headCellFormat);
            excelSheet.setColumnView(4, headCellView);
            excelSheet.addCell(label);

            label = new Label(5, 0, "Monto a Abonar (S/)", headCellFormat);
            excelSheet.setColumnView(5, headCellView);
            excelSheet.addCell(label);

            label = new Label(6, 0, "Cuotas Pagadas / Totales", headCellFormat);
            excelSheet.setColumnView(6, headCellView);
            excelSheet.addCell(label);

            label = new Label(7, 0, "Tasa (%)", headCellFormat);
            excelSheet.setColumnView(7, headCellView);
            excelSheet.addCell(label);

            label = new Label(8, 0, "Monto Pagado (S/)", headCellFormat);
            excelSheet.setColumnView(8, headCellView);
            excelSheet.addCell(label);

            label = new Label(9, 0, "Última Cuota", headCellFormat);
            excelSheet.setColumnView(9, headCellView);
            excelSheet.addCell(label);

            label = new Label(10, 0, "Monto Total Pendiene (S/)", headCellFormat);
            excelSheet.setColumnView(10, headCellView);
            excelSheet.addCell(label);

            label = new Label(11, 0, "Tipo de Crédito", headCellFormat);
            excelSheet.setColumnView(11, headCellView);
            excelSheet.addCell(label);

            if (credits != null && credits.size() > 0) {

                for (int i = 0; i < credits.size(); i++) {
                    Credit e = credits.get(i);

                    label = new Label(0, i + 1, e.getActiveDate() != null ? utilService.datetimeShortFormat(e.getActiveDate()) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(1, i + 1, e.getCode() != null ? e.getCode() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(2, i + 1, e.getPersonDocumentType() != null ? e.getPersonDocumentType().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(3, i + 1, e.getPersonDocumentNumber(), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(4, i + 1, e.getFullName(), leftCellFormat);
                    excelSheet.addCell(label);

                    Number number = new Number(5, i + 1, e.getPendingInstallmentAmount() != null ? e.getPendingInstallmentAmount() : 0, rightCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(6, i + 1, e.getInstallments(), rightCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(7, i + 1, e.getEffectiveAnnualRate(), rightCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(8, i + 1, e.getPendingCreditAmount() != null ? (e.getTotalCreditAmount() - e.getPendingCreditAmount()) : e.getTotalCreditAmount(), rightCellFormat);
                    excelSheet.addCell(number);

                    label = new Label(9, i + 1, e.getLastDueDate() != null ? utilService.datetimeShortFormat(e.getLastDueDate()) : null, leftCellFormat);
                    excelSheet.addCell(label);

                    number = new Number(10, i + 1, e.getPendingCreditAmount() != null ? e.getPendingCreditAmount() : 0, rightCellFormat);
                    excelSheet.addCell(number);

                    label = new Label(11, i + 1, e.getProduct() != null ? e.getProduct().getName() : null, leftCellFormat);
                    excelSheet.addCell(label);

                }
            } else {
                label = new Label(0, 1, "No existen registros a mostrar", leftCellFormat);
                excelSheet.addCell(label);
            }

            paysheetBook.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (paysheetBook != null) {
                try {
                    paysheetBook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/imputador", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerMultipayment(
            HttpServletRequest request, ModelMap model, Locale locale,
            @RequestParam("multiPaymentJson") String multiPaymentJsonString) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        JSONArray multiPaymentJson = new JSONArray(multiPaymentJsonString);

        SalaryAdvancePayment employerPayments = employerDao.getEmployerPendingSalaryAdvancePayments(user.getActiveCompany().getId(), locale);
        if (employerPayments == null || employerPayments.getCreditIds() == null || employerPayments.getCreditIds().length == 0)
            return AjaxResponse.redirect(request.getContextPath() + "/imputador");

        employerPayments.setCredits(creditDao.getCreditsByIds(employerPayments.getCreditIds(), locale, Credit.class));

        // validate that the amount inputed is correct
        double employerPaymentAmount = 0;
        for (int i = 0; i < multiPaymentJson.length(); i++) {
            employerPaymentAmount = employerPaymentAmount + multiPaymentJson.getJSONObject(i).getDouble("paymentAmount");
        }
        if (employerPaymentAmount > employerPayments.getTotalPaymentAmount() + employerPayments.getTotalSurplusAmount()) {
            return AjaxResponse.errorMessage("Los pagos enviados son mayores al pago realizado por esta.");
        }

        // Separate the json
        JSONArray creditsMultiPaymentJson = new JSONArray();
        for (int i = 0; i < multiPaymentJson.length(); i++) {
            JSONObject json = multiPaymentJson.getJSONObject(i);
            PersonCreditsSalaryAdvancePayment personPayment = employerPayments.getPersonCredits().stream()
                    .filter(e -> e.getPersonId() == json.getInt("personId")).findAny().orElse(null);
            if (personPayment == null)
                continue;

            MutableDouble paymentAmount = new MutableDouble(json.getDouble("paymentAmount"));
            if (paymentAmount.doubleValue() == 0)
                continue;

            for (Credit c : personPayment.getCredits()) {
                if (c.getPendingInstallmentAmount() <= 0)
                    continue;
                if (paymentAmount.doubleValue() <= 0)
                    break;
                JSONObject creditMultiPaymentJson = new JSONObject();
                creditMultiPaymentJson.put("creditId", c.getId());
                creditMultiPaymentJson.put("paymentAmount",
                        paymentAmount.doubleValue() >= c.getPendingInstallmentAmount() ? c.getPendingInstallmentAmount() : paymentAmount.doubleValue());
                creditsMultiPaymentJson.put(creditMultiPaymentJson);

                paymentAmount.setValue(paymentAmount.doubleValue() - creditMultiPaymentJson.getDouble("paymentAmount"));
            }
        }


        // TODO Cal the creditPaymentServiceImpl
        //Register the payments
        for (int i = 0; i < creditsMultiPaymentJson.length(); i++) {
            JSONObject json = creditsMultiPaymentJson.getJSONObject(i);

            double paymentAmount = json.getDouble("paymentAmount");
            if (paymentAmount == 0)
                continue;

            while (paymentAmount > 0) {
                MultiCreditPayment multiPayment;
                if (employerPayments.getCashSurplus() != null) {
                    multiPayment = employerPayments.getCashSurplus().stream().filter(s -> s.getCashSurplus() > 0).findFirst().orElse(null);
                    if (multiPayment != null) {
                        double amountToPay = multiPayment.getCashSurplus() >= paymentAmount ? paymentAmount : multiPayment.getCashSurplus();
                        employerService.registerMultiPayment(multiPayment, json.getInt("creditId"), amountToPay);
                        paymentAmount = paymentAmount - amountToPay;
                        multiPayment.setCashSurplus(multiPayment.getCashSurplus() - amountToPay);
                        continue;
                    }
                }
                if (employerPayments.getPayments() != null) {
                    multiPayment = employerPayments.getPayments().stream().filter(s -> s.getAmount() > 0).findFirst().orElse(null);
                    if (multiPayment != null) {
                        double amountToPay = multiPayment.getAmount() >= paymentAmount ? paymentAmount : multiPayment.getAmount();
                        employerService.registerMultiPayment(multiPayment, json.getInt("creditId"), amountToPay);
                        paymentAmount = paymentAmount - amountToPay;
                        multiPayment.setAmount(multiPayment.getAmount() - amountToPay);
                        continue;
                    }
                }
                // If it comes to here means that there is no more payments availables, so break it
                break;
            }
        }

        return AjaxResponse.ok(null);
    }

    // Closed by request
    @RequestMapping(value = "/historicos", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetCompanyHistory(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "month", required = false) String month) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        Date monthDate = null;
        if (month != null && !month.isEmpty()) {
            monthDate = new SimpleDateFormat("MM/yyyy").parse(month);
        }

        List<EmployeeCredits> list = employerClDao.getEmployeeCreditsByEmployer(user.getActiveCompany().getId(), monthDate, CreditStatus.CANCELED, locale);
        if (list != null) {
            EmployeeCredits total = new EmployeeCredits();
            for (EmployeeCredits ecList : list) {
                total.setCredits(total.getCredits() + ecList.getCredits());
                total.setTotalCreditAmount(total.getTotalCreditAmount() + ecList.getTotalCreditAmount());
                total.setTotalCreditAmountToPay(total.getTotalCreditAmountToPay() + ecList.getTotalCreditAmountToPay());
                total.setTotalPendingAmount(total.getTotalPendingAmount() + ecList.getTotalPendingAmount());
            }
            model.addAttribute("totals", total);
        }
        model.addAttribute("month", month);
        model.addAttribute("credits", list);
        model.addAttribute("firstCreditMonth", employerDao.getEmployerCreditStats(user.getActiveCompany().getId()).getFirstCreditMonth());
        return "/companyExtranet/extranetHistory";
    }


    @RequestMapping(value = "/paysheet/file", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> registerUserFile(
            Locale locale,
            @RequestParam("file") MultipartFile[] file) throws Exception {

        // Clean the list in session
        SecurityUtils.getSubject().getSession().setAttribute("empoyeesToAdd", null);

        if (file == null || file.length < 1)
            return AjaxResponse.errorMessage("No se subio ningun excel. Int&eacute;ntalo nuevamente.");

        List<RegisterEmployeeForm> employees = new ArrayList<>();
        List<RegisterEmployeeForm> employeesWithErrors = new ArrayList<>();
        JSONArray banksWithoutMatch = new JSONArray("[]");
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeDocument = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeEmail = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeePhoneNumber = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeCci = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeAccount = new HashMap<>();

        Map<String, List<RegisterEmployeeForm>> repitedEmployeeDocumentDB = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeEmailDB = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeePhoneNumberDB = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeCciDB = new HashMap<>();
        Map<String, List<RegisterEmployeeForm>> repitedEmployeeAccountDB = new HashMap<>();

        ByteArrayInputStream bis = new ByteArrayInputStream(file[0].getBytes());
        Workbook workbook = null;
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("ISO-8859-1");
        try {
            workbook = Workbook.getWorkbook(bis, ws);
            Sheet sheet = workbook.getSheet(0);
            int columns = sheet.getRows();
            for (int i = 1; i < columns; i++) {

                // If all the fields are empty, just pass
                boolean allEmpty = true;

                if (sheet.getRow(i).length == 0) {
                    continue;
                }

                for (int j = 0; j <= 18; j++) {
                    if (!sheet.getCell(j, i).getContents().trim().isEmpty()) {
                        allEmpty = false;
                        break;
                    }
                }
                if (allEmpty) {
                    break;
                }

                // Encapsulate all in a form object
                RegisterEmployeeForm employeeForm = new RegisterEmployeeForm();
                if (!sheet.getCell(0, i).getContents().trim().isEmpty()) {
                    Integer documentType = null;
                    switch (sheet.getCell(0, i).getContents().toLowerCase().trim()) {
                        case "dni":
                        case "di":
                        case "doi":
                        case "documento nacional de identidad":
                        case "documento nacional identidad":
                            documentType = 1;
                            break;
                        case "ce":
                        case "cex":
                        case "carne extranjería":
                        case "carné extranjería":
                        case "carne extranjeria":
                        case "carné extranjeria":
                        case "carne de extranjería":
                        case "carné de extranjería":
                        case "carné de extranjeria":
                        case "carne de extranjeria":
                        case "carnet de extranjería":
                        case "carnet de extranjeria":
                        case "carnet extranjería":
                        case "carnet extranjeria":
                            documentType = 2;
                            break;
                    }
                    employeeForm.setDocType(documentType);
                }
                if (!sheet.getCell(1, i).getContents().trim().isEmpty()) {
                    employeeForm.setDocNumber("" + sheet.getCell(1, i).getContents().trim());
                }
                if (!sheet.getCell(2, i).getContents().trim().isEmpty()) {
                    employeeForm.setName("" + sheet.getCell(2, i).getContents().trim());
                }
                if (!sheet.getCell(3, i).getContents().trim().isEmpty()) {
                    employeeForm.setFirstSurname("" + sheet.getCell(3, i).getContents().trim());
                }
                if (!sheet.getCell(4, i).getContents().trim().isEmpty()) {
                    employeeForm.setLastSurname("" + sheet.getCell(4, i).getContents().trim());
                }
                if (!sheet.getCell(5, i).getContents().trim().isEmpty()) {
                    if (sheet.getCell(5, i).getType() == CellType.DATE) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setTimeZone(TimeZone.getDefault());
                        employeeForm.setEmploymentStartDate(((DateCell) sheet.getCell(5, i)).getDate());
                    } else {
                        String[] splitted = sheet.getCell(5, i).getContents().trim().split("\\/");
                        if (splitted.length == 3 && splitted[splitted.length - 1].length() == 2) {
                            // If you're reading this in the year > 2099, sorry :(
                            employeeForm.setEmploymentStartDate(new SimpleDateFormat("dd/MM/yyyy").parse(splitted[0] + "/" + splitted[1] + "/20" + splitted[2]));
                        } else {
                            employeeForm.setEmploymentStartDate(new SimpleDateFormat("dd/MM/yyyy").parse(sheet.getCell(5, i).getContents().trim()));
                        }
                    }
                }
                if (!sheet.getCell(6, i).getContents().trim().isEmpty()) {
                    Character contractType = null;
                    switch (sheet.getCell(6, i).getContents().toLowerCase().trim()) {
                        case "determinado":
                        case "definido":
                        case "d":
                            contractType = 'D';
                            break;

                        case "indeterminado":
                        case "indefinido":
                        case "i":
                            contractType = 'I';
                            break;
                    }
                    employeeForm.setContractType(contractType);
                }

                if (!sheet.getCell(7, i).getContents().trim().isEmpty()) {
                    if (sheet.getCell(7, i).getType() == CellType.DATE) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        sdf.setTimeZone(TimeZone.getDefault());
                        employeeForm.setContractEndDate(sdf.format(((DateCell) sheet.getCell(7, i)).getDate()));
                    } else {
                        String[] splitted = sheet.getCell(7, i).getContents().trim().split("\\/");
                        if (splitted.length == 3 && splitted[splitted.length - 1].length() == 2) {
                            // If you're reading this in the year > 2099, sorry :(
                            employeeForm.setContractEndDate(splitted[0] + "/" + splitted[1] + "/20" + splitted[2]);
                        } else {
                            employeeForm.setContractEndDate("" + sheet.getCell(7, i).getContents().trim());
                        }
                    }
                }
                if (!sheet.getCell(8, i).getContents().trim().isEmpty()) {
                    employeeForm.setEmail("" + sheet.getCell(8, i).getContents().trim());
                }
                if (!sheet.getCell(9, i).getContents().trim().isEmpty()) {
                    employeeForm.setAddress("" + sheet.getCell(9, i).getContents().trim());
                }
                if (!sheet.getCell(10, i).getContents().trim().isEmpty()) {
                    if (utilService.stringToDouble(sheet.getCell(10, i).getContents().trim()) != null) {
                        employeeForm.setFixedGrossIncome(utilService.stringToDouble(sheet.getCell(10, i).getContents().trim()).intValue());
                    } else {
                        employeeForm.setFixedGrossIncome(utilService.stringToInteger(sheet.getCell(10, i).getContents().trim()));
                    }
                }
                if (!sheet.getCell(11, i).getContents().trim().isEmpty()) {
                    if (utilService.stringToDouble(sheet.getCell(11, i).getContents().trim()) != null) {
                        employeeForm.setVariableGrossIncome(utilService.stringToDouble(sheet.getCell(11, i).getContents().trim()).intValue());
                    } else {
                        employeeForm.setVariableGrossIncome(utilService.stringToInteger(sheet.getCell(11, i).getContents().trim()));
                    }
                }

                if (!sheet.getCell(12, i).getContents().trim().isEmpty()) {
                    if (utilService.stringToDouble(sheet.getCell(12, i).getContents().trim()) != null) {
                        employeeForm.setMonthlyDeduction(utilService.stringToDouble(sheet.getCell(12, i).getContents().trim()).intValue());
                    } else {
                        employeeForm.setMonthlyDeduction(utilService.stringToInteger(sheet.getCell(12, i).getContents().trim()));
                    }
                }

                if (!sheet.getCell(13, i).getContents().trim().isEmpty()) {
                    employeeForm.setPhoneNumber("" + sheet.getCell(13, i).getContents().trim());
                }
                if (!sheet.getCell(14, i).getContents().trim().isEmpty()) {
                    employeeForm.setBank("" + sheet.getCell(14, i).getContents().trim());
                }
                if (!sheet.getCell(15, i).getContents().trim().isEmpty() && !sheet.getCell(15, i).getContents().trim().equals("0")) {
                    employeeForm.setAccountNumber("" + sheet.getCell(15, i).getContents().replace(" ", "").replace("-", ""));
                }
                if (!sheet.getCell(16, i).getContents().trim().isEmpty() && !sheet.getCell(16, i).getContents().trim().equals("0")) {
                    employeeForm.setAccountNumberCci("" + sheet.getCell(16, i).getContents().replace(" ", "").replace("-", ""));
                }
                if (!sheet.getCell(17, i).getContents().trim().isEmpty()) {
                    Boolean salaryGarnishment = false;
                    switch (sheet.getCell(17, i).getContents().toLowerCase().trim()) {
                        case "true":
                        case "si":
                        case "yes":
                        case "verdadero":
                        case "1":
                            salaryGarnishment = true;
                            break;
                    }
                    employeeForm.setSalaryGarnishment(salaryGarnishment);
                }
                if (!sheet.getCell(18, i).getContents().trim().isEmpty()) {
                    Boolean unpaidLeave = false;
                    switch (sheet.getCell(18, i).getContents().toLowerCase().trim()) {
                        case "true":
                        case "si":
                        case "yes":
                        case "verdadero":
                        case "1":
                            unpaidLeave = true;
                            break;
                    }
                    employeeForm.setUnpaidLeave(unpaidLeave);
                }
                if (!sheet.getCell(19, i).getContents().trim().isEmpty()) {
                    employeeForm.setEmployerRuc(sheet.getCell(19, i).getContents().trim());
                }
                if (extranetCompanyService.isCustomMaxAmountActivated() && !sheet.getCell(20, i).getContents().trim().isEmpty()) {
                    employeeForm.setCustomMaxAmount(utilService.stringToDouble(sheet.getCell(20, i).getContents().trim()).intValue());
                }

                employeeForm.setValidationAgreementProduct(!extranetCompanyService.validateAgreementProduct(extranetCompanyService.getLoggedUserEmployer().getActiveCompany().getId()));

                // Validate the form
                employeeForm.getValidator().validate(locale);
                if (employeeForm.getValidator().isHasErrors()) {
                    employeesWithErrors.add(employeeForm);
                } else {
                    employees.add(employeeForm);
                }

                if (employeeForm.getDocNumber() != null) {
                    List<RegisterEmployeeForm> repeatedDocument = employees.stream().filter(e -> e.getDocNumber() != null && e.getDocNumber().equals(employeeForm.getDocNumber())).collect(Collectors.toList());
                    if (repeatedDocument.size() > 1) {
                        if (repitedEmployeeDocument.containsKey(employeeForm.getDocNumber())) {
                            repitedEmployeeDocument.get(employeeForm.getDocNumber()).addAll(repeatedDocument);
                        } else {
                            repitedEmployeeDocument.put(employeeForm.getDocNumber(), repeatedDocument);
                        }
                        employees.removeAll(repeatedDocument);
                    }
                }

                if (employeeForm.getEmail() != null) {
                    List<RegisterEmployeeForm> repeatedEmail = employees.stream().filter(e -> e.getEmail() != null && e.getEmail().equals(employeeForm.getEmail())).collect(Collectors.toList());
                    if (repeatedEmail.size() > 1) {
                        if (repitedEmployeeEmail.containsKey(employeeForm.getEmail())) {
                            repitedEmployeeEmail.get(employeeForm.getEmail()).addAll(repeatedEmail);
                        } else {
                            repitedEmployeeEmail.put(employeeForm.getEmail(), repeatedEmail);
                        }
                        employees.removeAll(repeatedEmail);
                    }
                }

                if (employeeForm.getPhoneNumber() != null) {
                    List<RegisterEmployeeForm> repeatedPhoneNumber = employees.stream().filter(e -> e.getPhoneNumber() != null && e.getPhoneNumber().equals(employeeForm.getPhoneNumber())).collect(Collectors.toList());
                    if (repeatedPhoneNumber.size() > 1) {
                        if (repitedEmployeePhoneNumber.containsKey(employeeForm.getPhoneNumber())) {
                            repitedEmployeePhoneNumber.get(employeeForm.getPhoneNumber()).addAll(repeatedPhoneNumber);
                        } else {
                            repitedEmployeePhoneNumber.put(employeeForm.getPhoneNumber(), repeatedPhoneNumber);
                        }
                        employees.removeAll(repeatedPhoneNumber);
                    }
                }

                if (employeeForm.getAccountNumber() != null) {
                    List<RegisterEmployeeForm> repeatedAccount = employees.stream().filter(e -> e.getAccountNumber() != null && e.getAccountNumber().equals(employeeForm.getAccountNumber())).collect(Collectors.toList());
                    if (repeatedAccount.size() > 1) {
                        if (repitedEmployeeAccount.containsKey(employeeForm.getAccountNumber())) {
                            repitedEmployeeAccount.get(employeeForm.getAccountNumber()).addAll(repeatedAccount);
                        } else {
                            repitedEmployeeAccount.put(employeeForm.getAccountNumber(), repeatedAccount);
                        }
                        employees.removeAll(repeatedAccount);
                    }
                }

                if (employeeForm.getAccountNumberCci() != null) {
                    List<RegisterEmployeeForm> repeatedCci = employees.stream().filter(e -> e.getAccountNumberCci() != null && e.getAccountNumberCci().equals(employeeForm.getAccountNumberCci())).collect(Collectors.toList());
                    if (repeatedCci.size() > 1) {
                        if (repitedEmployeeCci.containsKey(employeeForm.getAccountNumberCci())) {
                            repitedEmployeeCci.get(employeeForm.getAccountNumberCci()).addAll(repeatedCci);
                        } else {
                            repitedEmployeeCci.put(employeeForm.getAccountNumberCci(), repeatedCci);
                        }
                        employees.removeAll(repeatedCci);
                    }
                }
            }

            List<PersonValidator> personValidator;
            List<RegisterEmployeeForm> repeatedPhoneNumberDB = new ArrayList<>();
            List<RegisterEmployeeForm> repeatedEmailDB = new ArrayList<>();
            List<RegisterEmployeeForm> repeatedBankAccountDB = new ArrayList<>();
            List<RegisterEmployeeForm> repeatedCCIDB = new ArrayList<>();

            LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

            JSONArray jsonEmployees = employerService.processEmployeeForm(user.getActiveCompany().getId(), employees.toArray(new RegisterEmployeeForm[employees.size()]));
            personValidator = personDao.validatePerson(locale, user.getActiveCompany().getId(), jsonEmployees.toString());

            for (PersonValidator personVal : personValidator) {

                RegisterEmployeeForm employee = employees.stream().filter(e -> e.getDocType().intValue() == personVal.getDocType().intValue() && e.getDocNumber().equals(personVal.getDocNumber())).findFirst().orElse(null);
                boolean flagBorrado = false;

                if (!personVal.isValidPhoneNumber()) {
                    repeatedPhoneNumberDB.add(employee);
                    repitedEmployeePhoneNumberDB.put(employee.getPhoneNumber(), new ArrayList<>(repeatedPhoneNumberDB));
                    repeatedPhoneNumberDB.remove(employee);
                    flagBorrado = true;
                }

                if (!personVal.isValidEmail()) {
                    repeatedEmailDB.add(employee);
                    repitedEmployeeEmailDB.put(employee.getEmail(), new ArrayList<>(repeatedEmailDB));
                    repeatedEmailDB.remove(employee);
                    flagBorrado = true;
                }

                if (!personVal.isValidBankAccount()) {
                    repeatedBankAccountDB.add(employee);
                    repitedEmployeeAccountDB.put(employee.getAccountNumber(), new ArrayList<>(repeatedBankAccountDB));
                    repeatedBankAccountDB.remove(employee);
                    flagBorrado = true;
                }

                if (!personVal.isValidCCI()) {
                    repeatedCCIDB.add(employee);
                    repitedEmployeeCciDB.put(employee.getAccountNumberCci(), new ArrayList<>(repeatedCCIDB));
                    repeatedCCIDB.remove(employee);
                    flagBorrado = true;
                }

                if (flagBorrado) employees.remove(employee);
            }

            SecurityUtils.getSubject().getSession().setAttribute("empoyeesToAdd", employees);

            //List of distinct banks that didn't match
            List<String> banksToAdd = employees.stream().filter(x -> x.getBank() != null).map(x -> x.getBank().trim()).distinct().collect(Collectors.toList());
            List<String> banksInDB = catalogService.getBanks(false).stream().map(x -> x.getName().toLowerCase().trim()).distinct().collect(Collectors.toList());//lower
            for (int i = 0; i < banksToAdd.size(); i++) {
                String bankToadd = banksToAdd.get(i);
                if (!banksInDB.contains(bankToadd.toLowerCase())) {
                    banksWithoutMatch.put(bankToadd);
                }
            }
        } catch (BiffException | IOException | ParseException e) {
            errorService.onError(e);
            return AjaxResponse.errorMessage("El formato del archivo Excel no es el correcto. Intentar con Excel 97-2003 - Extensión .xls");
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (workbook != null)
                workbook.close();
        }

        JSONObject resJson = new JSONObject();
        resJson.put("employeesToAdd", employees.size());
        resJson.put("banksWithoutMatch", banksWithoutMatch.toString());
        if (!employeesWithErrors.isEmpty()) {
            JSONArray arrayEmployeeForm = new JSONArray();
            for (RegisterEmployeeForm form : employeesWithErrors) {
                JSONObject jsonFormError = new JSONObject();
                jsonFormError.put("docNumber", form.getDocNumber());
                jsonFormError.put("errorJson", new JSONObject(form.getValidator().getErrorsJson()));
                arrayEmployeeForm.put(jsonFormError);
            }
            resJson.put("employeesWithErrors", arrayEmployeeForm);
        }
        JSONArray repeatedArray = new JSONArray();
        for (Map.Entry<String, List<RegisterEmployeeForm>> entry : repitedEmployeeDocument.entrySet()) {
            JSONObject jsonRepeated = new JSONObject();
            jsonRepeated.put("repeatedValue", entry.getKey());
            jsonRepeated.put("repeatedField", "nro. de documento");
            jsonRepeated.put("count", entry.getValue().size());
            repeatedArray.put(jsonRepeated);
        }
        repitedEmployeeEmail.putAll(repitedEmployeeEmailDB);
        for (Map.Entry<String, List<RegisterEmployeeForm>> entry : repitedEmployeeEmail.entrySet()) {
            JSONObject jsonRepeated = new JSONObject();
            jsonRepeated.put("repeatedValue", entry.getKey());
            jsonRepeated.put("repeatedField", "email");
            jsonRepeated.put("count", entry.getValue().size());
            repeatedArray.put(jsonRepeated);
        }
        repitedEmployeePhoneNumber.putAll(repitedEmployeePhoneNumberDB);
        for (Map.Entry<String, List<RegisterEmployeeForm>> entry : repitedEmployeePhoneNumber.entrySet()) {
            JSONObject jsonRepeated = new JSONObject();
            jsonRepeated.put("repeatedValue", entry.getKey());
            jsonRepeated.put("repeatedField", "teléfono móvil");
            jsonRepeated.put("count", entry.getValue().size());
            repeatedArray.put(jsonRepeated);
        }
        repitedEmployeeAccount.putAll(repitedEmployeeAccountDB);
        for (Map.Entry<String, List<RegisterEmployeeForm>> entry : repitedEmployeeAccount.entrySet()) {
            JSONObject jsonRepeated = new JSONObject();
            jsonRepeated.put("repeatedValue", entry.getKey());
            jsonRepeated.put("repeatedField", "número de cuenta");
            jsonRepeated.put("count", entry.getValue().size());
            repeatedArray.put(jsonRepeated);
        }
        repitedEmployeeCci.putAll(repitedEmployeeCciDB);
        for (Map.Entry<String, List<RegisterEmployeeForm>> entry : repitedEmployeeCci.entrySet()) {
            JSONObject jsonRepeated = new JSONObject();
            jsonRepeated.put("repeatedValue", entry.getKey());
            jsonRepeated.put("repeatedField", "CCI");
            jsonRepeated.put("count", entry.getValue().size());
            repeatedArray.put(jsonRepeated);
        }

        if (repeatedArray.length() > 0)
            resJson.put("repeatedEmployees", repeatedArray);
        return AjaxResponse.ok(resJson.toString());

    }


    @RequestMapping(value = "/paysheet/file/confirm", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> confirmRegisterUserFile(
            Locale locale,
            @RequestParam(value = "bankArray", required = false) String bankFixArrayString,
            @RequestParam("disablePrevious") Boolean disablePrevious) throws Exception {

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("toAdd", 0);
        jsonResponse.put("toUpdate", 0);

        List<RegisterEmployeeForm> employeesToAdd = null;
        if (bankFixArrayString != null) {
            JSONArray bankArray = new JSONArray(bankFixArrayString);
            employeesToAdd = (List) SecurityUtils.getSubject().getSession().getAttribute("empoyeesToAdd");
            Map<Integer, String> employeeIndexToFix;
            for (int i = 0; i < bankArray.length(); i++) {
                JSONObject obj = bankArray.getJSONObject(i);
                String name = obj.getString("name");
                String catalogName = bankArray.getJSONObject(i).getString("catalogName");
                List<RegisterEmployeeForm> employeesToFix = employeesToAdd.stream().filter(e -> e.getBank().trim().equals(name.trim())).collect(Collectors.toList());
                for (RegisterEmployeeForm employeeToFix : employeesToFix) {
                    employeeToFix.setBank(catalogName.trim());
                }
            }
        }

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        if (employeesToAdd == null) {
            employeesToAdd = (List) SecurityUtils.getSubject().getSession().getAttribute("empoyeesToAdd");
        }
        if (employeesToAdd == null)
            return AjaxResponse.errorMessage("No hay colaboradores a agregar");

        List<RegisterEmployeeForm> employeesWithRuc = employeesToAdd.stream().filter(e -> e.getEmployerRuc() != null).collect(Collectors.toList());


        if (employeesWithRuc != null) {
            Map<String, List<RegisterEmployeeForm>> mapEmployeesByRuc = new HashMap<>();
            employeesWithRuc.stream().forEach(e -> {
                if (mapEmployeesByRuc.containsKey(e.getEmployerRuc())) {
                    mapEmployeesByRuc.get(e.getEmployerRuc()).add(e);
                } else {
                    List<RegisterEmployeeForm> rucEmployees = new ArrayList<>();
                    rucEmployees.add(e);
                    mapEmployeesByRuc.put(e.getEmployerRuc(), rucEmployees);
                }
            });

            for (Map.Entry<String, List<RegisterEmployeeForm>> entry : mapEmployeesByRuc.entrySet()) {
                // Validate the user has access to this employer ruc
                Employer rucEmployer = user.getCompanies().stream().filter(c -> c.getRuc().equals(entry.getKey())).findAny().orElse(null);
                if (rucEmployer != null) {
                    List<Employee> newEmployees = employerService.registerEmployees(rucEmployer.getId(), user.getId(), disablePrevious, locale, (RegisterEmployeeForm[]) entry.getValue().toArray(new RegisterEmployeeForm[entry.getValue().size()]));
                    if (newEmployees != null) {
                        jsonResponse.put("toAdd", jsonResponse.getInt("toAdd") + newEmployees.size());
                        jsonResponse.put("toUpdate", jsonResponse.getInt("toUpdate") + entry.getValue().size() - newEmployees.size());
                    } else {
                        jsonResponse.put("toUpdate", jsonResponse.getInt("toUpdate") + entry.getValue().size());
                    }
                } else {
                    // TODO Show message
                }
                employeesToAdd.removeAll(entry.getValue());
            }
        }
        List<Employee> newEmployees = employerService.registerEmployees(user.getActiveCompany().getId(), user.getId(), disablePrevious, locale, (RegisterEmployeeForm[]) employeesToAdd.toArray(new RegisterEmployeeForm[employeesToAdd.size()]));
        if (newEmployees != null) {
            jsonResponse.put("toAdd", jsonResponse.getInt("toAdd") + newEmployees.size());
            jsonResponse.put("toUpdate", jsonResponse.getInt("toUpdate") + employeesToAdd.size() - newEmployees.size());
        } else {
            jsonResponse.put("toUpdate", jsonResponse.getInt("toUpdate") + employeesToAdd.size());
        }

        SecurityUtils.getSubject().getSession().setAttribute("empoyeesToAdd", null);

        return AjaxResponse.ok(jsonResponse.toString());
    }


    @RequestMapping(value = "/paysheet/employee", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity addEmployee(HttpSession session, Locale locale, RegisterEmployeeForm form) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        if (form.getEmployeeId() != null) {
            Employee employee = personDao.getEmployeeById(form.getEmployeeId(), locale);
            form.setDocType(employee.getDocType().getId());
            form.setDocNumber(employee.getDocNumber());
            if (employee.getEmployer().getId().intValue() != user.getActiveCompany().getId()) {
                return AjaxResponse.errorMessage("El empleado no pertenece a tu empresa");
            }
        }

        employerService.registerEmployees(user.getActiveCompany().getId(), user.getId(), false, locale, form);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/paysheet/edit/employee", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity getEmployee(
            HttpSession session, Locale locale,
            @RequestParam("employeeId") int employeeId) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        Employee employee = personDao.getEmployeeById(employeeId, locale);

        if (employee == null) {
            return AjaxResponse.errorMessage("El empleado no existe");
        }
        if (employee.getEmployer().getId().intValue() != user.getActiveCompany().getId()) {
            return AjaxResponse.errorMessage("El empleado no pertenece a tu empresa");
        }

        return AjaxResponse.ok(new Gson().toJson(employee));
    }


    @RequestMapping(value = "/paysheet/search/{active:" +
            Active.ACTIVO + "|" +
            Active.INACTIVO + "}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object searchEmployee(
            ModelMap model, HttpSession session, Locale locale,
            @RequestParam("search") String search, @PathVariable("active") String active) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        StringFieldValidator validator = new StringFieldValidator(ValidatorUtil.SEARCH, search);

        if (!validator.validate(locale)) {
            return AjaxResponse.errorMessage(validator.getErrors());
        }

        Boolean isActive = false;
        if (active.equals("activa")) {
            isActive = true;
        }

        List<EntityProduct> entitiesOfEmployer = catalogService.getEntityProducts().stream()
                .filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == user.getActiveCompany().getId()).collect(Collectors.toList());
        model.addAttribute("employerAssociated", entitiesOfEmployer != null && !entitiesOfEmployer.isEmpty());
        model.addAttribute("employees", getPaysheetEmployees(locale, search, isActive));
        return new ModelAndView("/companyExtranet/extranetPaysheet :: list");
    }

    private List<EmployeeCompanyExtranetPainter> getPaysheetEmployees(Locale locale, String searchQuery, boolean isActive) throws Exception {

        LoggedUserEmployer loggedUser = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        List<EmployeeCompanyExtranetPainter> employees;

        if (searchQuery != null && !searchQuery.isEmpty()) {
            employees = personDao.getEmployeesBySlug(loggedUser.getId(), searchQuery, isActive, locale, EmployeeCompanyExtranetPainter.class);
        } else {
            employees = employerClDao.getEmployerEmployees(loggedUser.getActiveCompany().getId(), 0, 300, locale, isActive, EmployeeCompanyExtranetPainter.class);
        }

        if (employees != null) {
            for (EmployeeCompanyExtranetPainter e : employees) {
                if (e.getPersonId() != null) {

                    if (e.getContracts() != null && !e.getContracts().isEmpty()) {
                        List<UserFile> userFiles = new ArrayList<>();
                        userFiles.add(e.getContracts().get(0));
                        e.setContractFile(userFiles);
                    }

//                    List<Credit> credits = creditDao.getCreditsByPerson(e.getPersonId(), locale, Credit.class);
//                    if (credits != null) {
//                        List<Credit> creditsWithContract = credits.stream().filter(c -> c.getContractUserFileId() != null && ((c.getStatus().getId() == CreditStatus.ACTIVE || c.getStatus().getId() == CreditStatus.ORIGINATED_DISBURSED) && (c.getProduct().getId() == Product.SALARY_ADVANCE || c.getProduct().getId() == Product.AGREEMENT || c.getProduct().getId() == Product.DEBT_CONSOLIDATION))).collect(Collectors.toList());
//                        if (creditsWithContract != null && creditsWithContract.size()>0) {
//                            List<UserFile> userFiles = new ArrayList<>();
//                            for (int i = 0; i < creditsWithContract.size(); i++) {
//                                for(int j=0; j<creditsWithContract.get(i).getContractUserFileId().size();j++){
//                                    UserFile userFile = userDao.getUserFile(creditsWithContract.get(i).getContractUserFileId().get(j));
//                                    userFile.setProductCategory(catalogService.getCatalogById(ProductCategory.class, creditsWithContract.get(i).getProduct().getProductCategoryId(), locale));
//                                    userFiles.add(userFile);
//                                }
//                                e.setContractFile(userFiles);
//                            }
//                        }
//                    }

                    if (e.getPersonAssociateds() != null) {
                        PersonAssociated associated = e.getPersonAssociateds().stream().filter(p -> p.getEntity().getId() == Entity.ABACO).findFirst().orElse(null);
                        if (associated != null) {
                            e.setAgreementAssociated(associated);
                            e.setAgreementAssociatedValidated(associated.getValidated());
                        }

                        associated = e.getPersonAssociateds().stream().filter(p -> p.getEntity().getId() == Entity.EFL).findFirst().orElse(null);
                        if (associated != null) {
                            e.setConsolidationAssociated(associated);
                            e.setConsolidationAssociatedValidated(associated.getValidated());
                        }
                    }
                }
            }
        }

        return employees;
    }

    @RequestMapping(value = "/paysheet/removeEmployee", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity deleteEmployee(
            HttpSession session, Locale locale,
            @RequestParam("employeeIds") int[] employeeIds) throws Exception {

        if (employeeIds == null) {
            return AjaxResponse.ok(null);
        }

        employerClDao.updateEmployeeStatus(employeeIds, false);
        return AjaxResponse.ok(null);

    }

    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity activeCompany(
            HttpSession session, Locale locale,
            HttpServletRequest request,
            @RequestParam("employerId") int employerId) throws Exception {

        System.out.println("Employer id ==== " + employerId);

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        if (!user.getCompanies().stream().filter(c -> c.getId() == employerId).findAny().isPresent()) {
            return AjaxResponse.errorMessage("La empresa no esta asociada.");
        }

        user.setActiveCompany(catalogService.getEmployer(employerId));

        // This will ensure that the session will be updated with the change made in the line above
//        SecurityUtils.getSubject().getSession().touch();

        // This is just a test
        SecurityUtils.getSubject().getSession().setAttribute("activeCompanyExtranet", employerId);

        if (extranetCompanyService.validateAgreementProduct(extranetCompanyService.getLoggedUserEmployer().getActiveCompany().getId()))
            return AjaxResponse.redirect(request.getContextPath() + "/dashboard");
        else
            return AjaxResponse.redirect(request.getContextPath() + "/planilla/activa");

    }

    @RequestMapping(value = "/paysheet/activateEmployee", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity activateEmployee(
            HttpSession session, Locale locale,
            @RequestParam("employeeId") int[] employeeId) throws Exception {

        if (employeeId == null) {
            return AjaxResponse.ok(null);
        }

        employerClDao.updateEmployeeStatus(employeeId, true);

        String employeesWithErrors = "";
        for (int e : employeeId) {
            Employee employee = personDao.getEmployeeById(e, locale);
            if ((employee.getContractEndDate() != null && employee.getContractEndDate().before(new Date())) && employee.getContractType() == 'D') {
                employeesWithErrors += "El contrato del colaborador " + employee.getName() + " ya expiró.<br/>";
            }
        }

        return AjaxResponse.ok(employeesWithErrors.isEmpty() ? null : employeesWithErrors);
    }

    @RequestMapping(value = "/perfil", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetUserProfile(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        model.addAttribute("user", user);

        return "/companyExtranet/extranetUserProfile";
    }

    @RequestMapping(value = "/phoneNumber", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object addPhoneNumberEmployer(HttpSession session, Locale locale,
                                         @RequestParam("phoneNumber") String phoneNumber) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        StringFieldValidator validator = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER, phoneNumber);
        if (!validator.validate(locale)) {
            return AjaxResponse.errorMessage(validator.getErrors());
        }

        employerClDao.updateUserEmployerPhoneNumber(user.getId(), phoneNumber);
        user.setPhoneNumber(phoneNumber);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/file/avatar", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public byte[] getUserFile(
            ModelMap model, Locale locale,
            @RequestParam(required = false) boolean thumbnail) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        if (user.getAvatar() == null) {
            return null;
        }

        return fileService.getUserEmployerAvatar(user.getId(), user.getAvatar(), thumbnail);
    }

    @RequestMapping(value = "/file/avatar", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerUserFile(
            ModelMap model, Locale locale,
            @RequestParam("avatar") MultipartFile file) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();
        if (file == null || file.isEmpty()) {
            return AjaxResponse.errorMessage("El archivo no es válido");
        }

        String fileName = fileService.writeUserEmployerAvatar(file.getBytes(), user.getId(), file.getOriginalFilename());
        employerClDao.updateUserEmployerAvatar(user.getId(), fileName);
        user.setAvatar(fileName);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/paysheet/excel/template", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetImportPaysheetExcelTemplate(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Content-disposition", "attachment; filename=Campos_empleados.xls");
        response.setContentType("application/vnd.ms-excel");
        extranetCompanyService.createImportEmployeesExcelTemplate(response.getOutputStream());
    }

    @RequestMapping(value = "/paysheet/{active:" +
            Active.ACTIVO + "|" +
            Active.INACTIVO + "}/excel", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportPaysheetExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("active") String active) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        List<Employee> employees = null;
        switch (active) {
            case "activa":
                employees = employerClDao.getEmployerEmployees(user.getActiveCompany().getId(), 0, 300, locale, true, Employee.class);
                break;
            case "inactiva":
                employees = employerClDao.getEmployerEmployees(user.getActiveCompany().getId(), 0, 300, locale, false, Employee.class);
                break;
        }
        if (employees == null) {
            return;
        }

        response.setHeader("Content-disposition", "attachment; filename= planilla-" + user.getActiveCompany().getName() + "-" + utilService.dateCustomFormat(new Date(), "dd-MM-YYYY", locale) + ".xls");
        response.setContentType("application/vnd.ms-excel");
        extranetCompanyService.createEmployeesExcel(employees, response.getOutputStream());
    }

    @RequestMapping(value = "/salarayAdvances/excel", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportSalaryAdvancesExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        List<EmployeeCredits> employees = employerClDao.getEmployeeCreditsByEmployer(user.getActiveCompany().getId(), null, CreditStatus.ACTIVE, locale);

        if (employees == null) {
            employees = new ArrayList<>();
        }

        WritableWorkbook paysheetBook = null;

        try {

            response.setHeader("Content-disposition", "attachment; filename= adelantos-" + user.getActiveCompany().getName() + "-" + utilService.dateCustomFormat(new Date(), "dd-MM-YYYY", locale) + ".xls");
            response.setContentType("application/vnd.ms-excel");

            ServletOutputStream outputStream = response.getOutputStream();
            paysheetBook = Workbook.createWorkbook(outputStream);

            WritableSheet excelSheet = paysheetBook.createSheet("Adelantos", 0);


            // Format for Head

            // Fonts
            WritableFont headFontFormat = new WritableFont(WritableFont.ARIAL, 12);
            headFontFormat.setBoldStyle(WritableFont.BOLD);

            WritableFont bodyFontFormat = new WritableFont(WritableFont.ARIAL, 11);


            // Cells
            WritableCellFormat headCellFormat = new WritableCellFormat(headFontFormat);
            headCellFormat.setWrap(true);
            headCellFormat.setAlignment(Alignment.CENTRE);

            WritableCellFormat leftCellFormat = new WritableCellFormat(bodyFontFormat);
            leftCellFormat.setAlignment(Alignment.LEFT);

            WritableCellFormat rightCellFormat = new WritableCellFormat(bodyFontFormat);
            rightCellFormat.setAlignment(Alignment.RIGHT);

            WritableCellFormat centerCellFormat = new WritableCellFormat(bodyFontFormat);
            centerCellFormat.setAlignment(Alignment.CENTRE);

            CellView headCellView = new CellView();
            headCellView.setAutosize(true);

            // Add labels to sheet
            Label label = new Label(0, 0, "Tipo Doc.", headCellFormat);
            excelSheet.setColumnView(0, headCellView);
            excelSheet.addCell(label);

            label = new Label(1, 0, "Número Doc.", headCellFormat);
            excelSheet.setColumnView(1, headCellView);
            excelSheet.addCell(label);

            label = new Label(2, 0, "Nombre Completo", headCellFormat);
            excelSheet.setColumnView(2, headCellView);
            excelSheet.addCell(label);

            label = new Label(3, 0, "Cant. Créditos", headCellFormat);
            excelSheet.setColumnView(3, headCellView);
            excelSheet.addCell(label);

            label = new Label(4, 0, "Monto Créditos (S/)", headCellFormat);
            excelSheet.setColumnView(4, headCellView);
            excelSheet.addCell(label);

            label = new Label(5, 0, "Monto a Pagar (S/)", headCellFormat);
            excelSheet.setColumnView(5, headCellView);
            excelSheet.addCell(label);

            double totalCredits = 0;
            double totalCreditAmount = 0;
            double totalPendingAmount = 0;

            for (int i = 0; i < employees.size(); i++) {
                EmployeeCredits e = employees.get(i);

                label = new Label(0, i + 1, e.getDocType().getName() != null ? e.getDocType().getName() : "", leftCellFormat);
                excelSheet.addCell(label);

                label = new Label(1, i + 1, e.getDocNumber() != null ? e.getDocNumber() : "", leftCellFormat);
                excelSheet.addCell(label);

                label = new Label(2, i + 1, e.getFullName() != null ? e.getFullName() : "", leftCellFormat);
                excelSheet.addCell(label);

                Number number = new Number(3, i + 1, e.getCredits(), centerCellFormat);
                excelSheet.addCell(number);

                number = new Number(4, i + 1, e.getTotalCreditAmount(), rightCellFormat);
                excelSheet.addCell(number);

                number = new Number(5, i + 1, e.getTotalPendingAmount(), rightCellFormat);
                excelSheet.addCell(number);

                // Total
                label = new Label(2, employees.size() + 1, "TOTALES", rightCellFormat);
                excelSheet.addCell(label);

                totalCredits = totalCredits + e.getCredits();
                number = new Number(3, employees.size() + 1, totalCredits, centerCellFormat);
                excelSheet.addCell(number);

                totalCreditAmount = totalCreditAmount + e.getTotalCreditAmount();
                number = new Number(4, employees.size() + 1, totalCreditAmount, rightCellFormat);
                excelSheet.addCell(number);

                totalPendingAmount = totalPendingAmount + e.getTotalPendingAmount();
                number = new Number(5, employees.size() + 1, totalPendingAmount, rightCellFormat);
                excelSheet.addCell(number);

            }

            paysheetBook.write();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (paysheetBook != null) {
                try {
                    paysheetBook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/userFile/{jsonEncrypt}/image.jpg", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public ResponseEntity getUserFile(
            ModelMap model, Locale locale,
            @PathVariable("jsonEncrypt") String jsonEncrypt,
            @RequestParam(required = false) boolean thumbnail) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        JSONObject jsonDecrypt = new JSONObject(CryptoUtil.decrypt(jsonEncrypt));
        byte[] file = fileService.getUserFile(jsonDecrypt.getInt("user"), jsonDecrypt.getString("file"), thumbnail);
        if (file != null) {
            HttpHeaders responseHeaders = new HttpHeaders();
            String extension = FilenameUtils.getExtension(jsonDecrypt.getString("file"));
            switch (extension.toLowerCase()) {
                case "pdf":
                    responseHeaders.setContentType(MediaType.APPLICATION_PDF);
                    break;
                case "png":
                    responseHeaders.setContentType(MediaType.IMAGE_PNG);
                    break;
                case "gif":
                    responseHeaders.setContentType(MediaType.IMAGE_GIF);
                    break;
                default:
                    responseHeaders.setContentType(MediaType.IMAGE_JPEG);
                    break;
            }
            return new ResponseEntity(file, responseHeaders, HttpStatus.OK);
        }
        return null;
    }

    @RequestMapping(value = "/employee/associate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity registerAssociate(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("employeeId") Integer employeeId,
            @RequestParam("productId") int productId) throws Exception {
        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

//        List<EntityProduct> entitiesOfEmployer = catalogService.getEntityProducts().stream()
//                .filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == user.getActiveCompany().getId() && ep.getProduct().getId() == productId).collect(Collectors.toList());
//        if (entitiesOfEmployer == null || entitiesOfEmployer.isEmpty()) {
//            return AjaxResponse.errorMessage("La empresa no está asociada a ninguna entidad");
//        }
//
//        // TODO Select depending the LoggedUserEmployer
//        EntityProduct entity = entitiesOfEmployer.get(0);
        Employee employee = personDao.getEmployeeById(employeeId, locale);
        Person person = personDao.getPerson(catalogService, locale, employee.getPersonId(), false);
        if (employee.getPersonId() == null) {
            return AjaxResponse.errorMessage("El empleado aún no está registrado");
        }

        List<Credit> credits = creditDao.getCreditsByPerson(employee.getPersonId(), locale, Credit.class);
        if (credits != null) {
            List<Credit> creditsToCreate = credits
                    .stream()
                    .filter(c -> c.getStatus().getId() == CreditStatus.INACTIVE_W_SCHEDULE && c.getEmployer() != null && c.getEmployer().getId().intValue() == user.getActiveCompany().getId())
                    .collect(Collectors.toList());
            for (Credit c : creditsToCreate) {

                List<EntityProduct> entitiesOfEmployer = catalogService.getEntityProducts().stream()
                        .filter(ep -> ep.getEmployer() != null && ep.getEmployer().getId().intValue() == user.getActiveCompany().getId() && ep.getProduct().getId().intValue() == c.getProduct().getId()).collect(Collectors.toList());
                if (entitiesOfEmployer == null || entitiesOfEmployer.isEmpty()) {
                    return AjaxResponse.errorMessage("La empresa no está asociada a ninguna entidad");
                }

                if (c.getProduct().getId() == Product.AGREEMENT) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(c.getLoanApplicationId(), locale);
                    if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED_SIGNED)
                        continue;

                    // TODO Call abaco credit creation WS
                    agreementService.createAssociatedCredit(person, c);
                    if (c.getEntity().getId() == Entity.EFL) {

                        JSONObject jsonVars = new JSONObject();
                        jsonVars.put("CLIENT_NAME", person.getFirstName());
                        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                        PersonInteraction interaction = new PersonInteraction();
                        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.AGREEMENT_PAGARE_MAIL, loanApplication.getCountryId()));
                        //interaction.setBody(emailBody);
                        interaction.setDestination(user.getEmail());
                        interaction.setLoanApplicationId(c.getLoanApplicationId());
                        interaction.setCreditId(c.getId());
                        interaction.setPersonId(c.getPersonId());

                        interactionService.sendPersonInteraction(interaction, jsonVars, null);
                    }
                } else if (c.getProduct().getId() == Product.DEBT_CONSOLIDATION) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(c.getLoanApplicationId(), locale);
                    if (loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED_SIGNED)
                        continue;

                    debtConsolidationService.sendConsolidableAccountsEmail(c.getId(), locale, InteractionContent.CONSOLIDATION_MAIL, null);
                }

                personDao.validateAssociated(employee.getPersonId(), c.getEntity().getId(), true);
            }
        }


        // Call abaco to register the credit

//
//        if (productId == Product.AGREEMENT) {
//            List<Credit> credits = creditDao.getCreditsByPerson(employee.getPersonId(), locale, Credit.class);
//            if (credits != null) {
//                List<Credit> creditsToCreate = credits.stream().filter(c -> c.getProduct().getId() == Product.AGREEMENT
//                        && c.getEntity().getId().intValue() == entity.getEntityId() && c.getStatus().getId() == CreditStatus.INACTIVE_W_SCHEDULE).collect(Collectors.toList());
//                for (Credit c : creditsToCreate) {
//                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(c.getLoanApplicationId(), locale);
//                    if(loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED_SIGNED)
//                        continue;
//
//                    // TODO Call abaco credit creation WS
//                    agreementService.createAssociatedCredit(person, c);
//                    if (c.getEntity().getId() == Entity.EFL) {
//
//                        JSONObject jsonVars = new JSONObject();
//                        jsonVars.put("CLIENT_NAME", person.getFirstName());
//                        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
//                        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
//
//                        PersonInteraction interaction = new PersonInteraction();
//                        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
//                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.AGREEMENT_PAGARE_MAIL, loanApplication.getCountryId()));
//                        //interaction.setBody(emailBody);
//                        interaction.setDestination(user.getEmail());
//                        interaction.setLoanApplicationId(c.getLoanApplicationId());
//                        interaction.setCreditId(c.getId());
//                        interaction.setPersonId(c.getPersonId());
//
//                        interactionService.sendPersonInteraction(interaction, jsonVars);
//                    }
//                }
//            }
//        } else if (productId == Product.DEBT_CONSOLIDATION) {
//            List<Credit> credits = creditDao.getCreditsByPerson(employee.getPersonId(), locale, Credit.class);
//            if (credits != null) {
//                List<Credit> creditsToCreate = credits.stream().filter(c -> c.getProduct().getId() == Product.DEBT_CONSOLIDATION
//                        && c.getEntity().getId().intValue() == entity.getEntityId() && c.getStatus().getId() == CreditStatus.INACTIVE_W_SCHEDULE).collect(Collectors.toList());
//                for (Credit c : creditsToCreate) {
//                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(c.getLoanApplicationId(), locale);
//                    if(loanApplication.getStatus().getId() != LoanApplicationStatus.APPROVED_SIGNED)
//                        continue;
//
//                    debtConsolidationService.sendConsolidableAccountsEmail(c.getId(), locale, InteractionContent.CONSOLIDATION_MAIL, null);
//                }
//            }
//        }

        return AjaxResponse.ok(null);
    }


    @RequestMapping(value = "/userFileDebt/{creditId}", produces = "application/zip", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public byte[] getUserFiles(
            ModelMap model, Locale locale, HttpServletResponse response, @PathVariable("creditId") Integer creditId) throws Exception {
        Credit credit = creditDao.getCreditByID(creditId, locale, false, Credit.class);
        LoanApplicationUserFiles userFiles = personDao.getUserFiles(credit.getPersonId(), locale).stream().filter(e -> e.getCreditId() != null && e.getCreditId().intValue() == creditId.intValue()).findFirst().orElse(null);

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"Documentos.zip\"");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);
        List<UserFile> userFileList = userFiles.getUserFileList().stream().filter(e -> e.getFileType().getId() != UserFileType.DNI_MERGED).collect(Collectors.toList());
        for (UserFile userFile : userFileList) {
            File outputFile = new File(userFile.getFileName());
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(fileService.getUserFile(userDao.getUserIdByPersonId(credit.getPersonId()), userFile.getFileName(), false));

                zipOutputStream.putNextEntry(new ZipEntry(outputFile.getName()));
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (zipOutputStream != null) {
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
        }
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
