package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.AddressBanBifForm;
import com.affirm.common.model.form.Question182Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationAuxData;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.ResponseEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service("question182Service")
public class Question182Service extends AbstractQuestionService<Question182Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CountryContextService countryContextService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question182Form form = new Question182Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(loanApplication.getPersonId(), false, locale);

                List<Bank> permittedBanks = getPermittedBanks(loanApplication.getId());
                attributes.put("form", form);
                attributes.put("banks", permittedBanks);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question182Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question182Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question182Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                if(form.getDeposit()){
                    List<Bank> permittedBanks = getPermittedBanks(loanApplication.getId());
                    if (permittedBanks.stream().noneMatch(b -> b.getId().equals(form.getBankId()))) {
                        throw new ResponseEntityException(AjaxResponse.ok("El banco no es válido"));
                    }
                }

                personDao.updatePersonBankAccountInformation(loanApplication.getPersonId(), form.getBankId(), 'S', null, null, form.getBankAccountCci());

                loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_DIRECCION_ENTREGA.getKey(), form.getAddressToSend()));
                if (loanApplication.getAuxData() == null) loanApplication.setAuxData(new LoanApplicationAuxData());
                loanApplication.getAuxData().setAcceptedTyC(form.getAcceptedTyC());
                loanApplicationDao.updateAuxData(loanApplication.getId(), loanApplication.getAuxData());

                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        throw new Exception("No method configured");
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

    private List<Bank> getPermittedBanks(int loanApplicationId) throws Exception {
        EntityProductParams entityProductParams = null;
        List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplicationId);
        LoanOffer selectedoffer = offers.stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        if(selectedoffer.getEntityProductParam() != null){
            entityProductParams = selectedoffer.getEntityProductParam();
        }
        else entityProductParams = catalogService.getEntityProductParam(selectedoffer.getEntityId(), selectedoffer.getProduct().getId());

        if (entityProductParams != null && entityProductParams.getDisbursementBanks() != null) {
            List<Bank> addmittedBanks = new ArrayList<>();
            for (Integer i : entityProductParams.getDisbursementBanks()) {
                addmittedBanks.add(catalogService.getBank(i));
            }
            return addmittedBanks;
        }
        return null;
    }

}

