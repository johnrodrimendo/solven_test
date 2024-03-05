package com.affirm.system.configuration;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.transactional.AztecaGatewayBasePhone;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.onesignal.service.impl.OneSignalServiceImpl;
import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jrodriguez
 * <p>
 * Estos valores deberian venir de BD
 */
@Component("configuration")
public class Configuration {

    public enum Application {
        BACKOFFICE,
        CLIENT,
        JOB,
        SCHEDULE,
        COMPANY_EXT,
        ENTITY_EXT,
        LANDING,
        ACQUISITION
    }

    //    TOOD: PONER EN UNA CLASE APARTE
    public enum SqsQueue {
        SCRAPPER,
        SCHEDULE,
        EVALUATION_INITIALIZER,
        EVALUATION_PROCESSOR,
        REPORT,
        FRAUD_ALERTS,
        APPROVE_LOAN_APPLICATION,
        DEFAULT
    }

    public static final String AWS_ACCOUNT_ID = "xxxxxxxxxxxxxxxxxxxxx";

    public static final ExecutorService rekognitionExecutor = Executors.newFixedThreadPool(1);

    public static final String APP_NAME = "Solven";

    public static final String META_TITLE = "Solven | El crédito que mereces";
    public static final String META_KEYWORDS = "solven, crédito, créditos, crédito en linea, prestamo, prestamos, tarjeta de crédito, tarjetas de crédito, adelanto de sueldo, seguros, financiera, banco, el crédito que mereces";
    public static final String META_DESCRIPTION = "Creamos una experiencia financiera distinta. Nos asociamos con las mejores compañías, para ofrecerte créditos sin letras chicas y a tasas bajas.";
    public static final String CRYPTO_KEY = "xxxxxxxxxxxxxxxxxxxxx";

    // Solven pages
    public static final String BLOG_URL = "https://docs.solven.pe";
    //public static final String COMPLAINT_BOOK_URL = "https://app.libroreclamaciones.pe/solven-funding-sac/libro-reclamaciones";
    public static final String COMPLAINT_BOOK_URL = "https://goo.gl/forms/YSrUopQdzToWErxt2";
    // Social Networks
    public static final String FACEBOOK_URL = "https://www.facebook.com/SolvenLatam/";
    public static final String TWITTER_URL = "https://twitter.com/SolvenLatam/";
    public static final String LINKEDIN_URL = "https://www.linkedin.com/company/solven/";
    public static final String GOOGLE_PLUS_URL = "https://plus.google.com/";
    public static final String INSTAGRAM_URL = "https://www.instagram.com/SolvenLatam/";

    // Sendgrid
    public static final String SENGRID_API_KEY = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String DEFAULT_FROM_NAME = "Solven";
    public static final String EMAIL_DEV_TO = "debug@solven.pe";

    // Death by Captcha
    public static final String DEATHBYCAPTCHA_USER = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String DEATHBYCAPTCHA_PWD = "xxxxxxxxxxxxxxxxxxxxx";

    // Twilio
    public static final String TWILIO_ACCOUNT_SID = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String TWILIO_AUTH_TOKEN = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String TWILIO_FROM_NUMBER = "+14156896100";
    public static final String SMS_DEV_TO = "+51956764376";

    //Infobip
    public static final String INFOBIP_BASE_URL = "https://xr59pq.api.infobip.com";
    public static final String INFOBIP_API_KEY = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String INFOBIP_API_KEY_PREFIX = "App";

    // Icar
    public static final String ICAR_SECRET_KEY = "xxxxxxxxxxxxxxxxxxxxx";
    public static final int ICAR_FILETYPE = 20;

    public static final int MIN_PASSWORD = 8;
    public static final int OLD_PASSWORDS = 5;

    public static final int MIN_PASSWORD_BACKOFFICE = 8;
    public static final int OLD_PASSWORDS_BACKOFFICE = 5;

    // WordPress
    public static final String WORDPRESS_BASE_URL = "https://docs.solven.pe/wp-json/wp/v2/";
    public static final int WORDPRESS_PER_PAGE = 30;
    public static final int WORDPRESS_DEFAULT_TAG_COUNTRY_ID = 32; // Tag id de Peru

    // URLs Configurations
    public static final String EVALUATION_CONTROLLER_URL = "evaluacion";
    public static final String SELF_EVALUATION_CONTROLLER_URL = "autoevaluacion";
    public static final String SALARY_ADVANCE_CONTROLLER_URL = "salaryadvanceloanapplication";
    public static final String COMPARISON_CONTROLLER_URL = "compara";

    // Default URL To Redirect

    public static final String DEFAULT_URL = "https://www.solven.la";

    public static final String DELETE_LIST = "https://api.sendgrid.com/v3/contactdb/lists/";
    public static final String CREATE_LIST = "https://api.sendgrid.com/v3/contactdb/lists";
    public static final String CREATE_CONTACTS = "https://api.sendgrid.com/v3/contactdb/recipients";
    public static final String ADD_CONTACT = "https://api.sendgrid.com/v3/contactdb/lists/{list_id}/recipients";

    // WAVY
    public static final String WAVY_USERNAME = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String WAVY_AUTH_TOKEN = "xxxxxxxxxxxxxxxxxxxxx";

    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private BrandingService brandingService;

