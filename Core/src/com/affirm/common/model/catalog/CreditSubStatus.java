package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by jrodriguez on 13/06/16.
 */
public class CreditSubStatus implements Serializable {

    public static final int ACCESO_WAITING_FOR_CAR_DEALERSHIP = 1;
    public static final int ACCESO_SCHEDULE_SIGNATURE = 2;
    public static final int ACCESO_PHYSICAL_SIGNATURE = 3;
    public static final int PENDING_ENTIY_GENERATION = 4;
    public static final int PENDING_ENTIY_DISBURSEMENT = 5;
    public static final int PENDING_ENTIY_LOAD = 7;
    public static final int ACCESO_WAITING_FOR_APPOINTMENT = 8;
    public static final int ACCESO_APPOINTMENT_REGISTERED = 9;
    public static final int AELU_PENDING_PRELIMINARY_DOCUMENTATION = 10;
    public static final int AELU_PENDING_INTERNAL_DISBURSEMENT = 11;
    public static final int PENDING_FINAL_DOCUMENTATION = 12;
    public static final int AELU_PENDING_PROMISORY_NOTE = 13;

    private Integer id;
    private Integer entityId;
    private Integer productId;
    private String subStatus;
    private String messageKey;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "credit_sub_status_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setMessageKey(JsonUtil.getStringFromJson(json, "text_int", null));
        setSubStatus(catalogService.getMessageSource().getMessage(getMessageKey(), null, locale));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
