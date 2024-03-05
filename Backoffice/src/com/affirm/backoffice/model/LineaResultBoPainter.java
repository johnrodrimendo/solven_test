package com.affirm.backoffice.model;

import com.affirm.common.util.JsonUtil;
import com.affirm.common.model.transactional.LineaResult;
import org.json.JSONObject;

/**
 * Created by john on 29/09/16.
 */
public class LineaResultBoPainter extends LineaResult {

    private Integer personId;
    private String docNumber;

    public void fillFromDb(JSONObject json) {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setDocNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        super.fillFromDb(json);
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }
}
