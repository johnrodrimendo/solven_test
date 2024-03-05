package com.affirm.client.service.impl;

import com.affirm.client.service.LoanApplicationClService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service("loanApplicationClService")
public class LoanApplicationClServiceImpl implements LoanApplicationClService {

    private static Logger logger = Logger.getLogger(LoanApplicationClServiceImpl.class);

    @Override
    public void loginLoanApplicationProcess(String loanAplicationToken) {
        SecurityUtils.getSubject().getSession().setAttribute("loanApplicationToken", loanAplicationToken);
    }

}
