package com.affirm.common.service;

import com.affirm.common.model.transactional.Employee;

import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface EmployeeService {

    List<Employee> getEmployeesByEmailOrDocumentByProduct(String email, Integer docType, String docNumber, int productId, Locale locale) throws Exception;

    List<Employee> getEmployeesByEmailOrDocumentByEntity(String email, Integer docType, String docNumber, int entityId, Locale locale) throws Exception;

    List<Employee> getEmployeesByEmailOrDocumentByEntityProduct(String email, Integer docType, String docNumber, int entityId, int productId, Locale locale) throws Exception;
}
