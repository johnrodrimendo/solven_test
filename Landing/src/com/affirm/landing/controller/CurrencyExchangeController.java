package com.affirm.landing.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.form.RextieForm;
import com.affirm.common.util.AjaxResponse;
import com.affirm.rextie.client.RextieClient;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Controller
@Scope("request")
public class CurrencyExchangeController {

    private static final Logger logger = Logger.getLogger(CurrencyExchangeController.class);

    private final RextieClient rextieClient;

    public CurrencyExchangeController(RextieClient rextieClient) {
        this.rextieClient = rextieClient;
    }

    @RequestMapping(value = "/cambios", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getRextieLandingParams(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
//        Rextie rextie = rextieClient.exchangeRateQuote(Rextie.CurrencyType.USD, Rextie.CurrencyType.PEN, 1.00, null);
//        logger.debug(rextie);
//        model.addAttribute("rextieData", rextie);

        RextieForm rextieForm = new RextieForm();
        rextieForm.setSourceCurrencyAmount(1000.00);
        rextieForm.setSourceCurrency("PEN");
        rextieForm.setTargetCurrency("USD");

        model.addAttribute("rextieForm", rextieForm);

        return "page-cambios";
    }

    @RequestMapping(value = "/cambios", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object processRextieAfterSubmission(ModelMap model, Locale locale, HttpServletRequest request,
                                               @RequestParam(value = "sourceCurrency", required = true) Double sourceCurrency,
                                               @RequestParam(value = "targetCurrency", required = true) Double targetCurrency,
                                               @RequestParam(value = "sellRate", required = true) Double sellRate,
                                               @RequestParam(value = "buyRate", required = true) Double buyRate,
                                               @RequestParam(value = "quoteExpiringDate", required = true) String quoteExpiringDate) throws Exception {


        return AjaxResponse.ok(null);
    }
}