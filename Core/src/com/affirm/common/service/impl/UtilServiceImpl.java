/**
 *
 */
package com.affirm.common.service.impl;

import com.affirm.common.dao.ExternalWSRecordDAO;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.service.*;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.common.net.InternetDomainName;
import com.google.gson.Gson;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author jrodriguez
 */

@Service("utilService")
public class UtilServiceImpl implements UtilService {

    public final int START_HOUR = 9;
    public final int START_MINUTE = 0;
    public final int END_HOUR = 18;
    public final int END_MINUTE = 30;
    public final int SATURDAY = 6;
    public final int END_HOUR_SATURDAY = 13;
    public final int END_MINUTE_SATURDAY = 0;
    public final int SCHEDULE_HOURS_RANGE = 3;

    private static Logger logger = Logger.getLogger(UtilServiceImpl.class);

    public enum GeoIpServices {
        MAXMIND("https://geoip.maxmind.com/geoip/v2.1/country/{ip}"),
        MAXMIND_CITY("https://geoip.maxmind.com/geoip/v2.1/city/{ip}"),
        FREGEOIP("http://freegeoip.net/json/{ip}"),
        IPAPI("http://ip-api.com/json/{ip}");

        private String url;

        GeoIpServices(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;


    // ----- Double money formatters -----
    @Override
    public String integerToDoubleFormat(Integer value) {
        if (value == null) {
            return null;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        //symbols.setGroupingSeparator(' ');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @Override
    public String doubleToIntegerFormat(Double value) {
        if (value == null) {
            return null;
        }
        double dbl = value;
        return String.valueOf((int) dbl);
    }

    @Override
    public String customDoubleFormat(Double value, int decimals) {
        if (value == null) {
            return null;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
//        symbols.setGroupingSeparator(' ');
        formatter.applyPattern("########0." + StringUtils.repeat("0", decimals));
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @Override
    public String customDoubleFormatWithoutRounding(Double value, int decimals) {
        if (value == null) {
            return null;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setRoundingMode(RoundingMode.DOWN);
        formatter.applyPattern("########0." + StringUtils.repeat("0", decimals));
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @Override
    public String doubleFormat(Double value) {
        if (value == null) {
            return null;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
//        symbols.setGroupingSeparator(' ');
        formatter.applyPattern("########0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @Override
    public String doubleMoneyFormat(Double value) {
        if (value == null) {
            value = 0.0;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return "S/ " + formatter.format(value);
    }

    @Override
    public String doubleMoneyFormat(Integer value) {
        if (value == null) {
            return null;
        }
        return doubleMoneyFormat((double) value);
    }

    @Override
    public String doubleMoneyFormat(Double value, String currency) {
        if (value == null) {
            return null;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return currency != null ? currency + " " + formatter.format(value) : formatter.format(value);
    }

    @Override
    public String doubleMoneyFormat(Double value, Currency currency) {
        if (value == null)
            value = 0.0;
        if (currency == null)
            return doubleMoneyFormat(value, (String) null);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        symbols.setDecimalSeparator(currency.getDecimalSeparator().charAt(0));
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return currency.getSymbol() + " " + formatter.format(value);
    }

    @Override
    public String doubleOnlyMoneyFormat(Double value, Currency currency) {
        if (value == null)
            value = 0.0;
        if (currency == null)
            return doubleMoneyFormat(value, (String) null);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        symbols.setDecimalSeparator(currency.getDecimalSeparator().charAt(0));
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @Override
    public String doubleMoneyFormat(Double value, String currency, String separator) {
        if (value == null)
            value = 0.0;
        if (separator == null)
            separator = " ";
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(separator.charAt(0));
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return currency != null ? currency + " " + formatter.format(value) : formatter.format(value);
    }

    // ----- Integer money formatters -----
    @Override
    public String integerMoneyFormat(Integer value, String countryCode, boolean showCurrencySymbol) {
        if (value == null) {
            return null;
        }

        String currencySymbol = "";
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        switch (countryCode) {
            case "PE":
                if (showCurrencySymbol)
                    currencySymbol = "S/ ";
                symbols.setGroupingSeparator(' ');
                break;
            case "AR":
                if (showCurrencySymbol)
                    currencySymbol = "$ ";
                symbols.setGroupingSeparator('.');
                break;
        }
        formatter.setDecimalFormatSymbols(symbols);
        formatter.applyPattern("###,###,##0");

        return currencySymbol + formatter.format(value);
    }

    @Override
    public String integerMoneyFormat(Double value, String countryCode, Character separator, boolean showCurrencySymbol) {
        if (value == null) {
            return null;
        }

        String currencySymbol = "";
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        switch (countryCode) {
            case "PE":
                if (showCurrencySymbol)
                    currencySymbol = "S/ ";
                symbols.setGroupingSeparator(separator);
                break;
            case "AR":
                if (showCurrencySymbol)
                    currencySymbol = "$ ";
                symbols.setGroupingSeparator(separator);
                break;
        }
        formatter.setDecimalFormatSymbols(symbols);
        formatter.applyPattern("###,###,##0");

        return currencySymbol + formatter.format(value);
    }

    @Override
    public String integerOnlyMoney(Integer value, Currency currency) {
        if (value == null) {
            return null;
        }
        if (currency == null)
            return integerMoneyFormat(value);

        String currencySymbol = currency.getSymbol();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        formatter.setDecimalFormatSymbols(symbols);
        formatter.applyPattern("###,###,##0");

        return formatter.format(value);
    }

    @Override
    public String integerOnlyMoney(Double value, Character separator) {
        if (value == null)
            return null;

        return integerMoneyFormat(value, "PE", separator, false);
    }

    @Override
    public String integerMoneyFormat(Integer value, String currencySymbol, String separator) {
        if (value == null) {
            return null;
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(separator.charAt(0));
        formatter.setDecimalFormatSymbols(symbols);
        formatter.applyPattern("###,###,##0");
        return currencySymbol + " " + formatter.format(value);
    }

    @Override
    public String integerMoneyFormat(Double value, String currencySymbol, String separator) {
        if (value != null)
            return integerMoneyFormat(value.intValue(), currencySymbol, separator);
        return null;
    }

    @Override
    public String integerMoneyFormat(Double value) {
        if (value == null)
            return null;
        // TODO Send the correct country code
        return integerMoneyFormat(value.intValue(), "PE", true);
    }

    @Override
    public String integerOnlyMoney(Double value) {
        if (value == null)
            return null;
        // TODO Send the correct country code
        return integerMoneyFormat(value.intValue(), "PE", false);
    }

    @Override
    public String integerOnlyMoney(Integer value) {
        // TODO Send the correct country code
        return integerMoneyFormat(value, "PE", false);
    }

    @Override
    public String integerMoneyFormat(Integer value) {
        // TODO Send the correct country code
        return integerMoneyFormat(value, "PE", true);
    }

    @Override
    public String integerMoneyFormat(Integer value, Currency currency) {
        if (value == null) {
            return null;
        }
        if (currency == null)
            return integerMoneyFormat(value);

        String currencySymbol = currency.getSymbol();
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        formatter.setDecimalFormatSymbols(symbols);
        formatter.applyPattern("###,###,##0");

        return currencySymbol + " " + formatter.format(value);
    }

    @Override
    public String integerMoneyFormat(Integer value, String currency) {
        if (value == null) {
            return null;
        }
        if (currency == null)
            return integerMoneyFormat(value);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        formatter.setDecimalFormatSymbols(symbols);
        formatter.applyPattern("###,###,##0");

        return currency != null ? currency + " " + formatter.format(value) : formatter.format(value);
    }

    @Override
    public String percentFormat(Double value, Currency currency) {
        if (value == null)
            return null;
        if (currency == null)
            return doubleMoneyFormat(value, (String) null);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        symbols.setDecimalSeparator(currency.getDecimalSeparator().charAt(0));
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value) + " %";
    }

    @Override
    public String percentValueFormat(Double value, Currency currency) {
        if (value == null)
            return null;
        if (currency == null)
            return doubleMoneyFormat(value, (String) null);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        symbols.setDecimalSeparator(currency.getDecimalSeparator().charAt(0));
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value);
    }

    @Override
    public String percentFormat(Double value) {
        return percentFormat(value, "PE");
    }

    @Override
    public String percentFormat(Integer value) {
        return value == null ? null : value.toString() + "%";
    }

    @Override
    public String percentFormat(Double value, String countryCode) {
        if (value == null) {
            return null;
        }
        if (countryCode == null)
            countryCode = "PE";
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        switch (countryCode) {
            case "PE":
                symbols.setGroupingSeparator(' ');
                symbols.setDecimalSeparator('.');
                break;
            case "AR":
                symbols.setGroupingSeparator('.');
                symbols.setDecimalSeparator(',');
                break;
        }
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value) + " %";
    }

    @Override
    public String percentFormat1Digit(Double value) {
        return percentFormat1Digit(value, "PE");
    }

    @Override
    public String percentFormat1Digit(Double value, String countryCode) {
        if (value == null) {
            return null;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        switch (countryCode) {
            case "PE":
                symbols.setGroupingSeparator(' ');
                symbols.setDecimalSeparator('.');
                break;
            case "AR":
                symbols.setGroupingSeparator('.');
                symbols.setDecimalSeparator(',');
                break;
        }
        formatter.applyPattern("###,###,##0.0");
        formatter.setDecimalFormatSymbols(symbols);
        formatter.setRoundingMode(RoundingMode.FLOOR);
        return formatter.format(value) + " %";
    }

    @Override
    public String datetimeShortFormat(Date value) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_SHORT_DATE_FORMAT);
        return sdf.format(value);
    }

    @Override
    public String datetimeShortFormatByCountry(Date value, int countryId) {
        if (value == null) {
            return null;
        }

        TimeZone timeZone = TimeZone.getTimeZone(catalogService.getCountryParam(countryId).getTimezoneCode());
        SimpleDateFormat sdf = new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_SHORT_DATE_FORMAT);
        sdf.setTimeZone(timeZone);
        return sdf.format(value);
    }

    @Override
    public String startDateFormat(Date value) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(Configuration.START_DATE_FORMAT);
        return sdf.format(value);
    }

    @Override
    public String dateFormat(Date value) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_ONLY_DATE_FORMAT);
        return sdf.format(value);
    }

    @Override
    public String hourFormat(Date value) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_ONLY_HOUR_FORMAT);
        return sdf.format(value);
    }

    @Override
    public Integer daysOffCurrentMonth(Integer value) {
        if (value == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int daysOff = monthMaxDays - value;
        return daysOff;
    }

    @Override
    public String dateUntilNowFormat(Date value) {
        if (value == null) {
            return null;
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        final Long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
        final Long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        final Long hours = ChronoUnit.HOURS.between(startTime, endTime);
        final Long days = ChronoUnit.DAYS.between(startTime, endTime);
        final Long months = ChronoUnit.MONTHS.between(startTime, endTime);
        final Long years = ChronoUnit.YEARS.between(startTime, endTime);

        if (years > 0) {
            return years.toString() + "y";
        } else if (months > 0) {
            return months.toString() + "m";
        } else if (days > 0) {
            return days.toString() + "d";
        } else if (hours > 0) {
            return hours.toString() + "h";
        } else if (minutes > 0) {
            return minutes.toString() + "min";
        } else {
            return seconds.toString() + "seg";
        }
    }


    @Override
    public String maskCellphone(String match) {
        if (match == null || match.trim().isEmpty())
            return null;
        return match.substring(0, 6) + "XXX";
    }

    @Override
    public String maskBankAccount(String match, Integer range, char mask) {
        return StringUtils.repeat(mask, match.length() - range) + match.substring(match.length() - range);
    }


    @Override
    public String dateCustomFormat(Date value, String pattern, Locale locale) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return sdf.format(value);
    }

    @Override
    public Date parseDate(String value, String pattern, Locale locale) throws Exception {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return sdf.parse(value);
    }

    @Override
    public String localDateFormat(LocalDate value, String pattern, Locale locale) {
        if (value == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, locale);
        return formatter.format(value);
    }

    @Override
    public String humanDateFormat(Date value, Locale locale) {
        if (value == null) {
            return null;
        }
        //Locale esLocale = new Locale("es", "PE");
        SimpleDateFormat sdf = new SimpleDateFormat(com.affirm.system.configuration.Configuration.BACKOFFICE_FRONT_HUMAN_DATE, locale);
        return sdf.format(value);
    }

    @Override
    public String humanMonthFormat(Date value, Locale locale) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(Configuration.LONG_MONTH_YEAR_DATE_FORMAT, locale);
        return sdf.format(value);
    }

    @Override
    public String encodeUrlParam(String param) throws Exception {
        System.out.println("VOY A ENCODEAR ESTE PARAM: " + param);
        return URLEncoder.encode(param, "UTF-8");
    }

    @Override
    public String createLoanApplicationClientUrl(Integer userId, Integer loanId, Integer personId) throws Exception {
        if (userId == null || loanId == null || personId == null) {
            return null;
        }

        LoanApplication loanApplication = loanApplicationService.getLoanApplicationById(loanId);
        String countryDomain = loanApplication.getCountry().getDomains().get(0);
        if (countryDomain.contains("localhost"))
            countryDomain = countryDomain + ":8080";
        else if (Configuration.hostEnvIsLocal())
            countryDomain = "http://" + countryDomain + ":8080";
        else if (!Configuration.hostEnvIsProduction())
            countryDomain = "https://" + Configuration.getEnvironmmentName() + "." + countryDomain;
        else
            countryDomain = "https://" + countryDomain;

        if (loanApplication.getProductCategory().getId() == ProductCategory.ADELANTO)
            return countryDomain + "/salaryadvanceloanapplication/" + loanApplicationService.generateLoanApplicationToken(userId, personId, loanId);
        return countryDomain + "/loanapplication/" + loanApplicationService.generateLoanApplicationToken(userId, personId, loanId);
    }

    @Override
    public Integer getFieldFromLoanApplicationToken(String field, String loanApplicationToken) throws Exception {
        String decrypted = CryptoUtil.decrypt(loanApplicationToken);
        if (decrypted == null) {
            return null;
        }
        JSONObject jsonDecrypted = new JSONObject(decrypted);
        return jsonDecrypted.getInt(field);
    }

    @Override
    public String toJson(Object obj) {
        return new Gson().toJson(obj);
    }

    @Override
    public String encodeFileToBase64Binary(File file) {
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = encodeBase64(bytes).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedfile;
    }

    @Override
    public JSONObject parseHttpRequestAsJson(HttpServletRequest request) {
        JSONObject json = new JSONObject();
        JSONArray headers = new JSONArray();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                JSONObject header = new JSONObject();
                header.put(headerName, request.getHeader(headerName));
                headers.put(header);
            }
        }
        json.put("headers", headers);
        return json;
    }

    @Override
    public void processGoogleGeodecoding(Double latitude, Double longitude, String address, com.affirm.common.util.Service<String, String> function) throws Exception {
        Configuration.REVERSE_GEODECODING_EXECUTOR.execute(() -> {
            try {
                JSONObject json = getGoogleGeodecoding(latitude, longitude, address);
                if (json != null && json.getString("status").equals("OK")) {
                    function.executeService(json.getJSONArray("results").getJSONObject(0).getString("formatted_address"));
                }
            } catch (Exception ex) {
                ErrorServiceImpl.onErrorStatic(ex);
            }
        });
    }

    @Override
    public Double stringToDouble(String value) {
        try {
            return Double.parseDouble(value.replaceAll(",", "."));
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Integer stringToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void processGoogleReverseGeodecoding(Double latitude, Double longitude, String address, com.affirm.common.util.Service<String, Double> function) throws Exception {
        Configuration.REVERSE_GEODECODING_EXECUTOR.execute(() -> {
            try {
                JSONObject json = getGoogleGeodecoding(latitude, longitude, address);
                if (json != null && json.getString("status").equals("OK")) {
                    JSONObject geometry = json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    function.executeService(geometry.getDouble("lat"), geometry.getDouble("lng"));
                }
            } catch (Exception ex) {
                ErrorServiceImpl.onErrorStatic(ex);
            }
        });
    }

    @Override
    public JSONObject getGoogleGeodecoding(Double latitude, Double longitude, String address) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + Configuration.GOOGLE_API_KEY;
        if (latitude != null && longitude != null) {
            url = url + "&latlng=" + URLEncoder.encode(latitude + "", "UTF-8") + "," + URLEncoder.encode(longitude + "", "UTF-8");
        } else if (address != null) {
            url = url + "&address=" + URLEncoder.encode(address, "UTF-8");
        } else
            return null;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String res = response.toString();
        return new JSONObject(res);
    }

    @Override
    public boolean isIpFromCountry(String ip, String countryCode) {
        return getCountryFromIp(ip).equals(countryCode);
    }

    @Override
    public String getCountryFromIp(String ip) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                JSONObject jsonLocation;
                String countryCodeResult;
                long startTime = System.currentTimeMillis();
                if (Configuration.hostEnvIsProduction() || Configuration.hostEnvIsStage()) {
                    jsonLocation = getIpGeolocation(ip, GeoIpServices.MAXMIND);
                    countryCodeResult = JsonUtil.getStringFromJson(JsonUtil.getJsonObjectFromJson(jsonLocation, "country", new JSONObject()), "iso_code", "");

                } else {
                    jsonLocation = getIpGeolocation(ip, GeoIpServices.FREGEOIP);
                    countryCodeResult = JsonUtil.getStringFromJson(jsonLocation, "country_code", "");
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Total elapsed http request/response time in seconds for ip " + ip + " : " + (elapsedTime * 1.0 / 1000.0));

                return countryCodeResult;
            }
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            future.cancel(true); //this method will stop the running underlying task
            return "";
        }
    }

    @Override
    public JSONObject getIpGeolocation(String ip, GeoIpServices ipService) {
        /*try {
            if (ipService == null)
                ipService = GeoIpServices.IPAPI;

            if (ipService == GeoIpServices.MAXMIND)
                return new JSONObject(getStringFromUrl(ipService.getUrl().replace("{ip}", ip), Configuration.MAX_MIND_KEY));
            return new JSONObject(getStringFromUrl(ipService.getUrl().replace("{ip}", ip)));
        } catch (Exception ex) {
            logger.error("Error while gettin ip location from " + ipService.getUrl().replace("{ip}", ip));
        }*/
        return null;
    }

    @Override
    public JSONObject getIpGeolocationMaxMind(int loanApplicationId, String ip, GeoIpServices ipService) {
        String url = ipService.getUrl().replace("{ip}", ip);
        ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId,new Date(),null,url, ip,null,null);
        try {
            String result = getStringFromUrl(url, Configuration.MAX_MIND_KEY);
            externalWSRecord.setResponse(result);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
            return new JSONObject(result);
        } catch (Exception ex) {
            logger.error("Error while gettin ip location from " + ipService.getUrl().replace("{ip}", ip));
            errorService.onError(ex);
        }
        return null;
    }

