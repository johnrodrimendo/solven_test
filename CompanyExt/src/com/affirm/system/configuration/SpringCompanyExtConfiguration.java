package com.affirm.system.configuration;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.ExtranetService;
import com.affirm.common.util.AnyOfRolesAuthorizationFilter;
import com.affirm.common.util.CustomRedisManager;
import com.affirm.security.model.ExtranetCompanyRealmImpl;
import com.affirm.security.model.MultiSessionAuthenticationFilter;
import com.affirm.security.model.PhantomRealmImpl;
import com.affirm.system.configuration.Configuration.Application;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
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
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.Serializable;
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
        "com.affirm.companyExt.controller",
        "com.affirm.companyExt.service.impl",
        "com.affirm.companyExt.dao.impl"})
public class SpringCompanyExtConfiguration extends WebMvcConfigurerAdapter {

    private static Logger logger = Logger.getLogger(SpringCompanyExtConfiguration.class);

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
        System.setProperty("application", Application.COMPANY_EXT.name());
    }


    ///////////////////////////////
    // SHIRO
    ///////////////////////////////

    @Bean
    public AnyOfRolesAuthorizationFilter anyofrole() {
        return new AnyOfRolesAuthorizationFilter();
    }

    @Bean
    public ExtranetCompanyRealmImpl extranetCompanyRealm() {
        ExtranetCompanyRealmImpl realm = new ExtranetCompanyRealmImpl();
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
        chain.put("/health/**", "anon");
        chain.put("/**", "company");
        return chain;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() throws URISyntaxException {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManagerExtranetClient());
        shiroFilter.setFilterChainDefinitionMap(chainDefinitionFilterExtranetClient());

        MultiSessionAuthenticationFilter companyfilter = new MultiSessionAuthenticationFilter();
        companyfilter.setPermitedUserClass(LoggedUserEmployer.class);
        companyfilter.setLoginUrl("/login");
        companyfilter.setSuccessUrl("/");

        Map<String, javax.servlet.Filter> filters = new HashMap<>();
        filters.put("company", companyfilter);
        shiroFilter.setFilters(filters);

        return shiroFilter;
    }

    @Bean
    public DefaultWebSecurityManager securityManagerExtranetClient() throws URISyntaxException {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        Collection realms = new ArrayList<Realm>();
        realms.add(extranetCompanyRealm());


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
                    } else if (session.getAttribute("extranetCompanyLoginId") != null) {
                        // Add the minutes of the session timeout to be more precise with the logoutTime
                        LocalDateTime logoutTime = session.getLastAccessTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(com.affirm.system.configuration.Configuration.getExtranetTimeoutMinutes());
                        employerDao.registerSessionLogout((int) session.getAttribute("extranetCompanyLoginId"), Date.from(logoutTime.atZone(ZoneId.systemDefault()).toInstant()));
                    } else if (session.getAttribute("entityExtranetLoginId") != null) {
                        // Add the minutes of the session timeout to be more precise with the logoutTime
                        LocalDateTime logoutTime = session.getLastAccessTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(com.affirm.system.configuration.Configuration.getExtranetTimeoutMinutes());
                        entityExtranetService.onLogout((int) session.getAttribute("entityExtranetLoginId"), Date.from(logoutTime.atZone(ZoneId.systemDefault()).toInstant()));
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

                EhCacheManager ehcache = new EhCacheManager();
                ehcache.setCacheManager(ehCacheManager);
                securityManager.setCacheManager(ehcache);
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
        dwsm.getSessionIdCookie().setName("X-Company-Session");
        dwsm.setSessionIdUrlRewritingEnabled(false);
        securityManager.setSessionManager(dwsm);
        return securityManager;
    }
}