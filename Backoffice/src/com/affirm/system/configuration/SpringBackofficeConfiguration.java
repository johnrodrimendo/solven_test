package com.affirm.system.configuration;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.affirm.backoffice.model.TfaInterceptor;
import com.affirm.backoffice.service.SysUserService;
import com.affirm.common.util.AnyOfRolesAuthorizationFilter;
import com.affirm.security.model.GoogleAuthenticatorRealm;
import com.affirm.system.configuration.Configuration.Application;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
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
 * This is the configuration for the beans that only the Backoffice gonna use, including the controller.
 * If there are beans that gonna be used for the backoffice and the client, it should be in the lass SpringWebConfiguration
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(value = {
        "com.affirm.backoffice.controller",
        "com.affirm.backoffice.service.impl",
        "com.affirm.backoffice.dao.impl"})
public class SpringBackofficeConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    net.sf.ehcache.CacheManager ehCacheManager;
    @Resource(name = "trxDataSource")
    DataSource dataSource;
    @Autowired
    SysUserService sysUserService;

    @PostConstruct
    public void init() {
        System.setProperty("application", Application.BACKOFFICE.name());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/resources/theme_default/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("/resources/theme_default/js/");
        registry.addResourceHandler("/img/**").addResourceLocations("/resources/theme_default/img/");
        registry.addResourceHandler("/fonts/**").addResourceLocations("/resources/theme_default/fonts/");
        registry.addResourceHandler("/external/**").addResourceLocations("/resources/external/");
    }

    @Bean
    public TfaInterceptor tfaInterceptor() {
        return new TfaInterceptor();
    }

    @Bean
    public TemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setPrefix("/html/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("ISO-8859-1");
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
        templateEngine.addTemplateResolver(templateResolver());
        templateEngine.addTemplateResolver(commonHtmlTemplateResolver());
        templateEngine.addDialect(new ShiroDialect());
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("ISO-8859-1");
        viewResolver.setContentType("text/html;charset=ISO-8859-1");
        viewResolver.setOrder(1);
        return viewResolver;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/profile");
        registry.addViewController("/backoffice").setViewName("redirect:/");
    }

    // SHIRO
    @Bean
    public AnyOfRolesAuthorizationFilter anyofrole() {
        return new AnyOfRolesAuthorizationFilter();
    }

    @Bean
    public Map<String, String> chainDefinitionFilter() {
        Map<String, String> chain = new HashMap<>();
        chain.put("/css/**", "anon");
        chain.put("/loginStep2", "anon");
        chain.put("/js/**", "anon");
        chain.put("/img/**", "anon");
        chain.put("/fonts/**", "anon");
        chain.put("/external/**", "anon");
        chain.put("/resetPassword/**", "anon");
        chain.put("/resetSysUserPassword/**", "anon");
        chain.put("/**", "backoffice");
        chain.put("/confirmHuman/**", "anon");
        chain.put("/checkSuccess/**", "anon");
        chain.put("/externalView/**", "authcBasic");

        return chain;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter() throws URISyntaxException {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        //shiroFilter.setLoginUrl("/login");
        //shiroFilter.setSuccessUrl("/loanApplication/offer");
        //shiroFilter.setUnauthorizedUrl("/login");

        shiroFilter.setFilterChainDefinitionMap(chainDefinitionFilter());

        PassThruAuthenticationFilter backofficeFilter = new PassThruAuthenticationFilter();
        backofficeFilter.setLoginUrl("/login");
        backofficeFilter.setSuccessUrl("/profile");

        Map<String, javax.servlet.Filter> filters = new HashMap<>();
        /*
        filters.put("authc", new PassThruAuthenticationFilter());
        filters.put("authc", new FormAuthenticationFilter() {
            @Override
            protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
                String requestSenderTypeHeader = ((HttpServletRequest) request).getHeader("x-request-sender-type");
                if (requestSenderTypeHeader != null && requestSenderTypeHeader.equals("ajax")) {
                    ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED.value());
                    return false;
                }
                return super.onAccessDenied(request, response);
            }
        });*/
        filters.put("backoffice", backofficeFilter);
        shiroFilter.setFilters(filters);
        return shiroFilter;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() throws URISyntaxException {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        Collection realms = new ArrayList<Realm>();
        realms.add(shiroJdbcRealm());
//        realms.add(shiroAuthorizingRealm());
        securityManager.setRealms(realms);
        EhCacheManager ehcache = new EhCacheManager();
        ehcache.setCacheManager(ehCacheManager);
        securityManager.setCacheManager(ehcache);

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
                    if (session.getAttribute("boLoginId") != null) {
                        // Add the minutes of the session timeout to be more precise with the logoutTime
                        LocalDateTime logoutTime = session.getLastAccessTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusMinutes(com.affirm.system.configuration.Configuration.getBoTimeoutMinutes());
                        sysUserService.onLogout(Integer.parseInt(session.getAttribute("boLoginId").toString()), Date.from(logoutTime.atZone(ZoneId.systemDefault()).toInstant()));
                    }
                } catch (Exception ex) {
                    logger.error("Error on saving the logout", ex);
                }
            }
        });
        //shiro sessionl listener

        DefaultWebSessionManager dwsm = new DefaultWebSessionManager();

        if (com.affirm.system.configuration.Configuration.hostEnvIsProduction())
            dwsm.setSessionDAO(shiroSessionDao());

        dwsm.setGlobalSessionTimeout(60 * 60 * 1000);
        dwsm.setSessionListeners(listeners);
        dwsm.setSessionValidationInterval(
                com.affirm.system.configuration.Configuration.getExtranetSessionValidationIntervalMinutes() * 60 * 1000);
        dwsm.setSessionIdUrlRewritingEnabled(false);

        dwsm.getSessionIdCookie().setSecure(!com.affirm.system.configuration.Configuration.hostEnvIsLocal());
        dwsm.getSessionIdCookie().setHttpOnly(true);
        dwsm.getSessionIdCookie().setName("X-SolvenBO-Session");
        securityManager.setSessionManager(dwsm);

        return securityManager;
    }

    @Bean
    public JdbcRealm shiroJdbcRealm() throws URISyntaxException {
        JdbcRealm shiroRealm = new GoogleAuthenticatorRealm();
        shiroRealm.supports(new UsernamePasswordToken());
        shiroRealm.setDataSource(dataSource);
        shiroRealm.setPermissionsLookupEnabled(true);
        shiroRealm.setUserRolesQuery("select * from security.get_roles(?)");
        shiroRealm.setPermissionsQuery("select * from security.get_permission(?)");
        shiroRealm.setCredentialsMatcher(new CredentialsMatcher() {
            @Override
            public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
                return true;
            }
        });
        return shiroRealm;
    }
