package com.affirm.client.service;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface VehicleSheetService {
    void createImportEmployeesExcelTemplate(OutputStream outputStream) throws Exception;
    JSONObject getJsonVehicles(MultipartFile[] file) throws Exception;
    JSONObject saveVehicles(String jsonVehicles)throws Exception;
}
