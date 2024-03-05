package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class EntityAcquisitionChannel {

    public static final int ORGANIZADORES_PRODUCTORES = 1;
    public static final int AGENCIAS = 2;
    public static final int TIENDAS = 3;

    private Integer entityAcquisitionChannelId;
    private Integer entityId;
    private Integer entityProductParameterId;
    private String entityAcquisitionChannel;

    public void fillFromDb(JSONObject json) {
        this.setEntityAcquisitionChannelId(JsonUtil.getIntFromJson(json, "entity_acquisition_channel_id", null));
        this.setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        this.setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        this.setEntityAcquisitionChannel(JsonUtil.getStringFromJson(json, "entity_acquisition_channel", null));
    }

    public Integer getEntityAcquisitionChannelId() {
        return entityAcquisitionChannelId;
    }

    public void setEntityAcquisitionChannelId(Integer entityAcquisitionChannelId) {
        this.entityAcquisitionChannelId = entityAcquisitionChannelId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public String getEntityAcquisitionChannel() {
        return entityAcquisitionChannel;
    }

    public void setEntityAcquisitionChannel(String entityAcquisitionChannel) {
        this.entityAcquisitionChannel = entityAcquisitionChannel;
    }
}
