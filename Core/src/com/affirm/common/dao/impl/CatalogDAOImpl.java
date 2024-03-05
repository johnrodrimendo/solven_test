package com.affirm.common.dao.impl;

import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.FunnelStep;
import com.affirm.common.model.PreApprovedMail;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.model.SysUser;
import com.affirm.security.model.SysUserSchedule;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.*;

/**
 * @author jrodriguez
 */

@Repository("catalogDao")
@CacheConfig(cacheNames = "catalogCache", keyGenerator = "cacheKeyGenerator")
public class CatalogDAOImpl extends JsonResolverDAO implements CatalogDAO {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TranslatorDAO translatorDAO;

    @Override
    @Cacheable
    public List<LoanApplicationReason> getLoanApplicationReasons() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_loan_reason()", JSONArray.class, false);

        List<LoanApplicationReason> reasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationReason reason = new LoanApplicationReason();
            reason.fillFromDb(dbArray.getJSONObject(i));
            reasons.add(reason);
        }
        return reasons;
    }

    @Override
    @Cacheable
    public List<MaritalStatus> getMaritalStatus() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_marital_status()", JSONArray.class, false);

        List<MaritalStatus> status = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            status.add(new MaritalStatus(dbArray.getJSONObject(i).getInt("marital_status_id"),
                    dbArray.getJSONObject(i).getString("text_int")));
        }
        return status;
    }

    @Override
    @Cacheable
    public List<StreetType> getStreetTypes() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_street_type()", JSONArray.class, false);

        List<StreetType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            types.add(new StreetType(dbArray.getJSONObject(i).getInt("street_type_id"),
                    dbArray.getJSONObject(i).getString("street_type")));
        }
        return types;
    }

    @Override
    @Cacheable
    public List<ActivityType> getActivityTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_activity_type()", JSONArray.class, false);

        List<ActivityType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ActivityType type = new ActivityType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public Map<String, Department> getDepartments() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_ubigeo()", JSONArray.class, false);

        Map<String, Department> departments = new LinkedHashMap<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject ubigeo = dbArray.getJSONObject(i);

            Department department = null;
            Province province = null;
            District district = null;

            String depUbigeo = ubigeo.getString("ubigeo_id").substring(0, 2);
            String provUbigeo = ubigeo.getString("ubigeo_id").substring(2, 4);
            String distUbigeo = ubigeo.getString("ubigeo_id").substring(4, 6);

            // Department
            if (!departments.containsKey(depUbigeo)) {
                department = new Department(depUbigeo, dbArray.getJSONObject(i).getString("department"),
                        dbArray.getJSONObject(i).getString("phone_prefix"),
                        !dbArray.getJSONObject(i).isNull("inei_ubigeo_id") ? dbArray.getJSONObject(i).getString("inei_ubigeo_id").substring(0, 2) : null);
                departments.put(department.getId(), department);
            }

            // Province
            if (department == null) {
                department = departments.get(depUbigeo);
            }

            if (!department.getProvinces().containsKey(provUbigeo)) {
                province = new Province(provUbigeo, dbArray.getJSONObject(i).getString("province"), department,
                        !dbArray.getJSONObject(i).isNull("inei_ubigeo_id") ? dbArray.getJSONObject(i).getString("inei_ubigeo_id").substring(2, 4) : null);
                department.getProvinces().put(province.getId(), province);
            }

            // District
            if (province == null) {
                province = department.getProvinces().get(provUbigeo);
            }

            if (!province.getDistricts().containsKey(distUbigeo)) {
                district = new District(distUbigeo, dbArray.getJSONObject(i).getString("district"), province,
                        !dbArray.getJSONObject(i).isNull("inei_ubigeo_id") ? dbArray.getJSONObject(i).getString("inei_ubigeo_id").substring(4, 6) : null);
                province.getDistricts().put(district.getId(), district);
            }
        }
        return departments;
    }

    @Override
    @Cacheable
    public List<Bank> getBanks() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_banks()", JSONArray.class, false);

        List<Bank> banks = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Bank bank = new Bank();
            bank.fillFromDb(dbArray.getJSONObject(i), catalogService);
            banks.add(bank);
        }
        return banks;
    }

    @Override
    @Cacheable
    public List<Product> getProducts() {
        JSONArray dbArray = queryForObjectTrx("select * from product.get_min_max_amount()", JSONArray.class, false);
        return getProductFromJsonArray(dbArray);
    }

    @Override
    @Cacheable
    public List<Product> getProductsEntity() {
        JSONArray dbArray = queryForObjectTrx("select * from product.get_min_max_amount_by_entity()", JSONArray.class, false);
        return getProductFromJsonArray(dbArray);
    }

    private List<Product> getProductFromJsonArray(JSONArray dbArray) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ProductMaxMinParameter productParams = new ProductMaxMinParameter();
            productParams.fillFromDb(dbArray.getJSONObject(i), catalogService);

            Product product = products.stream().filter(p -> p.getId() == productParams.getProductId().intValue()).findFirst().orElse(null);
            if (product == null) {
                product = new Product();
                product.fillFromDb(dbArray.getJSONObject(i), catalogService);
            }

            product.getProductMaxMinParameters().add(productParams);
            products.add(product);
        }
        return products;
    }

    @Override
    @Cacheable
    public List<Nationality> getNationalities() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_nationality()", JSONArray.class, false);

        List<Nationality> nationalities = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            nationalities.add(new Nationality(dbArray.getJSONObject(i).getInt("nationality_id"), dbArray.getJSONObject(i).getString("nationality"),
                    dbArray.getJSONObject(i).getString("text_int")));
        }

        return nationalities;
    }

    @Override
    @Cacheable
    public List<Belonging> getBelongings() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_belonging()", JSONArray.class, false);

        List<Belonging> belongings = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Belonging belonging = new Belonging();
            belonging.fillFromDb(dbArray.getJSONObject(i));
            belongings.add(belonging);
        }
        return belongings;
    }

    @Override
    @Cacheable
    public List<PensionPayer> getPensionPayer() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_pension_payer()", JSONArray.class, false);

        List<PensionPayer> pensionPayers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            pensionPayers.add(new PensionPayer(dbArray.getJSONObject(i).getInt("pension_payer_id"), dbArray.getJSONObject(i).getString("pension_payer"),
                    dbArray.getJSONObject(i).getString("text_int")));
        }

        return pensionPayers;
    }

    @Override
    @Cacheable
    public List<VoucherType> getVoucherTypes() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_voucher_type()", JSONArray.class, false);

        List<VoucherType> voucherTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            voucherTypes.add(new VoucherType(dbArray.getJSONObject(i).getInt("voucher_type_id"), dbArray.getJSONObject(i).getString("voucher_type"),
                    dbArray.getJSONObject(i).getString("text_int")));
        }

        return voucherTypes;
    }

    @Override
    @Cacheable
    public List<LoanApplicationStatus> getLoanApplicationStatuses() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_application_status()", JSONArray.class, false);

        List<LoanApplicationStatus> statuses = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            statuses.add(new LoanApplicationStatus(
                    dbArray.getJSONObject(i).getInt("application_status_id"),
                    dbArray.getJSONObject(i).getString("text_int")));
        }

        return statuses;
    }

    @Override
    @Cacheable
    public List<CreditStatus> getCreditStatuses() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_credit_status()", JSONArray.class, false);

        List<CreditStatus> statuses = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            statuses.add(new CreditStatus(
                    dbArray.getJSONObject(i).getInt("credit_status_id"),
                    dbArray.getJSONObject(i).getString("text_int")));
        }

        return statuses;
    }

    @Override
    @Cacheable
    public List<UserFileType> getUserFileTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_ct_filetype()", JSONArray.class, false);

        List<UserFileType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            types.add(new UserFileType(
                    dbArray.getJSONObject(i).optInt("filetype_id"),
                    dbArray.getJSONObject(i).optString("filetype"),
                    dbArray.getJSONObject(i).optInt("order_id"),
                    dbArray.getJSONObject(i).optString("thumbnail"),
                    dbArray.getJSONObject(i).optString("tooltip_text_int")));
        }
        return types;
    }

    @Override
    @Cacheable
    public List<Cluster> getClusters() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from product.get_ct_cluster()", JSONArray.class, false);

        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Cluster cluster = new Cluster();
            cluster.fillFromDb(dbArray.getJSONObject(i));
            clusters.add(cluster);
        }
        return clusters;
    }

    @Override
    @Cacheable
    public List<CreditRejectionReason> getCreditRejectionReasons() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.bo_get_ct_credit_rejection_reason()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CreditRejectionReason> rejectionsReasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditRejectionReason reason = new CreditRejectionReason();
            reason.fillFromDb(dbArray.getJSONObject(i));
            rejectionsReasons.add(reason);
        }
        return rejectionsReasons;
    }

    @Override
    @Cacheable
    public List<ApplicationRejectionReason> getApplicationRejectionReasons() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.bo_get_ct_application_rejection_reason()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ApplicationRejectionReason> rejectionsReasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ApplicationRejectionReason reason = new ApplicationRejectionReason();
            reason.fillFromDb(dbArray.getJSONObject(i));
            rejectionsReasons.add(reason);
        }
        return rejectionsReasons;
    }

    @Override
    @Cacheable
    public List<Entity> getEntities() {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_entity()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Entity entity = new Entity();
            entity.fillFromDb(dbArray.getJSONObject(i), catalogService);
            entities.add(entity);
        }
        return entities;
    }

    @Override
    @Cacheable
    public List<Relationship> getRelationships() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_relationship()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Relationship> relationships = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            relationships.add(new Relationship(
                    dbArray.getJSONObject(i).getInt("relationship_id"),
                    dbArray.getJSONObject(i).getString("text_int")
            ));
        }
        return relationships;
    }

    @Override
    @Cacheable
    public List<BufferTransactionType> getBufferTransactionType() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_ct_transaction_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<BufferTransactionType> transactionTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            transactionTypes.add(new BufferTransactionType(
                    dbArray.getJSONObject(i).getInt("transaction_type_id"),
                    dbArray.getJSONObject(i).getString("transaction")
            ));
        }
        return transactionTypes;
    }

    @Override
    @Cacheable
    public List<Bot> getBots() throws Exception {
        JSONArray dbArray = queryForObject("select * from external.get_bots()", JSONArray.class, false, EXTERNAL_DB);

        List<Bot> bots = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            bots.add(new Bot(dbArray.getJSONObject(i)));
        }
        return bots;
    }

    @Override
    @Cacheable
    public List<InteractionType> getInteractionTypes() {
        JSONArray dbArray = queryForObject("select * from interaction.get_ct_interaction_type()", JSONArray.class, false, INTERACTION_DB);
        if (dbArray == null) {
            return null;
        }

        List<InteractionType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            InteractionType type = new InteractionType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }

        return types;
    }

    @Override
    @Cacheable
    public List<InteractionContent> getInteractionContents() {
        JSONArray dbArray = queryForObject("select * from interaction.get_ct_interaction_content()", JSONArray.class, false, INTERACTION_DB);
        if (dbArray == null) {
            return null;
        }

        List<InteractionContent> contents = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            InteractionContent content = new InteractionContent();
            content.fillFromDb(dbArray.getJSONObject(i), catalogService);
            contents.add(content);
        }

        return contents;
    }

    @Override
    @Cacheable
    public List<ContactResult> getContactResults() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_contact_result()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ContactResult> results = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ContactResult result = new ContactResult();
            result.fillFromDb(dbArray.getJSONObject(i));
            results.add(result);
        }

        return results;
    }

    @Override
    @Cacheable
    public List<TrackingAction> getTrackingActions() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_tracking_action()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<TrackingAction> results = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            TrackingAction result = new TrackingAction();
            result.fillFromDb(dbArray.getJSONObject(i));
            results.add(result);
        }

        return results;
    }

    @Override
    @Cacheable
    public List<Tranche> getTranches() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_tranche()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Tranche> tranches = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Tranche tranche = new Tranche();
            tranche.fillFromDb(dbArray.getJSONObject(i));
            tranches.add(tranche);
        }

        return tranches;
    }

    @Override
    @Cacheable
    public List<Employer> getEmployers() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_employers()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Employer employer = new Employer();
            employer.fillFromDb(catalogService, Configuration.getDefaultLocale(), dbArray.getJSONObject(i));
            employers.add(employer);
        }
        return employers;
    }

    @Override
    @Cacheable
    public List<HelpMessage> getHelpMessages() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_help_message()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<HelpMessage> messages = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            HelpMessage message = new HelpMessage();
            message.fillFromDb(dbArray.getJSONObject(i));
            messages.add(message);
        }
        return messages;
    }

    @Override
    @Cacheable
    public List<RccEntity> getRccEntiy() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_rcc_entity()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<RccEntity> entities = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            RccEntity entity = new RccEntity();
            entity.fillFromDb(dbArray.getJSONObject(i), catalogService);
            entities.add(entity);
        }
        return entities;
    }

    @Override
    @Cacheable
    public List<RccAccount> getRccAccounts() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from sysrcc.get_ct_accounts()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<RccAccount> accounts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            RccAccount account = new RccAccount();
            account.fillFromDb(dbArray.getJSONObject(i));
            accounts.add(account);
        }
        return accounts;
    }

    @Override
    @Cacheable
    public List<ConsolidationAccountType> getConsolidationAccountTypes() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from  credit.get_ct_consolidation_accounts()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ConsolidationAccountType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ConsolidationAccountType type = new ConsolidationAccountType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public Map<String, String> getConfigParams() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_config();", JSONArray.class, false);
        Map<String, String> config = new HashMap<>();
        for (int i = 0; i < dbArray.length(); i++) {
            String key = JsonUtil.getStringFromJson(dbArray.getJSONObject(i), "config", null);
            String val = JsonUtil.getStringFromJson(dbArray.getJSONObject(i), "values", null);
            config.put(key, val);
        }
        return config;
    }

    @Override
    @Cacheable
    public List<ProductPersonCategoryAmount> getPersonCategoryAmounts() {
        JSONArray dbArray = queryForObjectTrx("select * from product.get_person_category_ammounts();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<ProductPersonCategoryAmount> categories = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ProductPersonCategoryAmount category = new ProductPersonCategoryAmount();
            category.fillFromDb(dbArray.getJSONObject(i));
            categories.add(category);
        }
        return categories;
    }

    @Override
    @Cacheable
    public List<ProductAgeRange> getProductAgeRanges() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_hard_filter_ages()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<ProductAgeRange> ranges = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ProductAgeRange range = new ProductAgeRange();
            range.fillFromDb(dbArray.getJSONObject(i));
            ranges.add(range);
        }
        return ranges;
    }


    @Override
    @Cacheable
    public List<Contract> getContracts() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_contracts()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<Contract> contracts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Contract contract = new Contract();
            contract.fillFromDb(dbArray.getJSONObject(i));
            contracts.add(contract);
        }
        return contracts;
    }

    @Override
    @Cacheable
    public List<EntityProductParams> getEntityProductParams() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_entity_product_parameter()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<EntityProductParams> params = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityProductParams param = new EntityProductParams();
            param.fillFromDb(dbArray.getJSONObject(i), catalogService);
            params.add(param);
        }
        return params;
    }

    @Override
    @Cacheable
    public List<CustomEntityActivity> getCustomEntityActivities() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select support.get_ct_custom_entity_activity()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CustomEntityActivity> params = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CustomEntityActivity param = new CustomEntityActivity();
            param.fillFromDb(dbArray.getJSONObject(i), catalogService);
            params.add(param);
        }
        return params;
    }

    @Override
    @Cacheable
    public List<EntityBranding> getEntityBranding() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_entity_branding()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<EntityBranding> params = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityBranding param = new EntityBranding();
            param.fillFromDb(dbArray.getJSONObject(i), catalogService);
            params.add(param);
        }
        return params;
    }

    @Override
    public EntityBranding getEntityBrandingByIdNoCache(Integer entityId) throws Exception {

        JSONObject dbJson = queryForObjectTrx("select * from originator.get_ct_entity_branding_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId)
                );

        if (dbJson == null) {
            return null;
        }

        EntityBranding data = new EntityBranding();
        data.fillFromDb(dbJson, catalogService);

        return data;
    }

    @Override
    @Cacheable
    public List<LoanApplicationRegisterType> getLoanApplicationRegisterType() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_register_type();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<LoanApplicationRegisterType> registerTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationRegisterType registerType = new LoanApplicationRegisterType();
            registerType.fillFromDb(dbArray.getJSONObject(i));
            registerTypes.add(registerType);
        }
        return registerTypes;
    }

    @Override
    @Cacheable
    public List<HumanForm> getHumanForms() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from users.get_ct_human_forms_()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<HumanForm> humanForms = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            HumanForm humanForm = new HumanForm();
            humanForm.fillFromDb(dbArray.getJSONObject(i), catalogService);
            humanForms.add(humanForm);
        }
        return humanForms;
    }

    @Override
    @Cacheable
    public List<EntityProduct> getEntityProducts() {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_entity_product()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<EntityProduct> entityProducts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityProduct entityProduct = new EntityProduct();
            entityProduct.fillFromDb(dbArray.getJSONObject(i), catalogService);
            entityProducts.add(entityProduct);
        }
        return entityProducts;
    }

    @Override
    @Cacheable
    public List<Currency> getCurrency() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_currency()", JSONArray.class, false);

        List<Currency> currency = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Currency type = new Currency();
            type.fillFromDb(dbArray.getJSONObject(i));
            currency.add(type);
        }
        return currency;
    }

    @Override
    @Cacheable
    public List<LoanApplicationReasonCategory> getLoanReasonCategories() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_reason_category_()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<LoanApplicationReasonCategory> categories = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationReasonCategory category = new LoanApplicationReasonCategory();
            category.fillFromDb(dbArray.getJSONObject(i));
            categories.add(category);
        }
        return categories;
    }

    @Override
    @Cacheable
    public List<CreditUsage> getCreditUsages() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_usage()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CreditUsage> usages = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditUsage usage = new CreditUsage();
            usage.fillFromDb(dbArray.getJSONObject(i));
            usages.add(usage);
        }
        return usages;
    }

    @Override
    @Cacheable
    public List<HousingType> getHousingTypes() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_housing_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<HousingType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            HousingType type = new HousingType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public List<StudyLevel> getStudyLevels() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_study_level()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<StudyLevel> levels = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            StudyLevel level = new StudyLevel();
            level.fillFromDb(dbArray.getJSONObject(i));
            levels.add(level);
        }
        return levels;
    }

    @Override
    @Cacheable
    public List<Profession> getProfessions() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_profession()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Profession> professions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Profession profession = new Profession();
            profession.fillFromDb(dbArray.getJSONObject(i));
            professions.add(profession);
        }
        return professions;
    }

    @Override
    @Cacheable
    public List<ServiceType> getServiceType() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_service_types()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ServiceType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ServiceType type = new ServiceType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public List<Ocupation> getOcupations() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_ocupation()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Ocupation> ocupations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Ocupation ocupation = new Ocupation();
            ocupation.fillFromDb(dbArray.getJSONObject(i));
            ocupations.add(ocupation);
        }
        return ocupations;
    }

    @Override
    @Cacheable
    public List<SubActivityType> getSubActivityTypes(Locale locale) {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_sub_activity_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<SubActivityType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SubActivityType type = new SubActivityType();
            type.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public List<Agent> getAgents() {
        JSONArray dbArray = queryForObject("SELECT * FROM interaction.get_agents()", JSONArray.class, false, INTERACTION_DB);
        if (dbArray == null) {
            return null;
        }

        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Agent agent = new Agent();
            agent.fillFromDb(dbArray.getJSONObject(i), catalogService);
            agents.add(agent);
        }
        return agents;
    }

    @Override
    @Cacheable
    public List<ProductCategory> getProductCategories() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM product.get_ct_category()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ProductCategory> categories = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ProductCategory category = new ProductCategory();
            category.fillFromDb(dbArray.getJSONObject(i));
            categories.add(category);
        }
        return categories;
    }

    @Override
    @Cacheable
    public List<ComparisonReason> getComparisonReasons() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM comparator.get_ct_comparison_reason()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ComparisonReason> reasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ComparisonReason reason = new ComparisonReason();
            reason.fillFromDb(dbArray.getJSONObject(i));
            reasons.add(reason);
        }
        return reasons;
    }

    @Override
    @Cacheable
    public List<ComparisonCreditCost> getComparisonCreditCosts() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM comparator.get_ct_credit_cost()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ComparisonCreditCost> costs = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ComparisonCreditCost cost = new ComparisonCreditCost();
            cost.fillFromDb(dbArray.getJSONObject(i));
            costs.add(cost);
        }
        return costs;
    }


    @Override
    public ComparisonRates getComparisonInfo(int bankId) {
        JSONObject dbObject = queryForObjectTrx("SELECT * FROM comparator.get_product_rates(?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, bankId));

        if (dbObject == null)
            return null;
        ComparisonRates rates = new ComparisonRates();
        rates.fillFromDb(dbObject);
        return rates;
    }

    @Override
    @Cacheable
    public List<Category> getComparisonCategory() {
        JSONArray dbArray = queryForObjectTrx("select * from comparator.get_ct_comparison_category();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Category> comparisonCategories = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Category category = new Category();
            category.fillFromDb(dbArray.getJSONObject(i), catalogService);
            comparisonCategories.add(category);
        }
        return comparisonCategories;
    }

    @Override
    @Cacheable
    public List<Cost> getComparisonCosts() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from comparator.get_ct_credit_cost();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Cost> costs = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Cost cost = new Cost();
            cost.fillFromDb(dbArray.getJSONObject(i));
            costs.add(cost);
        }
        return costs;
    }

    @Override
    @Cacheable
    public List<CreditCardBrand> getCreditCardBrand() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from comparator.get_ct_brand();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CreditCardBrand> brands = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditCardBrand brand = new CreditCardBrand();
            brand.fillFromDb(dbArray.getJSONObject(i));
            brands.add(brand);
        }
        return brands;
    }

    @Override
    @Cacheable
    public List<FundableBankComparisonCategory> getFundableBankComparisonCategories() {
        JSONArray dbArray = queryForObjectTrx("select * from comparator.get_ct_fundable_bank_comparison_category()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<FundableBankComparisonCategory> fundables = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            FundableBankComparisonCategory fundable = new FundableBankComparisonCategory();
            fundable.fillFromDb(dbArray.getJSONObject(i), catalogService);
            fundables.add(fundable);
        }
        return fundables;
    }

    @Override
    @Cacheable
    public List<VehicleBrand> getVehicleBrands() {
        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_brand()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<VehicleBrand> brands = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            VehicleBrand brand = new VehicleBrand();
            brand.fillFromDb(dbArray.getJSONObject(i));
            brands.add(brand);
        }
        return brands;
    }

    @Override
    @Cacheable
    public List<Vehicle> getVehicles(Locale locale) {
        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_vehicle()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Vehicle vehicle = new Vehicle();
            vehicle.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            vehicles.add(vehicle);
        }
        return vehicles;
    }

    @Override
    @Cacheable
    public List<Avenue> getAvenues() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_street_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Avenue> avenues = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Avenue avenue = new Avenue();
            avenue.fillFromDb(dbArray.getJSONObject(i), catalogService);
            avenues.add(avenue);
        }
        return avenues;
    }

    @Override
    @Cacheable
    public List<HouseType> getHouseTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_interior_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<HouseType> houseTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            HouseType houseType = new HouseType();
            houseType.fillFromDb(dbArray.getJSONObject(i), catalogService);
            houseTypes.add(houseType);
        }
        return houseTypes;
    }

    @Override
    @Cacheable
    public List<AreaType> getAreaTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_zone_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<AreaType> areaTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            AreaType areaType = new AreaType();
            areaType.fillFromDb(dbArray.getJSONObject(i), catalogService);
            areaTypes.add(areaType);
        }
        return areaTypes;
    }

    @Override
    @Cacheable
    public List<CreditSignatureScheduleHour> getCreditSignatureScheduleHours() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_signature_hour()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CreditSignatureScheduleHour> hours = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditSignatureScheduleHour hour = new CreditSignatureScheduleHour();
            hour.fillFromDb(dbArray.getJSONObject(i), catalogService);
            hours.add(hour);
        }
        return hours;
    }

    @Override
    @Cacheable
    public List<DisbursementType> getDisbursementType() {
        JSONArray dbArray = queryForObjectTrx("SELECT originator.get_ct_disbursement_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<DisbursementType> disbursementTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            DisbursementType disbursementType = new DisbursementType();
            disbursementType.fillFromDb(dbArray.getJSONObject(i));
            disbursementTypes.add(disbursementType);
        }
        return disbursementTypes;
    }


    @Override
    @Cacheable
    public List<LoanApplicationAuditType> getLoanApplicationAuditTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_loan_application_audit_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<LoanApplicationAuditType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationAuditType type = new LoanApplicationAuditType();
            type.fillFromDb(dbArray.getJSONObject(i), catalogService);
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public List<LoanApplicationAuditRejectionReason> getLoanApplicationAuditRejectionReason() {
        JSONArray dbArray = queryForObjectTrx("select credit.get_ct_loan_application_audit_rejection_reason()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<LoanApplicationAuditRejectionReason> reasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationAuditRejectionReason reason = new LoanApplicationAuditRejectionReason();
            reason.fillFromDb(dbArray.getJSONObject(i), catalogService);
            reasons.add(reason);
        }
        return reasons;
    }

    @Override
    @Cacheable
    public List<ProcessQuestion> getProcessQuestions() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_process_questions()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ProcessQuestion> questions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ProcessQuestion question = new ProcessQuestion();
            question.fillFromDb(dbArray.getJSONObject(i), catalogService);
            questions.add(question);
        }
        return questions;
    }

    @Override
    @Cacheable
    public List<ProcessQuestionCategory> getProcessQuestionCategories() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_process_questions_category()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<ProcessQuestionCategory> categories = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ProcessQuestionCategory category = new ProcessQuestionCategory();
            category.fillFromDb(dbArray.getJSONObject(i));
            categories.add(category);
        }
        return categories;
    }

    @Override
    @Cacheable
    public List<SendGridList> getSendgridList() {
        JSONArray dbArray = queryForObject("select * from interaction.get_ct_contact_list();", JSONArray.class, false, INTERACTION_DB);
        if (dbArray == null) {
            return null;
        }
        List<SendGridList> sendGridLists = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SendGridList sendGridList = new SendGridList();
            sendGridList.fillFromDB(dbArray.getJSONObject(i));
            sendGridLists.add(sendGridList);
        }
        return sendGridLists;
    }

    @Override
    @Cacheable
    public List<FraudAlert> getFraudAlerts() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM support.get_ct_fraud_alerts();", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<FraudAlert> fraudAlerts = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); ++i) {
            FraudAlert fraudAlert = new FraudAlert();
            fraudAlert.fillFromDb(dbArray.getJSONObject(i));
            fraudAlerts.add(fraudAlert);

        }

        return fraudAlerts;
    }

    @Override
    @Cacheable
    public List<FraudFlagRejectionReason> getFraudAlertRejectionReasons() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM credit.get_ct_fraud_flag_rejection_reasons();", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<FraudFlagRejectionReason> rejectionReasons = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); ++i) {
            FraudFlagRejectionReason rejectionReason = new FraudFlagRejectionReason();
            rejectionReason.fillFromDb(dbArray.getJSONObject(i));
            rejectionReasons.add(rejectionReason);
        }

        return rejectionReasons;
    }

    @Override
    @Cacheable
    public List<FraudFlag> getFraudFlags() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM credit.get_ct_fraud_flags();", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<FraudFlag> fraudFlags = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); ++i) {
            FraudFlag fraudFlag = new FraudFlag();
            fraudFlag.fillFromDb(dbArray.getJSONObject(i));
            fraudFlags.add(fraudFlag);
        }

        return fraudFlags;
    }

    @Override
    @Cacheable
    public List<FraudAlertStatus> getFraudAlertStatus() {
        JSONArray jsonArray = queryForObjectTrx("select*  from credit.get_ct_fraud_alert_status();", JSONArray.class, false);

        List<FraudAlertStatus> fraudAlertStatuses = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            FraudAlertStatus fraudAlertStatus = new FraudAlertStatus();
            fraudAlertStatus.fillFromDb(jsonArray.getJSONObject(i));
            fraudAlertStatuses.add(fraudAlertStatus);
        }

        return fraudAlertStatuses;
    }

    @Override
    @Cacheable
    public List<FraudFlagStatus> getFraudFlagStatus() {
        JSONArray jsonArray = queryForObjectTrx("select*  from credit.get_ct_fraud_flag_status()", JSONArray.class, false);

        List<FraudFlagStatus> fraudFlagStatuses = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            FraudFlagStatus fraudFlagStatus = new FraudFlagStatus();
            fraudFlagStatus.fillFromDb(jsonArray.getJSONObject(i));
            fraudFlagStatuses.add(fraudFlagStatus);
        }

        return fraudFlagStatuses;
    }

    @Override
    @Cacheable
    public List<CountryParam> getCountryParams() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_country()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CountryParam> countryParams = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CountryParam countryParam = new CountryParam();
            countryParam.fillFromDb(dbArray.getJSONObject(i), catalogService);
            countryParams.add(countryParam);
        }
        return countryParams;
    }

    @Override
    @Cacheable
    public List<IdentityDocumentType> getIdentityDocumentTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_document_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<IdentityDocumentType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            IdentityDocumentType type = new IdentityDocumentType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public List<VehicleGasType> getVehicleGasType() {
        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_gas_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<VehicleGasType> types = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            VehicleGasType type = new VehicleGasType();
            type.fillFromDb(dbArray.getJSONObject(i));
            types.add(type);
        }
        return types;
    }

    @Override
    @Cacheable
    public List<VehicleDealership> getVehicleDealership(Locale locale) {
        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_car_dealership()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<VehicleDealership> dealerships = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            VehicleDealership dealership = new VehicleDealership();
            dealership.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            dealerships.add(dealership);
        }
        return dealerships;
    }

    @Override
    @Cacheable
    public List<CreditSubStatus> getCreditSubStatus(Locale locale) {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_credit_sub_status()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CreditSubStatus> statuses = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CreditSubStatus status = new CreditSubStatus();
            status.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            statuses.add(status);
        }
        return statuses;
    }

    @Override
    @Cacheable
    public List<Proxy> getProxies() throws Exception {
        JSONArray jsonArray = queryForObject("SELECT * from external.get_ct_proxy();", JSONArray.class, false, EXTERNAL_DB);

        if (jsonArray == null) {
            return null;
        }

        List<Proxy> proxies = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            Proxy proxy = new Proxy();
            proxy.fillFromDB(jsonArray.getJSONObject(i));
            proxies.add(proxy);
        }

        return proxies;
    }

    @Override
    @Cacheable
    public List<Department> getGeneralDeparments() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_department()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Department> departments = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Department department = new Department();
            department.fillFromDb(dbArray.getJSONObject(i), catalogService);
            departments.add(department);
        }
        return departments;
    }

    @Override
    @Cacheable
    public List<Province> getGeneralProvinces() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_province()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Province> provinces = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Province province = new Province();
            province.fillFromDb(dbArray.getJSONObject(i), catalogService);
            provinces.add(province);
        }
        return provinces;
    }


    @Override
    @Cacheable
    public List<District> getGeneralDistricts() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_locality()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<District> districts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            District district = new District();
            district.fillFromDb(dbArray.getJSONObject(i), catalogService);
            districts.add(district);
        }
        return districts;
    }

    @Override
    @Cacheable
    public List<EntityWebService> getEntityWebService() {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_ct_entity_ws()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<EntityWebService> entityWebServices = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityWebService entityWebService = new EntityWebService();
            entityWebService.fillFromDb(dbArray.getJSONObject(i));
            entityWebServices.add(entityWebService);
        }
        return entityWebServices;
    }

    @Override
    @Cacheable
    public List<Holiday> getHolidays() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_holiday()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Holiday> holidays = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Holiday holiday = new Holiday();
            holiday.fillFromDb(dbArray.getJSONObject(i));
            holidays.add(holiday);
        }
        return holidays;
    }

    @Override
    @Cacheable
    public List<CorrectionFlow> getCorrectionFlows() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_correction_flow()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CorrectionFlow> correctionFlows = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CorrectionFlow correctionFlow = new CorrectionFlow();
            correctionFlow.fillFromDb(dbArray.getJSONObject(i));
            correctionFlows.add(correctionFlow);
        }
        return correctionFlows;
    }

    @Override
    @Cacheable
    public List<ReturningReason> getReturningReasons() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_returning_reason()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<ReturningReason> returningReasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ReturningReason returningReason = new ReturningReason();
            returningReason.fillFromDb(dbArray.getJSONObject(i), catalogService);
            returningReasons.add(returningReason);
        }
        return returningReasons;
    }

    @Override
    @Cacheable
    public List<Bureau> getBureaus() {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_bureau()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Bureau> bureaus = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Bureau bureau = new Bureau();
            bureau.fillFromDb(dbArray.getJSONObject(i));
            bureaus.add(bureau);
        }

        return bureaus;
    }

    @Override
    @Cacheable
    public List<EmployerGroup> getEmployerGroups() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_employers_group()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<EmployerGroup> employers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EmployerGroup employer = new EmployerGroup();
            employer.fillFromDb(dbArray.getJSONObject(i));
            employers.add(employer);
        }
        return employers;
    }

    @Override
    @Cacheable
    public List<InstallmentStatus> getInstallmentStatus() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_installment_status();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<InstallmentStatus> installmentStatuses = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            InstallmentStatus installmentStatus = new InstallmentStatus();
            installmentStatus.fillFromDb(dbArray.getJSONObject(i));
            installmentStatuses.add(installmentStatus);
        }
        return installmentStatuses;
    }

    @Override
    @Cacheable
    public List<HardFilter> getHardFilters() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_hard_filter()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<HardFilter> hardFilters = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            HardFilter hardFilter = new HardFilter();
            hardFilter.fillFromDb(dbArray.getJSONObject(i));
            hardFilters.add(hardFilter);
        }
        return hardFilters;
    }

    @Override
    @Cacheable
    public List<Policy> getPolicies() {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_policy()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<Policy> policies = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Policy policy = new Policy();
            policy.fillFromDb(dbArray.getJSONObject(i), messageSource, Configuration.getDefaultLocale());
            policies.add(policy);
        }
        return policies;
    }

    @Override
    @Cacheable
    public List<SysUserSchedule> getSchedules() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_schedules()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<SysUserSchedule> schedules = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SysUserSchedule schedule = new SysUserSchedule();
            schedule.fillFromDb(dbArray.getJSONObject(i));
            schedules.add(schedule);
        }

        return schedules;
    }

    @Override
    @Cacheable
    public List<OfferRejectionReason> getOfferRejectionReasons() throws Exception{
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_offer_rejection_reasons()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<OfferRejectionReason> reasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            OfferRejectionReason reason = new OfferRejectionReason();
            reason.fillFromDb(dbArray.getJSONObject(i), messageSource, catalogService,Configuration.getDefaultLocale());
            reasons.add(reason);
        }

        return reasons;
    }

    @Override
    @Cacheable
    public List<MaintainedCarBrand> getMaintainedCarBrands() {
        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_maintained_car_brand()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<MaintainedCarBrand> maintainedCarBrands = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            MaintainedCarBrand maintainedCarBrand = new MaintainedCarBrand();
            maintainedCarBrand.fillFromDb(dbArray.getJSONObject(i));
            if (maintainedCarBrand.getActive()) {
                maintainedCarBrands.add(maintainedCarBrand);
            }

        }
        return maintainedCarBrands;
    }

    @Override
    @Cacheable

    public List<Mileage> getMileages() {

        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_mileage()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<Mileage> mileages = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {

            Mileage mileage = new Mileage();
            mileage.fillFromDb(dbArray.getJSONObject(i));
            mileages.add(mileage);
        }
        return mileages;
    }

    @Override
    @Cacheable
    public List<GuaranteedVehicle> getGuaranteedVehicles() {
        JSONArray dbArray = queryForObjectTrx("select * from vehicle.get_ct_guaranteed_vehicle()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<GuaranteedVehicle> guaranteedVehicles = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            GuaranteedVehicle guaranteedVehicle = new GuaranteedVehicle();
            guaranteedVehicle.fillFromDb(dbArray.getJSONObject(i), catalogService);
            guaranteedVehicles.add(guaranteedVehicle);
        }

        return guaranteedVehicles;
    }

    @Override
    @Cacheable
    public List<AppoimentSchedule> getAppoimentSchedules() {
        JSONArray dbArray = queryForObjectTrx("select * from support.get_ct_appointment_schedules()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<AppoimentSchedule> appoimentSchedules = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            AppoimentSchedule appoimentSchedule = new AppoimentSchedule();
            appoimentSchedule.fillFromDb(dbArray.getJSONObject(i));
            appoimentSchedules.add(appoimentSchedule);
        }

        return appoimentSchedules;
    }

    @Override
    @Cacheable
    public List<SocioeconomicLevel> getSocioEconomicLevel() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_socioeconomic_level()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<SocioeconomicLevel> levels = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SocioeconomicLevel level = new SocioeconomicLevel();
            level.fillFromDb(dbArray.getJSONObject(i));
            levels.add(level);
        }

        return levels;
    }

    @Override
    @Cacheable
    public List<InteractionProvider> getInteractionProviders() {
        JSONArray dbArray = queryForObject("select * from interaction.get_ct_interaction_provider()", JSONArray.class, false, INTERACTION_DB);
        if (dbArray == null) {
            return null;
        }

        List<InteractionProvider> providers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            InteractionProvider provider = new InteractionProvider();
            provider.fillFromDb(dbArray.getJSONObject(i));
            providers.add(provider);
        }

        return providers;
    }

    @Override
    @Cacheable
    public List<SysUser> getManagmentAnalystSysusers() {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_ct_tracker_sysuser()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<SysUser> sysUsers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            SysUser provider = new SysUser();
            provider.fillFromDb(catalogService, dbArray.getJSONObject(i));
            sysUsers.add(provider);
        }

        return sysUsers;
    }

    @Override
    @Cacheable
    public List<ObservationReason> getObservationReasons() {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_observation_reason()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<ObservationReason> reasons = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ObservationReason reason = new ObservationReason();
            reason.fillFromDb(dbArray.getJSONObject(i), Configuration.getDefaultLocale());
            reasons.add(reason);
        }

        return reasons;
    }

    @Override
    @Cacheable
    public List<RetirementScheme> getRetirementSchemes() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_retirement_scheme()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<RetirementScheme> schemes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            RetirementScheme scheme = new RetirementScheme();
            scheme.fillFromDb(dbArray.getJSONObject(i), Configuration.getDefaultLocale());
            schemes.add(scheme);
        }
        return schemes;
    }

    @Override
    @Cacheable
    public List<CustomProfession> getCustomProfessions() {
        JSONArray dbArray = queryForObjectTrx("select * from person.get_ct_custom_profession()", JSONArray.class, false);

        if (dbArray == null) {
            return null;
        }

        List<CustomProfession> schemes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CustomProfession customProfession = new CustomProfession();
            customProfession.fillFromDb(dbArray.getJSONObject(i), Configuration.getDefaultLocale());
            schemes.add(customProfession);
        }
        return schemes;
    }

    @Override
    @Cacheable
    public List<LeadActivityType> getLeadActivityTypes() {
        JSONArray dbArray = queryForObjectTrx("select * from lead.get_ct_activity_type()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<LeadActivityType> activityTypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LeadActivityType leadActivityType = new LeadActivityType();
            leadActivityType.fillFromDb(dbArray.getJSONObject(i));
            activityTypes.add(leadActivityType);
        }
        return activityTypes;
    }

    @Override
    @Cacheable
    public List<LeadProduct> getLeadProducts() {
        JSONArray dbArray = queryForObjectTrx("select * from lead.get_ct_product();", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }
        List<LeadProduct> leadProducts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LeadProduct leadProduct = new LeadProduct();
            leadProduct.fillFromDb(dbArray.getJSONObject(i));
            leadProducts.add(leadProduct);
        }
        return leadProducts;
    }


    @Override
    @Cacheable
    public List<PreApprovedMail> getPreApprovedMailingList() {
        JSONArray dbArray = queryForObject("select * from interaction.get_ct_pre_approved_mailing();", JSONArray.class, false, INTERACTION_DB);
        if (dbArray == null) {
            return null;
        }
        List<PreApprovedMail> list = new ArrayList<PreApprovedMail>();
        for (int i = 0; i < dbArray.length(); i++) {
            PreApprovedMail preApprovedMail = new PreApprovedMail();
            preApprovedMail.fillFromDb(dbArray.getJSONObject(i), catalogService);
            list.add(preApprovedMail);
        }
        return list;
    }

    @Override
    @Cacheable
    public List<AfipActivitiy> getAfipActivities() {
        JSONArray dbArray = queryForObject("select * from cendeu.get_actividades_afip();", JSONArray.class, false, REPOSITORY_DB);
        if (dbArray == null) {
            return null;
        }
        List<AfipActivitiy> list = new ArrayList<AfipActivitiy>();
        for (int i = 0; i < dbArray.length(); i++) {
            AfipActivitiy afipActivitiy = new AfipActivitiy();
            afipActivitiy.fillFromDb(dbArray.getJSONObject(i));
            list.add(afipActivitiy);
        }
        return list;
    }


    @Override
    @Cacheable
    public List<PersonProfessionOccupation> getProfessionOccupation(){
        JSONArray dbArray = queryForObjectTrx("select person.get_ct_profession_occupation()", JSONArray.class, false);
        if (dbArray == null)
            return null;

        List<PersonProfessionOccupation> personProfessionOccupationList = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            PersonProfessionOccupation personProfessionOccupation = new PersonProfessionOccupation();
            personProfessionOccupation.fillFromDb(json);
            personProfessionOccupationList.add(personProfessionOccupation);
        }
        return personProfessionOccupationList;
    }

    @Override
    public List<RateCommissionCluster> getRateCommissionClusters(){
        JSONArray dbArray = queryForObjectTrx("select product.get_ct_cluster_rate_commission()", JSONArray.class, false);
        if (dbArray == null)
            return new ArrayList<>();

        List<RateCommissionCluster> clusters = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            RateCommissionCluster cluster = new RateCommissionCluster();
            cluster.fillFromDb(json);
            clusters.add(cluster);
        }
        return clusters;
    }

    @Override
    @Cacheable
    public List<EntityUserType> getEntityUserTypes(){
        JSONArray dbArray = queryForObjectTrx("select * from users.get_ct_entity_user_type()", JSONArray.class);
        if (dbArray == null)
            return new ArrayList<>();

        List<EntityUserType> entiityUsertypes = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            EntityUserType entityUserType = new EntityUserType();
            entityUserType.fillFromDb(json);
            entiityUsertypes.add(entityUserType);
        }
        return entiityUsertypes;
    }

    @Override
    @Cacheable
    public List<OccupationArea> getOccupationAreas() {
        JSONArray dbArray = queryForObjectTrx("SELECT * FROM person.get_ct_ocupation_area()", JSONArray.class, false);
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<OccupationArea> occupationAreas = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            OccupationArea occupationArea = new OccupationArea();
            occupationArea.fillFromDb(dbArray.getJSONObject(i));
            occupationAreas.add(occupationArea);
        }
        return occupationAreas;
    }

    @Override
    @Cacheable
    public List<EntityAcquisitionChannel> getEntityAcquisitionChannels() {
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_entity_acquisition_channel()", JSONArray.class);
        if (dbArray == null)
            return new ArrayList<>();

        List<EntityAcquisitionChannel> entityAcquisitionChannels = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            EntityAcquisitionChannel entityUserType = new EntityAcquisitionChannel();
            entityUserType.fillFromDb(json);
            entityAcquisitionChannels.add(entityUserType);
        }
        return entityAcquisitionChannels;
    }

    @Override
    @Cacheable
    public List<ExtranetMenu> getExtranetMenus() {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_ct_extranet_menu()", JSONArray.class, false);

        List<ExtranetMenu> extranetMenus = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExtranetMenu extranetMenu = new ExtranetMenu();
            extranetMenu.fillFromDb(dbArray.getJSONObject(i), catalogService);
            extranetMenus.add(extranetMenu);
        }
        return extranetMenus;
    }

    @Override
    @Cacheable
    public List<Role> getRoles() {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_roles()", JSONArray.class, false);

        List<Role> roles = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            Role role = new Role();
            role.fillFromDb(dbArray.getJSONObject(i));
            roles.add(role);
        }
        return roles;
    }

    @Override
    @Cacheable
    public List<FunnelStep> getFunnelSteps(){
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_funnel_steps()", JSONArray.class, false);

        List<FunnelStep> steps = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            FunnelStep data = new FunnelStep();
            data.fillFromDb(dbArray.getJSONObject(i));
            steps.add(data);
        }
        return steps;
    }

    @Override
    @Cacheable
    public List<ApprovalValidation> getApprovalValidations(){
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_ct_approval_validation()", JSONArray.class, false);
        if (dbArray == null)
            return new ArrayList<>();

        List<ApprovalValidation> validations = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ApprovalValidation data = new ApprovalValidation();
            data.fillFromDb(dbArray.getJSONObject(i));
            validations.add(data);
        }
        return validations;
    }

    @Override
    public void updateEntityBrandingExtranetConfiguration(Integer entityId, EntityExtranetConfiguration entityExtranetConfiguration) throws Exception {
        updateTrx("UPDATE originator.ct_entity_branding SET js_extranet_configuration = ? WHERE entity_id = ?",
                new SqlParameterValue(Types.OTHER, new Gson().toJson(entityExtranetConfiguration)),
                new SqlParameterValue(Types.INTEGER, entityId)
        );
    }


    @Override
    @Cacheable
    public List<EntityProduct> getAllEntityProductsIncludeInactives(){
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_ct_entity_product_include_inactives()", JSONArray.class, false);
        if (dbArray == null)
            return new ArrayList<>();

        List<EntityProduct> entityProducts = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityProduct entityProduct = new EntityProduct();
            entityProduct.fillFromDb(dbArray.getJSONObject(i), catalogService);
            entityProducts.add(entityProduct);
        }
        return entityProducts;
    }

    @Override
    public List<EntityProductParamPolicyConfiguration> getEntityProductParamPolicy(int entityId, int entityProductParamId){
        JSONArray dbArray = queryForObjectTrx("select * from originator.get_policies_by_entity_product(?,?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, entityProductParamId));
        if (dbArray == null)
            return new ArrayList<>();

        List<EntityProductParamPolicyConfiguration> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityProductParamPolicyConfiguration data = new EntityProductParamPolicyConfiguration();
            data.fillFromDb(dbArray.getJSONObject(i),catalogService);
            list.add(data);
        }
        return list;
    }


    @Override
    public void registerCustomEntityActivity(Integer entityId, Integer identifier, String description, Long regulatoryEntityIdentifier) {
        Integer id = queryForObjectTrx("select support.insert_or_update_custom_entity_activity(?,?,?,?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, identifier),
                new SqlParameterValue(Types.VARCHAR, description),
                new SqlParameterValue(Types.BIGINT, regulatoryEntityIdentifier));
    }

    @Override
    public void registerCustomEntityProfession(Integer entityId, Long identifier, String description) {
        Integer id = queryForObjectTrx("select support.insert_or_update_custom_entity_profession(?,?,?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, identifier),
                new SqlParameterValue(Types.VARCHAR, description));
    }

    @Override
    @Cacheable
    public List<CustomEntityProfession> getCustomEntityProfessions() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select support.get_ct_custom_entity_profession()", JSONArray.class, false);
        if (dbArray == null) {
            return null;
        }

        List<CustomEntityProfession> params = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            CustomEntityProfession param = new CustomEntityProfession();
            param.fillFromDb(dbArray.getJSONObject(i), catalogService);
            params.add(param);
        }
        return params;
    }
}