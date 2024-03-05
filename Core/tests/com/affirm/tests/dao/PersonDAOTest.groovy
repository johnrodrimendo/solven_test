package com.affirm.tests.dao

import com.affirm.acceso.model.Direccion
import com.affirm.common.dao.PersonDAO
import com.affirm.common.model.PreApprovedInfo
import com.affirm.common.model.transactional.*
import com.affirm.common.service.CatalogService
import com.affirm.tests.BaseConfig
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PersonDAOTest extends BaseConfig {

    @Autowired
    PersonDAO personDAO

    @Autowired
    private CatalogService catalogService;

    static final int PERSON_ID = 3678
    static final Locale LOCALE = Locale.US
    static final boolean GET_PARTNER = true
    static final int DOCUMENT_TYPE = 10
    static final String DOCUMENT_NUMBER = "123456789"
    static final int CREDIT_ID = 333
    static final Integer LOAN_APPLICATION_ID = 1697
    static final Date CURRENT_DATE = new Date()
    static final Date BIRTHDAY = CURRENT_DATE
    static final Integer NATIONALITY_ID = 51
    static final Integer MARITAL_STATUS_ID = 2
    static final String NAME = "Omar"
    static final String FIRST_SURNAME = "Ccoa"
    static final String LAST_SURNAME = "Heredia"
    static final char GENDER = 'F'
    static final Integer PARTNER_PERSON_ID = 8638
    static final String EMAIL = "occoa@solven.pe"
    static final String COUNTRY_CODE = "51"
    static final String PHONE_NUMBER = "987456123"
    static final int USER_ID = 1633
    static final int OCCUPATIONAL_INFORMATION_NUMBER = 26
    static final int ACTIVITY_TYPE_ID = 123
    static final String COMPANY_NAME = "Solven"
    static final String CIIU = ""
    static final String REGIME_ID = "555"
    static final String RUC = "10111111111"
    static final String TAX_REGIME = ""
    static final String SHARED_HOLDING = ""
    static final Integer RESULT_U12M = 0
    static final Integer FIXED_GROSS_INCOME = 12
    static final Boolean OTHER_INCOME = true
    static final Integer VARIABLE_GROSS_INCOME = 13
    static final String ADDRESS = "Av. Mariscal La Mar 398"
    static final Boolean IS_HOME_ADDRESS = true
    static final String ADDRESS_DEPARTMENT = "14"
    static final String ADDRESS_PROVINCE = "07"
    static final String ADDRESS_DISTRICT = "03"
    static final String EMPLOYMENT_TIME = "5"
    static final Integer VARIABLE_INCOME = 8
    static final Integer VOUCHER_TYPE_ID = 10
    static final String BELONGINGS = ""
    static final Integer PENSION_PAYER_ID = 222
    static final String PHONE_TYPE = "C"
    static final Date START_DATE = CURRENT_DATE
    static final Integer NET_INCOME = 5
    static final Integer SERVICE_TYPE_ID = 83
    static final Integer OCCUPATION_ID = 18
    static final String SECTOR_ID = "456"
    static final Integer SUB_ACTIVITY_TYPE = 1
    static final String CLIENT_1 = "Acceso"
    static final String CLIENT_2 = "Banco del Sol"
    static final Double SELLINGS = 20_000
    static final Double EXERCISE_OUTCOME = 3
    static final Double SALES_PERCENTAGE_FIXED_COSTS = 3
    static final Double SALES_PERCENTAGE_VARIABLE_COSTS = 4
    static final Double SALES_PERCENTAGE_BEST_CLIENT = 5
    static final Double AVERAGE_DAILY_INCOME = 8
    static final Double LAST_YEAR_COMPENSATION = 9
    static final FrequencySalary FREQUENCY_SALARY = getFrequencySalary()
    static final Date RETIREMENT_DATE = CURRENT_DATE
    static final int RETIREMENT_SCHEMA_ID = 1
    static final String DEPARTMENT_ID = "14"
    static final String PROVINCE_ID = "07"
    static final String DISCTRICT_ID = "03"
    static final Integer STREET_TYPE_ID = 2
    static final String STREET_NAME = "Jr. Mariscal La Mar"
    static final String STREET_NUMBER = "398"
    static final String INTERIOR = "401"
    static final String DETAIL = ""
    static final Double LATITUDE = -12.087796
    static final Double LONGITUDE = -77.013877
    static final boolean VALIDATED = true
    static final int REFERRAL_ID = 698
    static final String FULLNAME = "Fullname"
    static final int RELATION_ID = 699
    static final String INFO = ""
    static final Integer BANK_ID = 12345
    static final Character ACCOUNT_TYPE = 'A'
    static final String ACCOUNT = 'A'
    static final String UBIGEO = "140104"
    static final String CCI = ""
    static final String BRANCH_OFFICE_CODE = "565"
    static final String RANDOM_PARTNERS = "88"
    static final int EMPLOYER_USER_ID = 3221
    static final String SLUG = ""
    static final Boolean IS_ACTIVE = true
    static final Class<Employee> RETURN_TYPE = Employee.class
    static final int EMPLOYER_ID = 322
    static final int EMPLOYEE_ID = 388
    static final Boolean IS_CONSOLIDATE = true
    static final int ENTITY_ID = 7
    static final boolean IS_VALIDATED = true
    static final String ASSCIATED_ID = "45"
    static final String PASSBOOK_NUMBER = "789798"
    static final Integer HOUSING_TYPE_ID = 2
    static final int RESIDENCE_TIME_IN_MONTHS = 9
    static final Integer FIXED_GROSS_INCOME_INCRESAL = 57
    static final Boolean CLIENT1_RUC_65 = false
    static final Integer LAST_YEAR_SELLING_INCREASAL = 2009
    static final Integer EXERCISE_OUTCOME_INCREASAL = 47
    static final Integer SALES_PERCENTAGE_BEST_CLIENT_INDICATOR = 89
    static final Integer STUDY_LEVEL_ID = 6
    static final Integer PROFESSION_ID = 11
    static final boolean COMPLETED = true
    static final String JSON = '[]'
    static final Integer DEPENDENTS = 0
    static final Boolean PEP = true
    static final String  PEP_DETAIL = ""
    static final int PRODUCT_CATEGORY_ID = 87
    static final Direccion DIRECCION = getDireccion()
    static final String TIPO_DOMICILIO = "4"
    static final Integer PRODUCT_ID = 78
    static final String EVALUATION_RESULT = ""
    static final String REJECTION_CODE = ""
    static final String REJECTION_DESCRIPTION = ""
    static final Double MAX_AMOUNT = 20_000
    static final Integer MAX_INSTALLMENTS = 3
    static final Double TEA = 0.75
    static final String CARD_TYPE = "V"
    static final String CARD_NUMBER = "9878987765446544"
    static final Integer PAYMENT_DAY = 5
    static final Integer BILLING_CLOSE = 10
    static final String CLUSTER = ""
    static final String ENTITY_PRODUCT_PARAMETER = '{2}'
    static final boolean CHANGED = false
    static final boolean VERIFEID = false
    static final Integer DAY = 1
    static final Integer MONTH = 12
    static final Integer CUSTOM_PROFESSION_ID = 6
    static final String TYPE = ""
    static final boolean IS_DISQUILIFIED = false
    static final int USER_FILE_ID = 878
    static final String LANDLINE = ""
    static final Integer USER_ENTITY_ID = 545


    static FrequencySalary getFrequencySalary() {
        FrequencySalary frequencySalary = new FrequencySalary()

        return  frequencySalary
    }

    static Direccion getDireccion() {
        Direccion direccion = new Direccion()
        direccion.setDistrictId(1)

        return direccion
    }

    @Test
    void getPersonFromPersonDAO() {
        Person result = personDAO.getPerson(catalogService, LOCALE, PERSON_ID, GET_PARTNER)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonIdByDocumentFromPersonDAO() {
        Integer result = personDAO.getPersonIdByDocument(DOCUMENT_TYPE, DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonNegativeBaseFromPersonDAO() {
        Boolean result = personDAO.getPersonNegativeBase(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonHasDebtFromPersonDAO() {
        Boolean result = personDAO.getPersonHasDebt(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonContactInformationFromPersonDAO() {
        PersonContactInformation result = personDAO.getPersonContactInformation(LOCALE, PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonOcupationalInformationFromPersonDAO() {
        List<PersonOcupationalInformation> result = personDAO.getPersonOcupationalInformation(LOCALE, PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonBankAccountInformationByCreditFromPersonDAO() {
        PersonBankAccountInformation result = personDAO.getPersonBankAccountInformationByCredit(LOCALE, PERSON_ID, CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void getPersonBankAccountInformationFromPersonDAO() {
        PersonBankAccountInformation result = personDAO.getPersonBankAccountInformation(LOCALE, PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getUserFilesFromPersonDAO() {
        List<LoanApplicationUserFiles> result = personDAO.getUserFiles(PERSON_ID, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void getLoanApplicationFilesFromPersonDAO() {
        List<LoanApplicationUserFiles> result = personDAO.getLoanApplicationFiles(LOAN_APPLICATION_ID, PERSON_ID, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void updateBirthdayFromPersonDAO() {
        personDAO.updateBirthday(PERSON_ID, BIRTHDAY)
    }

    @Test
    void updateNationalityFromPersonDAO() {
        personDAO.updateNationality(PERSON_ID, NATIONALITY_ID)
    }

    @Test
    void updateMaritalStatusFromPersonDAO() {
        personDAO.updateMaritalStatus(PERSON_ID, MARITAL_STATUS_ID)
    }

    @Test
    void updateNamePersonDAO() {
        personDAO.updateName(PERSON_ID, NAME)
    }

    @Test
    void updateFirstSurnameFromPersonDAO() {
        personDAO.updateFirstSurname(PERSON_ID, FIRST_SURNAME)
    }

    @Test
    void updateLastSurnameFromPersonDAO() {
        personDAO.updateLastSurname(PERSON_ID, LAST_SURNAME)
    }

    @Test
    void updateGenderFromPersonDAO() {
        personDAO.updateGender(PERSON_ID, GENDER)
    }

    @Test
    void updatePartnerFromPersonDAO() {
        personDAO.updatePartner(PERSON_ID, PARTNER_PERSON_ID)
    }

    @Test
    void updateEmailFromPersonDAO() {
        personDAO.updateEmail(PERSON_ID, EMAIL)
    }

    @Test
    void updatePhoneNumberFromPersonDAO() {
        personDAO.updatePhoneNumber(USER_ID, COUNTRY_CODE, PHONE_NUMBER)
    }

    @Test
    void updateOcupationalActivityTypeFromPersonDAO() {
        personDAO.updateOcupationalActivityType(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, ACTIVITY_TYPE_ID)
    }

    @Test
    void updateOcupationalCompanyFromPersonDAO() {
        personDAO.updateOcupationalCompany(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, COMPANY_NAME)
    }

    @Test
    void updateOcupationalCiiuFromPersonDAO() {
        personDAO.updateOcupationalCiiu(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, CIIU)
    }

    @Test
    void updateOcupationalRegimeFromPersonDAO() {
        personDAO.updateOcupationalRegime(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, REGIME_ID)
    }

    @Test
    void updateOcupationalRucFromPersonDAO() {
        personDAO.updateOcupationalRuc(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, RUC)
    }

    @Test
    void updateOcupationalRuc2FromPersonDAO() {
        personDAO.updateOcupationalRuc(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, RUC, TAX_REGIME)
    }

    @Test
    void updateOcupationalShareholdingFromPersonDAO() {
        personDAO.updateOcupationalShareholding(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, SHARED_HOLDING)
    }

    @Test
    void updateOcupationalResultU12MFromPersonDAO() {
        personDAO.updateOcupationalResultU12M(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, RESULT_U12M)
    }

    @Test
    void updateFixedGrossIncomeFromPersonDAO() {
        personDAO.updateFixedGrossIncome(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, FIXED_GROSS_INCOME)
    }

    @Test
    void updateHasOtherIncomeFromPersonDAO() {
        personDAO.updateHasOtherIncome(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, OTHER_INCOME)
    }

    @Test
    void updateVariableGrossIncomeFromPersonDAO() {
        personDAO.updateVariableGrossIncome(PERSON_ID,OCCUPATIONAL_INFORMATION_NUMBER, VARIABLE_GROSS_INCOME)
    }

    @Test
    void updateOcupationalAddressFromPersonDAO() {
        personDAO.updateOcupationalAddress(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, ADDRESS, IS_HOME_ADDRESS,
                ADDRESS_DEPARTMENT, ADDRESS_PROVINCE, ADDRESS_DISTRICT)
    }

    @Test
    void updateEmploymentTimeFromPersonDAO() {
        personDAO.updateEmploymentTime(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, EMPLOYMENT_TIME)
    }

    @Test
    void updateVariableIncomeFromPersonDAO() {
        personDAO.updateVariableIncome(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, VARIABLE_INCOME)
    }

    @Test
    void updateBelongingFromPersonDAO() {
        personDAO.updateBelonging(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, BELONGINGS)
    }

    @Test
    void updateOcupatinalPhoneNumberFromPersonDAO() {
        personDAO.updateOcupatinalPhoneNumber(PERSON_ID, PHONE_TYPE, OCCUPATIONAL_INFORMATION_NUMBER, PHONE_NUMBER)
    }

    @Test
    void updateOcupatinalStartDateFromPersonDAO() {
        personDAO.updateOcupatinalStartDate(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, START_DATE)
    }

    @Test
    void updateOtherActivityTypeIncomeFromPersonDAO() {
        personDAO.updateOtherActivityTypeIncome(PERSON_ID, NET_INCOME)
    }

    @Test
    void updateOcupatinalServiceTypeFromPersonDAO() {
        personDAO.updateOcupatinalServiceType(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, SERVICE_TYPE_ID)
    }

    @Test
    void updateOcupatinalOcupationFromPersonDAO() {
        personDAO.updateOcupatinalOcupation(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, OCCUPATION_ID)
    }

    @Test
    void updateOcupationalSectorFromPersonDAO() {
        personDAO.updateOcupationalSector(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, SECTOR_ID)
    }

    @Test
    void updateOcupatinalSubActivityTypeFromPersonDAO() {
        personDAO.updateOcupatinalSubActivityType(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, SUB_ACTIVITY_TYPE)
    }

    @Test
    void updateOcupatinalClient1FromPersonDAO() {
        personDAO.updateOcupatinalClient1(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, CLIENT_1)
    }

    @Test
    void updateOcupatinalClient2FromPersonDAO() {
        personDAO.updateOcupatinalClient2(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, CLIENT_2)
    }

    @Test
    void updateOcupatinalLastYearSellingsFromPersonDAO() {
        personDAO.updateOcupatinalLastYearSellings(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, SELLINGS)
    }

    @Test
    void updateOcupatinalExerciseOutcomeFromPersonDAO() {
        personDAO.updateOcupatinalExerciseOutcome(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, EXERCISE_OUTCOME)
    }

    @Test
    void updateOcupatinalSalesPercentageFixedCostsFromPersonDAO() {
        personDAO.updateOcupatinalSalesPercentageFixedCosts(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER,
                SALES_PERCENTAGE_FIXED_COSTS)
    }

    @Test
    void updateOcupatinalSalesPercentageVariableCostsFromPersonDAO() {
        personDAO.updateOcupatinalSalesPercentageVariableCosts(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER,
                SALES_PERCENTAGE_VARIABLE_COSTS)
    }

    @Test
    void updateOcupatinalSalesPercentageBestClientFromPersonDAO() {
        personDAO.updateOcupatinalSalesPercentageBestClient(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER,
                SALES_PERCENTAGE_BEST_CLIENT)
    }

    @Test
    void updateOcupatinalAverageDailyIncomeFromPersonDAO() {
        personDAO.updateOcupatinalAverageDailyIncome(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, AVERAGE_DAILY_INCOME)
    }

    @Test
    void updateOcupatinalLastYearCompensationFromPersonDAO() {
        personDAO.updateOcupatinalLastYearCompensation(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, LAST_YEAR_COMPENSATION)
    }

    @Test
    void updateFrequencySalaryFromPersonDAO() {
        personDAO.updateFrequencySalary(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, FREQUENCY_SALARY)
    }

    @Test
    void updateRetirementDateFromPersonDAO() {
        personDAO.updateRetirementDate(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, RETIREMENT_DATE)
    }

    @Test
    void updateRetirementSchemeFromPersonDAO() {
        personDAO.updateRetirementScheme(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, RETIREMENT_SCHEMA_ID)
    }

    @Test
    void createPersonFromPersonDAO() {
        Person result = personDAO.createPerson(DOCUMENT_TYPE, DOCUMENT_NUMBER, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void updateAddressInformationFromPersonDAO() {
        personDAO.updateAddressInformation(PERSON_ID, DEPARTMENT_ID, PROVINCE_ID, DISCTRICT_ID, STREET_TYPE_ID,
                STREET_NAME, STREET_NUMBER, INTERIOR, DETAIL, LATITUDE, LONGITUDE)
    }

    @Test
    void getReferralsFromPersonDAO() {
        List<Referral> result = personDAO.getReferrals(PERSON_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getPersonRccFromPersonDAO() {
        List<PersonRcc> result = personDAO.getPersonRcc(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getPersonRccCalificationFromPersonDAO() {
        List<RccCalification> result = personDAO.getPersonRccCalification(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getReniecDBDataFromPersonDAO() {
        Reniec result = personDAO.getReniecDBData(DOCUMENT_NUMBER)
        Assert.assertNull(result)
    }

    @Test
    void validateOcupationalInformationFromPersonDAO() {
        personDAO.validateOcupationalInformation(PERSON_ID, VALIDATED)
    }

    @Test
    void getCollectionContactsFromPersonDAO() {
        List<GatewayContacts> result = personDAO.getCollectionContacts(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateReferralFulNameFromPersonDAO() {
        personDAO.updateReferralFulName(REFERRAL_ID, FULLNAME)
    }

    @Test
    void updateReferralRelationshipFromPersonDAO() {
        personDAO.updateReferralRelationship(REFERRAL_ID, RELATION_ID)
    }

    @Test
    void updateReferralPhoneNumberFromPersonDAO() {
        personDAO.updateReferralPhoneNumber(REFERRAL_ID, PHONE_NUMBER)
    }

    @Test
    void updateReferralPhoneTypeFromPersonDAO() {
        personDAO.updateReferralPhoneType(REFERRAL_ID, PHONE_TYPE)
    }

    @Test
    void updateReferralInfoFromPersonDAO() {
        personDAO.updateReferralInfo(REFERRAL_ID, INFO)
    }

    @Test
    void updateReferralValidatedFromPersonDAO() {
        personDAO.updateReferralValidated(REFERRAL_ID, VALIDATED)
    }

    @Test
    void createReferralFromPersonDAO() {
        Referral result = personDAO.createReferral(PERSON_ID, PHONE_TYPE, FULLNAME, RELATION_ID, COUNTRY_CODE,
                PHONE_NUMBER, INFO, LOCALE)
        Assert.assertNotNull(result)
    }

    @Test
    void updatePersonBankAccountInformationFromPersonDAO() {
        personDAO.updatePersonBankAccountInformation(PERSON_ID, BANK_ID, ACCOUNT_TYPE, ACCOUNT, UBIGEO, CCI)
    }

    @Test
    void updateBranchOfficeCodeFromPersonDAO() {
        personDAO.updateBranchOfficeCode(PERSON_ID, BRANCH_OFFICE_CODE)
    }

    @Test
    void updateRandomPartnerFromPersonDAO() {
        personDAO.updateRandomPartner(PERSON_ID, RANDOM_PARTNERS)
    }

    @Test
    void updatePartnerValidatedFromPersonDAO() {
        personDAO.updatePartnerValidated(PERSON_ID, VALIDATED)
    }

    @Test
    void getBureauResultFromPersonDAO() {
        Object result = personDAO.getBureauResult(PERSON_ID, Object.class)
        Assert.assertNull(result)
    }

    @Test
    void getEmployeesByDocumentFromPersonDAO() {
        List<Employee> result = personDAO.getEmployeesByDocument(DOCUMENT_TYPE, DOCUMENT_NUMBER, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployeesBySlugFromPersonDAO() {
        List<Employee> result = personDAO.getEmployeesBySlug(EMPLOYER_USER_ID, SLUG, IS_ACTIVE, LOCALE, RETURN_TYPE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployeesByEmailFromPersonDAO() {
        List<Employee> result = personDAO.getEmployeesByEmail(EMAIL, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployeeByPersonFromPersonDAO() {
        Employee result = personDAO.getEmployeeByPerson(PERSON_ID, EMPLOYER_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployeeByPerson2FromPersonDAO() {
        Employee result = personDAO.getEmployeeByPerson(PERSON_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getEmployeeByIdFromPersonDAO() {
        Employee result = personDAO.getEmployeeById(EMPLOYEE_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void updateEmployeePersonOnlyFromPersonDAO() {
        personDAO.updateEmployeePersonOnly(EMPLOYEE_ID, PERSON_ID)
    }

    @Test
    void updateEmployeePersonFromPersonDAO() {
        personDAO.updateEmployeePerson(EMPLOYEE_ID, PERSON_ID)
    }

    @Test
    void getConsolidableDebtsFromPersonDAO() {
        List<EntityConsolidableDebt> result = personDAO.getConsolidableDebts(DOCUMENT_TYPE, DOCUMENT_NUMBER, IS_CONSOLIDATE)
        Assert.assertNotNull(result)
    }

    @Test
    void validateAssociatedFromPersonDAO() {
        personDAO.validateAssociated(PERSON_ID, ENTITY_ID, IS_VALIDATED)
    }

    @Test
    void getAssociatedFromPersonDAO() {
        PersonAssociated result = personDAO.getAssociated(PERSON_ID, ENTITY_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerAssociatedFromPersonDAO() {
        Object result = personDAO.registerAssociated(PERSON_ID, ENTITY_ID, ASSCIATED_ID, PASSBOOK_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void registerNegativeBaseFromPersonDAO() {
        personDAO.registerNegativeBase(PERSON_ID)
    }

    @Test
    void updateHousingTypeFromPersonDAO() {
        personDAO.updateHousingType(PERSON_ID, HOUSING_TYPE_ID)
    }

    @Test
    void updateResidenceTimeFromPersonDAO() {
        personDAO.updateResidenceTime(PERSON_ID, RESIDENCE_TIME_IN_MONTHS)
    }

    @Test
    void updateFixedGrossIncomeIncreasalFromPersonDAO() {
        personDAO.updateFixedGrossIncomeIncreasal(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, FIXED_GROSS_INCOME_INCRESAL)
    }

    @Test
    void updateClient1Ruc65FromPersonDAO() {
        personDAO.updateClient1Ruc65(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, CLIENT1_RUC_65)
    }

    @Test
    void updateLastYearSellingsIncreasalFromPersonDAO() {
        personDAO.updateLastYearSellingsIncreasal(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, LAST_YEAR_SELLING_INCREASAL)
    }

    @Test
    void updateExerciseOutcomeIncreasalFromPersonDAO() {
        personDAO.updateExerciseOutcomeIncreasal(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, EXERCISE_OUTCOME_INCREASAL)
    }

    @Test
    void updatesalesPercentageBestClientIndicatorFromPersonDAO() {
        personDAO.updatesalesPercentageBestClientIndicator(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, SALES_PERCENTAGE_BEST_CLIENT_INDICATOR)
    }

    @Test
    void updatePersonAccountByCreditFromPersonDAO() {
        personDAO.updatePersonAccountByCredit(PERSON_ID, CREDIT_ID, BANK_ID, ACCOUNT, ACCOUNT_TYPE, CCI)
    }

    @Test
    void updateStudyLevelFromPersonDAO() {
        personDAO.updateStudyLevel(PERSON_ID, STUDY_LEVEL_ID)
    }

    @Test
    void updateProfessionFromPersonDAO() {
        personDAO.updateProfession(PERSON_ID, PROFESSION_ID)
    }

    @Test
    void cleanOcupationalInformationFromPersonDAO() {
        personDAO.cleanOcupationalInformation(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER)
    }

    @Test
    void updateConcludedStudiesFromPersonDAO() {
        personDAO.updateConcludedStudies(PERSON_ID, COMPLETED)
    }

    @Test
    void validatePersonFromPersonDAO() {
        List<PersonValidator> result = personDAO.validatePerson(LOCALE, EMPLOYER_ID, JSON)
        Assert.assertNull(result)
    }

    @Test
    void getHistoricAddressByPersonFromPersonDAO() {
        List<PersonContactInformation> result = personDAO.getHistoricAddressByPerson(PERSON_ID, LOCALE)
        Assert.assertNull(result)
    }

    @Test
    void getMonthlyInstallmentMortageFromPersonDAO() {
        Double result = personDAO.getMonthlyInstallmentMortage(DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void updateDependentsFromPersonDAO() {
        personDAO.updateDependents(PERSON_ID, DEPENDENTS)
    }

    @Test
    void updatePepInformationFromPersonDAO() {
        personDAO.updatePepInformation(PERSON_ID, PEP, PEP_DETAIL)
    }

    @Test
    void isDocumentValidForEntityFromPersonDAO() {
        boolean result = personDAO.isDocumentValidForEntity(ENTITY_ID, PRODUCT_CATEGORY_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void registerDisgregatedAddressFromPersonDAO() {
        personDAO.registerDisgregatedAddress(PERSON_ID, DIRECCION)
    }

    @Test
    void getDisggregatedAddressFromPersonDAO() {
        Direccion result = personDAO.getDisggregatedAddress(511780, "H")
        Assert.assertNull(result)
    }

    @Test
    void hasDisggregatedAddressFromPersonDAO() {
        int result = personDAO.hasDisggregatedAddress(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getDisggregatedAddress2FromPersonDAO() {
        List<DisggregatedAddress> result = personDAO.getDisggregatedAddress(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getExternalGrossIncomeFromPersonDAO() {
        Double result = personDAO.getExternalGrossIncome(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getPreApprovedDataByDocumentFromPersonDAO() {
        List<PreApprovedInfo> result = personDAO.getPreApprovedDataByDocument(PRODUCT_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void getPreApprovedDataFromPersonDAO() {
        List<PreApprovedInfo> result = personDAO.getPreApprovedData(DOCUMENT_TYPE, DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void deletePreApprovedBaseFromPersonDAO() {
        personDAO.deletePreApprovedBase(ENTITY_ID, PRODUCT_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER)
    }

    @Test
    void registerRejectionFromPersonDAO() {
        personDAO.registerRejection(ENTITY_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER, EVALUATION_RESULT, REJECTION_CODE,
                REJECTION_DESCRIPTION)
    }

    @Test
    void registerPreApprovedBaseFromPersonDAO() {
        personDAO.registerPreApprovedBase(ENTITY_ID, PRODUCT_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER, MAX_AMOUNT,
                MAX_INSTALLMENTS, TEA, CARD_TYPE, CARD_NUMBER, PAYMENT_DAY, BILLING_CLOSE, CLUSTER)
    }

    @Test
    void registerPreApprovedBaseByEntityProductParameterFromPersonDAO() {
        personDAO.registerPreApprovedBaseByEntityProductParameter(ENTITY_ID, PRODUCT_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER,
                MAX_AMOUNT, MAX_INSTALLMENTS, TEA, CARD_TYPE, CARD_NUMBER, PAYMENT_DAY, BILLING_CLOSE, CLUSTER,
                ENTITY_PRODUCT_PARAMETER)
    }

    @Test
    void getSunatResultFromPersonDAO() {
        SunatResult result = personDAO.getSunatResult(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getSunatResultByRucFromPersonDAO() {
        SunatResult result = personDAO.getSunatResultByRuc(RUC)
        Assert.assertNull(result)
    }

    @Test
    void registerAbandonedApplicationFromPersonDAO() {
        personDAO.registerAbandonedApplication(EMAIL)
    }

    @Test
    void getDisaggregatedHomeAddressByCreditFromPersonDAO() {
        Direccion result = personDAO.getDisaggregatedHomeAddressByCredit(PERSON_ID, CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void getDisaggregatedJobAddressByCreditFromPersonDAO() {
        Direccion result = personDAO.getDisaggregatedJobAddressByCredit(PERSON_ID, CREDIT_ID)
        Assert.assertNull(result)
    }

    @Test
    void getEssaludResultFromPersonDAO() {
        EssaludResult result = personDAO.getEssaludResult(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getExternalEssaludStartDateFromPersonDAO() {
        Date result = personDAO.getExternalEssaludStartDate(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void updateOcupationalInformationBoChangedFromPersonDAO() {
        personDAO.updateOcupationalInformationBoChanged(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, CHANGED)
    }

    @Test
    void getAfipResultFromPersonDAO() {
        AfipResult result = personDAO.getAfipResult(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getBcraResultFromPersonDAO() {
        BcraResult result = personDAO.getBcraResult(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getAnsesResultFromPersonDAO() {
        AnsesResult result = personDAO.getAnsesResult(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void registerAddressCoordinatesFromPersonDAO() {
        personDAO.registerAddressCoordinates(PERSON_ID, DIRECCION)
    }

    @Test
    void registerJobAddressCoordinatesFromPersonDAO() {
        personDAO.registerJobAddressCoordinates(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, DIRECCION)
    }

    @Test
    void completeAddressCoordinatesFromPersonDAO() {
        Direccion result = personDAO.completeAddressCoordinates(PERSON_ID, DIRECCION)
        Assert.assertNotNull(result)
    }

    @Test
    void getInfoFromStaticDBFromPersonDAO() {
        StaticDBInfo result = personDAO.getInfoFromStaticDB(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void getRucInfoFromPersonDAO() {
        RucInfo result = personDAO.getRucInfo(RUC)
        Assert.assertNull(result)
    }

    @Test
    void updateBirthUbigeoFromPersonDAO() {
        personDAO.updateBirthUbigeo(PERSON_ID, UBIGEO)
    }

    @Test
    void updatebankAccountVerifiedFromPersonDAO() {
        personDAO.updatebankAccountVerified(VERIFEID, PERSON_ID)
    }

    @Test
    void getOnpeResultFromPersonDAO() {
        OnpeResult result = personDAO.getOnpeResult(PERSON_ID)
        Assert.assertNull(result)
    }

    @Test
    void isRawAssociatedFromPersonDAO() {
        Boolean result = personDAO.isRawAssociated(PERSON_ID, ENTITY_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void searchPersonByNamesFromPersonDAO() {
        JSONObject result = personDAO.searchPersonByNames(NAME, FIRST_SURNAME, LAST_SURNAME, GENDER, DAY, MONTH)
        Assert.assertNull(result)
    }

    @Test
    void updatePensionerIncomeFromPersonDAO() {
        personDAO.updatePensionerIncome(PERSON_ID, OCCUPATIONAL_INFORMATION_NUMBER, FIXED_GROSS_INCOME,
                RETIREMENT_SCHEMA_ID, RETIREMENT_DATE)
    }

    @Test
    void updateCustomProfessionFromPersonDAO() {
        personDAO.updateCustomProfession(PERSON_ID, CUSTOM_PROFESSION_ID)
    }

    @Test
    void getPersonDisqualifierByPersonIdFromPersonDAO() {
        List<PersonDisqualifier> result = personDAO.getPersonDisqualifierByPersonId(PERSON_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void savePersonDisqualifierFromPersonDAO() {
        personDAO.savePersonDisqualifier(PERSON_ID, TYPE, IS_DISQUILIFIED, DETAIL, USER_FILE_ID)
    }

    @Test
    void updatePersonDisqualifierFromPersonDAO() {
        personDAO.updatePersonDisqualifier(PERSON_ID, IS_DISQUILIFIED, DETAIL, USER_FILE_ID, TYPE)
    }

    @Test
    void updatePersonDisqualifierNoImageFromPersonDAO() {
        personDAO.updatePersonDisqualifierNoImage(PERSON_ID, IS_DISQUILIFIED, DETAIL, TYPE)
    }

    @Test
    void registerEmailToBlacklistFromPersonDAO() {
        personDAO.registerEmailToBlacklist(EMAIL)
    }

    @Test
    void getPersonRawAssociatedByDocumentFromPersonDAO() {
        List<PersonRawAssociated> result = personDAO.getPersonRawAssociatedByDocument(DOCUMENT_TYPE, DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void getCendeuResultFromPersonDAO() {
        List<CendeuResult> result = personDAO.getCendeuResult(DOCUMENT_NUMBER)
        Assert.assertNotNull(result)
    }

    @Test
    void migrateFromApprovedDataFromPersonDAO() {
        personDAO.migrateFromApprovedData(LOAN_APPLICATION_ID, ENTITY_ID, PRODUCT_ID, DOCUMENT_TYPE, DOCUMENT_NUMBER)
    }

    @Test
    void updateLandlineFromPersonDAO() {
        personDAO.updateLandline(PERSON_ID, LANDLINE)
    }

    @Test
    void getPadronAfipFullNameFromPersonDAO() {
        String result = personDAO.getPadronAfipFullName(DOCUMENT_NUMBER)
        Assert.assertNull(result)
    }

    @Test
    void updateOrInsertPersonDisqualifierByPersonIdOnlyFromPersonDAO() {
        personDAO.updateOrInsertPersonDisqualifierByPersonIdOnly(PERSON_ID, TYPE, IS_DISQUILIFIED, DETAIL)
    }

    @Test
    void getBancoDelSolEmploymentDetailsByPersonIdFromPersonDAO() {
        BancoDelSolCsvInfo result = personDAO.getBancoDelSolEmploymentDetailsByPersonId(LOAN_APPLICATION_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getBancoDelSolEmployeeHierarchyFromPersonDAO() {
        JSONObject result = personDAO.getBancoDelSolEmployeeHierarchy(USER_ENTITY_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerProfessionOccupationFromPersonDAO() {
        personDAO.registerProfessionOccupation(PERSON_ID, PROFESSION_ID)
    }

    @Test
    void getMigracionesFromPersonBODAO() {
        MigracionesResult migracionesResult = personDAO.getMigraciones(2007)
        Assert.assertNull(migracionesResult)
    }

    @Test
    void getRedamResultFromPersonBODAO() {
        RedamResult redamResult = personDAO.getRedamResult(2007)
        Assert.assertNull(redamResult)
    }

    @Test
    void deletePreApprovedLoanApplicationBase() {
        personDAO.deletePreApprovedLoanApplicationBase(24073)
    }
}
