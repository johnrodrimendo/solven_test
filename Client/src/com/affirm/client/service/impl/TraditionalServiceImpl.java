package com.affirm.client.service.impl;

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.service.TraditionalService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.*;
import com.affirm.common.util.Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jrodriguez
 */

@Service("traditionalService")
public class TraditionalServiceImpl implements TraditionalService {

    private static final Logger logger = Logger.getLogger(TraditionalServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private UserService userService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private PersonCLDAO personClDao;
    @Autowired
    private CountryContextService countryContextService;

    @Override
    public LoanApplication registerLoanApplication(HttpServletRequest request, int userId, int ammount, int installments, Integer loanReason, char origin, Integer cluster, Integer registerType) throws Exception {

        // Register the Loan Application
        LoanApplication loanApplication = loanApplicationDao.registerLoanApplication(
                userId, ammount, installments, loanReason, Product.TRADITIONAL, null, cluster, origin, null, registerType, null,
                countryContextService.getCountryParamsByRequest(request).getId());

        // Register the ip location
        userService.registerIpUbication(Util.getClientIpAddres(request), loanApplication.getId());

        return loanApplication;
    }

}