    // Intercom
    public static String getIntercomScript(String customAppId) {
        String appId = customAppId != null ? customAppId : "xxxxxxxxxxxxxxxxxxxxx";
        String script = "<script>\n" +
                "        window.intercomSettings = {\n" +
                "            app_id: \"" + appId + "\"\n" +
                "        };\n" +
                "    </script>\n" +
                "    <script>(function(){var w=window;var ic=w.Intercom;if(typeof ic===\"function\"){ic('reattach_activator');ic('update',intercomSettings);}else{var d=document;var i=function(){i.c(arguments)};i.q=[];i.c=function(args){i.q.push(args)};w.Intercom=i;function l(){var s=d.createElement('script');s.type='text/javascript';s.async=true;s.src='https://widget.intercom.io/widget/" + appId + "';var x=d.getElementsByTagName('script')[0];x.parentNode.insertBefore(s,x);}if(w.attachEvent){w.attachEvent('onload',l);}else{w.addEventListener('load',l,false);}}})()</script>";
        return script;
    }

    // Intercom
    public String getZendeskScript(HttpServletRequest request) throws Exception {
        String zendeskKey = "xxxxxxxxxxxxxxxxxxxxx";
        String brandingColor = brandingService.getEntityBrandingPrimaryColor(request, null);
        String zendeskColor = brandingColor != null ? "'" + brandingColor + "'" : "null";

        StringBuilder script = new StringBuilder("<script type=\"text/javascript\">\n" +
                "    window.zESettings = {\n" +
                "        webWidget: {\n" +
                "            chat: {\n" +
                "                suppress: true\n" +
                "            },\n" +
                "            color: {\n" +
                "                theme: " + zendeskColor + "\n" +
                "            }\n" +
                "        }\n" +
                "    };\n" +
                "</script>");
        script.append("<script id=\"ze-snippet\" src=\"https://static.zdassets.com/ekr/snippet.js?key=" + zendeskKey + "\"></script>");
        return script.toString();
    }

    // MAX MIND cuenta solven
    public static final String MAX_MIND_KEY = "Basic xxxxxxxxxxxxxxxxxxxxx==";

    // Google cuenta solven
    public static final String GOOGLE_API_KEY = "xxxxxxxxxxxxxxxxxxxxx";