//    @Bean
//    public JdbcRealm shiroJdbcRealm() throws URISyntaxException {
//        JdbcRealm shiroRealm = new JdbcRealmImpl();
//        shiroRealm.supports(new UsernamePasswordToken());
//        shiroRealm.setDataSource(dataSource);
//        shiroRealm.setPermissionsLookupEnabled(true);
//        shiroRealm.setAuthenticationQuery("select * from security.get_sysuser_password(?)");
//        shiroRealm.setUserRolesQuery("select * from security.get_roles(?)");
//        shiroRealm.setPermissionsQuery("select * from security.get_permission(?)");
//        shiroRealm.setCredentialsMatcher(new CredentialsMatcher() {
//            @Override
//            public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
//
//                SysUser sysUser = (SysUser)authenticationInfo.getPrincipals().getPrimaryPrincipal();
//                sysUser.getTfaSharedSecret()
//
//                boolean correctToken = new GoogleAuthenticator().authorize(sysUser.getTfaSharedSecret(), authenticationToken.get);
//                return CryptoUtil.validatePassword(password, hashed);
//
//                System.out.println("credentials encrypted comparison");
//                UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
//                String password = new String(userToken.getPassword());
//                char[] credentials = (char[]) authenticationInfo.getCredentials();
//                String hashed = new String(credentials);
//            }
//        });
//        return shiroRealm;
//    }

