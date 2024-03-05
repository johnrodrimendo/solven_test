package com.affirm.schedule.service;

/**
 * Created by john on 04/10/16.
 */
public interface ScheduleService {
    void updateExchangeRateTask() throws Exception;

    void runLowCaptchaAlertTask() throws Exception;

    void runBufferTask() throws Exception;

    void runManagementTask() throws Exception;

    void runDailyInteractionsTask() throws Exception;

    void runSbsBankRatesTask() throws Exception;

    void rubSbsTopRateTask() throws Exception;

    void runDailyProcess() throws Exception;

    void runAccesoSignatureStatusTask() throws Exception;

    void runRipleyDailyReportSender() throws Exception;

    void runSendgridListManagementTask() throws Exception;

    void runExchangeTask() throws Exception;

    void runAccesoVehicleCatalogTask() throws Exception;

    void runaccesoSignaturePendingTask() throws Exception;

    void runAccesoDispatchTask() throws Exception;

    void runEndMonthCompanyResume() throws Exception;

    void runScheduledBotsTask() throws Exception;

    void runAutomaticInteractionTask() throws Exception;

    void runReEvaluationEmailSender() throws Exception;

    void runTarjetasPeruanasyActivationDailySender() throws Exception;

    void runHourlyInteractions() throws Exception;

    void runAutoplanLeadsDailySender() throws Exception;

    void runMonitorServersTask();

    void runMonitorServersAWSTask();

    void runBanBifConversionsTask();

    void runBantotalAuthenticationTask();

    void runBanbifKonectaLeadTask();

    void runBanbifExpireLoansNewBaseTask();
}