    public static boolean isBackoffice() {
        try {
            Class.forName("com.affirm.backoffice.controller.BackofficeController");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isClient() {
        try {
            Class.forName("com.affirm.system.configuration.SpringLandingConfiguration");
            return true;
        } catch (ClassNotFoundException e) {

            try {
                Class.forName("com.affirm.system.configuration.SpringCompanyExtConfiguration");
                return true;
            } catch (ClassNotFoundException f) {

                try {
                    Class.forName("com.affirm.system.configuration.SpringEntityExtConfiguration");
                    return true;
                } catch (ClassNotFoundException g) {

                    try {
                        Class.forName("com.affirm.system.configuration.SpringAcquisitionConfiguration");
                        return true;
                    } catch (ClassNotFoundException h) {
                        return false;
                    }

                }

            }

        }
    }

    public static long getBoTimeoutMinutes() {
        return 60;
    }

    public static int getSessionTimeLife() {
        if (hostEnvIsProduction()) {
            return 60 * 60 * 2;
        }
        if (hostEnvIsStage()) {
            return 60 * 60 * 2;
        }
        if (hostEnvIsDev()) {
            return 60 * 60;
        }

        return 10;
    }

    public static int getMessengerSessionTimeLife() {
        return 2;
    }

    public enum OauthNetwork {
        GOOGLE("https://www.googleapis.com/oauth2/v4/token",
                Configuration.getClientDomain() + "/oauth/response/google",
                "xxxxxxxxxxxxxxxxxxxxx",
                "xxxxxxxxxxxxxxxxxxxxx",
                "https://people.googleapis.com/v1/people/me",
                "email https://www.googleapis.com/auth/contacts.readonly https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/gmail.readonly"),
        WINDOWS("https://login.live.com/oauth20_token.srf",
                Configuration.getClientDomain() + "/oauth/response/windows",
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" :
                        (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : "xxxxxxxxxxxxxxxxxxxxx"),
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" :
                        (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : "xxxxxxxxxxxxxxxxxxxxx"),
                "https://apis.live.net/v5.0/me",
                "wl.basic wl.offline_access wl.signin wl.emails"),
        YAHOO("https://api.login.yahoo.com/oauth2/get_token",
                Configuration.getClientDomain() + "/oauth/response/yahoo",
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx--" :
                        (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx--" : hostEnvIsDev() ? "xxxxxxxxxxxxxxxxxxxxx--" : "sorry-not-yahoo-for-local"),
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" :
                        (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : hostEnvIsDev() ? "xxxxxxxxxxxxxxxxxxxxx" : "sorry-not-yahoo-for-local"),
                "https://query.yahooapis.com/v1/yql?q=select%20*%20from%20social.profile(0)%20where%20guid%3Dme&format=json",
                "sdct-r msgr-w sdpp-w sdrl-w"),
        FACEBOOK("https://graph.facebook.com/v2.8/oauth/access_token",
                Configuration.getClientDomain() + "/oauth/response/facebook",
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" :
                        (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : "xxxxxxxxxxxxxxxxxxxxx"),
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" :
                        (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : "xxxxxxxxxxxxxxxxxxxxx"),
                "https://graph.facebook.com/me?fields=email,name,first_name,last_name,age_range,link,gender,locale,picture,timezone,updated_time,verified,location,birthday",
                "public_profile,email,user_birthday,user_location,user_friends"),
        LINKEDIN("https://www.linkedin.com/oauth/v2/accessToken",
                Configuration.getClientDomain() + "/oauth/response/linkedin",
                "xxxxxxxxxxxxxxxxxxxxx",
                "xxxxxxxxxxxxxxxxxxxxx",
                "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,maiden-name,formatted-name,phonetic-first-name,"
                        + "phonetic-last-name,formatted-phonetic-name,headline,location,industry,current-share,"
                        + "num-connections,num-connections-capped,summary,specialties,positions,picture-url,picture-urls,"
                        + "site-standard-profile-request,api-standard-profile-request,public-profile-url,email-address)?format=json",
                "r_basicprofile r_emailaddress"),
        MERCADOLIBRE("https://api.mercadolibre.com/oauth/token",
                Configuration.getClientDomain() + "/oauth/response/mercadolibre",
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" : (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : hostEnvIsDev() ? "xxxxxxxxxxxxxxxxxxxxx" : "sorry-not-yahoo-for-local"),
                hostEnvIsProduction() ? "xxxxxxxxxxxxxxxxxxxxx" : (hostEnvIsStage() ? "xxxxxxxxxxxxxxxxxxxxx" : hostEnvIsDev() ? "xxxxxxxxxxxxxxxxxxxxx" : "sorry-not-yahoo-for-local"),
                "https://api.mercadolibre.com/users/me",
                "");

        private String tokenUrl;
        private String redirectUri;
        private String clientId;
        private String clientSecret;
        private String profileUrl;
        private String defaultPermission;

        OauthNetwork(String tokenUrl, String redirectUri, String clientId, String clientSecret, String profileUrl, String defaultPermission) {
            this.tokenUrl = tokenUrl;
            this.redirectUri = redirectUri;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.profileUrl = profileUrl;
            this.defaultPermission = defaultPermission;
        }

        public String getTokenUrl() {
            return tokenUrl;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public String getDefaultPermission() {
            return defaultPermission;
        }

        public static OauthNetwork getByChar(char n) {
            switch (n) {
                case 'G':
                    return Configuration.OauthNetwork.GOOGLE;
                case 'W':
                    return Configuration.OauthNetwork.WINDOWS;
                case 'Y':
                    return Configuration.OauthNetwork.YAHOO;
                case 'F':
                    return Configuration.OauthNetwork.FACEBOOK;
                case 'L':
                    return Configuration.OauthNetwork.LINKEDIN;
                case 'M':
                    return Configuration.OauthNetwork.MERCADOLIBRE;
                default:
                    return null;
            }
        }
    }

    public static final String BACKOFFICE_FRONT_YEAR_FIRST_DATE_FORMAT = "yyyy/MM/dd HH:mm";
    public static final String BACKOFFICE_FRONT_SHORT_DATE_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String BACKOFFICE_FRONT_ONLY_DATE_FORMAT = "dd/MM/yyyy";
    public static final String BACKOFFICE_FRONT_ONLY_HOUR_FORMAT = "HH:mm";
    public static final String START_DATE_FORMAT = "MM/yyyy";
    public static final String LONG_MONTH_YEAR_DATE_FORMAT = "MMMM yyyy";
    public static final String BACKOFFICE_FRONT_HUMAN_DATE = "d MMMM 'de' yyyy";
    public static final Integer BACKOFFICE_PAGINATION_LIMIT = 200;

    public static final int BOT_TIMEOUT = 30;

    public static boolean SMS_AUTHORIZATION_ACTIVATED() {
        return hostEnvIsProduction();
    }

    public static final List<String> TESTABLE_PHONENUMBERS = Arrays.asList("+51976364877", "+51993346829", "+51973005600", "976364877", "993346829", "973005600");// DANIEL - ESTEBAN - JHON - EDSON - MATIAS - JESUS - OMAR - GUSTAVO

    public static boolean IS_AUTHORIZED_PHONENUMBER(String phoneNumber) {
        return hostEnvIsProduction() || (hostEnvIsNotLocal() && TESTABLE_PHONENUMBERS.contains(phoneNumber));
    }

    public static boolean IS_AUTHORIZED_PHONENUMBER_INTICO(String phoneNumber) {
        return hostEnvIsProduction() || (hostEnvIsNotLocal() && getPhoneNumbersForTest().contains(phoneNumber));
    }

    public static final String SOLES_CURRENCY = "S/";
    public static final String DOLLAR_CURRENCY = "$";


    public static final String CLIENT_PRD_URL = "https://www.solven.pe";
    public static final String CLIENT_STG_URL = "https://stg.solven.pe";
    public static final String CLIENT_DEV_URL = "https://dev.solven.pe";
    public static final String CLIENT_LOC_URL = "http://localhost:8080";

    public static final String ARG_CLIENT_PRD_URL = "https://www.solven.com.ar";
    public static final String ARG_CLIENT_STG_URL = "https://stg.solven.com.ar";
    public static final String ARG_CLIENT_DEV_URL = "https://dev.solven.com.ar";
    public static final String ARG_CLIENT_LOC_URL = "http://solven-test.ar:8080/";


    public static final String COMPARTAMOS_PRD_URL = "https://www.solven.pe";
    public static final String COMPARTAMOS_STG_URL = "https://stg-solven-c.herokuapp.com";
    public static final String COMPARTAMOS_DEV_URL = "https://compartamosdev.solven.pe";
    public static final String COMPARTAMOS_LOC_URL = "http://compartamos.solven-test.pe:8080";

    public static final String APP_FILE_THUMBANIL_PREFIX = "thumbnail.";

    public static final String AGENT_FULLNAME_COMMERCIAL = "John";
    public static final String AGENT_IMAGE_URL_COMMERCIAL = "https://s3.amazonaws.com/solven-public/img/colab/john.jpg";

    public static final String AGENT_FULLNAME_COLLECTION = "Esteban";
    public static final String AGENT_IMAGE_URL_COLLECTION = "https://s3.amazonaws.com/solven-public/img/colab/esteban.jpg";

    public static final String WEBHOOK_PATH = "webhook";

    public static final String API_REST_API_PATH= "apirest";

    // Hostenv Values
    public static final String HOSTENV_KEY = "HOSTENV";
    public static final String HOSTENV_VAL_LOC = "loc";
    public static final String HOSTENV_VAL_DEV = "dev";
    public static final String HOSTENV_VAL_STG = "stg";
    public static final String HOSTENV_VAL_PRD = "prd";
    public static final String HOSTENV_VAL_QA = "qa";

    // Kreditiweb
    public static final String KREDITIWEB_SOURCE = "Kreditiweb";
    public static final String KREDITIWEB_MEDIUM = "Click&Compare";
    public static final String KREDITIWEB_CAMPAIGN = "Prueba_Personales_CPA";

    //Thread pools
    public static final Executor MIGRACION_EXECUTOR = Executors.newFixedThreadPool(5);
    public static final Executor REVERSE_GEODECODING_EXECUTOR = Executors.newFixedThreadPool(10);

    public static boolean hostEnvIsProduction() {
        return System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_PRD);
    }

    public static boolean hostEnvIsQA() {
        return System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_QA);
    }

    public static boolean hostEnvIsStage() {
        return System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_STG);
    }

    public static boolean hostEnvIsDev() {
        return System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_DEV);
    }

