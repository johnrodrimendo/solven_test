package com.affirm.common.service.impl;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.EntityProduct;
import com.affirm.common.model.transactional.Employee;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EmployeeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static Logger logger = Logger.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public List<Employee> getEmployeesByEmailOrDocumentByProduct(String email, Integer docType, String docNumber, int productId, Locale locale) throws Exception {

        List<Employee> employees = null;
        if (email != null)
            employees = personDao.getEmployeesByEmail(email, locale);
        else if (docType != null)
            employees = personDao.getEmployeesByDocument(docType, docNumber, locale);

        if (employees == null || employees.isEmpty())
            return new ArrayList<>();

        // Filter only the employees that can have access to the product by an entity
        List<Employee> posiblesEmployees = new ArrayList<>();
        for (Employee employee : employees) {
            List<EntityProduct> entityProducts = catalogService.getEntityProductsByProduct(productId)
                    .stream().filter(e -> e.getEmployer() != null && e.getEmployer().getId().intValue() == employee.getEmployer().getId()).collect(Collectors.toList());
            if (!entityProducts.isEmpty()) {
                posiblesEmployees.add(employee);
            }
        }
        return posiblesEmployees;
    }

    @Override
    public List<Employee> getEmployeesByEmailOrDocumentByEntity(String email, Integer docType, String docNumber, int entityId, Locale locale) throws Exception {

        List<Employee> employees = null;
        if (email != null)
            employees = personDao.getEmployeesByEmail(email, locale);
        else if (docType != null)
            employees = personDao.getEmployeesByDocument(docType, docNumber, locale);

        if (employees == null || employees.isEmpty())
            return new ArrayList<>();

        // Filter only the employees that can have access to the product by an entity
        List<Employee> posiblesEmployees = new ArrayList<>();
        for (Employee employee : employees) {
            List<EntityProduct> entityProducts = catalogService.getEntityProductsByEntity(entityId)
                    .stream().filter(e -> e.getEmployer() != null && e.getEmployer().getId().intValue() == employee.getEmployer().getId()).collect(Collectors.toList());
            if (!entityProducts.isEmpty()) {
                posiblesEmployees.add(employee);
            }
        }
        return posiblesEmployees;
    }

    @Override
    public List<Employee> getEmployeesByEmailOrDocumentByEntityProduct(String email, Integer docType, String docNumber, int entityId, int productId, Locale locale) throws Exception {

        List<Employee> employees = null;
        if (email != null)
            employees = personDao.getEmployeesByEmail(email, locale);
        else if (docType != null)
            employees = personDao.getEmployeesByDocument(docType, docNumber, locale);

        if (employees == null || employees.isEmpty())
            return new ArrayList<>();

        // Filter only the employees that can have access to the product by an entity
        List<Employee> posiblesEmployees = new ArrayList<>();
        for (Employee employee : employees.stream().filter(e -> e.getActive()).collect(Collectors.toList())) {
            List<EntityProduct> entityProducts = catalogService.getEntityProductsByEntityProduct(entityId, productId)
                    .stream().filter(e -> e.getEmployer() != null && e.getEmployer().getId().intValue() == employee.getEmployer().getId()).collect(Collectors.toList());
            if (!entityProducts.isEmpty()) {
                posiblesEmployees.add(employee);
            }
        }
        return posiblesEmployees;
    }

}
