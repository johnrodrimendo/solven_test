/**
 *
 */
package com.affirm.common.dao;

import com.affirm.common.model.AfipActivitiy;
import com.affirm.common.model.FunnelStep;
import com.affirm.common.model.PreApprovedMail;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.security.model.SysUser;
import com.affirm.security.model.SysUserSchedule;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */
public interface CatalogDAO {

    List<LoanApplicationReason> getLoanApplicationReasons();

    List<MaritalStatus> getMaritalStatus();

    List<StreetType> getStreetTypes() throws Exception;

    List<ActivityType> getActivityTypes();

    Map<String, Department> getDepartments() throws Exception;

    List<Bank> getBanks();

    List<Product> getProducts();

    List<Product> getProductsEntity();

    List<Nationality> getNationalities();

    List<Belonging> getBelongings() throws Exception;

    List<VoucherType> getVoucherTypes() throws Exception;

    List<PensionPayer> getPensionPayer() throws Exception;

    List<LoanApplicationStatus> getLoanApplicationStatuses();

    List<CreditStatus> getCreditStatuses() throws Exception;

    List<UserFileType> getUserFileTypes();

    List<Cluster> getClusters() throws Exception;

    List<CreditRejectionReason> getCreditRejectionReasons() throws Exception;

    List<ApplicationRejectionReason> getApplicationRejectionReasons() throws Exception;

    List<Entity> getEntities();

    List<Relationship> getRelationships() throws Exception;

    List<BufferTransactionType> getBufferTransactionType() throws Exception;

    List<Bot> getBots() throws Exception;

    List<InteractionType> getInteractionTypes();

    List<InteractionContent> getInteractionContents();

    List<ContactResult> getContactResults() throws Exception;

    List<Tranche> getTranches() throws Exception;

    List<Employer> getEmployers();

    List<HelpMessage> getHelpMessages() throws Exception;

    List<RccEntity> getRccEntiy() throws Exception;

    List<RccAccount> getRccAccounts() throws Exception;

    List<Contract> getContracts() throws Exception;

    List<ConsolidationAccountType> getConsolidationAccountTypes() throws Exception;

    Map<String, String> getConfigParams();

    List<ProductPersonCategoryAmount> getPersonCategoryAmounts();

    List<ProductAgeRange> getProductAgeRanges() throws Exception;

    List<EntityProductParams> getEntityProductParams() throws Exception;

    List<EntityBranding> getEntityBranding() throws Exception;

    List<LoanApplicationRegisterType> getLoanApplicationRegisterType() throws Exception;

    List<HumanForm> getHumanForms() throws Exception;

    List<EntityProduct> getEntityProducts();

    List<Currency> getCurrency();

    List<LoanApplicationReasonCategory> getLoanReasonCategories();

    List<CreditUsage> getCreditUsages();

    List<HousingType> getHousingTypes();

    List<StudyLevel> getStudyLevels();

    List<Profession> getProfessions();

    List<ServiceType> getServiceType();

    List<Ocupation> getOcupations();

    List<SubActivityType> getSubActivityTypes(Locale locale);

    List<Agent> getAgents();

    List<ProductCategory> getProductCategories();

    List<ComparisonReason> getComparisonReasons();

    List<ComparisonCreditCost> getComparisonCreditCosts();

    ComparisonRates getComparisonInfo(int bankId);

    List<Category> getComparisonCategory();

    List<Cost> getComparisonCosts() throws Exception;

    List<CreditCardBrand> getCreditCardBrand() throws Exception;

    List<FundableBankComparisonCategory> getFundableBankComparisonCategories();

    List<VehicleBrand> getVehicleBrands();

    List<Vehicle> getVehicles(Locale locale);

    List<Avenue> getAvenues();

    List<HouseType> getHouseTypes();

    List<AreaType> getAreaTypes();

    List<CreditSignatureScheduleHour> getCreditSignatureScheduleHours();