    public static boolean hostEnvIsLocal() {
        return System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_LOC);
    }

    public static String getEnvironmmentName() {
        if (System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_PRD))
            return HOSTENV_VAL_PRD;
        else if (System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_DEV))
            return HOSTENV_VAL_DEV;
        else if (System.getenv(HOSTENV_KEY).equals(HOSTENV_VAL_STG))
            return HOSTENV_VAL_STG;
        else
            return HOSTENV_VAL_LOC;
    }

    public static String hostEnvName() {
        if (hostEnvIsProduction()) {
            return "Production";
        } else if (hostEnvIsStage()) {
            return "Stage";
        } else if (hostEnvIsDev()) {
            return "Development";
        } else {
            return "Local";
        }
    }

    public static boolean hostEnvIsNotLocal() {
        return !hostEnvIsLocal();
    }

    public static String getClientDomain() {
        if (hostEnvIsProduction()) {
            return CLIENT_PRD_URL;
        } else if (hostEnvIsStage()) {
            return CLIENT_STG_URL;
        } else if (hostEnvIsDev()) {
            return CLIENT_DEV_URL;
        } else {
            return CLIENT_LOC_URL;
        }
    }

    public static String getClientDomain(Integer countryId) {
        switch (countryId) {
            case CountryParam.COUNTRY_ARGENTINA:
                if (hostEnvIsProduction()) {
                    return ARG_CLIENT_PRD_URL;
                } else if (hostEnvIsStage()) {
                    return ARG_CLIENT_STG_URL;
                } else if (hostEnvIsDev()) {
                    return ARG_CLIENT_DEV_URL;
                } else {
                    return ARG_CLIENT_LOC_URL;
                }
            case CountryParam.COUNTRY_PERU:
                if (hostEnvIsProduction()) {
                    return CLIENT_PRD_URL;
                } else if (hostEnvIsStage()) {
                    return CLIENT_STG_URL;
                } else if (hostEnvIsDev()) {
                    return CLIENT_DEV_URL;
                } else {
                    return CLIENT_LOC_URL;
                }
        }
        return "";
    }

    public static String getCompartamosClientDomain() {
        if (hostEnvIsProduction()) {
            return COMPARTAMOS_PRD_URL;
        } else if (hostEnvIsStage()) {
            return COMPARTAMOS_STG_URL;
        } else if (hostEnvIsDev()) {
            return COMPARTAMOS_DEV_URL;
        } else {
            return COMPARTAMOS_LOC_URL;
        }
    }

    public static int getServiceTimeout() {
        if (hostEnvIsProduction()) {
            return 10;
        } else if (hostEnvIsStage()) {
            return 20;
        } else if (hostEnvIsDev()) {
            return 20;
        } else {
            return 20;
        }
    }

    public String getEFLHostedPlayer(Integer loanApplicationId) throws Exception {
        if (loanApplicationService.isConsolidationLoanApplication(loanApplicationId))
            return System.getenv("EFL_HOSTED_PLAYER_CONSOLIDACION").replaceAll("\\\\=", "=");
        else
            return System.getenv("EFL_HOSTED_PLAYER");
    }

    public static String getEFLDomain() {
        if (hostEnvIsProduction()) {
            return "https://solven.eflglobal.com";
        } else if (hostEnvIsStage()) {
            return "https://aj-demo-uat-external.eflglobal.com";
        } else if (hostEnvIsDev()) {
            return "https://aj-demo-uat-external.eflglobal.com";
        } else {
            return "https://aj-demo-uat-external.eflglobal.com";
        }
    }

    public static String getBackofficeDomain() {
        if (hostEnvIsProduction()) {
            return "https://boprd.solven.la";
        } else if (hostEnvIsStage()) {
            return "https://bostg.solven.la";
        } else if (hostEnvIsDev()) {
            return "https://bodev.solven.la";
        } else {
            return "http://localhost:8080/backoffice";
        }
    }

    public static String getRextieDomain() {
        if(hostEnvIsProduction()) {
            return "https://app.rextie.com";
        } else if(hostEnvIsStage() || hostEnvIsDev() || hostEnvIsLocal()) {
            return "https://stage.rextie.com";
        } else {
            return "https://app.rextie.com/";// ESTA URL ES LIBRE
        }
    }

    public static int getEntityExtranetTimeoutMinutes() {
        if (hostEnvIsProduction()) {
            return 60;
        } else {
            return 60;
        }
    }

    public static int getExtranetTimeoutMinutes() {
        if (hostEnvIsProduction()) {
            return 5;
        } else {
            return 5;
        }
    }

    public static String getGAKey() {
        if (!hostEnvIsProduction()) {
            return "xxxxxxxxxxxxxxxxxxxxx";
        }
        return "xxxxxxxxxxxxxxxxxxxxx";
    }

    public static String getGAKey(int countryId) {
        if (!hostEnvIsProduction()) {
            switch (countryId){
                case CountryParam.COUNTRY_ARGENTINA:
                    return "xxxxxxxxxxxxxxxxxxxxx";
                default:
                    return "xxxxxxxxxxxxxxxxxxxxx";
            }
        }else{
            switch (countryId){
                case CountryParam.COUNTRY_ARGENTINA:
                    return "xxxxxxxxxxxxxxxxxxxxx";
                default:
                    return "xxxxxxxxxxxxxxxxxxxxx";
            }
        }
    }

    /**
     * Replace local keys when to develop
     *
     * @return The key for Google Tag Manager
     */
    public static String getGoogleTagManagerKey(Integer countryId) {
        if(countryId == null)
            return null;
        if (hostEnvIsProduction()) {
            switch (countryId) {
                case CountryParam.COUNTRY_ARGENTINA:
                    return "xxxxxxxxxxxxxxxxxxxxx";
                default:
                    return "xxxxxxxxxxxxxxxxxxxxx";
            }
        } else if (hostEnvIsLocal()) {
            switch (countryId) {
                case CountryParam.COUNTRY_ARGENTINA:
                    return "xxxxxxxxxxxxxxxxxxxxx";
                default:
                    return "xxxxxxxxxxxxxxxxxxxxx";
            }
        } else {
            // TODO: Use another keys
            switch (countryId) {
                case CountryParam.COUNTRY_ARGENTINA:
                    return "xxxxxxxxxxxxxxxxxxxxx";
                default:
                    return "xxxxxxxxxxxxxxxxxxxxx";
            }
        }
    }

    public static final String GA_LOCAL = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_SOLVEN_PRD = "xxxxxxxxxxxxxxxxxxxxx";
    //    DE ESTA MANERA O AGREGARLOS EN "ENTITY"
    public static final String GA_MARKETPLACE = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_CAJA_SULLANA = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_RIPLEY = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_COMPARTAMOS = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_ACCESO = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_ABACO = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_EFL = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_BANBIF = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_AZTECA = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_AZTECA_COBRANZA = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_AZTECA_CUENTA = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_PRISMA = "xxxxxxxxxxxxxxxxxxxxx";
    public static final String GA_BF = GA_LOCAL;//TODO
    public static final String GA_EFECTIVA = GA_LOCAL;//TODO
    public static final String GA_CAJA_LOS_ANDES = GA_LOCAL;//TODO
    public static final String GA_MULTIFINANZAS = GA_LOCAL;//TODO
    public static final String GA_WENANCE = GA_LOCAL;//TODO
    public static final String GA_AELU = GA_LOCAL;//TODO

    public static String getViewID() {
        if (!hostEnvIsProduction() && !hostEnvIsStage()) {
            return GA_LOCAL;
        }
        return GA_SOLVEN_PRD;
    }

    public static int getExtranetSessionValidationIntervalMinutes() {
        if (hostEnvIsProduction()) {
            return 2;
        } else {
            return 1;
        }
    }

    public static String getDisbursmentMailTo() {
        if (hostEnvIsProduction()) {
            return "desembolsos@solven.pe";
        } else {
            return EMAIL_DEV_TO;
        }
    }

    public static String getDisbursmentConfirmatorMailTo() {
        if (hostEnvIsProduction()) {
            return Configuration.GENERAL_MANAGER_EMAIL;
        } else {
            return EMAIL_DEV_TO;
        }
    }

    public static Locale getDefaultLocale() {
        return new Locale("es", "PE");
    }

    public static String EMAIL_CONTACT_TO() {
        return hostEnvIsProduction() ? "hola@solven.pe" : EMAIL_DEV_TO;
    }

    public static String EMAIL_CONTACT_FROM() {
        return "contacto@solven.pe";
    }

    public static String EMAIL_ERROR_TO() {
        return hostEnvIsProduction() ? "alertas@solven.pe" : EMAIL_DEV_TO;
    }

    public static String EMAIL_ERROR_ENTITY_WEBSERVICE_TO() {
        return hostEnvIsProduction() ? "alertas@solven.pe" : EMAIL_DEV_TO;
    }

    public static String EMAIL_PROCESS_FROM() {
        return "procesos@solven.pe";
    }

    /**
     * Array of number to wich the error sms will be sent.Put Country code first, then the number!
     *
     * @return
     */
    public static String[] SMS_ERROR_TO() {
        return hostEnvIsProduction() ? new String[]{"+51998931099", "+51956764378", "+51976364877"} : null;
    }

    public static String TWILIO_WEBHOOK() {
        return getClientDomain() + "/webhook/twilio/";
    }

    public static boolean TOKY_ACTIVE() {
        return !hostEnvIsProduction();
    }

    public static String recaptchaPublicKey() {
        return System.getenv("RECAPTCHA_PUBLIC");
    }

    public static String hiddencaptchaPublicKey() {
        return System.getenv("HIDDENCAPTCHA_PUBLIC");
    }

    public static String invisibleCaptchaPublicKey() {
        return System.getenv("INVISIBLE_CAPTCHA_PUBLIC");
    }

    public static long MAX_UPLOAD_FILE_SIZE() {
        return 1024 * 1024 * 20;
    }

    public static long MAX_UPLOAD_FILE_SIZE_MB() {
        return MAX_UPLOAD_FILE_SIZE() / 1024 / 1024;
    }

    public static long MAX_REQUEST_SIZE() {
        return 1024 * 1024 * 22;
    }

    public static String[] ALLOW_USER_FILE_TYPE = {"image/png", "image/jpeg", "image/jpg", "image/gif", "application/pdf", "application/zip", "application/x-rar-compressed", "application/octet-stream", "application/x-zip-compressed", "multipart/x-zip", "application/gzip", "application/tar"};

    public static boolean HTTPS_SSL_IMPLEMENTED = true;

    public static boolean REDUCE_SECURITY_NOT_PROD = false;

    public static String COMPARISON_PROCESS = "{\"questions\":[{\"skippable\":false,\"id\":70,\"results\":{\"PAY_SOMETHING_URGENT\":71,\"GET_LOAN\":72,\"CREDIT_CARD\":73,\"START_CREDIT_HISTORY\":74}},{\"skippable\":false,\"id\":71,\"results\":{\"DEFAULT\":26}},{\"skippable\":false,\"id\":72,\"results\":{\"DEFAULT\":26}},{\"skippable\":false,\"id\":73,\"results\":{\"DEFAULT\":26}},{\"skippable\":false,\"id\":74,\"results\":{\"DEFAULT\":26}},{\"skippable\":false,\"id\":26,\"results\":{\"STUDENT_OR_NO_INCOME\":76,\"DEFAULT\":75}},{\"skippable\":false,\"id\":75,\"results\":{\"DEFAULT\":76}},{\"skippable\":false,\"id\":76,\"results\":{\"DEFAULT\":8}},{\"skippable\":false,\"id\":8,\"results\":{\"DEFAULT\":77}}],\"firstQuestionId\":70}";

    // This should not exist!!
    public static String EVALUATION_CONFIG = "{\"sections\":{\"preInformation\":[20,1,2,3,4,5,6,8,94],\"personalInformation\":[21,80,22,23,24,25],\"income\":[26,27,28,29,30,31,32,33,34,35,36,37,38,39,61,40,41,42,43,44,45,46,47,48,49,82,85,88,89,90,91,92],\"offer\":[50,79,81],\"verification\":[51,52,53,54,55,56,57,58,59,60, 83], \"result\":[11, 12, 13], \"waitingApproval\":[62]}}";

    public static final Integer PASSWORD_EXPIRATION_PERIOD = 30;
    public static final Integer MAX_ACTIVE_SESSIONS = 2;
    public static final Integer MAX_INACTIVE_DAYS = 30;
    public static final Integer MAX_LOGIN_ATTEMPTS = 3;

    public static final Integer MIN_INCOME = 850;

    public static String getsendgridApikey() {
        String apiKey = "xxxxxxxxxxxxxxxxxxxxx";
        return apiKey;
    }

    public static boolean ACCESO_DUMMY_WS() {
        return (hostEnvIsStage() || hostEnvIsDev() || hostEnvIsLocal()) && (System.getenv("ACCESO_DUMMY_WS") != null && System.getenv("ACCESO_DUMMY_WS").equalsIgnoreCase("true"));
    }

    public static Application getApplicationType() {
        if (isBackoffice()) {
            return Application.BACKOFFICE;
        } else if (isClient()) {
            return Application.CLIENT;
        }
        return null;
    }

    public static final String HOTJAR_ACCOUNT_ID = "xxxxxxxxxxxxxxxxxxxxx";

    public static boolean isHotjarActive() {
        return (hostEnvIsProduction());
    }

    public static boolean tfaLoginIsActive() {
        return false;
    }

    public static boolean BLOCK_COUNTRY_BO() {
        return false;
    }

    public static final String DEFAULT_BUTTON_COLOR = "#26C9D2";

    public static Integer QUESTION_CONVERT_ID = ProcessQuestion.Question.Constants.MARITAL_STATUS;

    public static final List<String> orderedTopDepartments = Arrays.asList("14", "04", "12", "19");//LIMA-AREQUIPA-LA LIBERTAD-PIURA
    public static final List<Integer> rejectedReasonsExclusiveBanBif = Arrays.asList(8, 9);
    public static final List<Integer> rejectedReasonsBanBif = Arrays.asList(8, 9, 6, 5);
    public static final List<Integer> areaTypesBanBif = Arrays.asList(1, 2, 7, 14, 3, 12, 15, 16, 17, 18, 19, 20);
    public static final List<Integer> areaTypesExclusiveBanBif = Arrays.asList(14, 15, 16, 17, 18, 19, 20);
    public static final List<Integer> streetTypesBanBif = Arrays.asList(1, 2, 3, 4, 7, 8, 9, 10, 11, 5, 12, 13);
    public static final List<Integer> streetTypesExclusiveBanBif = Arrays.asList(7, 8, 9, 10, 11, 12, 13);
    public static final List<Integer> areaTypesAcceso = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12);
    public static final List<Integer> streetTypesAcceso = Arrays.asList(1,2,3,4,5,6,13);
    public static final List<Integer> areaTypesAzteca = Arrays.asList(13,1,12,8,2,3,14,22,24,15,25,26,27,28,29,30,20);
    public static final List<Integer> areaTypesExclusiveAzteca = Arrays.asList(21,22,23,24,25,26,27,28,29,30,31,32,33,34);
    public static final List<Integer> streetTypesAzteca = Arrays.asList(14,2,3,4,1,7,5,6,9,15,16);
    public static final List<Integer> streetTypesExclusiveAzteca = Arrays.asList(14,15,16,17);


    public static final String CONVERSION_EVALUATION = "evaApr";
    public static final String CONVERSION_PRE_EVALUATION = "preApr";
    public static final String SOURCE_CONVERSION_ORGANIC = "organic";
    public static final String SOURCE_CONVERSION_GOOGLE = "google";
    public static final String SOURCE_CONVERSION_FACEBOOK = "facebook";


    public static final Integer DEFAULT_INSTALLMENTS = 24;
    public static final Integer DEFAULT_AGENT_ID = 9;
    public static final Integer MAX_INSTALLMENTS = 120;

    public static final Integer NATION_BANK_ID = 57;

    public static final Integer[] wenanceRejectOfferIds = {1, 2};

    public static String getCreditAmountModificationMailTo() {
        if (hostEnvIsProduction()) {
            return Configuration.PERU_MANAGER_EMAIL;
        } else {
            return EMAIL_DEV_TO;
        }
    }

    public static String GENERAL_MANAGER_EMAIL = "xxxxxxxxxxxxxxxxxxxxx@solven.pe";
    public static String PERU_MANAGER_EMAIL = "xxxxxxxxxxxxxxxxxxxxx@solven.pe";
    public static String ARGENTINA_MANAGER_EMAIL = "xxxxxxxxxxxxxxxxxxxxx@solven.com.ar";

    private static final String SQS_QUEUE_REGION = Regions.US_WEST_2.getName();
    public static final int SQS_QUEUE_LOCAL_PORT = 9324;
    public static final String SQS_ELASTICMQ_URL = "http://localhost:" + SQS_QUEUE_LOCAL_PORT;

    public static int queueThreads(SqsQueue queue) {
        switch (queue) {
            case SCRAPPER:
                if (hostEnvIsProduction()) {
                    return 2;
                } else {
                    return 1;
                }
            case SCHEDULE:
                if (hostEnvIsProduction()) {
                    return 3;
                } else {
                    return 1;
                }
            case EVALUATION_INITIALIZER:
                if (hostEnvIsProduction()) {
                    return 5;
                } else {
                    return 2;
                }
            case EVALUATION_PROCESSOR:
                if (hostEnvIsProduction()) {
                    return 9;
                } else {
                    return 5;
                }
            case REPORT:
                if (hostEnvIsProduction()) {
                    return 1;
                } else {
                    return 1;
                }
            case FRAUD_ALERTS:
                if (hostEnvIsProduction()) {
                    return 3;
                } else {
                    return 1;
                }
            case APPROVE_LOAN_APPLICATION:
                if (hostEnvIsProduction()) {
                    return 2;
                } else {
                    return 1;
                }
            case DEFAULT:
                if (hostEnvIsProduction()) {
                    return 2;
                } else {
                    return 1;
                }
            default:
                throw new IllegalArgumentException(queue.toString());
        }
    }

    //    ESTAS URLS DEBEN ESTAR REGISTRADAS EN AWS SQS TANTO PARA LOCAL COMO OTROS AMBIENTES
    //    ASEGURARSE QUE TODOS LOS SQS TENGAS LOS PERMISOS NECESARIOS. SI LANZARA EXCEPTION QueueDoesNotExistException o mas especificos
    public static String queueUrl(SqsQueue queue) {
        String sqsBaseUrl = "https://sqs." + SQS_QUEUE_REGION + ".amazonaws.com/" + AWS_ACCOUNT_ID + "/";
        String sqsMqUrl = SQS_ELASTICMQ_URL + "/queue/";
        String sqsEndpoint;

        switch (queue) {
            case SCRAPPER:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "webscrapper-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "webscrapper-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "webscrapper-development";
                } else {
                    sqsEndpoint = "webscrapper-local";
                }
                break;
            case SCHEDULE:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "schedule-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "schedule-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "schedule-development";
                } else {
                    sqsEndpoint = "schedule-local";
                }
                break;
            case EVALUATION_INITIALIZER:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "evaluation-initializer-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "evaluation-initializer-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "evaluation-initializer-development";
                } else {
                    sqsEndpoint = "evaluation-initializer-local";
                }
                break;
            case EVALUATION_PROCESSOR:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "evaluation-processor-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "evaluation-processor-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "evaluation-processor-development";
                } else {
                    sqsEndpoint = "evaluation-processor-local";
                }
                break;
            case REPORT:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "report-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "report-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "report-development";
                } else {
                    sqsEndpoint = "report-local";
                }
                break;
            case FRAUD_ALERTS:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "fraud-alerts-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "fraud-alerts-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "fraud-alerts-development";
                } else {
                    sqsEndpoint = "fraud-alerts-local";
                }
                break;
            case APPROVE_LOAN_APPLICATION:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "approve-loan-application-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "approve-loan-application-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "approve-loan-application-development";
                } else {
                    sqsEndpoint = "approve-loan-application-local";
                }
                break;
            case DEFAULT:
                if (hostEnvIsProduction()) {
                    sqsEndpoint = "default-production";
                } else if (hostEnvIsStage()) {
                    sqsEndpoint = "default-stage";
                } else if (hostEnvIsDev()) {
                    sqsEndpoint = "default-development";
                } else {
                    sqsEndpoint = "default-local";
                }
                break;
            default:
                throw new IllegalArgumentException(queue.toString());
        }

        return hostEnvIsLocal() ? (sqsMqUrl + sqsEndpoint) : (sqsBaseUrl + sqsEndpoint);
    }

    public static String getOneSignalAppId(Integer entityId, Integer countryId) {
        return "b0d5b9f9-e728-4e79-8e9e-21b7068f3c7b";// LOCAL
    }


    public static String getOneSignalAPIAuthKey(Integer entityId, Integer countryId) {
        return "xxxxxxxxxxxxxxxxxxxxx";
    }

    public static String[] getOneSignalTemplate(OneSignalServiceImpl.NotificationTemplate template) {
        switch (template) {
            case WITH_ACCEPTED_OFFER: {
                return new String[] { "¡Estás a un paso de obtener tu crédito!", "Hola, cerraste Solven sin finalizar tu solicitud de préstamo. Solo debes ingresar tus documentos y estará listo ¡Vuelve haciendo click aquí!" };
            }
            case WITH_OFFER: {
                return new String[] { "¡Tenemos la mejor oferta para ti!", "¡Hola {{user_name | default: ''}}!, cerraste Solven sin haber seleccionado la mejor oferta de préstamo que encontramos para ti ¡Vuelve haciendo click aquí!" };
            }
            case CALL_INTERACTION_NO_RESPONSE: {
                return new String[] { "Solven desea contactarte por tu préstamo", "¡Hola!, intentamos comunicarnos contigo sin éxito. Por favor, mantente atento que te devolveremos la llamada para validar tu información." };
            }
            default:
                return new String[] { "¡Tenemos la mejor oferta para ti!", "¡Hola {{user_name | default: ''}}!, cerraste Solven sin haber seleccionado la mejor oferta de préstamo que encontramos para ti ¡Vuelve haciendo click aquí!" };// default with offer
        }
    }

    public enum WenanceProduct {
        WELP,
        MANGO
    }

    public static String wenanceLeadAPIUri(WenanceProduct product) throws Exception {
        if (!hostEnvIsProduction() && WenanceProduct.WELP == product) {
            return "https://api-sandbox.welp.com";
        } else if (hostEnvIsProduction() && WenanceProduct.WELP == product) {
            return "https://api.welp.com";
        } else if (!hostEnvIsProduction() && WenanceProduct.MANGO == product) {
            return "https://api-sandbox.holamango.com";
        } else if (hostEnvIsProduction() && WenanceProduct.MANGO == product) {
            return "https://api.holamango.com";
        } else {
            throw new Exception("Product " + product.name() + " has no API URI");
        }
    }

    public static String installmentOrdinalNumbersSuffix(int installment) {
        switch (installment) {
            case 1:
            case 3:
            case 11:
            case 13:
            case 21:
            case 23: {
                return "ra.";
            }
            case 2:
            case 12:
            case 22: {
                return "da.";
            }
            case 4:
            case 5:
            case 6:
            case 14:
            case 15:
            case 16:
            case 24: {
                return "ta.";
            }
            case 7:
            case 10:
            case 17:
            case 20: {
                return "ma.";
            }
            case 8:
            case 18: {
                return "va.";
            }
            case 9:
            case 19: {
                return "na.";
            }
            default: {
                return "";
            }
        }
    }

    public static List<AztecaGatewayBasePhone> getCobranzasPhoneNumber(){
        List<String> phoneNumbers = getPhoneNumbersForTest();
        List<AztecaGatewayBasePhone> aztecaGatewayBasePhones = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            AztecaGatewayBasePhone aztecaGatewayBasePhone = new AztecaGatewayBasePhone();
            aztecaGatewayBasePhone.setNombre("Prueba");
            aztecaGatewayBasePhone.setApPaterno("Azteca");
            aztecaGatewayBasePhone.setMontoCampania(500.00);
            aztecaGatewayBasePhone.setCelular1(phoneNumber);
            aztecaGatewayBasePhones.add(aztecaGatewayBasePhone);
        }
        return aztecaGatewayBasePhones;
    }

    public static List<String> getPhoneNumbersForTest(){
        return Arrays.asList("954448023","976364877","933145807","920334646","940987173","940987120","966711972","920334646","940283979","993346829");
    }

    public static String getAESEncrypt() {
        if (hostEnvIsProduction()) {
            return "xxxxxxxxxxxxxxxxxxxxx#";
        }
        return "xxxxxxxxxxxxxxxxxxxxx";
    }
}