package com.affirm.schedule.controller;

import com.affirm.system.configuration.Configuration;
import com.kdgregory.log4j.aws.CloudWatchAppender;
import org.apache.log4j.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by john on 04/10/16.
 */
public class ScheduleInitializer {

    public static void main(String[] args) {
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
            cloudwatch.setLogGroup(String.format("solven-%s-schedule", Configuration.hostEnvIsDev() ? "dev" : Configuration.hostEnvIsStage() ? "stg" : "prd"));
            cloudwatch.setLogStream("{startupTimestamp}-{sequence}");
            cloudwatch.setBatchDelay(2500);
            cloudwatch.setRotationMode("daily");
            Logger.getRootLogger().addAppender(cloudwatch);
        }

        Logger.getLogger("org.springframework").setLevel(Level.ERROR);
        Logger.getLogger("org.apache.http").setLevel(Level.ERROR);
        Logger.getLogger("com.amazonaws").setLevel(Level.ERROR);

        System.out.println("Configurando spring");
        ApplicationContext context = new AnnotationConfigApplicationContext("com.affirm");
        System.out.println("Finalizó configuración");
    }
}
