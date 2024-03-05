package com.affirm.system.configuration;

import com.affirm.common.model.interceptor.CountryContextInterceptor;
import com.affirm.security.model.CSRFInterceptor;
import com.affirm.security.model.SecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * @author jrodriguez
 *         <p>
 *         This is the configuration for the controller layer. <br/>
 *         This has the beans that are gonna be used commonly by the client and backoffice profile.
 *         For specific beans for any profile, visit SpringClientConfiguration or SpringBackofficeConfiguration
 */
@EnableWebMvc
@Configuration
@ComponentScan(value = {
        "com.affirm.common.controller",
        "com.affirm.common.model.interceptor",
        "com.affirm.common.model.aspect"
})
public class SpringWebConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSizePerFile(com.affirm.system.configuration.Configuration.MAX_UPLOAD_FILE_SIZE());
        resolver.setMaxUploadSize(com.affirm.system.configuration.Configuration.MAX_REQUEST_SIZE());
        return resolver;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("es", "PE"));
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    public SecurityInterceptor securityInterceptor() {
        SecurityInterceptor securityInterceptor = new SecurityInterceptor();
        return securityInterceptor;
    }

    @Bean
    public CSRFInterceptor csrfInterceptor() {
        CSRFInterceptor csrfInterceptor = new CSRFInterceptor();
        return csrfInterceptor;
    }

    @Bean
    public CountryContextInterceptor countryContextInterceptor() {
        return new CountryContextInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(securityInterceptor()).excludePathPatterns("/api/**","/health/**", "/AW9lgiio1rtiz5,WA8y*hdvSxvEMiHJG", "/chatbot/**", "googleca9b0a9f80a77e8a.html");
        registry.addInterceptor(csrfInterceptor()).excludePathPatterns("/api/**","/health/**", "/chatbot/**", "/loanapplication/updatePlayerId","/evaluacion/question/171/verifyPayment");
        registry.addInterceptor(countryContextInterceptor()).excludePathPatterns("/api/**","/health/**", "/chatbot/**", "/public/zipAllLoanDocuments/**");
    }
}