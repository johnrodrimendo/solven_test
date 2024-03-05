
/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */
public class ApprovalValidation implements Serializable {

    public static final int VERIF_TELEFONICA = 1;
    public static final int VERIF_DOMICILIARIA = 2;
    public static final int ALERTAS_FRAUDE = 3;
    public static final int IDENTIDAD = 4;
    public static final int CCI = 5;
    public static final int VERIF_CORREO_ELECTRONICO = 6;
    public static final int LIMITE_DE_MONTO = 7;

    private Integer id;
    private String name;
    private Date registerDate;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "approval_validation_id", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
