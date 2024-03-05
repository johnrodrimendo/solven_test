package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jrodriguez
 */
public class LoanApplicationReason implements Serializable {

    public static final int CONSOLIDAR_CREDITOS = 14;
    public static final int ADELANTO = 17;
    public static final int CONVENIO = 18;
    public static final int AUTO = 3;

    private Integer id;
    private String reason;
    private Integer reasonOrder;
    private String mailingReplacement1;
    private String mailingReplacement2;
    private List<Integer> productIds;
    private Integer reasonCategoryId;
    private String reasonCategory;
    private boolean visible;
    private String textInt;
    private boolean messengerShown;
    private String image;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "loan_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "loan_reason", null));
        setReasonOrder(JsonUtil.getIntFromJson(json, "reason_order", null));
        setMailingReplacement1(JsonUtil.getStringFromJson(json, "mailing_replacement_1", null));
        setMailingReplacement2(JsonUtil.getStringFromJson(json, "mailing_replacement_2", null));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_product_id", null) != null) {
            JSONArray arrayProducts = JsonUtil.getJsonArrayFromJson(json, "ar_product_id", null);
            productIds = new ArrayList<>();
            for (int i = 0; i < arrayProducts.length(); i++)
                productIds.add(arrayProducts.getInt(i));
        }
        setReasonCategoryId(JsonUtil.getIntFromJson(json, "reason_category_id", null));
        setReasonCategory(JsonUtil.getStringFromJson(json, "reason_category", null));
        setVisible(JsonUtil.getBooleanFromJson(json, "is_visible", null));
        setTextInt(JsonUtil.getStringFromJson(json, "text_int", null));
        setMessengerShown(JsonUtil.getBooleanFromJson(json, "messenger_shown", null));
        setImage(JsonUtil.getStringFromJson(json, "image", null));
    }

    public boolean containsProduct(int productId) {
        return getProductIds() != null && getProductIds().stream().anyMatch(p -> p == productId);
    }

    public Integer getDefaultProductId() {
        return getProductIds() != null && !getProductIds().isEmpty() ? getProductIds().get(0) : null;
    }

    public LoanApplicationReason() {

    }

    public LoanApplicationReason(String reason, Integer id) {
        this.reason = reason;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getReasonOrder() {
        return reasonOrder;
    }

    public void setReasonOrder(Integer reasonOrder) {
        this.reasonOrder = reasonOrder;
    }

    public String getMailingReplacement1() {
        return mailingReplacement1;
    }

    public void setMailingReplacement1(String mailingReplacement1) {
        this.mailingReplacement1 = mailingReplacement1;
    }

    public String getMailingReplacement2() {
        return mailingReplacement2;
    }

    public void setMailingReplacement2(String mailingReplacement2) {
        this.mailingReplacement2 = mailingReplacement2;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productId) {
        this.productIds = productId;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Integer getReasonCategoryId() {
        return reasonCategoryId;
    }

    public void setReasonCategoryId(Integer reasonCategoryId) {
        this.reasonCategoryId = reasonCategoryId;
    }

    public String getReasonCategory() {
        return reasonCategory;
    }

    public void setReasonCategory(String reasonCategory) {
        this.reasonCategory = reasonCategory;
    }

    public String getTextInt() {
        return textInt;
    }

    public void setTextInt(String textInt) {
        this.textInt = textInt;
    }

    public boolean isMessengerShown() {
        return messengerShown;
    }

    public void setMessengerShown(boolean showMessenger) {
        this.messengerShown = showMessenger;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
