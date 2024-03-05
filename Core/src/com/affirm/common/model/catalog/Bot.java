/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */
public class Bot implements Serializable {

    public static final int SUNAT_BOT = 1;
    public static final int RENIEC_BOT = 2;
    public static final int ESSALUD_BOT = 3;
    public static final int SBS_B_BOT = 4;
    public static final int REDAM_BOT = 5;
    public static final int CLARO = 6;
    public static final int MOVISTAR = 7;
    public static final int BITEL = 8;
    public static final int BUFFER = 9;
    public static final int MANAGEMENT_SCHEDULE = 10;
    public static final int DAILY_INTERACTIONS = 11;
    public static final int EQUIFAX_BUREAU = 12;
    public static final int ENTEL = 13;
    public static final int SAT = 14;
    public static final int SIS = 15;
    public static final int MIGRACIONES = 16;
    public static final int EMAILAGE = 17;
    public static final int GOOGLE_USER_DATA = 18;
    public static final int WINDOWS_USER_DATA = 19;
    public static final int YAHOO_USER_DATA = 20;
    public static final int VIRGIN = 21;
    public static final int SBS_RATE = 22;
    public static final int DAILY_PROCESS = 23;
    public static final int ACCESO_OFFERS = 25;
    public static final int ACCESO_SIGNATURE_STATUS = 26;
    public static final int ACCESO_VEHICLE_CATALOG = 27;
    public static final int RIPLEY_REPORT_DAILY_SENDER = 28;
    public static final int SENDGRID_LIST_MANAGEMENT = 29;
    public static final int AFIP = 30;
    public static final int ANSES = 31;
    public static final int BCRA = 32;
    public static final int PADRON = 33;
    public static final int EXCHANGE = 34;
    public static final int ACCESO_SIGNATURE_PENDING = 35;
    public static final int ACCESO_DISPATCH = 36;
    public static final int EVALUATION_PROCESS = 37;
    public static final int PRE_EVALUATION_PROCESS = 38;
    public static final int SCHEDULED_BOTS = 39;
    public static final int END_MONTH_COMPANY_RESSUME = 40;
    public static final int REPORT_PROCESOR = 41;
    public static final int UPLOAD_PRE_APPROVED_BASE = 42;
    public static final int SEND_SMS = 43;
    public static final int RUN_SYNTHESIZED = 44;
    public static final int ENTITY_EVALUATION_PROCESS = 45;
    public static final int ONPE = 46;
    public static final int AUTOMATIC_INTERACTION = 47;
    public static final int RE_EVALUATION_EMAIL_SENDER = 48;
    public static final int SAT_PLATE = 49;
    public static final int SOAT_RECORDS = 50;
    public static final int TARJETAS_PERUANAS_ACTIVATION_DAILY_SENDER = 51;
    public static final int HOURLY_INTERACTIONS = 52;
    public static final int AUTOPLAN_LEADS_DAILY_SENDER = 53;
    public static final int FRAUD_ALERTS = 54;
    public static final int SEND_ACCESO_EXPIRATIO_INTERACTIONS = 55;
    public static final int APPROVE_LOAN_APPLICATION = 56;
    public static final int BO_MANAGEMENT_FOLLOWUP_INTERACTION = 57;
    public static final int UNIVERSIDAD_PERU = 58;
    public static final int MONITOR_SERVERS = 59;
    public static final int BANBIF_CONVERSIONS = 60;
    public static final int LOAD_PRE_APPROVED_BASE = 61;
    public static final int LOAD_NEGATIVE_BASE = 62;
    public static final int SEND_REPORT_TO_FTP = 64;
    public static final int MATI_PROCESS = 65;
    public static final int SEND_TCONEKTA_INFORMATION = 69;
    public static final int MONITOR_SERVERS_AWS = 70;
    public static final int BANTOTAL_AUTHENTICATION = 71;
    public static final int BANBIF_KONECTA_LEAD = 72;
    public static final int BANBIF_EXPIRE_LOANS_NEW_BASE = 73;



    private Integer id;
    private String name;
    private List<String> dayOfWeek;
    private Integer intervalMinute;
    private Date fixedTime;
    private Boolean isScheduled;
    private Boolean isTransactional;
    private Integer countryId;
    private Boolean active;
    private Integer fixedDayOfMonth;

    public Bot() {
    }

    public Bot(JSONObject botJson) {
        this.id = JsonUtil.getIntFromJson(botJson, "bot_id", null);
        this.name = JsonUtil.getStringFromJson(botJson, "bot", null);
        this.dayOfWeek = new ArrayList<>();
        for (int i = 0; i < botJson.getJSONArray("day_of_week").length(); i++) {
            this.dayOfWeek.add(botJson.getJSONArray("day_of_week").getString(i));
        }
        this.intervalMinute = JsonUtil.getIntFromJson(botJson, "interval_minutes", null);
        this.fixedTime = JsonUtil.getPostgresDateFromJson(botJson, "fixed_time", null);
        this.isScheduled = JsonUtil.getBooleanFromJson(botJson, "is_scheduled", false);
        this.isTransactional = JsonUtil.getBooleanFromJson(botJson, "is_transactional", false);
        setCountryId(JsonUtil.getIntFromJson(botJson, "country_id", null));
        setActive(JsonUtil.getBooleanFromJson(botJson, "is_active", null));
        setFixedDayOfMonth(JsonUtil.getIntFromJson(botJson, "fixed_dom", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(List<String> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getIntervalMinute() {
        return intervalMinute;
    }

    public void setIntervalMinute(Integer intervalMinute) {
        this.intervalMinute = intervalMinute;
    }

    public Date getFixedTime() {
        return fixedTime;
    }

    public void setFixedTime(Date fixedTime) {
        this.fixedTime = fixedTime;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public Boolean getIsTransactional() {
        return isTransactional;
    }

    public void setIsTransactional(Boolean isTransactional) {
        this.isTransactional = isTransactional;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getFixedDayOfMonth() {
        return fixedDayOfMonth;
    }

    public void setFixedDayOfMonth(Integer fixedDayOfMonth) {
        this.fixedDayOfMonth = fixedDayOfMonth;
    }

    @Override
    public String toString() {
        return "Bot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", intervalMinute=" + intervalMinute +
                ", fixedTime=" + fixedTime +
                ", isScheduled=" + isScheduled +
                ", isTransactional=" + isTransactional +
                '}';
    }
}
