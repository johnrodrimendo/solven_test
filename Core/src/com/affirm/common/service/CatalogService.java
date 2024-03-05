package com.affirm.common.service;

import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.PreApprovedMail;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.security.model.SysUser;
import com.affirm.security.model.SysUserSchedule;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * @author jrodriguez
 */
public interface CatalogService {


    MessageSource getMessageSource();

    District getDistrictById(String departmentId, String provinceId, String districtId) throws Exception;

    @org.springframework.context.event.EventListener
    void handleContextRefresh(ContextRefreshedEvent event) throws Exception;

    List<IdentityDocumentType> getIdentityDocumentTypes() throws Exception;

    IdentityDocumentType getIdentityDocumentType(int documentTypeId);

    List<IdentityDocumentType> getIdentityDocumentTypeByCountry(int countryId, Boolean taxIdentifierFilter);

    List<LoanApplicationReason> getLoanApplicationReasons(Locale locale) throws Exception;

    List<LoanApplicationReason> getLoanApplicationReasonsByCategory(Locale locale, Boolean visible, int categoryId) throws Exception;

    List<LoanApplicationReason> getLoanApplicationReasonsByCategoryAndEntityActivatedProducts(Locale locale, Boolean visible, int categoryId, int entityId) throws Exception;

    List<LoanApplicationReason> getLoanApplicationReasonsVisibles(Locale locale) throws Exception;

    List<LoanApplicationReason> getLoanApplicationReasonsMini(Locale locale) throws Exception;

    LoanApplicationReason getLoanApplicationReason(Locale locale, int reasonId);

    List<LoanApplicationReason> getLoanApplicationReasonsVisible(Locale locale);

    List<MaritalStatus> getMaritalStatus(Locale locale) throws Exception;

    MaritalStatus getMaritalStatus(Locale locale, int maritalStatusId);

    List<StreetType> getStreetTypes() throws Exception;

    List<StreetType> getStreetTypesBanBif() throws Exception;

    List<StreetType> getStreetTypesAcceso() throws Exception;

    StreetType getStreetType(int streetTypeId) throws Exception;

    /**
     * Get the activities types
     *
     * @param locale    locale for the text
     * @param principal if true or false, return only the prpincipal or secundaries. If null, return all
     * @return
     * @throws Exception
     */
    List<ActivityType> getActivityTypes(Locale locale, Boolean principal);

    ActivityType getActivityType(Locale locale, int activityTypeId);

