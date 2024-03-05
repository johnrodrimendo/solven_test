package com.affirm.system.configuration;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.interceptor.*;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.ExtranetService;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.system.configuration.Configuration.Application;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author jrodriguez
 * <p>
 * This is the configuration for the beans that only the client gonna use, including the controller.
 * If there are beans that gonna be used for the backoffice and the client, it should be in the lass SpringWebConfiguration
 */
@EnableAspectJAutoProxy
@EnableScheduling
@Configuration
@ComponentScan(value = {
        "com.affirm.latam.controller",
        "com.affirm.latam.dao.impl",
        "com.affirm.client.controller.rest",
        "com.affirm.client.controller.questions",
        "com.affirm.client.controller",
        "com.affirm.client.service.impl",
        "com.affirm.client.dao.impl",
        "com.affirm.client.model.form",
//        "com.affirm.client.model.messengerbot.modular",
//        "com.affirm.client.model.messengerbot.utils",
        "com.affirm.dialogflow",
        "com.affirm.client.model.aspect",
        "com.affirm.abaco.service.impl",
        "com.affirm.api.endpoint.soap.impl",
        "com.affirm.api.endpoint.soap.service"})
public class SpringClientConfiguration extends WebMvcConfigurerAdapter {

    private static Logger logger = Logger.getLogger(SpringClientConfiguration.class);

    @Autowired
    net.sf.ehcache.CacheManager ehCacheManager;
    @Resource(name = "trxDataSource")
    DataSource dataSource;
    @Autowired
    ExtranetService extranetService;
    @Autowired
    EntityExtranetService entityExtranetService;
    @Autowired
    EmployerCLDAO employerDao;


    @PostConstruct
    public void init() {
        System.setProperty("application", Application.CLIENT.name());
    }

    @Bean
    public SelfEvaluationInterceptor selfEvaluationInterceptor() {
        return new SelfEvaluationInterceptor();
    }

    @Bean
    public LoanApplicationInterceptor loanApplicationInterceptor() {
        return new LoanApplicationInterceptor();
    }

    @Bean
    public RestApiInterceptor restApiInterceptor() {
        return new RestApiInterceptor();
    }

    @Bean
    public ProcessQuestionInterceptor processQuestionInterceptor() {
        return new ProcessQuestionInterceptor();
    }

    @Bean
    public TwoFactorAuthentificationInterceptor twoFactorAuthentificationInterceptor() {
        return new TwoFactorAuthentificationInterceptor();
    }

    @Bean
    public BrandingInterceptor brandingInterceptor() {
        return new BrandingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(selfEvaluationInterceptor()).addPathPatterns(
                "/" + com.affirm.system.configuration.Configuration.SELF_EVALUATION_CONTROLLER_URL + "/**");
        registry.addInterceptor(loanApplicationInterceptor()).addPathPatterns(
                "/" + com.affirm.system.configuration.Configuration.SALARY_ADVANCE_CONTROLLER_URL + "/**",
                "/" + com.affirm.system.configuration.Configuration.EVALUATION_CONTROLLER_URL + "/**",
                "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/**",
                "/" + ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "/**",
                "/" + ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "/**",
                "/" + ProductCategory.GATEWAY_URL + "/**",
                "/" + ProductCategory.CUENTA_BANCARIA_URL + "/**",
                "/" + ProductCategory.VALIDACION_IDENTIDAD_URL + "/**",
                "/" + ProductCategory.CONSEJ0_URL + "/**",
                "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/**");
        registry.addInterceptor(restApiInterceptor()).addPathPatterns("/api/**");
        registry.addInterceptor(restApiInterceptor()).addPathPatterns("/creditoApi/**");
        registry.addInterceptor(processQuestionInterceptor()).addPathPatterns("/" + com.affirm.system.configuration.Configuration.EVALUATION_CONTROLLER_URL + "/question/**", "/" + com.affirm.system.configuration.Configuration.SELF_EVALUATION_CONTROLLER_URL + "/question/**", "/" + com.affirm.system.configuration.Configuration.COMPARISON_CONTROLLER_URL + "/question/**");
        registry.addInterceptor(brandingInterceptor()).addPathPatterns(
                "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/**",
                "/" + ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "/**",
                "/" + ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "/**",
                "/" + ProductCategory.GATEWAY_URL + "/**",
                "/" + ProductCategory.CUENTA_BANCARIA_URL + "/**",
                "/" + ProductCategory.VALIDACION_IDENTIDAD_URL + "/**",
                "/" + ProductCategory.CONSEJ0_URL + "/**",
                "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourcesMode = System.getenv("RESOURCES_DEVELOPER");
        int cachePeriod = 31556926;

        VersionResourceResolver versionResolver = null;
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            versionResolver = new VersionResourceResolver();
            versionResolver = versionResolver.addContentVersionStrategy("/**/*.css", "/**/*.js", "/**/*.eot", "/**/*.svg", "/**/*.ttf", "/**/*.woff");
        }

        ResourceHandlerRegistration cssResourceHadler = registry.addResourceHandler("/css/**").addResourceLocations("/resources/theme_default/build/css/", "classpath:/clientResources/theme_default/build/css/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            cssResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
        ResourceHandlerRegistration jsResourceHadler = registry.addResourceHandler("/js/**").addResourceLocations("/resources/theme_default/build/js/", "classpath:/clientResources/theme_default/build/js/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            jsResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
        ResourceHandlerRegistration imgResourceHadler = registry.addResourceHandler("/img/**").addResourceLocations("/resources/theme_default/build/img/", "classpath:/clientResources/theme_default/build/img/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            imgResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
        ResourceHandlerRegistration fontResourceHadler = registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/theme_default/fonts/", "classpath:/clientResources/theme_default/fonts/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            fontResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
        ResourceHandlerRegistration externalResourceHadler = registry.addResourceHandler("/external/**").addResourceLocations("/resources/external/", "classpath:/clientResources/external/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            externalResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
        ResourceHandlerRegistration staticResourceHadler = registry.addResourceHandler("/static/**").addResourceLocations("/resources/static/", "classpath:/clientResources/static/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            staticResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
        ResourceHandlerRegistration documentsResourceHadler = registry.addResourceHandler("/documents/**").addResourceLocations("/resources/theme_default/build/documents/", "classpath:/clientResources/theme_default/build/documents/").setCachePeriod(cachePeriod);
        if (com.affirm.system.configuration.Configuration.hostEnvIsNotLocal()) {
            documentsResourceHadler.resourceChain(true).addResolver(versionResolver);
        }
    }

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

    @Bean
    public TemplateResolver htmlTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setPrefix("/html/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        return templateResolver;
    }

    @Bean
    public TemplateResolver commonHtmlTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setPrefix("classpath:/html/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(2);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addTemplateResolver(commonHtmlTemplateResolver());
        templateEngine.addDialect(new ShiroDialect());
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setOrder(1);
        return viewResolver;
    }
}