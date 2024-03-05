package com.affirm.companyExt.controller;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.EmployeeCompanyExtranetPainter;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.client.service.ExtranetCompanyService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.CreditSubStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.util.AjaxResponse;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

@Controller
public class CompanyExtranetPendingAuthorizationController {

    @Autowired
    private EmployerCLDAO employerClDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private ExtranetCompanyService extranetCompanyService;

    @RequestMapping(value = "/pendingAuthorization", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object extranetCompanyPendingAuth(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<EmployeeCompanyExtranetPainter> employees =  extranetCompanyService.getPendingAuthorizationEmployees(locale);
        model.addAttribute("employees", employees);
        model.addAttribute("showAeluRegisterPreliminaryDocReceptionScript", employees.stream().anyMatch(c -> c.isShowAeluRecivePreliminaryDocButton()));
        model.addAttribute("showAeluRegisterPromisoryNoteReceptionScript", employees.stream().anyMatch(c -> c.isShowAeluRecivePromisoryNoteButton()));
        return "/companyExtranet/extranetPendingAuthorization";
    }

    @RequestMapping(value = "/pendingAuthorization/preliminaryDocumentation", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerPreliminaryDocumentationRepection(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("employeeId") Integer employeeId) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        Employee employee = personDao.getEmployeeById(employeeId, locale);
        if(!employee.getEmployer().getId().equals(user.getActiveCompany().getId())){
            return AjaxResponse.errorMessage("Empleado inválido");
        }

        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, employee.getPersonId(), ProductCategory.CONSUMO);
        if(loanApplication == null || loanApplication.getCreditId() == null)
            return AjaxResponse.errorMessage("El emplado no tiene una solicitud pendiente de documentación");

        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);
        if(credit == null || credit.getSubStatus() == null || credit.getSubStatus().getId() != CreditSubStatus.AELU_PENDING_PRELIMINARY_DOCUMENTATION)
            return AjaxResponse.errorMessage("El emplado no tiene una solicitud pendiente de documentación");

        creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.AELU_PENDING_INTERNAL_DISBURSEMENT);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/pendingAuthorization/promisoryNote", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerPromisoryNoteRepection(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("employeeId") Integer employeeId) throws Exception {

        LoggedUserEmployer user = (LoggedUserEmployer) SecurityUtils.getSubject().getPrincipal();

        Employee employee = personDao.getEmployeeById(employeeId, locale);
        if(!employee.getEmployer().getId().equals(user.getActiveCompany().getId())){
            return AjaxResponse.errorMessage("Empleado inválido");
        }

        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(locale, employee.getPersonId(), ProductCategory.CONSUMO);
        if(loanApplication == null || loanApplication.getCreditId() == null)
            return AjaxResponse.errorMessage("El emplado no tiene una solicitud pendiente de pagaré");

        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);
        if(credit == null || credit.getSubStatus() == null || credit.getSubStatus().getId() != CreditSubStatus.AELU_PENDING_PROMISORY_NOTE)
            return AjaxResponse.errorMessage("El emplado no tiene una solicitud pendiente de pagaré");

        creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
        return AjaxResponse.ok(null);
    }

}