    @Override
    public String getStringFromUrl(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    @Override
    public String getStringFromUrl(String url, String authorization) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestProperty("Authorization", authorization);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    @Override
    public boolean isWebhookRequest(HttpServletRequest request) {
        try {
            String[] paths;
            if (request.getPathInfo() != null) {
                paths = request.getPathInfo().split("/");
                if (paths.length > 1 && Configuration.WEBHOOK_PATH.equals(paths[1]))//for twillio, sendgrid, messenger, ...
                    return true;
            }
        } catch (Exception e) {
            logger.error("Error when identifying if it's a webhook: " + request.getRequestURL(), e);
        }
        return false;
    }

    @Override
    public boolean isBlogRequest(HttpServletRequest request) {
        try {
            String[] paths;
            if (request.getPathInfo() != null) {
                paths = request.getPathInfo().split("/");
                if (paths.length > 1 && "blog".equals(paths[1]))
                    return true;
            }
        } catch (Exception e) {
            logger.error("Error when identifying if it's a blog request: " + request.getRequestURL(), e);
        }
        return false;
    }

    @Override
    public boolean isApiRestRequest(HttpServletRequest request) {
        try {
            String[] paths;
            if (request.getPathInfo() != null) {
                paths = request.getPathInfo().split("/");
                if (paths.length > 1 && "apirest".equals(paths[1]))
                    return true;
            }
        } catch (Exception e) {
            logger.error("Error when identifying if it's a blog request: " + request.getRequestURL(), e);
        }
        return false;
    }

    @Override
    public String getDomainOfRequest(HttpServletRequest request) {
        try {
            logger.info("X-Forwarded-Host: " + request.getHeader("X-Forwarded-Host"));
            InternetDomainName requestInternetDomain;
            if (Configuration.hostEnvIsLocal()) {
                requestInternetDomain = InternetDomainName.from(request.getServerName());
            } else {
                requestInternetDomain = InternetDomainName.from(request.getHeader("X-Forwarded-Host"));
            }
            return requestInternetDomain.hasParent() ? requestInternetDomain.topPrivateDomain().toString() : request.getServerName();
        } catch (Exception ex) {
            if(Configuration.hostEnvIsProduction()){
                errorService.onError(ex);
            }
            else{
                logger.error("Error getDomainOfRequest", ex);
            }
            return "solven.pe";
        }
    }

    @Override
    public boolean isInCorrectCountry(HttpServletRequest request) {
        String countryCode = SecurityUtils.getSubject().getSession().getAttribute("ip_country_code").toString();

        CountryParam countryParamOfIp = catalogService.getCountryParamByCountryCode(countryCode);
        return countryParamOfIp != null && countryParamOfIp.getDomains().stream().anyMatch(s -> s.equals(getDomainOfRequest(request)));
    }

    @Override
    public String getRedirectUrl() {
        String countryCode = SecurityUtils.getSubject().getSession().getAttribute("ip_country_code").toString();
        CountryParam countryParam = catalogService.getCountryParamByCountryCode(countryCode);

        if (countryParam == null)
            return null;

        return (Configuration.hostEnvIsLocal() ? "http://" : "https://") + countryParam.getDomains().get(0) + (Configuration.hostEnvIsLocal() ? ":8080" : "");
    }

    @Override
    public Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    @Override
    public Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }

    @Override
    public Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    @Override
    public String dividePercentage(String a, String b) {
        if (a == null || a.equals("")) {
            a = "0.0";
        }

        if (b == null || b.equals("")) {
            b = "0.0";
        }

        a = a.replaceAll(" ", "");
        a = a.replaceAll(",", "");
        a = a.replaceAll("%", "");

        b = b.replaceAll(" ", "");
        b = b.replaceAll(",", "");
        b = b.replaceAll("%", "");


        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);

        Double aD = Double.valueOf(a);
        Double bD = Double.valueOf(b);
        Double value = 0.0;
        if (bD > 0.0) {
            value = 100.0 * aD / bD;
        }

        return formatter.format(value) + " %";
    }

    @Override
    public String divideMoney(String a, String b) {
        if (a == null || a.equals("")) {
            a = "0.0";
        }

        if (b == null || b.equals("")) {
            b = "0.0";
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);

        Double aD = Double.valueOf(a);
        Double bD = Double.valueOf(b);
        Double value = 0.0;
        if (bD > 0.0) {
            value = 100.0 * aD / bD;
        }

        return "S/ " + formatter.format(value);
    }

    @Override
    public String divide(String a, String b) {
        if (a == null || a.equals("")) {
            a = "0.0";
        }

        if (b == null || b.equals("")) {
            b = "0.0";
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);

        Double aD = Double.valueOf(a);
        Double bD = Double.valueOf(b);
        Double value = 0.0;
        if (bD > 0.0) {
            value = aD / bD;
        }

        return formatter.format(value);
    }

