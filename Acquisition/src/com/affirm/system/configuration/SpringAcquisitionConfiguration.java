package com.affirm.system.configuration;

import com.affirm.client.model.interceptor.ProductCategoryInterceptor;
import com.affirm.client.model.interceptor.RedirectEntityBrandingInterceptor;
import com.affirm.client.service.ExtranetService;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.User;
import com.affirm.common.util.AnyOfRolesAuthorizationFilter;
import com.affirm.common.util.CustomRedisManager;
import com.affirm.security.model.ClientExtranetRealmImpl;
import com.affirm.security.model.MultiSessionAuthenticationFilter;
import com.affirm.security.model.PhantomRealmImpl;
import com.affirm.system.configuration.Configuration.Application;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        "com.affirm.latam.controller",
        "com.affirm.latam.dao.impl",
        "com.affirm.acquisition.controller.rest",
        "com.affirm.acquisition.controller.questions",
        "com.affirm.acquisition.controller",
        "com.affirm.acquisition.service.impl",
        "com.affirm.acquisition.dao.impl",
        "com.affirm.acquisition.model.form",
//        "com.affirm.acquisition.model.messengerbot.modular",
//        "com.affirm.acquisition.model.messengerbot.utils",
        "com.affirm.dialogflow",
        "com.affirm.acquisition.model.aspect",
        "com.affirm.abaco.service.impl",
        "com.affirm.api.endpoint.soap.impl",
        "com.affirm.api.endpoint.soap.service",
        "com.affirm.apirest"
})
public class SpringAcquisitionConfiguration extends WebMvcConfigurerAdapter {

    private static Logger logger = Logger.getLogger(SpringAcquisitionConfiguration.class);

    @Autowired
    net.sf.ehcache.CacheManager ehCacheManager;
    @Autowired
    ExtranetService extranetService;

    @PostConstruct
    public void init() {
        System.setProperty("application", Application.ACQUISITION.name());
    }

    @Bean
    public RedirectEntityBrandingInterceptor redirectEntityBrandingInterceptor() {
        return new RedirectEntityBrandingInterceptor();
    }

    @Bean
    public ProductCategoryInterceptor productCategoryInterceptor() {
        return new ProductCategoryInterceptor();
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
    }


    ///////////////////////////////
    // SHIRO
    ///////////////////////////////

    @Bean
    public AnyOfRolesAuthorizationFilter anyofrole() {
        return new AnyOfRolesAuthorizationFilter();
    }

    @Bean
    public ClientExtranetRealmImpl clientExtranetRealm() {
        ClientExtranetRealmImpl realm = new ClientExtranetRealmImpl();
        realm.setCredentialsMatcher((authenticationToken, authenticationInfo) -> true);
        return realm;
    }

    @Bean
    public PhantomRealmImpl shiroPhantomRealmImpl() {
        PhantomRealmImpl realm = new PhantomRealmImpl();
        realm.setCredentialsMatcher((authenticationToken, authenticationInfo) -> true);
        return realm;
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
        chain.put("/client/**", "client");
        chain.put("/**", "anon");
        return chain;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() throws URISyntaxException {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManagerExtranetClient());
        shiroFilter.setFilterChainDefinitionMap(chainDefinitionFilterExtranetClient());

        MultiSessionAuthenticationFilter clientfilter = new MultiSessionAuthenticationFilter();
        clientfilter.setPermitedUserClass(User.class);
        clientfilter.setLoginUrl("/client/login");
        clientfilter.setSuccessUrl("/client");


        Map<String, javax.servlet.Filter> filters = new HashMap<>();
        filters.put("client", clientfilter);
        shiroFilter.setFilters(filters);

        return shiroFilter;
    }

    @Bean
    public DefaultWebSecurityManager securityManagerExtranetClient() throws URISyntaxException {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        Collection realms = new ArrayList<Realm>();
        realms.add(clientExtranetRealm());
        realms.add(shiroPhantomRealmImpl());

        securityManager.setAuthenticator(new ModularRealmAuthenticator() {
            @Override
            protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
                AuthenticationStrategy strategy = getAuthenticationStrategy();
                AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);

                for (Realm realm : realms) {
                    aggregate = strategy.beforeAttempt(realm, token, aggregate);
                    if (realm.supports(token)) {
                        logger.trace("Attempting to authenticate token [{}] using realm [{}]");
                        AuthenticationInfo info = null;
                        Throwable t = null;
                        info = realm.getAuthenticationInfo(token);
                        aggregate = strategy.afterAttempt(realm, token, info, aggregate, t);
                    } else {
                        logger.debug("Realm [{}] does not support token {}.  Skipping realm.");
                    }
                }
                aggregate = strategy.afterAllAttempts(token, aggregate);
                return aggregate;
            }
        });

        securityManager.setRealms(realms);


        Collection listeners = new ArrayList<SessionListener>();
        listeners.add(new SessionListener() {

            private Logger logger = Logger.getLogger(SessionListener.class);

            @Override
            public void onStart(Session session) {
            }

            @Override
            public void onStop(Session session) {
            }

            @Override
            public void onExpiration(Session session) {
                try {
                    if (session.getAttribute("extranetLoginId") != null) {
                        // Add the minutes of the session timeout to be more precise with the logoutTime
                        LocalDateTime logoutTime = session.getLastAccessTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(com.affirm.system.configuration.Configuration.getExtranetTimeoutMinutes());
                        extranetService.onLogout((int) session.getAttribute("extranetLoginId"), Date.from(logoutTime.atZone(ZoneId.systemDefault()).toInstant()));
                    }
                } catch (Exception ex) {
                    logger.error("Error on saving the logout", ex);
                }
            }
        });

        DefaultWebSessionManager dwsm = new DefaultWebSessionManager();

        boolean resisSetted = false;
        if (System.getenv("REDIS_CLOUD_URL") != null){
            try{
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
            }catch (Exception ex){
                logger.error("Error al crear Redis Session", ex);
            }
        }
        if(!resisSetted){
            EhCacheManager ehcache = new EhCacheManager();
            ehcache.setCacheManager(ehCacheManager);
            securityManager.setCacheManager(ehcache);
        }

        dwsm.setGlobalSessionTimeout(60 * 60 * 1000);
        dwsm.setSessionListeners(listeners);
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