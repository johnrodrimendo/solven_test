package com.affirm.common.dao;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.model.PreApprovedInfo;
import com.affirm.common.model.rcc.Synthesized;
import com.affirm.common.model.UniversidadPeru;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public interface PersonDAO {

    Person getPerson(CatalogService catalogService, Locale locale, int personId, boolean getPartner) throws Exception;

    Person getPerson(int personId, boolean getPartner, Locale locale) throws Exception;

    Integer getPersonIdByDocument(int documentType, String documentNumber) throws Exception;

    PersonContactInformation getPersonContactInformation(Locale locale, int personId) throws Exception;

    List<PersonOcupationalInformation> getPersonOcupationalInformation(Locale locale, int personId) throws Exception;

    PersonBankAccountInformation getPersonBankAccountInformationByCredit(Locale locale, int personId, int creditId) throws Exception;

    PersonBankAccountInformation getPersonBankAccountInformation(Locale locale, int personId) throws Exception;

    List<LoanApplicationUserFiles> getUserFiles(int personId, Locale locale);

    List<LoanApplicationUserFiles> getLoanApplicationFiles(int loanAppId, int personId, Locale locale);

    void updateBirthday(int personId, Date birthday) throws Exception;

    void updateNationality(int personId, Integer nationalityId) throws Exception;

    void updateMaritalStatus(int personId, Integer maritalStatusId) throws Exception;

    void updateName(int personId, String name) throws Exception;

    void updateFirstSurname(int personId, String firstSurname) throws Exception;

    void updateLastSurname(int personId, String lastSurname) throws Exception;

    void updateGender(int personId, char gender) throws Exception;

    void updatePartner(int personId, Integer partnerPersonId) throws Exception;

    void updateEmail(int personId, String email) throws Exception;

    void updatePhoneNumber(int userId, String countryCode, String phoneNumber) throws Exception;

    void updateOcupationalActivityType(int personId, int ocupationalInformationNumber, int activityTypeId) throws Exception;

    void updateOcupationalCompany(int personId, int ocupationalInformationNumber, String companyName) throws Exception;

    void updateOcupationalCiiu(int personId, int ocupationalInformationNumber, String ciiu) throws Exception;

    void updateOcupationalRegime(int personId, int ocupationalInformationNumber, String regimeId) throws Exception;

    void updateOcupationalRuc(int personId, int ocupationalInformationNumber, String ruc) throws Exception;

    void updateOcupationalRuc(int personId, int ocupationalInformationNumber, String ruc, String taxRegime) throws Exception;

    void updateOcupationalShareholding(int personId, int ocupationalInformationNumber, String shareholding) throws Exception;

    void updateOcupationalResultU12M(int personId, int ocupationalInformationNumber, Integer resultU12M) throws Exception;

    void updateFixedGrossIncome(int personId, int ocupationalInformationNumber, Integer fixedGrossIncome) throws Exception;

    void updateFixedGrossIncomeDouble(int personId, int ocupationalInformationNumber, Double fixedGrossIncome) throws Exception;

    void updateHasOtherIncome(int personId, int ocupationalInformationNumber, Boolean otherIncome) throws Exception;

    void updateVariableGrossIncome(int personId, int ocupationalInformationNumber, Integer variableGrossIncome) throws Exception;

    void updateVariableGrossIncomeDouble(int personId, int ocupationalInformationNumber, Double variableGrossIncome) throws Exception;

    void updateOcupationalAddress(int personId, int ocupationalInformationNumber, String address, Boolean isHomeAddress, String addressDepartment, String addressProvince, String addressDistrict) throws Exception;

    void updateEmploymentTime(int personId, int ocupationalInformationNumber, String employmentTime) throws Exception;

    void updateVariableIncome(int personId, int ocupationalInformationNumber, Integer variableIncome) throws Exception;

    void updateBelonging(int personId, int ocupationalInformationNumber, String belongings) throws Exception;

    void updateOcupatinalPhoneNumber(int personId, String phoneType, int ocupationalInformationNumber, String phoneNumber) throws Exception;

    void updateOcupatinalStartDate(int personId, int ocupationalInformationNumber, Date startDate) throws Exception;

    void updateOtherActivityTypeIncome(int personId, Integer netIncome) throws Exception;

    void updateOcupatinalServiceType(int personId, int ocupationalInformationNumber, Integer serviceTypeId) throws Exception;

    void updateOcupatinalOcupation(int personId, int ocupationalInformationNumber, Integer ocupationId) throws Exception;

    void updateOcupatinalSubActivityType(int personId, int ocupationalInformationNumber, Integer subActivityType) throws Exception;

    void updateOcupatinalClient1(int personId, int ocupationalInformationNumber, String client1) throws Exception;

    void updateOcupatinalClient2(int personId, int ocupationalInformationNumber, String client2) throws Exception;

    void updateOcupatinalLastYearSellings(int personId, int ocupationalInformationNumber, Double sellings) throws Exception;

    void updateOcupatinalExerciseOutcome(int personId, int ocupationalInformationNumber, Double exerciseOutcome) throws Exception;

    void updateOcupatinalSalesPercentageFixedCosts(int personId, int ocupationalInformationNumber, Double salesPercentageFixedCosts) throws Exception;

    void updateOcupatinalSalesPercentageVariableCosts(int personId, int ocupationalInformationNumber, Double salesPercentageVariableCosts) throws Exception;

    void updateOcupatinalSalesPercentageBestClient(int personId, int ocupationalInformationNumber, Double salesPercentageBestClient) throws Exception;

    void updateOcupatinalAverageDailyIncome(int personId, int ocupationalInformationNumber, Double averageDailyIncome) throws Exception;

    void updateOcupatinalLastYearCompensation(int personId, int ocupationalInformationNumber, Double lastYearCompensation) throws Exception;

    void updateFrequencySalary(int personId, int ocupationalInformationNumber, FrequencySalary frequencySalary) throws Exception;

    void updateRetirementDate(int personId, int ocupationalInformationNumber, Date retirementDate) throws Exception;

    void updateRetirementScheme(int personId, int ocupationalInformationNumber, int retirementSchemeId) throws Exception;

    Person createPerson(int documentType, String documentNumber, Locale locale) throws Exception;

    void updateAddressInformation(
            int personId, String departmentId, String provinceId, String districtId, Integer streetTypeId, String streetName,
            String streetNumber, String interior, String detail, Double latitude, Double longitude);

    List<Referral> getReferrals(int personId, Locale locale) throws Exception;

    List<PersonRcc> getPersonRcc(int personId) throws Exception;

    List<RccCalification> getPersonRccCalification(int personId) throws Exception;

    List<RucRccCalification> getRucRccCalification(String ruc) throws Exception;

    List<RucRcc> getRucRcc(String ruc) throws Exception;

    Reniec getReniecDBData(String docNumber) throws Exception;

    void validateOcupationalInformation(int personId, boolean validated) throws Exception;

    List<GatewayContacts> getCollectionContacts(int personId) throws Exception;

    void updateReferralFulName(int referralId, String fullName) throws Exception;

    void updateReferralRelationship(int referralId, int relationshipId) throws Exception;

    void updateReferralPhoneNumber(int referralId, String phoneNumber) throws Exception;

    void updateReferralInfo(int referralId, String info) throws Exception;

    void updateReferralValidated(int referralId, boolean validated) throws Exception;

    Referral createReferral(Integer personId, String phoneType, String fullName, Integer relationshipId, String countryCode, String phoneNumber, String info, Locale locale) throws Exception;

    void deletePreviousReferrals(Integer personId) throws Exception;

    void updatePersonBankAccountInformation(Integer personId, Integer bankId, Character accountType, String account, String ubigeo, String cci) throws Exception;

    void updateBranchOfficeCode(int personId, String branchOfficeCode) throws Exception;

    void updateRandomPartner(int personId, String randomPartners) throws Exception;

    void updatePartnerValidated(int personId, Boolean validated) throws Exception;

    <T> T getBureauResult(Integer personId, Class<T> klass) throws Exception;

    List<Employee> getEmployeesByDocument(Integer docType, String docNumber, Locale locale) throws Exception;

    <T extends Employee> List<T> getEmployeesBySlug(int employerUserId, String slug, Boolean isActive, Locale locale, Class<T> returntype) throws Exception;

    List<Employee> getEmployeesByEmail(String email, Locale locale) throws Exception;

    Employee getEmployeeByPerson(int personId, Integer employerId, Locale locale) throws Exception;

    Employee getEmployeeById(int employeeId, Locale locale) throws Exception;

    void updateEmployeePersonOnly(int employeeId, int personId) throws Exception;

    void updateEmployeePerson(int employeeId, int personId) throws Exception;

    List<EntityConsolidableDebt> getConsolidableDebts(int docType, String docNumber, Boolean isConsolidable) throws Exception;

    void validateAssociated(int personId, int entityId, boolean isValidated) throws Exception;

    PersonAssociated getAssociated(int personId, int entityId) throws Exception;

    Object registerAssociated(int personId, int entityId, String associatedId, String passbookNumber) throws Exception;

    Boolean getPersonNegativeBase(Integer personId) throws Exception;

    Boolean getPersonHasDebt(Integer personId) throws Exception;

    void registerNegativeBase(Integer personId) throws Exception;

    void updatePersonAccountByCredit(Integer personId, Integer creditId, Integer bankId, String account, Character accountType, String cci) throws Exception;

    void updateHousingType(int personId, Integer housingTypeId) throws Exception;

    void updateResidenceTime(int personId, int residenceTimeInMonths) throws Exception;

    void updateStudyLevel(int personId, Integer studyLevelId) throws Exception;

    void updateProfession(int personId, Integer professionId) throws Exception;

    void cleanOcupationalInformation(int personId, int ocupatinalInformationNumber);

    void updateConcludedStudies(int personId, Boolean completed);

    List<PersonValidator> validatePerson(Locale locale, int employerId, String json) throws Exception;

    void updateFixedGrossIncomeIncreasal(int personId, int number, Integer fixedGrossIncomeIncreasal) throws Exception;

    void updateClient1Ruc65(int personId, int number, Boolean client1Ruc65) throws Exception;

    void updateLastYearSellingsIncreasal(int personId, int number, Integer lastYearSellingsIncreasal) throws Exception;

    void updateExerciseOutcomeIncreasal(int personId, int number, Integer exerciseOutcomeIncreasal) throws Exception;

    void updatesalesPercentageBestClientIndicator(int personId, int number, Integer salesPercentageBestClientIndicator) throws Exception;

    Employee getEmployeeByPerson(int personId, Locale locale) throws Exception;

    Double getMonthlyInstallmentMortage(String docNumber);

    void updateDependents(int personId, Integer dependents);

    void updatePepInformation(int personId, Boolean pep, String pepDetail);

    boolean isDocumentValidForEntity(int entityId, int productCategoryId, int docType, String docNumber);

    void registerDisgregatedAddress(Integer personId, Direccion direccion);

    List<PersonContactInformation> getHistoricAddressByPerson(int personId, Locale locale) throws Exception;

    int hasDisggregatedAddress(Integer personId) throws Exception;

    Double getExternalGrossIncome(Integer personId) throws Exception;

    List<PreApprovedInfo> getPreApprovedDataByDocument(Integer productId, Integer documentType, String documentNumber) throws Exception;

    List<PreApprovedInfo> getPreApprovedData(Integer documentType, String documentNumber) throws Exception;

    void deletePreApprovedBase(Integer entityId, Integer productId, Integer documentType, String documentNumber);

    void registerPreApprovedBase(Integer entityId, Integer productId, Integer documentType, String documentNumber,
                                 Double maxAmount, Integer maxInstallments, Double tea, String cardType, String cardNumber,
                                 Integer paymentDay, Integer billingClose, String cluster);

    void registerPreApprovedBaseByEntityProductParameter(Integer entityId, Integer productId, Integer documentType, String documentNumber,
                                 Double maxAmount, Integer maxInstallments, Double tea, String cardType, String cardNumber,
                                 Integer paymentDay, Integer billingClose, String cluster, String entityProductParameter);

    void insertPreApprovedBaseByEntityProductParameter(Integer entityId, Integer productId, Integer documentType, String documentNumber,
                                                       Double maxAmount, Integer maxInstallments, Double tea, String cardType, String cardNumber,
                                                       Integer paymentDay, Integer billingClose, String cluster, String entityProductParameter);

    SunatResult getSunatResult(int personId) throws Exception;

    Direccion getDisaggregatedHomeAddressByCredit(int personId, int creditId) throws Exception;

    Direccion getDisaggregatedJobAddressByCredit(int personId, int creditId) throws Exception;

    void deletePreApprovedLoanApplicationBase(Integer loanApplicationId);

    void registerRejection(Integer entityId, Integer documentType, String documentNumber, String evaluationResult, String rejectionCode, String rejectionDescription);

    Direccion getDisggregatedAddress(Integer personId, String tipoDomicilio) throws Exception;

    List<DisggregatedAddress> getDisggregatedAddress(Integer personId) throws Exception;

    SunatResult getSunatResultByRuc(String ruc) throws Exception;

    void registerAbandonedApplication(String email);

    EssaludResult getEssaludResult(int personId) throws Exception;

    Date getExternalEssaludStartDate(int personId) throws Exception;

    void updateOcupationalInformationBoChanged(int personId, int ocupationalInformationNumber, boolean changed) throws Exception;

    AfipResult getAfipResult(int personId) throws Exception;

    BcraResult getBcraResult(int personId) throws Exception;

    AnsesResult getAnsesResult(int personId) throws Exception;

    void updateReferralPhoneType(int referralId, String phoneType) throws Exception;

    void registerAddressCoordinates(Integer personId, Direccion direccion) throws Exception;

    Direccion completeAddressCoordinates(Integer personId, Direccion direccion) throws Exception;

    void registerJobAddressCoordinates(Integer personId, Integer ocupationalInformationNumber, Direccion direccion) throws Exception;

    StaticDBInfo getInfoFromStaticDB(Integer personId) throws Exception;

    RucInfo getRucInfo(String ruc) throws Exception;

    void updateBirthUbigeo(int personId, String ubigeo) throws Exception;

    void updateOcupationalSector(int personId, int ocupationalInformationNumber, String sectorId) throws Exception;

    void updatebankAccountVerified(Boolean verified, Integer personId) throws Exception;

    OnpeResult getOnpeResult(int personId) throws Exception;

    Boolean isRawAssociated(int personId, int entityId) throws Exception;

    JSONObject searchPersonByNames(String name, String firstSurname, String secondSurname, Character gender, Integer day, Integer month) throws Exception;

    void updatePensionerIncome(int personId, int ocupationalInformationNumber, Integer income, Integer scheme, Date retDate) throws Exception;

    void updateCustomProfession(int personId, Integer customProfessionId) throws Exception;

    List<PersonDisqualifier> getPersonDisqualifierByPersonId(Integer personId) throws Exception;

    void savePersonDisqualifier(int personId, String type, boolean isDisquilified, String detail, int userFileId) throws Exception;

    void updatePersonDisqualifier(int personId, boolean isDisquilified, String detail, int userFileId, String type) throws Exception;

    void updatePersonDisqualifierNoImage(int personId, boolean isDisquilified, String detail, String type) throws Exception;

    void registerEmailToBlacklist(String email) throws Exception;

    List<PersonRawAssociated> getPersonRawAssociatedByDocument(int documentType, String documentNumber) throws Exception;

    List<CendeuResult> getCendeuResult(String documentNumber) throws Exception;

    void migrateFromApprovedData(int loanApplicationId, Integer entityId, Integer productId, Integer docType, String docNumber) throws Exception;

    void updateLandline(int personId, String landline) throws Exception;

    String getPadronAfipFullName(String docNumber) throws Exception;

    void updateOrInsertPersonDisqualifierByPersonIdOnly(int personId, String type, Boolean isDisqualifed, String detail);

    BancoDelSolCsvInfo getBancoDelSolEmploymentDetailsByPersonId (Integer loanApplicationId) throws Exception ;

    JSONObject getBancoDelSolEmployeeHierarchy(Integer userEntityId);

    void registerProfessionOccupation(int personId, int professionOccupationId) throws Exception;

    MigracionesResult getMigraciones(Integer personId) throws Exception;

    RedamResult getRedamResult(int personId) throws Exception;

    List<SatPlateResult> getSatPlateResult(int personId) throws Exception;

    void deleteDisaggregatedAddress(int personId);

    void deleteDisaggregatedAddress(int personId, char addressType);

    Integer getEmployeesQuantity(String enterpriseRuc);

    UniversidadPeru getUniversidadPeru(int personId);

    void updateAddressInformation(int personId, String districtId, Integer streetTypeId, String streetName,
                                  String streetNumber, String interior, String detail, Double latitude, Double longitude);

    void updateOcupationalAddress(int personId, int ocupationalInformationNumber, String address, Boolean isHomeAddress, String addressDistrict);

    void updateOcupationAreaId(int personId, int ocupationalInformationNumber, int ocupationalAreaId) throws Exception;

    void updateContractType(int personId, int ocupationalInformationNumber, char type) throws Exception;

    Boolean isInBdsSancorEmployees(String cuit);

    List<RccIdeGrouped> getIdeGroupeds(String documentNumber) throws Exception;

    List<Synthesized> getSynthesizeds(String documentNumber);

    void generateSynthesizedByDocument(String documentNumber);

    void updateDocumentType(int referralId, Integer documentTypeId) throws Exception;

    void updateDocumentNumber(int referralId, String documentNumber) throws Exception;

    void updateOcupationalFormal(int personId, int ocupationalInformationNumber, Boolean formal) throws Exception;

    List<CendeuUlt24Result> getCendeu24Result(String documentNumber) throws Exception;
}
