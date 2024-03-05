/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.HelpMessage;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
public class LoanApplicationPreliminaryEvaluation implements Serializable {

    private Integer id;
    private Integer loanApplicationId;
    private Integer entityId;
    private Entity entity;
    private Date evaluationDate;
    private Date evaluationExpirationDate;
    private String hardFilterMessageKey;
    private String hardFilterMessage;
    private Boolean approved;
    private Character status;
    private HelpMessage helpMessage;
    private Product product;
    private String parameter;
    private HardFilter hardFilter;
    private Integer entityProductParameterId;
    private List<Integer> queryBots;
    private Integer retries;
    private Integer rccCodMes;
    private Integer employerId;
    private List<PreliminaryHardFilter> preliminaryHardFilters;
    private Integer defaultEvaluationHardFilterId;
    private Boolean runDefaultEvaluation;
    private Boolean evaluationUpdatesStatus;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "preliminary_evaluation_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setEvaluationDate(JsonUtil.getPostgresDateFromJson(json, "preliminary_evaluation_date", null));
        setEvaluationExpirationDate(JsonUtil.getPostgresDateFromJson(json, "preliminary_evaluation_expiration_date", null));
        setHardFilterMessageKey(JsonUtil.getStringFromJson(json, "hard_filter_message", null));
        if(getHardFilterMessageKey() != null)
            setHardFilterMessage(catalog.getMessageSource().getMessage(getHardFilterMessageKey(), null, locale));
        setApproved(JsonUtil.getBooleanFromJson(json, "is_approved", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        if (JsonUtil.getIntFromJson(json, "help_message_id", null) != null) {
            setHelpMessage(catalog.getHelpMessage(JsonUtil.getIntFromJson(json, "help_message_id", null), locale));
        }
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        }
        setParameter(JsonUtil.getStringFromJson(json, "parameter", null));
        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        if(JsonUtil.getIntFromJson(json, "hard_filter_id", null) != null)
            setHardFilter(catalog.getHardFilterById(JsonUtil.getIntFromJson(json, "hard_filter_id", null)));
        if (JsonUtil.getJsonArrayFromJson(json, "js_preliminary_evaluation_bots", null) != null) {
            setQueryBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_preliminary_evaluation_bots", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setRetries(JsonUtil.getIntFromJson(json, "retries", null));
        setRccCodMes(JsonUtil.getIntFromJson(json, "rcc_cod_mes", null));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
        preliminaryHardFilters = new ArrayList<>();
        if (JsonUtil.getJsonArrayFromJson(json, "js_hard_filters", null) != null) {
            JSONArray arrayHardFilters = JsonUtil.getJsonArrayFromJson(json, "js_hard_filters", null);
            for(int i=0; i<arrayHardFilters.length(); i++){
                PreliminaryHardFilter filter = new PreliminaryHardFilter();
                filter.fillFromDb(arrayHardFilters.getJSONObject(i), catalog, locale);
                preliminaryHardFilters.add(filter);
            }
        }
        setDefaultEvaluationHardFilterId(JsonUtil.getIntFromJson(json, "default_evaluation_hard_filter_id", null));
        setRunDefaultEvaluation(JsonUtil.getBooleanFromJson(json, "run_default_evaluation", null));
        setEvaluationUpdatesStatus(JsonUtil.getBooleanFromJson(json, "evaluations_updates_status", null));
    }

    public void addQueryBot(int queryBotId) {
        if (queryBots == null)
            queryBots = new ArrayList<>();
        queryBots.add(queryBotId);
    }

    public List<PreliminaryHardFilter> getPreliminaryHardFiltersOrdered() {
        return preliminaryHardFilters.stream().sorted(Comparator.comparingInt(h -> h.getEvaluationOrder())).collect(Collectors.toList());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Date getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(Date evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public Date getEvaluationExpirationDate() {
        return evaluationExpirationDate;
    }

    public void setEvaluationExpirationDate(Date evaluationExpirationDate) {
        this.evaluationExpirationDate = evaluationExpirationDate;
    }

    public String getHardFilterMessageKey() {
        return hardFilterMessageKey;
    }

    public void setHardFilterMessageKey(String hardFilterMessageKey) {
        this.hardFilterMessageKey = hardFilterMessageKey;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getHardFilterMessage() {
        return hardFilterMessage;
    }

    public void setHardFilterMessage(String hardFilterMessage) {
        this.hardFilterMessage = hardFilterMessage;
    }

    public HelpMessage getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(HelpMessage helpMessage) {
        this.helpMessage = helpMessage;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public HardFilter getHardFilter() {
        return hardFilter;
    }

    public void setHardFilter(HardFilter hardFilter) {
        this.hardFilter = hardFilter;
    }

    public List<Integer> getQueryBots() {
        return queryBots;
    }

    public void setQueryBots(List<Integer> queryBots) {
        this.queryBots = queryBots;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getRccCodMes() {
        return rccCodMes;
    }

    public void setRccCodMes(Integer rccCodMes) {
        this.rccCodMes = rccCodMes;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public List<PreliminaryHardFilter> getPreliminaryHardFilters() {
        return preliminaryHardFilters;
    }

    public void setPreliminaryHardFilters(List<PreliminaryHardFilter> preliminaryHardFilters) {
        this.preliminaryHardFilters = preliminaryHardFilters;
    }

    public Integer getDefaultEvaluationHardFilterId() {
        return defaultEvaluationHardFilterId;
    }

    public void setDefaultEvaluationHardFilterId(Integer defaultEvaluationHardFilterId) {
        this.defaultEvaluationHardFilterId = defaultEvaluationHardFilterId;
    }

    public Boolean getRunDefaultEvaluation() {
        return runDefaultEvaluation;
    }

    public void setRunDefaultEvaluation(Boolean runDefaultEvaluation) {
        this.runDefaultEvaluation = runDefaultEvaluation;
    }

    public Boolean getEvaluationUpdatesStatus() {
        return evaluationUpdatesStatus;
    }

    public void setEvaluationUpdatesStatus(Boolean evaluationUpdatesStatus) {
        this.evaluationUpdatesStatus = evaluationUpdatesStatus;
    }
}
