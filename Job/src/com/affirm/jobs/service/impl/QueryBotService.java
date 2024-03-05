package com.affirm.jobs.service.impl;

import com.affirm.abaco.client.ERptaCredito;
import com.affirm.acceso.AccesoServiceCall;
import com.affirm.acceso.model.*;
import com.affirm.aws.elasticSearch.AWSElasticSearchClient;
import com.affirm.banbif.service.BanBifService;
import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.bantotalrest.model.authentication.AuthenticationResponse;
import com.affirm.bantotalrest.service.BTApiRestService;
import com.affirm.bitly.model.ShortLinkRequest;
import com.affirm.bitly.model.ShortLinkResult;
import com.affirm.bitly.service.BitlyService;
import com.affirm.cajasullana.CajaSullanaServiceCall;
import com.affirm.cajasullana.model.AdmisibilidadRequest;
import com.affirm.cajasullana.model.ValidarExperianRequest;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.entities.AutoplanService;
import com.affirm.common.service.entities.TarjetasPeruanasPrepagoService;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.common.service.impl.FileServiceImpl;
import com.affirm.common.service.question.Question165Service;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.compartamos.CompartamosServiceCall;
import com.affirm.compartamos.model.Cliente;
import com.affirm.compartamos.model.DocumentoIdentidad;
import com.affirm.compartamos.model.VariablePreEvaluacionRequest;
import com.affirm.compartamos.model.VariablePreEvaluacionResponse;
import com.affirm.efectiva.EfectivaClient;
import com.affirm.emailage.EmailAgeApi;
import com.affirm.fdlm.FdlmServiceCall;
import com.affirm.geocoding.model.GeocodingResponse;
import com.affirm.geocoding.model.GeocodingResult;
import com.affirm.geocoding.service.impl.GeocodingServiceImpl;
import com.affirm.heroku.model.Dyno;
import com.affirm.heroku.model.ServerStatus;
import com.affirm.intico.model.InticoConfirmSmsResponse;
import com.affirm.intico.model.InticoSendSmsResponse;
import com.affirm.intico.service.InticoClientService;
import com.affirm.jobs.webscrapper.*;
import com.affirm.marketingCampaign.dao.MarketingCampaignDAO;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import com.affirm.marketingCampaign.model.MarketingCampaignPersonInteraction;
import com.affirm.mati.model.CreateVerificationResponse;
import com.affirm.mati.service.MatiService;
import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.negativebase.service.NegativeBaseService;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import com.affirm.preapprovedbase.service.PreApprovedBaseService;
import com.affirm.qapaq.QapaqServiceCall;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.sendgrid.service.SendgridService;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.services.ecs.model.DescribeServicesResult;
import com.google.gson.Gson;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
/**
 * @author jrodriguez
 */

@Service
public class QueryBotService {
    private static final Logger logger = Logger.getLogger(QueryBotService.class);

    @Autowired
    private BotDAO botDao;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private OauthService oauthService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private CreditService creditService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private AccesoServiceCall accesoServiceCall;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private SendgridService sendgridService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private FileService fileService;
    @Autowired
    private AgreementService agreementService;
    @Autowired
    private CompartamosServiceCall compartamosServiceCall;
    @Autowired
    private EfectivaClient efectivaClient;
    @Autowired
    private TranslatorDAO translatorDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CajaSullanaServiceCall cajaSullanaServiceCall;
    @Autowired
    private BureauService bureauService;
    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private ServiceLogDAO serviceLogDao;
    @Autowired
    private EntityProductEvaluationProcessDAO entityProductEvaluationProcessDao;
    @Autowired
    private QapaqServiceCall qapaqServiceCall;
    @Autowired
    private TarjetasPeruanasPrepagoService tarjetasPeruanasPrepagoService;
    @Autowired
    private PreliminaryEvaluationService preliminaryEvaluationService;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDao;
    @Autowired
    private AutoplanService autoplanService;
    @Autowired
    private UserService userService;
    @Autowired
    private OfflineConversionService offlineConversionService;
    @Autowired
    private EvaluationBureauService evaluationBureauService;
    @Autowired
    private EvaluationDAO evaluationDao;
    @Autowired
    private InteractionDAO interactionDAO;
    @Autowired
    private AWSElasticSearchClient awsElasticSearchClient;
    @Autowired
    private FdlmServiceCall fdlmServiceCall;
    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private PreApprovedBaseService preApprovedBaseService;
    @Autowired
    private PreApprovedDAO preApprovedDAO;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private HerokuService herokuService;
    @Autowired
    private BanBifService banBifService;
    @Autowired
    private NegativeBaseProcessDAO negativeBaseProcessDAO;
    @Autowired
    private NegativeBaseService negativeBaseService;
    @Autowired
    private PSqlErrorMessageService pSqlErrorMessageService;
    @Autowired
    private PrismaService prismaService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private EvaluationCacheService evaluationCacheService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private MatiService matiService;
    @Autowired
    private BasesDAO basesDAO;
    @Autowired
    private InticoClientService inticoClientService;
    @Autowired
    private MarketingCampaignDAO marketingCampaignDAO;
    @Autowired
    private BitlyService bitlyService;
    @Autowired
    private AwsECSService awsECSService;
    @Autowired
    private BTApiRestService btApiRestService;
    @Autowired
    private ExternalDAO externalDAO;
    @Autowired
    private AwsLambdaService awsLambdaService;
    @Autowired
    private GeocodingServiceImpl geocodingService;

