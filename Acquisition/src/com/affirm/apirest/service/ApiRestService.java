package com.affirm.apirest.service;

import com.affirm.apirest.dao.ApiRestDao;
import com.affirm.apirest.model.ApiRestToken;
import com.affirm.apirest.model.GenerateLoanApiRequest;
import com.affirm.apirest.model.AdditionalDataLoanApi;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.UserService;
import com.affirm.common.service.impl.FunnelStepService;
import com.affirm.common.service.question.Question95Service;
import com.affirm.common.util.Util;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.*;

@Service("apiRestService")
public class ApiRestService {

    @Autowired
    private ApiRestDao apiRestDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserService userService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private Question95Service question95Service;
    @Autowired
    private FunnelStepService funnelStepService;
    @Autowired
    private CreditDAO creditDao;

    public ApiRestToken generateToken(int apiRestUserId) throws  Exception{
        String randomToken = RandomStringUtils.random(32, 0, 0, true, true, null, new SecureRandom());
        // Generate the token with 60 minutes of validity
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);

        ApiRestToken apiRestToken = new ApiRestToken();
        apiRestToken.setToken(randomToken);
        apiRestToken.setApiRestUserId(apiRestUserId);
        apiRestToken.setRegisterDate(new Date());
        apiRestToken.setValidityDate(calendar.getTime());
        return apiRestDao.registerApiRestToken(apiRestToken);
    }

    public boolean validateToken(String token) throws Exception {
        ApiRestToken apiRestToken = getApiRestToken(token);
        if(apiRestToken == null || apiRestToken.getValidityDate() == null) return false;
        if(apiRestToken.getValidityDate().compareTo(new Date()) < 1) return false;
        return true;
    }

    public String generateLoanApplicationLink(GenerateLoanApiRequest request, ApiRestUser apiRestUser, AdditionalDataLoanApi additionalDataLoanApi, HttpServletRequest httpServletRequest) throws Exception {
        EntityBranding brandingEntity = catalogService.getEntityBranding(apiRestUser.getEntityId());
        List<Agent> agentsList = catalogService.getFormAssistantsAgents(apiRestUser.getEntityId());
        int randomIndexWithinRange = (int) Math.floor(Math.random() * agentsList.size());
        int agentId = agentsList.get(randomIndexWithinRange).getId();

        User user = userService.getOrRegisterUser(request.getDocumentType(), request.getDocumentNumber(), null, null, null);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), user.getPersonId(), false);

        if(!brandingEntity.getEntity().getId().equals(Entity.AZTECA)){
            // Insert the email or respond that the email is invalid
            userService.createEmailPassword(request.getEmail(), user.getId());
        }


        LoanApplication loanApplication = null;
        if (brandingEntity != null) {
            loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), request.getProductCategoryId(), brandingEntity.getEntity().getId());
        } else {
            loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), request.getProductCategoryId());
        }

        boolean expireLoanApplication = false;
        if(request.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA && brandingEntity.getEntity().getId().equals(Entity.AZTECA) && loanApplication != null){
            if(loanApplication.getCredit() != null && !loanApplication.getCredit() && !Arrays.asList(LoanApplicationStatus.APPROVED_SIGNED,LoanApplicationStatus.WAITING_APPROVAL).contains(loanApplication.getStatus().getId())){
                if(loanApplication.getAuxData() == null || loanApplication.getAuxData().getApiRestUserId() == null) expireLoanApplication = true;
                else if(additionalDataLoanApi != null && additionalDataLoanApi.getBankAccountCustomOffer() != null && !additionalDataLoanApi.getBankAccountCustomOffer().equals(loanApplication.getAuxData().getBankAccountCustomOffer())) expireLoanApplication = true;
            }
        }

        if(request.getProductCategoryId() == ProductCategory.CONSUMO && brandingEntity.getEntity().getId().equals(Entity.AZTECA) && loanApplication != null){
            if(loanApplication.getCredit() != null && !loanApplication.getCredit() && !Arrays.asList(LoanApplicationStatus.APPROVED_SIGNED,LoanApplicationStatus.WAITING_APPROVAL).contains(loanApplication.getStatus().getId())){
                if(loanApplication.getAuxData() == null || loanApplication.getAuxData().getApiRestUserId() == null) expireLoanApplication = true;
            }
            else if(loanApplication.getCredit() != null && loanApplication.getCredit() && loanApplication.getCreditId() != null){
                Credit credit =  creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                if((loanApplication.getAuxData() == null || loanApplication.getAuxData().getApiRestUserId() == null) && !Arrays.asList(CreditStatus.ORIGINATED,CreditStatus.ORIGINATED_DISBURSED).contains(credit.getStatus().getId())) expireLoanApplication = true;
            }
        }

        if(expireLoanApplication){
            loanApplicationDao.expireLoanApplication(loanApplication.getId());
            loanApplication = null;
        }

        // If there is an active loan, return it
        if (loanApplication != null) {
            if(!brandingEntity.getEntity().getId().equals(Entity.AZTECA)){
                userDao.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", request.getCellphone());
            }
            else {
                if(user.getEmail() == null || (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail()))) userService.createEmailPasswordExcludeValidation(request.getEmail(), user.getId());
                if(user.getPhoneNumber() == null || (request.getCellphone() != null && !request.getCellphone().equalsIgnoreCase(user.getPhoneNumber()))) userDao.registerPhoneNumberExcludeVerification(user.getId(), brandingEntity.getEntity().getCountryId() + "", request.getCellphone());
            }
            return loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
        }

        if(!brandingEntity.getEntity().getId().equals(Entity.AZTECA)){
            // REGISTER PHONENUMBER OR RESPOND THAT PHONENUMBER IS INVALID
            userDao.registerPhoneNumber(user.getId(), brandingEntity.getEntity().getCountryId() + "", request.getCellphone());
        }

        Integer installemnts = Configuration.DEFAULT_INSTALLMENTS;
        if(brandingEntity != null && Arrays.asList(Entity.FINANSOL, Entity.PRISMA).contains(brandingEntity.getEntity().getId()))
            installemnts = 12;
        //Save values in loanApplication Database
        loanApplication = loanApplicationDao.registerLoanApplication(
                user.getId(),
                null,
                installemnts,
                null,
                null,
                null,
                null,
                LoanApplication.ORIGIN_API_REST,
                null,
                null,
                brandingEntity != null ? brandingEntity.getEntity().getId() : null,
                brandingEntity.getEntity().getCountryId());
        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());

        if (request.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA) {
            loanApplicationDao.updateLoanApplicationCode(loanApplication.getId(),  loanApplication.getCode().replace("LA","AA"));
        }

        if(brandingEntity.getEntity().getId().equals(Entity.AZTECA)){
            // Insert the email or respond that the email is invalid
            userService.createEmailPasswordExcludeValidation(request.getEmail(), user.getId());
            if(loanApplication.getAuxData() == null) loanApplication.setAuxData(new LoanApplicationAuxData());

            if(request.getEmail() != null){
                int emailId = userDao.insertEmail(user.getId(), request.getEmail(), false, false);
                loanApplication.getAuxData().setRegisteredEmail(request.getEmail());
                loanApplication.getAuxData().setRegisteredEmailId(emailId);
            }
            if(request.getCellphone() != null){
                Random rand = new Random();
                String smsToken = String.format("%04d%n", rand.nextInt(10000));
                int phoneNumberId = userDao.insertPhoneNumber(user.getId(), brandingEntity.getEntity().getCountryId() + "", request.getCellphone(), smsToken, false, false);
                loanApplication.getAuxData().setRegisteredPhoneNumber(request.getCellphone());
                loanApplication.getAuxData().setRegisteredPhoneNumberId(phoneNumberId);
                userDao.registerPhoneNumberExcludeVerification(user.getId(), brandingEntity.getEntity().getCountryId() + "", request.getCellphone());
            }
            loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplication.getAuxData());
            userService.activateUserEmailPhoneNumber(loanApplication.getAuxData().getRegisteredPhoneNumberId(), loanApplication.getAuxData().getRegisteredEmailId(),user.getId());
        }

        userService.registerIpUbication(Util.getClientIpAddres(httpServletRequest), loanApplication.getId());

        loanApplicationDao.updateProductCategory(loanApplication.getId(), request.getProductCategoryId());
        loanApplicationDao.updateFormAssistant(loanApplication.getId(), agentId);

        LoanApplicationAuxData loanApplicationAuxData = loanApplication.getAuxData();
        if(loanApplicationAuxData == null) loanApplicationAuxData = new LoanApplicationAuxData();
        loanApplicationAuxData = loanApplication.getAuxData();
        loanApplicationAuxData.setApiRestUserId(request.getApiRestUserId());
        loanApplicationAuxData.setApiRestWebhookUrl(request.getWebhookUrl());
        loanApplicationAuxData.setApiRestTrackingId(request.getTrackingId());
        //FIELDS TO UPDATE FROM REQUEST
        if(additionalDataLoanApi != null){
            loanApplicationAuxData.setBankAccountCustomOffer(additionalDataLoanApi.getBankAccountCustomOffer());
        }

        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplicationAuxData);

        String abTestingValue =  LoanApplication.AB_TESTING_A;

        if (brandingEntity != null && Entity.BANBIF == brandingEntity.getEntity().getId()) {
            if (loanApplication.getEntityCustomData() == null) {
                loanApplication.setEntityCustomData(new JSONObject());
            }

            loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey(), LoanApplication.AB_TESTING_A));
        }else if (brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId()) {
            if (loanApplication.getEntityCustomData() == null) {
                loanApplication.setEntityCustomData(new JSONObject());
            }

            if(Arrays.asList(ProductCategory.CONSUMO).contains(request.getProductCategoryId())) {
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_LANDING_AB_TESTING.getKey(), LoanApplication.AB_TESTING_A));
                abTestingValue = LoanApplication.AB_TESTING_A;
            }
            else loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_LANDING_AB_TESTING.getKey(), LoanApplication.AB_TESTING_A));
        }

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());

        if (brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId() && request.getProductCategoryId() == ProductCategory.GATEWAY) {
            loanApplicationDao.updateLoanApplicationCode(loanApplication.getId(),  loanApplication.getCode().replace("LA","CA"));
        }

        if (brandingEntity != null && brandingEntity.getLandingConfiguration() != null && brandingEntity.getLandingConfiguration().getQuestionToGo() != null)
            loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), brandingEntity.getLandingConfiguration().getQuestionToGo());
        else
        {
            if (person != null && person.getFirstName() != null && !person.getFirstName().isEmpty() && person.getBirthday() != null)
                loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION);
            else
                loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.NO_RENIEC_BEFORE_PRE_EVALUATION);
        }

        if (brandingEntity != null)
            loanApplicationDao.updateEntityId(loanApplication.getId(), brandingEntity.getEntity().getId());

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());
        loanApplicationDao.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());
        if(loanApplication.getCurrentProcessQuestion() != null && loanApplication.getCurrentProcessQuestion().getId() == ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION){
            Boolean runEvaluation = false;
            if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY) runEvaluation = true;
            question95Service.runPreEvaluationBotIfNoRunYet(loanApplication.getId(),runEvaluation);
        }

        loanApplicationAuxData = loanApplication.getAuxData();
        loanApplicationAuxData.setAbTestingValue(abTestingValue.charAt(0));
        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplicationAuxData);
        
        funnelStepService.registerStep(loanApplication);

        return loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
    }

    public ApiRestUser getUserByCredentials(String username, String password) throws Exception {
        return apiRestDao.getApiRestUserByCredentials(username,password);
    }

    public ApiRestToken getApiRestToken(String token) throws Exception {
        return apiRestDao.getApiRestToken(token);
    }

    public ApiRestUser getApiRestUserById(Integer userId) throws Exception {
        return apiRestDao.getApiRestUserById(userId);
    }

}
