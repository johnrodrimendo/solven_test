package com.affirm.client.model;

import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.ManagementSchedule;
import com.affirm.common.model.transactional.UserFile;
import com.affirm.common.service.CatalogService;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class CreditExtranetPainter extends Credit {

    private ManagementSchedule nextPayment;
    private String disbursementAccount;
    private List<UserFile> contractFile;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
    }

    public ManagementSchedule getNextPayment() {
        return nextPayment;
    }

    public void setNextPayment(ManagementSchedule nextPayment) {
        this.nextPayment = nextPayment;
    }

    public String getDisbursementAccount() {
        return disbursementAccount;
    }

    public void setDisbursementAccount(String disbursementAccount) {
        this.disbursementAccount = disbursementAccount;
    }

    public List<UserFile> getContractFile() {
        return contractFile;
    }

    public void setContractFile(List<UserFile> contractFile) {
        this.contractFile = contractFile;
    }
}