    public void runBot(int queryBotId) throws Throwable {
        QueryBot queryBot = botDao.getQueryBot(queryBotId);
        if (queryBot == null) {
            logger.error("The queryId " + queryBotId + " doesnt exists!");
            return;
        }
        if (queryBot.getStatusId() == QueryBot.STATUS_CANCELLED) {
            logger.error("The queryId " + queryBotId + " is cancelled");
            return;
        }
        registerRunning(queryBot);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        performAction(queryBot);
                    } catch (Throwable e) {
                        errorService.onError(e);
                        registerErrorMsgIfApprovalQueryBot(queryBot, e);
                        return false;
                    }
                    return true;
                }
            });

            long timeout = 5;
            if (queryBot.getBotId() == Bot.SENDGRID_LIST_MANAGEMENT)
                timeout = 5 * 60;
            else if (queryBot.getBotId() == Bot.SEND_SMS)
                timeout = 3 * 60;
            else if (queryBot.getBotId() == Bot.RE_EVALUATION_EMAIL_SENDER)
                timeout = 2 * 60;
            else if (queryBot.getBotId() == Bot.DAILY_INTERACTIONS)
                timeout = 2 * 60;
            else if (queryBot.getBotId() == Bot.REPORT_PROCESOR)
                timeout = 20;
            else if (queryBot.getBotId() == Bot.SEND_ACCESO_EXPIRATIO_INTERACTIONS)
                timeout = 15;
            else if (queryBot.getBotId() == Bot.BO_MANAGEMENT_FOLLOWUP_INTERACTION)
                timeout = 15;
            else if (queryBot.getBotId() == Bot.ENTITY_EVALUATION_PROCESS)
                timeout = 3;
            else if (queryBot.getBotId() == Bot.EVALUATION_PROCESS)
                timeout = 13;
            Boolean result = future.get(timeout, TimeUnit.MINUTES);
            future.cancel(true);

            if (!result) {
                registerFailure(queryBot);
                executorService.shutdownNow();
            }
        } catch (TimeoutException e) {
            registerFailure(queryBot);
            executorService.shutdownNow();
        } catch (Throwable e) {
            errorService.onError(e);
            registerFailure(queryBot);
            executorService.shutdownNow();
        }
    }

    private void registerErrorMsgIfApprovalQueryBot(QueryBot queryBot, Throwable throwable) {
        if (Bot.APPROVE_LOAN_APPLICATION == queryBot.getBotId()) {
            String errorMessage = "";
            if (throwable instanceof SqlErrorMessageException) {
                SqlErrorMessageException exception = (SqlErrorMessageException) throwable;
                errorMessage = exception.getMessageBody() != null ? exception.getMessageBody() : exception.getMessage();
            } else {
                Exception exception = new Exception(throwable);
                errorMessage = exception.getMessage();
            }

            Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
            Integer sysUserId = JsonUtil.getIntFromJson(queryBot.getParameters(), "sysUserId", null);
            Locale locale = new Gson().fromJson(queryBot.getParameters().get("locale").toString(), Locale.class);
            Integer auditTypeId = JsonUtil.getIntFromJson(queryBot.getParameters(), "auditTypeId", null);
            Integer userFileId = JsonUtil.getIntFromJson(queryBot.getParameters(), "userFileId", null);

            JSONObject params = new JSONObject();
            params.put("loanApplicationId", loanApplicationId);
            params.put("sysUserId", sysUserId);
            params.put("locale", new Gson().toJson(locale));
            params.put("auditTypeId", auditTypeId);
            params.put("userFileId", userFileId);
            params.put("errorMessage", errorMessage);

            botDao.updateParameters(queryBot.getId(), params);
        }
    }

    private void performAction(QueryBot queryBot) throws Throwable {
        Integer queryBotId = queryBot.getId();
        Bot bot = catalogService.getBot(queryBot.getBotId());
        Proxy proxy = null;
        if (bot.getCountryId() != null) {
            proxy = catalogService.getRandomProxyByCountry(bot.getCountryId());
            if (proxy != null) {
//                proxy.setBussy(true);
            }
        }
        if(bot != null && bot.getActive() != null && !bot.getActive()) return;

        switch (queryBot.getBotId()) {
            case Bot.SUNAT_BOT:
                SunatResult sunatResult = (SunatResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (SunatScrapper scrapper = new SunatScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    sunatResult = scrapper.getData(sunatResult.getInDocType(), sunatResult.getInDocNumber());
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + sunatResult);
                    if (sunatResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultSunat(queryBot.getId(), sunatResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
//            case Bot.RENIEC_BOT:
//                ReniecResult reniecResult = (ReniecResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
//                try (ReniecScrapper scrapper = new ReniecScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
//                    reniecResult = scrapper.getData(null, reniecResult.getInDocnumber());
//                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + reniecResult);
//                    if (reniecResult != null) {
//                        registerSucces(queryBot, proxy);
//                        botDao.updateQueryResultReniec(queryBot.getId(), reniecResult);
//                    } else {
//                        registerFailure(queryBot, proxy);
//                    }
//                }
//                break;
//            case Bot.ESSALUD_BOT:
//                EssaludResult essaludResult = (EssaludResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
//                try (EssaludScrapper scrapper = new EssaludScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
//                    essaludResult = scrapper.getData(essaludResult.getInDocType(), essaludResult.getInDocNumber());
//                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + essaludResult);
//                    if (essaludResult != null) {
//                        registerSucces(queryBot, proxy);
//                        botDao.updateQueryResultEssalud(queryBot.getId(), essaludResult);
//                    } else {
//                        registerFailure(queryBot, proxy);
//                    }
//                }
//                break;
            case Bot.SBS_B_BOT:
                SbsResult sbsResult = (SbsResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (SbsScrapper scrapper = new SbsScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    scrapper.getData(SbsScrapper.SBS_BANCA);
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + sbsResult);
                    if (sbsResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultSBS(queryBot.getId(), sbsResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
            case Bot.SBS_RATE:
                double sbsRateResult = (double) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (SbsScrapper scrapper = new SbsScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    scrapper.getData(SbsScrapper.SBS_BANCA);
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + sbsRateResult);
                    if (sbsRateResult > 0) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultSBSrate(sbsRateResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
//            case Bot.REDAM_BOT:
//                RedamResult redamResult = (RedamResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
//                try (RedamScrapper scrapper = new RedamScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
//                    redamResult = scrapper.getData(redamResult.getInDocType(), redamResult.getInDocNumber());
//                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + redamResult);
//                    if (redamResult != null) {
//                        registerSucces(queryBot, proxy);
//                        botDao.updateQueryResultRedam(queryBot.getId(), redamResult);
//                    } else {
//                        registerFailure(queryBot, proxy);
//                    }
//                }
//                break;
//            case Bot.SAT:
//                SatResult satResult = (SatResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
//                try (SatScrapper scrapper = new SatScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
//                    satResult = scrapper.getData(satResult.getInDocType(), satResult.getInDocNumber());
//                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + satResult);
//                    if (satResult != null) {
//                        registerSucces(queryBot, proxy);
//                        botDao.updateQueryResultSat(queryBot.getId(), satResult);
//                    } else {
//                        registerFailure(queryBot, proxy);
//                    }
//                }
//                break;
//            case Bot.SIS:
//                SisResult sisResult = (SisResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
//                try (SisScrapper scrapper = new SisScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
//                    sisResult = scrapper.getData(sisResult.getInDocType(), sisResult.getInDocNumber());
//                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + sisResult);
//                    if (sisResult != null) {
//                        registerSucces(queryBot, proxy);
//                        botDao.updateQueryResultSis(queryBot.getId(), sisResult);
//                    } else {
//                        registerFailure(queryBot, proxy);
//                    }
//                }
//                break;
            case Bot.MIGRACIONES:
                MigracionesResult migracionesResult = (MigracionesResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (MigracionesScrapper scrapper = new MigracionesScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    migracionesResult = scrapper.getData(migracionesResult.getInDocNumber(), migracionesResult.getInBirthday());
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + migracionesResult);
                    if (migracionesResult != null && migracionesResult.getFullName() != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultMigrations(queryBot.getId(), migracionesResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
            case Bot.SAT_PLATE:
                SatPlateResult satPlateResult = (SatPlateResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (SatScrapper scrapper = new SatScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    satPlateResult = scrapper.getTicketsByRegInLima(satPlateResult.getQueryId(), satPlateResult.getRegPlate());
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + satPlateResult);
                    if (satPlateResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultSATPlate(queryBot.getId(), satPlateResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
            case Bot.SOAT_RECORDS:
                SoatRecordsResult soatRecordsResult = (SoatRecordsResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (SoatRecordsScrapper scrapper = new SoatRecordsScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    soatRecordsResult = scrapper.getRecordsByReg(soatRecordsResult.getQueryId(), soatRecordsResult.getRegPlate());
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + soatRecordsResult);
                    if (soatRecordsResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultSoatRecords(queryBot.getId(), soatRecordsResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
            /*case Bot.CLARO:
            case Bot.MOVISTAR:
            case Bot.BITEL:
            case Bot.ENTEL:
                LineaResult lineaResult = (LineaResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());

                if (queryBot.getBotId().equals(Bot.MOVISTAR)) {
                    proxy = null;
                }

                try (LineaScrapper scrapper = new LineaScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    String phoneOperator = null;
                    switch (queryBot.getBotId()) {
                        case Bot.CLARO:
                            phoneOperator = PhoneContractOperator.CLARO;
                            break;
                        case Bot.MOVISTAR:
                            phoneOperator = PhoneContractOperator.MOVISTAR;
                            break;
                        case Bot.BITEL:
                            phoneOperator = PhoneContractOperator.BITEL;
                            break;
                        case Bot.ENTEL:
                            phoneOperator = PhoneContractOperator.ENTEL;
                            break;
                    }
                    lineaResult = scrapper.getData(lineaResult.getInDocType(), lineaResult.getInDocNumber(), phoneOperator);
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + lineaResult);
                    if (lineaResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultPhoneContracts(queryBot.getId(), lineaResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;*/
            case Bot.BUFFER:
                List<BufferTransaction> processedTransactions = botDao.runBufferTransactions();
                if (processedTransactions != null) {
                    for (BufferTransaction t : processedTransactions) {

                        Credit credit = creditDao.getCreditByID(t.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), credit.getPersonId(), false);
                        User user = userDao.getUser(person.getUserId());

                        switch (t.getBufferTransactionType().getId()) {
                            case BufferTransactionType.ACCREDIT_PAYMENT:
                                creditService.sendAccreditedPaymentEmailAndSms(credit, person, user);
                                break;
                            case BufferTransactionType.DISBURSEMENT_CONFIRMATION:
                                creditService.sendDisbursementConfirmationEmailAndSms(credit, person, user, null);
                                loanNotifierService.notifyDisbursement(creditDAO.getCreditByID(credit.getId(), Configuration.getDefaultLocale(), false, Credit.class).getLoanApplicationId(), Configuration.getDefaultLocale());
                                offlineConversionService.sendOfflineConversion(credit);
                                break;
                        }
                    }
                }
                registerSucces(queryBot);
                break;
            case Bot.MANAGEMENT_SCHEDULE:
                logger.debug("RUN MANAGEMENT_SCHEDULE");
                botDao.proccessManagementScheduleAll();
                registerSucces(queryBot);
                break;
            case Bot.DAILY_INTERACTIONS: {
                logger.debug("RUN DAILY_INTERACTIONS");
                List<PersonInteraction> personInteractionList = botDao.sendDailyInteractions();
                for (PersonInteraction personInteraction : personInteractionList) {
                    try {
                        logger.debug("personInteraction loop " + personInteraction.getDestination());

                        boolean sendMessage = true;
                        if (personInteraction.getTemplate() != null) {
                            personInteraction.getInteractionContent().setTemplate(personInteraction.getTemplate());
                        }

                        JSONObject json = null;
                        if (personInteraction.getLoanApplicationId() != null) {
                            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(personInteraction.getLoanApplicationId(), Configuration.getDefaultLocale());
                            if (loanApplication.getEntityId() == null || (loanApplication.getEntityId() != null && Arrays.asList(Entity.BANCO_DEL_SOL, null, Entity.AFFIRM).contains(loanApplication.getEntityId()))) {
                                sendMessage = false;
                            }
                            String link =
                                    (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) ? loanApplicationService.generateLoanApplicationLinkEntityMailing(loanApplication, "?utm_source=mailing&utm_medium=email_mkt&utm_campaign=fup_banbif")
                                            : loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
                            if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && Arrays.asList(ProductCategory.CONSUMO,ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId())){
                                if(loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) link = "https://www.alfinbanco.pe/prestamos";
                                else if(loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA) link = "https://www.alfinbanco.pe/ahorro";
                            }
                            json = new JSONObject("{\"LINK\": \"" + link + "\"}");
                            if (loanApplication != null && loanApplication.getCredit() != null && loanApplication.getCredit()) {
                                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                                if (credit != null)
                                    json.put("CREDIT_AMOUNT", credit.getAmount() != null ? utilService.customDoubleFormat(credit.getAmount(), 0) : "");
                            }
                            else if (loanApplication != null){
                                try{
                                    Integer maxPreApprovedAmount = loanApplicationDao.getMaxPreapprovedAmount(loanApplication.getId());
                                    if(maxPreApprovedAmount != null) json.put("CREDIT_AMOUNT", maxPreApprovedAmount.toString());
                                }
                                catch (Exception e){}
                            }
                            User user = userDao.getUser(loanApplication.getUserId());
                            Person person = personDao.getPerson(user.getPersonId(), false, Configuration.getDefaultLocale());
                            json.put("CLIENT_NAME", person.getFirstName());
                        }

                        Map<String,String> templateVars = new HashMap<>();
                        if( json != null){
                            for (String key : json.keySet()) {
                                Object keyvalue = json.get(key);
                                templateVars.put(key, String.valueOf(keyvalue));
                            }
                        }

                        if (sendMessage)
                            interactionService.sendPersonInteraction(personInteraction, json, null, templateVars);
                    } catch (Exception ex) {
                        errorService.onError(ex);
                    }
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.EQUIFAX_BUREAU: {

                try {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(queryBot.getParameters().getInt("loanApplicationId"), Configuration.getDefaultLocale());
                    Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                    bureauService.runBureau(loanApplication, person, Bureau.EQUIFAX);

//                    loanApplicationDao.executeEvaluationBureau(queryBot.getParameters().getInt("loanApplicationId"));

                    registerSucces(queryBot);
                } catch (Throwable exp) {
//                    loanApplicationDao.executeEvaluationBureau(queryBot.getParameters().getInt("loanApplicationId"));
                    throw exp;
                }
                break;
            }
            case Bot.EMAILAGE: {
                logger.debug("ENTRO EMAILAGE sacando params");
                Integer userIdEmail = queryBot.getParameters().getInt("userId");
                String emailageEmail = queryBot.getParameters().getString("email");
                String emailageIP = queryBot.getParameters().getString("ip");

                logger.debug("EMAILAGE validacion");
                EmailAgeApi emailAgeApi = new EmailAgeApi();
                String emailIpResult = emailAgeApi.validEmailIpJson(emailageEmail, emailageIP, null).trim();

                JSONObject json = new JSONObject();
                json.put("email", JsonUtil.sanitizedJSON(emailIpResult));

                userDao.registerEmailAge(userIdEmail, json.toString());

                registerSucces(queryBot);
                break;
            }
            case Bot.GOOGLE_USER_DATA:
            case Bot.WINDOWS_USER_DATA:
            case Bot.YAHOO_USER_DATA: {
                int userId = queryBot.getParameters().getInt("userId");

                UserNetworkToken userMailToken = null;
                JSONObject jsonContacts = null;

                // Get the User data
                if (queryBot.getBotId() == Bot.GOOGLE_USER_DATA) {
                    userMailToken = userDao.getUserNetworkTokenByProvider(userId, 'G');
                    jsonContacts = oauthService.requestUserData(Configuration.OauthNetwork.GOOGLE, userMailToken.getAccessToken(), "https://www.google.com/m8/feeds/contacts/default/full?alt=json");
                } else if (queryBot.getBotId() == Bot.WINDOWS_USER_DATA) {
                    userMailToken = userDao.getUserNetworkTokenByProvider(userId, 'W');
                    jsonContacts = oauthService.requestUserData(Configuration.OauthNetwork.WINDOWS, userMailToken.getAccessToken(), "https://apis.live.net/v5.0/me/contacts");
                } else if (queryBot.getBotId() == Bot.YAHOO_USER_DATA) {
                    userMailToken = userDao.getUserNetworkTokenByProvider(userId, 'Y');
                    jsonContacts = oauthService.requestUserData(Configuration.OauthNetwork.YAHOO, userMailToken.getAccessToken(), "https://query.yahooapis.com/v1/yql?q=SELECT%20*%20from%20social.contacts%20WHERE%20guid%3Dme&format=json");
                }

                userDao.updateNetworkContacts(userMailToken.getId(), jsonContacts.toString());

                registerSucces(queryBot);
                break;
            }
            case Bot.VIRGIN: {
                logger.debug("ENTRO VIRGIN sacando params");
                Integer docType = queryBot.getParameters().getInt("docType");
                String docNumber = queryBot.getParameters().getString("docNumber");
                LineaResult lineaResultV;

                if (docType == IdentityDocumentType.DNI) {
                    JSONObject response = JsonUtil.postJSONObjectFromUrl(
                            "https://www.virginmobile.pe/getMobileNumbers", "{\"dni\": \"" + docNumber + "\"}",
                            Optional.of("text/plain"));

                    lineaResultV = LineaResult.fromVirgin(response);
                } else {
                    logger.debug("Virgin only allows search by DNI");
                    lineaResultV = new LineaResult();
                }
                lineaResultV.setInDocNumber(docNumber);
                lineaResultV.setInDocType(docType);

                if (lineaResultV != null) {
                    registerSucces(queryBot, proxy);
                    botDao.updateQueryResultPhoneContracts(queryBot.getId(), lineaResultV);
                } else {
                    registerFailure(queryBot, proxy);
                }
                break;
            }
            case Bot.ACCESO_OFFERS: {
                if (catalogService.getEntity(Entity.ACCESO).getActive()) {
                    Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
                    Double downPayment = JsonUtil.getDoubleFromJson(queryBot.getParameters(), "downPayment", null);
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());

                    // Call cotizador
                    List<Oferta> accesoOffers = accesoServiceCall.callCotizar(new Cotizador(translatorDao,
                            Integer.parseInt(loanApplication.getEntityApplicationCode()),
                            loanApplication.getVehicle(),
                            new Date(), downPayment, loanApplication.getInstallments(), loanApplication.getVehicle().getVehicleDetails().get(0).getEntityPolicy()), loanApplicationId, true, 0);

                    Double changeRate = 0.0;

                    LoanApplicationEvaluation loanApplicationEvaluation = loanApplicationDao.getEvaluations(loanApplicationId, Configuration.getDefaultLocale()).stream().filter(e -> e.getEntityId().equals(Entity.ACCESO)).findFirst().orElse(null);

                    for (int i = 0; i < accesoOffers.size(); i++) {

                        // Insert offer in the DB
                        Oferta oferta = accesoOffers.get(i);
                        LoanOffer offer = oferta.toLoanOffer(catalogService);
                        offer.setEntityId(Entity.ACCESO);
                        offer.setProduct(catalogService.getProduct(Product.AUTOS));
                        offer.setLoanOfferOrder(i + 1);
                        int loanOfferId = loanApplicationDao.insertLoanOffer(loanApplication.getId(), loanApplication.getPersonId(), offer, loanApplicationEvaluation.getEntityProductParameterId());

                        // Select the offer in Acceso to get the schedule
                        accesoServiceCall.callCotizar(new Cotizador(translatorDao,
                                Integer.parseInt(loanApplication.getEntityApplicationCode()),
                                loanApplication.getVehicle(),
                                new Date(), oferta.getCuotaInicial(), oferta.getNumCuotas(), loanApplication.getVehicle().getVehicleDetails().get(0).getEntityPolicy()), loanApplicationId, false, 0);

                        // Get the Schedule and save it
                        List<Cronograma> cronogramas = accesoServiceCall.callCronograma(Integer.parseInt(loanApplication.getEntityApplicationCode()), loanApplicationId, 0);
                        List<OriginalSchedule> schedule = new ArrayList<>();
                        for (Cronograma cronograma : cronogramas) {
                            schedule.add(cronograma.toOriginalSchedule());
                        }
                        String jsonCronogramas = new Gson().toJson(schedule);
                        loanApplicationDao.insertLoanOfferSchedule(loanOfferId, jsonCronogramas);

                        changeRate = oferta.getTipoCambio();
                    }

                    loanApplicationDao.updateDebtnessValues(loanApplicationId);

                    if (changeRate != 0.0)
                        loanApplicationDao.updateLoanApplicationChangeRate(loanApplicationId, changeRate, Entity.ACCESO);
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.ACCESO_SIGNATURE_STATUS: {
                if (catalogService.getEntity(Entity.ACCESO).getActive()) {
                    // Call cotizador
                    List<EstadoFirma> statuses = accesoServiceCall.callEstadoFirmas(0);
                    if (statuses != null) {
                        for (EstadoFirma status : statuses) {
                            if (status.getResultadoVerificacion() != null && status.getResultadoVerificacion() == 1) {
                                LoanApplication loanApplication = loanApplicationDao.getLoanApplicationByEntityApplicationCode(status.getNroExpediente() + "", Configuration.getDefaultLocale());
                                Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                                creditDao.updateSignedOnEntity(credit.getId(), true);
                                creditDao.updateSignatureDate(credit.getId(), status.getFechaVerificacion());
                                creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED, 0);
                            }
                        }
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.ACCESO_DISPATCH: {
                if (catalogService.getEntity(Entity.ACCESO).getActive()) {
                    List<Expediente> expedientes = accesoServiceCall.callExpedientesDespachados(0);
                    if (expedientes != null) {
                        for (Expediente expediente : expedientes) {
                            LoanApplication loanApplication = loanApplicationDao.getLoanApplicationByEntityApplicationCode(expediente.getNroExpediente() + "", Configuration.getDefaultLocale());
                            Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                            if (!credit.getStatus().equals(CreditStatus.ORIGINATED_DISBURSED)) {
                                creditDao.updateDisbursmentInInEntity(credit.getId(), null);
                                creditDao.updateDisbursementDate(credit.getId(), expediente.getFechaDespacho());
                                creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED_DISBURSED, 0);
                                loanApplicationService.sendVehicleDisbursedInteraction(loanApplication.getUserId(), loanApplication.getId(), Configuration.getDefaultLocale());
                            }
                        }
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.ACCESO_VEHICLE_CATALOG: {
                if (catalogService.getEntity(Entity.ACCESO).getActive()) {
                    List<CatalogoVehicular> vehicles = accesoServiceCall.callCatalogoVehicular(0);
                    if (vehicles != null) {
                        botDao.registerACVehicles(new Gson().toJson(vehicles));
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.RIPLEY_REPORT_DAILY_SENDER: {
                if (catalogService.getEntity(Entity.RIPLEY).getActive()) {
                    byte[] reportExcel = reportsService.createRipleyReportExcel();
                    List<Attachment> attachments = null;
                    if (reportExcel != null) {
                        Attachment attachment = new Attachment();
                        attachment.setFilename("Ripley_reporte_sef_abono.xlsx");
                        attachment.setContent(new Base64().encodeAsString(reportExcel));
                        attachments = Arrays.asList(attachment);
                    }
                    awsSesEmailService.sendRawEmail(
                            null,
                            "procesos@solven.pe",
                            null,
                            "ripley@solven.pe",
                            null,
                            "Reporte diario",
                            "Reporte diario de creditos generados no desembolsados",
                            null,
                            null, attachments, null, null, null);
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.SENDGRID_LIST_MANAGEMENT: {
                sendgridService.processContactList();
                registerSucces(queryBot);
                break;
            }
            case Bot.AFIP: {
                AfipResult afipResult = (AfipResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (AfipScrapper scrapper = new AfipScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    afipResult = scrapper.getData(afipResult.getInDocumentType(), afipResult.getInDocumentNumber());
                    if (afipResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultAFIP(queryBot.getId(), afipResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
            }
            case Bot.ANSES: {
                AnsesResult ansesResult = (AnsesResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (AnsesScrapper scrapper = new AnsesScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT)) {
                    ansesResult = scrapper.getData(ansesResult.getInDocumentType(), ansesResult.getInDocumentNumber());
                    if (ansesResult != null) {
                        registerSucces(queryBot);
                        botDao.updateQueryResultANSES(queryBot.getId(), ansesResult);
                    } else {
                        registerFailure(queryBot);
                    }
                }
                break;
            }
            case Bot.BCRA: {
                BcraResult bcraResult = (BcraResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (BcraScrapper scrapper = new BcraScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT)) {
                    bcraResult = scrapper.getData(bcraResult.getInDocumentType(), bcraResult.getInDocumentNumber());
                    bcraResult.addDepthToHistorical();
                    if (bcraResult != null) {
                        registerSucces(queryBot);
                        botDao.updateQueryResultBCRA(queryBot.getId(), bcraResult);
                    } else {
                        registerFailure(queryBot);
                    }
                }
                break;
            }
            case Bot.EXCHANGE: {
                exchangeRateService.registerExchangeRate();
                registerSucces(queryBot);
                break;
            }
            case Bot.ACCESO_SIGNATURE_PENDING: {
                if (catalogService.getEntity(Entity.ACCESO).getActive()) {
                    List<PendienteFirma> pendings = accesoServiceCall.callPendienteFirma(0);
                    if (pendings != null)
                        for (PendienteFirma pending : pendings) {
                            LoanApplication loanApplication = loanApplicationDao.getLoanApplicationByEntityApplicationCode(pending.getNroExpediente() + "", Configuration.getDefaultLocale());
                            if (loanApplication != null) {
                                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                                if (credit.getSubStatus() == null || credit.getSubStatus().getId() == CreditSubStatus.ACCESO_WAITING_FOR_CAR_DEALERSHIP) {
                                    creditDAO.updateCreditSubStatus(loanApplication.getCreditId(), CreditSubStatus.ACCESO_SCHEDULE_SIGNATURE);
                                    loanApplicationService.sendSchedulePhysicalSignatureInteraction(loanApplication.getUserId(), loanApplication.getId(), Configuration.getDefaultLocale());
                                }
                            }
                        }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.EVALUATION_PROCESS: {
                Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                LoanApplicationEvaluationsProcess evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());

                // Preevaluation block
                if (evaluationProcess.getReadyForPreEvaluation() != null && evaluationProcess.getReadyForPreEvaluation()) {

                    ScheduledExecutorService delayedPreliminaryScheduleExecutor = null;
                    ScheduledFuture delayedPreliminarySchedule = null;

                    // If its the first time
                    if (evaluationProcess.getPreEvaluationStatus() == null) {

                        // Initialize the preliminary timer, so if it passes the spected time, the client should still pass
                        delayedPreliminaryScheduleExecutor = Executors.newSingleThreadScheduledExecutor();
                        delayedPreliminarySchedule = delayedPreliminaryScheduleExecutor.schedule(() -> {
                            LoanApplicationEvaluationsProcess evaluationProcessTemp = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                            if (evaluationProcessTemp.getPreEvaluationStatus() != null && evaluationProcessTemp.getPreEvaluationStatus() == LoanApplicationEvaluationsProcess.STATUS_RUNNING)
                                loanApplicationDao.updateLoanApplicationEvaluationProcessPreEvaluationStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_RUNNING_DELAYED);
                        }, 10, TimeUnit.SECONDS);

                        // Set the status to running
                        loanApplicationDao.updateLoanApplicationEvaluationProcessPreEvaluationStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_RUNNING);
                        evaluationProcess.setPreEvaluationStatus(LoanApplicationEvaluationsProcess.STATUS_RUNNING);

                        // Synthesized block: IF its from an entity that doesnt need it for its pre evaluation, run it as async process. Else, run it now
                        if (loanApplication.getEntityId() != null && Arrays.asList(Entity.BANBIF, Entity.AZTECA).contains(loanApplication.getEntityId())) {
                            webscrapperService.callRunSynthesized(person.getDocumentNumber(), loanApplicationId);
                        } else {
                            // If the synthesizer hasnt run, run it
                            if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU && evaluationProcess.getSynthesizedStatus() == null) {
                                loanApplicationDao.generateSynthesized(person.getDocumentNumber());
                                loanApplicationDao.updateEvaluationProcessSynthesizedStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_FINISHED);
                                evaluationProcess.setSynthesizedStatus(LoanApplicationEvaluationsProcess.STATUS_FINISHED);
                            }

                            // If the synthesizer hasnt finished yet, retry every sec. for 10 sec. If stil doesnt finish, run the synthesized.
                            if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU && evaluationProcess.getSynthesizedStatus() != LoanApplicationEvaluationsProcess.STATUS_FINISHED) {
                                int count = 0;
                                while (count < 10) {
                                    Thread.sleep(1000);
                                    evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                                    if (evaluationProcess.getSynthesizedStatus() == LoanApplicationEvaluationsProcess.STATUS_FINISHED) {
                                        count = 99;
                                    }
                                    count++;
                                }

                                evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                                if (evaluationProcess.getSynthesizedStatus() != LoanApplicationEvaluationsProcess.STATUS_FINISHED) {
                                    loanApplicationDao.generateSynthesized(person.getDocumentNumber());
                                    loanApplicationDao.updateEvaluationProcessSynthesizedStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_FINISHED);
                                    evaluationProcess.setSynthesizedStatus(LoanApplicationEvaluationsProcess.STATUS_FINISHED);
                                }
                            }
                        }


                        // If Argentina, run bots required for the preevaluation
                        // False: Temporary solution
                        if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {

                            // Run the ANSES and BCRA bot
                            webscrapperService.callANSESBot(person.getDocumentType().getId(), person.getDocumentNumber(), loanApplication.getUserId());
                            webscrapperService.callBCRABot(person.getDocumentType().getId(), person.getDocumentNumber(), loanApplication.getUserId());
//
//                            // Initialize the executors
//                            List<CompletableFuture> completableFutures = new ArrayList<>();
//                            ExecutorService executor = Executors.newFixedThreadPool(3);
//                            ExecutorService completableExecutor = Executors.newFixedThreadPool(3);
//
//                            // Run the scrappers
//                            Proxy proxyToUse = catalogService.getRandomProxyByCountry(CountryParam.COUNTRY_ARGENTINA);
////                            if (personDao.getAfipResult(person.getId()) == null) {
////                                runFutureTask(new FutureTask() {
////                                    @Override
////                                    public void runTask() throws Exception {
////                                        QueryBot scrapperBot = null;
////                                        try (AfipScrapper scrapper = new AfipScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxyToUse)) {
////                                            scrapperBot = botDao.registerQuery(Bot.AFIP, QueryBot.STATUS_QUEUE, new Date(), null, loanApplication.getUserId());
////                                            botDao.registerQueryResultAFIP(scrapperBot.getId(), person.getDocumentType().getId(), person.getDocumentNumber());
////
////                                            AfipResult afipResult = scrapper.getData(person.getDocumentType().getId(), person.getDocumentNumber());
////                                            if (afipResult != null) {
////                                                registerSucces(scrapperBot);
////                                                botDao.updateQueryResultAFIP(scrapperBot.getId(), afipResult);
////                                            } else {
////                                                registerFailure(scrapperBot);
////                                            }
////                                        } catch (Throwable th) {
////                                            if (scrapperBot != null)
////                                                registerFailure(scrapperBot);
////                                            throw th;
////                                        }
////                                    }
////
////                                    @Override
////                                    public void onException() throws Exception {
////                                    }
////                                }, completableFutures, executor, completableExecutor, 1, 150, TimeUnit.SECONDS);
////                            }
////                            if (personDao.getAnsesResult(person.getId()) == null) {
//                                runFutureTask(new FutureTask() {
//                                    @Override
//                                    public void runTask() throws Exception {
//                                        QueryBot scrapperBot = null;
//                                        try (AnsesScrapper scrapper = new AnsesScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT)) {
//                                            scrapperBot = botDao.registerQuery(Bot.ANSES, QueryBot.STATUS_QUEUE, new Date(), null, loanApplication.getUserId());
//                                            botDao.registerQueryResultANSES(scrapperBot.getId(), person.getDocumentType().getId(), person.getDocumentNumber());
//
//                                            AnsesResult ansesResult = scrapper.getData(person.getDocumentType().getId(), person.getDocumentNumber());
//                                            if (ansesResult != null) {
//                                                registerSucces(scrapperBot);
//                                                botDao.updateQueryResultANSES(scrapperBot.getId(), ansesResult);
//                                            } else {
//                                                registerFailure(scrapperBot);
//                                            }
//                                        } catch (Throwable th) {
//                                            if (scrapperBot != null)
//                                                registerFailure(scrapperBot);
//                                            throw th;
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onException() throws Exception {
//
//                                    }
//                                }, completableFutures, executor, completableExecutor, 1, 150, TimeUnit.SECONDS);
////                            }
////                            if (personDao.getBcraResult(person.getId()) == null) {
//                                runFutureTask(new FutureTask() {
//                                    @Override
//                                    public void runTask() throws Exception {
//                                        QueryBot scrapperBot = null;
//                                        try (BcraScrapper scrapper = new BcraScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT)) {
//                                            scrapperBot = botDao.registerQuery(Bot.BCRA, QueryBot.STATUS_QUEUE, new Date(), null, loanApplication.getUserId());
//                                            botDao.registerQueryResultBCRA(scrapperBot.getId(), person.getDocumentType().getId(), person.getDocumentNumber());
//
//                                            BcraResult bcraResult = scrapper.getData(person.getDocumentType().getId(), person.getDocumentNumber());
//                                            if (bcraResult != null) {
//                                                registerSucces(scrapperBot);
//                                                botDao.updateQueryResultBCRA(scrapperBot.getId(), bcraResult);
//                                            } else {
//                                                registerFailure(scrapperBot);
//                                            }
//                                        } catch (Throwable th) {
//                                            if (scrapperBot != null)
//                                                registerFailure(scrapperBot);
//                                            throw th;
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onException() throws Exception {
//                                        errorService.sendErrorCriticSlack("Fallo corrida de scrapper BCRA para cuit " + person.getDocumentNumber());
//                                    }
//                                }, completableFutures, executor, completableExecutor, 3, 12, TimeUnit.MINUTES);
////                            }
//
//                            // Wait for all the async threads
//                            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
//                            completableExecutor.shutdownNow();
//                            executor.shutdownNow();
                        }

                        // Start the preliminary evaluation
                        loanApplicationDao.startPreliminaryEvaluation(loanApplication.getId());
                    }

                    // Go to the entity WS if it need to
                    List<EntityProductEvaluationsProcess> entityProductEvaluationsProcesses =
                            loanApplicationDao.getEntityProductEvaluationProceses(loanApplicationId)
                                    .stream()
                                    .filter(e -> e.getReadyForProcess() && Arrays.asList(e.STATUS_RUNNING_DELAYED, null).contains(e.getPreEvaluationStatus()))
                                    .collect(Collectors.toList());
                    for (EntityProductEvaluationsProcess process : entityProductEvaluationsProcesses) {
                        // run bot
                        loanApplicationService.runEntityEvaluationBot(loanApplicationId, process.getEntityId(), process.getProductId(), true, false);
                    }

                    // Cancel and shutdown the schedule initialized in the begining
                    if (delayedPreliminarySchedule != null) {
                        delayedPreliminarySchedule.get();
                        delayedPreliminarySchedule.cancel(true);
                        delayedPreliminaryScheduleExecutor.shutdownNow();
                    }
                }

                // Evaluation block
                evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                if (evaluationProcess.getReadyForEvaluation() != null && evaluationProcess.getReadyForEvaluation()) {

                    // If its the first time
                    if (evaluationProcess.getEvaluationStatus() == null) {

                        // Change the status of the evaluation
                        loanApplicationDao.updateLoanApplicationEvaluationProcessEvaluationStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_RUNNING);
                        evaluationProcess.setEvaluationStatus(LoanApplicationEvaluationsProcess.STATUS_RUNNING);

                        // Update the evaluation start date
                        loanApplicationDao.updateLoanApplicationEvaluationStartDate(loanApplication.getId(), new Date());

                        // Update person information by the employee
                        List<Employee> agreementEmployees = employeeService.getEmployeesByEmailOrDocumentByProduct(null, person.getDocumentType().getId(), person.getDocumentNumber(), Product.AGREEMENT, Configuration.getDefaultLocale());
                        if (agreementEmployees != null && loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) {
                            for (Employee employee : agreementEmployees.stream().filter(e -> e.getActive()).collect(Collectors.toList())) {
                                personDao.updateEmployeePersonOnly(employee.getId(), person.getId());
                            }
                        }
                        List<Employee> consolidationEmployees = employeeService.getEmployeesByEmailOrDocumentByProduct(null, person.getDocumentType().getId(), person.getDocumentNumber(), Product.DEBT_CONSOLIDATION, Configuration.getDefaultLocale());
                        if (consolidationEmployees != null/* && loanApplication.getProductCategoryId() == ProductCategory.CONSOLIDACION*/) {
                            for (Employee employee : consolidationEmployees.stream().filter(e -> e.getActive()).collect(Collectors.toList())) {
                                personDao.updateEmployeePerson(employee.getId(), person.getId());
                            }
                        }
                    }

                    // Run the bots for the preliminary evaluations that are approved
                    List<EntityProductEvaluationsProcess> entityProductEvaluationsProcesses =
                            loanApplicationDao.getEntityProductEvaluationProceses(loanApplicationId)
                                    .stream()
                                    .filter(e -> e.getPreliminaryEvaluationApproved() != null && e.getPreliminaryEvaluationApproved() && Arrays.asList(e.STATUS_RUNNING_DELAYED, null).contains(e.getEvaluationStatus()))
                                    .collect(Collectors.toList());
                    for (EntityProductEvaluationsProcess process : entityProductEvaluationsProcesses) {
                        // run bot
                        loanApplicationService.runEntityEvaluationBot(loanApplicationId, process.getEntityId(), process.getProductId(), false, false);
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.ENTITY_EVALUATION_PROCESS: {
                Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
                Integer entityId = JsonUtil.getIntFromJson(queryBot.getParameters(), "entityId", null);
                Integer productId = JsonUtil.getIntFromJson(queryBot.getParameters(), "productId", null);

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), true);
                LoanApplicationEvaluationsProcess evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                EntityProductEvaluationsProcess entityProductEvaluationsProcess = loanApplicationService.getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);

                if (evaluationProcess.getReadyForPreEvaluation() != null && evaluationProcess.getReadyForPreEvaluation()) {

                    // Only run if its the first time or if is delayed
                    if (Arrays.asList(null, EntityProductEvaluationsProcess.STATUS_RUNNING_DELAYED).contains(entityProductEvaluationsProcess.getPreEvaluationStatus())) {

                        // If its the first time, update to Running
                        if (entityProductEvaluationsProcess.getPreEvaluationStatus() == null) {
                            updatePreliminaryEvaluationStatus(EntityProductEvaluationsProcess.STATUS_RUNNING, loanApplicationId, entityId, productId);
                            entityProductEvaluationsProcess.setPreEvaluationStatus(EntityProductEvaluationsProcess.STATUS_RUNNING);
                        }

                        // if the entityProductProcess doesnt have an result yet, run the WS
                        if (entityProductEvaluationsProcess.getPreliminaryEvaluationApproved() == null) {

                            // Initialize the executors
                            List<CompletableFuture> completableFutures = new ArrayList<>();
                            ExecutorService executor = Executors.newFixedThreadPool(1);
                            ExecutorService completableExecutor = Executors.newFixedThreadPool(1);

                            // Run the WebService(s) for the entity/product
                            runFutureTask(new FutureTask() {
                                @Override
                                public void runTask() throws Exception {
                                    if (entityId == Entity.COMPARTAMOS) {

                                        VariablePreEvaluacionResponse response = compartamosServiceCall.callTraerVariablesPreevaluacion(
                                                new VariablePreEvaluacionRequest(person, translatorDao),
                                                loanApplication.getId());

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    response != null ? EntityProductEvaluationsProcess.STATUS_SUCCESS : EntityProductEvaluationsProcess.STATUS_FAILED,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.EFECTIVA) {

                                        efectivaClient.processEfectiva(person.getDocumentNumber(), loanApplication.getId());
                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.ACCESO && productId == Product.AUTOS) {

                                        Date accesoResultDate = accesoServiceCall.callConsultarExpediente(person.getDocumentNumber(), loanApplication.getId(), 0);
                                        if (accesoResultDate != null) {
                                            loanApplicationDao.updateEntityApplicationExpirationDate(loanApplication.getId(), accesoResultDate);
                                        }

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    }else if (entityId == Entity.ACCESO && productId == Product.TRADITIONAL) {

                                        EvaluacionGenerica.Prospecto prospecto = new EvaluacionGenerica.Prospecto();
                                        prospecto.setTipoDocumento(person.getDocumentType().getId());
                                        prospecto.setDocumento(person.getDocumentNumber());

                                        EvaluacionGenerica evaluacionGenerica = new EvaluacionGenerica();
                                        evaluacionGenerica.setProspecto(prospecto);

                                         accesoServiceCall.callEvaluacionGenericaAsync(evaluacionGenerica, loanApplication.getId(), 0);


                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.CAJASULLANA && productId == 1) {

                                        cajaSullanaServiceCall.callConsultarAdmisibilidad(new AdmisibilidadRequest(translatorDao, person), loanApplication.getId());

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);
                                    } else if (entityId == Entity.QAPAQ) {

                                        qapaqServiceCall.callGenerateLoanOffers(loanApplication);

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.BANCO_DEL_SOL) {

                                        bancoDelSolService.updateBaseValuesInLoan(loanApplication, person.getDocumentNumber());

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.FINANSOL) {
                                        // Check if the document is in the pre aproved base. If true, then register in the table to use it in the offer generation
                                        FinansolPreApprovedBase finansolBase = rccDao.getFinansolPreApprovedBase(person.getDocumentType().getName(), person.getDocumentNumber());
                                        if (finansolBase != null && finansolBase.getMaxAmount() != null) {
                                            personDao.registerPreApprovedBaseByEntityProductParameter(Entity.FINANSOL, Product.TRADITIONAL, person.getDocumentType().getId(), person.getDocumentNumber(),
                                                    finansolBase.getMaxAmount() * 1.0, finansolBase.getMaxInstallments(), finansolBase.getTea(), null, null, null, null, null, "{" + EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO + "}");
                                        }

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.BANBIF) {

                                        banBifService.updateBaseValuesInLoan(loanApplication, person.getDocumentNumber());

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else if (entityId == Entity.PRISMA) {

                                        prismaService.updateBaseValuesInLoan(loanApplication, person);

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);

                                    } else {

                                        if (isStillRunning())
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                    loanApplicationId, entityId, productId);
                                    }
                                }

                                @Override
                                public void onException() throws Exception {
                                    switch (entityId) {
                                        default: {
                                            updatePreliminaryEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_FAILED,
                                                    loanApplicationId, entityId, productId);
                                            break;
                                        }
                                    }
                                }
                            }, completableFutures, executor, completableExecutor, 2, 30, TimeUnit.SECONDS);

                            // Wait for the async thread
                            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
                            completableExecutor.shutdownNow();
                            executor.shutdownNow();

                        } else {

                            updatePreliminaryEvaluationStatus(
                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                    loanApplicationId, entityId, productId);
                        }

                        // If the entityProductProcess is Failed, rety the bot if can
                        entityProductEvaluationsProcess = loanApplicationService.getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);
                        if (entityProductEvaluationsProcess.getPreEvaluationStatus() == EntityProductEvaluationsProcess.STATUS_FAILED) {
                            if (retryLoanApplicationPreliminaryEvaluation(loanApplication.getId(), entityId, productId)) {
                                registerSucces(queryBot);
                                break;
                            }
                        }

                        // Execute preliminary evaluations
                        List<LoanApplicationPreliminaryEvaluation> preEvaluations = preliminaryEvaluationDao.getPreliminaryEvaluationsWithHardFilters(loanApplicationId, Configuration.getDefaultLocale())
                                .stream().filter(e -> e.getEntityId().equals(entityId) && e.getProduct().getId().equals(productId))
                                .collect(Collectors.toList());
                        Map<String, Object> cachedSources = evaluationCacheService.initializeCachedSources(null);
                        for (LoanApplicationPreliminaryEvaluation preEvaluation : preEvaluations) {
                            preliminaryEvaluationService.runPreliminaryEvaluation(preEvaluation, loanApplication, cachedSources);
                        }

                        // Actions to do if the preliminary evaluation finished
                        LoanApplicationPreliminaryEvaluation lastPreliminaryEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null);
                        evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                        if (lastPreliminaryEvaluation != null && evaluationProcess.getPreEvaluationStatus() != evaluationProcess.STATUS_FINISHED) {

                            // Update the status to finished
                            loanApplicationDao.updateLoanApplicationEvaluationProcessPreEvaluationStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_FINISHED);

                            // Send email if created from entity extranet
                            // This was avoided because of mantis 0001000
                            if (false && loanApplication.getOrigin() == LoanApplication.ORIGIN_EXTRANET_ENTITY) {
                                loanApplicationService.sendBackendGeneratedPreEvaluationEmail(loanApplication);
                            }

                            // If is approved, call the bots
                            /*if (lastPreliminaryEvaluation.getApproved()) {
                                loanApplicationService.callUserBotsIfLoanOk(loanApplication.getId(), person, null, Configuration.getDefaultLocale());
                            }*/


                            // If is rejected and is Banco Del Sol, run the bureaus anyway
//                            if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANCO_DEL_SOL) {
//                                if (!lastPreliminaryEvaluation.getApproved()) {
//
//                                    // Initialize the executors
//                                    List<CompletableFuture> completableFutures = new ArrayList<>();
//                                    ExecutorService executor = Executors.newFixedThreadPool(1);
//                                    ExecutorService completableExecutor = Executors.newFixedThreadPool(1);
//
//
//                                    // Run the Bureau needed for the entity/ product
//                                    List<ApplicationBureau> bureausThatHasRun = loanApplicationDao.getBureauResults(loanApplication.getId());
//                                    for (Bureau bureau : catalogService.getEntity(Entity.BANCO_DEL_SOL).getBureaus()) {
//                                        if (bureausThatHasRun.stream().noneMatch(b -> b.getBureauId().equals(bureau.getId()))) {
//                                            final int bureauIdtoRun = bureau.getId();
//                                            runFutureTask(new FutureTask() {
//                                                @Override
//                                                public void runTask() throws Exception {
//                                                    bureauService.runBureau(loanApplication, person, bureauIdtoRun);
//                                                }
//
//                                                @Override
//                                                public void onException() throws Exception {
//                                                }
//                                            }, completableFutures, executor, completableExecutor, 1, 50, TimeUnit.SECONDS);
//                                        }
//                                    }
//
//                                    // Wait for all the async threads.
//                                    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
//                                    completableExecutor.shutdownNow();
//                                    executor.shutdownNow();
//                                }
//                            }
                        }

                        // If there are evaluations, run the update
                        if (!loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale()).isEmpty())
                            evaluationDao.updateApplicationStatusEvaluation(loanApplication.getId());
                    }
                }

                entityProductEvaluationsProcess = loanApplicationService.getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);
                evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                if (entityProductEvaluationsProcess.getPreliminaryEvaluationApproved() != null && entityProductEvaluationsProcess.getPreliminaryEvaluationApproved()
                        && evaluationProcess.getReadyForEvaluation() != null && evaluationProcess.getReadyForEvaluation()) {

                    Map<String, Object> cachedSources = evaluationCacheService.initializeCachedSources(person);

                    // VALIDATIONS BEFORE EVALUATION PRE BUREAU
                    Integer aztecaCreditProcessOption = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_CREDIT_PROCESS_OPTION.getKey(), null);
                    if(entityId == Entity.AZTECA && aztecaCreditProcessOption != null && Arrays.asList(Product.TRADITIONAL).contains(entityProductEvaluationsProcess.getProductId())){
                        List<LoanApplicationPreliminaryEvaluation> preEvaluationsApproved = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().filter(p -> p.getApproved() !=null && p.getApproved()).collect(Collectors.toList());
                        if(preEvaluationsApproved.size() > 1){
                            Integer preEvaluationToDisapprove = null;
                            if(aztecaCreditProcessOption == Question165Service.AZTECA_AGENCY_PROCESS){
                                preEvaluationToDisapprove = preEvaluationsApproved.stream().filter(p -> p.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE).findFirst().map(e -> e.getId()).orElse(null);
                            }else if(aztecaCreditProcessOption == Question165Service.AZTECA_ONLINE_PROCESS){
                                preEvaluationToDisapprove = preEvaluationsApproved.stream().filter(p -> p.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA).findFirst().map(e -> e.getId()).orElse(null);
                            }
                            if(preEvaluationToDisapprove != null){
                                preliminaryEvaluationDao.updateIsApproved(preEvaluationToDisapprove, false);
                            }
                        }
                    }

                    // If doesnt exists evaluations for the EntityProduct, then run the evaluation preBureau
                    boolean existsEvaluation = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                            .stream().anyMatch(e -> e.getProduct().getId().equals(productId) && e.getEntityId().equals(entityId));
                    if (!existsEvaluation) {
                        evaluationBureauService.runEvaluationPreBureau(loanApplication, productId, entityId, cachedSources);
                    }

                    // Only run if its the first time or if is delayed
                    entityProductEvaluationsProcess = loanApplicationService.getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);
                    if (Arrays.asList(null, EntityProductEvaluationsProcess.STATUS_RUNNING_DELAYED).contains(entityProductEvaluationsProcess.getEvaluationStatus())) {

                        // If its the first time, update to Running
                        if (entityProductEvaluationsProcess.getEvaluationStatus() == null) {
                            updateEvaluationStatus(EntityProductEvaluationsProcess.STATUS_RUNNING, loanApplicationId, entityId, productId);
                            entityProductEvaluationsProcess.setEvaluationStatus(EntityProductEvaluationsProcess.STATUS_RUNNING);
                        }

                        // if the entityProductProcess doesnt have an result yet, run the WS
                        if (entityProductEvaluationsProcess.getEvaluationApproved() == null) {

                            // Initialize the executors
                            List<CompletableFuture> completableFutures = new ArrayList<>();
                            ExecutorService executor = Executors.newFixedThreadPool(2);
                            ExecutorService completableExecutor = Executors.newFixedThreadPool(2);

                            // Run the WebService(s) for the entity/product
                            EntityProductEvaluationsProcess finalEntityProductEvaluationsProcess = entityProductEvaluationsProcess;
                            runFutureTask(new FutureTask() {
                                @Override
                                public void runTask() throws Exception {
                                    switch (entityId) {
                                        case Entity.ACCESO: {
                                            accesoServiceCall.callScoreLdConsumo(loanApplication);

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.ABACO: {
                                            // Register the associated
                                            ERptaCredito associated = agreementService.getAsociatedByEmployeeEntity(person.getDocumentType().getId(), person.getDocumentNumber(), Entity.ABACO, loanApplication.getId());
                                            if (associated != null && associated.isExitoso())
                                                personDao.registerAssociated(person.getId(), Entity.ABACO, associated.getIdSocio(), null);

                                            // Call abaco for the reclosure
                                            Locale locale = Configuration.getDefaultLocale();
                                            List<Employee> agreementEmployees = employeeService.getEmployeesByEmailOrDocumentByProduct(null, person.getDocumentType().getId(), person.getDocumentNumber(), Product.AGREEMENT, locale);
                                            if (agreementEmployees != null && loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) {
                                                for (Employee employee : agreementEmployees.stream().filter(e -> e.getActive()).collect(Collectors.toList())) {
                                                    EntityProduct entityProductEmployee = catalogService.getEntityProductsByProduct(Product.AGREEMENT)
                                                            .stream()
                                                            .filter(e -> e.getEmployer() != null && e.getEmployer().getId().equals(employee.getEmployer().getId()))
                                                            .findAny().orElse(null);
                                                    ERptaCredito associatedResponse = agreementService.getAsociatedByEmployeeEntity(employee.getDocType().getId(), employee.getDocNumber(), entityProductEmployee.getEntityId(), loanApplication.getId());
                                                    if (associatedResponse != null) {
                                                        if ((associatedResponse.getCuotasTotales() > 0) && (associatedResponse.getCuotasPagadas() < (associatedResponse.getCuotasTotales() / 2) + 1)) {
                                                            loanApplicationDao.registerLoanApplicationReclosure(loanApplication.getId(), entityProductEmployee.getEntityId(), false, associatedResponse.getSaldoCredito());
                                                        } else {
                                                            loanApplicationDao.registerLoanApplicationReclosure(loanApplication.getId(), entityProductEmployee.getEntityId(), true, associatedResponse.getSaldoCredito());
                                                        }
                                                    }
                                                }
                                            }

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.COMPARTAMOS: {
                                            // Call consultarVariablesCrediticias
                                            Boolean consultarVariablesCrediticias = compartamosServiceCall.callConsultarVariablesEvaluacion(new Cliente(person), loanApplication.getId());

                                            // Call consultar variables evaluacion
                                            if (consultarVariablesCrediticias != null && consultarVariablesCrediticias) {
                                                compartamosServiceCall.callTraerVariablesEvaluacion(new DocumentoIdentidad(person, translatorDao), loanApplication.getId());
                                            }

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.CAJASULLANA: {
                                            PersonAssociated associated = personDao.getAssociated(person.getId(), Entity.CAJASULLANA);
                                            List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), person.getId());
                                            PersonOcupationalInformation principalOcupation = ocupations != null ?
                                                    ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null) : null;
                                            cajaSullanaServiceCall.callValidarExperian(
                                                    new ValidarExperianRequest(translatorDao, person, associated != null ? associated.getAssociatedId() : "", loanApplication, principalOcupation),
                                                    loanApplication.getId());

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.AELU: {
                                            // Register the associated
                                            personDao.isRawAssociated(person.getId(), Entity.AELU);

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.QAPAQ: {

//                                            boolean shouldCallWsFlujo1 = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
//                                                    .stream()
//                                                    .anyMatch(e -> e.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_QAPAQ_FLUJO_1));
//                                            if (shouldCallWsFlujo1) {
//                                                Boolean result = qapaqServiceCall.capturarValor(person.getDocumentNumber(), loanApplicationId);
//                                                if (result != null) {
//                                                    loanApplicationDao.approvedDataLoanApplication(result, entityId, loanApplicationId, EntityProductParams.ENT_PROD_PARAM_QAPAQ_FLUJO_1);
//                                                }
//                                            }

                                            qapaqServiceCall.callGenerateLoanOffers(loanApplication);

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.FUNDACION_DE_LA_MUJER: {

                                            fdlmServiceCall.callConsultarPersona(loanApplication);

                                            fdlmServiceCall.callConsultarCredito(loanApplication);

                                            fdlmServiceCall.callGetDatosCliente(loanApplication);

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        case Entity.AZTECA: {

                                            if(finalEntityProductEvaluationsProcess.getProductId() != null &&  Arrays.asList(Product.TRADITIONAL,Product.SAVINGS_ACCOUNT,Product.VALIDACION_IDENTIDAD).contains(finalEntityProductEvaluationsProcess.getProductId())){
                                                String token = btApiRestService.getToken(Entity.AZTECA);
                                                if(loanApplication.getBanTotalApiData() != null && loanApplication.getBanTotalApiData().getPersonaUId() != null){
                                                    btApiRestService.btPersonasObtenerDatosPEP(loanApplication,loanApplication.getBanTotalApiData().getPersonaUId(),token);
                                                }
                                                btApiRestService.validarEnListasNegras(loanApplication,person,token);
                                            }

                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                        default: {
                                            if (isStillRunning())
                                                updateEvaluationStatus(
                                                        EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                                        loanApplicationId, entityId, productId);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onException() throws Exception {
                                    switch (entityId) {
                                        default: {
                                            updateEvaluationStatus(
                                                    EntityProductEvaluationsProcess.STATUS_FAILED,
                                                    loanApplicationId, entityId, productId);
                                            break;
                                        }
                                    }
                                }
                            }, completableFutures, executor, completableExecutor, 2, 90, TimeUnit.SECONDS);

                            // Run the Bureau needed for the entity/ product
                            List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                    .stream().filter(e -> (e.getApproved() == null || e.getApproved()) && e.getProduct().getId().equals(productId) && e.getEntityId().equals(entityId)).collect(Collectors.toList());
                            List<Integer> bureausPendingToRun = loanApplicationService.getBureausPendingToRun(loanApplication, evaluations).stream().mapToInt(i -> i).distinct().boxed().collect(Collectors.toList());
                            // IF it's BDS, run frst PYP and then NOSIS
                            Pair<Boolean, Integer> pypResult = null;
                            Triple<Boolean, Integer, Boolean> nosisResult = null;
                            if (entityId == Entity.BANCO_DEL_SOL && bureausPendingToRun.contains(Bureau.NOSIS_BDS)) {
                                // First run PYP
                                int bureauTimeout = 50;
                                if (bureausPendingToRun.contains(Bureau.PYP)) {
                                    runFutureTask(new FutureTask() {
                                        @Override
                                        public void runTask() throws Exception {
                                            bureauService.runBureau(loanApplication, person, Bureau.PYP);
                                        }

                                        @Override
                                        public void onException() throws Exception {
                                        }
                                    }, completableFutures, executor, completableExecutor, 2, bureauTimeout, TimeUnit.SECONDS);
                                    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
                                    completableExecutor.shutdownNow();
                                    executor.shutdownNow();
                                }

                                //Then validate the PYP response. If eveything ok, then run nosis
                                pypResult = evaluationBureauService.evaluatePypResponse(loanApplicationId, new HashMap<>());
                                if (pypResult.getLeft()) {
                                    completableFutures = new ArrayList<>();
                                    executor = Executors.newFixedThreadPool(2);
                                    completableExecutor = Executors.newFixedThreadPool(2);
                                    runFutureTask(new FutureTask() {
                                        @Override
                                        public void runTask() throws Exception {
                                            bureauService.runBureau(loanApplication, person, Bureau.NOSIS_BDS);
                                        }

                                        @Override
                                        public void onException() throws Exception {
                                        }
                                    }, completableFutures, executor, completableExecutor, 2, bureauTimeout, TimeUnit.SECONDS);
                                    CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
                                    completableExecutor.shutdownNow();
                                    executor.shutdownNow();

                                    // If it need to run Veraz, validate the Nosis result and the run the veraz Bureau
                                    if(bureausPendingToRun.contains(Bureau.VERAZ_BDS)){
                                        nosisResult = evaluationBureauService.evaluateNosisResponse(loanApplicationId, new HashMap<>());
                                        if(nosisResult.getRight()){
                                            completableFutures = new ArrayList<>();
                                            executor = Executors.newFixedThreadPool(2);
                                            completableExecutor = Executors.newFixedThreadPool(2);
                                            runFutureTask(new FutureTask() {
                                                @Override
                                                public void runTask() throws Exception {
                                                    bureauService.runBureau(loanApplication, person, Bureau.VERAZ_BDS);
                                                }

                                                @Override
                                                public void onException() throws Exception {
                                                }
                                            }, completableFutures, executor, completableExecutor, 2, 70, TimeUnit.SECONDS);
                                            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
                                            completableExecutor.shutdownNow();
                                            executor.shutdownNow();
                                        }else if(!nosisResult.getLeft()) {
                                            if (nosisResult.getMiddle() != null)
                                                evaluationDao.updateEvaluationPoliciy(loanApplicationId, entityId, productId, nosisResult.getMiddle());
                                        }
                                    }

                                    if(bureausPendingToRun.contains(Bureau.VERAZ_REST_BDS)){
                                        nosisResult = evaluationBureauService.evaluateNosisResponse(loanApplicationId, new HashMap<>());
                                        if(nosisResult.getRight()){
                                            completableFutures = new ArrayList<>();
                                            executor = Executors.newFixedThreadPool(2);
                                            completableExecutor = Executors.newFixedThreadPool(2);
                                            runFutureTask(new FutureTask() {
                                                @Override
                                                public void runTask() throws Exception {
                                                    bureauService.runBureau(loanApplication, person, Bureau.VERAZ_REST_BDS);
                                                }

                                                @Override
                                                public void onException() throws Exception {
                                                }
                                            }, completableFutures, executor, completableExecutor, 2, 70, TimeUnit.SECONDS);
                                            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
                                            completableExecutor.shutdownNow();
                                            executor.shutdownNow();
                                        }else if(!nosisResult.getLeft()) {
                                            if (nosisResult.getMiddle() != null)
                                                evaluationDao.updateEvaluationPoliciy(loanApplicationId, entityId, productId, nosisResult.getMiddle());
                                        }
                                    }

                                } else {
                                    if (pypResult.getRight() != null)
                                        evaluationDao.updateEvaluationPoliciy(loanApplicationId, entityId, productId, pypResult.getRight());
                                }

                            } else {
                                for (Integer bureauId : bureausPendingToRun) {
                                    final int bureauIdtoRun = bureauId;
                                    int bureauTimeout = bureauIdtoRun == Bureau.PYP || bureauIdtoRun == Bureau.NOSIS_BDS ? 50 : 30;
                                    runFutureTask(new FutureTask() {
                                        @Override
                                        public void runTask() throws Exception {
                                            if (bureauIdtoRun == Bureau.EQUIFAX_RUC) {
                                                List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), person.getId());
                                                PersonOcupationalInformation ocupation = ocupations.stream().filter(o -> o.getNumber() == o.PRINCIPAL).findFirst().orElse(null);
                                                bureauService.runBureau(loanApplication, IdentityDocumentType.RUC, ocupation.getRuc(), bureauIdtoRun);
                                            } else {
                                                bureauService.runBureau(loanApplication, person, bureauIdtoRun);
                                            }
                                        }

                                        @Override
                                        public void onException() throws Exception {
                                        }
                                    }, completableFutures, executor, completableExecutor, 2, bureauTimeout, TimeUnit.SECONDS);
                                }
                                // Wait for all the async threads.
                                CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()])).join();
                                completableExecutor.shutdownNow();
                                executor.shutdownNow();
                            }

                            // If the bureau didnt run and is in production, update to  FAILED
                            // Only PRD because in other envs equifax always fails
//                            if (Configuration.hostEnvIsProduction()) {
                            if (pypResult == null || pypResult.getLeft()) {
                                if (nosisResult == null || nosisResult.getLeft()) {
                                    bureausPendingToRun = loanApplicationService.getBureausPendingToRun(loanApplication, evaluations).stream().mapToInt(i -> i).distinct().boxed().collect(Collectors.toList());
                                    if (!bureausPendingToRun.isEmpty()) {
                                        updateEvaluationStatus(
                                                EntityProductEvaluationsProcess.STATUS_FAILED,
                                                loanApplicationId, entityId, productId);
                                    }
                                }
                            }
//                            }

                        } else {

                            updateEvaluationStatus(
                                    EntityProductEvaluationsProcess.STATUS_SUCCESS,
                                    loanApplicationId, entityId, productId);
                        }

                        // If the entityProductProcess is Failed, rety the bot if can
                        entityProductEvaluationsProcess = loanApplicationService.getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);
                        if (entityProductEvaluationsProcess.getEvaluationStatus() == EntityProductEvaluationsProcess.STATUS_FAILED) {
                            if (retryLoanApplicationEvaluation(loanApplication.getId(), entityId, productId)) {
                                registerSucces(queryBot);
                                break;
                            }
                        }

                        // Execute evaluations
                        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale())
                                .stream().filter(e -> e.getProduct().getId().equals(productId) && e.getEntityId().equals(entityId)).collect(Collectors.toList());
                        for (LoanApplicationEvaluation evaluation : evaluations) {
                            evaluationBureauService.runEvaluationBureau(loanApplication, evaluation, cachedSources);
                        }

                        // Actions to do if the evaluation finished
                        LoanApplicationEvaluation lastEvaluation = loanApplicationService.getLastEvaluation(loanApplication.getId(), person.getId(), Configuration.getDefaultLocale());
                        evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplication.getId());
                        if (lastEvaluation != null && (evaluationProcess.getEvaluationStatus() == null || evaluationProcess.getEvaluationStatus() != evaluationProcess.STATUS_FINISHED)) {

                            // Update the status to finished
                            loanApplicationDao.updateLoanApplicationEvaluationProcessEvaluationStatus(loanApplication.getId(), LoanApplicationEvaluationsProcess.STATUS_FINISHED);

                            // If the evaluation is DELAYED or STOPPED, send the email that the evaluation finished
                            if (entityId != Entity.BANCO_DEL_SOL) {
                                if (evaluationProcess.getSendDelayedEmail() != null && evaluationProcess.getSendDelayedEmail()) {
                                    User user = userDao.getUser(person.getUserId());
                                    sendEvaluationReadyInteraction(loanApplication, person, user, InteractionContent.COMPARTAMOS_READY_EVALUATION, user.getEmail());
                                }
                            }
                        }

                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.END_MONTH_COMPANY_RESSUME: {
                JSONObject jsonParams = queryBot.getParameters();
                JSONArray groups = JsonUtil.getJsonArrayFromJson(jsonParams, "groups", null);
                JSONArray employers = JsonUtil.getJsonArrayFromJson(jsonParams, "employers", null);

                for (int i = 0; i < groups.length(); i++) {
                    EmployerGroup employerGroup = catalogService.getEmployerGroup(groups.getInt(i));
                    if (employerGroup != null) {
                        byte[] reportExcel = reportsService.endMonthCompanyResume(groups.getInt(i), true);
                        if (reportExcel != null) {
                            Attachment attachment = new Attachment();
                            attachment.setFilename(employerGroup.getGroupName() + "_cierre_mes_" + utilService.dateCustomFormat(new Date(), "MMMM_yyyy", Configuration.getDefaultLocale()) + ".xlsx");
                            attachment.setContent(new Base64().encodeAsString(reportExcel));

                            awsSesEmailService.sendRawEmail(
                                    null,
                                    "hola@solven.pe",
                                    null,
                                    Configuration.PERU_MANAGER_EMAIL,
                                    new String[]{Configuration.GENERAL_MANAGER_EMAIL},
                                    "Cierre de mes - " + employerGroup.getGroupName(),
                                    "Reporte de cierre de mes del grupo " + employerGroup.getGroupName() + " del periodo " + utilService.dateCustomFormat(new Date(), "MMMM_yyyy", Configuration.getDefaultLocale()),
                                    "Reporte de cierre de mes del grupo " + employerGroup.getGroupName() + " del periodo " + utilService.dateCustomFormat(new Date(), "MMMM_yyyy", Configuration.getDefaultLocale()),
                                    null,
                                    Arrays.asList(attachment), null, null, null);
                        }
                    }
                }

                for (int i = 0; i < employers.length(); i++) {
                    Employer employer = catalogService.getEmployer(employers.getInt(i));
                    if (employer != null) {
                        byte[] reportExcel = reportsService.endMonthCompanyResume(employers.getInt(i), false);
                        if (reportExcel != null) {
                            Attachment attachment = new Attachment();
                            attachment.setFilename(employer.getName() + "_cierre_mes_" + utilService.dateCustomFormat(new Date(), "MMMM_yyyy", Configuration.getDefaultLocale()) + ".xlsx");
                            attachment.setContent(new Base64().encodeAsString(reportExcel));

                            awsSesEmailService.sendRawEmail(
                                    null,
                                    "hola@solven.pe",
                                    null,
                                    Configuration.PERU_MANAGER_EMAIL,
                                    new String[]{Configuration.GENERAL_MANAGER_EMAIL},
                                    "Cierre de mes - " + employer.getName(),
                                    "Reporte de cierre de mes del empleador " + employer.getName() + " del periodo " + utilService.dateCustomFormat(new Date(), "MMMM_yyyy", Configuration.getDefaultLocale()),
                                    "Reporte de cierre de mes del empleador " + employer.getName() + " del periodo " + utilService.dateCustomFormat(new Date(), "MMMM_yyyy", Configuration.getDefaultLocale()),
                                    null,
                                    Arrays.asList(attachment), null, null, null);
                        }
                    }
                }

                break;
            }
            case Bot.REPORT_PROCESOR: {
                Integer reportId = queryBot.getParameters().getInt("reportProcesId");
                ReportProces reportProces = reportsDao.getReportProces(reportId);
                if (reportProces != null) {
                    try {
                        reportsDao.updateReportProcesProcessDate(reportProces.getId(), new Date());
                        reportsDao.updateReportProcesStatus(reportProces.getId(), ReportProces.STATUS_PENDING);
                        Pair<byte[], String> result = reportsService.processReport(reportProces);
                        if (result != null) {
                            String fileName = fileService.writeReport(result.getLeft(), result.getRight());
                            reportsDao.updateReportProcesStatus(reportProces.getId(), ReportProces.STATUS_SUCCESS);
                            reportsDao.updateReportProcesUrl(reportProces.getId(), fileName);
                        } else {
                            reportsDao.updateReportProcesStatus(reportProces.getId(), ReportProces.STATUS_FAILED);
                        }
                    } catch (Throwable th) {
                        reportsDao.updateReportProcesStatus(reportProces.getId(), ReportProces.STATUS_FAILED);
                        throw th;
                    }
                    registerSucces(queryBot);
                }
                break;
            }
            case Bot.UPLOAD_PRE_APPROVED_BASE: {
                Integer entityId = queryBot.getParameters().getInt("entityId");
                Integer productId = queryBot.getParameters().getInt("productId");
                String fileName = queryBot.getParameters().getString("fileName");

                // Connect to the EC2 and run the query for the pre approved base
                try (SSHClient ssh = new SSHClient()) {
                    ssh.loadKnownHosts();
                    ssh.connect("xxxxxxxxxxxxxxxxxxxxx");
                    ssh.authPassword("xxxxxxxxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxxxxxxxx");
                    try (Session session = ssh.startSession()) {
                        Session.Command cmd = session.exec("psql `heroku config:get HEROKU_POSTGRESQL_ORANGE_URL -a prd-solven-c`?ssl=true -c \"DELETE FROM originator.tb_approved_data_temp;\"");
                        System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                        cmd.join(5, TimeUnit.SECONDS);
                    }
                    try (Session session = ssh.startSession()) {
                        String publicUrl = fileService.getS3FilePublicTemporalUrl(FileServiceImpl.S3Folder.PREAPPROVED_BASE_FOLDER, fileName, 60);
                        String command = "psql `heroku config:get HEROKU_POSTGRESQL_ORANGE_URL -a prd-solven-c`?ssl=true " +
                                "-c \"\\COPY originator.tb_approved_data_temp(document_type_id, document_number,max_amount, max_installments,effective_annual_rate, card_type,card_number,payment_day,contract) " +
                                "FROM PROGRAM 'curl \\\"" + publicUrl + "\\\"' WITH DELIMITER ',' CSV HEADER FORCE NOT NULL payment_day;\"";
                        Session.Command cmd2 = session.exec(command);
                        System.out.println(IOUtils.readFully(cmd2.getInputStream()).toString());
                        cmd2.join(5, TimeUnit.SECONDS);
                    }
                    try (Session session = ssh.startSession()) {
                        String queryToRun = "external.upload_approved_data";
                        if (!Configuration.hostEnvIsProduction())
                            queryToRun += "_test";
                        Session.Command cmd3 = session.exec("psql `heroku config:get HEROKU_POSTGRESQL_ORANGE_URL -a prd-solven-c`?ssl=true " +
                                "-c \"select * from " + queryToRun + "(" + entityId + ", " + productId + ")\"");
                        System.out.println(IOUtils.readFully(cmd3.getInputStream()).toString());
                        cmd3.join(5, TimeUnit.SECONDS);
                    }
                }

                // Make the file private and update the name with the QueryBotId
                fileService.makeFilePrivate(FileServiceImpl.S3Folder.PREAPPROVED_BASE_FOLDER, fileName);
                fileService.renameFile(FileServiceImpl.S3Folder.PREAPPROVED_BASE_FOLDER, fileName, queryBot.getId() + "_" + fileName);

                registerSucces(queryBot);
                break;
            }
            case Bot.SEND_SMS: {
                Integer countryId = queryBot.getParameters().getInt("countryId");
                final String message = queryBot.getParameters().getString("message");
                String fileName = queryBot.getParameters().getString("fileName");
                Integer sysUserId = queryBot.getParameters().getInt("sysUserId");

                // Parse the CSV to List  of users
                List<User> usersForSms = new ArrayList<>();
                fileService.useFileInputStream(FileServiceImpl.S3Folder.SMS_BULK_FOLDER, fileName, inputStream -> {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                        String line = br.readLine();
                        while (line != null) {
                            User user = new User();
                            String[] strSplit = line.split(",");
                            user.setFullName(strSplit[0].replaceAll("\"", ""));
                            user.setPhoneNumber(strSplit[1].replaceAll("\"", ""));
                            usersForSms.add(user);
                            line = br.readLine();
                        }
                    } catch (Exception ex) {
                        return ex;
                    }
                    return null;
                });

                // Create the threadpool to send the sms
                ExecutorService botsExecutor = Executors.newFixedThreadPool(Configuration.hostEnvIsProduction() ? 20 : 10);
                List<Future<User>> futureResults;
                try {
                    List<Callable<User>> listToRun = new ArrayList<>();
                    for (User user : usersForSms) {
                        listToRun.add(() -> {
                            try {
                                System.out.println(new SimpleDateFormat("mm:ss").format(new Date()) + " -----ENVIANDO SMS " + Thread.currentThread().getName() + " - " + user.getPhoneNumber() + " -----");
                                smsService.sendSms(user.getPhoneNumber(), message.replaceAll("%NAME%", user.getFullName().split(" ")[0]), null);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return user;
                            }
                            return null;
                        });
                    }

                    futureResults = botsExecutor.invokeAll(listToRun);
                } finally {
                    botsExecutor.shutdownNow();
                }

                // Get the users that have failed
                List<User> usersThatFailed = futureResults.stream().map(f -> {
                    try {
                        return f.get();
                    } catch (Exception ex) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());

                // Register in the DB the result
                serviceLogDao.registerSMSSenderServiceLog(
                        usersThatFailed.size(),
                        futureResults.size() - usersThatFailed.size(),
                        null,
                        null,
                        sysUserId,
                        queryBotId);
                registerSucces(queryBot);
                break;
            }
            case Bot.RUN_SYNTHESIZED: {
                Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
                String documentNumber = JsonUtil.getStringFromJson(queryBot.getParameters(), "documentNumber", null);

                if (loanApplicationId != null) {
                    LoanApplicationEvaluationsProcess evaluationsProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplicationId);

                    // If the synthesized process has finished, then just exit
                    if (evaluationsProcess.getSynthesizedStatus() != null && evaluationsProcess.getSynthesizedStatus().equals(LoanApplicationEvaluationsProcess.STATUS_FINISHED)) {
                        registerSucces(queryBot);
                        break;
                    }

                    loanApplicationDao.updateEvaluationProcessSynthesizedStatus(loanApplicationId, LoanApplicationEvaluationsProcess.STATUS_RUNNING);
                    loanApplicationDao.generateSynthesized(documentNumber);
                    loanApplicationDao.updateEvaluationProcessSynthesizedStatus(loanApplicationId, LoanApplicationEvaluationsProcess.STATUS_FINISHED);
                } else {
                    loanApplicationDao.generateSynthesized(documentNumber);
                }
                registerSucces(queryBot);
                break;
            }
            // TODO Bots for searching in the inbox of an email by a query
            case Bot.ONPE:
                OnpeResult onpeResult = (OnpeResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                try (OnpeScrapper scrapper = new OnpeScrapper(Configuration.DEATHBYCAPTCHA_USER, Configuration.DEATHBYCAPTCHA_PWD, Configuration.BOT_TIMEOUT, proxy)) {
                    onpeResult = scrapper.getData(onpeResult.getInDocnumber());
                    logger.debug("QueryBot " + queryBot.getId() + " have result: " + onpeResult);
                    if (onpeResult != null) {
                        registerSucces(queryBot, proxy);
                        botDao.updateQueryResultONPE(queryBot.getId(), onpeResult);
                    } else {
                        registerFailure(queryBot, proxy);
                    }
                }
                break;
            case Bot.AUTOMATIC_INTERACTION: {
                List<PersonInteraction> interactions = botDao.getAutomaticInteractionsToSend();
                for (PersonInteraction personInteraction : interactions) {
                    if (personInteraction.getTemplate() != null) {
                        personInteraction.getInteractionContent().setTemplate(personInteraction.getTemplate());
                    }

                    JSONObject json = null;
                    boolean sendEmail = true;
                    if (personInteraction.getLoanApplicationId() != null) {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(personInteraction.getLoanApplicationId(), Configuration.getDefaultLocale());
                        if (loanApplication.getEntityId() != null && Arrays.asList(Entity.BANCO_DEL_SOL, Entity.FINANSOL, Entity.BANBIF, Entity.PRISMA, Entity.AZTECA).contains(loanApplication.getEntityId())) {
                            sendEmail = false;
                        }
                        if (sendEmail) {
                            String link = null;

                            if (personInteraction.getInteractionContent().getId() == InteractionContent.PIN_PROBLEM_EMAIL) {
                                List<PhoneNumber> userPhoneNumbers = userDao.getAllPhoneNumbersByUser(loanApplication.getUserId());
                                PhoneNumber userPhoneNUmber = userService.getUserPhoneNumberToVerify(userPhoneNumbers);
                                if (userPhoneNUmber != null) {
                                    Map<String, Object> tokenParams = new HashMap<>();
                                    tokenParams.put("assistingPhoneNumberId", userPhoneNUmber.getPhoneNumberId());
                                    link = loanApplicationService.generateLoanApplicationLinkEntity(
                                            loanApplication,
                                            loanApplicationService.generateLoanApplicationToken(
                                                    loanApplication.getUserId(),
                                                    loanApplication.getPersonId(),
                                                    loanApplication.getId(),
                                                    tokenParams));
                                }
                            } else {
                                link = loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
                            }
                            json = new JSONObject("{\"LINK\": \"" + link + "\"}");
                        }
                    }

                    if (sendEmail) {
                        interactionService.sendPersonInteraction(personInteraction, json, null);
                    }
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.RE_EVALUATION_EMAIL_SENDER: {
                Integer interactionContentId = JsonUtil.getIntFromJson(queryBot.getParameters(), "interactionContentId", null);
                List<PersonInteraction> interactions = botDao.getApprovedDataMailsToSend(interactionContentId);
                if (!interactions.isEmpty()) {
                    ExecutorService emailExecutor = Executors.newFixedThreadPool(3);
                    List<CompletableFuture> emailCompletables = new ArrayList<>();

                    for (PersonInteraction personInteraction : interactions) {
                        if (personInteraction.getTemplate() != null) {
                            personInteraction.getInteractionContent().setTemplate(personInteraction.getTemplate());
                        }

                        String countryDomain = catalogService.getCountryParam(CountryParam.COUNTRY_PERU).getDomains().get(0);

                        JSONObject json = new JSONObject();
                        json.put("DOMAIN", "https://" + countryDomain);
                        Map<String, String> tags = new HashMap<String, String>();
                        tags.put("mailing_source", "RE_EVALUATION_EMAIL_SENDER");

                        final PersonInteraction finalPersonInteraction = personInteraction;
                        final JSONObject finalJson = json;
                        emailCompletables.add(CompletableFuture.supplyAsync(() -> {
                            try {
                                Map<String, String> templateVars = new HashMap<>();
                                if (finalPersonInteraction.getInteractionContent().getId() == InteractionContent.MATCH_TRANSACTIONAL_1)
                                    templateVars.put("banner_image_url", "https://s3.amazonaws.com/solven-public/img/mailing/templates/banner_template_transactinalMatch_1.jpg");
                                else if (finalPersonInteraction.getInteractionContent().getId() == InteractionContent.MATCH_TRANSACTIONAL_2)
                                    templateVars.put("banner_image_url", "https://s3.amazonaws.com/solven-public/img/mailing/templates/banner_template_transactinalMatch_2.jpg");
                                else if (finalPersonInteraction.getInteractionContent().getId() == InteractionContent.MATCH_TRANSACTIONAL_3)
                                    templateVars.put("banner_image_url", "https://s3.amazonaws.com/solven-public/img/mailing/templates/banner_template_transactinalMatch_3.jpg");
                                else if (finalPersonInteraction.getInteractionContent().getId() == InteractionContent.MATCH_TRANSACTIONAL_4)
                                    templateVars.put("banner_image_url", "https://s3.amazonaws.com/solven-public/img/mailing/templates/banner_template_transactinalMatch_4.jpg");
                                else if (finalPersonInteraction.getInteractionContent().getId() == InteractionContent.MATCH_TRANSACTIONAL_5)
                                    templateVars.put("banner_image_url", "https://s3.amazonaws.com/solven-public/img/mailing/templates/banner_template_transactinalMatch_5.jpg");
                                interactionService.sendPersonInteraction(finalPersonInteraction, finalJson, tags, templateVars);
                            } catch (Throwable throwable) {
                                errorService.onError(throwable);
                            }
                            return true;
                        }, emailExecutor));
                    }
                    CompletableFuture.allOf(emailCompletables.toArray(new CompletableFuture[emailCompletables.size()])).join();
                    emailExecutor.shutdownNow();
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.TARJETAS_PERUANAS_ACTIVATION_DAILY_SENDER: {
                if (catalogService.getEntity(Entity.TARJETAS_PERUANAS).getActive()) {
                    byte[] reportExcel = tarjetasPeruanasPrepagoService.createActivationSpreadSheet();
                    List<Attachment> attachments = null;
                    if (reportExcel != null) {
                        Attachment attachment = new Attachment();
                        attachment.setFilename("Reporte_de_tarjetas_a_emitir.xls");
                        attachment.setContent(new Base64().encodeAsString(reportExcel));
                        attachments = Arrays.asList(attachment);
                    }
                    awsSesEmailService.sendRawEmail(
                            null,
                            "procesos@solven.pe",
                            null,
                            "notificaciones@solven.pe",
                            null,
                            "Reporte de tarjetas a emitir",
                            "Reporte de tarjetas a emitir",
                            null,
                            null, attachments, null, null, null);
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.HOURLY_INTERACTIONS: {
                Calendar currentDate = Calendar.getInstance();
                int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
                List<PersonInteraction> personInteractionList = botDao.getHourlyInteractions(currentHour);
                for (PersonInteraction personInteraction : personInteractionList) {
                    try {
                        boolean sendMessage = true;
                        if (personInteraction.getTemplate() != null) {
                            personInteraction.getInteractionContent().setTemplate(personInteraction.getTemplate());
                        }

                        JSONObject json = null;
                        if (personInteraction.getLoanApplicationId() != null) {
                            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(personInteraction.getLoanApplicationId(), Configuration.getDefaultLocale());
                            if (loanApplication.getEntityId() == null || (loanApplication.getEntityId() != null && Arrays.asList(Entity.BANCO_DEL_SOL, Entity.BANBIF, null, Entity.AFFIRM).contains(loanApplication.getEntityId()))) {
                                sendMessage = false;
                            }
                            String link = loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
                            if(loanApplication != null && loanApplication.getProductCategoryId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && Arrays.asList(ProductCategory.CONSUMO,ProductCategory.CUENTA_BANCARIA).contains(loanApplication.getProductCategoryId())){
                                if(loanApplication.getProductCategoryId() == ProductCategory.CONSUMO) link = "https://www.alfinbanco.pe/prestamos";
                                else if(loanApplication.getProductCategoryId() == ProductCategory.CUENTA_BANCARIA) link = "https://www.alfinbanco.pe/ahorro";
                            }
                            json = new JSONObject("{\"LINK\": \"" + link + "\"}");
                            if (loanApplication != null && loanApplication.getCredit() != null && loanApplication.getCredit()) {
                                Credit credit = creditDAO.getCreditByID(loanApplication.getCreditId(), Configuration.getDefaultLocale(), false, Credit.class);
                                if (credit != null)
                                    json.put("CREDIT_AMOUNT", credit.getAmount() != null ? utilService.customDoubleFormat(credit.getAmount(), 0).replace(".", "") : "");
                            }
                            else if (loanApplication != null){
                                try{
                                    Integer maxPreApprovedAmount = loanApplicationDao.getMaxPreapprovedAmount(loanApplication.getId());
                                    if(maxPreApprovedAmount != null) json.put("CREDIT_AMOUNT", maxPreApprovedAmount.toString());
                                }
                                catch (Exception e){}
                            }
                            User user = userDao.getUser(loanApplication.getUserId());
                            Person person = personDao.getPerson(user.getPersonId(), false, Configuration.getDefaultLocale());
                            json.put("CLIENT_NAME", person.getFirstName());
                        }

                        Map<String,String> templateVars = new HashMap<>();
                        if( json != null){
                            for (String key : json.keySet()) {
                                Object keyvalue = json.get(key);
                                templateVars.put(key, String.valueOf(keyvalue));
                            }
                        }

                        if (sendMessage)
                            interactionService.sendPersonInteraction(personInteraction, json, null,templateVars);
                    } catch (Exception ex) {
                        errorService.onError(ex);
                    }
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.AUTOPLAN_LEADS_DAILY_SENDER: {
                if (catalogService.getEntity(Entity.AUTOPLAN).getActive()) {
                    byte[] reportExcel = autoplanService.createLeadsSpreadSheet();
                    List<Attachment> attachments = null;
                    if (reportExcel != null) {
                        Attachment attachment = new Attachment();
                        attachment.setFilename("Reporte_de_leads.xls");
                        attachment.setContent(new Base64().encodeAsString(reportExcel));
                        attachments = Arrays.asList(attachment);
                    }
                    awsSesEmailService.sendRawEmail(
                            null,
                            "procesos@solven.pe",
                            null,
                            "moyola@autoplan.pe",
                            new String[]{"notificaciones@solven.pe"},
                            "Reporte de leads - Autoplan",
                            "Reporte de leads",
                            "Reporte de leads",
                            null, attachments, null, null, null);
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.FRAUD_ALERTS: {
                Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                User user = userDao.getUser(loanApplication.getUserId());

                creditDAO.runFraudAlerts(loanApplicationId);

                if (user.getEmailVerified() == null || !user.getEmailVerified()) {
                    List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(loanApplication.getPersonId(), loanApplicationId, Configuration.getDefaultLocale());
                    int[] interactions = personInteractions != null ?
                            personInteractions
                                    .stream()
                                    .filter(pi -> loanApplicationId.equals(pi.getLoanApplicationId()))
                                    .filter(pi -> pi.getInteractionProvider() != null && pi.getInteractionProvider().getId() == InteractionProvider.AWS)
                                    .map(PersonInteraction::getId)
                                    .mapToInt(i -> i)
                                    .toArray() : new int[]{};

                    Map<String, Date> map = awsElasticSearchClient.getEmailEventsByPersonInteractionIds(interactions);

                    if(!loanApplication.getCountry().getId().equals(CountryParam.COUNTRY_PERU)){
                        if (!map.keySet().contains("Abierto")) {
                            creditDAO.registerLoanApplicationFraudAlert(FraudAlert.VERIFICATION_EMAIL_NOT_OPENED, loanApplicationId);
                        } else {
                            Integer activeEmailId = userDao.getUserEmails(loanApplication.getUserId()).stream().filter(e -> e.getActive() != null && e.getActive()).map(UserEmail::getId).findFirst().orElse(0);
                            userDao.verifyEmail(loanApplication.getUserId(), activeEmailId, true);
                        }
                    }

                }

                // if it have a Mati Verification fraud alert, send it to the documentation question to upload agaain the documentation (only one time)
                List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = creditDao.getLoanApplicationFraudAlerts(loanApplicationId, FraudAlertStatus.NUEVO);
                if (loanApplicationFraudAlerts != null && loanApplicationFraudAlerts.stream().anyMatch(f -> f.getFraudAlert().getFraudAlertId() == FraudAlert.MATI_VERIFICATION && f.getActive() != null && f.getActive())) {
                    List<MatiResult> matiResults = securityDAO.getMatiResultsByLoanApplication(loanApplicationId);
                    if (matiResults.size() <= 1) {
                        loanApplicationService.deleteDocumentsAndAskAgain(loanApplication, Arrays.asList(UserFileType.DNI_ANVERSO, UserFileType.DNI_FRONTAL, UserFileType.SELFIE));
                    }
                }

                registerSucces(queryBot);

                break;
            }
            case Bot.SEND_ACCESO_EXPIRATIO_INTERACTIONS: {
                logger.debug("SEND_ACCESO_EXPIRATIO_INTERACTIONS");
                JSONArray expirationsArray = JsonUtil.getJsonArrayFromJson(queryBot.getParameters(), "expirations", null);
                boolean sendWhatsapp = JsonUtil.getBooleanFromJson(queryBot.getParameters(), "sendWhatsapp", false);
                boolean sendEmail = JsonUtil.getBooleanFromJson(queryBot.getParameters(), "sendEmail", false);
//                int totalVencimiento = JsonUtil.getIntFromJson(queryBot.getParameters(), "totalVencimiento", 0);
//                int totalMora = JsonUtil.getIntFromJson(queryBot.getParameters(), "totalMora", 0);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                for (int i = 0; i < expirationsArray.length(); i++) {
                    try {
                        JSONObject expiration = expirationsArray.getJSONObject(i);
                        Date expireDate = sdf.parse(expiration.getString("expire_date"));
                        String docNumber = JsonUtil.getStringFromJson(expiration, "document_number", null);
//                        String name = JsonUtil.getStringFromJson(expiration, "name", null);
                        Integer installment = JsonUtil.getIntFromJson(expiration, "installment", null);
                        Double amount = JsonUtil.getDoubleFromJson(expiration, "amount", null);
                        String entityCreditCode = JsonUtil.getStringFromJson(expiration, "credit_code", "");

                        Integer personId = personDao.getPersonIdByDocument(IdentityDocumentType.DNI, docNumber);
                        Person person = personDao.getPerson(personId, false, Configuration.getDefaultLocale());
                        User user = userDao.getUser(person.getUserId());
                        Entity entity = catalogService.getEntity(Entity.ACCESO);
                        List<Credit> credits = creditDAO.getCreditsByPerson(personId, Configuration.getDefaultLocale(), Credit.class);
                        Credit credit = credits.stream().filter(c -> c.getEntity().getId() == Entity.ACCESO).filter(c -> entityCreditCode.equalsIgnoreCase(c.getEntityCreditCode())).findFirst().orElse(null);

                        long daysBetweenTodayAndExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                        String suffix = Configuration.installmentOrdinalNumbersSuffix(installment);

                        if (sendWhatsapp) {
                            PersonInteraction personInteraction = new PersonInteraction();
                            personInteraction.setQueryBotId(queryBotId);
                            personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.CHAT));

                            InteractionContent interactionContent;
//                            CASO VENCIMIENTO 1ERA CUOTA. HOY
                            if (daysBetweenTodayAndExpiration == 0 && installment == 1) {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_CHAT_FIRST_INSTALLMENT_TO_EXPIRE_TODAY, person.getCountry().getId());
                            }
//                            CASO VENCIMIENTO N CUOTAS. HOY
                            else if (daysBetweenTodayAndExpiration == 0 && installment > 1) {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_CHAT_N_INSTALLMENT_TO_EXPIRE_TODAY, person.getCountry().getId());
                            }
//                            CASO VENCIMIENTO 1ERA CUOTA
                            else if (daysBetweenTodayAndExpiration > 0 && installment == 1) {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_CHAT_FIRST_INSTALLMENT_TO_EXPIRE, person.getCountry().getId());
                            }
//                            CASO VENCIMIENTO N CUOTA
                            else if (daysBetweenTodayAndExpiration > 0 && installment > 1) {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_CHAT_N_INSTALLMENT_TO_EXPIRE, person.getCountry().getId());
                            }
//                            CASO MORA
                            else {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_CHAT_ALERT_EXPIRED_INSTALLMENT, person.getCountry().getId());
                            }

                            personInteraction.setInteractionContent(interactionContent);
                            personInteraction.setDestination("+" + user.getCountryCode() + user.getPhoneNumber());// TODO
                            personInteraction.setPersonId(personId);
                            if (credit != null) {
                                personInteraction.setLoanApplicationId(credit.getLoanApplicationId());
                                personInteraction.setCreditId(credit.getId());
                            }

                            Map<String, String> templateVars = new LinkedHashMap<>();
                            if (interactionContent.getId() == InteractionContent.ACCESO_CHAT_FIRST_INSTALLMENT_TO_EXPIRE || interactionContent.getId() == InteractionContent.ACCESO_CHAT_ALERT_EXPIRED_INSTALLMENT) {
                                templateVars.put("1", person.getFirstName());
                                templateVars.put("2", String.valueOf(Math.abs(daysBetweenTodayAndExpiration)));
                                templateVars.put("3", utilService.doubleMoneyFormat(amount, Currency.PEN_SYMBOL));
                            } else if (interactionContent.getId() == InteractionContent.ACCESO_CHAT_N_INSTALLMENT_TO_EXPIRE) {
                                templateVars.put("1", person.getFirstName());
                                templateVars.put("2", String.valueOf(Math.abs(daysBetweenTodayAndExpiration)));
                                templateVars.put("3", installment + "" + suffix);
                                templateVars.put("4", utilService.doubleMoneyFormat(amount, Currency.PEN_SYMBOL));
                            } else if (interactionContent.getId() == InteractionContent.ACCESO_CHAT_FIRST_INSTALLMENT_TO_EXPIRE_TODAY) {
                                templateVars.put("1", person.getFirstName());
                                templateVars.put("2", utilService.doubleMoneyFormat(amount, Currency.PEN_SYMBOL));
                            } else if (interactionContent.getId() == InteractionContent.ACCESO_CHAT_N_INSTALLMENT_TO_EXPIRE_TODAY) {
                                templateVars.put("1", person.getFirstName());
                                templateVars.put("2", installment + "" + suffix);
                                templateVars.put("3", utilService.doubleMoneyFormat(amount, Currency.PEN_SYMBOL));
                            } else {
                                logger.error("Interaction not mapped for wavy");
                            }

                            interactionService.sendPersonInteraction(personInteraction, new JSONObject(templateVars), null, templateVars);
                        }

                        if (sendEmail) {
                            PersonInteraction personInteraction = new PersonInteraction();
                            personInteraction.setQueryBotId(queryBotId);
                            personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));

                            InteractionContent interactionContent;
//                            CASO VENCIMIENTO 1ERA CUOTA
                            if (daysBetweenTodayAndExpiration >= 0 && installment == 1) {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_FIRST_INSTALLMENT_TO_EXPIRE, person.getCountry().getId());
                            }
//                            CASO VENCIMIENTO N CUOTA
                            else if (daysBetweenTodayAndExpiration >= 0 && installment > 1) {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_N_INSTALLMENT_TO_EXPIRE, person.getCountry().getId());
                            }
//                            CASO MORA
                            else {
                                interactionContent = catalogService.getInteractionContent(InteractionContent.ACCESO_ALERT_EXPIRED_INSTALLMENT, person.getCountry().getId());
                            }

                            personInteraction.setInteractionContent(interactionContent);
                            personInteraction.setDestination(user.getEmail());
                            personInteraction.setPersonId(personId);
                            if (credit != null) {
                                personInteraction.setLoanApplicationId(credit.getLoanApplicationId());
                                personInteraction.setCreditId(credit.getId());
                            }

                            int absoluteDaysBetweenTodayAndExpiration = (int) Math.abs(daysBetweenTodayAndExpiration);

                            JSONObject jsonVars = new JSONObject();
                            jsonVars.put("BANNER", entity.getJsParams().getOrDefault(Entity.JsParamsKeys.ACCESO_BANNER_COLLECTION.getKey(), ""));
                            jsonVars.put("CLIENT_NAME", person.getFirstName());
                            jsonVars.put("DELAY_DAYS", String.format("%s d&iacute;a%s", absoluteDaysBetweenTodayAndExpiration, absoluteDaysBetweenTodayAndExpiration == 1 ? "" : "s"));
                            jsonVars.put("NEXT_INSTALLMENT_DAYS", absoluteDaysBetweenTodayAndExpiration == 0 ? "<strong>HOY</strong>" : "en <strong>" + absoluteDaysBetweenTodayAndExpiration + " d&iacute;a" + (absoluteDaysBetweenTodayAndExpiration == 1 ? "" : "s") + "</strong>");
                            jsonVars.put("NEXT_INSTALLMENT_ID", installment + "" + suffix);
                            jsonVars.put("NEXT_INSTALLMENT_AMOUNT", utilService.doubleMoneyFormat(amount, (String) null));
                            jsonVars.put("NEXT_DUE_DATE", new SimpleDateFormat("dd/MM/yyyy").format(expireDate));
                            jsonVars.put("AMOUNT", utilService.doubleMoneyFormat(amount, (String) null));
                            jsonVars.put("ENTITY_PHONE_NUMBER", entity.getJsParams().getOrDefault(Entity.JsParamsKeys.ACCESO_PHONENUMBER.getKey(), ""));

                            interactionService.sendPersonInteraction(personInteraction, jsonVars, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.BO_MANAGEMENT_FOLLOWUP_INTERACTION: {
                Integer interactionId = JsonUtil.getIntFromJson(queryBot.getParameters(), "interactionId", null);
                JSONArray loanApplicationsArray = JsonUtil.getJsonArrayFromJson(queryBot.getParameters(), "loanApplicationIds", null);

                for (int i = 0; i < loanApplicationsArray.length(); i++) {
                    try {
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationsArray.getInt(i), Configuration.getDefaultLocale());
                        User user = userDao.getUser(loanApplication.getUserId());
                        Person person = personDao.getPerson(user.getPersonId(), false, Configuration.getDefaultLocale());

                        PersonInteraction personInteraction = new PersonInteraction();
                        personInteraction.setQueryBotId(queryBotId);
                        personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.CHAT));
                        personInteraction.setInteractionContent(catalogService.getInteractionContent(interactionId, CountryParam.COUNTRY_PERU));
                        personInteraction.setDestination("+" + user.getCountryCode() + user.getPhoneNumber());// TODO
                        personInteraction.setPersonId(loanApplication.getPersonId());
                        personInteraction.setLoanApplicationId(loanApplication.getId());

//                        TODO MOVE IF INCREASE
                        String stringProcessQuestion;
                        switch (loanApplication.getCurrentQuestionId()) {
                            case ProcessQuestion.Question.Constants.OFFER: {
                                stringProcessQuestion = "Elegir una Oferta";
                                break;
                            }
                            case ProcessQuestion.Question.Constants.VERIFICATION_REFERRALS: {
                                stringProcessQuestion = "Referencia de amigos o familiares";
                                break;
                            }
                            case ProcessQuestion.Question.Constants.VERIFICATION_DOCUMENTATION: {
                                stringProcessQuestion = "Solicitud de documentos";
                                break;
                            }
                            case ProcessQuestion.Question.Constants.BANK_ACCOUNT_INFORMATION: {
                                stringProcessQuestion = "Información de cuenta bancaria";
                                break;
                            }
                            case ProcessQuestion.Question.Constants.CONTRACT_SIGNATURE: {
                                stringProcessQuestion = "Firma del contrato";
                                break;
                            }
                            default:
                                stringProcessQuestion = "las preguntas";
                        }

                        Map<String, String> templateVars = new LinkedHashMap<>();

                        if (interactionId == InteractionContent.CHAT_FOLLOWUP_BEFORE_OFFER) {
                            templateVars.put("1", person.getFirstName());
                        } else if (interactionId == InteractionContent.CHAT_FOLLOWUP_WITH_OFFER) {
                            templateVars.put("1", person.getFirstName());
                            templateVars.put("2", stringProcessQuestion);
                        }

                        interactionService.sendPersonInteraction(personInteraction, new JSONObject(templateVars), null, templateVars);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.APPROVE_LOAN_APPLICATION: {
                Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
                Integer sysUserId = JsonUtil.getIntFromJson(queryBot.getParameters(), "sysUserId", null);
                Locale locale = new Gson().fromJson(queryBot.getParameters().get("locale").toString(), Locale.class);
                Integer auditTypeId = JsonUtil.getIntFromJson(queryBot.getParameters(), "auditTypeId", null);
                Integer userFileId = JsonUtil.getIntFromJson(queryBot.getParameters(), "userFileId", null);

                Integer rejectReasonId = null;
                String rejectReasonComment = "";
                boolean approved = true;

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
                LoanOffer offerSelected = loanApplicationDao.getLoanOffers(loanApplicationId)
                        .stream().filter(o -> o.getSelected()).findFirst().orElse(null);

                loanApplicationService.approveLoanApplicationWithoutAuditValidation(loanApplication, offerSelected, sysUserId, null, null, null, locale);
                creditDao.registerLoanApplicationAudit(loanApplicationId, auditTypeId, approved, userFileId, rejectReasonId, rejectReasonComment, sysUserId);

                registerSucces(queryBot);
                break;
            }
            case Bot.UNIVERSIDAD_PERU: {
                Integer loanApplicationId = JsonUtil.getIntFromJson(queryBot.getParameters(), "loanApplicationId", null);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                UniversidadPeruResult universidadPeruResult = (UniversidadPeruResult) botDao.getQueryResult(queryBotId, queryBot.getBotId());
                JSONObject params = new JSONObject();
                params.put("ruc", universidadPeruResult.getInRuc());
                String result = null;
                if(Configuration.hostEnvIsProduction()){
                    result = awsLambdaService.callFunction("universidad-peru-lambda-scraper-prd", params.toString());
                }else{
                    result = awsLambdaService.callFunction("universidad-peru-lambda-scraper-stg", params.toString());
                }
                if(loanApplication != null && Arrays.asList(Entity.ACCESO).contains(loanApplication.getEntityId())){
                    JSONObject responseJson = new JSONObject(result);
                    JSONObject responseBody = responseJson.getJSONObject("body");
                    String phoneNumber1 = null;
                    String phoneNumber2 = null;
                    String phoneNumber3 = null;
                    String phoneNumber4 = null;
                    if(responseBody.optJSONArray("telefonos") != null){
                        for(int i=0; i<responseBody.getJSONArray("telefonos").length(); i++){
                            if(i==0) phoneNumber1 = responseBody.getJSONArray("telefonos").getString(0);
                            if(i==1) phoneNumber2 = responseBody.getJSONArray("telefonos").getString(1);
                            if(i==2) phoneNumber3 = responseBody.getJSONArray("telefonos").getString(2);
                            if(i==3) phoneNumber4 = responseBody.getJSONArray("telefonos").getString(3);
                        }
                    }
                    botDao.updateQueryResultUniversidadPeru(queryBot.getId(),
                            phoneNumber1, phoneNumber2, phoneNumber3, phoneNumber4,
                            responseBody.optString("direccionLegal"),
                            responseBody.optString("distrito"),
                            responseBody.optString("departamento")
                    );

                    // Call to geolocation


                    Direccion address = new Direccion(catalogService,
                            "J",
                            StreetType.AVENIDA + "",
                            responseBody.optString("direccionLegal"),
                            null,
                            null,
                            null,
                            null,
                            "150101",
                            null,
                            AreaType.NO_APLICA + "",
                            null,
                            null,
                            null,
                            null,
                            null);

                    String urlParams = String.format("%s+%s+%s", responseBody.optString("departamento"), responseBody.optString("distrito"), responseBody.optString("direccionLegal"));
                    GeocodingResponse geocodingResponse = geocodingService.getGeoLocation(loanApplicationId, urlParams);
                    if(geocodingResponse != null && !geocodingResponse.getResults().isEmpty()){
                        GeocodingResult geocodingResult = geocodingResponse.getResults().get(0);
                        if(geocodingResult != null && geocodingResult.getGeometry() != null && geocodingResult.getGeometry().getLocation() != null){
                            address.setLatitude(geocodingResult.getGeometry().getLocation().getLat());
                            address.setLongitude(geocodingResult.getGeometry().getLocation().getLng());
                        }
                        personDao.registerDisgregatedAddress(loanApplication.getPersonId(), address);
                    }
                }

                registerSucces(queryBot);
                break;
            }
            case Bot.LOAD_PRE_APPROVED_BASE: {
                Integer processId = queryBot.getParameters().getInt("preApprovedProcessId");
                PreApprovedBaseProcessed preApprovedBaseProcessed = preApprovedDAO.getPreApprovedBaseProcessed(processId);
                if (preApprovedBaseProcessed != null) {
                    if (preApprovedBaseProcessed.getErrorDetail() == null)
                        preApprovedBaseProcessed.setErrorDetail(new PreApprovedBaseProcessed.ErrorDetail());
                    try {
                        preApprovedDAO.updateProcessDate(preApprovedBaseProcessed.getId(), new Date());
                        preApprovedDAO.updateProcessStatus(preApprovedBaseProcessed.getId(), PreApprovedBaseProcessed.STATUS_PENDING);
                        preApprovedBaseService.uploadData(preApprovedBaseProcessed.getUrl(), preApprovedBaseProcessed.getEntityId());
                        preApprovedDAO.updateProcessStatus(preApprovedBaseProcessed.getId(), PreApprovedBaseProcessed.STATUS_SUCCESS);
                        preApprovedDAO.updateProcessDate(preApprovedBaseProcessed.getId(), new Date());
                    } catch (SQLException e) {
                        preApprovedDAO.updateProcessStatus(preApprovedBaseProcessed.getId(), PreApprovedBaseProcessed.STATUS_FAILED);
                        preApprovedBaseProcessed.getErrorDetail().setCode(e.getSQLState());
                        preApprovedBaseProcessed.getErrorDetail().setDetailMessage(e.getMessage());
                        preApprovedBaseProcessed.getErrorDetail().setCustomMessage(pSqlErrorMessageService.getMessageByErrorCode(e.getSQLState(), e.getMessage()));
                        preApprovedDAO.updateProcessMessage(preApprovedBaseProcessed.getId(), preApprovedBaseProcessed.getErrorDetail());
                        throw e;
                    } catch (Throwable th) {
                        preApprovedDAO.updateProcessStatus(preApprovedBaseProcessed.getId(), PreApprovedBaseProcessed.STATUS_FAILED);
                        preApprovedBaseProcessed.getErrorDetail().setDetailMessage(th.getMessage());
                        preApprovedDAO.updateProcessMessage(preApprovedBaseProcessed.getId(), preApprovedBaseProcessed.getErrorDetail());
                        throw th;
                    }
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.LOAD_NEGATIVE_BASE: {
                Integer processId = queryBot.getParameters().getInt("negativeBaseProcessId");
                NegativeBaseProcessed negativeBaseProcessed = negativeBaseProcessDAO.getNegativeBaseProcessed(processId);
                if (negativeBaseProcessed != null) {
                    if (negativeBaseProcessed.getErrorDetail() == null)
                        negativeBaseProcessed.setErrorDetail(new NegativeBaseProcessed.ErrorDetail());
                    try {
                        negativeBaseProcessDAO.updateProcessDate(negativeBaseProcessed.getId(), new Date());
                        negativeBaseProcessDAO.updateProcessStatus(negativeBaseProcessed.getId(), NegativeBaseProcessed.STATUS_PENDING);
                        negativeBaseService.uploadData(negativeBaseProcessed.getUrl(), negativeBaseProcessed.getEntityId(), negativeBaseProcessed.getType());
                        negativeBaseProcessDAO.updateProcessStatus(negativeBaseProcessed.getId(), NegativeBaseProcessed.STATUS_SUCCESS);
                        negativeBaseProcessDAO.updateProcessDate(negativeBaseProcessed.getId(), new Date());
                    } catch (SQLException e) {
                        negativeBaseProcessDAO.updateProcessStatus(negativeBaseProcessed.getId(), NegativeBaseProcessed.STATUS_FAILED);
                        negativeBaseProcessed.getErrorDetail().setCode(e.getSQLState());
                        negativeBaseProcessed.getErrorDetail().setDetailMessage(e.getMessage());
                        negativeBaseProcessed.getErrorDetail().setCustomMessage(pSqlErrorMessageService.getMessageByErrorCode(e.getSQLState(), e.getMessage()));
                        negativeBaseProcessDAO.updateProcessMessage(negativeBaseProcessed.getId(), negativeBaseProcessed.getErrorDetail());
                        throw e;
                    } catch (Throwable th) {
                        negativeBaseProcessDAO.updateProcessStatus(negativeBaseProcessed.getId(), NegativeBaseProcessed.STATUS_FAILED);
                        negativeBaseProcessed.getErrorDetail().setDetailMessage(th.getMessage());
                        negativeBaseProcessDAO.updateProcessMessage(negativeBaseProcessed.getId(), negativeBaseProcessed.getErrorDetail());
                        throw th;
                    }
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.MONITOR_SERVERS: {
                Date now = new Date();

                Map<String, List<Dyno>> dynosByApp = herokuService.getDynos()
                        .stream()
                        .collect(Collectors.groupingBy(Dyno::getCustomAppName, Collectors.toList()));

                dynosByApp.forEach((customAppName, dynos) -> {
                    ServerStatus serverStatus = new ServerStatus();
                    serverStatus.setApp(customAppName);
                    serverStatus.setRegisterDate(now);
                    serverStatus.setState(dynos);

                    securityDAO.registerServerStatus(serverStatus);
                });
                registerSucces(queryBot);
                break;
            }
            case Bot.BANBIF_CONVERSIONS: {
                logger.debug("----------> BANBIF_CONVERSIONS");
//                if(Configuration.hostEnvIsProduction()){
//                    banBifService.readBaseFile();
//                }
                banBifService.readBaseFile();
                registerSucces(queryBot);
                break;
            }
            case Bot.SEND_REPORT_TO_FTP: {
                Integer loanId = queryBot.getParameters().getInt("loanId");
                if (loanId != null) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, Configuration.getDefaultLocale());
                    Date startDate = null;
                    Date endDate = null;
                    Integer entity = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
                    String query = null;

                    Integer[] products = loanApplication.getProduct() != null ? new Integer[]{loanApplication.getProduct().getId()} : null;

                    Integer[] steps = {};
                    EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
                    EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entity);
                    if (extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loanApplication.getProduct() != null && loanApplication.getProductCategoryId() != null)
                        funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == loanApplication.getProductCategoryId()).findFirst().orElse(null);
                    if (funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()) {
                        steps = funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()).toArray(new Integer[funnelConfiguration.getSteps().size()]);
                    }

                    Integer[] loanIds = {loanId};

                    Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                    Pair<byte[], String> result = Pair.of(reportsService.createTrayExtranetReport(entity, loanIds, products, steps), String.format("solicitud_%s_%s_%s.xls", person.getDocumentNumber(), new SimpleDateFormat("ddMMyyyy").format(new Date()), new SimpleDateFormat("HHmmss").format(new Date())));

                }
                registerSucces(queryBot);
                break;
            }
            case Bot.MATI_PROCESS: {
                Integer loanApplicationId = queryBot.getParameters().getInt("loanId");
                if (loanApplicationId != null) {
                    MatiResult matiResult = securityDAO.registerMatiResult(loanApplicationId, queryBotId);

                    String token = matiService.getOauthToken();
                    CreateVerificationResponse response = matiService.createVerification(loanApplicationId, matiResult.getId(), token);
                    securityDAO.updateMatiResultVerificationId(matiResult.getId(), response.getId());

                    List<UserFile> files = loanApplicationDao.getLoanApplicationUserFiles(loanApplicationId);
                    Integer selfieUserFileId = files.stream().filter(u -> u.getFileType().getId() == UserFileType.SELFIE).findFirst().map(u -> u.getId()).orElse(null);
                    Integer dniFrontUserFileId = files.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_FRONTAL).findFirst().map(u -> u.getId()).orElse(null);
                    Integer dniBackUserFileId = files.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_ANVERSO).findFirst().map(u -> u.getId()).orElse(null);
                    matiService.sendDocumentation(loanApplicationId, response.getIdentity(), selfieUserFileId, dniFrontUserFileId, dniBackUserFileId, token);
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.SEND_TCONEKTA_INFORMATION: {
                Integer loanApplicationId = queryBot.getParameters().getInt("loanApplicationId");
                if(loanApplicationId != null && Configuration.hostEnvIsProduction()){
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                    if(loanApplication != null){
                        boolean callTConektaWS = false;
                        if(loanApplication.getStatus() != null && Arrays.asList(LoanApplicationStatus.NEW,LoanApplicationStatus.PRE_EVAL_APPROVED).contains(loanApplication.getStatus().getId())) callTConektaWS = true;
                        else if(loanApplication.getStatus() != null && loanApplication.getStatus().equals(LoanApplicationStatus.EVAL_APPROVED)){
                            List<LoanOffer> offers = loanApplicationDao.getLoanOffers(loanApplication.getId());
                            if (offers == null || offers.isEmpty() || !offers.stream().anyMatch(e -> e.getSelected() != null && e.getSelected())) callTConektaWS = true;
                        }
                    }
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.BANTOTAL_AUTHENTICATION: {
                AuthenticationResponse authenticationResponse = btApiRestService.authenticateRequest(Entity.AZTECA);
                externalDAO.insertBantotalToken(authenticationResponse.getSessionToken());
                registerSucces(queryBot);
                break;
            }
            case Bot.MONITOR_SERVERS_AWS: {
                Date now = new Date();

                List<String> appNames = Arrays.asList(ServerStatus.ENTITY_APP_NAME,ServerStatus.SCHEDULER_APP_NAME,ServerStatus.JOB_APP_NAME,ServerStatus.LANDING_APP_NAME,ServerStatus.ACQUISITION_APP_NAME);

                for (String app : appNames) {
                    ServerStatus serverStatus = new ServerStatus();
                    DescribeServicesResult result = awsECSService.getECSStatus(serverStatus.generateClusterName(app),serverStatus.generateServiceName(app));
                    serverStatus.setApp(app);
                    serverStatus.setRegisterDate(now);
                    serverStatus.setCustomState(result != null && result.getServices() != null && !result.getServices().isEmpty() ? serverStatus.convertECSStatusToServerStatus(result.getServices().get(0)) : "down");
                    securityDAO.registerServerStatus(serverStatus);
                }
                registerSucces(queryBot);
                break;
            }
            case Bot.BANBIF_KONECTA_LEAD: {
                List<Integer> loanIds = loanApplicationDao.getLoanToSendBanbifKonectaLead();
                for(Integer loanId : loanIds)
                    banBifService.callKonectaLead(loanId);
                registerSucces(queryBot);
                break;
            }
            case Bot.BANBIF_EXPIRE_LOANS_NEW_BASE: {
                Date baseValidUntil = rccDao.getBanbifBaseValidUntil();
                Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
                if(currentDate.after(baseValidUntil) && utilService.daysBetween(baseValidUntil, currentDate) == 1){
                    logger.info("Limpiando la base banbif");
                    loanApplicationDao.expireBanbifNewBaseLoans();
                }else {
                    logger.info("Aun no se debe limpiar la base banbif");
                }
                registerSucces(queryBot);
                break;
            }
        }
    }

    public abstract class FutureTask {

        private boolean stillRunning = true;

        abstract void runTask() throws Exception;

        abstract void onException() throws Exception;

        public boolean isStillRunning() {
            return stillRunning;
        }

        public void setStillRunning(boolean stillRunning) {
            this.stillRunning = stillRunning;
        }
    }

//    public boolean runFutureTask(FutureTask task, int retries, int timeout, TimeUnit timeUnit) {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(() -> {
//            int counter = 0;
//            while (counter < retries) {
//                try {
//                    return task.runTask();
//                } catch (Throwable th) {
//                    ErrorServiceImpl.onErrorStatic(th);
//                }
//                counter++;
//            }
//            return false;
//        });
//
//        boolean result = false;
//        try {
//            result = future.get(timeout, timeUnit);
//        } catch (Throwable th) {
//            ErrorServiceImpl.onErrorStatic(th);
//            future.cancel(true);
//            executorService.shutdownNow();
//        }
//
//        return result;
//    }

    private Integer sendMarketingCampaignInteraction(String fromEmail, String senderName, Character sendType, String destination, String headerImageUrl, String emailTemplate, Integer personId, String body, String subject, Integer providerId,Map<String, String> templateVars) throws Exception{
        PersonInteraction interaction = new PersonInteraction();
        interaction.setInteractionContent(new InteractionContent());
        interaction.setDestination(destination);
        interaction.setPersonId(personId);
        interaction.setBody(body);
        interaction.getInteractionContent().setFromMail(fromEmail);
        interaction.setSenderName(senderName);
        interaction.getInteractionContent().setSubject(subject);

        if(providerId != null)
            interaction.setInteractionProvider(catalogService.getInteractionProvider(providerId));
        if(sendType == MarketingCampaign.EMAIL){
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            interaction.setTemplate(emailTemplate);
            interaction.getInteractionContent().setTemplate(emailTemplate);
        }else{
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        }
        templateVars.put("IMG_URL", headerImageUrl);
        JSONObject jsonVariables = new JSONObject();
        if (templateVars != null) {
            for (Map.Entry<String, String> entry : templateVars.entrySet()) {
                jsonVariables.put(entry.getKey(), entry.getValue());
            }
        }

        interactionService.sendPersonInteraction(interaction, jsonVariables, null, templateVars);
        return interaction.getId();
    }

    public void runFutureTask(FutureTask task, List<CompletableFuture> completables, ExecutorService executor, ExecutorService completableExecutor, int retries, int timeout, TimeUnit timeUnit) {

        completables.add(
                CompletableFuture.supplyAsync(() -> {
                    Future<Boolean> future = executor.submit(() -> {
                        int counter = 0;
                        while (counter < retries) {
                            try {
                                task.runTask();
                                return true;
                            } catch (Throwable th) {
                                ErrorServiceImpl.onErrorStatic(th);
                            }
                            counter++;
                        }
                        // If the process is here, that means that all the retries have exceptions
                        return false;
                    });


                    try {
                        Boolean result = future.get(timeout, timeUnit);
                        if (!result) {
                            task.setStillRunning(false);
                            task.onException();
                        }
                    } catch (Throwable th) {
                        ErrorServiceImpl.onErrorStatic(th);
                        future.cancel(true);
                        try {
                            task.setStillRunning(false);
                            task.onException();
                        } catch (Throwable th2) {
                            ErrorServiceImpl.onErrorStatic(th);
                        }
                    }
                    return null;
                }, completableExecutor)
        );
    }

    /**
     * This method re run the EVALUATION_PROCESS bot and update the ecaluatioNProcess to DELAYED only if the count of retrys is less than 5
     *
     * @param loanApplicationId
     * @return True if the evaluation has re runned or False if the number of retrys is greater than the maximum
     * @throws Exception
     */
    private boolean retryLoanApplicationPreliminaryEvaluation(int loanApplicationId, int entityId, int productId) throws Exception {
        boolean rerunEvaluationBot = loanApplicationService.runEntityEvaluationBot(loanApplicationId, entityId, productId, true, true);
        if (rerunEvaluationBot) {
            updatePreliminaryEvaluationStatus(EntityProductEvaluationsProcess.STATUS_RUNNING_DELAYED, loanApplicationId, entityId, productId);
        }
        return rerunEvaluationBot;
    }

    private boolean retryLoanApplicationEvaluation(int loanApplicationId, int entityId, int productId) throws Exception {
        boolean rerunEvaluationBot = loanApplicationService.runEntityEvaluationBot(loanApplicationId, entityId, productId, false, true);
        if (rerunEvaluationBot) {
            updateEvaluationStatus(EntityProductEvaluationsProcess.STATUS_RUNNING_DELAYED, loanApplicationId, entityId, productId);
        }
        return rerunEvaluationBot;
    }

    private void updatePreliminaryEvaluationStatus(char status, int loanApplicationId, int entityId, int productId) {
        entityProductEvaluationProcessDao.updatePreliminaryEvaluationStatus(status, loanApplicationId, entityId, productId);
        loanApplicationDao.updateLoanApplicationPreliminaryEvaluationStatus(loanApplicationId, entityId, productId, status);
    }

    private void updateEvaluationStatus(char status, int loanApplicationId, int entityId, int productId) {
        entityProductEvaluationProcessDao.updateEvaluationStatus(status, loanApplicationId, entityId, productId);
        loanApplicationDao.updateLoanApplicationEvaluationStatus(loanApplicationId, entityId, productId, status);
    }

    private void registerRunning(QueryBot queryBot) throws Exception {
        queryBot.setStatusId(QueryBot.STATUS_RUNNING);
        botDao.updateQuery(queryBot);
    }

    protected void registerSucces(QueryBot queryBot) throws Exception {
        queryBot.setStatusId(QueryBot.STATUS_SUCCESS);
        queryBot.setFinishTime(new Date());
        botDao.updateQuery(queryBot);
    }

    protected void registerSucces(QueryBot queryBot, Proxy proxy) throws Exception {
        queryBot.setStatusId(QueryBot.STATUS_SUCCESS);
        queryBot.setFinishTime(new Date());
        botDao.updateQuery(queryBot);

        if (proxy != null) {
            proxy.setBussy(false);
            botDao.registerProxyRequestLog(proxy.getId(), queryBot.getId(), true);
        }
    }

    protected void registerFailure(QueryBot queryBot) throws Exception {
        queryBot.setStatusId(QueryBot.STATUS_FAIL);
        queryBot.setFinishTime(new Date());
        botDao.updateQuery(queryBot);
    }


    protected void registerFailure(QueryBot queryBot, Proxy proxy) throws Exception {
        queryBot.setStatusId(QueryBot.STATUS_FAIL);
        queryBot.setFinishTime(new Date());
        botDao.updateQuery(queryBot);

        if (proxy != null) {
            proxy.setBussy(false);
            botDao.registerProxyRequestLog(proxy.getId(), queryBot.getId(), false);
        }
    }

    private void sendEvaluationReadyInteraction(LoanApplication loanApplication, Person person, User user, Integer interactionContentId, String to) throws Exception {
        try {
            String loanApplicationToken = loanApplicationService.generateLoanApplicationToken(user.getId(), person.getId(), loanApplication.getId());
            JSONObject jsonVars = new JSONObject();
            //jsonVars.put("LINK", Configuration.getClientDomain() + "/salaryadvanceloanapplication/" + loanApplicationToken + "/offer/validate/email");
            jsonVars.put("CLIENT_NAME", person.getFirstName());

            EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
            CountryParam countryParam = catalogService.getCountryParam(loanApplication.getCountryId());

            if (entityBranding != null && entityBranding.getBranded()) {
                jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent().getName());
                jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent().getAvatarUrl2() != null ? loanApplication.getAgent().getAvatarUrl2() : loanApplication.getAgent().getAvatarUrl());
            } else {
                jsonVars.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COMMERCIAL);
                jsonVars.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COMMERCIAL);
            }

            if (entityBranding != null && entityBranding.getBranded()) {
                jsonVars.put("LINK", "https://" + entityBranding.getSubdomain() + "." + countryParam.getDomains().get(0) + "/credito-de-consumo/evaluacion/" + loanApplicationToken);
            } else {
                String subdomain = "";
                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                    if (Configuration.hostEnvIsDev()) subdomain = "dev.";
                    if (Configuration.hostEnvIsStage()) subdomain = "stg.";
                }
                jsonVars.put("LINK", "https://" + subdomain + countryParam.getDomains().get(0) + "/credito-de-consumo/evaluacion/" + loanApplicationToken);
            }

            PersonInteraction interaction = new PersonInteraction();
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
            interaction.setDestination(to);
            interaction.setLoanApplicationId(loanApplication.getId());
            interaction.setPersonId(person.getId());

            interactionService.sendPersonInteraction(interaction, jsonVars, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        String docnumber = "70771112";

        JSONObject response = JsonUtil.postJSONObjectFromUrl(
                "https://www.virginmobile.pe/getMobileNumbers", "{\"dni\": \"" + docnumber + "\"}",
                Optional.of("text/plain"));
        try {
            System.out.println("code: " + response.getInt("code"));
            JSONArray arr = response.getJSONArray("description");
            for (int i = 0; i < arr.length(); i++) {
                System.out.println("description " + i + ": " + arr.getString(i));
            }
            JSONArray arrD = response.getJSONArray("data");
            for (int i = 0; i < arrD.length(); i++) {
                System.out.println("description " + i + ": " + arrD.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String>  fillJsonWithAztecaCobranzaBase(AztecaGetawayBase aztecaGetawayBase) {
        Map<String, String> templateVars = new HashMap<>();
        templateVars.put("PAIS", aztecaGetawayBase.getPais());
        templateVars.put("TIPO_DOCUMENTO", aztecaGetawayBase.getTipoDocumento());
        templateVars.put("NUMERO_DOCUMENTO", aztecaGetawayBase.getNumeroDocumento());
        templateVars.put("NOMBRE", aztecaGetawayBase.getNombre());
        templateVars.put("AP_PATERNO", aztecaGetawayBase.getApPaterno());
        templateVars.put("AP_MATERNO", aztecaGetawayBase.getApMaterno());
        templateVars.put("CELULAR_1", aztecaGetawayBase.getCelular1());
        templateVars.put("CELULAR_2", aztecaGetawayBase.getCelular2());
        templateVars.put("CELULAR_3", aztecaGetawayBase.getCelular3());
        templateVars.put("CELULAR_4", aztecaGetawayBase.getCelular4());
        templateVars.put("CELULAR_5", aztecaGetawayBase.getCelular5());
        templateVars.put("SALDO_CAPITAL", aztecaGetawayBase.getSaldoCapital() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoCapital(),2) : null);
        templateVars.put("SALDO_INTERES", aztecaGetawayBase.getSaldoInteres() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoInteres(),2) : null);
        templateVars.put("SALDO_MORATORIO", aztecaGetawayBase.getSaldoMoratorio() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoMoratorio(),2) : null);
        templateVars.put("SALDO_TOTAL", aztecaGetawayBase.getSaldoTotal() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getSaldoTotal(),2) : null);
        templateVars.put("DIAS_ATRASO", aztecaGetawayBase.getDiasAtrazo() != null ? aztecaGetawayBase.getDiasAtrazo().toString() : null);
        templateVars.put("MONTO_CAMPANIA", aztecaGetawayBase.getMontoCampania() != null ? utilService.customDoubleFormat(aztecaGetawayBase.getMontoCampania(),2) : null);
        templateVars.put("VENCIMIENTO_CAMPANIA", aztecaGetawayBase.getVencimientoCampania() != null ? utilService.dateFormat( aztecaGetawayBase.getVencimientoCampania()) : null);
        templateVars.put("DOMICILIO", aztecaGetawayBase.getDomicilio());
        templateVars.put("DEPARTAMENTO", aztecaGetawayBase.getDepartamento());
        templateVars.put("PROVINCIA", aztecaGetawayBase.getProvincia());
        templateVars.put("DISTRITO", aztecaGetawayBase.getDistrito());
        templateVars.put("CODIGO_CLIENTE_EXTERNO", aztecaGetawayBase.getCodigoClienteExterno());
        templateVars.put("CORREO_1", aztecaGetawayBase.getCorreo1());
        templateVars.put("CORREO_2", aztecaGetawayBase.getCorreo2());
        return templateVars;
    }

    private String getSenderEmailToUse(EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration) {

        String EMAIL_DEFAULT = "hola@solven.pe";

        if (marketingCampaignConfiguration == null || marketingCampaignConfiguration.getEmail() == null)
            return EMAIL_DEFAULT;

        String status = awsSesEmailService.getVerificationEmailStatus(marketingCampaignConfiguration.getEmail());

        if (status.equalsIgnoreCase("Success")) {
            return marketingCampaignConfiguration.getEmail();
        } else {
            return EMAIL_DEFAULT;
        }

    }

    private String additionalParamsLinkCampaign(MarketingCampaign marketingCampaign){
        String source = "";
        String medium = "";
        String utmCampaign = "";
        String utmContent = "";
        if(marketingCampaign != null){
            source = marketingCampaign.getType().equals(MarketingCampaign.EMAIL) ? "MAIL" : (marketingCampaign.getType().equals(MarketingCampaign.SMS) ? "" : "");
            medium = source;
            utmCampaign = marketingCampaign.getName().replaceAll(" ","_");
            utmContent = marketingCampaign.getJsTemplate().getName().replaceAll(" ","_");
        }
        return "?utm_source=".concat(source)
                .concat("&utm_medium=").concat(medium)
                .concat("&utm_campaign=").concat(utmCampaign)
                .concat("&utm_content=").concat(utmContent)
                .concat("&marketing_campaign=").concat(marketingCampaign.getMarketingCampaignId().toString());
    }
}