    List<LoanApplicationAuditType> getLoanApplicationAuditTypes();

    List<LoanApplicationAuditRejectionReason> getLoanApplicationAuditRejectionReason();

    List<ProcessQuestion> getProcessQuestions();

    List<ProcessQuestionCategory> getProcessQuestionCategories();

    List<DisbursementType> getDisbursementType();

    List<SendGridList> getSendgridList();

    List<FraudAlert> getFraudAlerts();

    List<FraudFlagRejectionReason> getFraudAlertRejectionReasons();

    List<FraudFlag> getFraudFlags();

    List<FraudAlertStatus> getFraudAlertStatus();

    List<FraudFlagStatus> getFraudFlagStatus();

    List<CountryParam> getCountryParams();

    List<IdentityDocumentType> getIdentityDocumentTypes();

    List<TrackingAction> getTrackingActions() throws Exception;

    List<VehicleGasType> getVehicleGasType();

    List<VehicleDealership> getVehicleDealership(Locale locale);

    List<CreditSubStatus> getCreditSubStatus(Locale locale);

    List<Proxy> getProxies() throws Exception;

    List<Department> getGeneralDeparments() throws Exception;

    List<Province> getGeneralProvinces() throws Exception;

    List<District> getGeneralDistricts() throws Exception;

    List<EntityWebService> getEntityWebService();

    List<Holiday> getHolidays();

    List<Bureau> getBureaus();

    List<EmployerGroup> getEmployerGroups();

    List<CorrectionFlow> getCorrectionFlows();

    List<ReturningReason> getReturningReasons();

    List<InstallmentStatus> getInstallmentStatus();

    List<HardFilter> getHardFilters() throws Exception;

    List<Policy> getPolicies() ;

    List<SysUserSchedule> getSchedules();

    List<OfferRejectionReason> getOfferRejectionReasons() throws Exception;

    List<MaintainedCarBrand> getMaintainedCarBrands();

    List<Mileage> getMileages();

    List<GuaranteedVehicle> getGuaranteedVehicles();

    List<AppoimentSchedule> getAppoimentSchedules();

    List<SocioeconomicLevel> getSocioEconomicLevel();

    List<InteractionProvider> getInteractionProviders();

    List<SysUser> getManagmentAnalystSysusers();

    List<ObservationReason> getObservationReasons();

    List<RetirementScheme> getRetirementSchemes();

    List<CustomProfession> getCustomProfessions();

    List<LeadActivityType> getLeadActivityTypes();

    List<LeadProduct> getLeadProducts();

    List<PreApprovedMail> getPreApprovedMailingList();

    List<AfipActivitiy> getAfipActivities();

    List<PersonProfessionOccupation> getProfessionOccupation();

    List<RateCommissionCluster> getRateCommissionClusters();

    List<EntityUserType> getEntityUserTypes();

    List<OccupationArea> getOccupationAreas();

    List<EntityAcquisitionChannel> getEntityAcquisitionChannels();

    List<ExtranetMenu> getExtranetMenus();

    List<Role> getRoles();

    List<FunnelStep> getFunnelSteps();

    @Cacheable
    List<ApprovalValidation> getApprovalValidations();

    EntityBranding getEntityBrandingByIdNoCache(Integer entityId) throws Exception;

    void updateEntityBrandingExtranetConfiguration(Integer entityId, EntityExtranetConfiguration entityExtranetConfiguration) throws Exception;

    List<EntityProduct> getAllEntityProductsIncludeInactives();

    List<EntityProductParamPolicyConfiguration> getEntityProductParamPolicy(int entityId, int entityProductParamId);

    List<CustomEntityActivity> getCustomEntityActivities() throws Exception;

    void registerCustomEntityActivity(Integer entityId, Integer identifier, String description, Long regulatoryEntityIdentifier);

    void registerCustomEntityProfession(Integer entityId, Long identifier, String description);

    List<CustomEntityProfession> getCustomEntityProfessions() throws Exception;
}
