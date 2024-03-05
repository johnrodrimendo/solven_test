package com.affirm.common.service.impl;

import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.PreApprovedMail;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.security.model.SysUser;
import com.affirm.security.model.SysUserSchedule;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jrodriguez
 */


@Service("catalogService")
//@CacheConfig(cacheNames = "catalogCache", keyGenerator = "cacheKeyGenerator")
public class CatalogServiceImpl implements CatalogService {

    private static Logger logger = Logger.getLogger(CatalogServiceImpl.class);

    @Autowired
    private CatalogDAO catalogDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UtilService utilService;

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }


    /**
     * This is the new way to get the catalogs.
     *
     * @param clazz
     * @param locale
     * @param <T>
     * @return
     */
    @Override
    public <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale) {
        return getCatalog(clazz, locale, true);
    }

    /**
     * This is the new way to get the catalogs.
     *
     * @param clazz
     * @param locale
     * @param active true to get only active, false to only inactive, null to get all.
     * @param <T>
     * @return
     */
    @Override
    public <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale, Boolean active) {
        return getCatalog(clazz, locale, true, null);
    }

    /**
     * This is the new way to get the catalogs.
     *
     * @param clazz
     * @param locale
     * @param predicate
     * @param <T>
     * @return
     */
    @Override
    public <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale, Predicate<T> predicate) {
        return getCatalog(clazz, locale, null, predicate);
    }

    /**
     * This is the new way to get the catalogs.
     *
     * @param clazz
     * @param locale
     * @param active    true to get only active, false to only inactive, null to get all
     * @param predicate
     * @param <T>
     * @return
     * @throws Exception
     */
    @Override
    public <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale, Boolean active, Predicate<T> predicate) {
        List<T> initialResults = new ArrayList<>();

        // Cant use switch with class :'(
        if (clazz.isAssignableFrom(ProductCategory.class)) {
            initialResults = (List<T>) catalogDao.getProductCategories();
        } else if (clazz.isAssignableFrom(Product.class)) {
            initialResults = (List<T>) catalogDao.getProducts();
        } else if (clazz.isAssignableFrom(CountryParam.class)) {
            initialResults = (List<T>) catalogDao.getCountryParams();
        } else if (clazz.isAssignableFrom(SubActivityType.class)) {
            initialResults = (List<T>) catalogDao.getSubActivityTypes(locale);
        }
//        else if (clazz.isAssignableFrom(IDENTITY_DOCUMENT_TYPE)) {
//            initialResults = (List<T>) catalogDao.getIdentityDocumentTypes();
//        } else if (clazz.isAssignableFrom(LOAN_APPLICATION_REASON)) {
//            initialResults = (List<T>) catalogDao.getLoanApplicationReasons();
//        } else if (clazz.isAssignableFrom(MARITAL_STATUS)) {
//            initialResults = (List<T>) catalogDao.getMaritalStatus();
//        } else if (clazz.isAssignableFrom(STREET_TYPE)) {
//            initialResults = (List<T>) catalogDao.getStreetTypes();
//        } else if (clazz.isAssignableFrom(ACTIVITY_TYPE)) {
//            initialResults = (List<T>) catalogDao.getActivityTypes();
//        } else if (clazz.isAssignableFrom(BANK)) {
//            initialResults = (List<T>) catalogDao.getBanks();
//        } else if (clazz.isAssignableFrom(PRODUCT)) {
//            initialResults = (List<T>) catalogDao.getProducts();
//        } else if (clazz.isAssignableFrom(NATIONALITY)) {
//            initialResults = (List<T>) catalogDao.getNationalities();
//        } else if (clazz.isAssignableFrom(BELONGING)) {
//            initialResults = (List<T>) catalogDao.getBelongings();
//        } else if (clazz.isAssignableFrom(VOUCHER_TYPE)) {
//            initialResults = (List<T>) catalogDao.getVoucherTypes();
//        } else if (clazz.isAssignableFrom(PENSION_PAYER)) {
//            initialResults = (List<T>) catalogDao.getPensionPayer();
//        } else if (clazz.isAssignableFrom(LOAN_APPLICATION_STATUS)) {
//            initialResults = (List<T>) catalogDao.getLoanApplicationStatuses();
//        } else if (clazz.isAssignableFrom(CREDIT_STATUS)) {
//            initialResults = (List<T>) catalogDao.getCreditStatuses();
//        } else if (clazz.isAssignableFrom(USER_FILE_TYPE)) {
//            initialResults = (List<T>) catalogDao.getUserFileTypes();
//        } else if (clazz.isAssignableFrom(CLUSTER)) {
//            initialResults = (List<T>) catalogDao.getClusters();
//        } else if (clazz.isAssignableFrom(CREDIT_REJECTION_REASON)) {
//            initialResults = (List<T>) catalogDao.getCreditRejectionReasons();
//        } else if (clazz.isAssignableFrom(APPLICATION_REJECTION_REASON)) {
//            initialResults = (List<T>) catalogDao.getApplicationRejectionReasons();
//        } else if (clazz.isAssignableFrom(ENTITY)) {
//            initialResults = (List<T>) catalogDao.getEntities();
//        } else if (clazz.isAssignableFrom(RELATIONSHIP)) {
//            initialResults = (List<T>) catalogDao.getRelationships();
//        } else if (clazz.isAssignableFrom(BUFFER_TRANSACTION_TYPE)) {
//            initialResults = (List<T>) catalogDao.getBufferTransactionType();
//        } else if (clazz.isAssignableFrom(BOT)) {
//            initialResults = (List<T>) catalogDao.getBots();
//        } else if (clazz.isAssignableFrom(INTERACTION_TYPE)) {
//            initialResults = (List<T>) catalogDao.getInteractionTypes();
//        } else if (clazz.isAssignableFrom(INTERACTION_CONTENT)) {
//            initialResults = (List<T>) catalogDao.getInteractionContents();
//        } else if (clazz.isAssignableFrom(CONTACT_RESULT)) {
//            initialResults = (List<T>) catalogDao.getContactResults();
//        } else if (clazz.isAssignableFrom(TRACKING_ACTION)) {
//            initialResults = (List<T>) catalogDao.getTrackingActions();
//        } else if (clazz.isAssignableFrom(TRANCHE)) {
//            initialResults = (List<T>) catalogDao.getTranches();
//        } else if (clazz.isAssignableFrom(EMPLOYER)) {
//            initialResults = (List<T>) catalogDao.getEmployers();
//        } else if (clazz.isAssignableFrom(HELP_MESSAGE)) {
//            initialResults = (List<T>) catalogDao.getHelpMessages();
//        } else if (clazz.isAssignableFrom(RCC_ENTITY)) {
//            initialResults = (List<T>) catalogDao.getRccEntiy();
//        } else if (clazz.isAssignableFrom(RCC_ACCOUNT)) {
//            initialResults = (List<T>) catalogDao.getRccAccounts();
//        } else if (clazz.isAssignableFrom(CONSOLIDATION_ACCOUNT_TYPE)) {
//            initialResults = (List<T>) catalogDao.getConsolidationAccountTypes();
//        } else if (clazz.isAssignableFrom(CONTRACT)) {
//            initialResults = (List<T>) catalogDao.getContracts();
//        } else if (clazz.isAssignableFrom(PRODUCT_AGE_RANGE)) {
//            initialResults = (List<T>) catalogDao.getProductAgeRanges();
//        } else if (clazz.isAssignableFrom(ENTITY_PRODUCT_PARAMS)) {
//            initialResults = (List<T>) catalogDao.getEntityProductParams();
//        } else if (clazz.isAssignableFrom(ENTITY_BRANDING)) {
//            initialResults = (List<T>) catalogDao.getEntityBranding();
//        } else if (clazz.isAssignableFrom(LOAN_APPLICATION_REGISTER_TYPE)) {
//            initialResults = (List<T>) catalogDao.getLoanApplicationRegisterType();
//        } else if (clazz.isAssignableFrom(HUMAN_FORM)) {
//            initialResults = (List<T>) catalogDao.getHumanForms();
//        } else if (clazz.isAssignableFrom(ENTITY_PRODUCT)) {
//            initialResults = (List<T>) catalogDao.getEntityProducts();
//        } else if (clazz.isAssignableFrom(CURRENCY)) {
//            initialResults = (List<T>) catalogDao.getCurrency();
//        } else if (clazz.isAssignableFrom(LOAN_APPLICATION_REASON_CATEGORY)) {
//            initialResults = (List<T>) catalogDao.getLoanReasonCategories();
//        } else if (clazz.isAssignableFrom(CREDIT_USAGE)) {
//            initialResults = (List<T>) catalogDao.getCreditUsages();
//        } else if (clazz.isAssignableFrom(HOUSING_TYPE)) {
//            initialResults = (List<T>) catalogDao.getHousingTypes();
//        } else if (clazz.isAssignableFrom(STUDY_LEVEL)) {
//            initialResults = (List<T>) catalogDao.getStudyLevels();
//        } else if (clazz.isAssignableFrom(PROFESSION)) {
//            initialResults = (List<T>) catalogDao.getProfessions();
//        } else if (clazz.isAssignableFrom(SERVICE_TYPE)) {
//            initialResults = (List<T>) catalogDao.getServiceType();
//        } else if (clazz.isAssignableFrom(OCUPATION)) {
//            initialResults = (List<T>) catalogDao.getOcupations();
//        } else if (clazz.isAssignableFrom(SUB_ACTIVITY_TYPE)) {
//            initialResults = (List<T>) catalogDao.getSubActivityTypes();
//        } else if (clazz.isAssignableFrom(AGENT)) {
//            initialResults = (List<T>) catalogDao.getAgents();
//        } else if (clazz.isAssignableFrom(COMPARISON_REASON)) {
//            initialResults = (List<T>) catalogDao.getComparisonReasons();
//        } else if (clazz.isAssignableFrom(COMPARISON_CREDIT_COST)) {
//            initialResults = (List<T>) catalogDao.getComparisonCreditCosts();
//        } else if (clazz.isAssignableFrom(COMPARISON_CREDIT_COST)) {
//            initialResults = (List<T>) catalogDao.getComparisonCosts();
//        } else if (clazz.isAssignableFrom(COMPARISON_CATEGORY)) {
//            initialResults = (List<T>) catalogDao.getComparisonCategory();
//        } else if (clazz.isAssignableFrom(CREDIT_CARD_BRAND)) {
//            initialResults = (List<T>) catalogDao.getCreditCardBrand();
//        } else if (clazz.isAssignableFrom(FUNDABLE_BANK_COMPARISON_CATEGORY)) {
//            initialResults = (List<T>) catalogDao.getFundableBankComparisonCategories();
//        } else if (clazz.isAssignableFrom(VEHICLE_BRAND)) {
//            initialResults = (List<T>) catalogDao.getVehicleBrands();
//        } else if (clazz.isAssignableFrom(VEHICLE)) {
//            initialResults = (List<T>) catalogDao.getVehicles();
//        } else if (clazz.isAssignableFrom(AVENUE)) {
//            initialResults = (List<T>) catalogDao.getAvenues();
//        } else if (clazz.isAssignableFrom(HOUSE_TYPE)) {
//            initialResults = (List<T>) catalogDao.getHouseTypes();
//        } else if (clazz.isAssignableFrom(CREDIT_SIGNATURE_SCHEDULE_HOURS)) {
//            initialResults = (List<T>) catalogDao.getCreditSignatureScheduleHours();
//        } else if (clazz.isAssignableFrom(LOAN_APPLICATION_AUDIT_TYPES)) {
//            initialResults = (List<T>) catalogDao.getCreditSignatureScheduleHours();
//        } else if (clazz.isAssignableFrom(LOAN_APPLICATION_AUDIT_REJECTION_REASON)) {
//            initialResults = (List<T>) catalogDao.getLoanApplicationAuditRejectionReason();
//        } else if (clazz.isAssignableFrom(PROCESS_QUESTION)) {
//            initialResults = (List<T>) catalogDao.getProcessQuestions();
//        } else if (clazz.isAssignableFrom(PROCESS_QUESTION_CATEGORY)) {
//            initialResults = (List<T>) catalogDao.getProcessQuestionCategories();
//        } else if (clazz.isAssignableFrom(DISBURSEMENT_TYPE)) {
//            initialResults = (List<T>) catalogDao.getDisbursementType();
//        } else if (clazz.isAssignableFrom(SENDGRID_LIST)) {
//            initialResults = (List<T>) catalogDao.getSendgridList();
//        } else if (clazz.isAssignableFrom(COUNTRY_PARAM)) {
//            initialResults = (List<T>) catalogDao.getCountryParams();
//        }

        // This to avoid concurrence erros
        List<T> results = initialResults != null ? new ArrayList<>(initialResults) : new ArrayList<>();

        if (active != null)
            results = results.stream().filter(c -> c.getActive() != null && c.getActive() == active).collect(Collectors.toList());
        if (predicate != null)
            results = results.stream().filter(predicate).collect(Collectors.toList());

        return results;
    }

    /**
     * This is the new way to get the catalogs, Forget about creating a method for every single catalog, thats in the past man, dont live in the past.
     * This method only return the object that match the id
     *
     * @param clazz
     * @param id
     * @param locale
     * @param <T>
     * @return
     */
    @Override
    public <T extends ICatalog> T getCatalogById(Class<T> clazz, Object id, Locale locale) {
        if (id == null)
            return null;

        if (ICatalogIntegerId.class.isAssignableFrom(clazz))
            return getCatalog(clazz, locale, (c -> ((ICatalogIntegerId) c).getId() == (int) id)).stream().findFirst().orElse(null);

        throw new RuntimeException("The catalog doesnt implement any Id interface!");
    }

    @Override
    public Ubigeo getUbigeo(String ubigeoId) throws Exception {
        if (ubigeoId == null) {
            return null;
        }
        String departmentId = ubigeoId.substring(0, 2);
        String provinceId = ubigeoId.substring(2, 4);
        String districtId = ubigeoId.substring(4, 6);

        Ubigeo ubigeo = new Ubigeo();
        Department department = catalogDao.getDepartments().get(departmentId);

        if (department != null) {
            ubigeo.setDepartment(department);
            Province province = ubigeo.getDepartment().getProvinces().get(provinceId);

            if (province != null) {
                ubigeo.setProvince(province);
                District district = province.getDistricts().get(districtId);

                if (district != null) {
                    ubigeo.setDistrict(district);
                }
            }
        }

        return ubigeo;
    }

    @Override
    public List<Department> getDepartments() throws Exception {
        return new ArrayList<>(catalogDao.getDepartments().values());
    }

    @Override
    public Department getDepartmentById(String departmentId) throws Exception {
        return getDepartments().stream().filter(d -> d.getId().equals(departmentId)).findFirst().orElse(null);
    }

    @Override
    public List<Province> getProvinces(String departmentId) throws Exception {
        if (departmentId == null) {
            return null;
        }

        return new ArrayList<>(catalogDao.getDepartments().get(departmentId)
                .getProvinces().values());
    }

    @Override
    public Province getProvinceById(String departmentId, String provinceId) throws Exception {
        return getProvinces(departmentId).stream().filter(p -> p.getId().equals(provinceId)).findFirst().orElse(null);
    }

    @Override
    public List<District> getDistricts(String departmentId, String provinceId) throws Exception {
        if (departmentId == null || provinceId == null) {
            return null;
        }

        if (catalogDao.getDepartments() == null || catalogDao.getDepartments().get(departmentId).getProvinces() == null ||
                catalogDao.getDepartments().get(departmentId).getProvinces().get(provinceId).getDistricts() == null) {
            return null;
        }

        return new ArrayList<>(catalogDao.getDepartments().get(departmentId)
                .getProvinces().get(provinceId)
                .getDistricts().values());
    }

    @Override
    public District getDistrictById(String departmentId, String provinceId, String districtId) throws Exception {
        return getDistricts(departmentId, provinceId).stream().filter(p -> p.getId().equals(districtId)).findFirst().orElse(null);
    }


    @Override
    @org.springframework.context.event.EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
        try {
            System.out.println("DENTRO DE EVENTO DE REFRESCO DE APP");

            getIdentityDocumentTypes();
            getStreetTypes();
            getDepartments();
            getBanks(false);
            getBanks(true);
            getProducts();
            getUserFileTypes();
            getCreditRejectionReasons(false);
            getCreditRejectionReasons(true);
            getEntities();
            getBots();
            getInteractionTypes();
            getInteractionContents();
            getContactResults();
            getTranches();
            getRccEntities();
            getRccAccounts();
            getConsolidationAccounttypes();
            getLoanApplicationRegisterType();
            getHumanForms();
            getEntityProducts();
            getAgents();
            getGeneralDepartment();
            getGeneralProvince();
            getGeneralDistrict();
            getGuaranteedVehicles();
            getMileages();
            getMaintainedCarBrands();
            ValidatorUtil.configure(this);
            if (Configuration.hostEnvIsQA()) {
                logger.debug("QA enviroment");
                callTestJobs();
            }

        } catch (Throwable th) {
            ErrorServiceImpl.onErrorStatic(th);
        }
    }


    @Override
    public List<IdentityDocumentType> getIdentityDocumentTypes() {
        return catalogDao.getIdentityDocumentTypes();
    }

    @Override
    public IdentityDocumentType getIdentityDocumentType(int documentTypeId) {
        List<IdentityDocumentType> types = new ArrayList<>(catalogDao.getIdentityDocumentTypes());
        return types.stream().filter(d -> d.getId() == documentTypeId).findFirst().orElse(null);
    }

    @Override
    public List<IdentityDocumentType> getIdentityDocumentTypeByCountry(int countryId, Boolean taxIdentifierFilter) {
        List<IdentityDocumentType> types = new ArrayList<>(catalogDao.getIdentityDocumentTypes());
        if (taxIdentifierFilter != null)
            return types.stream().filter(d -> d.getCountryId() == countryId && d.getTaxIdentifier() == taxIdentifierFilter.booleanValue()).collect(Collectors.toList());
        else
            return types.stream().filter(d -> d.getCountryId() == countryId).collect(Collectors.toList());
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasons(Locale locale) {
        List<LoanApplicationReason> reasons = new ArrayList<>(catalogDao.getLoanApplicationReasons());
        return reasons;
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasonsVisible(Locale locale) {
        List<LoanApplicationReason> reasons = new ArrayList<>(catalogDao.getLoanApplicationReasons().stream().filter(e -> e.isVisible()).collect(Collectors.toList()));
        return reasons;
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasonsByCategory(Locale locale, Boolean visible, int categoryId) throws Exception {
        List<LoanApplicationReason> reasons;
        if (visible != null) {
            reasons = getLoanApplicationReasons(locale).stream().filter(r -> r.isVisible() == visible).collect(Collectors.toList());
        } else {
            reasons = getLoanApplicationReasons(locale);
        }

        reasons.sort((reason1, reason2) -> reason1.getId() - reason2.getId());
        return reasons.stream()
                .filter(r -> r.getReasonCategoryId() != null && r.getReasonCategoryId() == categoryId)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasonsByCategoryAndEntityActivatedProducts(Locale locale, Boolean visible, int categoryId, int entityId) throws Exception {
        List<Integer> entityProductIds = getEntityProductsByEntity(entityId).stream().mapToInt(e -> e.getProduct().getId()).boxed().collect(Collectors.toList());

        return getLoanApplicationReasonsByCategory(locale, true, categoryId)
                .stream()
                .filter(r -> entityProductIds.stream().anyMatch(e -> r.containsProduct(e)) || r.getProductIds() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasonsVisibles(Locale locale) throws Exception {
        List<LoanApplicationReason> reasons = getLoanApplicationReasons(locale);
        return reasons.stream().filter(r -> r.isVisible()).collect(Collectors.toList());
    }

    @Override
    public List<LoanApplicationReason> getLoanApplicationReasonsMini(Locale locale) throws Exception {
        List<LoanApplicationReason> reasonsMini = getLoanApplicationReasonsVisibles(locale)
                .stream().collect(Collectors.toList());
        for (LoanApplicationReason reason : reasonsMini) {
            reason.setReason(messageSource.getMessage("loanapplication.reason.mini." + reason.getId(), null, locale));
        }
        return reasonsMini;
    }

    @Override
    public LoanApplicationReason getLoanApplicationReason(Locale locale, int reasonId) {
        return getLoanApplicationReasons(locale).stream().filter(r -> r.getId() == reasonId).findFirst().orElse(null);
    }

    @Override
    public List<MaritalStatus> getMaritalStatus(Locale locale) {
        List<MaritalStatus> status = new ArrayList<>(catalogDao.getMaritalStatus());
        for (MaritalStatus stat : status) {
            stat.setStatus(messageSource.getMessage(stat.getMessageKey(), null, locale));
        }
        return status;
    }

    @Override
    public MaritalStatus getMaritalStatus(Locale locale, int maritalStatusId) {
        return getMaritalStatus(locale).stream().filter(m -> m.getId() == maritalStatusId).findFirst().orElse(null);
    }

    @Override
    public List<StreetType> getStreetTypes() throws Exception {
        List<StreetType> streetTypes = new ArrayList<>(catalogDao.getStreetTypes());
        streetTypes.removeIf(e -> Configuration.streetTypesExclusiveBanBif.contains(e.getId()));
        streetTypes.removeIf(e -> Configuration.streetTypesExclusiveAzteca.contains(e.getId()));

        return streetTypes;
    }

    @Override
    public List<StreetType> getStreetTypesBanBif() throws Exception {
        List<StreetType> streetTypesBanBif = new ArrayList<>();
        List<StreetType> streetTypes = catalogDao.getStreetTypes();

        Configuration.streetTypesBanBif.forEach(areaTypeId -> {
            for (StreetType streetType : streetTypes) {
                if ( streetType.getId().equals(areaTypeId) ) {
                    streetTypesBanBif.add(streetType);
                    break;
                }
            }
        });

        return streetTypesBanBif;
    }

    @Override
    public List<StreetType> getStreetTypesAzteca() throws Exception {
        List<StreetType> streetTypesAzteca = new ArrayList<>();
        List<StreetType> streetTypes = catalogDao.getStreetTypes();

        Configuration.streetTypesAzteca.forEach(areaTypeId -> {
            for (StreetType streetType : streetTypes) {
                if ( streetType.getId().equals(areaTypeId) ) {
                    streetTypesAzteca.add(streetType);
                    break;
                }
            }
        });
        return streetTypesAzteca;
    }

    @Override
    public List<StreetType> getStreetTypesAcceso() throws Exception {
        List<StreetType> streetTypesAcceso = new ArrayList<>();
        List<StreetType> streetTypes = catalogDao.getStreetTypes();

        Configuration.streetTypesAcceso.forEach(areaTypeId -> {
            for (StreetType streetType : streetTypes) {
                if (streetType.getId().equals(areaTypeId)) {
                    streetTypesAcceso.add(streetType);
                    break;
                }
            }
        });
        return streetTypesAcceso;
    }

    @Override
    public StreetType getStreetType(int streetTypeId) throws Exception {
        return getStreetTypes().stream().filter(s -> s.getId() == streetTypeId).findFirst().orElse(null);
    }

    @Override
    public List<ActivityType> getActivityTypes(Locale locale, Boolean principal) {
        List<ActivityType> types = new ArrayList<>(catalogDao.getActivityTypes());
        for (ActivityType type : types) {
            type.setType(messageSource.getMessage(type.getMessageKey(), null, locale));
        }

        if (principal != null) {
            return types.stream().filter(a -> principal ? a.getPrincipal() : a.getSecundary()).collect(Collectors.toList());
        } else {
            return types;
        }
    }

    @Override
    public ActivityType getActivityType(Locale locale, int activityTypeId) {
        return getActivityTypes(locale, null).stream().filter(a -> a.getId() == activityTypeId).findFirst().orElse(null);
    }

//    @Override
//    public Ubigeo getUbigeo(String ubigeoId) throws Exception {
//        if (ubigeoId == null) {
//            return null;
//        }
//        String departmentId = ubigeoId.substring(0, 2);
//        String provinceId = ubigeoId.substring(2, 4);
//        String districtId = ubigeoId.substring(4, 6);
//
//        Ubigeo ubigeo = new Ubigeo();
//        Optional<Department> department = getDepartments().stream().filter(d -> d.getId().equals(departmentId)).findFirst();
//        department.ifPresent(dep -> {
//            ubigeo.setDepartment(dep);
//            Optional<Province> province = ubigeo.getDepartment().getProvinces().stream().filter(p -> p.getId().equals(provinceId)).findFirst();
//            province.ifPresent(pro -> {
//                ubigeo.setProvince(pro);
//                Optional<District> district = ubigeo.getProvince().getDistricts().stream().filter(d -> d.getId().equals(districtId)).findFirst();
//                district.ifPresent(d ->
//                        ubigeo.setDistrict(d)
//                );
//            });
//        });
//        return ubigeo;
//    }
//
//    @Override
//    public List<Department> getDepartments() throws Exception {
//        return new ArrayList<>(catalogDao.getDepartments());
//    }
//
//    @Override
//    public Department getDepartmentById(String departmentId) throws Exception {
//        return getDepartments().stream().filter(d -> d.getId().equals(departmentId)).findFirst().orElse(null);
//    }
//
//    @Override
//    public List<Province> getProvinces(String departmentId) throws Exception {
//        if (departmentId == null) {
//            return null;
//        }
//        return getDepartments().stream().filter(d -> d.getId().equals(departmentId))//Unique province list
//                .flatMap(d -> d.getProvinces().stream())//Stream of every districs of each province in list
//                .collect(Collectors.toList());//convert to list
//    }
//
//    @Override
//    public List<District> getDistricts(String departmentId, String provinceId) throws Exception {
//        if (departmentId == null || provinceId == null) {
//            return null;
//        }
//        return getProvinces(departmentId).stream().filter(p -> p.getId().equals(provinceId))//Unique province list
//                .flatMap(p -> p.getDistricts().stream())//Stream of every districs of each province in list
//                .collect(Collectors.toList());//convert to list
//    }

    @Override
    public List<Bank> getBanks(Integer entityId, boolean isDisbursement) {
        return getBanks(entityId, isDisbursement, CountryParam.COUNTRY_PERU);
    }

    @Override
    public List<Bank> getBanks(boolean byCountry, boolean justPayments, Integer country) {
        List<Bank> banks = new ArrayList<>(catalogDao.getBanks());
        if (justPayments) {
            banks = banks.stream().filter(e -> e.getPayments()).collect(Collectors.toList());
        }
        banks.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
        return banks;
    }

    @Override
    public List<Bank> getBanks(Integer entityId, boolean isDisbursement, CountryParam countryParam) {
        Integer countryId = countryParam == null ? CountryParam.COUNTRY_PERU : countryParam.getId();
        return getBanks(entityId, isDisbursement, countryId);
    }

    @Override
    public List<Bank> getBanks(Integer entityId, boolean isDisbursement, Integer countryId) {
        List<Bank> banks = new ArrayList<>(catalogDao.getBanks());
        List<Bank> entityBanks = null;
        List<Bank> finalBanks = banks.stream().filter(e -> e.getCountry() != null && e.getCountry().equals(countryId)).collect(Collectors.toList());

        if (isDisbursement) {
            finalBanks = finalBanks.stream().filter(e -> e.getDisbursement()).collect(Collectors.toList());
        }

        if (entityId != null) {
            entityBanks = banks.stream().filter(e -> e.getEntity() != null && e.getEntity().getId().equals(entityId)).collect(Collectors.toList());
        }

        if (entityBanks != null)
            finalBanks.addAll(entityBanks);
        finalBanks.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
        return finalBanks;
    }

    @Override
    public List<Bank> getBanks(boolean justPayments) {
        return getBanks(justPayments, CountryParam.COUNTRY_PERU);
    }

    @Override
    public List<Bank> getBanks(boolean justPayments, Integer countryId) {
        List<Bank> banks = getBanks(0, false, countryId);

        if (justPayments) {
            banks = banks.stream().filter(e -> e.getPayments()).collect(Collectors.toList());
        }

        banks.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
        return banks;
    }

    @Override
    public Bank getBank(int bankId) {
        return (new ArrayList<>(catalogDao.getBanks())).stream().filter(b -> b.getId() == bankId).findFirst().orElse(null);
    }

    @Override
    public List<Product> getProducts() {
        return new ArrayList<>(catalogDao.getProducts());
    }

    @Override
    public List<Product> getActiveProducts() {
        return new ArrayList<>(catalogDao.getProducts().stream().filter(Product::getActive).filter(distinctByKey(Product::getId)).collect(Collectors.toList()));
    }

    @Override
    public List<Product> getActiveProductsByCategory(int productCategoryId) throws Exception {
        return getProducts().stream()
                .filter(p -> p.getProductCategoryId() == productCategoryId && p.getActive()).collect(Collectors.toList());
    }

    @Override
    public Product getProduct(int id) {
        return getProducts().stream().filter(p -> p.getId() == id).findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> getProductsEntity() {
        return new ArrayList<>(catalogDao.getProductsEntity());
    }

    @Override
    public List<FraudAlert> getFraudAlerts() {
        return new ArrayList<>(catalogDao.getFraudAlerts());
    }

    @Override
    public List<FraudFlagRejectionReason> getFraudAlertRejectionReasons() {
        return new ArrayList<>(catalogDao.getFraudAlertRejectionReasons());
    }

    ;

    @Override
    public FraudFlagRejectionReason getFraudAlertRejectionReason(Integer fraudAlertRejectionReasonId) {
        return getFraudAlertRejectionReasons().stream().filter(p -> p.getId().equals(fraudAlertRejectionReasonId))
                .findFirst().orElse(null);
    }

    @Override
    public List<FraudFlag> getFraudFlags() {
        return new ArrayList<>(catalogDao.getFraudFlags());
    }

    @Override
    public FraudFlag getFraudFlag(Integer fraudFlagId) {
        return getFraudFlags().stream().filter(p -> p.getId().equals(fraudFlagId))
                .findFirst().orElse(null);
    }

    @Override
    public FraudAlert getFraudAlert(int id) {
        return getFraudAlerts().stream().filter(p -> p.getFraudAlertId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public List<FraudAlertStatus> getFraudAlertStatus() {
        return new ArrayList<>(catalogDao.getFraudAlertStatus());
    }

    @Override
    public FraudAlertStatus getFraudAlertStatus(Integer fraudAlertStatusId) {
        return getFraudAlertStatus().stream().filter(p -> p.getId().equals(fraudAlertStatusId))
                .findFirst().orElse(null);
    }

    @Override
    public List<FraudFlagStatus> getFraudFlagStatus() {
        return new ArrayList<>(catalogDao.getFraudFlagStatus());
    }

    @Override
    public FraudFlagStatus getFraudFlagStatus(Integer fraudFlagStatusId) {
        return getFraudFlagStatus().stream().filter(p -> p.getId().equals(fraudFlagStatusId))
                .findFirst().orElse(null);
    }

    @Override
    public List<Nationality> getNationalities(Locale locale) {
        List<Nationality> lstNationalities = new ArrayList<>(catalogDao.getNationalities());
        for (Nationality nat : lstNationalities) {
            nat.setName(messageSource.getMessage(nat.getMessageKey(), null, locale));
        }
        //reorder since locale changes the name value.
        lstNationalities.sort((n1, n2) -> n1.getName().compareTo(n2.getName()));
        return lstNationalities;
    }

    @Override
    public List<Belonging> getBelongings(Locale locale) throws Exception {
        List<Belonging> lstBelongings = new ArrayList<>(catalogDao.getBelongings());
        for (Belonging bel : lstBelongings) {
            bel.setName(messageSource.getMessage(bel.getMessageKey(), null, locale));
        }
        return lstBelongings;
    }

    @Override
    public Belonging getBelonging(Locale locale, int belongingId) throws Exception {
        return getBelongings(locale).stream().filter(a -> a.getId() == belongingId).findFirst().orElse(null);
    }

    @Override
    public List<VoucherType> getVoucherTypes(Locale locale) throws Exception {
        List<VoucherType> lstVoucherTypes = new ArrayList<>(catalogDao.getVoucherTypes());
        for (VoucherType vou : lstVoucherTypes) {
            vou.setName(messageSource.getMessage(vou.getMessageKey(), null, locale));
        }
        return lstVoucherTypes;
    }

    @Override
    public VoucherType getVoucherType(Locale locale, int voucherTypeId) throws Exception {
        return getVoucherTypes(locale).stream().filter(a -> a.getId() == voucherTypeId).findFirst().orElse(null);
    }

    @Override
    public List<PensionPayer> getPensionPayers(Locale locale) throws Exception {
        List<PensionPayer> lstPensionPayers = new ArrayList<>(catalogDao.getPensionPayer());
        for (PensionPayer pen : lstPensionPayers) {
            pen.setName(messageSource.getMessage(pen.getMessageKey(), null, locale));
        }
        return lstPensionPayers;
    }

    @Override
    public PensionPayer getPensionPayer(Locale locale, int pensionPayerId) throws Exception {
        return getPensionPayers(locale).stream().filter(a -> a.getId() == pensionPayerId).findFirst().orElse(null);
    }

    @Override
    public Nationality getNationality(Locale locale, int nationalityId) {
        return getNationalities(locale).stream().filter(n -> n.getId() == nationalityId).findFirst().orElse(null);
    }

    @Override
    public List<LoanApplicationStatus> getLoanApplicationStatuses(Locale locale) {
        List<LoanApplicationStatus> lstLoanStatus = new ArrayList<>(catalogDao.getLoanApplicationStatuses());
        for (LoanApplicationStatus status : lstLoanStatus) {
            status.setStatus(messageSource.getMessage(status.getMessageKey(), null, locale));
        }
        return lstLoanStatus;
    }

    @Override
    public LoanApplicationStatus getLoanApplicationStatus(Locale locale, int loanApplicationStatusId) {
        return getLoanApplicationStatuses(locale).stream().filter(s -> s.getId() == loanApplicationStatusId).findFirst().orElse(null);
    }

    @Override
    public List<CreditStatus> getCreditStatuses(Locale locale) throws Exception {
        List<CreditStatus> lstCreditStatus = new ArrayList<>(catalogDao.getCreditStatuses());
        for (CreditStatus status : lstCreditStatus) {
            status.setStatus(messageSource.getMessage(status.getMessageKey(), null, locale));
        }
        return lstCreditStatus;
    }

    @Override
    public CreditStatus getCreditStatus(Locale locale, int creditStatusId) throws Exception {
        return getCreditStatuses(locale).stream().filter(c -> c.getId() == creditStatusId).findFirst().orElse(null);
    }

    @Override
    public List<UserFileType> getUserFileTypes() {
        List<UserFileType> userFilesTypes = new ArrayList<>(catalogDao.getUserFileTypes());
        Collections.sort(userFilesTypes, (left, right) -> left.getOrder() - right.getOrder());
        return userFilesTypes;
    }

    @Override
    public UserFileType getUserFileType(int userFileTypeId) {
        return getUserFileTypes().stream().filter(f -> f.getId() == userFileTypeId).findFirst().orElse(null);
    }

    @Override
    public List<Cluster> getClusters(Locale locale) throws Exception {
        List<Cluster> lstClusters = new ArrayList<>(catalogDao.getClusters());
        for (Cluster cluster : lstClusters) {
            if (cluster.getDescriptionKey() != null) {
                cluster.setDescription(messageSource.getMessage(cluster.getDescriptionKey(), null, locale));
            }
        }
        return lstClusters;
    }

    @Override
    public String getClustersJson(Locale locale) throws Exception {
        return new Gson().toJson(getClusters(locale));
    }

    @Override
    public Cluster getCluster(int clusterId, Locale locale) throws Exception {
        return getClusters(locale).stream().filter(c -> c.getId() == clusterId).findFirst().orElse(null);
    }

    @Override
    public List<CreditRejectionReason> getCreditRejectionReasons(boolean justDisbursement) throws Exception {
        List<CreditRejectionReason> creditRejectionReasonsAll = new ArrayList<>(catalogDao.getCreditRejectionReasons());
        if (justDisbursement) {
            creditRejectionReasonsAll.removeIf(e -> (!e.getDisbursement()));
        }
        return creditRejectionReasonsAll;
    }

    @Override
    public List<CreditRejectionReason> getCreditRejectionReasonsEntityProductParamExclusive(int entityProductParamId, boolean toAdd) throws Exception {
        List<CreditRejectionReason> creditRejectionReasons = new ArrayList<>(catalogDao.getCreditRejectionReasons());
        return creditRejectionReasons.stream().filter(r -> r.exclusiveForEntityProduct(entityProductParamId, toAdd)).collect(Collectors.toList());
    }

    @Override
    public List<CreditRejectionReason> getCreditRejectionReasonExtranet() throws Exception {
        List<CreditRejectionReason> creditRejectionReasons = new ArrayList<>(catalogDao.getCreditRejectionReasons());
        return creditRejectionReasons.stream().filter(r -> r.getExtranetEntity()).collect(Collectors.toList());
    }

    @Override
    public List<CreditRejectionReason> getCreditRejectionReasonExtranet(boolean justDisbursement) throws Exception {
        return getCreditRejectionReasonExtranet().stream().filter(r -> r.getDisbursement()).collect(Collectors.toList());
    }

    @Override
    public List<CreditRejectionReason> getCreditRejectionReasonExtranetOnlyVerification() throws Exception {
        List<CreditRejectionReason> creditRejectionReasons = new ArrayList<>(catalogDao.getCreditRejectionReasons());
        return creditRejectionReasons.stream().filter(r -> r.getVerificationEntity()).collect(Collectors.toList());
    }

    public CreditRejectionReason getCreditRejectionReason(int rejectionReasonId) throws Exception {
        return getCreditRejectionReasons(false).stream().filter(r -> r.getId() == rejectionReasonId).findFirst().orElse(null);
    }

    @Override
    public List<ApplicationRejectionReason> getApplicationRejectionReasons() throws Exception {
        return new ArrayList<>(catalogDao.getApplicationRejectionReasons());
    }

    @Override
    public ApplicationRejectionReason getApplicationRejectionReason(int rejectionReasonId) throws Exception {
        return getApplicationRejectionReasons().stream().filter(r -> r.getId() == rejectionReasonId).findFirst().orElse(null);
    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>(catalogDao.getEntities());
    }

    @Override
    public Entity getEntity(Integer entityId) {
        return getEntities().stream().filter(e -> e.getId().equals(entityId)).findFirst().orElse(null);
    }

    @Override
    public List<Relationship> getRelationships(Locale locale) throws Exception {
        List<Relationship> lstrelationship = new ArrayList<>(catalogDao.getRelationships());
        for (Relationship relationship : lstrelationship) {
            relationship.setRelationship(messageSource.getMessage(relationship.getMessageKey(), null, locale));
        }
        return lstrelationship;
    }

    @Override
    public List<Relationship> getRelationships(Integer relationshipId, Locale locale) throws Exception {
        List<Relationship> lstrelationship = new ArrayList<>(catalogDao.getRelationships().stream().filter(e -> !e.getId().equals(relationshipId)).collect(Collectors.toList()));
        for (Relationship relationship : lstrelationship) {
            relationship.setRelationship(messageSource.getMessage(relationship.getMessageKey(), null, locale));
        }
        return lstrelationship;
    }

    @Override
    public Relationship getRelationship(Integer relationshipId, Locale locale) throws Exception {
        return getRelationships(locale).stream().filter(r -> r.getId() == relationshipId).findFirst().orElse(null);
    }

    @Override
    public List<BufferTransactionType> getBufferTransactionTypes() throws Exception {
        return new ArrayList<>(catalogDao.getBufferTransactionType());
    }

    @Override
    public BufferTransactionType getBufferTransactionType(Integer bufferTransactionTypeId) throws Exception {
        return getBufferTransactionTypes().stream().filter(r -> r.getId() == bufferTransactionTypeId).findFirst().orElse(null);
    }

    @Override
    public List<Bot> getBots() throws Exception {
        return new ArrayList<>(catalogDao.getBots());
    }

    @Override
    public Bot getBot(Integer botId) throws Exception {
        return getBots().stream().filter(b -> b.getId() == botId).findFirst().orElse(null);
    }

    @Override
    public List<InteractionType> getInteractionTypes() {
        return new ArrayList<>(catalogDao.getInteractionTypes());
    }

    @Override
    public InteractionType getInteractionType(Integer interactionTypeId) {
        return getInteractionTypes().stream().filter(i -> i.getId().equals(interactionTypeId)).findFirst().orElse(null);
    }

    @Override
    public List<InteractionContent> getInteractionContents() {
        return new ArrayList<>(catalogDao.getInteractionContents());
    }

    @Override
    public InteractionContent getInteractionContent(Integer interactionContentId, Integer countryId) {
        InteractionContent content = getInteractionContents().stream()
                .filter(i -> i.getId().equals(interactionContentId) && (i.getCountryId() != null && countryId != null && i.getCountryId().equals(countryId)))
                .findFirst().orElse(null);
        if (content != null)
            return content.getCopy(this);
        return null;
    }

    @Override
    public List<ContactResult> getContactResults() throws Exception {
        return new ArrayList<>(catalogDao.getContactResults());
    }

    @Override
    public List<TrackingAction> getTrackingActions() throws Exception {
        return new ArrayList<>(catalogDao.getTrackingActions());
    }

    @Override
    public List<TrackingAction> getTrackingActionsByType(String type) throws Exception {
        return getTrackingActions().stream().filter(i -> i.getActive() && i.getTrackingActionCategory().equals(type)).collect(Collectors.toList());
    }

    @Override
    public List<TrackingDetail> getTrackingDetailsByTrackingAction(Integer trackingActionId) throws Exception {
        TrackingAction trackingAction = getTrackingActions().stream().filter(i -> i.getTrackingActionId().equals(trackingActionId)).findFirst().orElse(null);
        if (trackingAction != null) return trackingAction.getTrackingDetails();
        return null;
    }

    @Override
    public List<TrackingAction> getTrackingActionsByType(String type, String contactType) throws Exception {
        if (contactType.equals("E"))
            return getTrackingActions().stream().filter(i -> i.getActive() && i.getTrackingActionCategory().equals(type) && i.getTrackingActionId() == TrackingAction.SCHEDULE_PHONECALL).collect(Collectors.toList());
        else if (contactType.equals("C"))
            return getTrackingActions().stream().filter(i -> i.getActive() && i.getTrackingActionCategory().equals(type)).collect(Collectors.toList());
        return getTrackingActionsByType(type);
    }

    @Override
    public List<TrackingAction> getTrackingActionsByTypeAndScreen(String type, String contactType, String screen) throws Exception {
        if (contactType != null) {
            if (contactType.equals("E"))
                return getTrackingActions()
                        .stream()
                        .filter(i -> i.getActive() && i.getTrackingActionCategory().equals(type) && i.getTrackingActionId() == TrackingAction.SCHEDULE_PHONECALL)
                        .filter(i -> i.getArScreen().contains(screen))
                        .collect(Collectors.toList());
            else if (contactType.equals("C"))
                return getTrackingActions()
                        .stream()
                        .filter(i -> i.getActive() && i.getTrackingActionCategory().equals(type))
                        .filter(i -> i.getArScreen().contains(screen))
                        .collect(Collectors.toList());
        }
        return getTrackingActionsByType(type)
                .stream()
                .filter(i -> i.getArScreen() != null && i.getArScreen().contains(screen))
                .collect(Collectors.toList());
    }

    @Override
    public ContactResult getContactResult(Integer contactResultId) throws Exception {
        return getContactResults().stream().filter(i -> i.getId().equals(contactResultId)).findFirst().orElse(null);
    }

    @Override
    public List<ContactResult> getContactResultsByType(String type) throws Exception {
        return getContactResults().stream().filter(i -> i.getType().equals(type)).collect(Collectors.toList());
    }

    @Override
    public List<Tranche> getTranches() throws Exception {
        return new ArrayList<>(catalogDao.getTranches());
    }

    @Override
    public Tranche getTranche(Integer trancheId) throws Exception {
        return getTranches().stream().filter(i -> i.getId().equals(trancheId)).findFirst().orElse(null);
    }

    @Override
    public List<Employer> getEmployers() {
        return new ArrayList<>(catalogDao.getEmployers());
    }

    @Override
    public Employer getEmployer(Integer employerId) {
        return getEmployers().stream().filter(i -> i.getId().equals(employerId)).findFirst().orElse(null);
    }

    @Override
    public List<HelpMessage> getHelpMessages(Locale locale) throws Exception {
        List<HelpMessage> messages = new ArrayList<>(catalogDao.getHelpMessages());
        messages.stream().forEach(h -> {
            if (h.getTittleKey() != null) h.setTittle(messageSource.getMessage(h.getTittleKey(), null, locale));
        });
        messages.stream().forEach(h -> {
            if (h.getBodyKey() != null) h.setBody(messageSource.getMessage(h.getBodyKey(), null, locale));
        });
        return messages;
    }

    @Override
    public HelpMessage getHelpMessage(Integer helpMessageId, Locale locale) throws Exception {
        return getHelpMessages(locale).stream().filter(i -> i.getId().equals(helpMessageId)).findFirst().orElse(null);
    }

    @Override
    public List<RccEntity> getRccEntities() throws Exception {
        return new ArrayList<>(catalogDao.getRccEntiy());
    }

    @Override
    public RccEntity getRccEntity(String code) throws Exception {
        return getRccEntities().stream().filter(e -> e.getCode().equals(code)).findFirst().orElse(null);
    }

    @Override
    public List<RccAccount> getRccAccounts() throws Exception {
        return new ArrayList<>(catalogDao.getRccAccounts());
    }

    @Override
    public RccAccount getRccAccount(int id) throws Exception {
        return getRccAccounts().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ConsolidationAccountType> getConsolidationAccounttypes() throws Exception {
        return new ArrayList<>(catalogDao.getConsolidationAccountTypes());
    }

    @Override
    public List<Contract> getContracts() throws Exception {
        return new ArrayList<>(catalogDao.getContracts());
    }

    @Override
    public Contract getContractById(int contractId) throws Exception {
        return getContracts().stream().filter(c -> c.getId() == contractId).findFirst().orElse(null);
    }

    @Override
    public List<ProductAgeRange> getProductAgeRanges() throws Exception {
        return new ArrayList<>(catalogDao.getProductAgeRanges());
    }

    @Override
    public ProductAgeRange getProductAgeRange(int productId, int entityId) throws Exception {
        return getProductAgeRanges().stream().filter(e -> e.getProductId() == productId && e.getEntityId() == entityId).findFirst().orElse(null);
    }

    @Override
    public EntityProductParams getEntityProductParam(int entityId, int productId) throws Exception {
        return catalogDao.getEntityProductParams().stream().filter(e -> e.getEntity().getId() == entityId && e.getProduct().getId() == productId).findFirst().orElse(null);
    }

    @Override
    public EntityProductParams getEntityProductParamById(int entityProductParamId) throws Exception {
        return catalogDao.getEntityProductParams().stream().filter(e -> e.getId() == entityProductParamId).findFirst().orElse(null);
    }

    @Override
    public EntityBranding getEntityBranding(int entityId) throws Exception {
        return catalogDao.getEntityBranding().stream().filter(e -> e.getEntity().getId() == entityId).findFirst().orElse(null);
    }

    @Override
    public EntityBranding getEntityBrandingNoCache(int entityId) throws Exception {
        return catalogDao.getEntityBrandingByIdNoCache(entityId);
    }

    @Override
    public void updateEntityBrandingExtranetConfiguration(Integer entityId, EntityExtranetConfiguration entityExtranetConfiguration) throws Exception{
        catalogDao.updateEntityBrandingExtranetConfiguration(entityId,entityExtranetConfiguration);
    }

    @Override
    public EntityBranding getEntityBranding(String subdomain) throws Exception {
        return catalogDao.getEntityBranding().stream().filter(e -> e.getSubdomain() != null && e.getSubdomain().equals(subdomain)).findFirst().orElse(null);
    }

    @Override
    public List<EntityBranding> getBrandedEntities() throws Exception {
        return catalogDao.getEntityBranding().stream().filter(e -> e.getBranded() == true).collect(Collectors.toList());
    }

    @Override
    public List<LoanApplicationRegisterType> getLoanApplicationRegisterType() throws Exception {
        return new ArrayList<>(catalogDao.getLoanApplicationRegisterType());
    }

    @Override
    public LoanApplicationRegisterType getLoanApplicationRegisterTypeById(int registerTypeId) throws Exception {
        return getLoanApplicationRegisterType().stream().filter(e -> e.getRegisterTypeId() == registerTypeId).findFirst().orElse(null);
    }

    @Override
    public List<HumanForm> getHumanForms() throws Exception {
        return new ArrayList<>(catalogDao.getHumanForms());
    }

    @Override
    public HumanForm getHumanForm(int humanFormId) throws Exception {
        return getHumanForms().stream().filter(h -> h.getId() == humanFormId).findFirst().orElse(null);
    }

    @Override
    public List<EntityProduct> getEntityProducts() {
        return new ArrayList<>(catalogDao.getEntityProducts());
    }

    @Override
    public List<EntityProduct> getEntityProductsByEntity(int entityId) {
        return getEntityProducts().stream().filter(e -> e.getEntityId() == entityId).filter(distinctByKey(EntityProduct::getProduct)).collect(Collectors.toList());
    }

    @Override
    public List<EntityProduct> getEntityProductsByProduct(int productId) throws Exception {
        return getEntityProducts().stream().filter(e -> e.getProduct().getId() == productId).collect(Collectors.toList());
    }

    @Override
    public List<EntityProduct> getEntityProductsByEntityProduct(int entityId, int productId) {
        return getEntityProducts().stream().filter(e -> e.getEntityId() == entityId && e.getProduct().getId() == productId).collect(Collectors.toList());
    }

    @Override
    public HumanFormProductParam getHumanFormProductParam(int humanFormId, int productId) throws Exception {
        HumanForm humanform = getHumanForm(humanFormId);
        HumanFormProductParam humanformProductParam = new HumanFormProductParam(getProduct(productId));
        if (humanform != null && humanform.getParams() != null)
            humanformProductParam = humanform.getParams().stream().filter(p -> p.getProduct().getId() == productId).findFirst().orElse(null);
        return humanformProductParam;
    }

    @Override
    public List<Currency> getCurrencies() {
        return new ArrayList<>(catalogDao.getCurrency());
    }

    @Override
    public Currency getCurrency(int id) {
        return getCurrencies().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<LoanApplicationReasonCategory> getLoanApplicationReasonCategories(Locale locale) {
        List<LoanApplicationReasonCategory> categories = new ArrayList<>(catalogDao.getLoanReasonCategories());
        for (LoanApplicationReasonCategory category : categories) {
            category.setCategory(messageSource.getMessage(category.getMessageKey(), null, locale));
        }
        Collections.sort(categories, (categories1, categories2) -> categories1.getCategory().compareTo(categories2.getCategory()));
        return categories;
    }

    @Override
    public List<CreditUsage> getCreditUsages(Locale locale) {
        List<CreditUsage> usages = new ArrayList<>(catalogDao.getCreditUsages());
        for (CreditUsage usage : usages) {
            usage.setUsage(messageSource.getMessage(usage.getMessageKey(), null, locale));
        }
        return usages;
    }

    @Override
    public CreditUsage getCreditUsage(Locale locale, int id) {
        return getCreditUsages(locale).stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<HousingType> getHousingTypes(Locale locale) {
        List<HousingType> housingTypes = new ArrayList<>(catalogDao.getHousingTypes());
        for (HousingType type : housingTypes) {
            type.setType(messageSource.getMessage(type.getTextInt(), null, locale));
            if (type.getDescription() != null) {
                type.setDescription(messageSource.getMessage(type.getDescription(), null, locale));
            }
        }
        return housingTypes;
    }

    @Override
    public List<HousingType> getHousingTypes(Locale locale, boolean actives) {
        return getHousingTypes(locale).stream().filter(h -> h.getActive() == actives).collect(Collectors.toList());
    }

    @Override
    public HousingType getHousingType(Locale locale, int id) {
        return getHousingTypes(locale).stream().filter(h -> h.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<StudyLevel> getStudyLevels(Locale locale) {
        List<StudyLevel> studyLevels = new ArrayList<>(catalogDao.getStudyLevels());
        Iterator<StudyLevel> iter = studyLevels.iterator();

        while (iter.hasNext()) {
            StudyLevel level = iter.next();
            String levelName = messageSource.getMessage(level.getText(), null, locale);

            if (!levelName.trim().equals("")) {
                level.setLevel(levelName);
            } else {
                iter.remove();
            }
        }

        return studyLevels;
    }

    @Override
    public StudyLevel getStudyLevel(Locale locale, int id) {
        return getStudyLevels(locale).stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Profession> getProfessions(Locale locale) {
        List<Profession> professions = new ArrayList<>(catalogDao.getProfessions());
        professions.forEach(p -> p.setProfession(messageSource.getMessage(p.getTextInt(), null, locale)));

        Profession otherProfession = professions.stream().filter(p -> p.getId() == Profession.OTHER).findFirst().orElse(null);
        Collections.sort(professions, (profession1, profession2) -> profession1.getProfession().compareTo(profession2.getProfession()));

        // Send the other to the end
        professions.remove(otherProfession);
        professions.add(otherProfession);
        return professions;
    }

    /*public List<Profession> getProfessionsActive(Locale locale) {
        return getProfessions(locale).stream().filter(p -> p.getActive()).collect(Collectors.toList());
    }*/

    @Override
    public List<Profession> getProfessionsActive(Locale locale) {
        List<Profession> professions = new ArrayList<>(catalogDao.getProfessions().stream().filter(e -> e.getActive()).collect(Collectors.toList()));
        for (Profession profession : professions) {
            profession.setProfession(messageSource.getMessage(profession.getTextInt(), null, locale));
        }
        Profession otherProfession = professions.stream().filter(p -> p.getId() == Profession.OTHER).findFirst().orElse(null);
        Collections.sort(professions, (profession1, profession2) -> profession1.getProfession().compareTo(profession2.getProfession()));

        // Send the other to the end
        professions.remove(otherProfession);
        professions.add(otherProfession);
        return professions;
    }

    @Override
    public Profession getProfession(Locale locale, int id) {
        return getProfessions(locale).stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ServiceType> getServiceTypes(Locale locale) {
        List<ServiceType> types = new ArrayList<>(catalogDao.getServiceType());
        for (ServiceType type : types) {
            type.setServiceType(messageSource.getMessage(type.getTextInt(), null, locale));
        }
        Collections.sort(types, (left, right) -> left.getServiceType().compareTo(right.getServiceType()));
        return types;
    }

    @Override
    public ServiceType getServiceType(Locale locale, int id) {
        return getServiceTypes(locale).stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Ocupation> getOcupations(Locale locale) {
        List<Ocupation> ocupations = new ArrayList<>(catalogDao.getOcupations());
        for (Ocupation ocupation : ocupations) {
            ocupation.setOcupation(messageSource.getMessage(ocupation.getTextInt(), null, locale));
        }
//        Ocupation otherOcupation = ocupations.stream().filter(o -> o.getId() == Ocupation.OTHER).findFirst().orElse(null);
//        Collections.sort(ocupations, (left, right) -> left.getOcupation().compareTo(right.getOcupation()));
//
//        // Send the other to the end
//        ocupations.remove(otherOcupation);
//        ocupations.add(otherOcupation);
        return ocupations;
    }

    @Override
    public Ocupation getOcupation(Locale locale, int id) {
        return getOcupations(locale).stream().filter(o -> o.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<SubActivityType> getSubActivityTypes(Locale locale) throws Exception {
        List<SubActivityType> types = new ArrayList<>(catalogDao.getSubActivityTypes(locale));
        for (SubActivityType type : types) {
            type.setType(messageSource.getMessage(type.getMessageKey(), null, locale));
        }
        return types;
    }

    @Override
    public List<SubActivityType> getSubActivityTypesByActivity(Locale locale, int activityType) throws Exception {
        return getSubActivityTypes(locale).stream().filter(s -> s.getActivityType().getId() == activityType).collect(Collectors.toList());
    }

    @Override
    public SubActivityType getSubActivityType(Locale locale, int id) throws Exception {
        return getSubActivityTypes(locale).stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Agent> getAgents() {
        return new ArrayList<>(catalogDao.getAgents());
    }


    @Override
    public List<Agent> getAgents(Integer entityId) {
        if (entityId != null)
            return new ArrayList<>(catalogDao.getAgents().stream().filter(e ->
                    e.getEntity() != null && e.getEntity().getId().equals(entityId)
            ).collect(Collectors.toList()));
        return getAgents();
    }

    @Override
    public Agent getAgent(int id) {
        return getAgents().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Agent> getFormAssistantsAgents(Integer entityId) {
        if (entityId != null){
            List<Agent> entityAgents = getAgents().stream().filter(a -> a.getFormAssistant() != null && a.getFormAssistant() && a.getEntity() != null && a.getEntity().getId().equals(entityId)).collect(Collectors.toList());
            if(!entityAgents.isEmpty())
                return entityAgents;
        }
        return getAgents().stream().filter(a -> a.getFormAssistant() != null && a.getFormAssistant() && a.getEntity() == null && a.getId() != Agent.ALE_EFECTIVO_AL_TOQUE).collect(Collectors.toList());
    }

    @Override
    public Agent getHiddenAssistant() throws Exception {
        return getAgents().stream().filter(e -> e.getHiddenAssistant()).findFirst().orElse(null);
    }

    @Override
    public List<ProductCategory> getProductCategories(Locale locale) {
        List<ProductCategory> productCategories = catalogDao.getProductCategories();
        for (ProductCategory productCategory : productCategories) {
            productCategory.setCategoryName(messageSource.getMessage(productCategory.getCategoryName(), null, locale));
            productCategory.setMessage(messageSource.getMessage(productCategory.getMessage(), null, locale));
        }
        return productCategories;
    }

    @Override
    public List<ProductCategory> getProductCategoriesVisible(Integer countryId, Locale locale) {
        return getProductCategories(locale).stream().filter(c -> {
            ProductCategoryCountry pcc = c.getCountriesConfig().stream().filter(cc -> cc.getCategoryId().equals(c.getId()) && cc.getCountryId().equals(countryId)).findFirst().orElse(null);
            return pcc != null && pcc.getVisible();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ComparisonReason> getComparisonReasons(Locale locale) {
        List<ComparisonReason> reasons = new ArrayList<>(catalogDao.getComparisonReasons());
        for (ComparisonReason reason : reasons) {
            reason.setReason(messageSource.getMessage(reason.getReasonKey(), null, locale));
            reason.setSubreason(messageSource.getMessage(reason.getSubreasonKey(), null, locale));
        }
        return reasons;
    }

    @Override
    public List<ComparisonReason> getComparisonReasonsByGroupId(int groupId, Locale locale) {
        return getComparisonReasons(locale).stream().filter(c -> c.getReasonGroupId() == groupId).collect(Collectors.toList());
    }

    @Override
    public ComparisonReason getComparisonReason(int id, Locale locale) {
        return getComparisonReasons(locale).stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ComparisonCreditCost> getComparisonCreditCosts(Locale locale) {
        List<ComparisonCreditCost> costs = new ArrayList<>(catalogDao.getComparisonCreditCosts());
        for (ComparisonCreditCost cost : costs) {
            cost.setCost(messageSource.getMessage(cost.getCostKey(), null, locale));
        }
        return costs;
    }

    @Override
    public ComparisonCreditCost getComparisonCreditCost(int id, Locale locale) {
        return getComparisonCreditCosts(locale).stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public ComparisonRates getComparisonInfo(int bankId) {
        return catalogDao.getComparisonInfo(bankId);
    }

    @Override
    public ComparisonCost getCostValuesByCategoryCost(int rateId, int bankId, int categoryId, int costId) {
        ComparisonRates comparisonRates = catalogDao.getComparisonInfo(bankId);
        if (comparisonRates.getNewRates() != null && comparisonRates.getNewRates().size() > 0)
            return comparisonRates.getNewRates().stream().filter(e -> e.getCategoryId() == categoryId && e.getRateId() == rateId).findFirst().orElse(null).getCosts().stream().filter(i -> i.getId() == costId).findFirst().orElse(null);
        else
            return comparisonRates.getCurrentRates().stream().filter(e -> e.getCategoryId() == categoryId && e.getRateId() == rateId).findFirst().orElse(null).getCosts().stream().filter(i -> i.getId() == costId).findFirst().orElse(null);

    }

    @Override
    public Category getComparisonCategoryById(int categoryId) {
        return catalogDao.getComparisonCategory().stream().filter(e -> e.getId() == categoryId).findFirst().orElse(null);
    }

    @Override
    public List<Category> getComparisonCategory() {
        return new ArrayList<>(catalogDao.getComparisonCategory());
    }

    @Override
    public List<Cost> getCosts() throws Exception {
        return new ArrayList<>(catalogDao.getComparisonCosts());
    }

    @Override
    public Cost getComparisonCostById(int costId) throws Exception {
        return catalogDao.getComparisonCosts().stream().filter(e -> e.getId() == costId).findFirst().orElse(null);
    }

    @Override
    public List<CreditCardBrand> getBrands() throws Exception {
        return new ArrayList<>(catalogDao.getCreditCardBrand());
    }

    @Override
    public CreditCardBrand getBrandById(int brandId) throws Exception {
        return catalogDao.getCreditCardBrand().stream().filter(e -> e.getId() == brandId).findFirst().orElse(null);
    }

    @Override
    public List<FundableBankComparisonCategory> getFundableBankComparisonCategories() {
        return new ArrayList<>(catalogDao.getFundableBankComparisonCategories());
    }

    @Override
    public FundableBankComparisonCategory getFundableBankComparisonCategory(int bankId, int comparisonCategoryId) {
        return getFundableBankComparisonCategories().stream().filter(f -> f.getBank().getId() == bankId && f.getComparisonCategory().getId() == comparisonCategoryId).findFirst().orElse(null);
    }

    @Override
    public List<CreditCardBrand> getCreditCardBrands(Locale locale) throws Exception {
        return new ArrayList<>(catalogDao.getCreditCardBrand());
    }

    @Override
    public List<VehicleBrand> getVehicleBrands() {
        return new ArrayList<>(catalogDao.getVehicleBrands());
    }

    @Override
    public List<VehicleBrand> getActiveVehicleBrands() {
        return new ArrayList<>(catalogDao.getVehicleBrands()).stream().filter(b -> b.getActive()).collect(Collectors.toList());
    }

    @Override
    public VehicleBrand getVehicleBrand(int vehicleBrandId) {
        return getVehicleBrands().stream().filter(e -> e.getId() == vehicleBrandId).findFirst().orElse(null);
    }

    @Override
    public List<Vehicle> getVehicles(Locale locale) {
        return new ArrayList<>(catalogDao.getVehicles(locale).stream().filter(v -> v.getActive()).collect(Collectors.toList()));
    }

    @Override
    public Vehicle getVehicle(int groupId) {
        return catalogDao.getVehicles(Configuration.getDefaultLocale()).stream().filter(e -> e.getGroupId() == groupId).findFirst().orElse(null);

    }

    @Override
    public Vehicle getVehicle(int vehicleId, Locale locale) {
        List<Vehicle> vehicles = catalogDao.getVehicles(locale);
        VehicleDetails vehicleDetailsTemp = new VehicleDetails();
        boolean success = false;
        for (Vehicle vehicle : vehicles) {
            for (VehicleDetails vehicleDetails : vehicle.getVehicleDetails()) {
                if (vehicleDetails.getId() == vehicleId) {
                    success = true;
                    vehicleDetailsTemp = vehicleDetails;
                    break;
                }
            }
            if (success) {
                List<VehicleDetails> details = new ArrayList<>();
                details.add(vehicleDetailsTemp);
                vehicle.setVehicleDetails(details);
                return vehicle;
            }
        }

        return null;
    }

    @Override
    public List<Bank> getBanksByVehicle(int vehicleId, Locale locale) {
        Vehicle vehicle = catalogDao.getVehicles(locale).stream().filter(e -> e.getGroupId() == vehicleId).findFirst().orElse(null);
        List<Bank> banks = new ArrayList<>();
        Set<Bank> bankSet = new HashSet<>();
        if (vehicle.getVehicleDealership() != null && vehicle.getVehicleDealership().getBankAccountList() != null)
            for (BankAccount bankAccount : vehicle.getVehicleDealership().getBankAccountList()) {
                bankSet.add(bankAccount.getBank());
            }

        banks.addAll(bankSet);

        return banks;
    }

    @Override
    public List<Avenue> getAvenues() {
        return new ArrayList<>(catalogDao.getAvenues());
    }

    @Override
    public Avenue getAvenuesById(Integer avenueId) {
        return catalogDao.getAvenues().stream().filter(e -> e.getId().equals(avenueId)).findFirst().orElse(null);
    }

    @Override
    public List<HouseType> getHouseType() {
        return new ArrayList<>(catalogDao.getHouseTypes());
    }

    @Override
    public HouseType getHouseTypeById(Integer houseTypeId) {
        return getHouseType().stream().filter(e -> e.getId().equals(houseTypeId)).findFirst().orElse(null);
    }

    @Override
    public List<AreaType> getAreaType() {
        List<AreaType> areaTypes = new ArrayList<>(catalogDao.getAreaTypes());
        areaTypes.removeIf(e -> Configuration.areaTypesExclusiveBanBif.contains(e.getId()));
        areaTypes.removeIf(e -> Configuration.areaTypesExclusiveAzteca.contains(e.getId()));
        return areaTypes;
    }

    @Override
    public List<AreaType> getAreaTypesBanBif() {
        List<AreaType> areaTypesBanBif = new ArrayList<>();
        List<AreaType> areaTypes = catalogDao.getAreaTypes();

        Configuration.areaTypesBanBif.forEach(areaTypeId -> {
            for (AreaType areaType : areaTypes) {
                if ( areaType.getId().equals(areaTypeId) ) {
                    areaTypesBanBif.add(areaType);
                    break;
                }
            }
        });

        return areaTypesBanBif;
    }

    @Override
    public List<AreaType> getAreaTypesAzteca() {
        List<AreaType> areaTypesAzteca = new ArrayList<>();
        List<AreaType> areaTypes = catalogDao.getAreaTypes();

        Configuration.areaTypesAzteca.forEach(areaTypeId -> {
            for (AreaType areaType : areaTypes) {
                if ( areaType.getId().equals(areaTypeId) ) {
                    areaTypesAzteca.add(areaType);
                    break;
                }
            }
        });
        return areaTypesAzteca;
    }

    @Override
    public List<AreaType> getAreaTypesAcceso() {
        List<AreaType> areaTypesAcceso = new ArrayList<>();
        List<AreaType> areaTypes = catalogDao.getAreaTypes();

        Configuration.areaTypesAcceso.forEach(areaTypeId -> {
            for (AreaType areaType : areaTypes) {
                if ( areaType.getId().equals(areaTypeId) ) {
                    areaTypesAcceso.add(areaType);
                    break;
                }
            }
        });
        return areaTypesAcceso;
    }

    @Override
    public AreaType getAreaTypeById(Integer areaTypeId) {
        return getAreaType().stream().filter(e -> e.getId().equals(areaTypeId)).findFirst().orElse(null);
    }

    @Override
    public List<CreditSignatureScheduleHour> getCreditSignatureScheduleHours() {
        return new ArrayList<>(catalogDao.getCreditSignatureScheduleHours());
    }

    @Override
    public CreditSignatureScheduleHour getCreditSignatureScheduleHour(int id) {
        return getCreditSignatureScheduleHours().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<EntityProductParams> getEntityProductParams() throws Exception {
        return catalogDao.getEntityProductParams();
    }

    @Override
    public List<LoanApplicationAuditType> getLoanApplicationAuditTypes() {
        return catalogDao.getLoanApplicationAuditTypes();
    }

    @Override
    public LoanApplicationAuditType getLoanApplicationAuditType(int id) {
        return getLoanApplicationAuditTypes().stream().filter(l -> l.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<LoanApplicationAuditRejectionReason> getLoanApplicationAuditRejectionReasons() {
        return catalogDao.getLoanApplicationAuditRejectionReason();
    }

    @Override
    public List<LoanApplicationAuditRejectionReason> getLoanApplicationAuditRejectionReasonsByAuditType(int auditType) {
        return getLoanApplicationAuditRejectionReasons().stream().filter(r -> r.getAuditType().getId() == auditType).collect(Collectors.toList());
    }

    @Override
    public LoanApplicationAuditRejectionReason getLoanApplicationAuditRejectionReason(int id) {
        return getLoanApplicationAuditRejectionReasons().stream().filter(l -> l.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ProcessQuestion> getProcessQuestions() {
        return new ArrayList<>(catalogDao.getProcessQuestions());
    }

    @Override
    public ProcessQuestion getProcessQuestion(Integer id) {
        if (id == null)
            return null;

        return getProcessQuestions().stream().filter(c -> c.getId().intValue() == id).findFirst().orElse(null);
    }

    @Override
    public List<ProcessQuestionCategory> getProcessQuestionCategories() {
        return new ArrayList<>(catalogDao.getProcessQuestionCategories());
    }

    @Override
    public ProcessQuestionCategory getProcessQuestionCategory(int id) {
        return getProcessQuestionCategories().stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<DisbursementType> getDisbursementTypes() throws Exception {
        return catalogDao.getDisbursementType();
    }

    @Override
    public DisbursementType getDisbursementTypeById(Integer disbursementTypeId) throws Exception {
        return catalogDao.getDisbursementType().stream().filter(e -> e.getDisbursementTypeId().intValue() == disbursementTypeId).findFirst().orElse(null);
    }

    @Override
    public List<SendGridList> getSendGridLists() {
        return new ArrayList<>(catalogDao.getSendgridList());
    }

    @Override
    public List<CountryParam> getCountryParams() {
        return catalogDao.getCountryParams();
    }

    @Override
    public CountryParam getCountryParam(Integer countryId) {
        return getCountryParams().stream().filter(e -> e.getId().intValue() == countryId).findFirst().orElse(null);
    }

    @Override
    public CountryParam getCountryParamByCountryCode(String countryCode) {
        return getCountryParams().stream().filter(e -> e.getCountryCode().equalsIgnoreCase(countryCode)).findFirst().orElse(null);
    }

    @Override
    public List<VehicleGasType> getVehicleGasTypes(Locale locale) {
        List<VehicleGasType> types = new ArrayList<>(catalogDao.getVehicleGasType());
        for (VehicleGasType type : types) {
            type.setType(messageSource.getMessage(type.getI18nKey(), null, locale));
        }
        return types;
    }

    @Override
    public VehicleGasType getVehicleGasType(int vehicleGasTypeId, Locale locale) {
        return getVehicleGasTypes(locale).stream().filter(e -> e.getId() == vehicleGasTypeId).findFirst().orElse(null);
    }

    @Override
    public List<VehicleDealership> getVehicleDealerships(Locale locale) {
        return new ArrayList<>(catalogDao.getVehicleDealership(locale));
    }

    @Override
    public VehicleDealership getVehicleDealership(int dealershipId, Locale locale) {
        return getVehicleDealerships(locale).stream().filter(e -> e.getId() == dealershipId).findFirst().orElse(null);
    }

    @Override
    public List<CreditSubStatus> getCreditSubStatuses(Locale locale) {
        return new ArrayList<>(catalogDao.getCreditSubStatus(locale));
    }

    @Override
    public CreditSubStatus getCreditSubStatus(Locale locale, int id) {
        return getCreditSubStatuses(locale).stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Proxy> getProxies() throws Exception {
        return new ArrayList<>(catalogDao.getProxies());
    }

    @Override
    public Proxy getProxy(Integer proxyId) throws Exception {
        return getProxies().stream().filter(p -> p.getId().equals(proxyId)).findFirst().orElse(null);
    }

    @Override
    public Proxy getRandomProxyByCountry(Integer countryId) throws Exception {
        Random random = new Random();
        List<Proxy> proxies = getProxies().stream().filter(p -> p.getCountryId().equals(countryId) && !p.isBussy()).collect(Collectors.toList());

        if (proxies == null || proxies.isEmpty()) {
            return null;
        }

        return proxies.get(random.nextInt(proxies.size()));
    }

    @Override
    public List<Department> getGeneralDepartment() throws Exception {
        return catalogDao.getGeneralDeparments();
    }

    @Override
    public Department getGeneralDepartmentById(Integer departmentId) throws Exception {
        return catalogDao.getGeneralDeparments().stream().filter(e -> e.getDepartmentId().equals(departmentId)).findFirst().orElse(null);
    }

    @Override
    public List<Province> getGeneralProvince() throws Exception {
        return catalogDao.getGeneralProvinces();
    }

    @Override
    public List<Province> getGeneralProvince(Integer countryId) throws Exception {
        return catalogDao.getGeneralProvinces().stream().filter(e -> e.getCountry().getId().equals(countryId)).collect(Collectors.toList());
    }

    @Override
    public Province getGeneralProvinceById(Integer provinceId) throws Exception {
        return catalogDao.getGeneralProvinces().stream().filter(e -> e.getProvinceId().equals(provinceId)).findFirst().orElse(null);
    }

    @Override
    public List<District> getGeneralDistrict() throws Exception {
        return catalogDao.getGeneralDistricts().stream().filter(e -> e.getActive()).collect(Collectors.toList());
    }

    @Override
    public District getGeneralDistrictById(Long district) throws Exception {
        return catalogDao.getGeneralDistricts().stream().filter(e -> e.getActive() && e.getDistrictId().equals(district)).findFirst().orElse(null);
    }

    @Override
    public List<District> getGeneralDistrictByProvince(Integer provinceId) throws Exception {
        return catalogDao.getGeneralDistricts().stream().filter(e -> e.getProvince().getProvinceId().equals(provinceId)).collect(Collectors.toList());
    }

    @Override
    public List<District> getGeneralDistrictByProvincePostalCode(Integer provinceId, String postalCode) throws Exception {
        return catalogDao.getGeneralDistricts().stream().filter(e -> e.getProvince().getProvinceId().equals(provinceId) && e.getPostalCode().equals(postalCode) && e.getActive()).collect(Collectors.toList());
    }

    @Override
    public Province getGeneralProvinceByDistrict(Long districtId) throws Exception {
        District district = catalogDao.getGeneralDistricts().stream().filter(e -> e.getDistrictId().equals(districtId)).findFirst().orElse(null);
        if (district != null) return district.getProvince();
        return null;
    }

    @Override
    public List<Province> getGeneralProvinceByPostalCode(Integer countryId, String postalCode) throws Exception {
        List<District> districts = catalogDao.getGeneralDistricts().stream().filter(d -> d.getPostalCode() != null && d.getPostalCode().equals(postalCode)).collect(Collectors.toList());
        List<Province> provinces = districts.stream().filter(y -> y.getActive()).map(x -> x.getProvince()).distinct().collect(Collectors.toList());
        return provinces;
    }

    @Override
    public List<EntityWebService> getEntityWebServices() {
        return catalogDao.getEntityWebService();
    }

    @Override
    public EntityWebService getEntityWebService(int entityWebServiceId) {
        return getEntityWebServices().stream().filter(e -> e.getId().equals(entityWebServiceId)).findFirst().orElse(null);
    }

    @Override
    public List<Holiday> getHolidays() {
        return new ArrayList<>(catalogDao.getHolidays());
    }

    @Override
    public List<Holiday> getHolidaysByCountry(int countryId) {
        return getHolidays().stream().filter(h -> h.getCountryId().equals(countryId)).collect(Collectors.toList());
    }

    @Override
    public List<PhoneType> getPhoneTypes() {
        List<PhoneType> phoneTypes = new ArrayList<>();
        PhoneType phoneTypeL = new PhoneType("L", "Fijo");
        PhoneType phoneTypeM = new PhoneType("M", "Celular");
        phoneTypes.add(phoneTypeL);
        phoneTypes.add(phoneTypeM);
        return phoneTypes;
    }

    @Override
    public List<Sector> getSectors() {
        List<Sector> sectors = new ArrayList<>();
        Sector sectorPrivate = new Sector("PRI", "Privado");
        Sector sectorPublic = new Sector("PUB", "Público");
        sectors.add(sectorPrivate);
        sectors.add(sectorPublic);
        return sectors;
    }

    @Override
    public Sector getSectorById(String id) {
        return getSectors().stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Bureau> getBureaus() {
        return catalogDao.getBureaus();
    }

    @Override
    public Bureau getBureau(int id) {
        return getBureaus().stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<EmployerGroup> getEmployerGroups() {
        return new ArrayList<>(catalogDao.getEmployerGroups());
    }

    @Override
    public EmployerGroup getEmployerGroup(Integer employerGroupId) {
        return getEmployerGroups().stream().filter(i -> i.getId().equals(employerGroupId)).findFirst().orElse(null);
    }

    @Override
    public List<CorrectionFlow> getCorrectionFlows() {
        return catalogDao.getCorrectionFlows();
    }

    @Override
    public CorrectionFlow getCorrectionFlowsById(Integer correctionFlowId) {
        return catalogDao.getCorrectionFlows().stream().filter(e -> e.getId().equals(correctionFlowId)).findFirst().orElse(null);
    }

    @Override
    public List<ReturningReason> getReturningReasons() {
        return catalogDao.getReturningReasons();
    }

    @Override
    public ReturningReason getReturningReasonById(Integer returningReasonId) {
        return catalogDao.getReturningReasons().stream().filter(e -> e.getId().equals(returningReasonId)).findFirst().orElse(null);
    }

    @Override
    public void callTestJobs() {
        final String URL_TEST_FLUJO_CONSUMO = "http://solven:341a6fb6e68d7fb6f58ec5f0239908c6@167.99.10.165:9091/job/TestFlujoConsumo/build?token=T3stS0lvenC0nsum0";
        try {
            URL obj = new URL(URL_TEST_FLUJO_CONSUMO);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            //con.setRequestProperty("User-Agent", USER_AGENT);
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print result
            logger.debug("Sending POST request to URL: " + URL_TEST_FLUJO_CONSUMO + " to start test job");
            logger.debug("Response Code : " + responseCode);
            logger.debug(response.toString().substring(0, 30));

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (ProtocolException pe) {
            pe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public List<InstallmentStatus> getInstallmentStatuses() {
        return catalogDao.getInstallmentStatus();
    }

    @Override
    public InstallmentStatus getInstallmentStatusesById(int installmentStatusId) {
        return getInstallmentStatuses().stream().filter(e -> e.getId() == installmentStatusId).findFirst().orElse(null);
    }

    @Override
    public List<HardFilter> getHardFilters() throws Exception {
        return catalogDao.getHardFilters();
    }

    @Override
    public HardFilter getHardFilterById(int hardFilterId) throws Exception {
        return catalogDao.getHardFilters().stream().filter(e -> e.getId() == hardFilterId).findFirst().orElse(null);
    }

    @Override
    public List<Policy> getPolicies(){
        return catalogDao.getPolicies();
    }

    @Override
    public Policy getPolicyById(int policyId) {
        return catalogDao.getPolicies().stream().filter(e -> e.getPolicyId() == policyId).findFirst().orElse(null);
    }

    @Override
    public List<SysUserSchedule> getSchedules() throws Exception {
        return catalogDao.getSchedules();
    }

    @Override
    public SysUserSchedule getScheduleById(int scheduleId) throws Exception {
        return getSchedules().stream().filter(s -> s.getId() == scheduleId).findFirst().orElse(null);
    }

    @Override
    public List<OfferRejectionReason> getOfferRejectionReasons() throws Exception {
        return catalogDao.getOfferRejectionReasons();
    }

    @Override
    public List<OfferRejectionReason> getOfferRejectionReasonsByProductCategory(List<OfferRejectionReason> offerRejectionReasons, Integer productCategory) throws Exception {
        if(productCategory == null) return offerRejectionReasons;
        if(offerRejectionReasons == null) offerRejectionReasons = catalogDao.getOfferRejectionReasons();
        return offerRejectionReasons.stream().filter(e -> e.getProductCategory() == null || (e.getProductCategory() != null && e.getProductCategory().getId() == productCategory)).collect(Collectors.toList());
    }

    @Override
    public List<OfferRejectionReason> getOfferRejectionReasons(int entityId) throws Exception {
        if (entityId == Entity.EFECTIVA || entityId == Entity.CAJA_LOS_ANDES) {
            return getOfferRejectionReasons().stream().filter(e -> !Configuration.rejectedReasonsExclusiveBanBif.contains(e.getId())).collect(Collectors.toList());
        } else if (entityId == Entity.BANBIF) {
            List<OfferRejectionReason> offerRejectionReasons = getOfferRejectionReasons();
            List<OfferRejectionReason> sortInSpecificOrder = new ArrayList<>();

            Configuration.rejectedReasonsBanBif.forEach(e ->
                offerRejectionReasons
                        .stream().filter(o -> o.getId().equals(e))
                        .findFirst()
                        .ifPresent(sortInSpecificOrder::add)

            );

            return sortInSpecificOrder;
        }

        return getOfferRejectionReasons().stream().filter(r -> !r.getPhysicalProcess() && !Configuration.rejectedReasonsExclusiveBanBif.contains(r.getId())).collect(Collectors.toList());
    }

    @Override
    public OfferRejectionReason getOfferRejectionReason(int offerRejectionReasonId) throws Exception {
        return getOfferRejectionReasons().stream().filter(r -> r.getId().equals(offerRejectionReasonId)).findFirst().orElse(null);
    }

    @Override
    public int getBrandId(String brandName) {
        List<MaintainedCarBrand> vehicleBrandList = catalogDao.getMaintainedCarBrands();
        for (MaintainedCarBrand vehicleBrand : vehicleBrandList) {
            if (vehicleBrand.getBrand().equals(brandName)) {
                return vehicleBrand.getId();
            }
        }
        return -1;
    }

    @Override
    public MaintainedCarBrand getMaintainedCarBrand(Integer maintainedCarBrandId) {
        List<MaintainedCarBrand> vehicleBrandList = catalogDao.getMaintainedCarBrands();
        for (MaintainedCarBrand vehicleBrand : vehicleBrandList) {
            if (vehicleBrand.getId().equals(maintainedCarBrandId)) {
                return vehicleBrand;
            }
        }
        return null;
    }

    public List<MaintainedCarBrand> getMaintainedCarBrands() {
        return catalogDao.getMaintainedCarBrands();
    }

    @Override
    public List<Mileage> getMileages() {
        return catalogDao.getMileages();
    }

    @Override
    public Mileage getMileage(Integer id) {
        return getMileages().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<MaintainedCarBrand> getBrands(String model) {
        List<MaintainedCarBrand> maintainedCarBrands = catalogDao.getMaintainedCarBrands();
        Set<Integer> guaranteedVehicleIds = catalogDao.getGuaranteedVehicles().stream()
                .filter(m -> m.getModel().equals(model))
                .map(GuaranteedVehicle::getBrandId)
                .collect(Collectors.toSet());

        return maintainedCarBrands.stream()
                .filter(c -> guaranteedVehicleIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<GuaranteedVehicle> getGuaranteedVehicles() {
        return catalogDao.getGuaranteedVehicles();
    }

    @Override
    public List<String> getGuaranteedVehicleModels(Integer brandId) {
        return getGuaranteedVehicles().stream()
                .filter(v -> v.getBrandId().equals(brandId))
                .map(GuaranteedVehicle::getModel)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getGuaranteedVehicleModels() {
        return getGuaranteedVehicles().stream()
                .map(GuaranteedVehicle::getModel)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getGuaranteedVehicleYears(Integer brandId, String model) {
        return getGuaranteedVehicles().stream()
                .filter(v -> v.getBrandId().equals(brandId) && v.getModel().equals(model))
                .map(GuaranteedVehicle::getYear)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getYears(String model) {
        List<Integer> guaranteedVehicleYears = catalogDao.getGuaranteedVehicles().stream()
                .filter(m -> m.getModel().equals(model))
                .map(GuaranteedVehicle::getYear)
                .distinct()
                .collect(Collectors.toList());
        return guaranteedVehicleYears;
    }

    @Override
    public List<MaintainedCarBrand> getBrandsByYear(Integer year) {
        List<MaintainedCarBrand> maintainedCarBrands = catalogDao.getMaintainedCarBrands();
        Set<Integer> guaranteedVehicleIds = catalogDao.getGuaranteedVehicles().stream()
                .filter(m -> m.getYear().equals(year))
                .map(GuaranteedVehicle::getBrandId)
                .collect(Collectors.toSet());

        return maintainedCarBrands.stream()
                .filter(c -> guaranteedVehicleIds.contains(c.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getModelByYears(Integer year) {
        return getGuaranteedVehicles().stream()
                .filter(v -> v.getYear().equals(year))
                .map(GuaranteedVehicle::getModel)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getGuaranteedVehicleYears() {
        List<Integer> years = IntStream.rangeClosed(2011, 1900 + (new Date().getYear()))
                .boxed().collect(Collectors.toList());
        Collections.reverse(years);
        return years;
    }

    @Override
    public List<Mileage> getGuaranteedVehicleMileages(Integer brandId, String model, Integer year) {
        return getGuaranteedVehicles().stream()
                .filter(v -> v.getBrandId().equals(brandId) && v.getModel().equals(model) && v.getYear().equals(year) && v.getMileage() != null)
                .map(GuaranteedVehicle::getMileage)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public GuaranteedVehicle getGuaranteedVehicle(Integer brandId, Integer year, Integer mileageId, String model) {
        return getGuaranteedVehicles().stream()
                .filter(v -> v.getBrandId().equals(brandId) && v.getYear().equals(year) &&
                        v.getMileageId().equals(mileageId) && v.getModel().equals(model))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<AppoimentSchedule> getAppoimentSchedules() {
        return catalogDao.getAppoimentSchedules();
    }

    @Override
    public AppoimentSchedule getAppoimentSchedulesById(int appoimentScheduleId) {
        return catalogDao.getAppoimentSchedules().stream().filter(e -> e.getId() == appoimentScheduleId).findFirst().orElse(null);
    }

    @Override
    public List<SocioeconomicLevel> getSocioEconomicLevel() {
        return catalogDao.getSocioEconomicLevel();
    }

    @Override
    public SocioeconomicLevel getSocioEconomicLevelByLevel(String level) {
        return getSocioEconomicLevel().stream().filter(l -> l.getLevel().equals(level)).findFirst().orElse(null);
    }

    @Override
    public List<InteractionProvider> getInteractionProviders() {
        return catalogDao.getInteractionProviders();
    }

    @Override
    public InteractionProvider getInteractionProvider(Integer interactionProviderId) {
        return getInteractionProviders().stream().filter(i -> i.getId().equals(interactionProviderId)).findFirst().orElse(null);
    }

    @Override
    public List<SysUser> getManagementAnalystSysusers() {
        return catalogDao.getManagmentAnalystSysusers();
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public List<Agent> getUnbrandedAgents() {
        return getAgents().stream()
                .filter(e -> e.getEntity() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<ObservationReason> getCreditObservationReasons() {
        return catalogDao.getObservationReasons();
    }

    @Override
    public ObservationReason getCreditObservationReason(Integer observationId) {
        return getCreditObservationReasons().stream().filter(i -> i.getId().equals(observationId)).findFirst().orElse(null);
    }

    @Override
    public List<RetirementScheme> getRetirementSchemes() {
        return catalogDao.getRetirementSchemes();
    }

    @Override
    public RetirementScheme getRetirementSchemeById(int id) {
        return getRetirementSchemes().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<CustomProfession> getCustomProfessions() {
        return catalogDao.getCustomProfessions();
    }

    @Override
    public CustomProfession getCustomProfessionById(int id) {
        return getCustomProfessions().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<LeadProduct> getLeadsProductActivity() {
        List<LeadProduct> leadProducts = catalogDao.getLeadProducts();

        for (LeadProduct leadProduct : leadProducts) {
            leadProduct.setProduct(messageSource.getMessage(leadProduct.getProductPropertyKey(), null, Configuration.getDefaultLocale()));
        }
        return leadProducts;
    }

    @Override
    public List<LeadActivityType> getLeadActivityTypes() {
        List<LeadActivityType> leadActivityTypes = catalogDao.getLeadActivityTypes();

        for (LeadActivityType leadActivityType : leadActivityTypes) {
            leadActivityType.setActivityType(messageSource.getMessage(leadActivityType.getActivityPropertyKey(), null, Configuration.getDefaultLocale()));
        }
        return leadActivityTypes;
    }

    @Override
    public LeadProduct getLeadProductById(int id) {
        return getLeadsProductActivity().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public LeadActivityType getLeadActivityById(int id) {
        return getLeadActivityTypes().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<PreApprovedMail> getAllEmailTemplates() {
        return catalogDao.getPreApprovedMailingList();
    }

    @Override
    public List<AfipActivitiy> getAfipActivities() {
        return catalogDao.getAfipActivities();
    }

    @Override
    public AfipActivitiy getAfipActivityById(int id) {
        return getAfipActivities().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<PersonProfessionOccupation> getProfessionOccupations() {
        return catalogDao.getProfessionOccupation();
    }

    @Override
    public PersonProfessionOccupation getProfessionOccupation(int id) {
        return getProfessionOccupations().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<RateCommissionProduct> getRateCommissionProductByEntity(int entityId) {

        Entity entity = getEntity(entityId);
        List<RateCommissionProduct> products = new ArrayList<>();

//        Map<Integer, List<RateCommissionCluster>> clustersByProduct = new HashMap<>();
        List<RateCommissionCluster> rateCommissionClusters = new ArrayList<>(catalogDao.getRateCommissionClusters());

        // Get all the products availables
        List<Integer> productsAvailables = rateCommissionClusters
                .stream()
                .flatMap(c -> c.getRateCommissions().stream())
                .filter(c -> c.getEntityId() != null && c.getEntityId().equals(entityId))
                .mapToInt(c -> c.getProductId())
                .distinct()
                .boxed()
                .collect(Collectors.toList());

        for (Integer productId : productsAvailables) {

            Product productParams = getProductsEntity().stream().filter(p -> p.getId().equals(productId)).findFirst().orElse(null);
            ProductMaxMinParameter productMaxMinParameter;
            if (productParams.getProductParams(entity.getCountryId(), entityId) != null) {
                productMaxMinParameter = productParams.getProductParams(entity.getCountryId(), entityId);
            } else {
                productMaxMinParameter = productParams.getProductParams(entity.getCountryId());
            }


            RateCommissionProduct product = new RateCommissionProduct();
            product.setProductId(productId);
            product.setProduct(getProduct(productId).getName());

            // Get all the prices availables
            List<Integer> pricesAvailables = rateCommissionClusters
                    .stream()
                    .flatMap(c -> c.getRateCommissions().stream())
                    .filter(c -> c.getEntityId() != null && c.getEntityId().equals(entityId) && productId.equals(c.getProductId()))
                    .mapToInt(c -> c.getPriceId())
                    .distinct()
                    .boxed()
                    .collect(Collectors.toList());

            for (Integer priceId : pricesAvailables) {

                RateCommissionPrice price = new RateCommissionPrice();
                price.setPriceId(priceId);
                price.setPrice("Falta poner el nombre del price");

                // Get all cluster availables by product
                List<RateCommissionCluster> clusters = new ArrayList<>(rateCommissionClusters
                        .stream()
                        .filter(c -> c.getRateCommissions()
                                .stream()
                                .anyMatch(r ->
                                        r.getEntityId() != null
                                                && r.getEntityId().equals(entityId)
                                                && productId.equals(r.getProductId())
                                                && r.getPriceId().equals(priceId)))
                        .collect(Collectors.toList()));

                // Filter only the availabel rate commissions by entity/product
                clusters.forEach(c -> {
                    c.setRateCommissions(c.getRateCommissions()
                            .stream()
                            .filter(r -> r.getEntityId() != null && r.getEntityId().equals(entityId) && productId.equals(r.getProductId()))
                            .collect(Collectors.toList()));
                });

                // Order by cluster name
                clusters.sort(((e1, e2) -> e1.getCluster().compareToIgnoreCase(e2.getCluster())));

                // Order the rate commissions
                clusters.forEach(c -> {
                    // Fisrt sort the list
                    c.getRateCommissions().sort((Comparator.comparing(RateCommission::getInstallments)));
                    // Then, set the min installents
                    for (int i = 0; i < c.getRateCommissions().size(); i++) {
                        if(c.getRateCommissions().get(i).getMinInstallments() == null){
                            if (i == 0)
                                c.getRateCommissions().get(i).setMinInstallments(productMaxMinParameter.getMinInstallments());
                            else
                                c.getRateCommissions().get(i).setMinInstallments(c.getRateCommissions().get(i - 1).getInstallments() + 1);
                        }

                        if (c.getRateCommissions().get(i).getMinAmount() == null)
                            c.getRateCommissions().get(i).setMinAmount(productMaxMinParameter.getMinAmount());

                        if(entity.getCountryId() == CountryParam.COUNTRY_ARGENTINA)
                            c.getRateCommissions().get(i).setEffectiveAnualRate(utilService.teaToTna(c.getRateCommissions().get(i).getEffectiveAnualRate() / 100.0) * 100.0);
                    }
                });

                price.setClusters(clusters);
                product.getPrices().add(price);

                // fix
                rateCommissionClusters = new ArrayList<>(catalogDao.getRateCommissionClusters());
            }

            products.add(product);
        }
        return products;
    }

    @Override
    public List<EntityUserType> getEntityUserTypes() {
        return catalogDao.getEntityUserTypes();
    }

    @Override
    public EntityUserType getEntityUserType(int id) {
        return getEntityUserTypes().stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<ProcessQuestion> getQuestionsAfterOffer() {
        List<Integer> questionsAfterOffer = new ArrayList<>();
        questionsAfterOffer.add(ProcessQuestion.Question.Constants.VERIFICATION_DOCUMENTATION);
        questionsAfterOffer.add(ProcessQuestion.Question.Constants.OFFER);
        questionsAfterOffer.add(ProcessQuestion.Question.Constants.VERIFICATION_REFERRALS);
        questionsAfterOffer.add(ProcessQuestion.Question.Constants.CONTRACT_SIGNATURE);
        questionsAfterOffer.add(ProcessQuestion.Question.Constants.BANK_ACCOUNT_INFORMATION);
        questionsAfterOffer.add(ProcessQuestion.Question.Constants.OFFER_REJECTION_REASON);

        List<ProcessQuestion> processQuestions =
                getProcessQuestions()
                        .stream()
                        .filter(c -> questionsAfterOffer.contains(c.getId()))
                        .collect(Collectors.toList());

        processQuestions.forEach(c -> c.setQuestion(messageSource.getMessage("process.question.id." + c.getId(), null, Configuration.getDefaultLocale())));

        return processQuestions.stream()
                .filter(c -> !c.getQuestion().contains("process.question.id."))
                .sorted(Comparator.comparing(ProcessQuestion::getQuestion))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessQuestion> getQuestionsBeforeOffer() {
        List<Integer> categoriesBeforeOffer = new ArrayList<>();
        categoriesBeforeOffer.add(ProcessQuestionCategory.PRE_INFORMATION);
        categoriesBeforeOffer.add(ProcessQuestionCategory.PERSONAL_INFORMATION);
        categoriesBeforeOffer.add(ProcessQuestionCategory.INCOME);
        categoriesBeforeOffer.add(ProcessQuestionCategory.EVALUATION);

        List<ProcessQuestion> processQuestions =
                getProcessQuestions()
                        .stream()
                        .filter(c -> c.getCategory() != null)
                        .filter(c -> categoriesBeforeOffer.contains(c.getCategory().getId()))
                        .collect(Collectors.toList());

        processQuestions.forEach(c -> c.setQuestion(messageSource.getMessage("process.question.id." + c.getId(), null, Configuration.getDefaultLocale())));

        return processQuestions.stream()
                .filter(c -> !c.getQuestion().contains("process.question.id."))
                .sorted(Comparator.comparing(ProcessQuestion::getQuestion))
                .collect(Collectors.toList());
    }

    @Override
    public List<Department> getGeneralDepartment(Integer countryId) throws Exception {
        return catalogDao.getGeneralDeparments().stream().filter(e -> e.getCountry().getId().equals(countryId)).collect(Collectors.toList());
    }

    @Override
    public List<Province> getGeneralProvinces(Integer departmentId) throws Exception {
        if (departmentId == null) {
            return null;
        }

        return catalogDao.getGeneralProvinces()
                .stream()
                .filter(e -> e.getDepartment() != null && departmentId.equals(e.getDepartment().getDepartmentId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<District> getDistrictsByProvinceId(Integer provinceId) throws Exception {
        if (provinceId == null) {
            return null;
        }

        return catalogDao.getGeneralDistricts()
                .stream()
                .filter(e -> e.getProvince() != null && provinceId.equals(e.getProvince().getProvinceId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OccupationArea> getOccupationAreas(Locale locale) {
        List<OccupationArea> occupationAreas = new ArrayList<>(catalogDao.getOccupationAreas());
        for (OccupationArea area : occupationAreas) {
            area.setArea(messageSource.getMessage(area.getTextInt(), null, locale));
        }

        return occupationAreas;
    }

    @Override
    public List<RateCommissionCluster> getRateCommissionClusters() {
        return catalogDao.getRateCommissionClusters();
    }

    public List<EntityAcquisitionChannel> getEntityAcquisitionChannelsByEntity(int entityId) {
        return this.catalogDao.getEntityAcquisitionChannels()
                .stream()
                .filter(eac -> eac.getEntityId() == entityId)
                .collect(Collectors.toList());
    }

    @Override
    public EntityAcquisitionChannel getEntityAcquisitionChannelById(int entityAcquisitionId) {
        return this.catalogDao.getEntityAcquisitionChannels()
                .stream()
                .filter(eac -> eac.getEntityAcquisitionChannelId() == entityAcquisitionId)
                .findFirst().orElse(null);
    }

    @Override
    public ExtranetMenu getExtranetMenu(int extranetMenuId) {
        return this.catalogDao.getExtranetMenus()
                .stream()
                .filter(e -> e.getId() == extranetMenuId)
                .findFirst().orElse(null);
    }

    @Override
    public Role getRole(int roleId) {
        return this.catalogDao.getRoles()
                .stream()
                .filter(e -> e.getId() == roleId)
                .findFirst().orElse(null);
    }

    @Override
    public List<ApprovalValidation> getApprovalValidations() {
        return this.catalogDao.getApprovalValidations();
    }

    @Override
    public ApprovalValidation getApprovalValidation(int id) {
        return this.catalogDao.getApprovalValidations()
                .stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElse(null);
    }

    @Override
    public List<EntityProduct> getAllEntityProductsByEntityIncludeInactives(int entityId) {
        return catalogDao.getAllEntityProductsIncludeInactives().stream().filter(e -> e.getEntityId() == entityId).filter(distinctByKey(EntityProduct::getProduct)).collect(Collectors.toList());
    }

    @Override
    public List<CustomEntityActivity> getCustomEntityActivities(int entityId) throws Exception {
        return catalogDao.getCustomEntityActivities().stream().filter(e -> e.getEntity() != null &&  e.getEntity().getId().equals(entityId)).collect(Collectors.toList());
    }

    @Override
    public CustomEntityActivity getCustomActivity(int activityId) throws Exception {
        return catalogDao.getCustomEntityActivities().stream().filter(e -> e.getId().equals(activityId)).findFirst().orElse(null);
    }

    @Override
    public List<CustomEntityProfession> getCustomEntityProfessions(int entityId) throws Exception {
        return catalogDao.getCustomEntityProfessions().stream().filter(e -> e.getEntity() != null &&  e.getEntity().getId().equals(entityId)).collect(Collectors.toList());
    }

    @Override
    public CustomEntityProfession getCustomEntityProfession(int professionId) throws Exception {
        return catalogDao.getCustomEntityProfessions().stream().filter(e -> e.getId().equals(professionId)).findFirst().orElse(null);
    }
}