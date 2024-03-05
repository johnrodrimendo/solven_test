package com.affirm.tests.mati.service

import com.affirm.common.dao.LoanApplicationDAO
import com.affirm.common.dao.UserDAO
import com.affirm.common.model.catalog.EntityProductParams
import com.affirm.common.model.catalog.UserFileType
import com.affirm.common.model.transactional.LoanApplication
import com.affirm.common.model.transactional.LoanOffer
import com.affirm.common.model.transactional.MatiResult
import com.affirm.common.model.transactional.UserFile
import com.affirm.common.service.CatalogService
import com.affirm.common.util.AjaxResponse
import com.affirm.heroku.HerokuServiceCall
import com.affirm.mati.model.CreateVerificationResponse
import com.affirm.mati.model.MatiValidationError
import com.affirm.mati.service.MatiService
import com.affirm.security.dao.SecurityDAO
import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseConfig
import groovy.transform.CompileStatic
import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class MatiServiceTest extends BaseConfig {

    @Autowired
    private MatiService matiService
    @Autowired
    private LoanApplicationDAO loanApplicationDao
    @Autowired
    private SecurityDAO securityDao
    @Autowired
    private CatalogService catalogService
    @Autowired
    private UserDAO userDAO

    @Test
    void TestFlow() {
        int loanApplicationId = 636905

        MatiResult matiResult = securityDao.registerMatiResult(loanApplicationId, null)

        String token = matiService.getOauthToken();
        CreateVerificationResponse response = matiService.createVerification(loanApplicationId, matiResult.getId(), token)
        securityDao.updateMatiResultVerificationId(matiResult.getId(), response.getId());

        List<UserFile> files = loanApplicationDao.getLoanApplicationUserFiles(loanApplicationId);
        Integer selfieUserFileId = files.stream().filter { u -> ((UserFile) u).getFileType().getId() == UserFileType.SELFIE }.findFirst().map { u -> ((UserFile) u).getId() }.orElse(null)
        Integer dniFrontUserFileId = files.stream().filter { u -> ((UserFile) u).getFileType().getId() == UserFileType.DNI_FRONTAL }.findFirst().map { u -> ((UserFile) u).getId() }.orElse(null)
        Integer dniBackUserFileId = files.stream().filter { u -> ((UserFile) u).getFileType().getId() == UserFileType.DNI_ANVERSO }.findFirst().map { u -> ((UserFile) u).getId() }.orElse(null)
        matiService.sendDocumentation(loanApplicationId, response.getIdentity(), selfieUserFileId, dniFrontUserFileId, dniBackUserFileId, token)

        println response.getIdentity() + " - " + response.getId()

    }

    @Test
    void testController() {
        try {
            int loanApplicationId = 636905
            JSONArray jsonArray = new JSONArray("[{'error':{'type':'ValidationError','code':'documentPhoto.smallImageSize'}},{'error':{'type':'ValidationError','code':'documentPhoto.smallImageSize'}},{'result':true}]");

            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale())

            EntityProductParams entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
            if(entityProductParams.getEntityProductParamIdentityValidationConfig() != null &&
                    entityProductParams.getEntityProductParamIdentityValidationConfig().getRunMati() != null &&
                    entityProductParams.getEntityProductParamIdentityValidationConfig().getRunMati()){

                List<UserFile> files = loanApplicationDao.getLoanApplicationUserFiles(loanApplicationId);
                Integer selfieUserFileId = files.stream().filter { u -> ((UserFile) u).getFileType().getId() == UserFileType.SELFIE }.findFirst().map { u -> ((UserFile) u).getId() }.orElse(null)
                Integer dniFrontUserFileId = files.stream().filter { u -> ((UserFile) u).getFileType().getId() == UserFileType.DNI_FRONTAL }.findFirst().map { u -> ((UserFile) u).getId() }.orElse(null)
                Integer dniBackUserFileId = files.stream().filter { u -> ((UserFile) u).getFileType().getId() == UserFileType.DNI_ANVERSO }.findFirst().map { u -> ((UserFile) u).getId() }.orElse(null)

                MatiResult matiResult = securityDao.registerMatiResult(null, null);
                String matiToken = matiService.getOauthToken();
                CreateVerificationResponse response = matiService.createVerification(loanApplication.getId(), matiResult.getId(), matiToken);
                List<MatiValidationError> matiValidationErrors = matiService.sendDocumentation(loanApplication.getId(), response.getIdentity(), selfieUserFileId, dniFrontUserFileId, dniBackUserFileId, matiToken);
                if(!matiValidationErrors.isEmpty()){
                    for(MatiValidationError matiValidationError : matiValidationErrors){
                        userDAO.updateUserFileType(matiValidationError.getUserFileId(), UserFileType.ELIMINADOS);
                    }
                }else{
                    securityDao.updateMatiResultVerificationId(matiResult.getId(), response.getId());
                    securityDao.updateMatiResultLoanApplicationId(matiResult.getId(), loanApplication.getId());
                }
            }
        } catch (Exception ex) {
            println ex
        }
    }

    @Test
    @Disabled
    void getResponseById(){
        String token = matiService.getOauthToken();
        String response = matiService.getVerification("60b00e5717ea7c001c3b8016", token);
        System.out.println(response);
    }



}
