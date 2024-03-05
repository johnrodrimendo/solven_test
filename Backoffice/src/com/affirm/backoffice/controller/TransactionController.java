
/**
 *
 */
package com.affirm.backoffice.controller;

import com.affirm.backoffice.dao.CreditPaymentDAO;
import com.affirm.backoffice.model.CreditPayment;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.CreditPaymentService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.util.AjaxResponse;
import com.affirm.security.model.SysUser;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class TransactionController {

    private static Logger logger = Logger.getLogger(TransactionController.class);

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private CreditPaymentService creditPaymentService;
    @Autowired
    private CreditPaymentDAO creditPaymentDao;
    @Autowired
    private BackofficeService backofficeService;

    @RequestMapping(value = "/imputatorOpenMarket", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:imputation:openMarket", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showImputatorOpenMarket() {
        return "imputatorOpenMarket";
    }

    @RequestMapping(value = "/imputatorCloseMarket", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:imputation:closeMarket", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showImputatorCloseMarket() {
        return "imputatorCloseMarket";
    }


    @RequestMapping(value = "/imputator", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:imputation:uploadPayments", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showImputator() {
        return "imputator";
    }

    @RequestMapping(value = "/preImputatorOpen", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:imputation:openMarket", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> imputatorOpenMarket(
            Locale locale, HttpServletRequest request,
            @RequestParam("file") MultipartFile file) throws Exception {

        try {
            Workbook workbook = new HSSFWorkbook(file.getInputStream());
            Sheet datatypeSheet = workbook.getSheetAt(0);
            JSONArray jsonOpen = creditPaymentService.parseOpenMarketFile(datatypeSheet);
            request.getSession().setAttribute("imputatorOpenFile", jsonOpen);
            return AjaxResponse.ok(jsonOpen.toString());
        }catch (FileNotFoundException e) {
            return AjaxResponse.errorMessage("No se envió ningún archivo");
        } catch (IOException e) {
            return AjaxResponse.errorMessage("Error al procesar el archivo");
        }
    }

    @RequestMapping(value = "/preImputatorClose", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:imputation:closeMarket", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> imputatorCloseMarket(
            Locale locale, HttpServletRequest request,
            @RequestParam("file") MultipartFile file) throws Exception {

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
//            Workbook workbook = new HSSFWorkbook(file.getInputStream());
            Sheet datatypeSheet = workbook.getSheetAt(0);
            JSONArray jsonOpen = creditPaymentService.parseCloseMarketFile(datatypeSheet);
            request.getSession().setAttribute("imputatorCloseFile", jsonOpen.toString());
            return AjaxResponse.ok(jsonOpen.toString());
        }catch (FileNotFoundException e) {
            return AjaxResponse.errorMessage("No se envió ningún archivo");
        } catch (IOException e) {
            return AjaxResponse.errorMessage("Error al procesar el archivo");
        }
    }

    @RequestMapping(value = "/imputatorSingleOpen", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:imputation:openMarket", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> processImputationSingleOpen(
            Locale locale, HttpServletRequest request,
            @RequestParam("codPais") String codPais,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("numDocumento") String numDocumento,
            @RequestParam("codCredito") String codCredito,
            @RequestParam("codDepositante") String codDepositante,
            @RequestParam("fechaPago") String fechaPago,
            @RequestParam("montoPago") String montoPago,
            @RequestParam("numOperacion") String numOperacion) throws Exception {

        JSONArray paymentArray = new JSONArray();
        JSONObject paymentJson = new JSONObject();
        paymentJson.put("codPais", codPais);
        paymentJson.put("tipoDocumento", tipoDocumento);
        paymentJson.put("numDocumento", numDocumento);
        paymentJson.put("codCredito", codCredito);
        paymentJson.put("codDepositante", codDepositante);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = format.parse(fechaPago);
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        paymentJson.put("fechaPago", newFormat.format(date));
        paymentJson.put("montoPago", montoPago);
        paymentJson.put("numOperacion", numOperacion);
        paymentArray.put(paymentJson);
        creditPaymentService.processMarketPayment(paymentArray, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/imputatorOpen", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:imputation:openMarket", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> processImputationOpen(
            Locale locale, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("imputatorOpenFile") == null) {
            return AjaxResponse.errorMessage("No hay pagos a procesar");
        }

        JSONArray jsonImputatorSesion = (JSONArray) request.getSession().getAttribute("imputatorOpenFile");
        creditPaymentService.processMarketPayment(jsonImputatorSesion, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/imputatorClose", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:imputation:openMarket", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> processImputationClose(
            Locale locale, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("imputatorCloseFile") == null) {
            return AjaxResponse.errorMessage("No hay pagos a procesar");
        }

        JSONArray jsonImputatorSesion = new JSONArray((String) request.getSession().getAttribute("imputatorCloseFile"));
        creditPaymentService.processMarketPayment(jsonImputatorSesion, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/preimputator", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:payment:processBankFile", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> preProcessBankCreditPaymentFile(
            Locale locale, HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam("bankId") Integer bankId) throws Exception {
        if (file != null && file.getSize() > 0) {
            String result = IOUtils.toString(file.getBytes(), "UTF-8");
            JSONArray payments = creditPaymentService.parseBankPaymentFile(result, bankId);
            if (payments == null || payments.length() == 0) {
                return AjaxResponse.errorMessage("No hay pagos a procesar");
            }

            JSONObject jsonImputatorSesion = new JSONObject();
            jsonImputatorSesion.put("bankId", bankId);
            jsonImputatorSesion.put("paymnets", payments);
            request.getSession().setAttribute("imputatorFile", jsonImputatorSesion.toString());
            return AjaxResponse.ok(payments.toString());
        } else {
            return AjaxResponse.errorMessage("No se envio ningun archivo");
        }
    }

    @RequestMapping(value = "/imputator", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:payment:processBankFile", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> processBankCreditPaymentFile(
            Locale locale, HttpServletRequest request) throws Exception {
        if (request.getSession().getAttribute("imputatorFile") == null) {
            return AjaxResponse.errorMessage("No hay pagos a procesar");
        }

        JSONObject jsonImputatorSesion = new JSONObject((String) request.getSession().getAttribute("imputatorFile"));
        creditPaymentService.processBankPayments(jsonImputatorSesion.getJSONArray("paymnets"), jsonImputatorSesion.getInt("bankId"));
        creditPaymentService.processMultipaymentAutomatically(locale);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/creditPayment/pending", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:imputation:confirmPayment", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showPendingCreditPayment(
            ModelMap model, Locale locale) throws Exception {

        List<CreditPayment> pendings = creditPaymentDao.getPendingCreditPayment(backofficeService.getCountryActiveSysuser());
        if (pendings != null) {
            Map<Integer, Credit> credits = new HashMap<>();
            for (CreditPayment payment : pendings) {
                if (payment.getCreditId() != null) {
                    if (!credits.containsKey(payment.getCreditId())) {
                        credits.put(payment.getCreditId(), creditDAO.getCreditByID(payment.getCreditId(), locale, false, Credit.class));
                    }
                    payment.setCredit(credits.get(payment.getCreditId()));
                }
            }
            model.addAttribute("payments", pendings);
        }
        return "pendingCreditPayment";
    }

    @RequestMapping(value = "/creditPayment/confirm", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:payment:confirm", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> processBankCreditPaymentFile(
            ModelMap model, Locale locale,
            @RequestParam("ids") String ids) throws Exception {

        JSONArray array = new JSONArray(ids);
        creditPaymentDao.registerCreditPaymentConfirmation(array.toString(), ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/creditPayment/updateCreditPayment", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:payment:assignCreditId", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> updateCreditPayment(
            ModelMap model, Locale locale,
            @RequestParam("creditPaymentId") int creditPaymentId,
            @RequestParam(value = "value", required = false) String value) throws Exception {

        boolean result = creditPaymentDao.updateCreditPayment(creditPaymentId, value, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId());

        if (result) {
            return ResponseEntity.ok(null);
        } else {
            return AjaxResponse.errorMessage(messageSource.getMessage("credit.invalidcode", null, locale));
        }
    }

//    @RequestMapping(value = "/creditPayment/multipayment/pending", method = RequestMethod.GET)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
//    public Object showMultipaymentPending(
//            ModelMap model, Locale locale) throws Exception {
//        List<SalaryAdvancePayment> payments = creditPaymentDao.getPendingSalaryAdvancePayments(locale);
//        if (payments == null)
//            return "pendingCreditMultiPayment";
//
//        List<SalaryAdvancePayment> paymentsToProcess = payments.stream().filter(p -> p.getCreditIds() != null && p.getCreditIds().length > 0).collect(Collectors.toList());
//        if (paymentsToProcess == null || paymentsToProcess.isEmpty())
//            return "pendingCreditMultiPayment";
//
//        for (SalaryAdvancePayment p : paymentsToProcess) {
//            p.setCredits(creditDAO.getCreditsByIds(p.getCreditIds(), locale, PersonCreditsSalaryAdvancePayment.class));
//
//            Double totalAmountToPay = p.getTotalPaymentAmount() + p.getTotalSurplusAmount();
//            for (PersonCreditsSalaryAdvancePayment c : p.getCredits()) {
//                if (totalAmountToPay - c.getPendingInstallmentAmount() >= 0) {
//                    c.setAmountToPay(c.getPendingInstallmentAmount());
//                    totalAmountToPay = totalAmountToPay - c.getPendingInstallmentAmount();
//                } else {
//                    c.setAmountToPay(totalAmountToPay);
//                    break;
//                }
//            }
//        }
//
//        model.addAttribute("salaryAdvancePayments", paymentsToProcess);
//        return "pendingCreditMultiPayment";
//    }

//    @RequestMapping(value = "/creditPayment/multipayment", method = RequestMethod.POST)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
//    public Object registerMultipayment(
//            HttpServletRequest request, ModelMap model, Locale locale,
//            @RequestParam("employerId") int employerId,
//            @RequestParam("multiPaymentJson") String multiPaymentJsonString) throws Exception {
//
//        JSONArray multiPaymentJson = new JSONArray(multiPaymentJsonString);
//
//        List<SalaryAdvancePayment> payments = creditPaymentDao.getPendingSalaryAdvancePayments(locale);
//        if (payments == null)
//            return AjaxResponse.redirect(request.getContextPath() + "/creditPayment/multipayment/pending");
//        SalaryAdvancePayment employerPayments = payments.stream().filter(s -> s.getEmployer().getId() == employerId).findAny().orElse(null);
//        if (employerPayments == null)
//            return AjaxResponse.errorMessage("El empleador no tiene pagos registrados");
//
//
//        // validate that the amount inputed is correct
//        double employerPaymentAmount = 0;
//        for (int i = 0; i < multiPaymentJson.length(); i++) {
//            employerPaymentAmount = employerPaymentAmount + multiPaymentJson.getJSONObject(i).getDouble("paymentAmount");
//        }
//        if (employerPaymentAmount > employerPayments.getTotalPaymentAmount() + employerPayments.getTotalSurplusAmount()) {
//            return AjaxResponse.errorMessage("Los pagos enviados para la empresa " + employerPayments.getEmployer().getName() + " son mayores al pago realizado por esta.");
//        }
//
//        //Register the payments
//        for (int i = 0; i < multiPaymentJson.length(); i++) {
//            JSONObject json = multiPaymentJson.getJSONObject(i);
//
//            double paymentAmount = json.getDouble("paymentAmount");
//            if (paymentAmount == 0)
//                continue;
//
//            while (paymentAmount > 0) {
//                MultiCreditPayment multiPayment;
//                if (employerPayments.getCashSurplus() != null) {
//                    multiPayment = employerPayments.getCashSurplus().stream().filter(s -> s.getCashSurplus() > 0).findFirst().orElse(null);
//                    if (multiPayment != null) {
//                        double amountToPay = multiPayment.getCashSurplus() >= paymentAmount ? paymentAmount : multiPayment.getCashSurplus();
//                        creditPaymentService.registerMultiPayment(multiPayment, json.getInt("creditId"), amountToPay);
//                        paymentAmount = paymentAmount - amountToPay;
//                        multiPayment.setCashSurplus(multiPayment.getCashSurplus() - amountToPay);
//                        continue;
//                    }
//                }
//                if (employerPayments.getPayments() != null) {
//                    multiPayment = employerPayments.getPayments().stream().filter(s -> s.getAmount() > 0).findFirst().orElse(null);
//                    if (multiPayment != null) {
//                        double amountToPay = multiPayment.getAmount() >= paymentAmount ? paymentAmount : multiPayment.getAmount();
//                        creditPaymentService.registerMultiPayment(multiPayment, json.getInt("creditId"), amountToPay);
//                        paymentAmount = paymentAmount - amountToPay;
//                        multiPayment.setAmount(multiPayment.getAmount() - amountToPay);
//                        continue;
//                    }
//                }
//                // If it comes to here means that there is no more payments availables, so break it
//                break;
//            }
//        }
//
//        return AjaxResponse.ok(null);
//    }

}
