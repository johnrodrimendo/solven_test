package com.affirm.entityExt.controller.bancodelsol;

import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.ReportsDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Report;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.ReportsService;
import com.affirm.common.util.AjaxResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/bds")
public class EntityExtranetReportsController {

    private final ReportsService reportsService;
    private final EntityExtranetService entityExtranetService;

    @Autowired
    private ReportsDAO reportsDao;

    public EntityExtranetReportsController(ReportsService reportsService, EntityExtranetService entityExtranetService) {
        this.reportsService = reportsService;
        this.entityExtranetService = entityExtranetService;
    }

    @RequestMapping(value = "/reports/loanInProcess/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:solicitudesEnProceso:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object downloadLoanInProcessReport(@RequestParam(value = "startDate[]", required = false) String startDate,
                                              @RequestParam(value = "endDate[]", required = false) String endDate,
                                              @RequestParam(value = "documentNumber[]", required = false) String documentNumber,
                                              @RequestParam(value = "organizers[]", required = false) Integer[] organizers,
                                              @RequestParam(value = "producers[]", required = false) Integer[] producers,
                                              @RequestParam(value = "loanStatuses[]", required = false) Integer[] loanStatuses
    ) throws Exception {
        Pair<Date, Date> fromAndTo = convertStringDateToDate(startDate, endDate);
        String countryId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId().toString();

        documentNumber = returnNullIfEmpty(documentNumber);

        Integer documentType = getCdiTypeIfDocumentNumberNotNull(documentNumber);
        Integer[] fused = ArrayUtils.addAll(producers, organizers);

        reportsService.createReportSolicitudesEnProceso(entityExtranetService.getLoggedUserEntity().getId(), countryId, documentType, documentNumber, fused, fromAndTo.getLeft(), fromAndTo.getRight(), loanStatuses);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reports/disburseCredit/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:creditosADesembolsar:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object downloadDisburseCreditReport(@RequestParam(value = "startDate[]", required = false) String startDate,
                                               @RequestParam(value = "endDate[]", required = false) String endDate,
                                               @RequestParam(value = "documentNumber[]", required = false) String documentNumber,
                                               @RequestParam(value = "organizers[]", required = false) Integer[] organizers,
                                               @RequestParam(value = "producers[]", required = false) Integer[] producers
    ) throws Exception {
        Pair<Date, Date> fromAndTo = convertStringDateToDate(startDate, endDate);
        String countryId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId().toString();

        documentNumber = returnNullIfEmpty(documentNumber);

        Integer documentType = getCdiTypeIfDocumentNumberNotNull(documentNumber);
        Integer[] fused = ArrayUtils.addAll(producers, organizers);

        reportsService.createReportCreditosADesembolsar(entityExtranetService.getLoggedUserEntity().getId(), countryId, documentType, documentNumber, fused, fromAndTo.getLeft(), fromAndTo.getRight());

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reports/disbursedCredit/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:creditosDesembolsados:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object downloadDisbursedCreditReport(@RequestParam(value = "startDate[]", required = false) String startDate,
                                                @RequestParam(value = "endDate[]", required = false) String endDate,
                                                @RequestParam(value = "documentNumber[]", required = false) String documentNumber,
                                                @RequestParam(value = "organizers[]", required = false) Integer[] organizers,
                                                @RequestParam(value = "producers[]", required = false) Integer[] producers,
                                                @RequestParam(value = "internalStatuses[]", required = false) String[] internalStatuses,
                                                @RequestParam(value = "disbursementStartDate[]", required = false) String disbursementStartDate,
                                                @RequestParam(value = "disbursementEndDate[]", required = false) String disbursementEndDate
    ) throws Exception {
        Pair<Date, Date> fromAndTo = convertStringDateToDate(startDate, endDate);
        Pair<Date, Date> disbursementFromAndTo = convertStringDateToDate(disbursementStartDate, disbursementEndDate);
        String countryId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId().toString();

        documentNumber = returnNullIfEmpty(documentNumber);

        Integer documentType = getCdiTypeIfDocumentNumberNotNull(documentNumber);
        Integer[] fused = ArrayUtils.addAll(producers, organizers);

        reportsService.createReportCreditosDesembolsados(entityExtranetService.getLoggedUserEntity().getId(), countryId, documentType, documentNumber, fused,
                fromAndTo.getLeft(), fromAndTo.getRight(), internalStatuses, disbursementFromAndTo.getLeft(), disbursementFromAndTo.getRight());

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reports/risk/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:riesgosBds:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object downloadRiskReport(@RequestParam(value = "startDate[]", required = false) String startDate,
                                     @RequestParam(value = "endDate[]", required = false) String endDate,
                                     @RequestParam(value = "documentNumber[]", required = false) String documentNumber,
                                     @RequestParam(value = "organizers[]", required = false) Integer[] organizers,
                                     @RequestParam(value = "producers[]", required = false) Integer[] producers,
                                     @RequestParam(value = "loanStatuses[]", required = false) Integer[] loanStatuses,
                                     @RequestParam(value = "internalStatuses[]", required = false) String[] internalStatuses,
                                     @RequestParam(value = "creditStatuses[]", required = false) Integer[] creditStatuses,
                                     @RequestParam(value = "lastExecutionStartDate[]", required = false) String lastExecutionStartDate,
                                     @RequestParam(value = "lastExecutionEndDate[]", required = false) String lastExecutionEndDate
    ) throws Exception {
        Pair<Date, Date> fromAndTo = convertStringDateToDate(startDate, endDate);
        Pair<Date, Date> lastExecutionFromAndTo = convertStringDateToDate(lastExecutionStartDate, lastExecutionEndDate);
        String countryId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId().toString();

        documentNumber = returnNullIfEmpty(documentNumber);

        Integer documentType = getCdiTypeIfDocumentNumberNotNull(documentNumber);
        Integer[] fused = ArrayUtils.addAll(producers, organizers);

        reportsService.createReportRiesgos(entityExtranetService.getLoggedUserEntity().getId(), countryId, documentType,
                documentNumber, fused, fromAndTo.getLeft(), fromAndTo.getRight(), loanStatuses, internalStatuses,
                creditStatuses, lastExecutionFromAndTo.getLeft(), lastExecutionFromAndTo.getRight());

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/reports/loan/inProcessList", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:solicitudesEnProceso:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoanInProcessReportList(ModelMap model) throws Exception {

        if (entityExtranetService.getLoggedUserEntity() == null)
            return AjaxResponse.ok(null);

        int userId = entityExtranetService.getLoggedUserEntity().getId();

        List<ReportProces> loanInProcessHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_BDS, 0, 5);

        model.addAttribute("loanInProcessHistoricReports", loanInProcessHistoricReports);
        return new ModelAndView("/entityExtranet/extranetReports :: loanInProcessResults");
    }

    @RequestMapping(value = "/reports/loan/toBeDisbursedList", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:creditosADesembolsar:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoanToBeDisbursedReportList(ModelMap model) throws Exception {

        if (entityExtranetService.getLoggedUserEntity() == null)
            return AjaxResponse.ok(null);

        int userId = entityExtranetService.getLoggedUserEntity().getId();

        List<ReportProces> loanToBeDisbursedHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_CREDITOS_A_DESEMBOLSAR_EXT_BDS, 0, 5);

        model.addAttribute("loanToBeDisbursedHistoricReports", loanToBeDisbursedHistoricReports);
        return new ModelAndView("/entityExtranet/extranetReports :: loanToBeDisbursedResults");
    }

    @RequestMapping(value = "/reports/loan/disbursedList", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:creditosDesembolsados:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showDisbursedLoanReportList(ModelMap model) throws Exception {

        if (entityExtranetService.getLoggedUserEntity() == null)
            return AjaxResponse.ok(null);

        int userId = entityExtranetService.getLoggedUserEntity().getId();

        List<ReportProces> disbursedLoanHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_CREDITOS_DESEMBOLSADOS_EXT_BDS, 0, 5);

        model.addAttribute("disbursedLoanHistoricReports", disbursedLoanHistoricReports);
        return new ModelAndView("/entityExtranet/extranetReports :: disbursedLoanResults");
    }

    @RequestMapping(value = "/reports/loan/riskList", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:riesgosBds:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showRiskReportList(ModelMap model) throws Exception {

        if (entityExtranetService.getLoggedUserEntity() == null)
            return AjaxResponse.ok(null);

        int userId = entityExtranetService.getLoggedUserEntity().getId();

        List<ReportProces> loanRisksHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_RIESGOS_EXT_BDS, 0, 5);

        model.addAttribute("loanRisksHistoricReports", loanRisksHistoricReports);
        return new ModelAndView("/entityExtranet/extranetReports :: loanRisksResults");
    }

    private Integer getCdiTypeIfDocumentNumberNotNull(String documentNumber) {
        if (documentNumber != null)
            return IdentityDocumentType.CDI;
        return null;
    }

    private String returnNullIfEmpty(String value) {
        if (value.isEmpty())
            return null;
        return value;
    }

    private Pair<Date, Date> convertStringDateToDate(String stringFrom, String stringTo) throws ParseException {
        Date from;
        Date to;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        if (stringFrom != null && !stringFrom.isEmpty() && stringTo != null && !stringTo.isEmpty()) {
            from = formatter.parse(stringFrom);
            to = formatter.parse(stringTo);

            return Pair.of(from, to);
        }
        return Pair.of(null, null);
    }
}
