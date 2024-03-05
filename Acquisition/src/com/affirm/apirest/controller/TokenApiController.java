package com.affirm.apirest.controller;

import com.affirm.apirest.model.*;
import com.affirm.apirest.service.ApiRestService;
import com.affirm.client.model.annotation.ErrorRestControllerAnnotation;
import com.affirm.common.model.transactional.ApiRestUser;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.auth.oauth2.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Locale;

@RestController("tokenApiController")
public class TokenApiController {

    public static final String URL = "/"+Configuration.API_REST_API_PATH+"/authorization";
    @Autowired
    private ApiRestService apiRestService;

    @RequestMapping(value =  URL+"/token", method = RequestMethod.POST)
    @ErrorRestControllerAnnotation
    public Object generateToken(
            Locale locale,
            @RequestBody TokenApiRequest tokenApiRequest) throws Exception {

        tokenApiRequest.getValidator().validate(locale);
        if (tokenApiRequest.getValidator().isHasErrors()) return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_FIELDS);

        ApiRestUser apiRestUser = apiRestService.getUserByCredentials(tokenApiRequest.getUser(), tokenApiRequest.getPassword());

        if(apiRestUser == null || !apiRestUser.getValid()){
            return ApiRestResponse.errorMessage(ApiRestResponse.ERROR_INVALID_USER);
        }

        ApiRestToken token = apiRestService.generateToken(apiRestUser.getId());

        TokenApiResponse tokenResponse = new TokenApiResponse();
        tokenResponse.setToken(token.getToken());

        return ApiRestResponse.ok(tokenResponse);
    }

}
