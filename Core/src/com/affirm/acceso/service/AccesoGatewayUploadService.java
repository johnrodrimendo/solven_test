package com.affirm.acceso.service;

import org.apache.poi.ss.usermodel.Sheet;

public interface AccesoGatewayUploadService {

    void lookupForNotCollectedSoonToExpireInstallments(Sheet report, int[] kindsOfInteraction, Integer userId) throws Exception;

}
