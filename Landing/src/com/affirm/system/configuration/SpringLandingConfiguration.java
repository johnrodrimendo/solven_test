package com.affirm.system.configuration;

import com.affirm.client.model.interceptor.ProductCategoryInterceptor;
import com.affirm.client.model.interceptor.RedirectEntityBrandingInterceptor;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.util.AnyOfRolesAuthorizationFilter;
import com.affirm.common.util.CustomRedisManager;
import com.affirm.referrerExt.controller.ReferrerExtLandingController;
import com.affirm.referrerExt.interceptor.ReferrerExtSessionInterceptor;
import com.affirm.system.configuration.Configuration.Application;
import org.apache.log4j.Logger;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
        "com.affirm.landing.controller",
        "com.affirm.landing.service.impl",
        "com.affirm.landing.dao.impl",
        "com.affirm.referrerExt.controller",
        "com.affirm.referrerExt.service",
        "com.affirm.referrerExt.dao"})
public class SpringLandingConfiguration extends WebMvcConfigurerAdapter {

    private static Logger logger = Logger.getLogger(SpringLandingConfiguration.class);

    @Autowired
    net.sf.ehcache.CacheManager ehCacheManager;

    @PostConstruct
    public void init() {
        System.setProperty("application", Application.LANDING.name());
    }

    @Bean
    public RedirectEntityBrandingInterceptor redirectEntityBrandingInterceptor() {
        return new RedirectEntityBrandingInterceptor();
    }

    @Bean
    public ProductCategoryInterceptor productCategoryInterceptor() {
        return new ProductCategoryInterceptor();
    }

    @Bean
    public ReferrerExtSessionInterceptor referrerExtSessionInterceptor() {
        return new ReferrerExtSessionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(redirectEntityBrandingInterceptor()).addPathPatterns(
                "/",
                "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/**",
                "/" + ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "/**",
                "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/**",
                "/" + ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "/**",
                "/" + ProductCategory.GATEWAY_URL + "/**",
                "/" + ProductCategory.CUENTA_BANCARIA_URL + "/**",
                "/" + ProductCategory.VALIDACION_IDENTIDAD_URL + "/**",
                "/" + ProductCategory.CONSEJ0_URL + "/**"
        );
        registry.addInterceptor(productCategoryInterceptor()).addPathPatterns(
                "/" + ProductCategory.CONSUMO_CATEGORY_URL + "/**",
                "/" + ProductCategory.ADELANTO_SUELDO_CATEGORY_URL + "/**",
                "/" + ProductCategory.VEHICULO_CATEGORY_URL + "/**",
                "/" + ProductCategory.TARJETA_CREDITO_CATEGORY_URL + "/**",
                "/" + ProductCategory.GATEWAY_URL + "/**",
                "/" + ProductCategory.CUENTA_BANCARIA_URL + "/**",
                "/" + ProductCategory.VALIDACION_IDENTIDAD_URL + "/**",
                "/" + ProductCategory.CONSEJ0_URL + "/**"
        );
        registry.addInterceptor(referrerExtSessionInterceptor()).addPathPatterns(
                "/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL + "/**"
        ).excludePathPatterns(
                "/" + ReferrerExtLandingController.BASE_URI_EXT_REFERRAL
        );
    }

    ///////////////////////////////
    // SHIRO
    ///////////////////////////////

    @Bean
    public AnyOfRolesAuthorizationFilter anyofrole() {
        return new AnyOfRolesAuthorizationFilter();
    }

    ///////////////////////////////
    // SHIRO CONFIG
    ///////////////////////////////

    @Bean
    public Map<String, String> chainDefinitionFilterExtranetClient() {
        Map<String, String> chain = new HashMap<>();
        chain.put("/css/**", "anon");
        chain.put("/js/**", "anon");
        chain.put("/img/**", "anon");
        chain.put("/fonts/**", "anon");
        chain.put("/external/**", "anon");
        chain.put("/documents/**", "anon");
        chain.put("/**", "anon");
        return chain;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() throws URISyntaxException {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManagerExtranetClient());
        shiroFilter.setFilterChainDefinitionMap(chainDefinitionFilterExtranetClient());

        return shiroFilter;
    }

    @Bean
    public DefaultWebSecurityManager securityManagerExtranetClient() throws URISyntaxException {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        DefaultWebSessionManager dwsm = new DefaultWebSessionManager();

        boolean resisSetted = false;
        if (System.getenv("REDIS_CLOUD_URL") != null) {
            try {
                URI redisUri = new URI(System.getenv("REDIS_CLOUD_URL"));

                CustomRedisManager redisManager = new CustomRedisManager();
                redisManager.setHost(redisUri.getHost());
                redisManager.setPort(redisUri.getPort());
                if(redisUri.getUserInfo() != null && !redisUri.getUserInfo().isEmpty()) redisManager.setPassword(redisUri.getUserInfo().split(":", 2)[1]);

                RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
                redisSessionDAO.setRedisManager(redisManager);

                dwsm.setSessionDAO(redisSessionDAO);

                RedisCacheManager redisCacheManager = new RedisCacheManager();
                redisCacheManager.setRedisManager(redisManager);
                securityManager.setCacheManager(redisCacheManager);
                resisSetted = true;
            } catch (Exception ex) {
                logger.error("Error al crear Redis Session", ex);
            }
        }
        if (!resisSetted) {
            EhCacheManager ehcache = new EhCacheManager();
            ehcache.setCacheManager(ehCacheManager);
            securityManager.setCacheManager(ehcache);
        }

        dwsm.setGlobalSessionTimeout(60 * 60 * 1000);
        dwsm.setSessionValidationInterval(
                com.affirm.system.configuration.Configuration.getExtranetSessionValidationIntervalMinutes() * 60 * 1000);
        dwsm.getSessionIdCookie().setSecure(!com.affirm.system.configuration.Configuration.hostEnvIsLocal());
        dwsm.getSessionIdCookie().setHttpOnly(true);
        dwsm.getSessionIdCookie().setName("X-Client-Session");
        dwsm.getSessionIdCookie().setComment(";SameSite=NONE");
        dwsm.setSessionIdUrlRewritingEnabled(false);
        securityManager.setSessionManager(dwsm);
        return securityManager;
    }
}