//    @Bean
//    public AuthorizingRealm shiroAuthorizingRealm() {
//        AuthorizingRealm realm = new AuthorizingRealmImpl();
//        return realm;
//    }

//    @Bean
//    public HttpSessionListener httpSessionListener() {
//        return new HttpSessionListener() {
//            @Override
//            public void sessionCreated(HttpSessionEvent httpSessionEvent) {
//                httpSessionEvent.getSession().setAttribute(SecurityConfiguration.SESSION_MUTEX_KEY, new Object());
//            }
//
//            @Override
//            public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
//            }
//        };
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tfaInterceptor())
                .excludePathPatterns("/login", "/loginStep2", "/logout", "/resetPassword", "/resetPassword/*", "/resetSysUserPassword", "/health/*");
    }

    @Bean
    public SessionDAO shiroSessionDao() {
        return new EnterpriseCacheSessionDAO() {
            JedisPool pool;

            {
                //RedisCloud
                try {
                    if (System.getenv("REDIS_CLOUD_URL") != null) {
                        URI redisUri = new URI(System.getenv("REDIS_CLOUD_URL"));

                        JedisPoolConfig poolConfig = new JedisPoolConfig();
                        poolConfig.setMaxTotal(25);

                        if(redisUri.getUserInfo() != null && !redisUri.getUserInfo().isEmpty()){
                            pool = new JedisPool(poolConfig,
                                    redisUri.getHost(),
                                    redisUri.getPort(),
                                    Protocol.DEFAULT_TIMEOUT,
                                    redisUri.getUserInfo().split(":", 2)[1]);
                        }
                        else{
                            pool = new JedisPool(poolConfig,
                                    redisUri.getHost(),
                                    redisUri.getPort(),
                                    Protocol.DEFAULT_TIMEOUT);
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                            System.out.println("<---------------------------------- REDIS CONNECTED ----------------------------------> ");
                        }
                    }

                } catch (Exception e) {
//                    throw new RuntimeException(e);
                }

                //AMAZON
                //JedisPool pool = new JedisPool(new JedisPoolConfig(), "35.162.234.94", 6379, 5000);//AMAZON
            }

            @Override
            protected void doUpdate(Session session) {
                long time = System.nanoTime();
                sendToRedis(SerializationUtils.serialize(session.getId()), SerializationUtils.serialize((Serializable) session));
                System.out.println("SESSION DAO UPDATE -> " + ((System.nanoTime() - time) / 1000000));
            }

            @Override
            protected void doDelete(Session session) {
                long time = System.nanoTime();
                try (Jedis jedis = pool.getResource()) {
                    jedis.del(SerializationUtils.serialize(session.getId()));
                }
                System.out.println("SESSION DAO DELETE -> " + ((System.nanoTime() - time) / 1000000));
            }

            @Override
            protected Serializable doCreate(Session session) {
                long time = System.nanoTime();
                Serializable sessionId = generateSessionId(session);
                assignSessionId(session, sessionId);
                sendToRedis(SerializationUtils.serialize(session.getId()), SerializationUtils.serialize((Serializable) session));
                System.out.println("SESSION DAO CREATE -> " + ((System.nanoTime() - time) / 1000000));
                return session.getId();
            }

            @Override
            protected Session doReadSession(Serializable sessionid) {
                try {
                    long time = System.nanoTime();
                    byte[] object = readFromRedis(SerializationUtils.serialize(sessionid));
                    System.out.println("SESSION DAO READ -> " + ((System.nanoTime() - time) / 1000000));
                    if (object == null) {
                        return null;
                    }
                    return (Session) SerializationUtils.deserialize(object);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            private void sendToRedis(byte[] key, byte[] value) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.set(key, value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            private byte[] readFromRedis(byte[] key) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.get(key);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
    }
}