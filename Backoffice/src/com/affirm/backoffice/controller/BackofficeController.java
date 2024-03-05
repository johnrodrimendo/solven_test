package com.affirm.backoffice.controller;

import com.affirm.acceso.model.Direccion;
import com.affirm.acceso.service.AccesoGatewayUploadService;
import com.affirm.backoffice.dao.BackofficeDAO;
import com.affirm.backoffice.dao.LoanApplicationBODAO;
import com.affirm.backoffice.model.*;
import com.affirm.backoffice.model.form.RegisterDisbursementForm;
import com.affirm.backoffice.service.*;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.impl.FileServiceImpl;
import com.affirm.common.util.*;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Number;
import jxl.write.*;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.Boolean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class BackofficeController {

    private static Logger logger = Logger.getLogger(BackofficeController.class);

    @Autowired
    private LoanAplicationBoService loanAplicationBoService;
    @Autowired
    private LoanApplicationBODAO loanApplicationBoDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private BackofficeDAO backofficeDAO;
    @Autowired
    private BufferTransactionDAO bufferTransactionDAO;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ComparatorDAO comparatorDAO;
    @Autowired
    private UtilService utilService;
    @Autowired
    private ReportsBoService reportsBoService;
    @Autowired
    private net.sf.ehcache.CacheManager ehCacheManager;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private GoogleAnalyticsReportingService googleAnalyticsReportingService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private BackofficeService backofficeService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private BotDAO botDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private ServiceLogDAO serviceLogDao;

    @Autowired
    private EmployerDAO employerDAO;

    @Autowired
    private EmployerService employerService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private AccesoGatewayUploadService accesoGatewayUploadService;
    @Autowired
    private InteractionDAO interactionDAO;


    @RequestMapping(value = "/backoffice/imputationOpenMarket/excel/template", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetOpenMarketExcelTemplate(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Content-disposition", "attachment; filename=Campos_imputador_open.xls");
        response.setContentType("application/vnd.ms-excel");
        backofficeService.createImportImputationOpenMarketExcelTemplate(response.getOutputStream());
    }

    @RequestMapping(value = "/backoffice/imputationCloseMarket/excel/template", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetCloseMarketExcelTemplate(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Content-disposition", "attachment; filename=Campos_imputador_close.xls");
        response.setContentType("application/vnd.ms-excel");
        backofficeService.createImportImputationCloseMarketExcelTemplate(response.getOutputStream());
    }

    @RequestMapping(value = "/loanApplication/step1", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:prefilter", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsStep1(
            ModelMap model, Locale locale) throws Exception {

        PaginationWrapper<LoanApplicationSummaryBoPainter> loanSummaries = loanApplicationBoDao.getLoanApplicationsSummaries(
                backofficeService.getCountryActiveSysuser(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                locale, 1, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT);
        if (loanSummaries != null && loanSummaries.getResults() != null)
            for (LoanApplicationSummaryBoPainter painter : loanSummaries.getResults()) {
                if (painter.getCreditId() != null)
                    painter.setCredit(creditDao.getCreditByID(painter.getCreditId(), locale, false, Credit.class));
            }

        model.addAttribute("wrapper", loanSummaries);
        return "loanApplicationsStep1";
    }

    @RequestMapping(value = "/loanApplication/step1/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:prefilter", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsStep1(
            ModelMap model, Locale locale,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {

        model.addAttribute("wrapper", loanApplicationBoDao.getLoanApplicationsSummaries(
                backofficeService.getCountryActiveSysuser(),
                amountFromFilter,
                amountToFilter,
                creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null,
                creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null,
                documentNumberFilter,
                entity != null ? (entity.length > 0 ? entity[0] : null) : null,
                employer != null ? (employer.length > 0 ? employer[0] : null) : null,
                reason != null ? (reason.length > 0 ? reason[0] : null) : null,
                analyst,
                locale, 1, offset, limit));
        return new ModelAndView("loanApplicationsStep1 :: list");
    }

    @RequestMapping(value = "/reporting/noholding", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:taxholding", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingNoHolding(ModelMap model, Locale locale) throws Exception {
        PaginationWrapper<ReportNoHolding> wrapper = backofficeDAO.getNoHoldingReport(backofficeService.getCountryActiveSysuser(), 0, Configuration.BACKOFFICE_PAGINATION_LIMIT);
        model.addAttribute("wrapper", wrapper);
        return "reportNoHolding";
    }

    @RequestMapping(value = "/reporting/origination", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingOrigination(ModelMap model, Locale locale,
                                           @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
                                           @RequestParam(value = "creationTo[]", required = false) String creationTo) throws Exception {
        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_ORIGINACIONES_BO, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return "reportOrigination";
    }

    @RequestMapping(value = "/reporting/origination/excel", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object extranetExportSalaryAdvancesExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
            @RequestParam(value = "creationTo[]", required = false) String creationTo) throws Exception {

        Date from = new Date();
        Date to = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (creationFrom != null && !creationFrom.isEmpty() && creationTo != null && !creationTo.isEmpty()) {
            from = formatter.parse(creationFrom);
            to = formatter.parse(creationTo);
        }

        reportsService.createReporteOriginacionesBo(backofficeService.getLoggedSysuser().getId(), from, to, backofficeService.getCountryActiveSysuser(), currencyService.getCurrencySymbolReport(), currencyService.showInternationalCurrency());

        return AjaxResponse.ok(null);

    }

    @RequestMapping(value = "/reporting/collection/excel", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void exportCollectionReport(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
            @RequestParam(value = "creationTo[]", required = false) String creationTo) throws Exception {

        List<ReportCreditGateway> result = backofficeDAO.getCreditCollectionReport(backofficeService.getCountryActiveSysuser(), catalogService, locale,
                creationFrom != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null);

        WritableWorkbook paysheetBook = null;

        try {

            response.setHeader("Content-disposition", "attachment; filename= reporte-cobranzas-" + utilService.dateCustomFormat(new Date(), "dd-MM-YYYY", locale) + ".xls");
            response.setContentType("application/vnd.ms-excel");

            ServletOutputStream outputStream = response.getOutputStream();
            paysheetBook = Workbook.createWorkbook(outputStream);

            WritableSheet excelSheet = paysheetBook.createSheet("Reporte de Cobranzas", 0);

            String symbol = currencyService.getCurrencySymbolReport();


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
            Label label = new Label(0, 0, "#", headCellFormat);
            excelSheet.setColumnView(0, headCellView);
            excelSheet.addCell(label);

            label = new Label(1, 0, "País", headCellFormat);
            excelSheet.setColumnView(1, headCellView);
            excelSheet.addCell(label);

            label = new Label(2, 0, "Fecha de Imputación", headCellFormat);
            excelSheet.setColumnView(2, headCellView);
            excelSheet.addCell(label);

            label = new Label(3, 0, "Loan Application", headCellFormat);
            excelSheet.setColumnView(3, headCellView);
            excelSheet.addCell(label);

            label = new Label(4, 0, "Crédito", headCellFormat);
            excelSheet.setColumnView(4, headCellView);
            excelSheet.addCell(label);

            label = new Label(5, 0, "Person Id", headCellFormat);
            excelSheet.setColumnView(5, headCellView);
            excelSheet.addCell(label);

            label = new Label(6, 0, "Tipo de Documento", headCellFormat);
            excelSheet.setColumnView(6, headCellView);
            excelSheet.addCell(label);

            label = new Label(7, 0, "Número de Documento", headCellFormat);
            excelSheet.setColumnView(7, headCellView);
            excelSheet.addCell(label);

            label = new Label(8, 0, "R.U.C.", headCellFormat);
            excelSheet.setColumnView(8, headCellView);
            excelSheet.addCell(label);

            label = new Label(9, 0, "Apellidos", headCellFormat);
            excelSheet.setColumnView(9, headCellView);
            excelSheet.addCell(label);

            label = new Label(10, 0, "Nombres", headCellFormat);
            excelSheet.setColumnView(10, headCellView);
            excelSheet.addCell(label);

            label = new Label(11, 0, "Financiador", headCellFormat);
            excelSheet.setColumnView(11, headCellView);
            excelSheet.addCell(label);

            label = new Label(12, 0, "Empresa (Convenio)", headCellFormat);
            excelSheet.setColumnView(12, headCellView);
            excelSheet.addCell(label);

            label = new Label(13, 0, "Capital (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(13, headCellView);
            excelSheet.addCell(label);

            label = new Label(14, 0, "Monto Cobrado (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(14, headCellView);
            excelSheet.addCell(label);

            label = new Label(15, 0, "Seguro (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(15, headCellView);
            excelSheet.addCell(label);

            label = new Label(16, 0, "Comisión de Cobranza (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(16, headCellView);
            excelSheet.addCell(label);

            label = new Label(17, 0, "Comisión de Cobranza I.G.V. (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(17, headCellView);
            excelSheet.addCell(label);

            label = new Label(18, 0, "Interés Moratorio (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(18, headCellView);
            excelSheet.addCell(label);

            label = new Label(19, 0, "Interés Moratorio I.G.V. (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(19, headCellView);
            excelSheet.addCell(label);

            label = new Label(20, 0, "Interés (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(20, headCellView);
            excelSheet.addCell(label);

            label = new Label(21, 0, "Interés I.G.V. (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(21, headCellView);
            excelSheet.addCell(label);

            label = new Label(22, 0, "Capital de Cuota (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(22, headCellView);
            excelSheet.addCell(label);

            label = new Label(23, 0, "Cargo Moratorio (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(23, headCellView);
            excelSheet.addCell(label);

            label = new Label(24, 0, "Cargo Moratorio I.G.V. (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(24, headCellView);
            excelSheet.addCell(label);

            label = new Label(25, 0, "Retención de ISR (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(25, headCellView);
            excelSheet.addCell(label);

            label = new Label(26, 0, "Success Fee Solven (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(26, headCellView);
            excelSheet.addCell(label);

            label = new Label(27, 0, "Neto para el financiador (" + symbol + ")", headCellFormat);
            excelSheet.setColumnView(27, headCellView);
            excelSheet.addCell(label);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            if (result != null && result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    ReportCreditGateway creditCollection = result.get(i);

                    label = new Label(0, i + 1, String.valueOf(i + 1), leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(1, i + 1, creditCollection.getCountry() != null ? creditCollection.getCountry().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(2, i + 1, creditCollection.getRegisterDate() != null ? df.format(creditCollection.getRegisterDate()) : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(3, i + 1, creditCollection.getLoanApplicatonCode() != null ? creditCollection.getLoanApplicatonCode() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(4, i + 1, creditCollection.getCreditCode() != null ? creditCollection.getCreditCode() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(5, i + 1, creditCollection.getPersonId() != null ? creditCollection.getPersonId().toString() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(6, i + 1, creditCollection.getIdentityDocumentType() != null ? creditCollection.getIdentityDocumentType().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(7, i + 1, creditCollection.getDocumentNumber() != null ? creditCollection.getDocumentNumber() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(8, i + 1, creditCollection.getRuc() != null ? creditCollection.getRuc() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(9, i + 1, creditCollection.getSurname() != null ? creditCollection.getSurname() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(10, i + 1, creditCollection.getPersonName() != null ? creditCollection.getPersonName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(11, i + 1, creditCollection.getEntity() != null ? creditCollection.getEntity().getShortName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    label = new Label(12, i + 1, creditCollection.getEmployer() != null ? creditCollection.getEmployer().getName() : "", leftCellFormat);
                    excelSheet.addCell(label);

                    Number number = new Number(13, i + 1, creditCollection.getLoanCapital() != null ? creditCollection.getLoanCapital() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(14, i + 1, creditCollection.getAmount() != null ? creditCollection.getAmount() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(15, i + 1, creditCollection.getInsurance() != null ? creditCollection.getInsurance() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(16, i + 1, creditCollection.getCollectionCommission() != null ? creditCollection.getCollectionCommission() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(17, i + 1, creditCollection.getCollectionCommissionTax() != null ? creditCollection.getCollectionCommissionTax() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(18, i + 1, creditCollection.getMoratoriumInterest() != null ? creditCollection.getMoratoriumInterest() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(19, i + 1, creditCollection.getMoratoriumInterestTax() != null ? creditCollection.getMoratoriumInterestTax() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(20, i + 1, creditCollection.getInterest() != null ? creditCollection.getInterest() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(21, i + 1, creditCollection.getInterestTax() != null ? creditCollection.getInterestTax() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(22, i + 1, creditCollection.getInstallmentCapital() != null ? creditCollection.getInstallmentCapital() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(23, i + 1, creditCollection.getMoratoriumCharge() != null ? creditCollection.getMoratoriumCharge() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(24, i + 1, creditCollection.getMoratoriumChargeTax() != null ? creditCollection.getMoratoriumChargeTax() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(25, i + 1, creditCollection.getTaxWithHolding() != null ? creditCollection.getTaxWithHolding() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(26, i + 1, creditCollection.getSolvenDistribution() != null ? creditCollection.getSolvenDistribution() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                    number = new Number(27, i + 1, creditCollection.getNetToEntity() != null ? creditCollection.getNetToEntity() : 0.0, leftCellFormat);
                    excelSheet.addCell(number);

                }
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

    @RequestMapping(value = "/loanApplication/step2", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:processing", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsStep2(ModelMap model, Locale locale) throws Exception {
        model.addAttribute("wrapper", loanApplicationBoDao.getLoanApplicationsSummaries(backofficeService.getCountryActiveSysuser(), null, null, null, null, null, null, null, null, null, locale, 2, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT));
        return "loanApplicationsStep2";
    }

    @RequestMapping(value = "/loanApplication/step2/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:prefilter", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsStep2(
            ModelMap model, Locale locale,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {
        model.addAttribute("wrapper", loanApplicationBoDao.getLoanApplicationsSummaries(
                backofficeService.getCountryActiveSysuser(),
                amountFromFilter,
                amountToFilter,
                creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null,
                creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null,
                documentNumberFilter,
                entity != null ? (entity.length > 0 ? entity[0] : null) : null,
                employer != null ? (employer.length > 0 ? employer[0] : null) : null,
                reason != null ? (reason.length > 0 ? reason[0] : null) : null,
                analyst,
                locale, 2, offset, limit));
        return new ModelAndView("loanApplicationsStep2 :: list");
    }

    @RequestMapping(value = "/credit/{creditId}/assignAnalyst", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDisbursement(
            ModelMap model, Locale locale,
            RegisterDisbursementForm registerDisbursementForm,
            @PathVariable("creditId") Integer creditId) throws Exception {

        return null;
    }

    @RequestMapping(value = "/loanApplication/offer", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:offers", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsOffer(ModelMap model, Locale locale) throws Exception {
        return "redirect:/loanApplication/offerv2";
    }

    @RequestMapping(value = "/loanApplication/offerv2", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:offers", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsOffervs(ModelMap model, Locale locale) throws Exception {
        model.addAttribute("wrapper", loanAplicationBoService.getLoanApplicationsSummariesListEvaluation(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                backofficeService.getCountryActiveSysuser(),
                null,
                locale,
                0,
                Configuration.BACKOFFICE_PAGINATION_LIMIT));

        model.addAttribute("offset", 0);

        return "loanOffersv2";
    }

    @RequestMapping(value = "/loanApplication/offerv2/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:offers", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsOffersList(
            ModelMap model, Locale locale,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {

        Date creationFrom = creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null;
        Date creationTo = creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null;
        Integer entities = entity != null ? (entity.length > 0 ? entity[0] : null) : null;
        Integer employes = employer != null ? (employer.length > 0 ? employer[0] : null) : null;
        Integer reasons = reason != null ? (reason.length > 0 ? reason[0] : null) : null;

        model.addAttribute("wrapper", loanAplicationBoService.getLoanApplicationsSummariesListEvaluation(
                creationFrom,
                creationTo,
                amountFromFilter,
                amountToFilter,
                documentNumberFilter,
                entities,
                employes,
                reasons,
                backofficeService.getCountryActiveSysuser(),
                analyst,
                locale,
                offset,
                limit));

        model.addAttribute("offset", offset);

        return new ModelAndView("loanOffersv2 :: list");
    }

    @RequestMapping(value = "/loanApplication/offerv2/row", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:offers", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsManagementRow(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId) throws Exception {
        model.addAttribute("loan", loanApplicationBoDao.getEvaluationLoanApplicationById(loanApplicationId, Configuration.getDefaultLocale()));
        return new ModelAndView("loanOffersv2 :: row");
    }

    @RequestMapping(value = "/fraudAlerts/review", method = RequestMethod.GET)
    public String showFraudAlertsReview(ModelMap model, Locale locale) throws Exception {
        model.addAttribute("fraudAlerts", creditDao.getToReviewLoanApplicationFraudFlags());
        model.addAttribute("fraudAlertsLogs", creditDao.getLogFraudFlags());

        return "fraudAlertsReview";
    }

    @RequestMapping(value = "/person/review/loanApplicationFraudAlert", method = RequestMethod.POST)
    public Object reviewFraudAlert(ModelMap modelMap, Locale locale, HttpServletRequest request,
                                   @RequestParam("loanApplicationFraudAlertId") Integer loanApplicationFraudAlertId,
                                   @RequestParam("approve") Boolean approve) throws Exception {
        creditDao.reviewFraudFlag(loanApplicationFraudAlertId, approve, sysUserService.getSessionSysuser().getId());
        return AjaxResponse.redirect(request.getContextPath() + "/fraudAlerts/review");
    }

    @RequestMapping(value = "/loanApplication/pendingsignature", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:pendingSignature", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsPendingSignature(ModelMap model, Locale locale) throws Exception {
        model.addAttribute("wrapper", loanApplicationBoDao.getLoanApplicationsSummaries(backofficeService.getCountryActiveSysuser(), null, null, null, null, null, null, null, null, null, locale, 4, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT));
        return "pendingSignature";
    }

    @RequestMapping(value = "/loanApplication/pendingsignature/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:pendingSignature", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsPendingSignature(
            ModelMap model, Locale locale,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {
        model.addAttribute("wrapper", loanApplicationBoDao.getLoanApplicationsSummaries(
                backofficeService.getCountryActiveSysuser(),
                amountFromFilter,
                amountToFilter,
                creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null,
                creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null,
                documentNumberFilter,
                entity != null ? (entity.length > 0 ? entity[0] : null) : null,
                employer != null ? (employer.length > 0 ? employer[0] : null) : null,
                reason != null ? (reason.length > 0 ? reason[0] : null) : null,
                analyst,
                locale, 4, offset, limit));
        return new ModelAndView("pendingSignature :: list");
    }

    @RequestMapping(value = "/loanApplication/pendingaudit", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:pendingAudit", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsPendingAudit(ModelMap model, Locale locale) throws Exception {

        List<LoanApplicationSummaryBoPainter> loanApplications = loanApplicationBoDao.getLoanApplicationsToAudit(backofficeService.getCountryActiveSysuser(), locale);
        boolean atLeastOneInApproval = false;
        List<Integer> loanApplicationIdsInApproval = new ArrayList<>();

        if (loanApplications != null) {
            for (int i = 0; i < loanApplications.size(); i++) {
                LoanApplicationSummaryBoPainter loanApplication = loanApplications.get(i);

                if (loanApplication.getApprovalQueryBotIds() != null) {
                    Integer lastExecutedBot = loanApplication.getApprovalQueryBotIds().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                    if (lastExecutedBot != null) {
                        QueryBot queryBot = botDao.getQueryBot(lastExecutedBot);
                        if (queryBot != null) {
                            loanApplication.setLastApprovalBotStatusId(queryBot.getStatusId());

                            String errorMessage = JsonUtil.getStringFromJson(queryBot.getParameters(), "errorMessage", null);
                            loanApplication.setLastErrorMessageApprovalBot(errorMessage);

                            if (QueryBot.STATUS_QUEUE == queryBot.getStatusId() || QueryBot.STATUS_RUNNING == queryBot.getStatusId()) {
                                atLeastOneInApproval = true;
                                loanApplicationIdsInApproval.add(loanApplication.getId());
                            }
                        }
                    }
                }
            }
        }

        model.addAttribute("list", loanApplications);
        model.addAttribute("atLeastOneInApproval", atLeastOneInApproval);
        model.addAttribute("loanApplicationIdsInApproval", loanApplicationIdsInApproval);

        return "pendingAudit";
    }

    @RequestMapping(value = "/loanApplication/pendingaudit", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:pendingAudit", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object refreshLoanApplicationsPendingAudit(
            ModelMap model, Locale locale,
            @RequestParam(value = "loanApplicationIdsInApproval[]") Integer[] loanApplicationIdsInApprovalParam) throws Exception {

        List<Integer> loanApplicationIdsInApproval = new ArrayList<>();
        List<Integer> loanApplicationIdsInError = new ArrayList<>();
        List<Integer> loanApplicationIdsSuccess = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        boolean atLeastOneInApproval = false;

        if (loanApplicationIdsInApprovalParam.length > 0) {
            for (Integer loanApplicationId : loanApplicationIdsInApprovalParam) {
                LoanApplicationBoPainter loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale, LoanApplicationBoPainter.class);

                if (loanApplication.getApprovalQueryBotIds() != null) {
                    Integer lastExecutedBot = loanApplication.getApprovalQueryBotIds().stream().sorted(Comparator.reverseOrder()).findFirst().orElse(null);
                    if (lastExecutedBot != null) {
                        QueryBot queryBot = botDao.getQueryBot(lastExecutedBot);
                        if (queryBot != null) {

                            if (QueryBot.STATUS_QUEUE == queryBot.getStatusId() || QueryBot.STATUS_RUNNING == queryBot.getStatusId()) {
                                atLeastOneInApproval = true;
                                loanApplicationIdsInApproval.add(loanApplication.getId());
                            } else if (QueryBot.STATUS_FAIL == queryBot.getStatusId()) {
                                loanApplicationIdsInError.add(loanApplication.getId());
                                String errorMessage = JsonUtil.getStringFromJson(queryBot.getParameters(), "errorMessage", null);
                                errorMessages.add(errorMessage);
                            } else if (QueryBot.STATUS_SUCCESS == queryBot.getStatusId()){
                                loanApplicationIdsSuccess.add(loanApplication.getId());
                            }
                        }
                    }
                }
            }
        }

        model.addAttribute("atLeastOneInApproval", atLeastOneInApproval);
        model.addAttribute("loanApplicationIdsInApproval", loanApplicationIdsInApproval);
        model.addAttribute("loanApplicationIdsInError", loanApplicationIdsInError);
        model.addAttribute("loanApplicationIdsSuccess", loanApplicationIdsSuccess);
        model.addAttribute("errorMessages", errorMessages);

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @RequestMapping(value = "/loanApplication/pendingaudit/modal", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "loanapplication:audit:register", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsPendingAuditModal(
            ModelMap model, Locale locale,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam(value = "userFileSpeechId", required = false) Integer userFileSpeechId,
            @RequestParam(value = "userFileWelcomeId", required = false) Integer userFileWelcomeId) throws Exception {

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        LoanOffer selectedOffer = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        EntityProductParams params = catalogService.getEntityProductParamById(selectedOffer.getEntityProductParameterId());

        model.addAttribute("loanApplication", loanApplication);
        model.addAttribute("params", params);
        model.addAttribute("userFileSpeechId", userFileSpeechId);
        model.addAttribute("userFileWelcomeId", userFileWelcomeId);

        return "pendingAudit :: registerAuditModal";
    }

    @RequestMapping(value = "/loanApplication/pendingaudit/modal", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loanapplication:audit:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object postLoanApplicationsPendingAuditModal(
            Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("loanApplicationId") Integer loanApplicationId,
            @RequestParam("auditTypeId") Integer auditTypeId,
            @RequestParam("userFileId") Integer userFileId,
            @RequestParam(value = "rejectReasonId", required = false) Integer rejectReasonId,
            @RequestParam(value = "rejectionReasonComment", required = false) String rejectReasonComment,
            @RequestParam(value = "approved") boolean approved) throws Exception {
        Integer sysuserId = ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId();

        if (rejectReasonId != null && (rejectReasonComment != null && !rejectReasonComment.trim().isEmpty() && rejectReasonComment.trim().length() > 200)) {
            return AjaxResponse.errorMessage("El comentario no puede tener más de 200 caracteres");
        }

        if (approved) {
            LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale);

            if (Entity.ACCESO == loanApplication.getSelectedEntityId()) {
                webscrapperService.callApproveLoanApplication(loanApplication, sysuserId, locale, auditTypeId, userFileId);
            } else {
                LoanOffer offerSelected = loanApplicationDao.getLoanOffers(loanApplicationId)
                        .stream().filter(o -> o.getSelected()).findFirst().orElse(null);

                loanApplicationService.approveLoanApplicationWithoutAuditValidation(loanApplication, offerSelected, sysuserId, request, response, templateEngine, locale);
                creditDao.registerLoanApplicationAudit(loanApplicationId, auditTypeId, approved, userFileId, rejectReasonId, rejectReasonComment, sysuserId);
            }
        } else {
            creditDao.registerLoanApplicationAudit(loanApplicationId, auditTypeId, approved, userFileId, rejectReasonId, rejectReasonComment, sysuserId);
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/bot/runPending", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "person:externalData:executeBots", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> runBot(
            ModelMap model, Locale locale,
            @RequestParam("userId") Integer userId,
            @RequestParam("personId") Integer personId,
            @RequestParam("docType") Integer docType,
            @RequestParam("docNumber") String docNumber) throws Exception {
        String runningBots = "";
        List<ExternalUserInfoState> externals = userDao.getUserExternalInfoState(userId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        webscrapperService.setCountry(person.getCountry());

        if (person.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA) {
//            Optional<ExternalUserInfoState> afipInfoState = externals.stream().filter(e -> e.getBotId() == Bot.AFIP).findFirst();
//            if (afipInfoState.isPresent() && !afipInfoState.get().getResult()) {
//                webscrapperService.callAFIPBot(docType, docNumber, userId);
//                runningBots = runningBots + "<br>Afip";
//            }

            Optional<ExternalUserInfoState> ansesInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ANSES).findFirst();
            if (ansesInfoState.isPresent() && !ansesInfoState.get().getResult()) {
                webscrapperService.callANSESBot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Anses";
            }

            Optional<ExternalUserInfoState> bcraInfoState = externals.stream().filter(e -> e.getBotId() == Bot.BCRA).findFirst();
            if (bcraInfoState.isPresent() && !bcraInfoState.get().getResult()) {
                webscrapperService.callBCRABot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Bcra";
            }
        }

        /*if (person.getCountry().getId() == CountryParam.COUNTRY_PERU) {
            Optional<ExternalUserInfoState> sunatInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SUNAT_BOT).findFirst();
            if (sunatInfoState.isPresent() && !sunatInfoState.get().getResult()) {
                webscrapperService.callSunatDniBot(docNumber, userId);
                runningBots = runningBots + "<br>Sunat";
            }

//            Optional<ExternalUserInfoState> reniecInfoState = externals.stream().filter(e -> e.getBotId() == Bot.RENIEC_BOT).findFirst();
//            if (reniecInfoState.isPresent() && !reniecInfoState.get().getResult()) {
//                webscrapperService.callReniecBot(docNumber, userId);
//                runningBots = runningBots + "<br>Reniec";
//            }

//            Optional<ExternalUserInfoState> essaludInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ESSALUD_BOT).findFirst();
//            if (essaludInfoState.isPresent() && !essaludInfoState.get().getResult()) {
//                webscrapperService.callEssaludBot(docType, docNumber, userId);
//                runningBots = runningBots + "<br>Essalud";
//            }

//            Optional<ExternalUserInfoState> redamInfoState = externals.stream().filter(e -> e.getBotId() == Bot.REDAM_BOT).findFirst();
//            if (redamInfoState.isPresent() && !redamInfoState.get().getResult()) {
//                webscrapperService.callRedamBot(docType, docNumber, userId);
//                runningBots = runningBots + "<br>Redam";
//            }

            Optional<ExternalUserInfoState> claroInfoState = externals.stream().filter(e -> e.getBotId() == Bot.CLARO).findFirst();
            if (claroInfoState.isPresent() && !claroInfoState.get().getResult()) {
                webscrapperService.callClaroBot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Claro";
            }

            Optional<ExternalUserInfoState> movistarInfoState = externals.stream().filter(e -> e.getBotId() == Bot.MOVISTAR).findFirst();
            if (movistarInfoState.isPresent() && !movistarInfoState.get().getResult()) {
                webscrapperService.callMovistarBot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Movistar";
            }

            Optional<ExternalUserInfoState> bitelInfoState = externals.stream().filter(e -> e.getBotId() == Bot.BITEL).findFirst();
            if (bitelInfoState.isPresent() && !bitelInfoState.get().getResult()) {
                webscrapperService.callBitelBot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Bitel";
            }

            Optional<ExternalUserInfoState> entelInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ENTEL).findFirst();
            if (entelInfoState.isPresent() && !entelInfoState.get().getResult()) {
                webscrapperService.callEntelBot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Entel";
            }

//            Optional<ExternalUserInfoState> satInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SAT).findFirst();
//            if (satInfoState.isPresent() && !satInfoState.get().getResult()) {
//                webscrapperService.callSatBot(docType, docNumber, userId);
//                runningBots = runningBots + "<br>SAT";
//            }

//            Optional<ExternalUserInfoState> sisInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SIS).findFirst();
//            if (sisInfoState.isPresent() && !sisInfoState.get().getResult()) {
//                webscrapperService.callSisBot(docType, docNumber, userId);
//                runningBots = runningBots + "<br>SIS";
//            }

            if (docType == 2) {
                Optional<ExternalUserInfoState> migracionesInfoState = externals.stream().filter(e -> e.getBotId() == Bot.MIGRACIONES).findFirst();
                if (migracionesInfoState.isPresent() && !migracionesInfoState.get().getResult()) {
                    webscrapperService.callMigracionesBot(docNumber, person.getBirthday(), userId);
                    runningBots = runningBots + "<br>Migraciones";
                }
            }

            Optional<ExternalUserInfoState> virginInfoState = externals.stream().filter(e -> e.getBotId() == Bot.VIRGIN).findFirst();
            if (virginInfoState.isPresent() && !virginInfoState.get().getResult()) {
                webscrapperService.callVirginBot(docType, docNumber, userId);
                runningBots = runningBots + "<br>Virgin";
            }

            Optional<ExternalUserInfoState> onpeInfoState = externals.stream().filter(e -> e.getBotId() == Bot.ONPE).findFirst();
            if (onpeInfoState.isPresent() && !onpeInfoState.get().getResult()) {
                webscrapperService.callONPEBot(docNumber, userId);
                runningBots = runningBots + "<br>ONPE";
            }

            Optional<ExternalUserInfoState> universidadPeruInfoState = externals.stream().filter(e -> e.getBotId() == Bot.UNIVERSIDAD_PERU).findFirst();
            if (universidadPeruInfoState.isPresent() && !universidadPeruInfoState.get().getResult()) {

                PersonOcupationalInformation principalOccupation = personDao.getPersonOcupationalInformation(locale, personId)
                        .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                        .findFirst().orElse(null);

                if (principalOccupation != null) {
                    webscrapperService.callUniversidadPeruBot(principalOccupation.getRuc(), userId);
                    runningBots = runningBots + "<br>Universidad Perú";
                }
            }

//TODO
//            Optional<ExternalUserInfoState> satPlateInfoState = externals.stream().filter(e -> e.getBotId() == Bot.SAT_PLATE).findFirst();
//            if (satInfoState.isPresent() && !satInfoState.get().getResult()) {
//                webscrapperService.callSatBot(docType, docNumber, userId,plate);
//                runningBots = runningBots + "<br>SAT_PLATE";
//            }
        }*/


        model.addAttribute("onlyView", false);
        return AjaxResponse.ok(!runningBots.isEmpty() ? "Se ejecutarán los bots: " + runningBots : "Todos los bots se encuentran ejecutados");
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "backoffice:search", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String search(
            ModelMap model, Locale locale,
            @RequestParam("query") String query) throws Exception {
        model.addAttribute("searchResult", backofficeDAO.generalSearch(query, locale));
        return "generalSearchResult";
    }

    @RequestMapping(value = "/buffer", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:buffer", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showBuffer(
            ModelMap model, Locale locale) throws Exception {
        model.addAttribute("bufferTransactions", bufferTransactionDAO.getPendingBufferTransactions());
        return "buffer";
    }

    @RequestMapping(value = "/buffer/{bufferTransactionId}/pauseBufferTransaction", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "buffer:transaction:pause", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object pauseBufferTransaction(
            ModelMap model, Locale locale,
            @PathVariable("bufferTransactionId") Integer bufferTransactionId,
            @RequestParam("pause") boolean pause) throws Exception {
        bufferTransactionDAO.pauseBufferTransactions(bufferTransactionId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), pause);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/buffer/{bufferTransactionId}/deleteBufferTransaction", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "buffer:transaction:delete", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object pauseBufferTransaction(
            ModelMap model, Locale locale,
            @PathVariable("bufferTransactionId") Integer bufferTransactionId) throws Exception {
        bufferTransactionDAO.deleteBufferTransactions(bufferTransactionId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/parameters", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:parameters", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showParameters(
            ModelMap model, Locale locale) throws Exception {
        return "parameters";
    }

    @RequestMapping(value = "/parameters/bankParameters", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:parameters", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showBankparameters(
            ModelMap model, Locale locale,
            @RequestParam("bankId") int bankId) throws Exception {
        ComparisonRates comparisonRates = catalogService.getComparisonInfo(bankId);
        if (comparisonRates.getNewRates() != null && comparisonRates.getNewRates().size() > 0) {
            model.addAttribute("flagNew", false);
            model.addAttribute("maxId", comparisonRates.getNewRates().stream().mapToInt(e -> e.getRateId()).max().getAsInt());
            model.addAttribute("comparisonRates", comparisonRates.getNewRates());
        } else if (comparisonRates.getCurrentRates() != null && comparisonRates.getCurrentRates().size() > 0) {
            model.addAttribute("flagNew", false);
            model.addAttribute("maxId", comparisonRates.getCurrentRates().stream().mapToInt(e -> e.getRateId()).max().getAsInt());
            model.addAttribute("comparisonRates", comparisonRates.getCurrentRates());
        } else {
            Category category = catalogService.getComparisonCategoryById(1);
            ComparisonCategory comparisonCategory = new ComparisonCategory();
            comparisonCategory.setRateId(0);
            comparisonCategory.setBankId(bankId);
            comparisonCategory.setCategoryId(category.getId());
            List<ComparisonCost> comparisonCostList = new ArrayList<>();
            for (int i = 0; i < category.getCreditCosts().size(); i++) {
                Cost cost = catalogService.getComparisonCostById(category.getCreditCosts().get(i));
                ComparisonCost comparisonCost = new ComparisonCost();
                comparisonCost.setId(cost.getId());
                comparisonCost.setCostType(cost.getCostType());
                comparisonCostList.add(comparisonCost);
            }
            comparisonCategory.setCosts(comparisonCostList);
            List<ComparisonCategory> comparisonCategories = new ArrayList<>();
            comparisonCategories.add(comparisonCategory);
            model.addAttribute("flagNew", true);
            model.addAttribute("maxId", 1);
            model.addAttribute("comparisonRates", comparisonCategories);
        }
        return new ModelAndView("parameters :: #parametersTable");
    }

    @RequestMapping(value = "/parameters/save", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:parameters", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object saveParameters(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("bankId") int bankId,
            @RequestParam("jsonRow") String json) throws Exception {

        Gson gson = new Gson();
        try {
            List<ComparisonCategory> comparisonCategories = gson.fromJson(json, new TypeToken<List<ComparisonCategory>>() {
            }.getType());
            for (ComparisonCategory comparisonCategory : comparisonCategories) {
                if (comparisonCategory.getProductName() == null || comparisonCategory.getProductName().isEmpty())
                    return AjaxResponse.errorMessage("Debes ingresar el nombre del producto");
                if (comparisonCategory.getProductURL() == null || comparisonCategory.getProductURL().isEmpty())
                    return AjaxResponse.errorMessage("Debes ingresar la URL del producto");
            }
        } catch (NumberFormatException e) {
            return AjaxResponse.errorMessage("Debes ingresar valores para la TEA, el ingreso mínimo y los costos");
        }
        comparatorDAO.registerBankProductRates(bankId, json);
        return "parameters";
    }

    @RequestMapping(value = "/parameters/change", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:parameters", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object changeCostsList(
            ModelMap model, Locale locale,
            @RequestParam("bankId") int bankId,
            @RequestParam("categoryId") int categoryId) throws Exception {
        //ComparisonRates comparisonRates = catalogService.getComparisonInfo(bankId);
        Category category = catalogService.getComparisonCategory().stream().filter(e -> e.getId() == categoryId).findFirst().orElse(null);
        ComparisonCategory comparisonCategory = new ComparisonCategory();
        List<ComparisonCost> arrCosts = new ArrayList<>();
        for (int i = 0; i < category.getCreditCosts().size(); i++) {
            ComparisonCost comparisonCost = new ComparisonCost();
            Cost cost = catalogService.getComparisonCostById(category.getCreditCosts().get(i));
            comparisonCost.setId(cost.getId());
            comparisonCost.setCostType(cost.getCostType());
            arrCosts.add(comparisonCost);
        }
        comparisonCategory.setBankId(bankId);
        comparisonCategory.setRateId(0);
        comparisonCategory.setCategoryId(categoryId);
        comparisonCategory.setCosts(arrCosts);
        model.addAttribute("comparisonRate", comparisonCategory);

        return "parameters :: costsTable";
    }

    @RequestMapping(value = "/country/documentType", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getDocumentTypes(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("countryId") Integer countryId) throws Exception {

        if (countryId != null)
            return AjaxResponse.ok(new Gson().toJson(catalogService.getIdentityDocumentTypeByCountry(countryId, false)));
        else
            return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/parameters/apply", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:parameters", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object applyParameters(
            ModelMap model, Locale locale) throws Exception {
        comparatorDAO.applyProductRates();
        return "parameters";
    }

    @RequestMapping(value = "/address/save", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:parameters", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object saveAddress(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("jsonDireccionCasa") String jsonCasa,
            @RequestParam("jsonDireccionTrabajo") String jsonTrabajo,
            @RequestParam("personId") Integer personId) throws Exception {

        Gson gson = new Gson();
        try {
            Direccion direccionCasa = gson.fromJson(jsonCasa, Direccion.class);
            if (direccionCasa.getNombreVia() == null || direccionCasa.getNombreVia().isEmpty())
                return AjaxResponse.errorMessage("[Dirección Domicilio] - Debes ingresar el nombre de la vía");
            if (direccionCasa.getNombreZona() == null || direccionCasa.getNombreZona().isEmpty())
                return AjaxResponse.errorMessage("[Dirección Domicilio] - Debes ingresar el nombre de la zona");
            if (direccionCasa.getReferencia() == null || direccionCasa.getReferencia().isEmpty())
                return AjaxResponse.errorMessage("[Dirección Domicilio] - Debes ingresar la referencia");
            if (direccionCasa.getUbigeo() == null || direccionCasa.getUbigeo().isEmpty() || direccionCasa.getUbigeo().length() < 6)
                return AjaxResponse.errorMessage("[Dirección Domicilio] - Debes ingresar el ubigeo - departamento / provincia / distrito");

            Direccion direccionTrabajo = gson.fromJson(jsonTrabajo, Direccion.class);
            if (direccionTrabajo.getNombreVia() == null || direccionTrabajo.getNombreVia().isEmpty())
                return AjaxResponse.errorMessage("[Dirección Trabajo] - Debes ingresar el nombre de la vía");
            if (direccionTrabajo.getNombreZona() == null || direccionTrabajo.getNombreZona().isEmpty())
                return AjaxResponse.errorMessage("[Dirección Trabajo] - Debes ingresar el nombre de la zona");
            if (direccionTrabajo.getReferencia() == null || direccionTrabajo.getReferencia().isEmpty())
                return AjaxResponse.errorMessage("[Dirección Trabajo] - Debes ingresar la referencia");
            if (direccionTrabajo.getUbigeo() == null || direccionTrabajo.getUbigeo().isEmpty() || direccionTrabajo.getUbigeo().length() < 6)
                return AjaxResponse.errorMessage("[Dirección Trabajo] - Debes ingresar el ubigeo - departamento / provincia / distrito");

            personDao.registerDisgregatedAddress(personId, direccionCasa);
            personDao.registerDisgregatedAddress(personId, direccionTrabajo);

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage("Error al registrar firmas");
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/loanApplication/selfEvaluation", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:selfEvaluation", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsSelfEvaluation(ModelMap model, Locale locale) throws Exception {
        model.addAttribute("wrapper", loanApplicationBoDao.getLoanApplicationsSelfEvaluation(backofficeService.getCountryActiveSysuser(), null, null, null, null, null, null, null, locale, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT));
        return "selfEvaluation";
    }

    @RequestMapping(value = "/loanApplication/selfEvaluation/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:selfEvaluation", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showLoanApplicationsSelfEvaluation(
            ModelMap model, Locale locale,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "score[]", required = false) Integer[] score,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {

        PaginationWrapper<SelfEvaluationBoPainter> loanApplications = loanApplicationBoDao.getLoanApplicationsSelfEvaluation(
                backofficeService.getCountryActiveSysuser(),
                amountFromFilter,
                amountToFilter,
                creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null,
                creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null,
                documentNumberFilter,
                reason != null ? (reason.length > 0 ? reason[0] : null) : null,
                score != null ? (score.length > 0 ? score[0] : null) : null,
                locale, offset, limit);

        model.addAttribute("offset", offset);
        model.addAttribute("limit", limit);
        model.addAttribute("total", loanApplications != null ? loanApplications.getResults().size() : 0);
        model.addAttribute("wrapper", loanApplications);

        return new ModelAndView("selfEvaluation :: list");
    }

    @RequestMapping(value = "/reporting/originationDashboard", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingOrigination2(ModelMap model, Locale locale) throws Exception {

        Calendar period1 = Calendar.getInstance();
        Calendar period2 = Calendar.getInstance();
        period2.add(Calendar.DATE, -1);

        List<OriginationReportPeriod> periods = backofficeDAO.getOriginationProductReportPeriod(backofficeService.getCountryActiveSysuser(), period1.getTime(), period1.getTime(), period2.getTime(), period2.getTime(), null);
        OriginationReportDashboardPainter report = new OriginationReportDashboardPainter(periods);
        report.processReport();

        model.addAttribute("report", report);
        model.addAttribute("period1From", period1.getTime());
        model.addAttribute("period1To", period1.getTime());
        model.addAttribute("period2From", period2.getTime());
        model.addAttribute("period2To", period2.getTime());
        return "reportOrigination2";
    }

    @RequestMapping(value = "/reporting/originationDashboard/result", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingOrigination2Results(
            ModelMap model, Locale locale,
            @RequestParam(value = "period1FromFilter[]", required = false) String period1FromFilter,
            @RequestParam(value = "period1ToFilter[]", required = false) String period1ToFilter,
            @RequestParam(value = "period2FromFilter[]", required = false) String period2FromFilter,
            @RequestParam(value = "period2ToFilter[]", required = false) String period2ToFilter,
            @RequestParam(value = "entity[]", required = false) Integer[] entity) throws Exception {

        Date period1From = period1FromFilter != null ? new SimpleDateFormat("dd-MM-yyyy").parse(period1FromFilter) : null;
        Date period1To = period1ToFilter != null ? new SimpleDateFormat("dd-MM-yyyy").parse(period1ToFilter) : null;
        Date period2From = period2FromFilter != null ? new SimpleDateFormat("dd-MM-yyyy").parse(period2FromFilter) : null;
        Date period2To = period2ToFilter != null ? new SimpleDateFormat("dd-MM-yyyy").parse(period2ToFilter) : null;

        List<OriginationReportPeriod> periods = backofficeDAO.getOriginationProductReportPeriod(backofficeService.getCountryActiveSysuser(), period1From, period1To, period2From, period2To, entity);
        OriginationReportDashboardPainter report = new OriginationReportDashboardPainter(periods);
        report.processReport();

        model.addAttribute("report", report);
        model.addAttribute("period1From", period1From);
        model.addAttribute("period1To", period1To);
        model.addAttribute("period2From", period2From);
        model.addAttribute("period2To", period2To);
        return "reportOrigination2 :: results";
    }

    @RequestMapping(value = "/reporting/originationEntityProduct", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingOrigination3(ModelMap model, Locale locale) throws Exception {
        return "reportOrigination3";
    }

    @RequestMapping(value = "/reporting/originationEntityProduct/export", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public void showReportingOrigination3(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        byte[] bytes = reportsBoService.createOriginationReportEntityGrouped(backofficeService.getCountryActiveSysuser(), currencyService.getCurrencySymbolReport(), backofficeService.getSeparator());
        response.setHeader("Content-disposition", "attachment; filename=Reporte_originacion_por_entidad.xlsx");
        response.getOutputStream().write(bytes);
    }

    @RequestMapping(value = "/report/adwords/view", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String exportAdwordsView(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "reportAdWords";
    }

    @RequestMapping(value = "/report/adwords/export", method = RequestMethod.GET)
    public void exportAdwords(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
            @RequestParam(value = "creationTo[]", required = false) String creationTo) throws Exception {
        Date from = new Date();
        Date to = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (creationFrom != null && !creationFrom.isEmpty() && creationTo != null && !creationTo.isEmpty()) {
            from = formatter.parse(creationFrom);
            to = formatter.parse(creationTo);
        }

        CSVWriter writer = new CSVWriter();
        String reportName = "AdWords - Pre-evaluaciones.csv";
        JSONArray jsonArray = loanApplicationDao.getAdwordsConversions(from, to);
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment;filename=" + reportName);
        if (jsonArray != null && jsonArray.length() > 0) {
            JsonFlattener parser = new JsonFlattener();
            List<Map<String, String>> flatJson = parser.parseJson(jsonArray.toString());
            response.getOutputStream().write(writer.writeAsCSV(flatJson, reportName).getBytes());
        } else {
            response.getOutputStream().write(new String("No hay registros a mostrar").getBytes());
        }
    }

    @RequestMapping(value = "/reporting/funnelDashboard", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingFunnel(ModelMap model) throws Exception {

        Calendar period1From = Calendar.getInstance();
        period1From.set(Calendar.HOUR_OF_DAY, 0);
        period1From.set(Calendar.MINUTE, 0);
        period1From.set(Calendar.SECOND, 0);

        Calendar period2From = Calendar.getInstance();
        period2From.add(Calendar.DATE, -1);
        period2From.set(Calendar.HOUR_OF_DAY, 0);
        period2From.set(Calendar.MINUTE, 0);
        period2From.set(Calendar.SECOND, 0);

        Calendar period1To = Calendar.getInstance();
        period1To.set(Calendar.HOUR_OF_DAY, 23);
        period1To.set(Calendar.MINUTE, 59);
        period1To.set(Calendar.SECOND, 59);

        Calendar period2To = Calendar.getInstance();
        period2To.add(Calendar.DATE, -1);
        period2To.set(Calendar.HOUR_OF_DAY, 23);
        period2To.set(Calendar.MINUTE, 59);
        period2To.set(Calendar.SECOND, 59);

        String countryActiveUser = backofficeService.getCountryActiveSysuser();

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_FUNNEL_DASHBOARD_BO, 0, 10);

        model.addAttribute("historicReports", historicReports);
        model.addAttribute("period1From", period1From.getTime());
        model.addAttribute("period1To", period1To.getTime());
        model.addAttribute("period2From", period2From.getTime());
        model.addAttribute("period2To", period2To.getTime());
        model.addAttribute("countryActiveUser", countryActiveUser);

        return "reportFunnelDashboard";
    }

    @RequestMapping(value = "/reporting/funnelDashboard/export", method = RequestMethod.POST)
    //@RequiresPermissionOr403(permissions = "menu:reporting:funnelDashboard", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingFunnelDashboard(@RequestParam(value = "dateType[]", required = false) Integer dateType,
                                               @RequestParam(value = "period1FromFilter[]", required = false) String period1FromFilter,
                                               @RequestParam(value = "period1ToFilter[]", required = false) String period1ToFilter,
                                               @RequestParam(value = "period2FromFilter[]", required = false) String period2FromFilter,
                                               @RequestParam(value = "period2ToFilter[]", required = false) String period2ToFilter,
                                               @RequestParam(value = "country[]", required = false) Integer[] countryId,
                                               @RequestParam(value = "entity[]", required = false) Integer entity,
                                               @RequestParam(value = "disbursementType[]", required = false) Integer[] disbursementType) throws Exception {
        Date period1From = new Date();
        Date period1To = new Date();
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
        if (period1FromFilter != null && !period1FromFilter.isEmpty() && period1ToFilter != null && !period1ToFilter.isEmpty()) {
            period1From = formatter1.parse(period1FromFilter);
            period1To = formatter1.parse(period1ToFilter);
        }

        Date period2From = new Date();
        Date period2To = new Date();
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy");
        if (period2FromFilter != null && !period2FromFilter.isEmpty() && period2ToFilter != null && !period2ToFilter.isEmpty()) {
            period2From = formatter2.parse(period2FromFilter);
            period2To = formatter2.parse(period2ToFilter);
        }

        if (disbursementType.length == 0) {
            disbursementType = null;
        }

        reportsService.createReporteFunnelDashboardBo(backofficeService.getLoggedSysuser().getId(), dateType, period1From, period1To, period2From, period2To, countryId, entity, disbursementType);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reporting/funnelDashboard/list", method = RequestMethod.POST)
    //@RequiresPermissionOr403(permissions = "menu:reporting:funnelDashboard", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showFunnelDashboardReportList(Model model) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_FUNNEL_DASHBOARD_BO, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("reportFunnelDashboard :: results");
    }

    @RequestMapping(value = "/reporting/funnelReport", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingFunnelExport(ModelMap model, Locale locale) throws Exception {
        List<ReportProces> historicReports = reportsDao.getFunnelReportProcesHistoric(
                WebApplication.BACKOFFICE, Report.REPORTE_FUNNEL, null, 0, 10);
        List<String> sources = loanApplicationService.getUtmSources();
        model.addAttribute("sources", sources);
        model.addAttribute("historicReports", historicReports);
        return "reportFunnel";
    }

    @RequestMapping(value = "/reporting/funnelReport/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingFunnelExport(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
                                            @RequestParam(value = "creationTo[]", required = false) String creationTo,
                                            @RequestParam(value = "status[]", required = false) Integer dateType,
                                            @RequestParam(value = "entities[]", required = false) Integer[] entities,
                                            @RequestParam(value = "product[]", required = false) Integer[] products,
                                            @RequestParam(value = "source[]", required = false) String source,
                                            @RequestParam(value = "medium[]", required = false) String medium,
                                            @RequestParam(value = "campaign[]", required = false) String campaign,
                                            @RequestParam(value = "country[]", required = false) Integer[] country) throws Exception {

        if (country == null || country.length == 0) {
            return AjaxResponse.errorMessage("Se debe seleccionar al menos un país");
        }

        int origin = WebApplication.BACKOFFICE;
        Date from;
        Date to;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (creationFrom != null && !creationFrom.isEmpty() && creationTo != null && !creationTo.isEmpty()) {
            from = formatter.parse(creationFrom);
            to = formatter.parse(creationTo);
        } else {
            Calendar today = Calendar.getInstance();

            from = today.getTime();
            to = today.getTime();
        }

        reportsService.createReporteFunnelBo(backofficeService.getLoggedSysuser().getId(), from, to, dateType, entities, products, source, medium, campaign, origin, country);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reporting/funnelDashboard/getEntities", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getEntitiesParameters(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "country", required = true) Integer country) throws Exception {

        List<Entity> entities = catalogService.getEntities();
        List<Entity> entitiesByCountry = entities.stream().filter(entity -> entity.getCountryId().intValue() == country.intValue()).collect(Collectors.toList());
        return AjaxResponse.ok(new Gson().toJson(entitiesByCountry));
    }

    @RequestMapping(value = "/reporting/funnelReport/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showFunnelReportList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getFunnelReportProcesHistoric(
                WebApplication.BACKOFFICE, Report.REPORTE_FUNNEL, null, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("reportFunnel :: results");
    }

    @RequestMapping(value = "/reporting/funnelReport/getMediums", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getMediumParameters(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                      @RequestParam(value = "source", required = false) String source) throws Exception {

        List<String> mediums = loanApplicationService.getUtmMediumsBySource(source);
        return AjaxResponse.ok(new Gson().toJson(mediums));
    }

    @RequestMapping(value = "/reporting/funnelReport/getCampaings", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getCampaignParameters(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "medium", required = false) String medium,
                                        @RequestParam(value = "source", required = false) String source) throws Exception {

        List<String> campaigns = loanApplicationService.getUtmCampaignsByMedium(source, medium);
        return AjaxResponse.ok(new Gson().toJson(campaigns));
    }

    @RequestMapping(value = "/speech", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:speech", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showSpeech(
            ModelMap model, Locale locale) throws Exception {
        return "speech";
    }

    @RequestMapping(value = "/entity/product", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getProducts(
            ModelMap model, Locale locale,
            @RequestParam("entityId") Integer entityId) throws Exception {
        Gson gson = new Gson();
        return AjaxResponse.ok(gson.toJson(catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(entityId)).distinct().collect(Collectors.toList())));
    }

    @RequestMapping(value = "/speech/save", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object saveSpeech(
            ModelMap model, Locale locale,
            @RequestParam("entityProductId") Integer entityProductParameterId,
            @RequestParam("speechTypeId") Integer speechTypeId,
            @RequestParam("speech") String speech) throws Exception {

        backofficeDAO.updateSpeech(entityProductParameterId, speechTypeId, speech);
        ehCacheManager.clearAll();
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/speech/get", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:speech", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public String getSpeech(
            ModelMap model, Locale locale,
            @RequestParam("entityProductId") Integer entityProductParameterId,
            @RequestParam("speechTypeId") Integer speechTypeId) throws Exception {
        EntityProductParams entityProductParams = catalogService.getEntityProductParamById(entityProductParameterId);
        if (speechTypeId == 2)
            model.addAttribute("speech", entityProductParams.getCommercialSpeech());
        else if (speechTypeId == 1)
            model.addAttribute("speech", entityProductParams.getWellcomeSpeech());
        return "speech :: summerNote";
    }

    @RequestMapping(value = "/reporting/screenReporting", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:screens", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showReportingscreenTrack(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ProcessQuestion> questions = catalogService.getProcessQuestions();
        List<ReportProces> historicScreenReports = reportsDao.getReportProcesHistoric(Report.REPORTE_DE_PANTALLAS, 0, 10);
        List<ReportProces> historicTrackScreenReports = reportsDao.getReportProcesHistoric(Report.REPORTE_DE_PANTALLAS_RECORRIDAS, 0, 10);

        Collections.sort(questions, (left, right) -> left.getId() - right.getId());
        model.addAttribute("historicTrackScreenReports", historicTrackScreenReports);
        model.addAttribute("historicScreenReports", historicScreenReports);
        model.addAttribute("processQuestions", questions);
        return "reportScreenTrack";
    }

    @RequestMapping(value = "/reporting/screenTrackReporting/export", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:screens", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showReportingscreenTrackExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("fromFilter") String fromFilter,
            @RequestParam("untilFilter") String untilFilter) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        reportsService.createReportPantallasRecorridas(backofficeService.getLoggedSysuser().getId(), backofficeService.getCountryActiveSysuser(), sdf.parse(fromFilter), sdf.parse(untilFilter));

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reporting/screenReporting/export", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:screens", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showReportingscreenExcel(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("fromFilter") String fromFilter,
            @RequestParam("untilFilter") String untilFilter,
            @RequestParam(value = "productFilter[]", required = false) Integer[] productFilter,
            @RequestParam(value = "statusFilter[]", required = false) Integer[] statusFilter) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        reportsService.createReportPantallas(backofficeService.getLoggedSysuser().getId(), backofficeService.getCountryActiveSysuser(), sdf.parse(fromFilter), sdf.parse(untilFilter), productFilter, statusFilter);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reporting/screenTrackReporting/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:screens", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showReportingscreenTrackList(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_DE_PANTALLAS_RECORRIDAS, 0, 10);
        model.addAttribute("historicTrackScreenReports", historicReports);
        return new ModelAndView("reportScreenTrack :: trackresult");
    }

    @RequestMapping(value = "/reporting/screenReporting/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:screens", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showReportingscreenList(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_DE_PANTALLAS, 0, 10);
        model.addAttribute("historicScreenReports", historicReports);
        return new ModelAndView("reportScreenTrack :: screensresult");
    }

    @RequestMapping(value = "/reporting/loansLight", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:loansLight", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoansLightReport(ModelMap model) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(0, Report.REPORTE_SOLICITUDES_LIGHT, 0, 10, WebApplication.BACKOFFICE);
        model.addAttribute("historicReports", historicReports);
        model.addAttribute("entities", catalogService.getEntities());
        return "reportApplicationsLight";
    }

    @RequestMapping(value = "/reporting/loansLight/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:loansLight", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoansLightReportList(ModelMap model) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(0, Report.REPORTE_SOLICITUDES_LIGHT, 0, 10, WebApplication.BACKOFFICE);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("reportApplicationsLight :: results");
    }

    @RequestMapping(value = "/reporting/loansLight/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:loansLight", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingLoansLight(@RequestParam(value = "creationFrom[]", required = false) String creationFrom,
                                          @RequestParam(value = "creationTo[]", required = false) String creationTo,
                                          @RequestParam(value = "countries[]", required = false) Integer[] countries,
                                          @RequestParam(value = "entities[]", required = false) Integer[] entities) throws Exception {
        Date from = new Date();
        Date to = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (creationFrom != null && !creationFrom.isEmpty() && creationTo != null && !creationTo.isEmpty()) {
            from = formatter.parse(creationFrom);
            to = formatter.parse(creationTo);
        }
        reportsService.createReporteSolicitudesLight(backofficeService.getLoggedSysuser().getId(), from, to, countries, entities, WebApplication.BACKOFFICE);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reporting/loans", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoansReport(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_SOLICITUDES_BO, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return "reportApplications";
    }

    @RequestMapping(value = "/reporting/loans/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoansReportList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_SOLICITUDES_BO, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("reportApplications :: results");
    }

    @RequestMapping(value = "/reporting/originations/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showOriginationsReportList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_ORIGINACIONES_BO, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("reportOrigination :: results");
    }

    @RequestMapping(value = "/reporting/loans/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:origination", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingLoans(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                     @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
                                     @RequestParam(value = "creationTo[]", required = false) String creationTo,
                                     @RequestParam(value = "country[]", required = false) String country) throws Exception {
        Date from = new Date();
        Date to = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        if (creationFrom != null && !creationFrom.isEmpty() && creationTo != null && !creationTo.isEmpty()) {
            from = formatter.parse(creationFrom);
            to = formatter.parse(creationTo);
        }
        reportsService.createReporteSolicitudesBo(backofficeService.getLoggedSysuser().getId(), from, to, country);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reporting/operatorsManagements", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:operatorsManagements", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showOperatorsManagementsReport(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_GESTION_OPERADORES, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return "reportOperatorsManagements";
    }

    @RequestMapping(value = "/reporting/operatorsManagements/result", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:operatorsManagements", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showOperatorsManagementsReportResults(
            ModelMap model, Locale locale,
            @RequestParam(value = "period1FromFilter[]", required = false) String period1FromFilter,
            @RequestParam(value = "period1ToFilter[]", required = false) String period1ToFilter,
            @RequestParam(value = "period2FromFilter[]", required = false) String period2FromFilter,
            @RequestParam(value = "period2ToFilter[]", required = false) String period2ToFilter,
            @RequestParam(value = "sysUser[]", required = false) Integer sysUserId,
            @RequestParam(value = "entity[]", required = false) Integer entityId,
            @RequestParam(value = "product[]", required = false) Integer productId) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_GESTION_OPERADORES, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return "reportOperatorsManagements :: list";
    }

    @RequestMapping(value = "/reporting/operatorsManagements/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:operatorsManagements", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showOperatorsManagementsReportExport(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "period1FromFilter[]", required = true) String period1FromFilter,
            @RequestParam(value = "period1ToFilter[]", required = true) String period1ToFilter,
            @RequestParam(value = "period2FromFilter[]", required = true) String period2FromFilter,
            @RequestParam(value = "period2ToFilter[]", required = true) String period2ToFilter,
            @RequestParam(value = "sysUser[]", required = false) Integer sysUserId,
            @RequestParam(value = "entity[]", required = false) Integer entityId,
            @RequestParam(value = "product[]", required = false) Integer productId) throws Exception {

        if ((period1FromFilter == null || "".equals(period1FromFilter)) || (period1ToFilter == null || "".equals(period1ToFilter))) {
            return AjaxResponse.errorMessage("[Periodo 1] Debes ingresar un rango de fechas");
        } else if (new SimpleDateFormat("dd/MM/yyyy").parse(period1FromFilter).compareTo(new SimpleDateFormat("dd/MM/yyyy").parse(period1ToFilter)) > 0) {
            return AjaxResponse.errorMessage("[Periodo 1] Debes ingresar un rango de fechas válido");
        }
        if ((period2FromFilter == null || "".equals(period2FromFilter)) || (period2ToFilter == null || "".equals(period2ToFilter))) {
            return AjaxResponse.errorMessage("[Periodo 2] Debes ingresar un rango de fechas");
        } else if (new SimpleDateFormat("dd/MM/yyyy").parse(period2FromFilter).compareTo(new SimpleDateFormat("dd/MM/yyyy").parse(period2ToFilter)) > 0) {
            return AjaxResponse.errorMessage("[Periodo 2] Debes ingresar un rango de fechas válido");
        }

        reportsService.createReporteGestionOperadoresBO(
                backofficeService.getLoggedSysuser().getId(),
                backofficeService.getCountryActiveSysuser(),
                new SimpleDateFormat("dd/MM/yyyy").parse(period1FromFilter),
                new SimpleDateFormat("dd/MM/yyyy").parse(period1ToFilter),
                new SimpleDateFormat("dd/MM/yyyy").parse(period2FromFilter),
                new SimpleDateFormat("dd/MM/yyyy").parse(period2ToFilter),
                sysUserId,
                entityId,
                productId,
                currencyService.getCurrencySymbolReport()
        );

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/upload/preapproved", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:uploadPreApproved", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String uploadPreApprovedBase(
            ModelMap model, Locale locale) throws Exception {
        return "uploadPreApproved";
    }

    @RequestMapping(value = "/upload/preapproved", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "system:uploadPreApproved", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object uploadPreApprovedBase(
            ModelMap model, Locale locale,
            @RequestParam("entity") Integer entity,
            @RequestParam("product") Integer product,
            @RequestParam("fileName") String fileName) throws Exception {

        if (entity == null || product == null || fileName == null)
            return AjaxResponse.errorMessage("Los valores enviados no son válidos");

        if (!fileService.fileExistsInFolder(FileServiceImpl.S3Folder.PREAPPROVED_BASE_FOLDER, fileName))
            return AjaxResponse.errorMessage("El archivo no existe.");

        webscrapperService.callUploadPreApprovedBase(entity, product, fileName);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/upload/preapproved/list", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:uploadPreApproved", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getUploadPreApprovedBase(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<QueryBot> queryBots = botDao.getQueryBotsByBotId(Bot.UPLOAD_PRE_APPROVED_BASE, 10, 0);
        model.addAttribute("queryBots", queryBots);
        return new ModelAndView("uploadPreApproved :: results");
    }

    @RequestMapping(value = "/sendBulkSms", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:sendBulkSms", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String sendSms(
            ModelMap model, Locale locale) throws Exception {
        return "sendBulkSms";
    }

    @RequestMapping(value = "/sendBulkSms", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "system:sendBulkSms", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object sendSms(
            ModelMap model, Locale locale,
            @RequestParam("country") Integer country,
            @RequestParam("message") String message,
            @RequestParam("fileName") String fileName,
            @RequestParam("scheduled") String scheduled) throws Exception {

        if (country == null || message == null || fileName == null)
            return AjaxResponse.errorMessage("Los valores enviados no son válidos");

        if (!fileService.fileExistsInFolder(FileServiceImpl.S3Folder.SMS_BULK_FOLDER, fileName))
            return AjaxResponse.errorMessage("El archivo no existe.");

        Date scheduledDate = null;
        if (scheduled != null && !scheduled.trim().isEmpty()) {
            scheduledDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(scheduled);
        }

        webscrapperService.callSendSms(country, message, fileName, scheduledDate, backofficeService.getLoggedSysuser().getId());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/sendBulkSms/list", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:sendBulkSms", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getSendSmsList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Pair<QueryBot, LogSmsBulkSend>> pairs = new ArrayList<>();

        List<QueryBot> queryBots = botDao.getQueryBotsByBotId(Bot.SEND_SMS, 10, 0);
        for (QueryBot queryBot : queryBots) {
            LogSmsBulkSend log = serviceLogDao.getLogSmsBulkSendByQueryBot(queryBot.getId());
            pairs.add(Pair.of(queryBot, log));
        }

        model.addAttribute("pairs", pairs);
        return new ModelAndView("sendBulkSms :: results");
    }

    @RequestMapping(value = "/employers", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object extranetEmployers(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Employer> employers = catalogService.getEmployers();
        modelMap.addAttribute("form", new EmployerForm());
        modelMap.addAttribute("employers", employers);
        return new ModelAndView("/registerEmployer");
    }

    @RequestMapping(value = "/employers/employer", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object extranetGroup(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                @RequestParam("employerId") Integer employerId) throws Exception {
        JSONObject resJson = new JSONObject();
        Employer employer = catalogService.getEmployer(employerId);

        JSONObject jsEmployer = null;
        if (employer != null) {
            jsEmployer = new JSONObject();
            jsEmployer.put("name", employer.getName());
            jsEmployer.put("ruc", employer.getRuc());
            jsEmployer.put("address", employer.getAddress());
            jsEmployer.put("phone", employer.getPhoneNumber());
            jsEmployer.put("profession", employer.getProfession() != null ? employer.getProfession().getId() : null);
            jsEmployer.put("daysBeforeEndOfMonth", employer.getDaysBeforeEndOfMonth());
            jsEmployer.put("daysAfterEndOfMonth", employer.getDaysAfterEndOfMonth());
        }

        List<UserEmployer> userEmployers = employerDAO.getUserEmployersByEmployer(employerId, locale);
        JSONArray userEmployersjsonArr = new JSONArray();
        if (userEmployers != null) {
            for (UserEmployer userEmployer : userEmployers) {
                JSONObject jsUserEmployer = new JSONObject();
                jsUserEmployer.put("employerUserId", userEmployer.getId());
                jsUserEmployer.put("personName", userEmployer.getName());
                jsUserEmployer.put("firstSurname", userEmployer.getFirstSurname());
                jsUserEmployer.put("lastSurname", userEmployer.getLastSurname());
                jsUserEmployer.put("email", userEmployer.getEmail());
                userEmployersjsonArr.put(jsUserEmployer);
            }
        }

        resJson.put("employer", jsEmployer);
        resJson.put("usersEmployer", userEmployersjsonArr);
        return AjaxResponse.ok(resJson.toString());
    }

    @RequestMapping(value = "/employers/update", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object extranetEmployerUpdate(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response,
                                         EmployerForm form) throws Exception {
        JSONObject resJson = new JSONObject();
        if (form.getId() != null) {

            employerDAO.updateEmployer(form.getId(), form.getName(), form.getRuc(), form.getAddress(), form.getPhone(), form.getProfession(), form.getDaysAfterEndOfMonth(), form.getDaysBeforeEndOfMonth());
            ehCacheManager.clearAll();
            JSONArray arrayJsonUsers = new JSONArray();
            int index = 0;

            if (form.getUser1Id() != null) {
                employerDAO.updateUserEmployer(form.getUser1Id(), form.getUser1Name(), form.getUser1FirstSurname(), form.getUser1LastSurname(), form.getUser1Email());
            } else {
                if (form.getUser1Email() != null && !form.getUser1Email().equals("")) {
                    arrayJsonUsers.put(index++, new JSONObject()
                            .put("person_name", form.getUser1Name())
                            .put("email", form.getUser1Email())
                            .put("first_surname", form.getUser1FirstSurname())
                            .put("last_surname", form.getUser1LastSurname()));
                }

            }
            if (form.getUser2Id() != null) {
                employerDAO.updateUserEmployer(form.getUser2Id(), form.getUser2Name(), form.getUser2FirstSurname(), form.getUser2LastSurname(), form.getUser2Email());
            } else {
                if (form.getUser2Email() != null && !form.getUser2Email().equals("")) {
                    arrayJsonUsers.put(index++, new JSONObject()
                            .put("person_name", form.getUser2Name())
                            .put("email", form.getUser2Email())
                            .put("first_surname", form.getUser2FirstSurname())
                            .put("last_surname", form.getUser2LastSurname()));
                }
            }

            if (form.getUser3Id() != null) {
                employerDAO.updateUserEmployer(form.getUser3Id(), form.getUser3Name(), form.getUser3FirstSurname(), form.getUser3LastSurname(), form.getUser3Email());
            } else {
                if (form.getUser3Email() != null && !form.getUser3Email().equals("")) {
                    arrayJsonUsers.put(index++, new JSONObject()
                            .put("person_name", form.getUser3Name())
                            .put("email", form.getUser3Email())
                            .put("first_surname", form.getUser3FirstSurname())
                            .put("last_surname", form.getUser3LastSurname()));
                }
            }
            if (arrayJsonUsers.length() > 0) {
                employerService.updateUserEmployer(form.getId(), form.getRuc(), form.getName(), arrayJsonUsers);
            }
        }

        //form.getValidator().validate(locale);
        //if(!form.getValidator().isHasErrors()){
        //employerDAO.updateEmployer(form.getId(), form.getName(), form.getRuc(), form.getAddress(), form.getPhone(), form.getDaysAfterEndOfMonth(), form.getDaysBeforeEndOfMonth());
        //return AjaxResponse.ok();
        //}

        return AjaxResponse.ok(resJson.toString());
    }

    @RequestMapping(value = "/employers", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object extranetRegisterEmployer(ModelMap modelMap, Locale locale, HttpServletRequest request, HttpServletResponse response, EmployerForm form) throws Exception {

        form.getValidator().validate(locale);
        if (form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        JSONArray arrayJsonUsers = new JSONArray();

        if (form.getUser1Email() != null) {
            arrayJsonUsers.put(0, new JSONObject()
                    .put("person_name", form.getUser1Name())
                    .put("email", form.getUser1Email())
                    .put("first_surname", form.getUser1FirstSurname())
                    .put("last_surname", form.getUser1LastSurname()));
        }

        if (form.getUser2Email() != null && !form.getUser2Email().equals("")) {
            arrayJsonUsers.put(1, new JSONObject()
                    .put("person_name", form.getUser2Name())
                    .put("email", form.getUser2Email())
                    .put("first_surname", form.getUser2FirstSurname())
                    .put("last_surname", form.getUser2LastSurname()));
        }

        if (form.getUser3Email() != null && !form.getUser3Email().equals("")) {
            arrayJsonUsers.put(2, new JSONObject()
                    .put("person_name", form.getUser3Name())
                    .put("email", form.getUser3Email())
                    .put("first_surname", form.getUser3FirstSurname())
                    .put("last_surname", form.getUser3LastSurname()));
        }

        employerService.registerEmployer(Entity.AFFIRM, form.getName(), form.getRuc(), form.getAddress(),
                form.getPhone(), form.getProfession(), form.getDaysAfterEndOfMonth(), form.getDaysBeforeEndOfMonth(), arrayJsonUsers);
        ehCacheManager.clearAll();
        return AjaxResponse.redirect(request.getContextPath() + "/employers");
    }

    @RequestMapping(value = "/employers/activate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object activateEmployer(ModelMap modelMap,
                                   @RequestParam("active") Boolean active,
                                   @RequestParam("employerId") Integer employerId) throws Exception {
        employerDAO.activateEmployerByEntity(Entity.AFFIRM, employerId, active);

        return AjaxResponse.ok("");
    }

    @RequestMapping(value = "/reporting/debtorsConsolidationReport", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:reporting:debtorsConsolidation", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showDebtorsConsolidationReport(ModelMap model, Locale locale) throws Exception {
        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_CONSOLIDADO_DEUDORES, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return "reportDebtorConsolidation";
    }

    @RequestMapping(value = "/reporting/debtorsConsolidationReport/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:debtorsConsolidation", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showDebtorsConsolidationReportList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(Report.REPORTE_CONSOLIDADO_DEUDORES, 0, 10);
        model.addAttribute("historicReports", historicReports);
        return new ModelAndView("reportDebtorConsolidation :: results");
    }

    @RequestMapping(value = "/reporting/debtorsConsolidationReport/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:reporting:debtorsConsolidation", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showDebtorsConsolidationReport(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        reportsService.createReportDebtorConsolidation(backofficeService.getLoggedSysuser().getId());

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/system/transactionalmatch", method = RequestMethod.GET)
     @RequiresPermissionOr403(permissions = "menu:system:transactionalMatch", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showTransactionalMatches(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("canBotBeRun", botDao.isBotAbleToRun(Bot.RE_EVALUATION_EMAIL_SENDER));
        model.addAttribute("templates", catalogService.getAllEmailTemplates());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        String currentYearAndMonth = dateFormat.format(new Date());
        List<QueryBot> queryBots = botDao.getScheduledQueryBots(Bot.RE_EVALUATION_EMAIL_SENDER, currentYearAndMonth);

        model.addAttribute("queryBots", queryBots);
        return "transactionalmatch";
    }

    @RequestMapping(value = "/system/transactionalmatch/querybots", method = RequestMethod.POST)
     @RequiresPermissionOr403(permissions = "transactionalMatch:scheduled:read", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> showQueryBotsByMonthAndYear(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, @RequestParam("yearAndMonth") String yearAndMonth) throws Exception {
        List<QueryBot> queryBots = botDao.getScheduledQueryBots(Bot.RE_EVALUATION_EMAIL_SENDER, yearAndMonth);
        return AjaxResponse.ok(new Gson().toJson(queryBots));
    }


    @RequestMapping(value = "/system/transactionalmatch", method = RequestMethod.POST, produces = "application/json")
     @RequiresPermissionOr403(permissions = "transactionalMatch:onDemand:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity submitTransactionalMatches(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "templateId", required = true) Integer templateId, @RequestParam(value = "canBotBeRun", required = true) Boolean canBotBeRun, @RequestParam(value = "interactionContentId", required = true) Integer interactionContentId) throws Exception {
        if (canBotBeRun != null && canBotBeRun == true) {
            JSONObject json = new JSONObject();
            json.put("interactionContentId", interactionContentId);
            webscrapperService.callBulkMailing(null, json);
            return new ResponseEntity(HttpStatus.CREATED);

        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/system/transactionalmatch/remove", method = RequestMethod.POST, produces = "application/json")
     @RequiresPermissionOr403(permissions = "transactionalMatch:scheduled:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity removeScheduledQueryBot(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "queryBotId", required = true) Integer queryBotId) throws Exception {
        QueryBot queryBot = botDao.getQueryBot(queryBotId);
        if (queryBot != null) {
            queryBot.setStatusId(QueryBot.STATUS_CANCELLED);
            botDao.updateQuery(queryBot);

        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/system/transactionalmatch/add", method = RequestMethod.POST, produces = "application/json")
     @RequiresPermissionOr403(permissions = "transactionalMatch:scheduled:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity addScheduledQueryBot(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "date", required = true) String date, @RequestParam(value = "interactionContentId", required = true) Integer interactionContentId) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date scheduledDate = formatter.parse(date);
        JSONObject json = new JSONObject();
        json.put("interactionContentId", interactionContentId);
        botDao.registerQuery(Bot.RE_EVALUATION_EMAIL_SENDER, QueryBot.STATUS_SCHEDULED, new Date(), json, null, scheduledDate);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/system/accesoCollections", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:accesoCollectionsReport", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showAccesoCollectionsReport(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ReportInteractionGateway> interactionReport = new ArrayList<>();

        List<QueryBot> queryBotList = botDao.getQueryBotsByBotId(Bot.SEND_ACCESO_EXPIRATIO_INTERACTIONS, 5, 0);
        queryBotList.forEach(q -> {
            JSONObject params = q.getParameters();
            ReportInteractionGateway report = new ReportInteractionGateway();
            report.setSentDate(q.getRegisterDate());
            report.setSentWhatsapp(JsonUtil.getBooleanFromJson(params, "sendWhatsapp", false));
            report.setSentEmail(JsonUtil.getBooleanFromJson(params, "sendEmail", false));
            report.setUserId(q.getUserId());
            report.setStatusId(q.getStatusId());
            report.setTotalMoratoriumFee(JsonUtil.getIntFromJson(params, "totalMora", 0));
            report.setTotalExpirationFee(JsonUtil.getIntFromJson(params, "totalVencimiento", 0));
            report.setQueryBotId(q.getId());

            interactionReport.add(report);
        });

        model.addAttribute("interactionsHistory", interactionReport);
        return "accesoCollectionsReportUploader";
    }

    @RequestMapping(value = "/system/accesoCollections/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:accesoCollectionsReport", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showAccesoCollectionsReportList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ReportInteractionGateway> interactionReport = new ArrayList<>();

        List<QueryBot> queryBotList = botDao.getQueryBotsByBotId(Bot.SEND_ACCESO_EXPIRATIO_INTERACTIONS, 5, 0);
        queryBotList.forEach(q -> {
            JSONObject params = q.getParameters();
            ReportInteractionGateway report = new ReportInteractionGateway();
            report.setSentDate(q.getRegisterDate());
            report.setSentWhatsapp(JsonUtil.getBooleanFromJson(params, "sendWhatsapp", false));
            report.setSentEmail(JsonUtil.getBooleanFromJson(params, "sendEmail", false));
            report.setUserId(q.getUserId());
            report.setStatusId(q.getStatusId());
            report.setTotalMoratoriumFee(JsonUtil.getIntFromJson(params, "totalMora", 0));
            report.setTotalExpirationFee(JsonUtil.getIntFromJson(params, "totalVencimiento", 0));
            report.setQueryBotId(q.getId());

            interactionReport.add(report);
        });

        model.addAttribute("interactionsHistory", interactionReport);
        return new ModelAndView("accesoCollectionsReportUploader :: results");
    }

//    TODO GENERICO
    @RequestMapping(value = "/system/accesoCollections/responses", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:accesoCollectionsReport", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showAccesoCollectionsReportResponses(@RequestParam("queryBotId") Integer queryBotId, ModelMap model, Locale locale) throws Exception {
        List<PersonInteraction> interactionLists = interactionDAO.getPersonInteractionByQueryBotId(queryBotId, locale).stream().filter(p -> p.getInteractionType().getId() == InteractionType.CHAT).collect(Collectors.toList());

        model.addAttribute("interactions", interactionLists);
        return new ModelAndView("accesoCollectionsReportUploader :: responses");
    }

    @RequestMapping(value = "/system/accesoCollections", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "acceso:collections:uploadReport", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> uploadAccesoCollectionsReport(
            HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file, @RequestParam("deliver[]") int[] deliver
    ) throws Exception {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet datatypeSheet = workbook.getSheetAt(0);
            accesoGatewayUploadService.lookupForNotCollectedSoonToExpireInstallments(datatypeSheet, deliver, backofficeService.getLoggedSysuser().getId());

            return AjaxResponse.ok(null);
        }catch (FileNotFoundException e) {
            logger.debug("FileNotFoundException", e);
            return AjaxResponse.errorMessage("No se envió ningún archivo");
        } catch (IOException e) {
            logger.debug("IOException", e);
            return AjaxResponse.errorMessage(e.getMessage() != null ? e.getMessage() : "Error al procesar el archivo");
        } catch (Exception e) {
            logger.debug("Exception", e);
            return AjaxResponse.errorMessage("Ocurrio un error");
        }
    }

    @RequestMapping(value = "/system/interactionsHistory/responses", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:interactionsHistory", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showManagementIntmeractionsResponses(@RequestParam("queryBotId") Integer queryBotId, ModelMap model, Locale locale) throws Exception {
        List<PersonInteraction> interactionLists = interactionDAO.getPersonInteractionByQueryBotId(queryBotId, locale).stream().filter(p -> p.getInteractionType().getId() == InteractionType.CHAT).collect(Collectors.toList());

        model.addAttribute("interactions", interactionLists);
        return new ModelAndView("managementInteractionsHistory :: responses");
    }

    @RequestMapping(value = "/system/interactionsHistory", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:interactionsHistory", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showManagementInteractionsHistory(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ReportInteractionManagement> interactionReport = new ArrayList<>();

        List<QueryBot> queryBotList = botDao.getQueryBotsByBotId(Bot.BO_MANAGEMENT_FOLLOWUP_INTERACTION, 5, 0);
        queryBotList.forEach(q -> {
            JSONObject params = q.getParameters();
            JSONArray totalInteractions = JsonUtil.getJsonArrayFromJson(params, "loanApplicationIds", null);

            ReportInteractionManagement report = new ReportInteractionManagement();
            report.setSentDate(q.getRegisterDate());
            report.setInteractionId(JsonUtil.getIntFromJson(params, "interactionId", null));
            report.setUserId(q.getUserId());
            report.setStatusId(q.getStatusId());
            report.setTotalInteractions(totalInteractions == null ? 0 : totalInteractions.length());
            report.setAppliedFilters(JsonUtil.getStringFromJson(params, "filters", null));
            report.setQueryBotId(q.getId());

            interactionReport.add(report);
        });

        model.addAttribute("interactionsHistory", interactionReport);
        model.addAttribute("country", backofficeService.getLoggedSysuser().getActiveCountries().get(0));
        return new ModelAndView("managementInteractionsHistory");
    }

    @RequestMapping(value = "/system/interactionsHistory/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:interactionsHistory", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showManagementInteractionsHistoryList(ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ReportInteractionManagement> interactionReport = new ArrayList<>();

        List<QueryBot> queryBotList = botDao.getQueryBotsByBotId(Bot.BO_MANAGEMENT_FOLLOWUP_INTERACTION, 5, 0);
        queryBotList.forEach(q -> {
            JSONObject params = q.getParameters();
            JSONArray totalInteractions = JsonUtil.getJsonArrayFromJson(params, "loanApplicationIds", null);

            ReportInteractionManagement report = new ReportInteractionManagement();
            report.setSentDate(q.getRegisterDate());
            report.setInteractionId(JsonUtil.getIntFromJson(params, "interactionId", null));
            report.setUserId(q.getUserId());
            report.setStatusId(q.getStatusId());
            report.setTotalInteractions(totalInteractions == null ? 0 : totalInteractions.length());
            report.setAppliedFilters(JsonUtil.getStringFromJson(params, "filters", null));
            report.setQueryBotId(q.getId());

            interactionReport.add(report);
        });

        model.addAttribute("interactionsHistory", interactionReport);
        model.addAttribute("country", backofficeService.getLoggedSysuser().getActiveCountries().get(0));
        return new ModelAndView("managementInteractionsHistory :: results");
    }

    public static class EmployerForm extends FormGeneric implements Serializable {
        public Integer id;
        private String name;
        private String ruc;
        private String address;
        private String phone;
        private Integer profession;
        private Integer user1Id;
        private String user1Name;
        private String user1FirstSurname;
        private String user1LastSurname;
        private String user1Email;
        private Integer user2Id;
        private String user2Name;
        private String user2FirstSurname;
        private String user2LastSurname;
        private String user2Email;
        private Integer user3Id;
        private String user3Name;
        private String user3FirstSurname;
        private String user3LastSurname;
        private String user3Email;
        private Integer daysAfterEndOfMonth;
        private Integer daysBeforeEndOfMonth;
        private Boolean active;

        public EmployerForm() {
            this.setValidator(new EmployerForm.Validator());
        }

        public Integer getUser1Id() {
            return user1Id;
        }

        public void setUser1Id(Integer user1Id) {
            this.user1Id = user1Id;
        }

        public Integer getUser2Id() {
            return user2Id;
        }

        public void setUser2Id(Integer user2Id) {
            this.user2Id = user2Id;
        }

        public Integer getUser3Id() {
            return user3Id;
        }

        public void setUser3Id(Integer user3Id) {
            this.user3Id = user3Id;
        }

        public class Validator extends FormValidator implements Serializable {
            //            public IntegerFieldValidator id;
            public StringFieldValidator name;
            public StringFieldValidator ruc;
            public StringFieldValidator address;
            public StringFieldValidator phone;
            public IntegerFieldValidator profession;
            public StringFieldValidator user1Name;
            public StringFieldValidator user1FirstSurname;
            public StringFieldValidator user1LastSurname;
            public StringFieldValidator user1Email;
            public StringFieldValidator user2Name;
            public StringFieldValidator user2FirstSurname;
            public StringFieldValidator user2LastSurname;
            public StringFieldValidator user2Email;
            public StringFieldValidator user3Name;
            public StringFieldValidator user3FirstSurname;
            public StringFieldValidator user3LastSurname;
            public StringFieldValidator user3Email;
            public IntegerFieldValidator daysAfterEndOfMonth;
            public IntegerFieldValidator daysBeforeEndOfMonth;

            public Validator() {
                addValidator(name = new StringFieldValidator().setRequired(true).setFieldName("Nombre"));
                addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC).setRequired(true).setFieldName("RUC"));
                addValidator(address = new StringFieldValidator().setRequired(true).setFieldName("Direccion"));
                addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(true).setFieldName("Telefono"));
                addValidator(profession = new IntegerFieldValidator().setRequired(true).setFieldName("Rubro"));
                // User 1
                addValidator(user1Name = new StringFieldValidator().setRequired(true).setFieldName("Nombre del usuario 1"));
                addValidator(user1FirstSurname = new StringFieldValidator().setRequired(true).setFieldName("Apellido paterno del usuario 1"));
                addValidator(user1LastSurname = new StringFieldValidator().setRequired(true).setFieldName("Apellido materno del usuario 1"));
                addValidator(user1Email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true).setFieldName("Email del usuario 1"));
                // User 2
                addValidator(user2Name = new StringFieldValidator().setRequired(false).setFieldName("Nombre del usuario 2"));
                addValidator(user2FirstSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido paterno del usuario 2"));
                addValidator(user2LastSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido materno del usuario 2"));
                addValidator(user2Email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false).setFieldName("Email del usuario 2"));
                // User 3
                addValidator(user3Name = new StringFieldValidator().setRequired(false).setFieldName("Nombre del usuario 3"));
                addValidator(user3FirstSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido paterno del usuario 3"));
                addValidator(user3LastSurname = new StringFieldValidator().setRequired(false).setFieldName("Apellido materno del usuario 3"));
                addValidator(user3Email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false).setFieldName("Email del usuario 3"));

                addValidator(daysAfterEndOfMonth = new IntegerFieldValidator().setRequired(true).setFieldName("Inicio de adelanto"));
                addValidator(daysBeforeEndOfMonth = new IntegerFieldValidator().setRequired(true).setFieldName("Fin de adelanto"));
            }

            @Override
            protected void setDynamicValidations() {
            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return EmployerForm.this;
            }
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRuc() {
            return ruc;
        }

        public void setRuc(String ruc) {
            this.ruc = ruc;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getProfession() {
            return profession;
        }

        public void setProfession(Integer profession) {
            this.profession = profession;
        }

        public String getUser1Name() {
            return user1Name;
        }

        public void setUser1Name(String user1Name) {
            this.user1Name = user1Name;
        }

        public String getUser1FirstSurname() {
            return user1FirstSurname;
        }

        public void setUser1FirstSurname(String user1FirstSurname) {
            this.user1FirstSurname = user1FirstSurname;
        }

        public String getUser1LastSurname() {
            return user1LastSurname;
        }

        public void setUser1LastSurname(String user1LastSurname) {
            this.user1LastSurname = user1LastSurname;
        }

        public String getUser1Email() {
            return user1Email;
        }

        public void setUser1Email(String user1Email) {
            this.user1Email = user1Email;
        }

        public String getUser2Name() {
            return user2Name;
        }

        public void setUser2Name(String user2Name) {
            this.user2Name = user2Name;
        }

        public String getUser2FirstSurname() {
            return user2FirstSurname;
        }

        public void setUser2FirstSurname(String user2FirstSurname) {
            this.user2FirstSurname = user2FirstSurname;
        }

        public String getUser2LastSurname() {
            return user2LastSurname;
        }

        public void setUser2LastSurname(String user2LastSurname) {
            this.user2LastSurname = user2LastSurname;
        }

        public String getUser2Email() {
            return user2Email;
        }

        public void setUser2Email(String user2Email) {
            this.user2Email = user2Email;
        }

        public String getUser3Name() {
            return user3Name;
        }

        public void setUser3Name(String user3Name) {
            this.user3Name = user3Name;
        }

        public String getUser3FirstSurname() {
            return user3FirstSurname;
        }

        public void setUser3FirstSurname(String user3FirstSurname) {
            this.user3FirstSurname = user3FirstSurname;
        }

        public String getUser3LastSurname() {
            return user3LastSurname;
        }

        public void setUser3LastSurname(String user3LastSurname) {
            this.user3LastSurname = user3LastSurname;
        }

        public String getUser3Email() {
            return user3Email;
        }

        public void setUser3Email(String user3Email) {
            this.user3Email = user3Email;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Integer getDaysAfterEndOfMonth() {
            return daysAfterEndOfMonth;
        }

        public void setDaysAfterEndOfMonth(Integer daysAfterEndOfMonth) {
            this.daysAfterEndOfMonth = daysAfterEndOfMonth;
        }

        public Integer getDaysBeforeEndOfMonth() {
            return daysBeforeEndOfMonth;
        }

        public void setDaysBeforeEndOfMonth(Integer daysBeforeEndOfMonth) {
            this.daysBeforeEndOfMonth = daysBeforeEndOfMonth;
        }
    }
}
