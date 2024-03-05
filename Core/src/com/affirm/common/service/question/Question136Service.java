package com.affirm.common.service.question;

import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.bancodelsol.service.impl.BancoDelSolServiceImpl;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.form.Question136Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question136Service")
public class Question136Service extends AbstractQuestionService<Question136Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private RccDAO rccDAO;
    @Autowired
    private PersonDAO personDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question136Form form = new Question136Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                if (fillSavedData) {
                    if (loanApplication.getAmount() != null)
                        form.setAmount(loanApplication.getAmount());
                }

                JSONObject json = bancoDelSolService.commissionClusterByClientType();
                JSONObject sancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_SANCOR_CLUSTER_NAME);
                JSONObject noSancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_NO_SANCOR_CLUSTER_NAME);

//                TODO FIND A BETTER WAY
                attributes.put("sancorMinInstallments", sancorClientJson.getInt("minInstallments"));
                attributes.put("sancorMaxInstallments", sancorClientJson.getInt("maxInstallments"));
                attributes.put("noSancorMinInstallments", noSancorClientJson.getInt("minInstallments"));
                attributes.put("noSancorMaxInstallments", noSancorClientJson.getInt("maxInstallments"));
                attributes.put("sancorMinAmount", sancorClientJson.getInt("minAmount"));
                attributes.put("noSancorMinAmount", noSancorClientJson.getInt("minAmount"));
                attributes.put("sancorMaxAmount", sancorClientJson.getInt("maxAmount"));
                attributes.put("noSancorMaxAmount", noSancorClientJson.getInt("maxAmount"));
                attributes.put("sancorMinRateCommission", sancorClientJson.getDouble("minRateCommission"));
                attributes.put("sancorMaxRateCommission", sancorClientJson.getDouble("maxRateCommission"));
                attributes.put("noSancorMinRateCommission", noSancorClientJson.getDouble("minRateCommission"));
                attributes.put("noSancorMaxRateCommission", noSancorClientJson.getDouble("maxRateCommission"));

                attributes.put("loanApplication", loanApplication);

                int clientType = BancoDelSolServiceImpl.CLIENT_TYPES.indexOf(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey()));

                switch (clientType) {
                    case 0:
                        ((Question136Form.Validator) form.getValidator()).amount.setMaxValue(sancorClientJson.getInt("maxAmount"));
                        ((Question136Form.Validator) form.getValidator()).amount.setMinValue(sancorClientJson.getInt("minAmount"));
                        break;
                    case 1:
                        ((Question136Form.Validator) form.getValidator()).amount.setMaxValue(noSancorClientJson.getInt("maxAmount"));
                        ((Question136Form.Validator) form.getValidator()).amount.setMinValue(noSancorClientJson.getInt("minAmount"));
                        break;
                }

                attributes.put("form", form);
                break;
            case SELFEVALUATION:

                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                if (fillSavedData) {
                    if (selfEvaluation.getAmount() != null)
                        form.setAmount(selfEvaluation.getAmount().intValue());
                }
                attributes.put("selfEvaluation", selfEvaluation);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question136Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question136Form form, Locale locale) throws Exception {
        JSONObject json = bancoDelSolService.commissionClusterByClientType();
        JSONObject sancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_SANCOR_CLUSTER_NAME);
        JSONObject noSancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_NO_SANCOR_CLUSTER_NAME);

        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                int clientType = BancoDelSolServiceImpl.CLIENT_TYPES.indexOf(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey()));

                switch (clientType) {
                    case 0:
                        ((Question136Form.Validator) form.getValidator()).amount.setMaxValue(sancorClientJson.getInt("maxAmount"));
                        ((Question136Form.Validator) form.getValidator()).amount.setMinValue(sancorClientJson.getInt("minAmount"));
                        break;
                    case 1:
                        ((Question136Form.Validator) form.getValidator()).amount.setMaxValue(noSancorClientJson.getInt("maxAmount"));
                        ((Question136Form.Validator) form.getValidator()).amount.setMinValue(noSancorClientJson.getInt("minAmount"));
                        break;
                }

                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question136Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                JSONObject json = bancoDelSolService.commissionClusterByClientType();
                JSONObject sancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_SANCOR_CLUSTER_NAME);
                JSONObject noSancorClientJson = json.getJSONObject(BancoDelSolServiceImpl.CLIENTE_NO_SANCOR_CLUSTER_NAME);

                int clientType = BancoDelSolServiceImpl.CLIENT_TYPES.indexOf(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey()));

                loanApplicationDao.updateAmount(loanApplication.getId(), form.getAmount());

                Person person = personDao.getPerson(loanApplication.getPersonId(), false, locale);
                CampaniaBds campaignBds = rccDAO.getLastCampaniaBds(person.getDocumentNumber());
                if(campaignBds != null && campaignBds.getPlazoMaximo() != null){
                    loanApplicationDao.updateInstallments(loanApplication.getId(), campaignBds.getPlazoMaximo());
                }else{
                    switch (clientType) {
                        case 0:
                            loanApplicationDao.updateInstallments(loanApplication.getId(), sancorClientJson.getInt("maxInstallments"));
                            break;
                        case 1:
                            loanApplicationDao.updateInstallments(loanApplication.getId(), noSancorClientJson.getInt("maxInstallments"));
                            break;
                    }
                }

                break;
            case SELFEVALUATION:
                selfEvaluationDao.updateAmount(id, form.getAmount());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
            case SELFEVALUATION:
                break;
        }
        return null;
    }
}