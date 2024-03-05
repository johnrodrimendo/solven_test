package com.affirm.tests.dao

import com.affirm.common.dao.CatalogDAO
import com.affirm.common.model.AfipActivitiy
import com.affirm.common.model.PreApprovedMail
import com.affirm.common.model.catalog.*
import com.affirm.common.model.transactional.*
import com.affirm.security.model.SysUser
import com.affirm.security.model.SysUserSchedule
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CatalogDAOTest extends BaseConfig {

    @Autowired
    CatalogDAO catalogDAO

    static final int BANK_ID = 12345
    static final Locale LOCALE = Locale.US

    @Test
    void getLoanApplicationReasonsFromCatalogDAO() {
        List<LoanApplicationReason> result = catalogDAO.getLoanApplicationReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getMaritalStatusFromCatalogDAO() {
        List<MaritalStatus> result = catalogDAO.getMaritalStatus()
        Assert.assertNotNull(result)
    }

    @Test
    void getStreetTypesReasonsFromCatalogDAO() {
        List<StreetType> result = catalogDAO.getStreetTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getActivityTypesFromCatalogDAO() {
        List<ActivityType> result = catalogDAO.getActivityTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getDepartmentsFromCatalogDAO() {
        Map<String, Department> result = catalogDAO.getDepartments()
        Assert.assertNotNull(result)
    }

    @Test
    void getBanksFromCatalogDAO() {
        List<Bank> result = catalogDAO.getBanks()
        Assert.assertNotNull(result)
    }

    @Test
    void getProductsFromCatalogDAO() {
        List<Product> result = catalogDAO.getProducts()
        Assert.assertNotNull(result)
    }

    @Test
    void getProductsEntityFromCatalogDAO() {
        List<Product> result = catalogDAO.getProductsEntity()
        Assert.assertNotNull(result)
    }

    @Test
    void getNationalitiesFromCatalogDAO() {
        List<Nationality> result = catalogDAO.getNationalities()
        Assert.assertNotNull(result)
    }

    @Test
    void getBelongingsFromCatalogDAO() {
        List<Belonging> result = catalogDAO.getBelongings()
        Assert.assertNotNull(result)
    }

    @Test
    void getPensionPayerFromCatalogDAO() {
        List<PensionPayer> result = catalogDAO.getPensionPayer()
        Assert.assertNotNull(result)
    }

    @Test
    void getVoucherTypesFromCatalogDAO() {
        List<VoucherType> result = catalogDAO.getVoucherTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getLoanApplicationStatusesFromCatalogDAO() {
        List<LoanApplicationStatus> result = catalogDAO.getLoanApplicationStatuses()
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditStatusesFromCatalogDAO() {
        List<CreditStatus> result = catalogDAO.getCreditStatuses()
        Assert.assertNotNull(result)
    }

    @Test
    void getUserFileTypesFromCatalogDAO() {
        List<UserFileType> result = catalogDAO.getUserFileTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getClustersFromCatalogDAO() {
        List<Cluster> result = catalogDAO.getClusters()
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditRejectionReasonsFromCatalogDAO() {
        List<CreditRejectionReason> result = catalogDAO.getCreditRejectionReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getApplicationRejectionReasonsFromCatalogDAO() {
        List<ApplicationRejectionReason> result = catalogDAO.getApplicationRejectionReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getEntitiesFromCatalogDAO() {
        List<Entity> result = catalogDAO.getEntities()
        Assert.assertNotNull(result)
    }

    @Test
    void getRelationshipsFromCatalogDAO() {
        List<Relationship> result = catalogDAO.getRelationships()
        Assert.assertNotNull(result)
    }

    @Test
    void getBufferTransactionTypeFromCatalogDAO() {
        List<BufferTransactionType> result = catalogDAO.getBufferTransactionType()
        Assert.assertNotNull(result)
    }

    @Test
    void getBotsFromCatalogDAO() {
        List<Bot> result = catalogDAO.getBots()
        Assert.assertNotNull(result)
    }

    @Test
    void getInteractionTypesFromCatalogDAO() {
        List<InteractionType> result = catalogDAO.getInteractionTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getInteractionContentsFromCatalogDAO() {
        List<InteractionContent> result = catalogDAO.getInteractionContents()
        Assert.assertNotNull(result)
    }

    @Test
    void getContactResultsFromCatalogDAO() {
        List<ContactResult> result = catalogDAO.getContactResults()
        Assert.assertNotNull(result)
    }

    @Test
    void getTrackingActionsFromCatalogDAO() {
        List<TrackingAction> result = catalogDAO.getTrackingActions()
        Assert.assertNotNull(result)
    }

    @Test
    void getTranchesFromCatalogDAO() {
        List<Tranche> result = catalogDAO.getTranches()
        Assert.assertNotNull(result)
    }

    @Test
    void getEmployersFromCatalogDAO() {
        List<Employer> result = catalogDAO.getEmployers()
        Assert.assertNotNull(result)
    }

    @Test
    void getHelpMessagesFromCatalogDAO() {
        List<HelpMessage> result = catalogDAO.getHelpMessages()
        Assert.assertNotNull(result)
    }

    @Test
    void getRccEntiyFromCatalogDAO() {
        List<RccEntity> result = catalogDAO.getRccEntiy()
        Assert.assertNotNull(result)
    }

    @Test
    void getRccAccountsFromCatalogDAO() {
        List<RccAccount> result = catalogDAO.getRccAccounts()
        Assert.assertNotNull(result)
    }

    @Test
    void getConsolidationAccountTypesFromCatalogDAO() {
        List<ConsolidationAccountType> result = catalogDAO.getConsolidationAccountTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getConfigParamsFromCatalogDAO() {
        Map<String, String> result = catalogDAO.getConfigParams()
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonCategoryAmountsFromCatalogDAO() {
        List<ProductPersonCategoryAmount> result = catalogDAO.getPersonCategoryAmounts()
        Assert.assertNotNull(result)
    }

    @Test
    void getProductAgeRangesFromCatalogDAO() {
        List<ProductAgeRange> result = catalogDAO.getProductAgeRanges()
        Assert.assertNotNull(result)
    }

    @Test
    void getContractsFromCatalogDAO() {
        List<Contract> result = catalogDAO.getContracts()
        Assert.assertNotNull(result)
    }

    @Test
    void getEntityProductParamsFromCatalogDAO() {
        List<EntityProductParams> result = catalogDAO.getEntityProductParams()
        Assert.assertNotNull(result)
    }

    @Test
    void getEntityBrandingFromCatalogDAO() {
        List<EntityBranding> result = catalogDAO.getEntityBranding()
        Assert.assertNotNull(result)
    }

    @Test
    void getLoanApplicationRegisterTypeFromCatalogDAO() {
        List<LoanApplicationRegisterType> result = catalogDAO.getLoanApplicationRegisterType()
        Assert.assertNotNull(result)
    }

    @Test
    void getHumanFormsFromCatalogDAO() {
        List<HumanForm> result = catalogDAO.getHumanForms()
        Assert.assertNotNull(result)
    }

    @Test
    void getEntityProductsFromCatalogDAO() {
        List<EntityProduct> result = catalogDAO.getEntityProducts()
        Assert.assertNotNull(result)
    }

    @Test
    void getCurrencyFromCatalogDAO() {
        List<Currency> result = catalogDAO.getCurrency()
        Assert.assertNotNull(result)
    }

    @Test
    void getLoanReasonCategoriesFromCatalogDAO() {
        List<LoanApplicationReasonCategory> result = catalogDAO.getLoanReasonCategories()
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditUsagesFromCatalogDAO() {
        List<CreditUsage> result = catalogDAO.getCreditUsages()
        Assert.assertNotNull(result)
    }

    @Test
    void getHousingTypesFromCatalogDAO() {
        List<HousingType> result = catalogDAO.getHousingTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getStudyLevelsFromCatalogDAO() {
        List<StudyLevel> result = catalogDAO.getStudyLevels()
        Assert.assertNotNull(result)
    }

    @Test
    void getProfessionsFromCatalogDAO() {
        List<Profession> result = catalogDAO.getProfessions()
        Assert.assertNotNull(result)
    }

    @Test
    void getServiceTypeFromCatalogDAO() {
        List<ServiceType> result = catalogDAO.getServiceType()
        Assert.assertNotNull(result)
    }

    @Test
    void getOcupationsFromCatalogDAO() {
        List<Ocupation> result = catalogDAO.getOcupations()
        Assert.assertNotNull(result)
    }

    @Test
    void getSubActivityTypesFromCatalogDAO() {
        List<SubActivityType> result = catalogDAO.getSubActivityTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getAgentsFromCatalogDAO() {
        List<Agent> result = catalogDAO.getAgents()
        Assert.assertNotNull(result)
    }

    @Test
    void getProductCategoriesFromCatalogDAO() {
        List<ProductCategory> result = catalogDAO.getProductCategories()
        Assert.assertNotNull(result)
    }

    @Test
    void getComparisonReasonsFromCatalogDAO() {
        List<ComparisonReason> result = catalogDAO.getComparisonReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getComparisonCreditCostsFromCatalogDAO() {
        List<ComparisonCreditCost> result = catalogDAO.getComparisonCreditCosts()
        Assert.assertNotNull(result)
    }

    @Test
    void getComparisonInfoFromCatalogDAO() {
        ComparisonRates result = catalogDAO.getComparisonInfo(BANK_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getComparisonCategoryFromCatalogDAO() {
        List<Category> result = catalogDAO.getComparisonCategory()
        Assert.assertNotNull(result)
    }

    @Test
    void getComparisonCostsFromCatalogDAO() {
        List<Cost> result = catalogDAO.getComparisonCosts()
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditCardBrandFromCatalogDAO() {
        List<CreditCardBrand> result = catalogDAO.getCreditCardBrand()
        Assert.assertNotNull(result)
    }

    @Test
    void getFundableBankComparisonCategoriesFromCatalogDAO() {
        List<FundableBankComparisonCategory> result = catalogDAO.getFundableBankComparisonCategories()
        Assert.assertNotNull(result)
    }

    @Test
    void getVehicleBrandsFromCatalogDAO() {
        List<VehicleBrand> result = catalogDAO.getVehicleBrands()
        Assert.assertNotNull(result)
    }

    @Test
    void getVehiclesFromCatalogDAO() {
        List<Vehicle> result = catalogDAO.getVehicles(LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void getAvenuesFromCatalogDAO() {
        List<Avenue> result = catalogDAO.getAvenues()
        Assert.assertNotNull(result)
    }

    @Test
    void getHouseTypesFromCatalogDAO() {
        List<HouseType> result = catalogDAO.getHouseTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getAreaTypesFromCatalogDAO() {
        List<AreaType> result = catalogDAO.getAreaTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditSignatureScheduleHoursFromCatalogDAO() {
        List<CreditSignatureScheduleHour> result = catalogDAO.getCreditSignatureScheduleHours()
        Assert.assertNotNull(result)
    }

    @Test
    void getDisbursementTypeFromCatalogDAO() {
        List<DisbursementType> result = catalogDAO.getDisbursementType()
        Assert.assertNotNull(result)
    }

    @Test
    void getLoanApplicationAuditTypesFromCatalogDAO() {
        List<LoanApplicationAuditType> result = catalogDAO.getLoanApplicationAuditTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getLoanApplicationAuditRejectionReasonFromCatalogDAO() {
        List<LoanApplicationAuditRejectionReason> result = catalogDAO.getLoanApplicationAuditRejectionReason()
        Assert.assertNotNull(result)
    }

    @Test
    void getProcessQuestionsFromCatalogDAO() {
        List<ProcessQuestion> result = catalogDAO.getProcessQuestions()
        Assert.assertNotNull(result)
    }

    @Test
    void getProcessQuestionCategoriesFromCatalogDAO() {
        List<ProcessQuestionCategory> result = catalogDAO.getProcessQuestionCategories()
        Assert.assertNotNull(result)
    }

    @Test
    void getSendgridListFromCatalogDAO() {
        List<SendGridList> result = catalogDAO.getSendgridList()
        Assert.assertNotNull(result)
    }

    @Test
    void getFraudAlertsFromCatalogDAO() {
        List<FraudAlert> result = catalogDAO.getFraudAlerts()
        Assert.assertNotNull(result)
    }

    @Test
    void getFraudAlertRejectionReasonsFromCatalogDAO() {
        List<FraudFlagRejectionReason> result = catalogDAO.getFraudAlertRejectionReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getFraudFlagsFromCatalogDAO() {
        List<FraudFlag> result = catalogDAO.getFraudFlags()
        Assert.assertNotNull(result)
    }

    @Test
    void getFraudAlertStatusFromCatalogDAO() {
        List<FraudAlertStatus> result = catalogDAO.getFraudAlertStatus()
        Assert.assertNotNull(result)
    }

    @Test
    void getFraudFlagStatusFromCatalogDAO() {
        List<FraudFlagStatus> result = catalogDAO.getFraudFlagStatus()
        Assert.assertNotNull(result)
    }

    @Test
    void getCountryParamsFromCatalogDAO() {
        List<CountryParam> result = catalogDAO.getCountryParams()
        Assert.assertNotNull(result)
    }

    @Test
    void getIdentityDocumentTypesFromCatalogDAO() {
        List<IdentityDocumentType> result = catalogDAO.getIdentityDocumentTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getVehicleGasTypeFromCatalogDAO() {
        List<VehicleGasType> result = catalogDAO.getVehicleGasType()
        Assert.assertNotNull(result)
    }

    @Test
    void getVehicleDealershipFromCatalogDAO() {
        List<VehicleDealership> result = catalogDAO.getVehicleDealership(LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void getCreditSubStatusFromCatalogDAO() {
        List<CreditSubStatus> result = catalogDAO.getCreditSubStatus()
        Assert.assertNotNull(result)
    }

    @Test
    void getProxiesFromCatalogDAO() {
        List<Proxy> result = catalogDAO.getProxies()
        Assert.assertNotNull(result)
    }

    @Test
    void getGeneralDeparmentsFromCatalogDAO() {
        List<Department> result = catalogDAO.getGeneralDeparments()
        Assert.assertNotNull(result)
    }

    @Test
    void getGeneralProvincesFromCatalogDAO() {
        List<Province> result = catalogDAO.getGeneralProvinces()
        Assert.assertNotNull(result)
    }

    @Test
    void getGeneralDistrictsFromCatalogDAO() {
        List<District> result = catalogDAO.getGeneralDistricts()
        Assert.assertNotNull(result)
    }

    @Test
    void getEntityWebServiceFromCatalogDAO() {
        List<EntityWebService> result = catalogDAO.getEntityWebService()
        Assert.assertNotNull(result)
    }

    @Test
    void getHolidaysFromCatalogDAO() {
        List<Holiday> result = catalogDAO.getHolidays()
        Assert.assertNotNull(result)
    }

    @Test
    void getCorrectionFlowsFromCatalogDAO() {
        List<CorrectionFlow> result = catalogDAO.getCorrectionFlows()
        Assert.assertNotNull(result)
    }

    @Test
    void getReturningReasonsFromCatalogDAO() {
        List<ReturningReason> result = catalogDAO.getReturningReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getBureausFromCatalogDAO() {
        List<Bureau> result = catalogDAO.getBureaus()
        Assert.assertNotNull(result)
    }

    @Test
    void getEmployerGroupsFromCatalogDAO() {
        List<EmployerGroup> result = catalogDAO.getEmployerGroups()
        Assert.assertNotNull(result)
    }

    @Test
    void getInstallmentStatusFromCatalogDAO() {
        List<InstallmentStatus> result = catalogDAO.getInstallmentStatus()
        Assert.assertNotNull(result)
    }

    @Test
    void getHardFiltersFromCatalogDAO() {
        List<HardFilter> result = catalogDAO.getHardFilters()
        Assert.assertNotNull(result)
    }

    @Test
    void getPoliciesFromCatalogDAO() {
        List<Policy> result = catalogDAO.getPolicies()
        Assert.assertNotNull(result)
    }

    @Test
    void getSchedulesFromCatalogDAO() {
        List<SysUserSchedule> result = catalogDAO.getSchedules()
        Assert.assertNotNull(result)
    }

    @Test
    void getOfferRejectionReasonsFromCatalogDAO() {
        List<OfferRejectionReason> result = catalogDAO.getOfferRejectionReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getMaintainedCarBrandsFromCatalogDAO() {
        List<MaintainedCarBrand> result = catalogDAO.getMaintainedCarBrands()
        Assert.assertNotNull(result)
    }

    @Test
    void getMileagesFromCatalogDAO() {
        List<Mileage> result = catalogDAO.getMileages()
        Assert.assertNotNull(result)
    }

    @Test
    void getGuaranteedVehiclesFromCatalogDAO() {
        List<GuaranteedVehicle> result = catalogDAO.getGuaranteedVehicles()
        Assert.assertNotNull(result)
    }

    @Test
    void getAppoimentSchedulesFromCatalogDAO() {
        List<AppoimentSchedule> result = catalogDAO.getAppoimentSchedules()
        Assert.assertNotNull(result)
    }

    @Test
    void getSocioEconomicLevelFromCatalogDAO() {
        List<SocioeconomicLevel> result = catalogDAO.getSocioEconomicLevel()
        Assert.assertNotNull(result)
    }

    @Test
    void getInteractionProvidersFromCatalogDAO() {
        List<InteractionProvider> result = catalogDAO.getInteractionProviders()
        Assert.assertNotNull(result)
    }

    @Test
    void getManagmentAnalystSysusersFromCatalogDAO() {
        List<SysUser> result = catalogDAO.getManagmentAnalystSysusers()
        Assert.assertNotNull(result)
    }

    @Test
    void getObservationReasonsFromCatalogDAO() {
        List<ObservationReason> result = catalogDAO.getObservationReasons()
        Assert.assertNotNull(result)
    }

    @Test
    void getRetirementSchemesFromCatalogDAO() {
        List<RetirementScheme> result = catalogDAO.getRetirementSchemes()
        Assert.assertNotNull(result)
    }

    @Test
    void getCustomProfessionsFromCatalogDAO() {
        List<CustomProfession> result = catalogDAO.getCustomProfessions()
        Assert.assertNotNull(result)
    }

    @Test
    void getLeadActivityTypesFromCatalogDAO() {
        List<LeadActivityType> result = catalogDAO.getLeadActivityTypes()
        Assert.assertNotNull(result)
    }

    @Test
    void getLeadProductsFromCatalogDAO() {
        List<LeadProduct> result = catalogDAO.getLeadProducts()
        Assert.assertNotNull(result)
    }

    @Test
    void getPreApprovedMailingListFromCatalogDAO() {
        List<PreApprovedMail> result = catalogDAO.getPreApprovedMailingList()
        Assert.assertNotNull(result)
    }

    @Test
    void getAfipActivitiesFromCatalogDAO() {
        List<AfipActivitiy> result = catalogDAO.getAfipActivities()
        Assert.assertNotNull(result)
    }

    @Test
    void getProfessionOccupationFromCatalogDAO() {
        List<PersonProfessionOccupation> result = catalogDAO.getProfessionOccupation()
        Assert.assertNotNull(result)
    }

}
