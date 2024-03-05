/**
 *
 */
package com.affirm.common.service;

import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.Holiday;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.impl.UtilServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * @author jrodriguez
 */
public interface UtilService {


    String integerToDoubleFormat(Integer value);

    String doubleToIntegerFormat(Double value);

    String customDoubleFormat(Double value, int decimals);

    String doubleFormat(Double value);

    String doubleMoneyFormat(Double value);

    String doubleMoneyFormat(Double value, String currency);

    String doubleMoneyFormat(Integer value);

    String integerMoneyFormat(Double value);

    String integerMoneyFormat(Integer value);


    String integerMoneyFormat(Integer value, String currency);

    String integerOnlyMoney(Integer value, Currency currency);

    String integerOnlyMoney(Double value);

    String integerOnlyMoney(Integer value);

    String doubleMoneyFormat(Double value, Currency currency);

    String doubleOnlyMoneyFormat(Double value, Currency currency);

    String integerMoneyFormat(Double value, String countryCode, Character separator, boolean showCurrencySymbol);

    String integerOnlyMoney(Double value, Character separator);

    String integerMoneyFormat(Integer value, String countryCode, boolean showCurrencySymbol);

    String integerMoneyFormat(Integer value, Currency currency);

    String percentFormat(Double value, Currency currency);

    String percentFormat(Double value);

    String percentFormat(Double value, String countryCode);

    String percentValueFormat(Double value, Currency currency);

    String datetimeShortFormat(Date value);

    String dateFormat(Date value);

    String hourFormat(Date value);

    String startDateFormat(Date value);

    Integer daysOffCurrentMonth(Integer value);

    String dateUntilNowFormat(Date value);

    String maskCellphone(String match);

    String maskBankAccount(String match, Integer range, char mask);

    String dateCustomFormat(Date value, String pattern, Locale locale);

    Date parseDate(String value, String pattern, Locale locale) throws Exception;

    String localDateFormat(LocalDate value, String pattern, Locale locale);

    String humanDateFormat(Date value, Locale locale);

    String humanMonthFormat(Date value, Locale locale);

    String encodeUrlParam(String param) throws Exception;

    String createLoanApplicationClientUrl(Integer userId, Integer loanId, Integer personId) throws Exception;

    Integer getFieldFromLoanApplicationToken(String field, String loanApplicationToken) throws Exception;

    String toJson(Object obj);

    String encodeFileToBase64Binary(File file);

    JSONObject parseHttpRequestAsJson(HttpServletRequest request);

    void processGoogleGeodecoding(Double latitude, Double longitude, String address, com.affirm.common.util.Service<String, String> function) throws Exception;

    Double stringToDouble(String value);

    Integer stringToInteger(String value);

    void processGoogleReverseGeodecoding(Double latitude, Double longitude, String address, com.affirm.common.util.Service<String, Double> function) throws Exception;

    JSONObject getGoogleGeodecoding(Double latitude, Double longitude, String address) throws Exception;

    boolean isIpFromCountry(String ip, String countryCode);

    String getCountryFromIp(String ip);

    JSONObject getIpGeolocation(String ip, UtilServiceImpl.GeoIpServices ipService);

    String getStringFromUrl(String url) throws Exception;

    String getStringFromUrl(String url, String authorization) throws Exception;

    boolean isWebhookRequest(HttpServletRequest request);

    boolean isBlogRequest(HttpServletRequest request);

    boolean isApiRestRequest(HttpServletRequest request);

    String getDomainOfRequest(HttpServletRequest request);

    boolean isInCorrectCountry(HttpServletRequest request);

    String getRedirectUrl();

    Date addDays(Date date, int days);

    Date addHours(Date date, int hours);

    Date addMonths(Date date, int months);

    String integerMoneyFormat(Integer value, String currencySymbol, String separator);

    String doubleMoneyFormat(Double value, String currency, String separator);

    String dividePercentage(String a, String b);

    String dividePercentageInverse(String a, String b);

    String divideMoney(String a, String b);

    String divide(String a, String b);

    String integerMoneyFormat(Double value, String currencySymbol, String separator);

    boolean validarCuit(String cuit);

    boolean validarNit(String nit);

    String datetimeShortFormatByCountry(Date value, int countryId);

    Integer roundUpto50(Double value);

    Integer roundDownto100(Double value);

    double convertMoney(Double value, double rate);

    String percentFormat(Integer value);

    String percentajeSave(Integer total, Integer offer);

    String percentFormat1Digit(Double value);

    String percentFormat1Digit(Double value, String countryCode);

    Integer getDefaultValueField(JSONArray arr, String key, Integer countryId);

    String getDateFormatFromNumericValue(String value);

    int yearsBetween(Date first, Date last);

    long daysBetween(Date first, Date last);

    String getRandomName();

    String qrImageUrl(String keyId, String secret);

    Date getAvailableDate(List<Holiday> holidays, Integer contactResultId, String date) throws Exception;

    String getEntitiesFunnelByIds(Object entitiesId);

    String getProductsFunnelByIds(Object productsId);

    String getStatusesFunnelByIds(Object statusId);

    String getDateTypeFunnel(Object dateTypeId);

    Map<String, String> getAssistedProcessScheduleHours();

    String getAssistedProcessScheduleRange(Date assistedProcessSchedule);

    String threeDigitPercentFormat(Double value, Currency currency);

    String datetimeYearFirstFormat(Date value);

    boolean isEfectivoAlToqueBrand(HttpServletRequest request);

    Double round(Double value, int places);

    String getEntityFunnelById(Integer entityId);

    String getCountriesFunnelByIds(Object entitiesId);

    String getDisbursementTypesFunnelByIds(Object entitiesId) throws Exception;

    double teaToTna(double tea);

    double tnaToTea(double tna);

    Double getUsdPenExchangeRate();

    int getInstallments(LoanApplication loanApplication) throws Exception;

    String getHyphenatedPhoneNumber(String phoneNumber) throws Exception;

    String getRandomABTesting(int loanApplicationId);

    String datetimeCustomFormat(Date value, String format);

    String doubleMoneyFormatWithoutSpaces(Double value);
    
    String getTextWithoutValues(String text, List<String> search);

    String getTextWithoutSpecialCharacters(String text);

    Boolean validateUrl(String url);

    String integerFormatWithoutSpaces(Integer value);
    
    String doubleMoneyFormatWithoutSpaces(Double value, String currency);

    String integerMoneyFormatWithoutSpaces(Double value, String currency);

    String getWordLetter(String value, int start, int end);
    
    String doubleMoneyFormatWithoutSpaces(Double value, String currency, Character groupingSeparator);

    <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map);

    String customDoubleFormatWithoutRounding(Double value, int decimals);

    JSONObject getIpGeolocationMaxMind(int loanApplicationId, String ip, UtilServiceImpl.GeoIpServices ipService);
}
