package com.affirm.jobs.controller;

import com.affirm.common.service.ErrorService;
import com.affirm.jobs.model.WorkerSqsRunnable;
import com.affirm.jobs.service.impl.QueryBotService;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.kdgregory.log4j.aws.CloudWatchAppender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 03/10/16.
 */
@Component
public class WorkerInitializer {
    private static final Logger logger = Logger.getLogger(WorkerInitializer.class);

    private final QueryBotService queryBotService;
    private final AmazonSQSClient amazonSQSClient;
    private final ErrorService errorService;

    private boolean runThreads = true;
    private static final int PRINT_THREADS_STATS_SEC = 20 * 1000;

    private List<WorkerSqsRunnable> scrapperThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> scheduleThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> evaluationInitializerThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> evaluationProcessorThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> reportThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> fraudAlertsThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> approveLoanApplicationThreads = new ArrayList<>();
    private List<WorkerSqsRunnable> defaultThreads = new ArrayList<>();

    public WorkerInitializer(QueryBotService queryBotService, AmazonSQSClient amazonSQSClient, ErrorService errorService) {
        this.queryBotService = queryBotService;
        this.amazonSQSClient = amazonSQSClient;
        this.errorService = errorService;
    }

    public static void main(String[] args) {

        // Only for local enviroment
        // See instructions in SqsLocalServer
        if (Configuration.hostEnvIsLocal()) {
            SqsLocalServer sqsLocalServer = new SqsLocalServer();
            sqsLocalServer.build();
        }

        System.out.println("Configurando Log4j");

        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.ALL);
        console.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(console);

        if (Configuration.hostEnvIsNotLocal()) {
            CloudWatchAppender cloudwatch = new CloudWatchAppender();
            cloudwatch.setLayout(new PatternLayout("%d{ABSOLUTE} %5p %c{1}:%L - %m%n"));
            cloudwatch.setLogGroup(String.format("solven-%s-job", Configuration.hostEnvIsDev() ? "dev" : Configuration.hostEnvIsStage() ? "stg" : "prd"));
            cloudwatch.setLogStream("{startupTimestamp}-{sequence}");
            cloudwatch.setBatchDelay(2500);
            cloudwatch.setRotationMode("daily");
            Logger.getRootLogger().addAppender(cloudwatch);
        }

        Logger.getLogger("org.springframework").setLevel(Level.ERROR);
        Logger.getLogger("org.apache.http").setLevel(Level.ERROR);
        Logger.getLogger("com.amazonaws").setLevel(Level.ERROR);

        // Java Params
        System.setProperty("java.awt.headless", "true");

        // Configure Spring
        new AnnotationConfigApplicationContext("com.affirm");
    }


    @PostConstruct
    private void initWebscrapperQueue() {
        initQueue(Configuration.SqsQueue.SCRAPPER, scrapperThreads);
    }

    @PostConstruct
    private void initScheduleQueue() {
        initQueue(Configuration.SqsQueue.SCHEDULE, scheduleThreads);
    }

    @PostConstruct
    private void initEvaluationInitializerQueue() {
        initQueue(Configuration.SqsQueue.EVALUATION_INITIALIZER, evaluationInitializerThreads);
    }

    @PostConstruct
    private void initEvaluationProcessorQueue() {
        initQueue(Configuration.SqsQueue.EVALUATION_PROCESSOR, evaluationProcessorThreads);
    }

    @PostConstruct
    private void initReportQueue() {
        initQueue(Configuration.SqsQueue.REPORT, reportThreads);
    }

    @PostConstruct
    private void initFraudAlertsQueue() {
        initQueue(Configuration.SqsQueue.FRAUD_ALERTS, fraudAlertsThreads);
    }

    @PostConstruct
    private void initApproveLoanApplicationQueue() {
        initQueue(Configuration.SqsQueue.APPROVE_LOAN_APPLICATION, approveLoanApplicationThreads);
    }

    @PostConstruct
    private void initDefaultQueue() {
        initQueue(Configuration.SqsQueue.DEFAULT, defaultThreads);
    }

    private void initQueue(Configuration.SqsQueue sqsQueue, List<WorkerSqsRunnable> queueThreadList) {
        if (!Configuration.isClient()) {
            logger.debug("Initializing " + sqsQueue.name() + " Queue Listener");
            initThreads(Configuration.queueThreads(sqsQueue), queueThreadList, Configuration.queueUrl(sqsQueue));
        }
    }

    @PreDestroy
    private void clean() {
        runThreads = false;
    }

    private void initThreads(int threads, List<WorkerSqsRunnable> listToSave, String sqsUrl) {
        for (int i = 0; i < threads; i++) {
            WorkerSqsRunnable r = new WorkerSqsRunnable(queryBotService, amazonSQSClient, errorService, sqsUrl);
            listToSave.add(r);
            Thread t = new Thread(r);
            t.start();
        }

        statusThreads(listToSave, sqsUrl.substring(sqsUrl.lastIndexOf("/") + 1));
    }

    private void statusThreads(List<WorkerSqsRunnable> threadList, String queue) {
        new Thread(() -> {
            System.out.println(" >>>>>> Stats " + queue + " Queue initialized!");
            while (runThreads) {
                try {
                    Thread.sleep(PRINT_THREADS_STATS_SEC);
                    String stats = " >>>>> STATS " + queue + " <<<<< \n";
                    for (WorkerSqsRunnable runnable : threadList) {
                        stats = stats + " - " + runnable.getThreadName() + " - " + (runnable.getStatus() == WorkerSqsRunnable.STATUS_WORKING ? "working -> " + runnable.getCurrentQueryBotId() + " -> " + runnable.getWorkingTimeInMs() : "waiting") + "\n";
                    }
                    stats = stats + " <<<<<<<< >>>>>>>>";
                    System.out.println(stats);
                } catch (Throwable ex) {
                    logger.error("Stats thread fail", ex);
                }
            }
            System.out.println(" <<<<< Stats Thread finish!");
        }).start();
    }

}