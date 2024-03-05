package com.affirm.client.model.interceptor;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.Agent;
import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.RequestScoped;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Random;

/**
 * Created by john on 18/11/16.
 */
@Component
public class LoanApplicationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private Provider<RequestScoped> requestScoped;
    @Autowired
    private CreditDAO creditDAO;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = doPreHandle(request, response, handler);
        return result;

    }

    public boolean doPreHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if (request.getPathInfo().split("/").length < 2)
            return true;

        String[] arrStr = request.getPathInfo().split("/");

        String decryptedText = null;

        for (String str : arrStr) {
            if (!(decryptedText == null || decryptedText.equals(""))) {
                break;
            }
            decryptedText = CryptoUtil.decrypt(str);
        }

        String[] queries = {"token", "loanAplicationToken"};

        for (String str : queries) {
            if (!(decryptedText == null || decryptedText.equals(""))) {
                break;
            }
            decryptedText = CryptoUtil.decrypt(request.getParameter(str));
        }

        if (!(decryptedText == null || decryptedText.equals(""))) {
            JSONObject jsonDecrypted = new JSONObject(decryptedText);

            LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(jsonDecrypted.getInt("loan"), Configuration.getDefaultLocale());
            Integer loanApplicationStatus = null;
            Credit credit = null;
            boolean excludeExpiration = false;

            if (loanApplication != null && loanApplication.getStatus() != null) {
                loanApplicationStatus = loanApplication.getStatus().getId();
                requestScoped.get().setLoanApplicationLite(loanApplication);
                if(loanApplication.getCredit() != null && loanApplication.getCredit()){
                    credit = creditDAO.getCreditByLoanApplicationId(loanApplication.getId(), Configuration.getDefaultLocale(), false, Credit.class);
                    if(credit != null && credit.getStatus().getId().intValue() == CreditStatus.ORIGINATED) excludeExpiration = true;
                }
            }


            // Validate if (only)loan is expired or the expiration date has passed
            if (
                    loanApplication != null && loanApplication.getStatus() != null && !excludeExpiration &&
                            (
                                    (LoanApplicationStatus.EXPIRED == loanApplicationStatus || (loanApplication.getExpirationDate() != null && loanApplication.getExpirationDate().before(new Date())))
                            )
            ) {

                if(request.getPathInfo().contains("evaluacion/question/")){
                    AjaxResponse.sendReloadPage(response);
                    return false;
                }

                ModelAndView model = new ModelAndView("401");
                String status = null;
                String header = null;
                ;
                if (loanApplicationStatus == LoanApplicationStatus.REJECTED_AUTOMATIC || loanApplicationStatus == LoanApplicationStatus.REJECTED) {
                    if(loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD || loanApplication.getProductCategoryId() == ProductCategory.TARJETA_CREDITO){
                        status = "loanapplicationinterceptor.status.expired";
                        header = "loanapplicationinterceptor.status.expired.header";
                    }else{
                        status = "loanapplicationinterceptor.status.rejected";
                        header = "loanapplicationinterceptor.status.rejected.header";
                    }

                } else if (!excludeExpiration && (loanApplicationStatus == LoanApplicationStatus.EXPIRED || (loanApplication.getExpirationDate() != null && loanApplication.getExpirationDate().before(new Date())))) {
                    if(loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD){
                        status = "loanapplicationinterceptor.status.identityValidationExpired";
                    }else{
                        status = "loanapplicationinterceptor.status.expired";
                    }
                    header = "loanapplicationinterceptor.status.expired.header";
                }

                model.addObject("header", messageSource.getMessage(header, null, Configuration.getDefaultLocale()).toLowerCase());
                model.addObject("status", messageSource.getMessage(status, null, Configuration.getDefaultLocale()));
                Agent agent = loanApplication.getAgent();

                if (agent == null) {
                    int size = catalogService.getFormAssistantsAgents(null).size();
                    int index = new Random().nextInt(size);

                    agent = catalogService.getFormAssistantsAgents(null).get(index);
                }

                model.addObject("agent", agent);
                throw new ModelAndViewDefiningException(model);
            }

            // Validate that the country of the selfevaluation is the same of the current domain
            if (!loanApplication.getCountryId().equals(countryContextService.getCountryParamsByRequest(request).getId())) {
                ModelAndView model = new ModelAndView("forward:/404");
                throw new ModelAndViewDefiningException(model);
            }
        }
        return true;

    }

}
