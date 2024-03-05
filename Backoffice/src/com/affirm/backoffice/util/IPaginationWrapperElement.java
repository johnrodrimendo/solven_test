package com.affirm.backoffice.util;

import com.affirm.common.service.CatalogService;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * Created by john on 29/12/16.
 */
public interface IPaginationWrapperElement {

    void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messagesource, Locale locale) throws Exception;

}
