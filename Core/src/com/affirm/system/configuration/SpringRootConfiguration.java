package com.affirm.system.configuration;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.zaxxer.hikari.HikariDataSource;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author jrodriguez
 * <p>
 * This is the configuration for the business and persistence layer.
 * <br/>
 * It should scan the services and the Daos
 */
@Configuration
@EnableCaching
@EnableAsync
@ComponentScan({
        "com.affirm.common.dao",
        "com.affirm.common.model",
        "com.affirm.common.service",
        "com.affirm.security.dao",
        "com.affirm.security.model",
        "com.affirm.security.service",
        "com.affirm.system.configuration",
        "com.affirm.abaco.client",
        "com.affirm.solven.client",
        "com.affirm.efl.model",
        "com.affirm.efl.util",
        "com.affirm.acceso",
        "com.affirm.acceso.util",
        "com.affirm.acceso.service",
        "com.affirm.efectiva",
        "com.affirm.compartamos",
        "com.affirm.compartamos.util",
        "com.affirm.sendgrid.service",
        "com.affirm.cajasullana",
        "com.affirm.pipedrive.client",
        "com.affirm.aws.elasticSearch",
        "com.affirm.rextie.client",
        "com.affirm.qapaq",
        "com.affirm.onesignal",
        "com.affirm.bancodelsol.service",
        "com.affirm.aws.lambda",
        "com.affirm.wenance",
        "com.affirm.wavy.service",
        "com.affirm.fdlm",
        "com.affirm.warmi.service",
        "com.affirm.preapprovedbase.service",
        "com.affirm.heroku",
        "com.affirm.warmi.service",
        "com.affirm.sentinel.rest",
        "com.affirm.banbif.service",
        "com.affirm.bancoazteca.service",
        "com.affirm.bancoazteca.util",
        "com.affirm.negativebase.service",
        "com.affirm.mati.service",
        "com.affirm.marketingCampaign",
        "com.affirm.intico.service",
        "com.affirm.pagolink.service",
        "com.affirm.bitly.service",
        "com.affirm.bantotalrest",
        "com.affirm.bpeoplerest",
        "com.affirm.geocoding.service",
        "com.affirm.livenessapi.service",
        "com.affirm.livenessapi.util",
})
public class SpringRootConfiguration implements AsyncConfigurer {

    public static final String LATAM_CACHE_NAME = "latamCache";