    <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale);

    <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale, Boolean active);

    <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale, Predicate<T> predicate);

    <T extends ICatalog> List<T> getCatalog(Class<T> clazz, Locale locale, Boolean active, Predicate<T> predicate);

    <T extends ICatalog> T getCatalogById(Class<T> clazz, Object id, Locale locale);

    Ubigeo getUbigeo(String ubigeoId) throws Exception;

    List<Department> getDepartments() throws Exception;

    Department getDepartmentById(String departmentId) throws Exception;

    List<Province> getProvinces(String departmentId) throws Exception;

    Province getProvinceById(String departmentId, String provinceId) throws Exception;

    List<District> getDistricts(String departmentId, String provinceId) throws Exception;

    List<Bank> getBanks(Integer entityId, boolean isDisbursement);

    List<Bank> getBanks(boolean justPayments);

    List<Bank> getBanks(boolean justPayments, Integer countryId);

    List<Bank> getBanks(boolean byCountry, boolean justPayments, Integer country);

    List<Bank> getBanks(Integer entityId, boolean isDisbursement, Integer countryId);

    List<Bank> getBanks(Integer entityId, boolean isDisbursement, CountryParam countryParam);

    Bank getBank(int bankId);

    List<Product> getProducts();

    List<Product> getActiveProductsByCategory(int productCategoryId) throws Exception;

    Product getProduct(int id);

    List<Product> getProductsEntity();

    List<FraudAlert> getFraudAlerts();

    FraudAlert getFraudAlert(int id);

    List<FraudFlagRejectionReason> getFraudAlertRejectionReasons();

    FraudFlagRejectionReason getFraudAlertRejectionReason(Integer fraudAlertRejectionReasonId);

    List<FraudFlag> getFraudFlags();

    FraudFlag getFraudFlag(Integer fraudFlagId);

    List<FraudAlertStatus> getFraudAlertStatus();

    FraudAlertStatus getFraudAlertStatus(Integer fraudAlertStatusId);

    List<FraudFlagStatus> getFraudFlagStatus();

    FraudFlagStatus getFraudFlagStatus(Integer fraudFlagStatusId);

    List<Nationality> getNationalities(Locale locale) throws Exception;

    List<Belonging> getBelongings(Locale locale) throws Exception;

    Belonging getBelonging(Locale locale, int belongingId) throws Exception;

    List<VoucherType> getVoucherTypes(Locale locale) throws Exception;

    VoucherType getVoucherType(Locale locale, int voucherTypeId) throws Exception;

    List<PensionPayer> getPensionPayers(Locale locale) throws Exception;

    PensionPayer getPensionPayer(Locale locale, int pensionPayerId) throws Exception;

    Nationality getNationality(Locale locale, int nationalityId);

    List<LoanApplicationStatus> getLoanApplicationStatuses(Locale locale);

    LoanApplicationStatus getLoanApplicationStatus(Locale locale, int loanApplicationStatusId);

    List<CreditStatus> getCreditStatuses(Locale locale) throws Exception;

    CreditStatus getCreditStatus(Locale locale, int creditStatusId) throws Exception;

    List<UserFileType> getUserFileTypes() throws Exception;

    UserFileType getUserFileType(int userFileTypeId);

    List<Cluster> getClusters(Locale locale) throws Exception;

    String getClustersJson(Locale locale) throws Exception;

    Cluster getCluster(int clusterId, Locale locale) throws Exception;

    List<CreditRejectionReason> getCreditRejectionReasons(boolean disbursement) throws Exception;

    List<CreditRejectionReason> getCreditRejectionReasonsEntityProductParamExclusive(int entityProductParamId, boolean toAdd) throws Exception;

    List<CreditRejectionReason> getCreditRejectionReasonExtranet() throws Exception;

    List<CreditRejectionReason> getCreditRejectionReasonExtranet(boolean justDisbursement) throws Exception;

    List<CreditRejectionReason> getCreditRejectionReasonExtranetOnlyVerification() throws Exception;

    CreditRejectionReason getCreditRejectionReason(int rejectionReasonId) throws Exception;

    List<ApplicationRejectionReason> getApplicationRejectionReasons() throws Exception;

    ApplicationRejectionReason getApplicationRejectionReason(int rejectionReasonId) throws Exception;

    List<Entity> getEntities();

    List<Relationship> getRelationships(Locale locale) throws Exception;

    Relationship getRelationship(Integer relationshipId, Locale locale) throws Exception;

    Entity getEntity(Integer entityId);

    List<BufferTransactionType> getBufferTransactionTypes() throws Exception;

    BufferTransactionType getBufferTransactionType(Integer bufferTransactionTypeId) throws Exception;

    List<Bot> getBots() throws Exception;

    Bot getBot(Integer botId) throws Exception;

    List<InteractionType> getInteractionTypes() throws Exception;

    InteractionType getInteractionType(Integer interactionTypeId);

    List<InteractionContent> getInteractionContents() throws Exception;

    InteractionContent getInteractionContent(Integer interactionContentId, Integer countryId);

    List<ContactResult> getContactResults() throws Exception;

    ContactResult getContactResult(Integer contactResultId) throws Exception;

    List<ContactResult> getContactResultsByType(String type) throws Exception;

    List<Tranche> getTranches() throws Exception;

    Tranche getTranche(Integer trancheId) throws Exception;

    List<Employer> getEmployers() throws Exception;

    Employer getEmployer(Integer employerId);

    List<HelpMessage> getHelpMessages(Locale locale) throws Exception;

    HelpMessage getHelpMessage(Integer helpMessageId, Locale locale) throws Exception;

    List<RccEntity> getRccEntities() throws Exception;

    RccEntity getRccEntity(String code) throws Exception;

    List<RccAccount> getRccAccounts() throws Exception;

    List<Contract> getContracts() throws Exception;

    RccAccount getRccAccount(int id) throws Exception;

    List<ConsolidationAccountType> getConsolidationAccounttypes() throws Exception;

    Contract getContractById(int contractId) throws Exception;

    List<ProductAgeRange> getProductAgeRanges() throws Exception;

    List<HumanForm> getHumanForms() throws Exception;

    ProductAgeRange getProductAgeRange(int productId, int entityId) throws Exception;

    EntityProductParams getEntityProductParam(int entityId, int productId) throws Exception;

    EntityProductParams getEntityProductParamById(int entityProductParamId) throws Exception;

    EntityBranding getEntityBranding(int entityId) throws Exception;

    EntityBranding getEntityBranding(String subdomain) throws Exception;

    List<EntityBranding> getBrandedEntities() throws Exception;

    List<LoanApplicationRegisterType> getLoanApplicationRegisterType() throws Exception;

    LoanApplicationRegisterType getLoanApplicationRegisterTypeById(int registerTypeId) throws Exception;

    HumanForm getHumanForm(int humanFormId) throws Exception;

    List<EntityProduct> getEntityProducts();

    List<EntityProduct> getEntityProductsByEntity(int entityId);

    List<EntityProduct> getEntityProductsByProduct(int productId) throws Exception;

    List<EntityProduct> getEntityProductsByEntityProduct(int entityId, int productId);

    HumanFormProductParam getHumanFormProductParam(int humanFormId, int productId) throws Exception;

    List<Currency> getCurrencies();

    Currency getCurrency(int id);

    List<LoanApplicationReasonCategory> getLoanApplicationReasonCategories(Locale locale);

    List<CreditUsage> getCreditUsages(Locale locale);

    CreditUsage getCreditUsage(Locale locale, int id);

    List<HousingType> getHousingTypes(Locale locale);

    List<HousingType> getHousingTypes(Locale locale, boolean actives);

    HousingType getHousingType(Locale locale, int id);

    List<StudyLevel> getStudyLevels(Locale locale);

    StudyLevel getStudyLevel(Locale locale, int id);

    List<Profession> getProfessions(Locale locale);

    Profession getProfession(Locale locale, int id);

    List<ServiceType> getServiceTypes(Locale locale);

    ServiceType getServiceType(Locale locale, int id);

    List<Ocupation> getOcupations(Locale locale);

    Ocupation getOcupation(Locale locale, int id);

    List<SubActivityType> getSubActivityTypes(Locale locale) throws Exception;

    List<SubActivityType> getSubActivityTypesByActivity(Locale locale, int activityType) throws Exception;

    SubActivityType getSubActivityType(Locale locale, int id) throws Exception;

    List<Agent> getAgents();

    Agent getAgent(int id);

    List<Agent> getFormAssistantsAgents(Integer entityId);

    Agent getHiddenAssistant() throws Exception;

    List<ProductCategory> getProductCategories(Locale locale);

    List<ComparisonReason> getComparisonReasons(Locale locale);