    @Override
    public String dividePercentageInverse(String a, String b) {
        if (a == null || a.equals("")) {
            a = "0.0";
        }

        if (b == null || b.equals("")) {
            b = "0.0";
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);

        Double aD = Double.valueOf(a);
        Double bD = Double.valueOf(b);
        Double value = 100.0;
        if (bD > 0.0) {
            value = 100.0 - 100.0 * aD / bD;
        }

        return formatter.format(value) + " %";
    }

    @Override
    public boolean validarCuit(String cuit) {
        //Eliminamos todos los caracteres que no son números
        cuit = cuit.replaceAll("[^\\d]", "");
        //Controlamos si son 11 números los que quedaron, si no es el caso, ya devuelve falso
        if (cuit.length() != 11) {
            return false;
        }
        //Convertimos la cadena que quedó en una matriz de caracteres
        char[] cuitArray = cuit.toCharArray();
        //Inicializamos una matriz por la cual se multiplicarán cada uno de los dígitos
        Integer[] serie = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        //Creamos una variable auxiliar donde guardaremos los resultados del cálculo del número validador
        Integer aux = 0;
        //Recorremos las matrices de forma simultanea, sumando los productos de la serie por el número en la misma posición
        for (int i = 0; i < 10; i++) {
            aux += Character.getNumericValue(cuitArray[i]) * serie[i];
        }
        //Hacemos como se especifica: 11 menos el resto de de la división de la suma de productos anterior por 11
        aux = 11 - (aux % 11);
        //Si el resultado anterior es 11 el código es 0
        if (aux == 11) {
            aux = 0;
        }
        //Si el resultado anterior es 10 el código no tiene que validar, cosa que de todas formas pasa
        //en la siguiente comparación.
        //Devuelve verdadero si son iguales, falso si no lo son
        //(Esta forma esta dada para prevenir errores, en java 6 se puede usar: return aux.equals(Character.getNumericValue(cuitArray[10]));)
        return Objects.equals(Character.getNumericValue(cuitArray[10]), aux);
    }

