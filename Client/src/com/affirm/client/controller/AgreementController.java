package com.affirm.client.controller;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CreditService;
import com.affirm.common.util.CryptoUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@Scope("request")
public class AgreementController {

    private static final Logger logger = Logger.getLogger(AgreementController.class);
    public static final String LOANAPPLICATION_URL = "agreementloanapplication";
    public static final String LANDING_URL = "credito-por-convenio";

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CreditService creditService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired CatalogService catalogService;

    @RequestMapping(value = "/" + LOANAPPLICATION_URL + "/{loanAplicationToken}/associatedFile", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.OTHER)
    @ResponseBody
    public ResponseEntity downloadAssociatedFile(
            Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("loanAplicationToken") String loanAplicationToken) throws Exception {
        String decrypted = CryptoUtil.decrypt(loanAplicationToken);
        JSONObject jsonDecrypted = new JSONObject(decrypted);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(jsonDecrypted.getInt("loan"), locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), true);
        User user = userDao.getUser(person.getUserId());

        if (loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED_SIGNED) {
            String filename = "ficha de socio.pdf";
            if (credit.getEntity().getId() == Entity.EFL)
                filename = "pagaré.pdf";

            byte[] pdfAsBytes = creditService.renderAssociatedFileFromPdf(credit, person, user, locale);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(filename, filename);
            return new ResponseEntity<byte[]>(pdfAsBytes, headers, HttpStatus.OK);
        }
        return null;
    }
}
