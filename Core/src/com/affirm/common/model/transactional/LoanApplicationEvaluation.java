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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */
public class LoanApplicationEvaluation implements Serializable {

    private Integer id;
    private Integer loanApplicationId;
    private Integer entityId;
    private Entity entity;
    private Integer productId;
    private Product product;
    private Date evaluationDate;
    private Date evaluationExpirationDate;
    private String policyMessageKey;
    private Boolean approved;
    private Character status;
    private String policyMessage;
    private HelpMessage helpMessage;
    private String parameter;
    private Policy policy;
    private Date rccDate;
    private Integer entityProductParameterId;
    private List<Integer> queryBots;
    private Integer retries;
    private Integer rccCodMes;
    private List<EvaluationPolicy> evaluationPolicies;
    private Boolean runDefaultEvaluation;
    private Integer defaultEvaluationPolicyId;
    private Integer step;
    private Integer employerId;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "evaluation_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setEvaluationDate(JsonUtil.getPostgresDateFromJson(json, "evaluation_date", null));
        setEvaluationExpirationDate(JsonUtil.getPostgresDateFromJson(json, "evaluation_expiration_date", null));
        setPolicyMessageKey(JsonUtil.getStringFromJson(json, "policy_message", null));
        if(getPolicyMessageKey() != null)
            setPolicyMessage(catalog.getMessageSource().getMessage(getPolicyMessageKey(), null, locale));
        setApproved(JsonUtil.getBooleanFromJson(json, "is_approved", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        if (JsonUtil.getIntFromJson(json, "help_message_id", null) != null) {
            setHelpMessage(catalog.getHelpMessage(JsonUtil.getIntFromJson(json, "help_message_id", null), locale));
        }
        setParameter(JsonUtil.getStringFromJson(json, "parameter", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        if(JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        if(JsonUtil.getIntFromJson(json, "policy_id", null) != null)
            setPolicy(catalog.getPolicyById(JsonUtil.getIntFromJson(json, "policy_id", null)));
        if (JsonUtil.getJsonArrayFromJson(json, "js_evaluation_bots", null) != null) {
            setQueryBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_evaluation_bots", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setRetries(JsonUtil.getIntFromJson(json, "retries", null));
        setRccCodMes(JsonUtil.getIntFromJson(json, "rcc_cod_mes", null));
        String rccStringDate = JsonUtil.getIntFromJson(json, "rcc_cod_mes", null).toString() + "01";
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        setRccDate(sf.parse(rccStringDate));
        evaluationPolicies = new ArrayList<>();
        if (JsonUtil.getJsonArrayFromJson(json, "js_policies", null) != null) {
            JSONArray arrayPolicies = JsonUtil.getJsonArrayFromJson(json, "js_policies", null);
            for(int i=0; i<arrayPolicies.length(); i++){
                EvaluationPolicy policy = new EvaluationPolicy();
                policy.fillFromDb(arrayPolicies.getJSONObject(i), catalog, locale);
                evaluationPolicies.add(policy);
            }
        }
        setRunDefaultEvaluation(JsonUtil.getBooleanFromJson(json, "run_default_evaluation", null));
        setDefaultEvaluationPolicyId(JsonUtil.getIntFromJson(json, "default_evaluation_policy_id", null));
        setStep(JsonUtil.getIntFromJson(json, "step", null));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
    }

    public List<EvaluationPolicy> getEvaluationPoliciesOrdered(int step) {
        return evaluationPolicies.stream().filter(p -> p.getStep() == step).sorted(Comparator.comparingInt(h -> h.getEvaluationOrder())).collect(Collectors.toList());
    }

    public void addQueryBot(int queryBotId) {
        if (queryBots == null)
            queryBots = new ArrayList<>();
        queryBots.add(queryBotId);
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

    public String getPolicyMessageKey() {
        return policyMessageKey;
    }

    public void setPolicyMessageKey(String policyMessageKey) {
        this.policyMessageKey = policyMessageKey;
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

    public String getPolicyMessage() {
        return policyMessage;
    }

    public void setPolicyMessage(String policyMessage) {
        this.policyMessage = policyMessage;
    }

    public HelpMessage getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(HelpMessage helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
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

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public Integer getRccCodMes() {
        return rccCodMes;
    }

    public void setRccCodMes(Integer rccCodMes) {
        this.rccCodMes = rccCodMes;
    }

    public List<EvaluationPolicy> getEvaluationPolicies() {
        return evaluationPolicies;
    }

    public void setEvaluationPolicies(List<EvaluationPolicy> evaluationPolicies) {
        this.evaluationPolicies = evaluationPolicies;
    }

    public Boolean getRunDefaultEvaluation() {
        return runDefaultEvaluation;
    }

    public void setRunDefaultEvaluation(Boolean runDefaultEvaluation) {
        this.runDefaultEvaluation = runDefaultEvaluation;
    }

    public Integer getDefaultEvaluationPolicyId() {
        return defaultEvaluationPolicyId;
    }

    public void setDefaultEvaluationPolicyId(Integer defaultEvaluationPolicyId) {
        this.defaultEvaluationPolicyId = defaultEvaluationPolicyId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }
}
