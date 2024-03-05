 package com.affirm.common.service.impl;

import com.affirm.common.service.UrlService;
import com.affirm.common.util.Util;
import com.affirm.system.configuration.Configuration;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jarmando on 26/01/17.
 */
@Service("urlService")
public class UrlServiceImpl implements UrlService {

    @Override
    public String externalUrl(HttpServletRequest request) {
        //local
        if (Configuration.hostEnvIsLocal()) {
            return request.getContextPath() + "/external";
        } else {
            //nonlocal
            if (Configuration.isBackoffice()) {
                return request.getContextPath() + "/external";
            }
        }
        //cli
        return "https://s3.amazonaws.com/solven-public/external";
    }
}
