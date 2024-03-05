package com.affirm.system.configuration;

import com.affirm.common.model.catalog.Bot;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration.Application;
import org.apache.log4j.Logger;
import org.quartz.Trigger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author jrodriguez
 */
@Configuration
@ComponentScan({
        "com.affirm.schedule.service",
        "com.affirm.schedule.controller",
        "com.affirm.common.service"
})
public class SpringScheduleConfiguration {
    private static final Logger logger = Logger.getLogger(SpringScheduleConfiguration.class);

    @Autowired
    CatalogService catalogService;

    @PostConstruct
    public void init() {
        System.setProperty("application", Application.SCHEDULE.name());
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean updateExchangeRateJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("updateExchangeRateTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean updateExchangeRateTriggerFactoryBeans() throws Exception {
        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(updateExchangeRateJobDetailFactoryBean().getObject());
        stFactory.setCronExpression(hourMinutesDailyCron(1, 1));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean lowCaptchaAlertJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runLowCaptchaAlertTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean lowCaptchaAlertTriggerFactoryBeans() throws Exception {
        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(lowCaptchaAlertJobDetailFactoryBean().getObject());
        stFactory.setCronExpression(hourMinutesDailyCron(4, 0));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean bufferJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runBufferTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean bufferTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.BUFFER);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(bufferJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bufferBot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean managementJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runManagementTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean managementTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.MANAGEMENT_SCHEDULE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(managementJobDetailFactoryBean().getObject());
        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean dailyProcessJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runDailyProcess");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean dailyProcessTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.DAILY_PROCESS);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        logger.debug("seteando daily process " + hours + ":" + min);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(dailyProcessJobDetailFactoryBean().getObject());
        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean banBifConversionsJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runBanBifConversionsTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean banBifConversionsTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.BANBIF_CONVERSIONS);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        logger.debug("seteando banBif conversions " + hours + ":" + min);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(banBifConversionsJobDetailFactoryBean().getObject());
        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean dailyInteractionsJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runDailyInteractionsTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean dailyInteractionsTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.DAILY_INTERACTIONS);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        logger.debug("seteando daily interactions " + hours + ":" + min);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(dailyInteractionsJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        //stFactory.setCronExpression(hourMinutesDailyCron(17, 34));

        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean sbsBankRatesJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runSbsBankRatesTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean sbsBankRatesTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.SBS_B_BOT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        logger.debug("seteando daily sbsBankRates " + hours + ":" + min);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(sbsBankRatesJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean accesoSignatureStatusJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runAccesoSignatureStatusTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean accesoSignatureStatusTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.ACCESO_SIGNATURE_STATUS);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(accesoSignatureStatusJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bufferBot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean accesoVehicleCatalogJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runAccesoVehicleCatalogTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean accesoVehicleCatalogTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.ACCESO_VEHICLE_CATALOG);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        logger.debug("seteando daily accesoVehicleCatalog " + hours + ":" + min);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(accesoVehicleCatalogJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean ripleyDailyReportSenderJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runRipleyDailyReportSender");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean ripleyDailyReportSenderTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.RIPLEY_REPORT_DAILY_SENDER);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(ripleyDailyReportSenderJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }


    @Bean
    public MethodInvokingJobDetailFactoryBean sendgridListManagementJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runSendgridListManagementTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean sendgridListManagementTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.SENDGRID_LIST_MANAGEMENT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(sendgridListManagementJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));

        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean exchangeJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runExchangeTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean exchangeTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.EXCHANGE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(exchangeJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));

        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean accesoSignaturePendingJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runaccesoSignaturePendingTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean accesoSignaturePendingTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.ACCESO_SIGNATURE_PENDING);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(accesoSignaturePendingJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));

        return stFactory;
    }

    // Dummy implementation
    @Bean
    public SimpleTriggerFactoryBean accesoSignaturePendingTriggerFactoryBeansDummy() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.ACCESO_SIGNATURE_PENDING);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(accesoSignaturePendingJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bufferBot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean accesoDispatchJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runAccesoDispatchTask");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean accesoDispatchTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.ACCESO_DISPATCH);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(accesoDispatchJobDetailFactoryBean().getObject());

        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));

        return stFactory;
    }

    // Dummy implementation
    @Bean
    public SimpleTriggerFactoryBean accesoDispatchTriggerFactoryBeansDummy() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.ACCESO_DISPATCH);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(accesoDispatchJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bufferBot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }


    @Bean
    public MethodInvokingJobDetailFactoryBean scheduledBotsJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runScheduledBotsTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean scheduledBotsTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.SCHEDULED_BOTS);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(scheduledBotsJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bufferBot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean endMonthCompanyResumeJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runEndMonthCompanyResume");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean endMonthCompanyResumeTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.END_MONTH_COMPANY_RESSUME);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bufferBot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(endMonthCompanyResumeJobDetailFactoryBean().getObject());
        stFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return stFactory;
    }


    @Bean
    public MethodInvokingJobDetailFactoryBean automaticInteractinoJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runAutomaticInteractionTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean automaticInteractinoTriggerFactoryBeans() throws Exception {
        Bot bufferBot = catalogService.getBot(Bot.AUTOMATIC_INTERACTION);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(automaticInteractinoJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bufferBot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean tarjetasPeruanasyActivationDailySenderJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runTarjetasPeruanasyActivationDailySender");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean tarjetasPeruanasyActivationDailySenderTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.TARJETAS_PERUANAS_ACTIVATION_DAILY_SENDER);
        return createCronFactoryBean(tarjetasPeruanasyActivationDailySenderJobDetailFactoryBean(), bot);
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean hourlyInteractionsJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runHourlyInteractions");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean hourlInteractionsTriggerFactoryBeans() throws Exception {
        CronTriggerFactoryBean cronFactory = new CronTriggerFactoryBean();
        cronFactory.setJobDetail(hourlyInteractionsJobDetailFactoryBean().getObject());
        cronFactory.setCronExpression("0 0 * * * ? *");
        return cronFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean autoplanLeadsDailySenderJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runAutoplanLeadsDailySender");
        return obj;
    }

    @Bean
    public CronTriggerFactoryBean autoplanLeadsDailySenderTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.AUTOPLAN_LEADS_DAILY_SENDER);
        return createCronFactoryBean(autoplanLeadsDailySenderJobDetailFactoryBean(), bot);
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean monitorServersJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runMonitorServersTask");
        return obj;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean monitorServersAWSJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runMonitorServersAWSTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean monitorServersTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.MONITOR_SERVERS);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(monitorServersJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean bantotalAuthenticationJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runBantotalAuthenticationTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean bantotalAuthenticationTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.BANTOTAL_AUTHENTICATION);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(bantotalAuthenticationJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean monitorServersAWSTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.MONITOR_SERVERS_AWS);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(monitorServersAWSJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean banbifKonectaLeadJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runBanbifKonectaLeadTask");
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean banbifKonectaLeadTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.BANBIF_KONECTA_LEAD);
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(banbifKonectaLeadJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(1000);
        stFactory.setRepeatInterval(bot.getIntervalMinute() * 60 * 1000);
        return stFactory;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean banbifExpireLoansJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("scheduleService");
        obj.setTargetMethod("runBanbifExpireLoansNewBaseTask");
        return obj;
    }



    @Bean
    public CronTriggerFactoryBean banbifExpireLoansTriggerFactoryBeans() throws Exception {
        Bot bot = catalogService.getBot(Bot.BANBIF_EXPIRE_LOANS_NEW_BASE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bot.getFixedTime());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        CronTriggerFactoryBean cronFactory = new CronTriggerFactoryBean();
        cronFactory.setJobDetail(banbifExpireLoansJobDetailFactoryBean().getObject());
        cronFactory.setCronExpression(hourMinutesDailyCron(hours, min));
        return cronFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws Exception {

        List<Trigger> triggers = new ArrayList<>();
        triggers.add(bufferTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.MANAGEMENT_SCHEDULE).getActive()) triggers.add(managementTriggerFactoryBeans().getObject());
        triggers.add(lowCaptchaAlertTriggerFactoryBeans().getObject());
        triggers.add(updateExchangeRateTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.SBS_B_BOT).getActive()) triggers.add(sbsBankRatesTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.DAILY_PROCESS).getActive()) triggers.add(dailyProcessTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.BANBIF_CONVERSIONS).getActive()) triggers.add(banBifConversionsTriggerFactoryBeans().getObject());
        //triggers.add(accesoSignatureStatusTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.ACCESO_VEHICLE_CATALOG).getActive()) triggers.add(accesoVehicleCatalogTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.RIPLEY_REPORT_DAILY_SENDER).getActive()) triggers.add(ripleyDailyReportSenderTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.SENDGRID_LIST_MANAGEMENT).getActive()) triggers.add(sendgridListManagementTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.EXCHANGE).getActive()) triggers.add(exchangeTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.SCHEDULED_BOTS).getActive()) triggers.add(scheduledBotsTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.END_MONTH_COMPANY_RESSUME).getActive()) triggers.add(endMonthCompanyResumeTriggerFactoryBeans().getObject());
        triggers.add(com.affirm.system.configuration.Configuration.ACCESO_DUMMY_WS() ? accesoSignaturePendingTriggerFactoryBeansDummy().getObject() : accesoSignaturePendingTriggerFactoryBeans().getObject());
        triggers.add(com.affirm.system.configuration.Configuration.ACCESO_DUMMY_WS() ? accesoDispatchTriggerFactoryBeansDummy().getObject() : accesoDispatchTriggerFactoryBeans().getObject());
        CronTriggerFactoryBean dailyInteraction = dailyInteractionsTriggerFactoryBeans();
        if (catalogService.getBot(Bot.DAILY_INTERACTIONS).getActive()) triggers.add(dailyInteraction.getObject());
        if (catalogService.getBot(Bot.AUTOMATIC_INTERACTION).getActive()) triggers.add(automaticInteractinoTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.TARJETAS_PERUANAS_ACTIVATION_DAILY_SENDER).getActive()) triggers.add(tarjetasPeruanasyActivationDailySenderTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.HOURLY_INTERACTIONS).getActive()) triggers.add(hourlInteractionsTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.AUTOPLAN_LEADS_DAILY_SENDER).getActive()) triggers.add(autoplanLeadsDailySenderTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.MONITOR_SERVERS).getActive()) triggers.add(monitorServersTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.BANTOTAL_AUTHENTICATION).getActive()) triggers.add(bantotalAuthenticationTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.MONITOR_SERVERS_AWS).getActive()) triggers.add(monitorServersAWSTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.BANBIF_KONECTA_LEAD).getActive()) triggers.add(banbifKonectaLeadTriggerFactoryBeans().getObject());
        if (catalogService.getBot(Bot.BANBIF_EXPIRE_LOANS_NEW_BASE).getActive()) triggers.add(banbifExpireLoansTriggerFactoryBeans().getObject());

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        return scheduler;
    }

    private String everyXminutesCron(int x) {
        return "0 0/" + x + " * 1/1 * ? *";
    }

    private String hourMinutesDailyCron(int hour, int minutes) {
        return "0 " + minutes + " " + hour + " * * ? *";
    }

    private String cronGenerator(int hour, int minutes, Integer dayOfMonth) {
        return "0 " + minutes + " " + hour + " " + (dayOfMonth != null ? dayOfMonth : "*") + " * ? *";
    }

    private CronTriggerFactoryBean createCronFactoryBean(MethodInvokingJobDetailFactoryBean method, Bot bot) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(bot.getFixedTime());
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        Integer dayOfMonth = bot.getFixedDayOfMonth();

        CronTriggerFactoryBean stFactory = new CronTriggerFactoryBean();
        stFactory.setJobDetail(method.getObject());
        stFactory.setCronExpression(cronGenerator(hour, min, dayOfMonth));
        return stFactory;
    }

}