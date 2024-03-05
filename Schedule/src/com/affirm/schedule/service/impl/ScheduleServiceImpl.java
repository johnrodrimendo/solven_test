package com.affirm.schedule.service.impl;

import com.DeathByCaptcha.SocketClient;
import com.affirm.common.dao.BotDAO;
import com.affirm.common.dao.ConfigDAO;
import com.affirm.common.dao.ReportsDAO;
import com.affirm.common.model.catalog.Bot;
import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.QueryBot;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.currencylayer.CurrencyLayerApi;
import com.affirm.schedule.service.ScheduleService;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by john on 04/10/16.
 */
@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {
    private static final Logger logger = Logger.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private BotDAO botDao;
    @Autowired
    private AmazonSQSClient amazonSQSClient;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private ConfigDAO configDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private ReportsDAO reportsDao;

    private SocketClient captchaClient = new SocketClient("fturconi", "ostk2004");

    @Override
    public void updateExchangeRateTask() throws Exception {
        logger.debug("updateExchangeRateTask");
        Double newUSDPEN = new CurrencyLayerApi().fetchUSDPEN();
        System.out.println("New USDPEN: " + newUSDPEN);
        configDAO.updateExchangeRateUSDPEN(newUSDPEN);
    }

    @Override
    public void runLowCaptchaAlertTask() throws Exception {
        logger.debug("runLowCaptchaAlertTask");
        double balance = captchaClient.getBalance();
        logger.info("There is " + balance + " captchas in our balance.");
        if (balance < 200) {
            String from = "alertas@solven.pe";
            String to = Configuration.EMAIL_ERROR_TO();
            String subject = "Security Alert in " + Configuration.hostEnvName();

            awsSesEmailService.sendEmail(from, to, null, subject, "There is only " + balance + " Captchas left. Buy more captchas so robots can continue.", null, null);
        }
    }

    @Override
    public void runBufferTask() throws Exception {
        logger.debug("runBufferTask");
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BUFFER, QueryBot.STATUS_QUEUE, new Date(), null, null);

        logger.debug("poniendo en cola: runBufferTask");
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));
    }

    @Override
    public void runManagementTask() throws Exception {
        // Register query
        System.out.println("runManagementTask");
        QueryBot queryBot = botDao.registerQuery(Bot.MANAGEMENT_SCHEDULE, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runManagementTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runDailyInteractionsTask() throws Exception {
        // Register query
        logger.debug("runDailyInteractionsTask");
        QueryBot queryBot = botDao.registerQuery(Bot.DAILY_INTERACTIONS, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runDailyInteractionsTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runSbsBankRatesTask() throws Exception {
        // Register query
        logger.debug("runSbsBankRatesTask");
        QueryBot queryBot = botDao.registerQuery(Bot.SBS_B_BOT, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runSbsBankRatesTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));
    }

    @Override
    public void rubSbsTopRateTask() throws Exception {
        // Register query
        logger.debug("rubSbsTopRateTask");
        QueryBot queryBot = botDao.registerQuery(Bot.SBS_B_BOT, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: rubSbsTopRateTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));
    }

    @Override
    public void runDailyProcess() throws Exception {
        // Register query
        logger.debug("runDailyProcess");
        QueryBot queryBot = botDao.registerQuery(Bot.DAILY_PROCESS, QueryBot.STATUS_RUNNING, new Date(), null, null);
        logger.debug("ejecutando: runDailyProcess " + queryBot.getId());
        botDao.dailyProccess();
        logger.debug("terminando: runDailyProcess " + queryBot.getId());
        queryBot.setStatusId(QueryBot.STATUS_SUCCESS);
        queryBot.setFinishTime(new Date());
        botDao.updateQuery(queryBot);
    }

    @Override
    public void runAccesoSignatureStatusTask() throws Exception {
        // Register query
        logger.debug("runAccesoSignatureStatusTask");
        QueryBot queryBot = botDao.registerQuery(Bot.ACCESO_SIGNATURE_STATUS, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runAccesoSignatureStatusTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));
    }

    @Override
    public void runAccesoVehicleCatalogTask() throws Exception {
        // Register query
        logger.debug("runAccesoVehicleCatalogTask");
        QueryBot queryBot = botDao.registerQuery(Bot.ACCESO_VEHICLE_CATALOG, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runAccesoVehicleCatalogTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runRipleyDailyReportSender() throws Exception {
        // Register query
        logger.debug("runRipleyDailyReportSender");
        QueryBot queryBot = botDao.registerQuery(Bot.RIPLEY_REPORT_DAILY_SENDER, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runRipleyDailyReportSender " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runSendgridListManagementTask() throws Exception {
        // Register query
        logger.debug("runSendgridListManagementTask");
        QueryBot queryBot = botDao.registerQuery(Bot.SENDGRID_LIST_MANAGEMENT, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runSendgridListManagementTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runExchangeTask() throws Exception {
        // Register query
        logger.debug("runExchangeTask");
        QueryBot queryBot = botDao.registerQuery(Bot.EXCHANGE, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runExchangeTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));
    }

    @Override
    public void runaccesoSignaturePendingTask() throws Exception {
        // Register query
        logger.debug("runaccesoSignaturePendingTask");
        QueryBot queryBot = botDao.registerQuery(Bot.ACCESO_SIGNATURE_PENDING, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runaccesoSignaturePendingTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runAccesoDispatchTask() throws Exception {
        // Register query
        logger.debug("runAccesoDispatchTask");
        QueryBot queryBot = botDao.registerQuery(Bot.ACCESO_DISPATCH, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runAccesoDispatchTask " + queryBot.getId());
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runEndMonthCompanyResume() throws Exception {

        List<Employer> employersToProcess = reportsDao.getEmployersToSenEndOfMonthReport();
        if (employersToProcess == null || employersToProcess.isEmpty())
            return;

        List<Employer> employersToProcessWithoutGroup = employersToProcess.stream().filter(e -> e.getEmployerGroupId() == null).collect(Collectors.toList());
        List<Employer> employersToProcessWithGroup = employersToProcess.stream().filter(e -> e.getEmployerGroupId() != null).collect(Collectors.toList());
        int[] groupIds = employersToProcessWithGroup.stream().mapToInt(e -> e.getEmployerGroupId()).distinct().toArray();

        if (!employersToProcessWithoutGroup.isEmpty() || groupIds.length > 0) {
            JSONObject jsonToProcess = new JSONObject();
            JSONArray arrayEmployerIds = new JSONArray();
            for (Employer employer : employersToProcessWithoutGroup) {
                arrayEmployerIds.put(employer.getId());
            }
            jsonToProcess.put("employers", arrayEmployerIds);

            JSONArray arrayGroupIds = new JSONArray();
            for (int i : groupIds) {
                arrayGroupIds.put(i);
            }
            jsonToProcess.put("groups", arrayGroupIds);

            QueryBot queryBot = botDao.registerQuery(Bot.END_MONTH_COMPANY_RESSUME, QueryBot.STATUS_QUEUE, new Date(), jsonToProcess, null);
            amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
        }
    }

    @Override
    public void runScheduledBotsTask() throws Exception {
        List<QueryBot> queryBots = botDao.getScheduledQueryBots();
        if (queryBots != null) {
            for (QueryBot bot : queryBots) {
                bot.setStatusId(QueryBot.STATUS_QUEUE);
                botDao.updateQuery(bot);
                amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), bot.getId() + ""));
            }
        }
    }

    @Override
    public void runAutomaticInteractionTask() throws Exception {
        QueryBot queryBot = botDao.registerQuery(Bot.AUTOMATIC_INTERACTION, QueryBot.STATUS_QUEUE, new Date(), null, null);
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runReEvaluationEmailSender() throws Exception {
        QueryBot queryBot = botDao.registerQuery(Bot.RE_EVALUATION_EMAIL_SENDER, QueryBot.STATUS_QUEUE, new Date(), null, null);
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runTarjetasPeruanasyActivationDailySender() throws Exception {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.TARJETAS_PERUANAS_ACTIVATION_DAILY_SENDER, QueryBot.STATUS_QUEUE, new Date(), null, null);
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runHourlyInteractions() throws Exception {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.HOURLY_INTERACTIONS, QueryBot.STATUS_QUEUE, new Date(), null, null);
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runAutoplanLeadsDailySender() throws Exception {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.AUTOPLAN_LEADS_DAILY_SENDER, QueryBot.STATUS_QUEUE, new Date(), null, null);
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runMonitorServersTask() {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.MONITOR_SERVERS, QueryBot.STATUS_QUEUE, new Date(), null, null);
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runMonitorServersAWSTask() {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.MONITOR_SERVERS_AWS, QueryBot.STATUS_QUEUE, new Date(), null, null);
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runBanBifConversionsTask() {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BANBIF_CONVERSIONS, QueryBot.STATUS_QUEUE, new Date(), null, null);
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runBantotalAuthenticationTask() {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BANTOTAL_AUTHENTICATION, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runBantotalAuthenticationTask");
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runBanbifKonectaLeadTask() {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BANBIF_KONECTA_LEAD, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runBanbifKonectaLeadTask");
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }

    @Override
    public void runBanbifExpireLoansNewBaseTask() {
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BANBIF_EXPIRE_LOANS_NEW_BASE, QueryBot.STATUS_QUEUE, new Date(), null, null);
        logger.debug("poniendo en cola: runBanbifExpireLoansNewBaseTask");
        // Call AWS SQS
        amazonSQSClient.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCHEDULE), queryBot.getId() + ""));
    }
}