package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class CreditEntityExtranetPainter extends Credit {

    private Integer ripleyFinalScheduleFileId;

    private boolean showPendingToValidateButton = false;
    private boolean showPendingRejectButton = false;
    private boolean showGeneratedDisbursementButton = false;
    private boolean showGeneratedRejectButton = false;
    private boolean showUploadRipleyFinalSchedule = false;
    private boolean showRemoveRipleyFinalSchedule = false;
    private boolean showAeluInternalDisbursementButton = false;
    private boolean showAeluUploadFinalDocumentationButton = false;
    private boolean showAccesoScheduleAppointmentButton = false;
    private boolean showAccesoReScheduleAppointmentButton = false;
    private boolean showAccesoCreateButton = false;
    private boolean showAccesoUploadFinalDocumentationButton = false;
    private boolean showPendingObservations = true;
    private JSONObject entityCustomData;
    private Integer entityUserId;
    private EntityExtranetUser entityExtranetUser;

    private DisggregatedAddress homeAddress;
    private DisggregatedAddress workplaceAddress;
    private String deliveryPoint;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String agencyName;
    private Boolean hasFiles;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        setRipleyFinalScheduleFileId(JsonUtil.getIntFromJson(json, "user_file_id_39", null));
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", new JSONObject()));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        if(JsonUtil.getJsonObjectFromJson(json, "home_address", null) != null){
            setHomeAddress(new DisggregatedAddress());
            getHomeAddress().fillFromDb(JsonUtil.getJsonObjectFromJson(json, "home_address", null), catalog);
        }
        if(JsonUtil.getJsonObjectFromJson(json, "workplace_address", null) != null){
            setWorkplaceAddress(new DisggregatedAddress());
            getWorkplaceAddress().fillFromDb(JsonUtil.getJsonObjectFromJson(json, "workplace_address", null), catalog);
        }
        setDeliveryPoint(getEntityCustomDataPuntoEntrega());
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        if(this.entityCustomData != null) setAgencyName(JsonUtil.getStringFromJson(this.entityCustomData , "agencyName", null));
        setHasFiles(JsonUtil.getBooleanFromJson(json , "has_files", false));
    }

    public String getEntityCustomDataBancoDelSolInternalStatus() {
        if (entityCustomData == null)
            return "";

        return JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_INTERNAL_CREDIT_STATUS.getKey(), "");
    }

    public String getEntityCustomDataPuntoEntrega() {
        if (entityCustomData == null)
            return "";

        return JsonUtil.getStringFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANBIF_DIRECCION_ENTREGA.getKey(), "");
    }

    public String getBanbifBaseDataAsString(String dataKey){
        if(getEntityProductParameterId() != null && getEntityProductParameterId().intValue() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA && dataKey != null && dataKey.equalsIgnoreCase("plastico")){
            return BanbifPreApprovedBase.BANBIF_ZERO_MEMBERSHIP_CARD;
        }
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
            JSONObject banbifBase = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey());
            return JsonUtil.getStringFromJson(banbifBase, dataKey, null);
        }
        return null;
    }

    public String getBanbifBaseDataWelcomeBonusAsString(){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
            BanbifPreApprovedBase preApprovedBase = new Gson().fromJson(entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey()).toString(),BanbifPreApprovedBase.class);
            return preApprovedBase.getBonoBienvenida().toString();
        }
        return "0";
    }

    public Integer getBanbifBaseDataAsInteger(String dataKey){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
            JSONObject banbifBase = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey());
            return JsonUtil.getIntFromJson(banbifBase, dataKey, null);
        }
        return null;
    }

    public Double getBanbifBaseDataAsDouble(String dataKey){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
            JSONObject banbifBase = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey());
            return JsonUtil.getDoubleFromJson(banbifBase, dataKey, null);
        }
        return null;
    }

    public Integer getRipleyFinalScheduleFileId() {
        return ripleyFinalScheduleFileId;
    }

    public void setRipleyFinalScheduleFileId(Integer ripleyFinalScheduleFileId) {
        this.ripleyFinalScheduleFileId = ripleyFinalScheduleFileId;
    }

    public boolean isShowGeneratedDisbursementButton() {
        return showGeneratedDisbursementButton;
    }

    public void setShowGeneratedDisbursementButton(boolean showGeneratedDisbursementButton) {
        this.showGeneratedDisbursementButton = showGeneratedDisbursementButton;
    }

    public boolean isShowGeneratedRejectButton() {
        return showGeneratedRejectButton;
    }

    public void setShowGeneratedRejectButton(boolean showGeneratedRejectButton) {
        this.showGeneratedRejectButton = showGeneratedRejectButton;
    }

    public boolean isShowUploadRipleyFinalSchedule() {
        return showUploadRipleyFinalSchedule;
    }

    public void setShowUploadRipleyFinalSchedule(boolean showUploadRipleyFinalSchedule) {
        this.showUploadRipleyFinalSchedule = showUploadRipleyFinalSchedule;
    }

    public boolean isShowRemoveRipleyFinalSchedule() {
        return showRemoveRipleyFinalSchedule;
    }

    public void setShowRemoveRipleyFinalSchedule(boolean showRemoveRipleyFinalSchedule) {
        this.showRemoveRipleyFinalSchedule = showRemoveRipleyFinalSchedule;
    }

    public boolean isShowAeluInternalDisbursementButton() {
        return showAeluInternalDisbursementButton;
    }

    public void setShowAeluInternalDisbursementButton(boolean showAeluInternalDisbursementButton) {
        this.showAeluInternalDisbursementButton = showAeluInternalDisbursementButton;
    }

    public boolean isShowAeluUploadFinalDocumentationButton() {
        return showAeluUploadFinalDocumentationButton;
    }

    public void setShowAeluUploadFinalDocumentationButton(boolean showAeluUploadFinalDocumentationButton) {
        this.showAeluUploadFinalDocumentationButton = showAeluUploadFinalDocumentationButton;
    }

    public boolean isShowAccesoScheduleAppointmentButton() {
        return showAccesoScheduleAppointmentButton;
    }

    public void setShowAccesoScheduleAppointmentButton(boolean showAccesoScheduleAppointmentButton) {
        this.showAccesoScheduleAppointmentButton = showAccesoScheduleAppointmentButton;
    }

    public boolean isShowAccesoCreateButton() {
        return showAccesoCreateButton;
    }

    public void setShowAccesoCreateButton(boolean showAccesoCreateButton) {
        this.showAccesoCreateButton = showAccesoCreateButton;
    }

    public boolean isShowAccesoUploadFinalDocumentationButton() {
        return showAccesoUploadFinalDocumentationButton;
    }

    public void setShowAccesoUploadFinalDocumentationButton(boolean showAccesoUploadFinalDocumentationButton) {
        this.showAccesoUploadFinalDocumentationButton = showAccesoUploadFinalDocumentationButton;
    }

    public boolean isShowPendingToValidateButton() {
        return showPendingToValidateButton;
    }

    public void setShowPendingToValidateButton(boolean showPendingToValidateButton) {
        this.showPendingToValidateButton = showPendingToValidateButton;
    }

    public boolean isShowPendingRejectButton() {
        return showPendingRejectButton;
    }

    public void setShowPendingRejectButton(boolean showPendingRejectButton) {
        this.showPendingRejectButton = showPendingRejectButton;
    }

    public boolean isShowAccesoReScheduleAppointmentButton() {
        return showAccesoReScheduleAppointmentButton;
    }

    public void setShowAccesoReScheduleAppointmentButton(boolean showAccesoReScheduleAppointmentButton) {
        this.showAccesoReScheduleAppointmentButton = showAccesoReScheduleAppointmentButton;
    }

    public boolean isShowPendingObservations() {
        return showPendingObservations;
    }

    public void setShowPendingObservations(boolean showPendingObservations) {
        this.showPendingObservations = showPendingObservations;
    }

    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public EntityExtranetUser getEntityExtranetUser() {
        return entityExtranetUser;
    }

    public void setEntityExtranetUser(EntityExtranetUser entityExtranetUser) {
        this.entityExtranetUser = entityExtranetUser;
    }

    public DisggregatedAddress getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(DisggregatedAddress homeAddress) {
        this.homeAddress = homeAddress;
    }

    public DisggregatedAddress getWorkplaceAddress() {
        return workplaceAddress;
    }

    public void setWorkplaceAddress(DisggregatedAddress workplaceAddress) {
        this.workplaceAddress = workplaceAddress;
    }

    public String getDeliveryPoint() {
        return deliveryPoint;
    }

    public void setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public Boolean getHasFiles() {
        return hasFiles;
    }

    public void setHasFiles(Boolean hasFiles) {
        this.hasFiles = hasFiles;
    }

    public String getAgencyNameWithoutText(){
        if(agencyName == null) return agencyName;
        List<String> textToRemove = Arrays.asList("Agencia", "Agencias", "agencia", "agencia");
        String agencyNameAux = agencyName.concat("");
        for (String s : textToRemove) {
            agencyNameAux = agencyNameAux.replaceAll(s,"");
        }
        return agencyNameAux.trim();
    }

    public Boolean showDisbursementTypeColumn(){

        if(getProduct() != null && getProduct().getId() != Product.SAVINGS_ACCOUNT){
            return true;
        }
        return false;
    }
}
