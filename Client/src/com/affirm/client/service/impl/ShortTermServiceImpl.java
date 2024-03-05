package com.affirm.client.service.impl;

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.client.service.ShortTermService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.InteractionService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jrodriguez
 */

@Service("shortTermService")
public class ShortTermServiceImpl implements ShortTermService {

    private static final Logger logger = Logger.getLogger(ShortTermServiceImpl.class);

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

    @Override
    public LoanApplication registerLoanApplication(HttpServletRequest request, int userId, int ammount, int installments, char origin, Integer cluster, Integer registerType) throws Exception {

        // Register the Loan Application
        LoanApplication loanApplication = loanApplicationDao.registerLoanApplication(
                userId, ammount, installments, null, Product.SHORT_TERM, null, cluster, origin, null, registerType, null,
                CountryParam.COUNTRY_PERU);

        // Register the ip location
        userService.registerIpUbication(Util.getClientIpAddres(request), loanApplication.getId());

        return loanApplication;
    }

}