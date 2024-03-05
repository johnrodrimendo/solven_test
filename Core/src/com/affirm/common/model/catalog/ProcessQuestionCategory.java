package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 08/11/16.
 */
public class ProcessQuestionCategory implements Serializable {

    public static final int PRE_INFORMATION = 1;
    public static final int PERSONAL_INFORMATION = 2;
    public static final int INCOME = 3;
    public static final int OFFER = 4;
    public static final int VERIFICATION = 5;
    public static final int RESULT = 6;
    public static final int WAITING_APPROVAL = 7;
    public static final int EVALUATION = 8;
    public static final int ADDRESS = 9;
    public static final int COMPROMISE = 10;
    public static final int COMPLETED = 11;

    public static String getKeyAnalyticsPage(int categoryId) {
        switch (categoryId) {
            case PRE_INFORMATION:
                return "pre-registro";
            case PERSONAL_INFORMATION:
                return "datos-personales";
            case INCOME:
                return "ingresos";
            case OFFER:
                return "ofertas";
            case VERIFICATION:
                return "verificacion";
            case WAITING_APPROVAL:
                return "en-evaluacion";
        }
        return null;
    }

    private Integer id;
    private String category;

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "process_questions_category_id", null));
        setCategory(JsonUtil.getStringFromJson(json, "process_questions_category", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
