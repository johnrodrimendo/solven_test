package com.affirm.apirest.controller;

/**
 * Created by dev5 on 22/11/17.
 */

import com.affirm.apirest.model.*;
import com.affirm.apirest.service.ApiRestService;
import com.affirm.client.model.annotation.ErrorRestControllerAnnotation;
import com.affirm.common.model.BankAccountOfferData;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Locale;

@RestController("loanApplicationApiController")
public class LoanApplicationApiController {

    @Autowired
    private ApiRestService apiRestService;

    public static final String URL = "/"+Configuration.API_REST_API_PATH+"/loanapplication";

    @RequestMapping(value = URL, method = RequestMethod.POST)
    @ErrorRestControllerAnnotation
    public Object generateLoanApplication(
            HttpServletRequest request,
            Locale locale,
            @RequestBody GenerateLoanApiRequest generateLoanApiRequest) throws Exception {

        generateLoanApiRequest.getValidator().validate(locale);
        if (generateLoanApiRequest.getValidator().isHasErrors()) return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_FIELDS);

        // Validate the token
        if(!apiRestService.validateToken(generateLoanApiRequest.getToken()))
            return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_TOKEN);

        ApiRestToken apiRestToken = apiRestService.getApiRestToken(generateLoanApiRequest.getToken());

        ApiRestUser apiRestUser = apiRestService.getApiRestUserById(apiRestToken.getApiRestUserId());

        if(apiRestUser == null || !apiRestUser.getValid()){
            return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_USER);
        }

        AdditionalDataLoanApi additionalDataLoanApi = new AdditionalDataLoanApi();

        //IS PRODUCT CATEGORY VALID?
        switch (apiRestUser.getEntityId()){
            case Entity.AZTECA:
                if(!Arrays.asList(GenerateLoanApiRequest.AZTECA_CREDITO_CONSUMO,GenerateLoanApiRequest.AZTECA_CUENTA_AHORRO_META,GenerateLoanApiRequest.AZTECA_CUENTA_AHORRO_DIA).contains(generateLoanApiRequest.getEntityProductId())) return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_FIELDS);
                switch (generateLoanApiRequest.getEntityProductId()){
                    case GenerateLoanApiRequest.AZTECA_CREDITO_CONSUMO:
                        generateLoanApiRequest.setProductCategoryId(ProductCategory.CONSUMO);
                        break;
                    case GenerateLoanApiRequest.AZTECA_CUENTA_AHORRO_META:
                    case GenerateLoanApiRequest.AZTECA_CUENTA_AHORRO_DIA:
                        generateLoanApiRequest.setProductCategoryId(ProductCategory.CUENTA_BANCARIA);
                        if(generateLoanApiRequest.getEntityProductId().equals(GenerateLoanApiRequest.AZTECA_CUENTA_AHORRO_META)) additionalDataLoanApi.setBankAccountCustomOffer(BankAccountOfferData.HIGH_PROFITABILITY_TYPE);
                        if(generateLoanApiRequest.getEntityProductId().equals(GenerateLoanApiRequest.AZTECA_CUENTA_AHORRO_DIA)) additionalDataLoanApi.setBankAccountCustomOffer(BankAccountOfferData.TRADITIONAL_TYPE);
                        break;
                }
                break;
        }

        generateLoanApiRequest.setApiRestUserId(apiRestUser.getId());

        String loanUrl = null;

        try{
            loanUrl = apiRestService.generateLoanApplicationLink(generateLoanApiRequest, apiRestUser, additionalDataLoanApi, request);
        }
        catch(SqlErrorMessageException e){
            return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_FIELDS);
        }
        catch (Exception e){
            e.printStackTrace();
            return ApiRestResponse.errorMessage(ApiRestResponse.INTERNAL_SERVER_ERROR);
        }

        // return the url
        GenerateLoanApiResponse response = new GenerateLoanApiResponse();
        response.setUrl(loanUrl);
        return ApiRestResponse.ok(response);
    }

}