    @Override
    public boolean validarNit(String nit) {
//        https://es.wikipedia.org/wiki/N%C3%BAmero_de_Identificaci%C3%B3n_Tributaria
        nit = nit.replaceAll("[^\\d]", "");

        if (nit.length() != 10) {
            return true;// TODO there is no clear validation for NIT of less/more than 10 (w/ verification number)
        }

        List<Integer> splitAndMapInt = Arrays.stream(nit.split("")).map(Integer::parseInt).collect(Collectors.toList());
        int[] serie = {41,37,29,23,19,17,13,7,3};
        int verification = splitAndMapInt.remove(splitAndMapInt.size() - 1);

        // sadly java stream reduce is not the same as js one
        int sumAndMulti = 0;
        for (int i = 0; i < splitAndMapInt.size(); i++) {
            sumAndMulti += (serie[i] * splitAndMapInt.get(i));
        }

        sumAndMulti = sumAndMulti % 11;
        sumAndMulti = sumAndMulti >= 2 ? (11 - sumAndMulti) : sumAndMulti;

        return sumAndMulti == verification;
    }

    @Override
    public Integer roundUpto50(Double value) {
        return Math.toIntExact(Math.round(value / 50f) * 50);
    }

    @Override
    public Integer roundDownto100(Double value) {
        return Math.toIntExact((long) Math.floor(value / 100f) * 100);
    }

