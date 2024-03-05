package com.affirm.aws;

import com.affirm.common.model.transactional.RecognitionResult;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Created by sTbn on 15/08/16.
 */
public class RecognitionResultsPainter {

    private Integer personId;
    private Integer loanApplicationId;
    private String loanApplicationcode;
    private Integer creditId;
    private String creditCode;
    private List<RecognitionResult> results;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setLoanApplicationcode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        if (JsonUtil.getJsonArrayFromJson(json, "rekognition", null) != null) {
            results = new ArrayList<>();
            JSONArray arrayResults = JsonUtil.getJsonArrayFromJson(json, "rekognition", null);
            for (int i = 0; i < arrayResults.length(); i++) {
                RecognitionResult recognitionResult = new RecognitionResult();
                recognitionResult.fillFromDb(arrayResults.getJSONObject(i), catalog, locale);
                results.add(recognitionResult);
            }
        }
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getLoanApplicationcode() {
        return loanApplicationcode;
    }

    public void setLoanApplicationcode(String loanApplicationcode) {
        this.loanApplicationcode = loanApplicationcode;
    }

    public List<RecognitionResult> getResults() {
        return results;
    }

    public void setResults(List<RecognitionResult> results) {
        this.results = results;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public RecognitionResult getLastRekognitionResult(){
        if(results != null && !results.isEmpty()) {
            results.sort(Comparator.comparing(RecognitionResult::getProcessDate));
            return results.get(results.size() -1 );
        }
        return null;
    }
}
