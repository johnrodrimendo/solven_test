package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.io.Serializable;
import java.util.Locale;

public class OfferRejectionReason implements Serializable {
    private Integer id;
    private InteractionContent interactionContent;
    private String reason;
    private Boolean active;
    private Boolean physicalProcess;
    private ProductCategory productCategory;

    public void fillFromDb(JSONObject json, MessageSource messageSource, CatalogService catalogService, Locale locale) throws Exception{
        setId(JsonUtil.getIntFromJson(json, "offer_rejection_reason_id", null));
        if(JsonUtil.getStringFromJson(json, "offer_rejection_reason", null) != null)
            setReason(messageSource.getMessage(JsonUtil.getStringFromJson(json, "offer_rejection_reason", null), null, locale));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", true));
        setPhysicalProcess(JsonUtil.getBooleanFromJson(json, "is_physical_process", false));
        if(JsonUtil.getIntFromJson(json, "interaction_content_id", null) != null){
            setInteractionContent(catalogService.getInteractionContents().stream().filter(it -> it.getId() == json.getInt("interaction_content_id")).findFirst().orElse(null));
        }
        if(JsonUtil.getIntFromJson(json, "product_category_id", null) != null){
           setProductCategory(catalogService.getCatalogById(ProductCategory.class, (Object) JsonUtil.getIntFromJson(json, "product_category_id", null), locale));
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getReason() { return reason; }

    public void setReason(String reason) { this.reason = reason; }

    public Boolean getActive() { return active; }

    public void setActive(Boolean active) { this.active = active; }

    public Boolean getPhysicalProcess() { return physicalProcess; }

    public void setPhysicalProcess(Boolean physicalProcess) { this.physicalProcess = physicalProcess; }

    public InteractionContent getInteractionContent() {
        return interactionContent;
    }

    public void setInteractionContent(InteractionContent interactionContent) {
        this.interactionContent = interactionContent;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }
}