    @Override
    public double convertMoney(Double value, double rate) {
        return value * rate;

    }

    @Override
    public String percentajeSave(Integer total, Integer offer) {
        Double save = 100 - offer * 1.0 * 100 / total;
        String roundedValue = this.percentFormat1Digit(save);
        return "-" + roundedValue;
    }

    @Override
    public Integer getDefaultValueField(JSONArray arr, String key, Integer countryId) {
        for (int i = 0; i < arr.length(); i++) {
            Integer country = JsonUtil.getIntFromJson(arr.getJSONObject(i), "country_id", null);
            if (country.equals(countryId)) {
                switch (key) {
                    case "income":
                        return JsonUtil.getIntFromJson(arr.getJSONObject(i), "income", null);
                    case "employment_time":
                        return JsonUtil.getIntFromJson(arr.getJSONObject(i), "employment_time", null);
                }
            }
        }
        return null;
    }

    @Override
    public String getDateFormatFromNumericValue(String value) {
        if (value != null) {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = null;
            try {
                date = originalFormat.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formatedDate = newFormat.format(date);

            return formatedDate;
        } else {
            return null;
        }
    }

    @Override
    public int yearsBetween(Date first, Date last) {
        if (first != null && last != null) {
            Calendar a = Calendar.getInstance();
            a.setTime(first);
            Calendar b = Calendar.getInstance();
            b.setTime(last);
            int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
            if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) ||
                    (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
                diff--;
            }
            return diff;
        }
        return 0;
    }

    @Override
    public long daysBetween(Date first, Date last) {
        if (first != null && last != null) {
            long startTime = DateUtils.truncate(first, Calendar.DATE).getTime();
            long endTime = DateUtils.truncate(last, Calendar.DATE).getTime();
            long diffTime = endTime - startTime;
            return diffTime / (1000 * 60 * 60 * 24);
        }
        return 0;
    }

    @Override
    public String getRandomName() {
        return RandomStringUtils.random(10, true, false);
    }

    @Override
    public String qrImageUrl(String keyId, String secret) {
        StringBuilder sb = new StringBuilder(128);
        sb.append("https://chart.googleapis.com/chart");
        sb.append("?chs=200x200&cht=qr&chl=200x200&chld=M|0&cht=qr&chl=");
        sb.append("otpauth://totp/").append(keyId).append("%3Fsecret%3D").append(secret);
        return sb.toString();
    }

    private boolean isWeekend(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private boolean isHoliday(List<Holiday> holidays, Calendar calendar) {
        for (Holiday holiday : holidays) {
            Calendar holCal = Calendar.getInstance();
            holCal.setTime(holiday.getDate());
            if (holCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    holCal.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR))
                return true;
        }
        return false;
    }

    private boolean isWithinSchedule(Calendar now) {
        if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            return now.get(Calendar.HOUR_OF_DAY) <= END_HOUR_SATURDAY && now.get(Calendar.HOUR_OF_DAY) >= START_HOUR;
        else
            return now.get(Calendar.HOUR_OF_DAY) <= END_HOUR && now.get(Calendar.HOUR_OF_DAY) >= START_HOUR;
    }

