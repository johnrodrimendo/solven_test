package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.CreditRejectionReason;
import com.affirm.common.model.catalog.OfferRejectionReason;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;


public class EntityAllRejectionReasons implements Serializable {

    private List<HardFilter> hardFilters;
    private List<Policy> policys;
    private List<OfferRejectionReason> offerRejectionReasons;
    private List<CreditRejectionReason> creditRejectionReasons;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception{

        if (JsonUtil.getJsonArrayFromJson(json, "hard_filter_ids", null) != null) {
            hardFilters = new ArrayList<>();
            JSONArray hardFilterIdArray = JsonUtil.getJsonArrayFromJson(json, "hard_filter_ids", null);
            for(int i=0; i<hardFilterIdArray.length(); i++){
                hardFilters.add(catalog.getHardFilterById(hardFilterIdArray.getInt(i)));
            }
        }

        if (JsonUtil.getJsonArrayFromJson(json, "policy_ids", null) != null) {
            policys = new ArrayList<>();
            JSONArray policyIdArray = JsonUtil.getJsonArrayFromJson(json, "policy_ids", null);
            for(int i=0; i<policyIdArray.length(); i++){
                policys.add(catalog.getPolicyById(policyIdArray.getInt(i)));
            }
        }

        if (JsonUtil.getJsonArrayFromJson(json, "offer_rejection_reason_ids", null) != null) {
            offerRejectionReasons = new ArrayList<>();
            JSONArray OfferRejectionReasonArray = JsonUtil.getJsonArrayFromJson(json, "offer_rejection_reason_ids", null);
            for(int i=0; i<OfferRejectionReasonArray.length(); i++){
                offerRejectionReasons.add(catalog.getOfferRejectionReason(OfferRejectionReasonArray.getInt(i)));
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "credit_rejection_reason_ids", null) != null) {
            creditRejectionReasons = new ArrayList<>();
            JSONArray creditRejectionReasonArray = JsonUtil.getJsonArrayFromJson(json, "credit_rejection_reason_ids", null);
            for(int i=0; i<creditRejectionReasonArray.length(); i++){
                creditRejectionReasons.add(catalog.getCreditRejectionReason(creditRejectionReasonArray.getInt(i)));
            }
        }

    }


    public List<HardFilter> getHardFilters() {
        return hardFilters;
    }

    public void setHardFilters(List<HardFilter> hardFilters) {
        this.hardFilters = hardFilters;
    }

    public List<Policy> getPolicys() {
        return policys;
    }

    public void setPolicys(List<Policy> policys) {
        this.policys = policys;
    }

    public List<OfferRejectionReason> getOfferRejectionReasons() {
        return offerRejectionReasons;
    }

    public void setOfferRejectionReasons(List<OfferRejectionReason> offerRejectionReasons) {
        this.offerRejectionReasons = offerRejectionReasons;
    }

    public List<CreditRejectionReason> getCreditRejectionReasons() {
        return creditRejectionReasons;
    }

    public void setCreditRejectionReasons(List<CreditRejectionReason> creditRejectionReasons) {
        this.creditRejectionReasons = creditRejectionReasons;
    }
}
