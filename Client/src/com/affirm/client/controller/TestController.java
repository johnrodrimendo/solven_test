package com.affirm.client.controller;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.ReportsService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private CreditDAO creditDao;

    @RequestMapping(value = "/solicitudRipley", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public void downloadReport(Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!Configuration.hostEnvIsLocal()){
            return;
        }

        try {
            //767-762
            byte[] pdfAsBytes = loanApplicationService.generateLoanRequestSheet(creditDao.getCreditByID(767, locale, true, Credit.class));

            response.setHeader("Content-disposition", "attachment; filename= test.xls");
            response.getOutputStream().write(pdfAsBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping(value = "/funnelreport", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public void downloadFunnelReport(Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!Configuration.hostEnvIsLocal()){
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            byte[] pdfAsBytes = reportsService.createFunnelReport(sdf.parse("01-08-2018"), sdf.parse("30-08-2018"), 1, "[]", "[]", "", "", "", WebApplication.BACKOFFICE, "[51,54]");

            response.setHeader("Content-disposition", "attachment; filename= test.xls");
            response.getOutputStream().write(pdfAsBytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



}