    @Override
    public Date getAvailableDate(List<Holiday> holidays, Integer contactResultId, String date) throws Exception {

        Date finalDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(finalDate);

        if (contactResultId.equals(TrackingAction.BUSY) || contactResultId.equals(TrackingAction.NO_SE_ENCUENTRA) ||
                catalogService.getTrackingActionsByType(TrackingAction.CATEGORY_NO_CONTACT).stream().anyMatch(tracking -> tracking.getTrackingActionId().equals(contactResultId))
        ) finalDate = addHours(finalDate, 1);
        else if (contactResultId.equals(TrackingAction.INTERESTED)) finalDate = addHours(finalDate, 48);
        else finalDate = date != null && !date.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy H:mm").parse(date) : null;
//        final date is null. do not schedule
        if (finalDate == null) {
            return null;
        }

        if (finalDate == null)
            return null;

        calendar.setTime(finalDate);
        if (!isWithinSchedule(calendar)) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                if (calendar.get(Calendar.HOUR_OF_DAY) > END_HOUR_SATURDAY)
                    calendar.add(Calendar.DAY_OF_WEEK, 2);
            } else {
                if (calendar.get(Calendar.HOUR_OF_DAY) > END_HOUR_SATURDAY)
                    calendar.add(Calendar.DAY_OF_WEEK, 1);
            }
            calendar.set(Calendar.HOUR_OF_DAY, START_HOUR);
            calendar.set(Calendar.MINUTE, START_MINUTE);
            finalDate = calendar.getTime();
            calendar.setTime(finalDate);
        }

        if (isWeekend(calendar) || isHoliday(holidays, calendar)) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
                calendar.add(Calendar.DAY_OF_WEEK, 2);
            else
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            calendar.set(Calendar.HOUR_OF_DAY, START_HOUR);
            calendar.set(Calendar.MINUTE, START_MINUTE);
            finalDate = calendar.getTime();
        }
        return finalDate;
    }

    @Override
    public String getEntitiesFunnelByIds(Object entitiesId) {
        String entitiesConcated = "";
        String[] entitiesArray = entitiesId.toString().split("\\[");
        String[] entitiesArray2 = entitiesArray[1].split("\\]");
        if (entitiesArray2.length > 0) {
            String[] entities = entitiesArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < entities.length; i++) {
                entitiesConcated = entitiesConcated + catalogService.getEntity(Integer.parseInt(entities[i])).getShortName();
                if (i < entities.length - 1)
                    entitiesConcated = entitiesConcated + separator + " ";
            }
        }
        return entitiesConcated;
    }

    @Override
    public String getProductsFunnelByIds(Object productsId) {
        String productsConcated = "";
        String[] productsArray = productsId.toString().split("\\[");
        String[] productsArray2 = productsArray[1].split("\\]");
        if (productsArray2.length > 0) {
            String[] products = productsArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < products.length; i++) {
                Product product = catalogService.getProduct(Integer.parseInt(products[i]));
                productsConcated += product == null ? "Sin producto" : product.getName();
                if (i < products.length - 1)
                    productsConcated += separator + " ";
            }
        }
        return productsConcated;
    }

    @Override
    public String getStatusesFunnelByIds(Object statusId) {
        String statusesConcated = "";
        String[] statusesArray = statusId.toString().split("\\[");
        String[] statusesArray2 = statusesArray[1].split("\\]");
        if (statusesArray2.length > 0) {
            String[] statuses = statusesArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < statuses.length; i++) {
                LoanApplicationStatus status = catalogService.getLoanApplicationStatus(Configuration.getDefaultLocale(), Integer.parseInt(statuses[i]));
                statusesConcated += status == null ? "Sin estado" : status.getStatus();
                if (i < statuses.length - 1)
                    statusesConcated += separator + " ";
            }
        }
        return statusesConcated;
    }

    @Override
    public String getDateTypeFunnel(Object dateTypeId) {
        if ((Integer) dateTypeId == 1) {
            return ReportsServiceImpl.FUNNEL_REPORT_DATETYPE_1;
        }
        if ((Integer) dateTypeId == 2) {
            return ReportsServiceImpl.FUNNEL_REPORT_DATETYPE_2;
        }
        return "";
    }

    @Override
    public Map<String, String> getAssistedProcessScheduleHours() {
        Map<String, String> assistedProcess = new LinkedHashMap<>();
        int sumHours = START_HOUR;

        while (sumHours < END_HOUR) {
            String startHour = String.format("%s:00", sumHours);
            sumHours += SCHEDULE_HOURS_RANGE;
            String endHour = String.format("%s:00", sumHours);
            assistedProcess.put(startHour, String.format("%s a %shs", startHour, endHour));
        }

        return assistedProcess;
    }

    @Override
    public String getAssistedProcessScheduleRange(Date assistedProcessSchedule) {
        if(assistedProcessSchedule == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(assistedProcessSchedule);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return getAssistedProcessScheduleHours().get(String.format("%s:00", hour));
    }

    @Override
    public String threeDigitPercentFormat(Double value, Currency currency) {
        if (value == null)
            return null;
        if (currency == null)
            return doubleMoneyFormat(value, (String) null);

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(currency.getSeparator().charAt(0));
        symbols.setDecimalSeparator(currency.getDecimalSeparator().charAt(0));
        formatter.applyPattern("###,###,##0.000");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value) + " %";
    }

    @Override
    public String datetimeYearFirstFormat(Date value) {

        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(Configuration.BACKOFFICE_FRONT_YEAR_FIRST_DATE_FORMAT);
        return sdf.format(value);

    }

    @Override
    public boolean isEfectivoAlToqueBrand(HttpServletRequest request) {
        return productService.getProductDomainByRequest(request) != null && productService.getProductDomainByRequest(request).getProductId() == Product.LEADS;
    }

    @Override
    public Double round(Double value, int places) {
        if (value == null)
            return null;

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public String getEntityFunnelById(Integer entityId) {
        if (entityId == 0) {
            return "Solo Marketplace";
        }

        Entity entity = catalogService.getEntity(entityId);

        return entity.getShortName();
    }

    @Override
    public String getCountriesFunnelByIds(Object couintriesId) {
        String countriesConcated = "";
        String[] entitiesArray = couintriesId.toString().split("\\[");
        String[] entitiesArray2 = entitiesArray[1].split("\\]");
        if (entitiesArray2.length > 0) {
            String[] countries = entitiesArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < countries.length; i++) {
                countriesConcated = countriesConcated + catalogService.getCountryParam(Integer.parseInt(countries[i])).getName();
                if (i < countries.length - 1)
                    countriesConcated = countriesConcated + separator + " ";
            }
        }
        return countriesConcated;
    }

    @Override
    public String getDisbursementTypesFunnelByIds(Object disbursementTypesId) throws Exception {
        String disbursementTypesConcated = "";
        String[] entitiesArray = disbursementTypesId.toString().split("\\[");
        String[] entitiesArray2 = entitiesArray[1].split("\\]");
        if (entitiesArray2.length > 0) {
            String[] disbursementTypes = entitiesArray2[0].split(",");
            String separator = ",";
            for (int i = 0; i < disbursementTypes.length; i++) {
                disbursementTypesConcated = disbursementTypesConcated + catalogService.getDisbursementTypeById(Integer.parseInt(disbursementTypes[i])).getDisbursementType();
                if (i < disbursementTypes.length - 1)
                    disbursementTypesConcated = disbursementTypesConcated + separator + " ";
            }
        }
        return disbursementTypesConcated;
    }

    @Override
    public double teaToTna(double tea) {
        double middle = Math.pow(1 + tea, (30.0 / 365.0)) - 1;
        BigDecimal bigDecimal = new BigDecimal(middle * 365.0 / 30.0);
        BigDecimal bigDecimalRedondeado = bigDecimal.setScale(4, RoundingMode.HALF_UP);
        return bigDecimalRedondeado.doubleValue();
    }

    @Override
    public double tnaToTea(double tna) {
        double middle = +(Math.pow(1 + tna / 365.0 * 30.0, (365.0 / 30.0)) - 1);
        BigDecimal bigDecimal = new BigDecimal(middle);
        BigDecimal bigDecimalRedondeado = bigDecimal.setScale(4, RoundingMode.HALF_UP);
        return bigDecimalRedondeado.doubleValue();
    }

    @Override
    public Double getUsdPenExchangeRate() {
        HttpClient httpclient;
        try {
            httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://free.currconv.com/api/v7/convert?q=USD_PEN&compact=ultra&apiKey=f113c937ce680b4686af");

            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            JSONObject json = new JSONObject(responseString);
            return JsonUtil.getDoubleFromJson(json, "USD_PEN", null);
        } catch (Exception ex) {

            try{
                httpclient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost("https://app.rextie.com/api/v1/fxrates/rate/");

                HttpResponse response = httpclient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                JSONObject json = new JSONObject(responseString);
                return JsonUtil.getDoubleFromJson(json, "fx_rate_buy", null);
            }catch (Exception ex2){
                errorService.onError(ex);
            }
        }
        return null;
    }

    @Override
    public int getInstallments(LoanApplication loanApplication) throws Exception{
        if (loanApplication != null) {
            List<LoanOffer> loanOffers = loanApplicationService.getLonaOffers(loanApplication.getId());

            if (loanOffers != null && !loanOffers.isEmpty()) {
                if (EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(loanOffers.get(0).getEntityProductParameterId()))
                    return loanApplication.getInstallments() + 2;

                return loanApplication.getInstallments();
            }
        }

        return 0;
    }

    @Override
    public String getHyphenatedPhoneNumber(String phoneNumber) throws Exception {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }

        return String.format("%s-%s", phoneNumber.substring(0, 3), phoneNumber.substring(3));
    }

    @Override
    public String getRandomABTesting(int loanApplicationId) {
        return loanApplicationId % 20 == 0 ?
                LoanApplication.AB_TESTING_A :
                LoanApplication.AB_TESTING_B;
    }

    @Override
    public String datetimeCustomFormat(Date value, String format) {
        if (value == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.forLanguageTag("es-ES"));
        return sdf.format(value);
    }

    @Override
    public String doubleMoneyFormatWithoutSpaces(Double value) {
        return doubleMoneyFormatWithoutSpaces(value, "S/");
    }

    @Override
    public String doubleMoneyFormatWithoutSpaces(Double value, String currency){
        return doubleMoneyFormatWithoutSpaces(value, currency, ',');
    }

    @Override
    public String doubleMoneyFormatWithoutSpaces(Double value, String currency, Character groupingSeparator) {
        if (value == null) {
            value = 0.0;
        }
        Character decimalSeparator = '.';

        switch (groupingSeparator){
            case '.':
                decimalSeparator = ',';
                break;
            case ',':
                decimalSeparator = '.';
                break;
        }
        if(currency == null) currency = "";
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(groupingSeparator);
        symbols.setDecimalSeparator(decimalSeparator);
        formatter.applyPattern("###,###,##0.00");
        formatter.setDecimalFormatSymbols(symbols);
        return currency + formatter.format(value).replaceAll(" ","");
    }

    @Override
    public String integerFormatWithoutSpaces(Integer value) {
        if (value == null) {
            value = 0;
        }
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0");
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(value).replaceAll(" ","");
    }

    @Override
    public String getTextWithoutValues(String text, List<String> search){
        if(text == null || search == null) return text;
        String textAux = text.concat("");
        for (String s : search) {
            textAux = textAux.replaceAll(s,"");
        }
        return textAux.trim().substring(0,1).toUpperCase() + textAux.trim().substring(1).toLowerCase();
    }

    @Override
    public String getTextWithoutSpecialCharacters(String text){
        if(text == null) return text;
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    @Override
    public Boolean validateUrl(String url){
        if(url == null) return false;

        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        if (urlValidator.isValid(url)) return true;
        return false;
    }

    @Override
    public String integerMoneyFormatWithoutSpaces(Double value, String currency) {
        if (value == null) {
            value = 0.0;
        }
        if(currency == null) currency = "";
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        formatter.applyPattern("###,###,##0");
        formatter.setDecimalFormatSymbols(symbols);
        return currency + formatter.format(value).replaceAll(" ","");
    }

    @Override
    public String getWordLetter(String value, int start, int end) {

        StringBuilder builder = new StringBuilder();

        String words[] = value.split(" ");
        for (String word: words) {
            builder.append(word.substring(start,end));
        }
        return builder.toString();
    }

    @Override
    public <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
