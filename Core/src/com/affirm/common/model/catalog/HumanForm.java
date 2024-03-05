package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 08/11/16.
 */
public class HumanForm implements Serializable {

    public static final int PREEVALUATION = 0;
    public static final int HELLO = 1;
    public static final int ARE_YOU_READY = 2;
    public static final int PARTNER = 3;
    public static final int EMAIL = 4;
    public static final int PHONE = 5;
    public static final int ADDESS = 6;
    public static final int OCUPATION = 7;
    public static final int SOCIAL_NETWORKS = 8;
    public static final int EVALUATION = 9;

    /* Questions */
    public static final int QUESTION_DOCUMENT = 1;
    public static final int QUESTION_BIRTHDATE = 2;
    public static final int QUESTION_WILL_LIKE = 3;
    public static final int QUESTION_LOAN_FOR = 4;

    public static final int QUESTION_CAR_INITIAL_FEE = 5; // Car initial fee

    public static final int QUESTION_AMOUNT_INSTALLMENTS = 6;
    public static final int QUESTION_SALARY = 7;
    public static final int QUESTION_OFFER = 8;
    public static final int QUESTION_CALCULATE = 9;


    private Integer id;
    private String humanForm;
    private List<HumanFormProductParam> params;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "human_form_id", null));
        setHumanForm(JsonUtil.getStringFromJson(json, "human_form", null));
        if (JsonUtil.getJsonArrayFromJson(json, "params", null) != null) {
            params = new ArrayList<>();
            JSONArray paramsArray = JsonUtil.getJsonArrayFromJson(json, "params", null);
            for (int i = 0; i < paramsArray.length(); i++) {
                HumanFormProductParam param = new HumanFormProductParam();
                param.fillFromDb(paramsArray.getJSONObject(i), catalog);
                params.add(param);
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHumanForm() {
        return humanForm;
    }

    public void setHumanForm(String humanForm) {
        this.humanForm = humanForm;
    }

    public List<HumanFormProductParam> getParams() {
        return params;
    }

    public void setParams(List<HumanFormProductParam> params) {
        this.params = params;
    }
}
