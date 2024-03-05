package com.affirm.common.service.question;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question137Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question137Service")
public class Question137Service extends AbstractQuestionService<Question137Form> {

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CatalogDAO catalogDao;

    @Autowired
    TranslatorDAO translatorDAO;

    @Autowired
    UserDAO userDAO;


    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question137Form prepopulatedForm = new Question137Form();

        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                LoanOffer selectedOfer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                User user = userDAO.getUser(person.getUserId());

                prepopulatedForm.setName(person.getName());
                prepopulatedForm.setSurname(person.getFirstSurname());
                prepopulatedForm.setEmail(user.getEmail());

                prepopulatedForm.setDocumentNumber(person.getDocumentNumber());
                prepopulatedForm.setAmount(selectedOfer.getAmmount().intValue());
                JSONObject jsonObject = loanApplication.getEntityCustomData();
                prepopulatedForm.setReason(jsonObject.length() == 0 ? null : jsonObject.getString("bancoDelSolLoanReason"));
                prepopulatedForm.setDateOfBirth(utilService.dateFormat(person.getBirthday()));
                String landlineNumber = person.getLandline();
                if (landlineNumber != null) {
                    String landline = landlineNumber.substring(landlineNumber.indexOf(')') + 1).trim();
                    String landlineAreaCode = landlineNumber.substring(1, landlineNumber.indexOf(')'));
                    prepopulatedForm.setLandline(landline.replace("-", ""));
                    prepopulatedForm.setLandlineAreaCode(landlineAreaCode);
                }

                String mobile = user.getPhoneNumber();
                if (mobile != null) {
                    String mobileNumber = mobile.substring(mobile.indexOf(')') + 1).trim();
                    String mobileNumberAreaCode = mobile.substring(1, mobile.indexOf(')'));
                    prepopulatedForm.setMobile(mobileNumber.replace("-", ""));
                    prepopulatedForm.setMobileAreaCode(mobileNumberAreaCode);
                }

                JSONObject jsonFormData = new JSONObject();
                if (person.getMaritalStatus() != null) {
                    jsonFormData.put("maritalStatusValue", person.getMaritalStatus().getId());
                    jsonFormData.put("maritalStatusText", person.getMaritalStatus().getStatus());
                }

                if (loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey()) != null) {
                    String activityId = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey());
                    jsonFormData.put("activityId", activityId);
                }

                List<PersonDisqualifier> personDisqualifiers = personDao.getPersonDisqualifierByPersonId(person.getId());

                if (null != personDisqualifiers) {
                    PersonDisqualifier pep = personDisqualifiers.stream().filter(p -> p.getType().equals(PersonDisqualifier.PEP)).findFirst().orElse(null);
                    PersonDisqualifier facta = personDisqualifiers.stream().filter(p -> p.getType().equals(PersonDisqualifier.FACTA)).findFirst().orElse(null);

                    if (pep != null && pep.isDisqualified() == true) {
                        attributes.put("pepTrue", true);
                        attributes.put("pepDetail", pep.getDetail());
                    }
                    if (pep != null && pep.isDisqualified() == false) {
                        attributes.put("pepFalse", true);
                    }
                    if (facta != null && facta.isDisqualified() == true) {
                        attributes.put("factaTrue", true);
                    }
                    if (facta != null && facta.isDisqualified() == false) {
                        attributes.put("factaFalse", true);
                    }

                }
                attributes.put("maritalStatus", catalogService.getMaritalStatus(locale));
                attributes.put("formDefaults", prepopulatedForm);
                attributes.put("jsonFormData", jsonFormData.toString());
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question137Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                // Landing 5
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question137Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question137Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);

                User user = userDAO.getUser(person.getUserId());
                user.setEmail(form.getEmail());

                String mobile = form.getMobile();
                int mobileIndex = form.getMobileAreaCode().length() == 2 ? 3 : 4;
                user.setPhoneNumber("(" + form.getMobileAreaCode() + ") " + mobile.substring(0, mobileIndex) + "-" + mobile.substring(mobileIndex));

                if (null != form.getLandline()) {
                    String landline = form.getLandline();
                    int landlineIndex = form.getLandlineAreaCode().length() == 2 ? 3 : 4;
                    person.setLandline("(" + form.getLandlineAreaCode() + ") " + landline.substring(0, landlineIndex) + "-" + landline.substring(landlineIndex));
                } else {
                    person.setLandline(null);
                }

                int emailId = userDAO.registerEmailChange(user.getId(), user.getEmail());
                userDAO.validateEmailChange(user.getId(), emailId);
                personDao.updatePepInformation(person.getId(), form.getAcceptAgreement() != null, null);
                personDao.updateLandline(person.getId(), person.getLandline());
                personDao.updateMaritalStatus(person.getId(), form.getMaritalStatus());
                String smsToken = userDAO.registerPhoneNumber(user.getId(), CountryParam.COUNTRY_ARGENTINA + "", user.getPhoneNumber());
                userDAO.validateCellphone(user.getId(), smsToken);
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                        loanApplication.getEntityCustomData()
                                .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_ACTIVITY_TYPE.getKey(), form.getAfipActivity().toString())
                );
                boolean factaDisqualified = form.getFactaTrue() != null && form.getFactaFalse() == null;
                boolean pepDisqualified = form.getPepTrue() != null && form.getPepFalse() == null;
                String pepDetails = form.getPepTrue() != null ? form.getPepReason() : null;
                personDao.updateOrInsertPersonDisqualifierByPersonIdOnly(person.getId(), PersonDisqualifier.PEP, pepDisqualified, pepDetails);
                personDao.updateOrInsertPersonDisqualifierByPersonIdOnly(person.getId(), PersonDisqualifier.FACTA, factaDisqualified, null);
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }
}