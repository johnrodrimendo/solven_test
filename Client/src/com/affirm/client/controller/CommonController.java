package com.affirm.client.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Scope("request")
public class CommonController {

    //    Es necesario mandar estos archivos con header Service-Worker-Allowed porque estos NO son servidos desde root sino desde la carpeta /js/
    @RequestMapping(value = "/js/{jsFile:OneSignalSDKUpdaterWorker.js|OneSignalSDKWorker.js}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getOneSignalResource(@PathVariable("jsFile") String jsFile) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Service-Worker-Allowed", "/");
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:/**/" + jsFile);
        return new ResponseEntity<>(resources != null && resources.length > 0 ? resources[0] : null, headers, HttpStatus.OK);
    }

}
