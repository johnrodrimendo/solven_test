package com.affirm.client.controller;

import com.affirm.client.dao.PersonCLDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.transactional.Reniec;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Locale;

@Controller
@Scope("request")
@RequestMapping("")
public class PersonController {

    private static final Logger logger = Logger.getLogger(PersonController.class);

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonCLDAO personCLDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/person/reniec/{docNumber}", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> getReniecFromDni(
            ModelMap model, Locale locale, HttpSession session,
            @PathVariable("docNumber") String docNumber) throws Exception {
        Reniec reniec = personDao.getReniecDBData(docNumber);
        if (reniec != null) {
            return AjaxResponse.ok(new Gson().toJson(reniec));
        } else {
            return AjaxResponse.ok(null);
        }
    }

}
