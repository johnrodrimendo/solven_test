package com.affirm.backoffice.service;

import org.json.JSONArray;

public interface EmployerService {
    void registerEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId, Integer cutoffDay, Integer paymentDay, JSONArray users) throws Exception;
    void updateUserEmployer(int entityId, String name, String ruc, JSONArray users) throws Exception;
}
