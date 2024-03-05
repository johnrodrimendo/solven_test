package com.affirm.common.service.question;

import com.affirm.banbif.service.BanBifService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.form.Question119Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question129Service")
public class Question129Service extends Question119Service{

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private BanBifService banBifService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question119Form form = new Question119Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ((Question119Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

                attributes.put("form", form);
                attributes.put("loanApplication", loanApplication);
                attributes.put("yearFrom", 1900);
                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question119Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question119Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question119Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
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

                if  (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER) {
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.FDM_PROCEDENCIA_SOLICITUD.getKey(), form.getLoanOrigin());
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                }

                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                if (/*!loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU) || */!person.getFirstName().isEmpty() && person.getBirthday() != null) {
                    return "DEFAULT";
                }
                // If its banbif, check if there is a name in the pre approved base
                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF){
                    BanbifPreApprovedBase banbifBase = banBifService.getBanbifPreApprovedBase("DNI", person.getDocumentNumber(), loanApplication);
                    if(banbifBase != null){
                        personDao.updateName(loanApplication.getPersonId(), banbifBase.getNombres());
                        personDao.updateFirstSurname(loanApplication.getPersonId(), banbifBase.getApellidos());
                        return "DEFAULT";
                    }
                }
                // If its azteca, check if there is a name in the pre approved base
                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA){
                    AztecaPreApprovedBase aztecaPreApprovedBase = rccDao.getAztecaPreApprovedBase(person.getDocumentNumber());
                    if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getNombre() != null && aztecaPreApprovedBase.getApPaterno() != null){
                        personDao.updateName(loanApplication.getPersonId(), aztecaPreApprovedBase.getNombre());
                        personDao.updateFirstSurname(loanApplication.getPersonId(), aztecaPreApprovedBase.getApPaterno());
                        if(aztecaPreApprovedBase.getApMaterno() != null)
                            personDao.updateLastSurname(loanApplication.getPersonId(), aztecaPreApprovedBase.getApMaterno());
                        return "DEFAULT";
                    }

                    if(aztecaPreApprovedBase != null && aztecaPreApprovedBase.getNombre() != null && aztecaPreApprovedBase.getApPaterno() != null){
                        personDao.updateName(loanApplication.getPersonId(), aztecaPreApprovedBase.getNombre());
                        personDao.updateFirstSurname(loanApplication.getPersonId(), aztecaPreApprovedBase.getApPaterno());
                        if(aztecaPreApprovedBase.getApMaterno() != null)
                            personDao.updateLastSurname(loanApplication.getPersonId(), aztecaPreApprovedBase.getApMaterno());
                        return "DEFAULT";
                    }

                    if(loanApplication.getProductCategoryId() != null && Arrays.asList(ProductCategory.GATEWAY).contains(loanApplication.getProductCategoryId())){
                        List<AztecaGetawayBase> aztecaGetawayBases = loanApplication.getAztecaGatewayBasesData();
                        AztecaGetawayBase aztecaGetawayBase = aztecaGetawayBases != null && !aztecaGetawayBases.isEmpty() ? aztecaGetawayBases.get(0) : null;
                        if (aztecaGetawayBases != null && aztecaGetawayBase.getNombre() != null && !aztecaGetawayBase.getNombre().trim().isEmpty()) {
                            personDao.updateName(loanApplication.getPersonId(), aztecaGetawayBase.getNombre().trim());
                            if(aztecaGetawayBase.getApPaterno() != null && person.getFirstSurname() == null) personDao.updateFirstSurname(loanApplication.getPersonId(), aztecaGetawayBase.getApPaterno());
                            if(aztecaGetawayBase.getApMaterno() != null && person.getLastSurname() == null) personDao.updateLastSurname(loanApplication.getPersonId(), aztecaGetawayBase.getApMaterno());
                            return "DEFAULT";
                        }
                    }
                }
                break;
        }

        return null;
    }
}
