package com.affirm.latam.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.util.AjaxResponse;
import com.affirm.latam.dao.LatamDAO;
import com.affirm.system.configuration.SpringRootConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/latam")
public class LatamController {

    private static final Logger logger = Logger.getLogger(LatamController.class);

    private final CacheManager cacheManager;
    private final LatamDAO latamDAO;

    @Autowired
    public LatamController(CacheManager cacheManager, LatamDAO latamDAO) {
        this.cacheManager = cacheManager;
        this.latamDAO = latamDAO;
    }

    @CrossOrigin
    @RequestMapping(value = "/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> defaultLandingData() {
        return AjaxResponse.ok(this.latamDAO.avgCreditRate().toString());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearLatamCacheDaily() {
        for (String name : this.cacheManager.getCacheNames()) {
            if(SpringRootConfiguration.LATAM_CACHE_NAME.equals(name)) {
                logger.debug(String.format("Deleted cache %s", name));
                this.cacheManager.getCache(name).clear();
            }
        }
    }

}
