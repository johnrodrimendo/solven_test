package com.affirm.common.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jarmando on 26/01/17.
 */
public interface UrlService {
    String externalUrl(HttpServletRequest request);
}
