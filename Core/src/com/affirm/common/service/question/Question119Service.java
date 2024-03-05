package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question119Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question119Service")
public class Question119Service extends AbstractQuestionService<Question119Form>{

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question119Form form = new Question119Form();

        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                attributes.put("form", form);
                attributes.put("yearFrom", 1900);
                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
                attributes.put("loanApplication", loanApplication);
                break;
        }

        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question119Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                // Landing 5
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question119Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question119Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                personDao.updateName(loanApplication.getPersonId(), form.getName());
                personDao.updateFirstSurname(loanApplication.getPersonId(), form.getFirstSurname());
                personDao.updateLastSurname(loanApplication.getPersonId(), form.getLastSurname());
                personDao.updateBirthday(loanApplication.getPersonId(), utilService.parseDate(form.getBirthday(), "dd/MM/yyyy", locale));
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(id, locale);
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                if (!loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU) || !person.getFirstName().isEmpty()) {
                    return "DEFAULT";
                }
                break;
        }

        return null;
    }

    public HashMap<Integer, String> getFDLMLoanOrigins() {
        HashMap<Integer, String> fdlmLoanOrigin = new LinkedHashMap<>();
        fdlmLoanOrigin.put(1, "Referenciado por un cliente");
        fdlmLoanOrigin.put(2, "Volante");
        fdlmLoanOrigin.put(4, "Radio");
        fdlmLoanOrigin.put(5, "Prensa");
        fdlmLoanOrigin.put(6, "Televisión");
        fdlmLoanOrigin.put(8, "Afiche");
        fdlmLoanOrigin.put(10, "Referenciado por un funcionario");
        fdlmLoanOrigin.put(11, "Valla publicitaria");
        fdlmLoanOrigin.put(13, "Agencia móvil");
        fdlmLoanOrigin.put(17, "Renovación");
        fdlmLoanOrigin.put(19, "Analista de inclusión financiera");
        fdlmLoanOrigin.put(21, "Contact center de fidelización");
        fdlmLoanOrigin.put(22, "Web");
        fdlmLoanOrigin.put(26, "Línea de apoyo");
        fdlmLoanOrigin.put(27, "Facebook");
        fdlmLoanOrigin.put(28, "Chatbot");
        fdlmLoanOrigin.put(29, "Instagram");
        fdlmLoanOrigin.put(30, "Whatsapp");

        return fdlmLoanOrigin;
    }
}
