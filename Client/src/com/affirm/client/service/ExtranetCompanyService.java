package com.affirm.client.service;

import com.affirm.client.model.EmployeeCompanyExtranetPainter;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.common.model.transactional.Employee;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface ExtranetCompanyService {
    void login(AuthenticationToken token, HttpServletRequest request) throws Exception;

    void onLogout(int sessionId, Date logoutDate) throws Exception;

    LoggedUserEmployer getLoggedUserEmployer() ;

    Boolean validateAgreementProduct(int entityId) throws Exception;

    Boolean validateProduct(int employerId, int productId) throws Exception;

    boolean isCustomMaxAmountActivated() throws Exception;

    void createEmployeesExcel(List<Employee> employees, OutputStream outputStream) throws Exception;

    void createImportEmployeesExcelTemplate(OutputStream outputStream) throws Exception;

    Integer getLoggedUserActiveEntity() throws Exception;

    List<EmployeeCompanyExtranetPainter> getPendingAuthorizationEmployees(Locale locale) throws Exception;
}