    @Bean("trxDataSource")
    public DataSource getTrxDataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("TRX_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?ssl=true&sslmode=verify-ca&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(9);
        datasource.setMinimumIdle(1);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("SolvenTrx");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("evaluationDataSource")
    public DataSource getEvaluationDataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("EVALUATION_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?ssl=true&sslmode=verify-ca&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(9);
        datasource.setMinimumIdle(1);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("SolvenEvaluation");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("externalDataSource")
    public DataSource getExternalDataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("EXTERNAL_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(9);
        datasource.setMinimumIdle(1);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("SolvenExternal");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("interactionDataSource")
    public DataSource getInteractionDataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("INTERACTION_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(9);
        datasource.setMinimumIdle(1);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("SolvenInteraction");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("readableDataSource")
    public DataSource getReadableDataSource() throws URISyntaxException {

        if (!com.affirm.system.configuration.Configuration.hostEnvIsProduction())
            return null;

        URI dbUri = new URI(System.getenv("READABLE_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(9);
        datasource.setMinimumIdle(1);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("SolvenReadable");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("repositoryDataSource")
    public DataSource getRepositoryeDataSource() throws URISyntaxException {

        URI dbUri = new URI(System.getenv("REPOSITORY_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(5);
        datasource.setMinimumIdle(0);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("Affirm Repository");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("evaluationFollowerDataSource")
    public DataSource getEvaluationFollowerDataSource() throws URISyntaxException {

        if (!com.affirm.system.configuration.Configuration.hostEnvIsProduction())
            return null;
        if (System.getenv("EVALUATION_FOLLOWER_DATABASE_URL") == null || System.getenv("EVALUATION_FOLLOWER_DATABASE_URL").isEmpty())
            return null;

        URI dbUri = new URI(System.getenv("EVALUATION_FOLLOWER_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?ssl=true&sslmode=verify-ca&sslfactory=org.postgresql.ssl.NonValidatingFactory";

        HikariDataSource datasource = new HikariDataSource();
        datasource.setJdbcUrl(dbUrl);
        datasource.setDriverClassName("org.postgresql.Driver");
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setMaximumPoolSize(6);
        datasource.setMinimumIdle(1);
        datasource.setConnectionTimeout(10000);
        datasource.setIdleTimeout(10000);
        datasource.setMaxLifetime(10000);
        datasource.setValidationTimeout(5000);
        datasource.setLeakDetectionThreshold(30000);
        datasource.setPoolName("SolvenFollowerEvaluation");
        datasource.setRegisterMbeans(true);
        return datasource;
    }

    @Bean("trxJdbcTemplate")
    public JdbcTemplate getTrxJdbcTemplate() throws URISyntaxException {
        return new JdbcTemplate(getTrxDataSource());
    }

    @Bean("evaluationJdbcTemplate")
    public JdbcTemplate getEvaluationJdbcTemplate() throws URISyntaxException {
        return new JdbcTemplate(getEvaluationDataSource());
    }

    @Bean("externalJdbcTemplate")
    public JdbcTemplate getExternalJdbcTemplate() throws URISyntaxException {
        return new JdbcTemplate(getExternalDataSource());
    }

    @Bean("interactionJdbcTemplate")
    public JdbcTemplate getInteractionJdbcTemplate() throws URISyntaxException {
        return new JdbcTemplate(getInteractionDataSource());
    }

    @Bean("readableJdbcTemplate")
    public JdbcTemplate getReadableJdbcTemplate() throws URISyntaxException {
        if (getReadableDataSource() != null)
            return new JdbcTemplate(getReadableDataSource());
        return null;
    }

    @Bean("repositoryJdbcTemplate")
    public JdbcTemplate getRepositoryJdbcTemplate() throws URISyntaxException {
        return new JdbcTemplate(getRepositoryeDataSource());
    }

    @Bean("evaluationFollowerJdbcTemplate")
    public JdbcTemplate getEvaluationFollowerJdbcTemplate() throws URISyntaxException {
        if (getEvaluationFollowerDataSource() != null)
            return new JdbcTemplate(getEvaluationFollowerDataSource());
        return null;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/locale/bd_errors", "classpath:/locale/messages", "/locale/form_validation_messages", "/locale/base/html_core", "/locale/questions", "classpath:/locale/form_validation_messages", "classpath:/locale/base/html_core", "classpath:/locale/questions");
        messageSource.setDefaultEncoding("ISO-8859-1");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /* BEANS FOR EHCACHE */

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("catalogCache");
        cacheConfiguration.setMaxEntriesLocalHeap(10000);
        cacheConfiguration.setEternal(true);

        CacheConfiguration cacheLatamConfiguration = new CacheConfiguration();
        cacheLatamConfiguration.setName(LATAM_CACHE_NAME);
        cacheLatamConfiguration.setMaxEntriesLocalHeap(10000);
        cacheLatamConfiguration.setEternal(true);

        CacheConfiguration shiroDefaultConfiguration = new CacheConfiguration();
        shiroDefaultConfiguration.setMaxEntriesLocalHeap(10000);
        shiroDefaultConfiguration.setEternal(false);
        shiroDefaultConfiguration.setOverflowToDisk(false);
        shiroDefaultConfiguration.setTimeToLiveSeconds(120);
        shiroDefaultConfiguration.setTimeToIdleSeconds(120);
        shiroDefaultConfiguration.setDiskPersistent(false);
        shiroDefaultConfiguration.setDiskExpiryThreadIntervalSeconds(120);

        CacheConfiguration shiroActtiveSessinoCache = new CacheConfiguration();
        shiroActtiveSessinoCache.setName("shiro-activeSessionCache");
        shiroActtiveSessinoCache.setMaxEntriesLocalHeap(10000);
        shiroActtiveSessinoCache.setOverflowToDisk(true);
        shiroActtiveSessinoCache.setEternal(true);
        shiroActtiveSessinoCache.setTimeToLiveSeconds(0);
        shiroActtiveSessinoCache.setTimeToIdleSeconds(0);
        shiroActtiveSessinoCache.setDiskPersistent(true);
        shiroActtiveSessinoCache.setDiskExpiryThreadIntervalSeconds(600);

        CacheConfiguration shiroPropertiesRealm = new CacheConfiguration();
        shiroPropertiesRealm.setName("org.apache.shiro.realm.text.PropertiesRealm-0-accounts");
        shiroPropertiesRealm.setMaxEntriesLocalHeap(1000);
        shiroPropertiesRealm.setOverflowToDisk(true);
        shiroPropertiesRealm.setEternal(true);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.setDefaultCacheConfiguration(shiroDefaultConfiguration);
        config.addCache(cacheConfiguration);
        config.addCache(cacheLatamConfiguration);
        config.addCache(shiroActtiveSessinoCache);
        config.addCache(shiroPropertiesRealm);

        return net.sf.ehcache.CacheManager.create(config);
    }

    @Bean
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    public KeyGenerator cacheKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                final List<Object> key = new ArrayList<>();
                key.add(method.getDeclaringClass().getName());
                key.add(method.getName());

                for (final Object o : params) {
                    key.add(o);
                }
                return key;
            }
        };
    }

    @Bean
    public AmazonSQSClient amazonSQSClient() {
        AmazonSQSClient sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        return sqs;
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Core ThreadPool");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}