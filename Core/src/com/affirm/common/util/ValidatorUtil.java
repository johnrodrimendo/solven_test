/**
 *
 */
package com.affirm.common.util;

import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author jrodriguez
 */
public class ValidatorUtil {

    private static int currentYear;

    static {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        currentYear = calendar.get(Calendar.YEAR);
    }

    public static boolean isEmpty(String value) {
        if (value != null && !value.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * List of all the reusable validations
     * <p>
     * This ones have the required set to TRUE. If you want to change it, do it in the calling form
     */
    // Seacrh
    public static final StringFieldValidator SEARCH = new StringFieldValidator().setRequired(false).setRestricted(false).setMaxCharacters(60).setValidRegex(StringFieldValidator.REGEXP_SEARCH);
    //Person
    public static final StringFieldValidator NAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(40).setValidRegex(StringFieldValidator.REGEXP_NAMES);
    public static final StringFieldValidator FULLNAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(50).setValidRegex(StringFieldValidator.REGEXP_NAMES);
    public static final StringFieldValidator NAME_AND_LASTNAMES = new StringFieldValidator().setRequired(true).setMaxCharacters(100).setValidRegex(StringFieldValidator.REGEXP_FULLNAMES);
    public static final StringFieldValidator FIRST_SURNAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(30).setValidRegex(StringFieldValidator.REGEXP_NAMES);
    public static final StringFieldValidator LAST_SURNAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(30).setValidRegex(StringFieldValidator.REGEXP_NAMES);
    public static final StringFieldValidator BIRTHDAY = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE);
    public static final IntegerFieldValidator NATIONALITY = new IntegerFieldValidator().setRequired(true);
    public static final CharFieldValidator GENDER = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'M', 'F'});
    public static final IntegerFieldValidator MARITAL_STATUS_ID = new IntegerFieldValidator().setRequired(true);
    public static final IntegerFieldValidator DEPENDENTS = new IntegerFieldValidator().setRequired(true);
    public static final IntegerFieldValidator DOC_TYPE_ID = new IntegerFieldValidator().setRequired(true);
    public static final StringFieldValidator DOC_NUMBER_DNI = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(8).setMinCharacters(8).setTypeField("dígitos");
    public static final StringFieldValidator DOC_NUMBER_CE = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC).setMaxCharacters(9).setMinCharacters(9).setTypeField("dígitos");
    public static final StringFieldValidator DOC_NUMBER_CDI = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(11).setTypeField("dígitos").setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL).setValidPatternErrorMsg("validation.regex.cdi_cuit_cuil").setValidRegexErrorMsg("validation.regex.cdi_cuit_cuil");
    public static final StringFieldValidator DOC_NUMBER_CUIL = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(11).setTypeField("dígitos").setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL).setValidPatternErrorMsg("validation.regex.cdi_cuit_cuil").setValidRegexErrorMsg("validation.regex.cdi_cuit_cuil");
    public static final StringFieldValidator DOC_NUMBER_CUIT = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(11).setTypeField("dígitos").setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL).setValidPatternErrorMsg("validation.regex.cdi_cuit_cuil").setValidRegexErrorMsg("validation.regex.cdi_cuit_cuil");
    public static final StringFieldValidator DOC_NUMBER_CDI_PERSONAL = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(11).setTypeField("dígitos").setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL_PERSONAL).setValidPatternErrorMsg("validation.regex.cdi_cuit_cuil_personal").setValidRegexErrorMsg("validation.regex.cdi_cuit_cuil_personal");
    public static final StringFieldValidator DOC_NUMBER_CUIT_PERSONAL = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(11).setTypeField("dígitos").setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL_PERSONAL).setValidPatternErrorMsg("validation.regex.cdi_cuit_cuil_personal").setValidRegexErrorMsg("validation.regex.cdi_cuit_cuil_personal");
    public static final StringFieldValidator DOC_NUMBER_CUIL_PERSONAL = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(11).setTypeField("dígitos").setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL_PERSONAL).setValidPatternErrorMsg("validation.regex.cdi_cuit_cuil_personal").setValidRegexErrorMsg("validation.regex.cdi_cuit_cuil_personal");
    public static final StringFieldValidator DOC_NUMBER_COL_CEDULA = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(5).setTypeField("dígitos");
    public static final StringFieldValidator DOC_NUMBER_COL_CEDULA_EXT = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(11).setMinCharacters(8).setTypeField("dígitos");
    public static final StringFieldValidator EMAIL = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(80).setEmailFormat(true).setValidRegex(StringFieldValidator.REGEXP_EMAIL).setValidPattern(StringFieldValidator.PATTER_REGEX_EMAIL).setValidRegexErrorMsg("validation.string.emailFormat").setValidPatternErrorMsg("validation.string.emailFormat");
    public static final StringFieldValidator COUNTRY_CODE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(2).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator CITY_CODE = new StringFieldValidator().setRequired(true).setRestricted(true).setMinCharacters(1).setMaxCharacters(2).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator BIRTH_UBIGEO = new StringFieldValidator().setRequired(true).setRestricted(true).setMinCharacters(6).setMaxCharacters(6).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator LAND_LINE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(9).setMinCharacters(6).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator PHONE_OR_CELLPHONE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(10).setMinCharacters(7).setValidRegex(StringFieldValidator.REGEXP_PHONE_NUMBERS_HYPHEN).setMaxCharactersErrorMsg("validation.phoneNumber.genericMaxCharacters").setMinCharactersErrorMsg("validation.phoneNumber.genericMinCharacters");
    public static final StringFieldValidator PERU_PHONE_AREA_CODE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(3).setMinCharacters(1).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator ARGENTINA_PHONE_AREA_CODE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(4).setMinCharacters(2).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setValidPattern(StringFieldValidator.PATTER_PHONE_CODE_AR).setValidPatternErrorMsg("validation.procces.phoneCodeError.ar").setValidRegexErrorMsg("validation.procces.phoneCodeError.ar");
    public static final StringFieldValidator CELLPHONE_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(9).setMinCharacters(9).setValidRegex(StringFieldValidator.REGEXP_PHONE_NUMBERS).setValidPattern(StringFieldValidator.PATTER_PHONE_NUMBERS_PERU).setValidRegexErrorMsg("validation.process.cellphoneNumber").setValidPatternErrorMsg("validation.process.cellphoneNumber");
    public static final StringFieldValidator CELLPHONE_NUMBER_ARGENTINA = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(8).setMinCharacters(6)./*setValidPattern(StringFieldValidator.PATTER_PHONE_NUMBERS_ARGENTINA).*/setValidRegex(StringFieldValidator.REGEXP_PHONE_NUMBERS_HYPHEN);
    public static final StringFieldValidator CELLPHONE_NUMBER_COLOMBIA = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(10).setMinCharacters(10).setValidRegex(StringFieldValidator.REGEXP_PHONE_NUMBERS).setValidPattern(StringFieldValidator.PATTER_PHONE_NUMBERS_COLOMBIA).setValidRegexErrorMsg("validation.process.cellphoneNumber").setValidPatternErrorMsg("validation.process.cellphoneNumber");
    public static final StringFieldValidator BO_PHONE_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(15).setMinCharacters(6).setValidRegex(StringFieldValidator.REGEXP_PHONE_NUMBERS_WITH_AREA_CODE);
    public static final StringFieldValidator ANNEX_PHONE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(5).setMinCharacters(2).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator SMS_TOKEN = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(4).setMinCharacters(4).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setRequiredErrorMsg("validation.process.smsToken");
    public static final StringFieldValidator EMAIL_TOKEN = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(4).setMinCharacters(4).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setRequiredErrorMsg("validation.process.smsToken");
    public static final BooleanFieldValidator PEP = new BooleanFieldValidator().setRequired(true);
    public static final CharFieldValidator NETWORK_PROVIDER = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'W', 'G', 'Y', 'F', 'L', 'M'});
    public static final StringFieldValidator ACCESS_TOKEN = new StringFieldValidator().setRequired(true).setValidRegex(null);
    public static final StringFieldValidator OAUTH_CODE = new StringFieldValidator().setRequired(true).setValidRegex(null);
    public static final StringFieldValidator AR_POSTAL_CODE = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setMaxCharacters(4).setMinCharacters(4).setTypeField("dígitos");

    // User
    public static final StringFieldValidator USER_PASSWORD = new StringFieldValidator().setRequired(true).setRestricted(false).setMinCharacters(Configuration.MIN_PASSWORD).setMaxCharacters(40).setRequired(false).setValidRegex(null).setValidPattern(StringFieldValidator.PATTER_REGEX_PASSWORD);

    //Address

    public static final StringFieldValidator DEPARTMENT = new StringFieldValidator().setRequired(true);
    public static final StringFieldValidator PROVINCE = new StringFieldValidator().setRequired(true);
    public static final StringFieldValidator DISTRICT = new StringFieldValidator().setRequired(true);
    public static final IntegerFieldValidator STREET_TYPE_ID = new IntegerFieldValidator().setRequired(true);
    public static final StringFieldValidator STREET_TYPE = new StringFieldValidator().setRequired(true);
    public static final StringFieldValidator STREET_NAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(150);
    public static final StringFieldValidator STREET_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
    public static final StringFieldValidator INTERIOR_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_FULLADDRESS);
    public static final StringFieldValidator INTERIOR = new StringFieldValidator().setRequired(false).setRestricted(true).setMaxCharacters(10);
    public static final StringFieldValidator DETAIL = new StringFieldValidator().setRequired(false).setRestricted(true).setMaxCharacters(25);
    public static final StringFieldValidator FULL_ADDRESS = new StringFieldValidator().setRequired(true).setRestricted(false).setMaxCharacters(150).setMinCharacters(5).setValidRegex(StringFieldValidator.REGEXP_FULLADDRESS);
    public static final DoubleFieldValidator LATITUDE = new DoubleFieldValidator().setRequired(true);
    public static final DoubleFieldValidator LONGITUDE = new DoubleFieldValidator().setRequired(true);
    public static final StringFieldValidator DEPARTMENT_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(5).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS);
    public static final StringFieldValidator MANZANA = new StringFieldValidator().setRequired(false).setRestricted(true).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
    public static final StringFieldValidator LOTE = new StringFieldValidator().setRequired(false).setRestricted(true).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
    public static final BooleanFieldValidator BANBIF_TYCS = new BooleanFieldValidator().setRequired(false);
    public static final CharFieldValidator ADDRESS_TO_SEND = new CharFieldValidator().setRequired(false).setValidValues(new Character[]{'H', 'J'});

    //Ocupational Information
    public static final StringFieldValidator PHONE_TYPE = new StringFieldValidator().setRequired(true).setMaxCharacters(1).setMinCharacters(1).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
    public static final IntegerFieldValidator ACTIVITY_TYPE_ID = new IntegerFieldValidator().setRequired(true);
    public static final IntegerFieldValidator OCUPATION_ID = new IntegerFieldValidator().setRequired(true);
    public static final StringFieldValidator SECTOR_ID = new StringFieldValidator().setRequired(true);
    public static final StringFieldValidator RUC = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(11).setMinCharacters(11).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setValidPattern(StringFieldValidator.PATTER_REGEX_RUC);
    public static final StringFieldValidator CUIT = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(11).setMinCharacters(11).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setValidPattern(StringFieldValidator.PATTER_REGEX_CDI_CUIT_CUIL);
    public static final StringFieldValidator CUIT_COMPANY = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(11).setMinCharacters(11).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setValidPattern(StringFieldValidator.PATTER_REGEX_CUIT_COMPANY);
    public static final StringFieldValidator NIT = new StringFieldValidator().setRequired(true).setRestricted(true).setMinCharacters(8).setMaxCharacters(15).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator COMPANY_NAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(25);
    public static final StringFieldValidator CIIU = new StringFieldValidator().setRequired(true).setRestricted(true).setMinCharacters(4).setMaxCharacters(5).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator ACTIVITY_TIME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(3).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final IntegerFieldValidator GROSS_INCOME = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(999999).setMinValue(100);
    public static final DoubleFieldValidator GROSS_INCOME_DOUBLE = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMaxValue(999999.9).setMinValue(100.0);
    public static final IntegerFieldValidator GROSS_INCOME_COLOMBIA = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(99_000_000).setMinValue(500_000);
    public static final IntegerFieldValidator NET_INCOME = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(99999).setMinValue(500);
    public static final StringFieldValidator REGIME = new StringFieldValidator().setMinCharacters(2).setRequired(true);
    public static final IntegerFieldValidator VOUCHER_TYPE_ID = new IntegerFieldValidator().setRequired(true);
    public static final IntegerFieldValidator RENTIER_BELONGING = new IntegerFieldValidator().setRequired(true);
    public static final DoubleFieldValidator SHAREHOLDING = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMaxValue(100.00).setMinValue(0.01);
    public static final IntegerFieldValidator SHAREHOLDING_INT = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(100).setMinValue(1);
    public static final IntegerFieldValidator RESULT_U12M = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(9999999).setMinValue(1000);
    public static final IntegerFieldValidator PENSIONER_FROM_ID = new IntegerFieldValidator().setRequired(true);
    public static final StringFieldValidator HOUSEKEEPER_EMPLOYER = new StringFieldValidator().setRequired(true).setMaxCharacters(35).setMinCharacters(5).setValidRegex(StringFieldValidator.REGEXP_FULLNAMES);
    public static final IntegerFieldValidator OTHER_INCOME = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(999999).setMinValue(100);
    public static final StringFieldValidator OCUPATIONAL_ADDRESS = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(80);
    public static final StringFieldValidator OCUPATION_START_DATE = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_MMYYYY_SLASH_DATE);
    public static final DoubleFieldValidator OCUPATION_LAST_YEAR_SELLINGS = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMaxValue(99999999.0).setMinValue(0.0);
    public static final StringFieldValidator HOPE_GROW = new StringFieldValidator().setRequired(true).setRestricted(true);
    public static final IntegerFieldValidator OCUPATION_SALES_PERCENTAGE = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(100).setMinValue(1);
    public static final StringFieldValidator OCUPATION_CLIENTS_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true);
    public static final DoubleFieldValidator OCUPATION_DAILY_INCOME = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMaxValue(99999999.9).setMinValue(0.0);
    public static final DoubleFieldValidator OCUPATION_U12M_INCOME = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMaxValue(9999999.9).setMinValue(0.0);

    //Bank
    public static final IntegerFieldValidator BANK_ID = new IntegerFieldValidator().setRequired(true);
    public static final StringFieldValidator BANK_NAME = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(80).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS);
    public static final CharFieldValidator BANK_ACCOUNT_TYPE = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'C', 'S'});
    public static final StringFieldValidator BANK_ACCOUNT_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(20).setMinCharacters(7).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator BANK_CCI_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(20).setMinCharacters(20).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator BANK_CBU_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(22).setMinCharacters(22).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator BANK_SUBSIDIARI_NUMBER = new StringFieldValidator().setMaxCharacters(4).setMinCharacters(4).setRequired(true).setRestricted(true);

    //Loan Application
    public static IntegerFieldValidator LOANAPPLICATION_AMMOUNT_SHORT_TERM;
    public static IntegerFieldValidator LOANAPPLICATION_AMMOUNT_TRADITIONAL;
    public static IntegerFieldValidator LOANAPPLICATION_AMMOUNT_SALARY_ADVANCE;
    public static final IntegerFieldValidator LOANAPPLICATION_INSTALLMENTS = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMinValue(3).setMaxValue(Configuration.MAX_INSTALLMENTS);
    public static IntegerFieldValidator LOANAPPLICATION_INSTALLMENTS_SHORT_TERM;
    public static final IntegerFieldValidator LOANAPPLICATION_DAYS = new IntegerFieldValidator().setRequired(true).setMinValue(3).setMaxValue(30);
    public static final DoubleFieldValidator SALARYADVANCE_COMMISSION = new DoubleFieldValidator().setRequired(true);
    public static final IntegerFieldValidator LOANAPPLICATION_CLUSTER = new IntegerFieldValidator().setRequired(true);


    public static final IntegerFieldValidator LOANAPPLICATION_MOOD = new IntegerFieldValidator().setRequired(true).setMinValue(1).setMaxValue(4);

    //Loan Offer
    public static final IntegerFieldValidator ENTITY_ID = new IntegerFieldValidator().setRequired(true);
    public static final CharFieldValidator BANBIF_LOAN_APPLICATION_TYPE = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'O', 'T', 'R'});

    //Credit
    public static final StringFieldValidator FIRST_DUE_DATE = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE);

    //Disbursement
    public static final StringFieldValidator DISBURSEMENT_DATE = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE);
    public static final CharFieldValidator PAYMENT_TYPE = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'C', 'T'});
    public static final StringFieldValidator PAYMENT_CHECK_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final IntegerFieldValidator PAYMENT_SIGNATURE_SYSUSER_ID = new IntegerFieldValidator().setRequired(true);

    //Messaging
    public static final StringFieldValidator EMAIL_SUBJECT = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(70);
    public static final StringFieldValidator EMAIL_MESSAGE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(500);


    // Signature
    public static final StringFieldValidator LOANAPPLICATION_SIGNATURE = new StringFieldValidator().setRequired(true).setRestricted(false).setMaxCharacters(35).setMinCharacters(5).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC_SPEC_CHARS);

    // Debt Consolidations
    public static final IntegerFieldValidator CONSOLIDABLE_ACCOUNT_TYPE = new IntegerFieldValidator().setRequired(true);
    public static final StringFieldValidator RCC_ENTITY_CODE = new StringFieldValidator().setRequired(true).setRestricted(true).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final IntegerFieldValidator CONSOLIDABLE_DEBT_BALANCE = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(999999).setMinValue(10);
    public static final DoubleFieldValidator CONSOLIDABLE_DEBT_RATE = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMaxValue(200.0).setMinPercentageValue(1.0);
    public static final IntegerFieldValidator CONSOLIDABLE_DEBT_INSTALLMENTS = new IntegerFieldValidator().setRequired(true).setMinValue(0).setMaxValue(Configuration.MAX_INSTALLMENTS);

    // Employee
    public static final CharFieldValidator CONTRACT_TYPE = new CharFieldValidator().setRequired(true).setValidValues(new Character[]{'I', 'D'});
    public static final StringFieldValidator CONTRACT_END_DATE = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE);
    public static final DateFieldValidator EMPLOYEE_START_DATE = new DateFieldValidator().setRequired(true).setValidRegex(DateFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE);
    public static final StringFieldValidator SMS_PIN_SIGNATURE = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(6).setMinCharacters(6).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setRequiredErrorMsg("validation.process.smsToken");

    // Self evaluation
    public static final IntegerFieldValidator SELF_EVALUATION_AMOUNT = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(999999).setMinValue(100);
    public static final IntegerFieldValidator SELF_EVALUATION_INITIAL_AMOUNT = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(999999).setMinValue(1000);
    public static final IntegerFieldValidator SELF_EVALUATION_INSTALLMENTS = new IntegerFieldValidator().setRestricted(true).setRequired(true).setMinValue(3).setMaxValue(Configuration.MAX_INSTALLMENTS);
    public static final IntegerFieldValidator SELF_EVALUATION_FIXED_GROSS_INCOME = new IntegerFieldValidator().setRestricted(true).setRequired(true).setMinValue(100).setMaxValue(99999);

    public static final StringFieldValidator CREDIT_CARD_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(16).setMinCharacters(13).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);
    public static final StringFieldValidator LOAN_NUMBER = new StringFieldValidator().setRequired(true).setRestricted(true).setMaxCharacters(30).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS);

    public static final StringFieldValidator VEHICLE_PLATE = new StringFieldValidator().setRequired(true).setRestricted(true).setMinCharacters(6).setMaxCharacters(6).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC).setMinCharactersErrorMsg("validation.string.plate.minCharacters").setMaxCharactersErrorMsg("validation.string.plate.maxCharacters");

    public static final IntegerFieldValidator PENSION_BENEFIT = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(99999).setMinValue(100);
    public static final IntegerFieldValidator BIRTHDATE_DAY = new IntegerFieldValidator().setRequired(true).setMinValue(1).setMaxValue(31).setRequired(true);
    public static final IntegerFieldValidator BIRTHDATE_MONTH = new IntegerFieldValidator().setRequired(true).setMinValue(1).setMaxValue(12).setRequired(true);
    public static final IntegerFieldValidator BIRTHDATE_YEAR = new IntegerFieldValidator().setRequired(true).setMinValue(currentYear - 100).setMaxValue(currentYear - 18).setRequired(true);
    public static final IntegerFieldValidator RETIREMENT_YEAR = new IntegerFieldValidator().setRequired(true).setMinValue(currentYear - 50).setMaxValue(currentYear).setRequired(true);


    /**
     * Method called once, in the application init.
     * Configures the validator that needs the catalog for its parameters
     *
     * @param catalog
     * @throws Exception
     */
    public static void configure(CatalogService catalog) throws Exception {
        //TODO CONFIGURE MORE VALIDATORS!
        Product traditional = catalog.getCatalogById(Product.class, Product.TRADITIONAL, Configuration.getDefaultLocale());
        Product salaryAdvance = catalog.getCatalogById(Product.class, Product.SALARY_ADVANCE, Configuration.getDefaultLocale());


        LOANAPPLICATION_AMMOUNT_TRADITIONAL = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(traditional.getProductMaxMinParameters().get(0).getMaxAmount()).setMinValue(traditional.getProductMaxMinParameters().get(0).getMinAmount());
        LOANAPPLICATION_AMMOUNT_SALARY_ADVANCE = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(salaryAdvance.getProductMaxMinParameters().get(0).getMaxAmount()).setMinValue(salaryAdvance.getProductMaxMinParameters().get(0).getMinAmount());


    }
}