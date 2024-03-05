package com.affirm.client.model.interceptor;

import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.RequestScoped;
import com.affirm.common.service.SelfEvaluationService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by john on 18/11/16.
 */
@Component
public class SelfEvaluationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private SelfEvaluationService selfEvaluationService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private Provider<RequestScoped> requestScoped;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getPathInfo().split("/").length < 2)
            return true;

        String[] arrPathInfo = request.getPathInfo().split("/");
        String possibleToken = null;
        if (arrPathInfo[1].equalsIgnoreCase(Configuration.SELF_EVALUATION_CONTROLLER_URL)) {
            String[] queries = {"token"};
            for (String str : queries) {
                if (possibleToken != null) {
                    break;
                }
                possibleToken = request.getParameter(str) != null && !request.getParameter(str).isEmpty() ? request.getParameter(str) : null;
            }

            if (possibleToken == null && arrPathInfo.length > 2)
                possibleToken = arrPathInfo[2];


            Integer selfEvaluationId = possibleToken != null ? selfEvaluationService.getIdFromToken(possibleToken) : null;

            if (selfEvaluationId != null) {
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(selfEvaluationId, Configuration.getDefaultLocale());
                if (selfEvaluation != null) {
                    requestScoped.get().setSelfEvaluation(selfEvaluation);
                    // Validate that the country of the selfevaluation is the same of the current domain
                    if (!selfEvaluation.getCountryParam().getId().equals(countryContextService.getCountryParamsByRequest(request).getId())) {
                        ModelAndView model = new ModelAndView("forward:/404");
                        throw new ModelAndViewDefiningException(model);
                    }
                }
            }
        }

        return true;
    }

}
