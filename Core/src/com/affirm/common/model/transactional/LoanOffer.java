/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.model.AlfinGatewayVigenteOfferData;
import com.affirm.common.model.BankAccountOfferData;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author jrodriguez
 */
public class LoanOffer implements Serializable {

    public static final String SIGNATURE_PIN_VALIDATED_KEY = "is_validated";
    public static final String SIGNATURE_PIN_PIN_KEY = "pin";

    private Integer id;
    private String code;
    private Double ammount;
    private Currency currency;
    private Integer installments;
    private Double installmentAmmount;
    private Double installmentAmountAvg;
    private Double effectiveAnualRate;
    private Double monthlyRate;
    private Double loanCommission;
    private Double minAmmount;
    private Double maxAmmount;
    private Integer maxInstallments;
    private Date registerDate;
    private String signatureFullName;
    private Date signatureDate;
    private Integer signatureDocType;
    private String signatureDocNumber;
    private String signatureEmail;
    private Double effectiveDailyRate;
    private Integer rccCodMes;
    private Boolean selected;
    private Boolean selectedByClient;
    private Boolean selectedByAnalyst;
    private Double commission;
    private Character commissionType;
    private Double commissionIgv;
    private Double totalCommission;
    private Integer entityId;
    private String entityCode;
    private String entityShortName;
    private String entityFullName;
    private Double effectiveAnnualCostRate;
    private Double sbsMonthlyInstallment;
    private Double sbsMonthlyInstallmentMotgage;
    private Double admissionTotalIncome;
    private Double rci;
    private Double internalRci;
    private List<OriginalSchedule> offerSchedule;
    private Integer loanOfferOrder;
    private Entity entity;
    private Double moratoriumRate;
    private Double commission2;
    private Product product;
    private Integer entityScore;
    private Double downPayment;
    private Currency downPaymentCurrency;
    private Double insurance;
    private Double carInsurance;
    private String commercialSpeech;
    private String wellcomeSpeech;
    private Double cost;
    private Employer employer;
    private Date firstDueDate;
    private Double hiddenCommission;
    private Double exchangeRate;
    private String entityClusterId;
    private Boolean selectedOffer;
    private Double stampTax;
    private Double nominalAnualRate;
    private Integer entityProductParameterId;
    private Double totalDebt;
    private Double sbsMonthlyInstallmentEfx;
    private Double dti;
    private EntityProductParams entityProductParam;
    private Double loanCapital;
    private String signaturePin;
    private Boolean signaturePinValidated;
    private Integer signaturePinTries;
    private JSONObject entityCustomData;

    public enum EntityCustomDataKeys {
        BANK_ACCOUNT_DATA("bankAccountData"),
        ALFIN_INSURANCE_PLAN("alfinInsurancePlan"),
        ALFIN_INSURANCE_COST("alfinInsuranceCost"),
        ;

        private String key;

        EntityCustomDataKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "loan_offer_id", null));
        setCode(JsonUtil.getStringFromJson(json, "loan_offer_code", null));
        setAmmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setInstallmentAmmount(JsonUtil.getDoubleFromJson(json, "installment_amount", null));
        setEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setMonthlyRate(JsonUtil.getDoubleFromJson(json, "effective_monthly_rate", null));
        setLoanCommission(JsonUtil.getDoubleFromJson(json, "loan_commission", null));
        setMinAmmount(JsonUtil.getDoubleFromJson(json, "min_amount", null));
        setMaxAmmount(JsonUtil.getDoubleFromJson(json, "max_amount", null));
        setMaxInstallments(JsonUtil.getIntFromJson(json, "max_installments", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setSignatureFullName(JsonUtil.getStringFromJson(json, "signature_full_name", null));
        setSignatureDate(JsonUtil.getPostgresDateFromJson(json, "signature_date", null));
        setSignatureDocType(JsonUtil.getIntFromJson(json, "signature_document_type_id", null));
        setSignatureDocNumber(JsonUtil.getStringFromJson(json, "signature_document_number", null));
        setSignatureEmail(JsonUtil.getStringFromJson(json, "signature_mail", null));
        setEffectiveDailyRate(JsonUtil.getDoubleFromJson(json, "effective_daily_rate", null));
        setRccCodMes(JsonUtil.getIntFromJson(json, "rcc_cod_mes", null));
        setSelected(JsonUtil.getBooleanFromJson(json, "is_selected", null));
        setSelectedByClient(JsonUtil.getBooleanFromJson(json, "is_selected_by_client", null));
        setSelectedByAnalyst(JsonUtil.getBooleanFromJson(json, "is_selected_by_analyst", null));
        setCommission(JsonUtil.getDoubleFromJson(json, "commission", null));
        setCommissionType(JsonUtil.getCharacterFromJson(json, "commission_type", null));
        setCommissionIgv(JsonUtil.getDoubleFromJson(json, "commission_igv", null));
        setTotalCommission(JsonUtil.getDoubleFromJson(json, "total_commission", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setEntityCode(JsonUtil.getStringFromJson(json, "entity_code", null));
        setEntityFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setEntityShortName(JsonUtil.getStringFromJson(json, "short_name", null));
        setEffectiveAnnualCostRate(JsonUtil.getDoubleFromJson(json, "effective_annual_cost_rate", null));
        setInstallmentAmountAvg(JsonUtil.getDoubleFromJson(json, "installment_amount_avg", null));
        setSbsMonthlyInstallment(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment", null));
        setSbsMonthlyInstallmentMotgage(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment_mortgage", null));
        setAdmissionTotalIncome(JsonUtil.getDoubleFromJson(json, "admission_total_income", null));
        setRci(JsonUtil.getDoubleFromJson(json, "rci", null));
        setInternalRci(JsonUtil.getDoubleFromJson(json, "internal_rci", null));
        setOfferSchedule(fillSchedule(JsonUtil.getJsonArrayFromJson(json, "offer_schedule", null)));
        setLoanOfferOrder(JsonUtil.getIntFromJson(json, "loan_offer_order", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null) {
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        }
        setMoratoriumRate(JsonUtil.getDoubleFromJson(json, "moratorium_rate", null));
        setCommission2(JsonUtil.getDoubleFromJson(json, "commission_2", null));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        }
        setEntityScore(JsonUtil.getIntFromJson(json, "entity_score", null));
        setDownPayment(JsonUtil.getDoubleFromJson(json, "down_payment", null));
        if (JsonUtil.getIntFromJson(json, "down_payment_currency_id", null) != null)
            setDownPaymentCurrency(catalog.getCurrency(JsonUtil.getIntFromJson(json, "down_payment_currency_id", null)));
        setInsurance(JsonUtil.getDoubleFromJson(json, "insurance", null));
        setCarInsurance(JsonUtil.getDoubleFromJson(json, "car_insurance", null));
        setCost(JsonUtil.getDoubleFromJson(json, "cost", null));
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null)
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        setFirstDueDate(JsonUtil.getPostgresDateFromJson(json, "first_due_date", null));
        setCommercialSpeech(JsonUtil.getStringFromJson(json, "commercial_speech", null));
        setWellcomeSpeech(JsonUtil.getStringFromJson(json, "welcome_speech", null));
        setHiddenCommission(JsonUtil.getDoubleFromJson(json, "hidden_commission", null));
        if (JsonUtil.getIntFromJson(json, "currency_id", null) != null) {
            setCurrency(catalog.getCurrency(JsonUtil.getIntFromJson(json, "currency_id", null)));
        }
        setExchangeRate(JsonUtil.getDoubleFromJson(json, "exchange_rate", null));
        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        if(getEntityProductParameterId() != null){
            setEntityProductParam(catalog.getEntityProductParamById(getEntityProductParameterId()));
        }
        setEntityClusterId(JsonUtil.getStringFromJson(json, "entity_cluster_id", null));
        setSelectedOffer(JsonUtil.getBooleanFromJson(json, "selected_offer", null));
        setStampTax(JsonUtil.getDoubleFromJson(json, "stamp_tax", null));
        setNominalAnualRate(JsonUtil.getDoubleFromJson(json, "nominal_annual_rate", null));
        setTotalDebt(JsonUtil.getDoubleFromJson(json, "total_debt", null));
        setSbsMonthlyInstallmentEfx(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment_efx", null));
        setDti(JsonUtil.getDoubleFromJson(json, "dti", null));
        setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        setSignaturePin(JsonUtil.getStringFromJson(json, "signature_pin", null));
        setSignaturePinValidated(JsonUtil.getBooleanFromJson(json, "signature_pin_validated", null));
        setSignaturePinTries(JsonUtil.getIntFromJson(json, "signature_pin_tries", null));
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "js_entity_custom_data", new JSONObject()));
    }

    public Double getFutureRevenue(){
        if(getCommissionType() == null
                || getEntity().getLoanNonCapitalizableCommission() == null
                || getLoanCapital() == null
                || getCommission() == null
                || getCommissionType() == null)
            return null;
        try{
            if(getCommissionType().equals('P')){
                return (getAmmount() * getCommission()) + (getLoanCapital() * getEntity().getLoanNonCapitalizableCommission());
            }else if(getCommissionType().equals('V')){
                return getCommission() + (getLoanCapital() * getEntity().getLoanNonCapitalizableCommission());
            }
        }catch (Exception ex){
            ErrorServiceImpl.onErrorStatic(ex);
        }
        return null;
    }

    public Double getTotalScheduleField(String field) {
        if (offerSchedule == null)
            return null;

        switch (field) {
            case "installmentAmount":
                return offerSchedule.stream().mapToDouble(c -> c.getInstallmentAmount()).sum();
            case "insurance":
                return offerSchedule.stream().mapToDouble(c -> c.getInsurance() != null ? c.getInsurance() : 0).sum();
            case "totalCollectionCommission":
                return offerSchedule.stream().mapToDouble(c -> c.getTotalCollectionCommission()).sum();
            case "totalInterest":
                return offerSchedule.stream().mapToDouble(c -> c.getTotalInterest()).sum();
            case "installmentCapital":
                return offerSchedule.stream().mapToDouble(c -> c.getInstallmentCapital()).sum();
            case "interest":
                return offerSchedule.stream().mapToDouble(c -> c.getInterest()).sum();
            case "interestTax":
                return offerSchedule.stream().mapToDouble(c -> c.getInterestTax() != null ? c.getInterestTax() : 0).sum();
            case "collectionCommission":
                return offerSchedule.stream().mapToDouble(c -> c.getCollectionCommission() != null ? c.getCollectionCommission() : 0).sum();
            case "collectionCommissionTax":
                return offerSchedule.stream().mapToDouble(c -> c.getCollectionCommissionTax() != null ? c.getCollectionCommissionTax() : 0).sum();
            case "remainingCapital":
                return offerSchedule.stream().mapToDouble(c -> c.getRemainingCapital() != null ? c.getRemainingCapital() : 0).sum();
            case "carInsurance":
                return offerSchedule.stream().mapToDouble(c -> c.getCarInsurance() != null ? c.getCarInsurance() : 0).sum();
            case "installment":
                return offerSchedule.stream().filter(e -> e.getInstallment() != null).mapToDouble(c -> c.getInstallment()).sum();
        }
        return null;
    }

    private List<OriginalSchedule> fillSchedule(JSONArray offerJson) throws Exception {
        if (offerJson == null)
            return null;
        List<OriginalSchedule> originalSchedule = new ArrayList<>();
        for (int i = 0; i < offerJson.length(); i++) {
            OriginalSchedule installment = new OriginalSchedule();
            installment.fillFromDb(offerJson.getJSONObject(i), OriginalSchedule.OFFER);
            originalSchedule.add(installment);
        }

        return originalSchedule;
    }

    public String getLoanOfferDescription(UtilService utilService, Integer countryCode) {
        if(ammount == null) return "";
        String loanOfferDescription = utilService.integerMoneyFormat(ammount.intValue(), currency);
        if(countryCode == CountryParam.COUNTRY_PERU)
            loanOfferDescription = loanOfferDescription + "\t@ " + installments + "m\t@ " + utilService.percentFormat(effectiveAnualRate) + "\t@ " + utilService.percentFormat(effectiveAnnualCostRate);
        else if(countryCode == CountryParam.COUNTRY_ARGENTINA)
            loanOfferDescription = loanOfferDescription + "\t@ " + installments + "m\t@ " + utilService.percentFormat(nominalAnualRate) + "\t@ " + utilService.percentFormat(effectiveAnnualCostRate);

        return loanOfferDescription;
    }

//    public String getLoanOfferDescription(UtilService utilService, String symbol, String separator) {
//        String loanOfferDescription = utilService.integerMoneyFormat(ammount.intValue(), currency);
//        if (installments != null) {
//            loanOfferDescription = loanOfferDescription + "\t@ " + installments + "m\t@ " + utilService.percentFormat(effectiveAnualRate,currency) + "\t@ " + utilService.percentFormat(effectiveAnnualCostRate,currency);
//        }
//        return loanOfferDescription;
//    }

    public Double getVehicleDownPaymentInPercentage(Double vehiclePrice) {
        if (vehiclePrice == null)
            return null;
        return (downPayment * 100.0) / vehiclePrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getInstallmentAmmount() {
        return installmentAmmount;
    }

    public void setInstallmentAmmount(Double installmentAmmount) {
        this.installmentAmmount = installmentAmmount;
    }

    public Double getEffectiveAnualRate() {
        return effectiveAnualRate;
    }

    public void setEffectiveAnualRate(Double effectiveAnualRate) {
        this.effectiveAnualRate = effectiveAnualRate;
    }

    public Double getLoanCommission() {
        return loanCommission;
    }

    public void setLoanCommission(Double loanCommission) {
        this.loanCommission = loanCommission;
    }

    public Double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(Double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getMinAmmount() {
        return minAmmount;
    }

    public void setMinAmmount(Double minAmmount) {
        this.minAmmount = minAmmount;
    }

    public Double getMaxAmmount() {
        return maxAmmount;
    }

    public void setMaxAmmount(Double maxAmmount) {
        this.maxAmmount = maxAmmount;
    }

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getSignatureFullName() {
        return signatureFullName;
    }

    public void setSignatureFullName(String signatureFullName) {
        this.signatureFullName = signatureFullName;
    }

    public Date getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(Date signatureDate) {
        this.signatureDate = signatureDate;
    }

    public Integer getSignatureDocType() {
        return signatureDocType;
    }

    public void setSignatureDocType(Integer signatureDocType) {
        this.signatureDocType = signatureDocType;
    }

    public String getSignatureDocNumber() {
        return signatureDocNumber;
    }

    public void setSignatureDocNumber(String signatureDocNumber) {
        this.signatureDocNumber = signatureDocNumber;
    }

    public String getSignatureEmail() {
        return signatureEmail;
    }

    public void setSignatureEmail(String signatureEmail) {
        this.signatureEmail = signatureEmail;
    }

    public Double getEffectiveDailyRate() {
        return effectiveDailyRate;
    }

    public void setEffectiveDailyRate(Double effectiveDailyRate) {
        this.effectiveDailyRate = effectiveDailyRate;
    }

    public Integer getRccCodMes() {
        return rccCodMes;
    }

    public void setRccCodMes(Integer rccCodMes) {
        this.rccCodMes = rccCodMes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Double getCommissionIgv() {
        return commissionIgv;
    }

    public void setCommissionIgv(Double commissionIgv) {
        this.commissionIgv = commissionIgv;
    }

    public Double getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(Double totalCommission) {
        this.totalCommission = totalCommission;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getEntityShortName() {
        return entityShortName;
    }

    public void setEntityShortName(String entityShortName) {
        this.entityShortName = entityShortName;
    }

    public String getEntityFullName() {
        return entityFullName;
    }

    public void setEntityFullName(String entityFullName) {
        this.entityFullName = entityFullName;
    }

    public Double getInstallmentAmountAvg() {
        return installmentAmountAvg;
    }

    public void setInstallmentAmountAvg(Double installmentAmountAvg) {
        this.installmentAmountAvg = installmentAmountAvg;
    }

    public Double getEffectiveAnnualCostRate() {
        return effectiveAnnualCostRate;
    }

    public void setEffectiveAnnualCostRate(Double effectiveAnnualCostRate) {
        this.effectiveAnnualCostRate = effectiveAnnualCostRate;
    }

    public Double getSbsMonthlyInstallment() {
        return sbsMonthlyInstallment;
    }

    public void setSbsMonthlyInstallment(Double sbsMonthlyInstallment) {
        this.sbsMonthlyInstallment = sbsMonthlyInstallment;
    }

    public Double getSbsMonthlyInstallmentMotgage() {
        return sbsMonthlyInstallmentMotgage;
    }

    public void setSbsMonthlyInstallmentMotgage(Double sbsMonthlyInstallmentMotgage) {
        this.sbsMonthlyInstallmentMotgage = sbsMonthlyInstallmentMotgage;
    }

    public Double getAdmissionTotalIncome() {
        return admissionTotalIncome;
    }

    public void setAdmissionTotalIncome(Double admissionTotalIncome) {
        this.admissionTotalIncome = admissionTotalIncome;
    }

    public Double getRci() {
        return rci;
    }

    public void setRci(Double rci) {
        this.rci = rci;
    }

    public List<OriginalSchedule> getOfferSchedule() {
        return offerSchedule;
    }

    public void setOfferSchedule(List<OriginalSchedule> offerSchedule) {
        this.offerSchedule = offerSchedule;
    }

    public Boolean getSelectedByClient() {
        return selectedByClient;
    }

    public void setSelectedByClient(Boolean selectedByClient) {
        this.selectedByClient = selectedByClient;
    }

    public Boolean getSelectedByAnalyst() {
        return selectedByAnalyst;
    }

    public void setSelectedByAnalyst(Boolean selectedByAnalyst) {
        this.selectedByAnalyst = selectedByAnalyst;
    }

    public Integer getLoanOfferOrder() {
        return loanOfferOrder;
    }

    public void setLoanOfferOrder(Integer loanOfferOrder) {
        this.loanOfferOrder = loanOfferOrder;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Double getMoratoriumRate() {
        return moratoriumRate;
    }

    public void setMoratoriumRate(Double moratoriumRate) {
        this.moratoriumRate = moratoriumRate;
    }

    public Double getCommission2() {
        return commission2;
    }

    public void setCommission2(Double commission2) {
        this.commission2 = commission2;
    }

    public Double getInternalRci() {
        return internalRci;
    }

    public void setInternalRci(Double internalRci) {
        this.internalRci = internalRci;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getEntityScore() {
        return entityScore;
    }

    public void setEntityScore(Integer entityScore) {
        this.entityScore = entityScore;
    }

    public Double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Currency getDownPaymentCurrency() {
        return downPaymentCurrency;
    }

    public void setDownPaymentCurrency(Currency downPaymentCurrency) {
        this.downPaymentCurrency = downPaymentCurrency;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public Double getCarInsurance() {
        return carInsurance;
    }

    public void setCarInsurance(Double carInsurance) {
        this.carInsurance = carInsurance;
    }

    @Override
    public String toString() {
        return "LoanOffer{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", ammount=" + ammount +
                ", installments=" + installments +
                ", installmentAmmount=" + installmentAmmount +
                ", installmentAmountAvg=" + installmentAmountAvg +
                ", effectiveAnualRate=" + effectiveAnualRate +
                ", monthlyRate=" + monthlyRate +
                ", loanCommission=" + loanCommission +
                ", minAmmount=" + minAmmount +
                ", maxAmmount=" + maxAmmount +
                ", maxInstallments=" + maxInstallments +
                ", registerDate=" + registerDate +
                ", signatureFullName='" + signatureFullName + '\'' +
                ", signatureDate=" + signatureDate +
                ", signatureDocType=" + signatureDocType +
                ", signatureDocNumber='" + signatureDocNumber + '\'' +
                ", signatureEmail='" + signatureEmail + '\'' +
                ", effectiveDailyRate=" + effectiveDailyRate +
                ", rccCodMes=" + rccCodMes +
                ", selected=" + selected +
                ", commission=" + commission +
                ", commissionIgv=" + commissionIgv +
                ", totalCommission=" + totalCommission +
                ", entityId=" + entityId +
                ", entityCode='" + entityCode + '\'' +
                ", entityShortName='" + entityShortName + '\'' +
                ", entityFullName='" + entityFullName + '\'' +
                ", effectiveAnnualCostRate=" + effectiveAnnualCostRate +
                ", offerSchedule=" + offerSchedule +
                '}';
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Date getFirstDueDate() {
        return firstDueDate;
    }

    public void setFirstDueDate(Date firstDueDate) {
        this.firstDueDate = firstDueDate;
    }

    public Character getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Character commissionType) {
        this.commissionType = commissionType;
    }

    public String getCommercialSpeech() {
        return commercialSpeech;
    }

    public void setCommercialSpeech(String commercialSpeech) {
        this.commercialSpeech = commercialSpeech;
    }

    public String getWellcomeSpeech() {
        return wellcomeSpeech;
    }

    public void setWellcomeSpeech(String wellcomeSpeech) {
        this.wellcomeSpeech = wellcomeSpeech;
    }

    public Double getHiddenCommission() {
        return hiddenCommission;
    }

    public void setHiddenCommission(Double hiddenCommission) {
        this.hiddenCommission = hiddenCommission;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public String getEntityClusterId() { return entityClusterId; }

    public void setEntityClusterId(String entityClusterId) { this.entityClusterId = entityClusterId; }

    public Boolean getSelectedOffer() {
        return selectedOffer;
    }

    public void setSelectedOffer(Boolean selectedOffer) {
        this.selectedOffer = selectedOffer;
    }

    public Double getStampTax() {
        return stampTax;
    }

    public void setStampTax(Double stampTax) {
        this.stampTax = stampTax;
    }

    public Double getNominalAnualRate() {
        return nominalAnualRate;
    }

    public void setNominalAnualRate(Double nominalAnualRate) {
        this.nominalAnualRate = nominalAnualRate;
    }

    public Double getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(Double totalDebt) {
        this.totalDebt = totalDebt;
    }

    public Double getSbsMonthlyInstallmentEfx() {
        return sbsMonthlyInstallmentEfx;
    }

    public void setSbsMonthlyInstallmentEfx(Double sbsMonthlyInstallmentEfx) {
        this.sbsMonthlyInstallmentEfx = sbsMonthlyInstallmentEfx;
    }

    public Double getDti() {
        return dti;
    }

    public void setDti(Double dti) {
        this.dti = dti;
    }

    public EntityProductParams getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(EntityProductParams entityProductParam) {
        this.entityProductParam = entityProductParam;
    }

    public Double getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Double loanCapital) {
        this.loanCapital = loanCapital;
    }

    public String getSignaturePin() {
        return signaturePin;
    }

    public void setSignaturePin(String signaturePin) {
        this.signaturePin = signaturePin;
    }

    public Boolean getSignaturePinValidated() {
        return signaturePinValidated;
    }

    public void setSignaturePinValidated(Boolean signaturePinValidated) {
        this.signaturePinValidated = signaturePinValidated;
    }

    public Integer getSignaturePinTries() {
        return signaturePinTries;
    }

    public void setSignaturePinTries(Integer signaturePinTries) {
        this.signaturePinTries = signaturePinTries;
    }

    public JSONObject getEntityCustomData() {
        if(entityCustomData == null) entityCustomData = new JSONObject();
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public BankAccountOfferData getBankAccountOfferData() {
        JSONObject data = getEntityCustomData().optJSONObject(LoanOffer.EntityCustomDataKeys.BANK_ACCOUNT_DATA.getKey());
        if(data != null){
            return new Gson().fromJson(data.toString(), BankAccountOfferData.class);
        }
        return null;
    }

    public Integer getAztecaGraceDays(){
        if(entityProductParameterId != null && Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA,EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(entityProductParameterId)) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(registerDate);
        cal.add(Calendar.MONTH, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateString1 = sdf.format(cal.getTime());
        String dateString2 = sdf.format(firstDueDate);

        LocalDate date1 = LocalDate.parse(dateString1, dtf);
        LocalDate date2 = LocalDate.parse(dateString2, dtf);
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return (int)daysBetween;
    }

}
