package com.affirm.backoffice.model;

import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by miberico on 18/07/17.
 */
public class SelfEvaluationBoPainter implements IPaginationWrapperElement {

    private Integer rowNumber;
    private Integer id;
    private Integer personId;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String personName;
    private String personFirstSurame;
    private String personLastSurname;
    private LoanApplicationReason reason;
    private CreditUsage ussage;
    private Integer installments;
    private Double amount;
    private Double fixedGrossIncome;
    private Integer score;
    private Date registerDate;
    private Integer currentProcessQuestion;
    private Integer[] processQuestionSequence;
    private Double downPayment;
    private Date expirationDate;
    private String selfEvaluationStatus;
    private Agent agent;
    private CountryParam country;

    public SelfEvaluationBoPainter() {
    }

    @Override
    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        setRowNumber(JsonUtil.getIntFromJson(json, "row_number", null));
        setId(JsonUtil.getIntFromJson(json, "self_evaluation_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstSurame(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        Integer reason = JsonUtil.getIntFromJson(json, "loan_reason_id", null);
        if(reason != null ) setReason(catalog.getLoanApplicationReason(locale, reason));
        Integer ussageid = JsonUtil.getIntFromJson(json, "ussage_id", null);
        if (ussageid != null) setUssage(catalog.getCreditUsage(locale, ussageid));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setScore(JsonUtil.getIntFromJson(json, "score", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        Integer processQuestion = JsonUtil.getIntFromJson(json, "current_process_question_id", null);
        if(processQuestion != null) setCurrentProcessQuestion(processQuestion);
        JSONArray processSequence = JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null);
        if(processSequence != null && processSequence.length() > 0) setProcessQuestionSequence(toStringArray(processSequence));
        Double downPayment = JsonUtil.getDoubleFromJson(json, "down_payment", null);
        if (downPayment != null) setDownPayment(downPayment);
        setExpirationDate(JsonUtil.getPostgresDateFromJson(json, "expiration_date", null));
        setSelfEvaluationStatus(JsonUtil.getStringFromJson(json, "self_evaluation_status", null));
        Integer agentId = JsonUtil.getIntFromJson(json, "form_assistant_id", null);
        if(agentId != null) setAgent(catalog.getAgent(JsonUtil.getIntFromJson(json, "form_assistant_id", null)));
        setCountry(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
    }

    private static Integer[] toStringArray(JSONArray array) {
        if(array==null) return null;
        Integer[] arr = new Integer[array.length()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = array.optInt(i);
        }
        return arr;
    }


    public String getFullName(){
        String fullName = "";
        if(personName != null && !personName.isEmpty())
            fullName = fullName + personName.concat(" ");
        if(personFirstSurame != null && !personFirstSurame.isEmpty())
            fullName = fullName + personFirstSurame.concat(" ");
        if(personLastSurname != null && !personLastSurname.isEmpty())
            fullName = fullName + personLastSurname;
        return fullName;
    }

    public String getLoanApplicationAmmount(UtilService utilService) {
        String loanApplicationAmount = utilService.integerMoneyFormat(amount);
        if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "m";
        } else if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "d";
        }
        return loanApplicationAmount;
    }

    public String getLoanApplicationAmmount(UtilService utilService, String symbol, String separator) {
        String loanApplicationAmount = utilService.integerMoneyFormat(amount, symbol, separator);
        if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "m";
        } else if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "d";
        }
        return loanApplicationAmount;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoanApplicationReason getReason() {
        return reason;
    }

    public void setReason(LoanApplicationReason reason) {
        this.reason = reason;
    }

    public CreditUsage getUssage() {
        return ussage;
    }

    public void setUssage(CreditUsage ussage) {
        this.ussage = ussage;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getCurrentProcessQuestion() {
        return currentProcessQuestion;
    }

    public void setCurrentProcessQuestion(Integer currentProcessQuestion) {
        this.currentProcessQuestion = currentProcessQuestion;
    }

    public Double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSelfEvaluationStatus() {
        return selfEvaluationStatus;
    }

    public void setSelfEvaluationStatus(String selfEvaluationStatus) {
        this.selfEvaluationStatus = selfEvaluationStatus;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonFirstSurame() {
        return personFirstSurame;
    }

    public void setPersonFirstSurame(String personFirstSurame) {
        this.personFirstSurame = personFirstSurame;
    }

    public String getPersonLastSurname() {
        return personLastSurname;
    }

    public void setPersonLastSurname(String personLastSurname) {
        this.personLastSurname = personLastSurname;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer[] getProcessQuestionSequence() {
        return processQuestionSequence;
    }

    public void setProcessQuestionSequence(Integer[] processQuestionSequence) {
        this.processQuestionSequence = processQuestionSequence;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }
}
