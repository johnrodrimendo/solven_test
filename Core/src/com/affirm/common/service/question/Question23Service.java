package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.HouseType;
import com.affirm.common.model.catalog.HousingType;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.form.Question142Form;
import com.affirm.common.model.form.Question23Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question23Service")
public class Question23Service extends AbstractQuestionService<Question23Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question23Form form = new Question23Form();
        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if(fillSavedData){
                    PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
                    form.setHousingType(contactInformation != null && contactInformation.getHousingType() != null ? contactInformation.getHousingType().getId() : null);
                }

                List<Integer> houseTypeIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(id, ProcessQuestion.Question.Constants.HOME_TYPE, "houseTypeIds");

                List<HousingType> houseTypes = catalogService.getHousingTypes(locale, true);

                if(houseTypeIds != null && !houseTypeIds.isEmpty()){
                    houseTypes = catalogService.getHousingTypes(locale).stream().filter(h -> houseTypeIds.contains(h.getId())).collect(Collectors.toList());
                }

                attributes.put("houseTypes", houseTypes);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question23Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question23Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question23Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personDao.updateHousingType(loanApplication.getPersonId(), form.getHousingType());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                Double monthlyInstallmentMortage = personDao.getMonthlyInstallmentMortage(person.getDocumentNumber());
                if (monthlyInstallmentMortage != null && monthlyInstallmentMortage > 0) {
                    if(saveData)
                        personDao.updateHousingType(loanApplication.getPersonId(), HousingType.OWN_FINANCED);
                    return "DEFAULT";
                }
                break;
        }
        return null;
    }

}

