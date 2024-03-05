package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class PersonProfessionOccupation {

    public static final int PROFESSION_OCCUPATION_AMA_DE_CASA = 14;
    public static final int PROFESSION_OCCUPATION_ESTUDIANTE = 56;
    public static final int PROFESSION_OCCUPATION_JUBILADO = 66;
    public static final int PROFESSION_OCCUPATION_MIEMBRO_FFAA = 75;
    public static final int PROFESSION_OCCUPATION_NO_DECLARA = 76;
    public static final int PROFESSION_OCCUPATION_OBRERO = 78;
    public static final int PROFESSION_OCCUPATION_DESEMPLEADO = 219;
    public static final int PROFESSION_OCCUPATION_EMPLEADO = 220;
    public static final int PROFESSION_OCCUPATION_EMPLEADOR = 221;
    public static final int PROFESSION_OCCUPATION_TRABAJADOR_DEL_HOGAR = 222;
    public static final int PROFESSION_OCCUPATION_TRABAJADOR_INDEPENDIENTE = 223;

    private Integer id;
    private String occupation;
    private Boolean active;
    private String textInt;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json , "profession_occupation_id", null));
        setOccupation(JsonUtil.getStringFromJson(json , "profession_occupation", null));
        setActive(JsonUtil.getBooleanFromJson(json , "is_active", false));
        setTextInt(JsonUtil.getStringFromJson(json , "text_int", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getTextInt() {
        return textInt;
    }

    public void setTextInt(String textInt) {
        this.textInt = textInt;
    }
}
