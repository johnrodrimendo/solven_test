/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationAuditType implements Serializable {

    private Integer id;
    private String type;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "loan_application_audit_type_id", null));
        setType(JsonUtil.getStringFromJson(json, "loan_application_audit_type", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