//    ProductCategory getProductCategory(int id);

    List<ComparisonReason> getComparisonReasonsByGroupId(int groupId, Locale locale);

    List<ComparisonCreditCost> getComparisonCreditCosts(Locale locale);

    ComparisonReason getComparisonReason(int id, Locale locale);

    ComparisonCreditCost getComparisonCreditCost(int id, Locale locale);

    ComparisonRates getComparisonInfo(int bankId);

    Category getComparisonCategoryById(int categoryId);

    Cost getComparisonCostById(int costId) throws Exception;

    List<Cost> getCosts() throws Exception;

    List<Category> getComparisonCategory();

    ComparisonCost getCostValuesByCategoryCost(int rateId, int bankId, int categoryId, int costId);

    List<CreditCardBrand> getBrands() throws Exception;

    List<VehicleBrand> getVehicleBrands();

    CreditCardBrand getBrandById(int brandId) throws Exception;

    List<FundableBankComparisonCategory> getFundableBankComparisonCategories();

    FundableBankComparisonCategory getFundableBankComparisonCategory(int bankId, int comparisonCategoryId);

    List<CreditCardBrand> getCreditCardBrands(Locale locale) throws Exception;

    List<VehicleBrand> getActiveVehicleBrands();

    VehicleBrand getVehicleBrand(int vehicleBrandId);

    List<Vehicle> getVehicles(Locale locale);

    Vehicle getVehicle(int vehicleId, Locale locale);

    List<Avenue> getAvenues();

    List<HouseType> getHouseType();

    HouseType getHouseTypeById(Integer houseTypeId);

    List<AreaType> getAreaType();

    List<AreaType> getAreaTypesBanBif();

    List<AreaType> getAreaTypesAcceso();

    List<CreditSignatureScheduleHour> getCreditSignatureScheduleHours();

    AreaType getAreaTypeById(Integer areaTypeId);

    //List<String> getCreditSignatureScheduleHours();

    List<Product> getActiveProducts();

    List<Relationship> getRelationships(Integer relationshipId, Locale locale) throws Exception;

    CreditSignatureScheduleHour getCreditSignatureScheduleHour(int id);

    List<EntityProductParams> getEntityProductParams() throws Exception;

    List<LoanApplicationAuditType> getLoanApplicationAuditTypes();

    List<LoanApplicationAuditRejectionReason> getLoanApplicationAuditRejectionReasons();

    LoanApplicationAuditType getLoanApplicationAuditType(int id);

    List<LoanApplicationAuditRejectionReason> getLoanApplicationAuditRejectionReasonsByAuditType(int auditType);

    LoanApplicationAuditRejectionReason getLoanApplicationAuditRejectionReason(int id);

    ProcessQuestion getProcessQuestion(Integer id);

    List<ProcessQuestionCategory> getProcessQuestionCategories();

    List<ProcessQuestion> getProcessQuestions();

    ProcessQuestionCategory getProcessQuestionCategory(int id);

    List<DisbursementType> getDisbursementTypes() throws Exception;

    List<CountryParam> getCountryParams();

    DisbursementType getDisbursementTypeById(Integer disbursementTypeId) throws Exception;

    List<SendGridList> getSendGridLists();

    CountryParam getCountryParam(Integer countryId);

    CountryParam getCountryParamByCountryCode(String countryCode);

    List<TrackingAction> getTrackingActions() throws Exception;

    List<TrackingAction> getTrackingActionsByType(String type) throws Exception;

    List<TrackingDetail> getTrackingDetailsByTrackingAction(Integer trackingActionId) throws Exception;

    List<VehicleGasType> getVehicleGasTypes(Locale locale);

    VehicleGasType getVehicleGasType(int vehicleGasTypeId, Locale locale);

    List<VehicleDealership> getVehicleDealerships(Locale locale);

    VehicleDealership getVehicleDealership(int dealershipId, Locale locale);

    List<CreditSubStatus> getCreditSubStatuses(Locale locale);

    CreditSubStatus getCreditSubStatus(Locale locale, int id);

    List<Bank> getBanksByVehicle(int vehicleId, Locale locale);

    List<TrackingAction> getTrackingActionsByType(String type, String contactType) throws Exception;

    List<TrackingAction> getTrackingActionsByTypeAndScreen(String type, String contactType, String screen) throws Exception;

    List<Profession> getProfessionsActive(Locale locale);

    Avenue getAvenuesById(Integer avenueId);

    List<Proxy> getProxies() throws Exception;

    Proxy getProxy(Integer proxyId) throws Exception;

    Proxy getRandomProxyByCountry(Integer countryId) throws Exception;

    Vehicle getVehicle(int groupId);

    List<Agent> getAgents(Integer entityId);

    List<Department> getGeneralDepartment() throws Exception;

    Department getGeneralDepartmentById(Integer departmentId) throws Exception;

    List<Province> getGeneralProvince(Integer countryId) throws Exception;

    List<Province> getGeneralProvince() throws Exception;

    Province getGeneralProvinceById(Integer provinceId) throws Exception;

    List<District> getGeneralDistrict() throws Exception;

    District getGeneralDistrictById(Long districtId) throws Exception;

    List<District> getGeneralDistrictByProvince(Integer provinceId) throws Exception;

    Province getGeneralProvinceByDistrict(Long districtId) throws Exception;

    List<EntityWebService> getEntityWebServices();

    EntityWebService getEntityWebService(int entityWebServiceId);

    List<ProductCategory> getProductCategoriesVisible(Integer countryId, Locale locale);

    List<Holiday> getHolidays();

    List<Holiday> getHolidaysByCountry(int countryId);

    List<Province> getGeneralProvinceByPostalCode(Integer countryId, String postalCode) throws Exception;

    List<District> getGeneralDistrictByProvincePostalCode(Integer provinceId, String postalCode) throws Exception;

    List<PhoneType> getPhoneTypes();

    List<Bureau> getBureaus();

    Bureau getBureau(int id);

    List<EmployerGroup> getEmployerGroups();

    EmployerGroup getEmployerGroup(Integer employerGroupId);

    List<CorrectionFlow> getCorrectionFlows();

    CorrectionFlow getCorrectionFlowsById(Integer correctionFlowId);

    List<ReturningReason> getReturningReasons();

    ReturningReason getReturningReasonById(Integer returningReasonId);

    void callTestJobs();

    List<InstallmentStatus> getInstallmentStatuses();

    InstallmentStatus getInstallmentStatusesById(int installmentStatusId);

    List<HardFilter> getHardFilters() throws Exception;

    HardFilter getHardFilterById(int hardFilterId) throws Exception;

    List<Policy> getPolicies() ;

    Policy getPolicyById(int policyId) ;

    List<SysUserSchedule> getSchedules() throws Exception;

    SysUserSchedule getScheduleById(int scheduleId) throws Exception;

    List<Sector> getSectors();

    Sector getSectorById(String id);

    List<OfferRejectionReason> getOfferRejectionReasons() throws Exception;

    List<OfferRejectionReason> getOfferRejectionReasons(int entityId) throws Exception;

    OfferRejectionReason getOfferRejectionReason(int offerRejectionReasonId) throws Exception;

    int getBrandId(String brandName);

    MaintainedCarBrand getMaintainedCarBrand(Integer maintainedCarBrandId);

    List<MaintainedCarBrand> getMaintainedCarBrands();

    List<Mileage> getMileages();

    List<MaintainedCarBrand> getBrands(String model);

    Mileage getMileage(Integer id);

    List<GuaranteedVehicle> getGuaranteedVehicles();

    List<String> getGuaranteedVehicleModels(Integer brandId);

    List<Mileage> getGuaranteedVehicleMileages(Integer brandId, String model, Integer year);

    List<Integer> getGuaranteedVehicleYears(Integer brandId, String model);

    List<Integer> getYears(String model);

    List<String> getModelByYears(Integer year);

    List<MaintainedCarBrand> getBrandsByYear(Integer year);

    GuaranteedVehicle getGuaranteedVehicle(Integer brandId, Integer year, Integer mileageId, String model);

    List<String> getGuaranteedVehicleModels();

    List<Integer> getGuaranteedVehicleYears();

    List<AppoimentSchedule> getAppoimentSchedules();

    AppoimentSchedule getAppoimentSchedulesById(int appoimentScheduleId);

    List<SocioeconomicLevel> getSocioEconomicLevel();

    SocioeconomicLevel getSocioEconomicLevelByLevel(String level);

    List<InteractionProvider> getInteractionProviders();

    InteractionProvider getInteractionProvider(Integer interactionProviderId);

    List<SysUser> getManagementAnalystSysusers();

    List<Agent> getUnbrandedAgents();

    List<ObservationReason> getCreditObservationReasons();

    ObservationReason getCreditObservationReason(Integer observationId);

    List<RetirementScheme> getRetirementSchemes();

    RetirementScheme getRetirementSchemeById(int id);

    List<CustomProfession> getCustomProfessions();

    CustomProfession getCustomProfessionById(int id);

    List<LeadProduct> getLeadsProductActivity();

    List<LeadActivityType> getLeadActivityTypes();

    LeadProduct getLeadProductById(int id);

    LeadActivityType getLeadActivityById(int id);

    List<PreApprovedMail> getAllEmailTemplates();

    List<AfipActivitiy> getAfipActivities();

    AfipActivitiy getAfipActivityById(int id);

    List<PersonProfessionOccupation> getProfessionOccupations();

    List<EntityUserType> getEntityUserTypes();

    PersonProfessionOccupation getProfessionOccupation(int id);

    List<RateCommissionProduct> getRateCommissionProductByEntity(int entityId);

    EntityUserType getEntityUserType(int id);

    List<ProcessQuestion> getQuestionsAfterOffer();

    List<ProcessQuestion> getQuestionsBeforeOffer();

    List<Department> getGeneralDepartment(Integer countryId) throws Exception;

    List<Province> getGeneralProvinces(Integer departmentId) throws Exception;

    List<District> getDistrictsByProvinceId(Integer provinceId) throws Exception;

    List<OccupationArea> getOccupationAreas(Locale locale);

    List<RateCommissionCluster> getRateCommissionClusters();

    List<EntityAcquisitionChannel> getEntityAcquisitionChannelsByEntity(int entityId);

    EntityAcquisitionChannel getEntityAcquisitionChannelById(int entityAcquisitionId);

    ExtranetMenu getExtranetMenu(int extranetMenuId);

    Role getRole(int roleId);

    List<ApprovalValidation> getApprovalValidations();

    ApprovalValidation getApprovalValidation(int id);

    EntityBranding getEntityBrandingNoCache(int entityId) throws Exception;

    void updateEntityBrandingExtranetConfiguration(Integer entityId, EntityExtranetConfiguration entityExtranetConfiguration) throws Exception;


    List<OfferRejectionReason> getOfferRejectionReasonsByProductCategory(List<OfferRejectionReason> offerRejectionReasons, Integer productCategory) throws Exception;

    List<EntityProduct> getAllEntityProductsByEntityIncludeInactives(int entityId);

    List<CustomEntityActivity> getCustomEntityActivities(int entityId) throws Exception;

    CustomEntityActivity getCustomActivity(int activityId) throws Exception;

    List<CustomEntityProfession> getCustomEntityProfessions(int entityId) throws Exception;

    CustomEntityProfession getCustomEntityProfession(int professionId) throws Exception;

    List<AreaType> getAreaTypesAzteca();

    List<StreetType> getStreetTypesAzteca() throws Exception;
}