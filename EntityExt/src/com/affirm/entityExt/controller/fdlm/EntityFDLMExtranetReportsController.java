package com.affirm.entityExt.controller.fdlm;

import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.ReportsDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Report;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.ReportsService;
import com.affirm.common.util.AjaxResponse;
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
@RequestMapping("/fdlm")
public class EntityFDLMExtranetReportsController {

    private final ReportsService reportsService;
    private final EntityExtranetService entityExtranetService;
    private final ReportsDAO reportsDao;

    public EntityFDLMExtranetReportsController(ReportsService reportsService, EntityExtranetService entityExtranetService, ReportsDAO reportsDao) {
        this.reportsService = reportsService;
        this.entityExtranetService = entityExtranetService;
        this.reportsDao = reportsDao;
    }

    @RequestMapping(value = "/reports/loanInProcess/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:solicitudesEnProceso:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object downloadLoanInProcessReport(@RequestParam(value = "startDate[]", required = false) String startDate,
                                              @RequestParam(value = "endDate[]", required = false) String endDate,
                                              @RequestParam(value = "lastname[]", required = false) String lastname,
                                              @RequestParam(value = "updatedStartDate[]", required = false) String updatedStartDate,
                                              @RequestParam(value = "updatedEndDate[]", required = false) String updatedEndDate,
                                              @RequestParam(value = "documentNumber[]", required = false) String documentNumber,
                                              @RequestParam(value = "loanStatuses[]", required = false) Integer[] loanStatuses
    ) throws Exception {
        Pair<Date, Date> fromAndTo = convertStringDateToDate(startDate, endDate);
        Pair<Date, Date> updatedFromAndupdatedTo = convertStringDateToDate(updatedStartDate, updatedEndDate);
        String countryId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId().toString();
        documentNumber = "".equals(documentNumber) ? null : documentNumber;
        lastname = "".equals(lastname) ? null : lastname;
        Integer documentType = IdentityDocumentType.COL_CEDULA_CIUDADANIA;// TODO DEFAULT

        reportsService.createReportSolicitudesEnProcesoFDLM(entityExtranetService.getLoggedUserEntity().getId(), countryId, documentType, documentNumber, lastname, fromAndTo.getLeft(), fromAndTo.getRight(), updatedFromAndupdatedTo.getLeft(), updatedFromAndupdatedTo.getRight(), loanStatuses);

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

        List<ReportProces> loanInProcessHistoricReports = reportsDao.getReportProcesHistoric(userId, Report.REPORTE_SOLICITUDES_EN_PROCESO_EXT_FDLM, 0, 5);

        model.addAttribute("loanInProcessHistoricReports", loanInProcessHistoricReports);

        return new ModelAndView("/entityExtranet/extranetReports :: loanInProcessResultsFDLM");
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
