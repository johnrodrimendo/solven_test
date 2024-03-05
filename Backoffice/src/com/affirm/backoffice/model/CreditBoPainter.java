package com.affirm.backoffice.model;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.CreditStatus;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.LoanApplicationUserFiles;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class CreditBoPainter extends Credit implements IPaginationWrapperElement {

    public static final int SITUATION_NA = 0;
    public static final int SITUATION_FINALIZADO = 1;
    public static final int SITUATION_AL_DIA = 2;
    public static final int SITUATION_POR_VENCER = 3;
    public static final int SITUATION_VENCIDO = 4;

    private boolean showExpanded = false; //Shows expanded the tab of the credit
    private LoanApplicationUserFiles userFilesObjectList;
    private RecognitionResultsPainter recognition;
    private PersonInteractionPainter interactions;
    private LoanApplicationBoPainter loanApplication;
    private EntityProductParams entityProductParams;
    private Boolean isBranded;
    private Double loanToValue;
    private Double nosisCommitment;
    private Double bcraIndebtedness;
    private Double estimateIncome;
    private Double dti;
    private Double htAmount;
    private Integer htInstallments;
    private Double htEffectiveAnnualRate;
    private String observedHexColor;

    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        setBranded(JsonUtil.getBooleanFromJson(json, "is_branded", false));
        if (JsonUtil.getDoubleFromJson(json, "guaranteed_vehicle_price", null) != null)
            setLoanToValue((getAmount() * 100) / JsonUtil.getDoubleFromJson(json, "guaranteed_vehicle_price", null));
        setHtAmount(JsonUtil.getDoubleFromJson(json, "ht_amount", null));
        setHtInstallments(JsonUtil.getIntFromJson(json, "ht_installments", null));
        setHtEffectiveAnnualRate(JsonUtil.getDoubleFromJson(json, "ht_effective_annual_rate", null));
        if (JsonUtil.getStringFromJson(json, "line_color", null) != null)
            setObservedHexColor(JsonUtil.getStringFromJson(json, "line_color", null));
    }

    public boolean isShowExpanded() {
        return showExpanded;
    }

    public void setShowExpanded(boolean showExpanded) {
        this.showExpanded = showExpanded;
    }

    public void setBackofficeAnalyst(String backofficeAnalyst) {
        if (backofficeAnalyst != null && !backofficeAnalyst.isEmpty())
            super.setBackofficeAnalyst(backofficeAnalyst);
        else
            super.setBackofficeAnalyst("-");
    }

    public String getTranche() {
        //(Al día, 1 a 8, 9 a 30, 31 a 60, 61 a 90, 91 a 120, 121 a 180, Más de 180)
        if (getDaysInArrears() <= 0) {
            return "-";
        } else if (1 <= getDaysInArrears() && getDaysInArrears() <= 8) {
            return "1 - 8";
        } else if (9 <= getDaysInArrears() && getDaysInArrears() <= 30) {
            return "9 - 30";
        } else if (31 <= getDaysInArrears() && getDaysInArrears() <= 60) {
            return "31 - 60";
        } else if (61 <= getDaysInArrears() && getDaysInArrears() <= 90) {
            return "61 - 90";
        } else if (91 <= getDaysInArrears() && getDaysInArrears() <= 120) {
            return "91 - 120";
        } else if (121 <= getDaysInArrears() && getDaysInArrears() <= 180) {
            return "121 - 180";
        } else {
            return "  180+";
        }
    }

    public int getSituation() {
        if (getStatus().getId() == CreditStatus.CANCELED) {
            return SITUATION_FINALIZADO;
        } else if (getNeverManageCollection()) {
            return SITUATION_NA;
        } else if (getDaysInArrears() <= 0 && !getExpiring()) {
            return SITUATION_AL_DIA;
        } else if (getExpiring()) {
            return SITUATION_POR_VENCER;
        } else if (getDaysInArrears() > 0 && !getExpiring()) {
            return SITUATION_VENCIDO;
        }
        return -1;
    }

    public LoanApplicationUserFiles getUserFilesObjectList() {
        return userFilesObjectList;
    }

    public void setUserFilesObjectList(LoanApplicationUserFiles userFilesObjectList) {
        this.userFilesObjectList = userFilesObjectList;
    }

    public RecognitionResultsPainter getRecognition() {
        return recognition;
    }

    public void setRecognition(RecognitionResultsPainter recognition) {
        this.recognition = recognition;
    }

    public PersonInteractionPainter getInteractions() {
        return interactions;
    }

    public void setInteractions(PersonInteractionPainter interactions) {
        this.interactions = interactions;
    }

    public LoanApplicationBoPainter getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplicationBoPainter loanApplication) {
        this.loanApplication = loanApplication;
    }

    public EntityProductParams getEntityProductParams() {
        return entityProductParams;
    }

    public void setEntityProductParams(EntityProductParams entityProductParams) {
        this.entityProductParams = entityProductParams;
    }

    public String isBranded() {
        return isBranded ? "Brandeada" : "Marketplace";
    }

    public Boolean getBranded() {
        return isBranded;
    }

    public void setBranded(Boolean branded) {
        isBranded = branded;
    }

    public Double getLoanToValue() {
        return loanToValue;
    }

    public void setLoanToValue(Double loaToValue) {
        this.loanToValue = loaToValue;
    }

    public Double getNosisCommitment() {
        return nosisCommitment;
    }

    public void setNosisCommitment(Double nosisCommitment) {
        this.nosisCommitment = nosisCommitment;
    }

    public Double getBcraIndebtedness() {
        return bcraIndebtedness;
    }

    public void setBcraIndebtedness(Double bcraIndebtedness) {
        this.bcraIndebtedness = bcraIndebtedness;
    }

    public Double getEstimateIncome() {
        return estimateIncome;
    }

    public void setEstimateIncome(Double estimateIncome) {
        this.estimateIncome = estimateIncome;
    }

    public Double getDti() {
        return dti;
    }

    public void setDti(Double dti) {
        this.dti = dti;
    }

    public Double getHtAmount() {
        return htAmount;
    }

    public void setHtAmount(Double htAmount) {
        this.htAmount = htAmount;
    }

    public Integer getHtInstallments() {
        return htInstallments;
    }

    public void setHtInstallments(Integer htInstallments) {
        this.htInstallments = htInstallments;
    }

    public Double getHtEffectiveAnnualRate() {
        return htEffectiveAnnualRate;
    }

    public void setHtEffectiveAnnualRate(Double htEffectiveAnnualRate) {
        this.htEffectiveAnnualRate = htEffectiveAnnualRate;
    }

    public String getObservedHexColor() {
        return observedHexColor;
    }

    public void setObservedHexColor(String observedHexColor) {
        this.observedHexColor = observedHexColor;
    }
}
