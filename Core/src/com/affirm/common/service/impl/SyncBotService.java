package com.affirm.common.service.impl;

import com.affirm.common.dao.BotDAO;
import com.affirm.common.model.catalog.Bot;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.transactional.MigracionesResult;
import com.affirm.common.model.transactional.QueryBot;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.WebscrapperService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by jarmando on 06/02/17.
 */
@Service
public class SyncBotService {
    private static final Logger logger = Logger.getLogger(SyncBotService.class);

    @Autowired
    BotDAO botDAO;
    @Autowired
    WebscrapperService webscrapperService;
    @Autowired
    CatalogService catalogService;

    public Integer callMigraciones(String docNumber, Date birthdate) {
        //returns queryId
        webscrapperService.setCountry(catalogService.getCountryParam(CountryParam.COUNTRY_PERU));
        return webscrapperService.callMigracionesBot(docNumber, birthdate, null).getId();
    }

    public Boolean migracionesSuccess(int queryId) {
        int pollIntervalSec = 3;
        int maxSeconds = 40;
        int count = 0;
        while (count < maxSeconds) {
            count += pollIntervalSec;
            Integer status = botDAO.getQueryBot(queryId).getStatusId();
            if(status == QueryBot.STATUS_FAIL) {
                return null;
            }
            if(status == QueryBot.STATUS_SUCCESS) {
                MigracionesResult result = (MigracionesResult) botDAO.getQueryResult(queryId, Bot.MIGRACIONES);
                if(result.getFullName() != null) {
                    return true;
                }
                else {
                    return false;
                }
            }
            try { Thread.sleep(pollIntervalSec * 1000); } catch (Exception e) {}
        }
        throw new RuntimeException("Timeout, el lg query bot sigue en running");
    }
}