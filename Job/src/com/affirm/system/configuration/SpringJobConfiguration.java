package com.affirm.system.configuration;

import com.affirm.system.configuration.Configuration.Application;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author jrodriguez
 * <p>
 * This is the configuration for the beans that only the client gonna use, including the controller.
 * If there are beans that gonna be used for the backoffice and the client, it should be in the lass SpringWebConfiguration
 */
@Configuration
@ComponentScan({
        "com.affirm.jobs.controller",
        "com.affirm.jobs.dao.impl",
        "com.affirm.jobs.model.impl",
        "com.affirm.jobs.service.impl"})
public class SpringJobConfiguration {

    @PostConstruct
    public void init() {
        System.setProperty("application", Application.JOB.name());
    }
}
