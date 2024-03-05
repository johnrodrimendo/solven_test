package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.service.EmailBoService;
import com.affirm.backoffice.service.EmployerService;
import com.affirm.common.dao.EmployerDAO;
import com.affirm.common.util.CryptoUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    private EmployerDAO employerDao;

    @Autowired
    private EmailBoService emailCLService;

    @Override
    public void registerEmployer(int entityId, String name, String ruc, String address, String phone, Integer professionId, Integer daysAfterEndOfMonth, Integer daysBeforeEndOfMonth, JSONArray users) throws Exception {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()-_=+[{]}|;:,./?";

        for (int i = 0; i < users.length(); i++) {
            String password = RandomStringUtils.random(15, characters);
            String hashPassword = CryptoUtil.hashPassword(password);

            users.getJSONObject(i).put("password", hashPassword);
            users.getJSONObject(i).put("passwordBeforeHash", password);
        }

        employerDao.registerAdvanceSalaryEmployer(entityId, name, ruc, address, phone, professionId, daysAfterEndOfMonth, daysBeforeEndOfMonth, users);

        emailCLService.sendPasswordEmployersMail(name, ruc, users);
    }


    @Override
    public void updateUserEmployer(int entityId, String name, String ruc, JSONArray users) throws Exception {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~!@#$%^&*()-_=+[{]}|;:,./?";

        for (int i = 0; i < users.length(); i++) {
            String password = RandomStringUtils.random(15, characters);
            String hashPassword = CryptoUtil.hashPassword(password);
            users.getJSONObject(i).put("password", hashPassword);
            users.getJSONObject(i).put("passwordBeforeHash", password);
        }

        employerDao.registerUserEmployer(entityId,users);
        emailCLService.sendPasswordEmployersMail(name, ruc, users);
    }

}
