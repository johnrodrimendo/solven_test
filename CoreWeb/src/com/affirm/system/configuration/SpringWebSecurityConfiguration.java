package com.affirm.system.configuration;


import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.service.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jarmando on 15/11/16.
 *
 * We only use Spring Security for its security headers configuration
 */

@Configuration
@EnableWebSecurity
public class SpringWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final Logger logger = LoggerFactory.getLogger(SpringWebSecurityConfiguration.class);

//    @Autowired
//    private CatalogDAO catalogDAO;

    public SpringWebSecurityConfiguration() {
        super(true);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement().disable();
        //httpSecurity.sessionManagement().enableSessionUrlRewriting(false);
        //We have a custom CSRF implementation which stores CSRF in secure cookie and works for ajax nicely.
        httpSecurity.csrf().disable();
        //Configure Headers
        HeadersConfigurer<HttpSecurity> hc = httpSecurity.headers();
        hc.contentTypeOptions();
        hc.xssProtection();
        hc.cacheControl();
        hc.frameOptions().disable();

        boolean reduceSecurityNotProd = com.affirm.system.configuration.Configuration.REDUCE_SECURITY_NOT_PROD;
        boolean isPrd = com.affirm.system.configuration.Configuration.hostEnvIsProduction();
        boolean isStg = com.affirm.system.configuration.Configuration.hostEnvIsStage();
        boolean isLocal = com.affirm.system.configuration.Configuration.hostEnvIsLocal();
        boolean sslEnabled = com.affirm.system.configuration.Configuration.HTTPS_SSL_IMPLEMENTED;
        boolean isBo = com.affirm.system.configuration.Configuration.isBackoffice();
        boolean isCli = com.affirm.system.configuration.Configuration.isClient();


//        if (sslEnabled && isPrd) {
        if (false) {
            hc.httpStrictTransportSecurity()//Esta implementacion manda el header stricto solo si el request fue https
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000);
            httpSecurity.requiresChannel().anyRequest().requiresSecure();
        }

        //init csp
        StringBuilder csp = new StringBuilder().append("default-src 'none';");
        //SCRIPT
        csp.append(" script-src 'self' 'unsafe-inline' 'unsafe-eval' ")
        .append((isPrd ? "https://api.eflglobal.com/api/v2/applicant_journey/ " : "https://uat-external.eflglobal.com/api/v2/applicant_journey/ "))
        .append("https://s3.amazonaws.com/solven-stg/ ")
        .append("https://s3.amazonaws.com/solven-prd/ ")
        .append("https://solven-stg.s3.amazonaws.com/ ")
        .append("https://solven-loc.s3.amazonaws.com/ ")
        .append("https://solven-prd.s3.amazonaws.com/ ")
        .append("https://secure.gravatar.com/ ")
        .append("https://www.google.com/ ")
        .append("https://www.gstatic.com/ ")
        .append("https://connect.facebook.net/ ")
        .append("https://platform.twitter.com/ ")
        .append("https://app.toky.co/ ")
        .append("https://toky.co/ ")
        .append("https://s.adroll.com/ ")
        .append("https://s3.amazonaws.com/solven-public/ ")
        .append("https://s3-us-west-2.amazonaws.com/solven-prd/ ")
        .append("https://www.google-analytics.com/ ")
        .append("https://ajax.googleapis.com/ ")
        .append("https://cdnjs.cloudflare.com/ ")
        .append("https://use.fontawesome.com/ ")
        .append("https://d10lpsik1i8c69.cloudfront.net/w.js ") //lucky orange
        .append("https://d10lpsik1i8c69.cloudfront.net/js/clickstream.js ") //lucky orange
        //.append("https://settings.luckyorange.net/ ") //lucky orange
        //.append("https://cs.luckyorange.net/ ") //lucky orange
        //.append("https://d10lpsik1i8c69.cloudfront.net/ ") //lucky orange
        //.append("https://ping.luckyorange.net/ ") //lucky orange
        .append("https://ci-mpsnare.iovation.com/ ") //iovation
        //.append("https://geo.luckyorange.net/ ") //iovation
        .append("https://mpsnare.iesnare.com/ ") //iovation
        .append("https://cdn.fontawesome.com/js/stats.js ")
        .append("https://ajax.cloudflare.com/ ")
        .append("http://mozilla.github.io/ ")
        .append("https://maps.googleapis.com/ ")
        .append("https://cdn.getmoreproof.com/ ")
        .append("https://widget.intercom.io/ ")
        .append("https://js.intercomcdn.com/ ")
        .append("https://www.googletagmanager.com/ ")
        .append("http://tagmanager.google.com/ ")
        .append("https://tagmanager.google.com/ ")
        .append("https://optimize.google.com/ ")
        .append("http://optimize.google.com/ ")
        .append("https://www.google-analytics.com/ ")
        .append("https://www.googleadservices.com/ ")
        .append("http://www.googleadservices.com/ ")
        .append("https://googleads.g.doubleclick.net/ ")
        .append("https://www.google.com.pe/ ")
        .append("http://*.hotjar.com:* ") //hotjar
        .append("https://*.hotjar.com:* ") //hotjar
        .append("ws://*.hotjar.com ") //hotjar
        .append("https://static.zdassets.com/ ")
        .append("https://ekr.zdassets.com/ ")
        .append("https://*.zopim.com/ ")
        .append("https://*.zopim.io/ ")
        .append("wss://*.zopim.io/ ")
        .append("https://cdn.onesignal.com/ ")
        .append("https://onesignal.com/ ")
        .append("https://*.vnforapps.com/ ")
        .append("https://*.online-metrix.net/ ")
        .append("https://calendly.com/ ")
        .append(";")
        ;


        //CSS STYLE
        csp.append(" style-src 'self' 'unsafe-inline' ")
        .append("https://docs.solven.pe/ ")
        .append("https://secure.gravatar.com/ ")
        .append("https://s3.amazonaws.com/solven-public/ ")
        .append("https://solven-public.s3.amazonaws.com/ ")
        .append("https://s3-us-west-2.amazonaws.com/solven-prd/ ")
        .append("https://cdnjs.cloudflare.com/ ")
        .append("https://fonts.googleapis.com/ ")
        .append("https://ajax.cloudflare.com/ ")
        .append("https://d10lpsik1i8c69.cloudfront.net/ ")
        .append("https://use.fontawesome.com/ ")
        .append("https://cdn.getmoreproof.com/ ")
        .append("https://tagmanager.google.com/ ")
        .append("https://static.intercomassets.com/ ")
        .append("https://onesignal.com/ ")
        .append("https://*.vnforapps.com/ ")
        .append("https://calendly.com/ ")
        .append(";")
        ;

        //IMGinline
        csp.append(" img-src 'self' data: blob: ")
        .append("https://docs.solven.pe/ ")
        .append("https://secure.gravatar.com/ ")
        .append("https://retodigital.files.wordpress.com/ ")
        .append("https://unsplash.it/ ")
        .append("https://www.facebook.com/ ")
        .append("https://web.facebook.com/ ")
        .append("https://s3.amazonaws.com/solven-public/ ")
        .append("https://s3.amazonaws.com/solven-stg/ ")
        .append("https://s3.amazonaws.com/solven-prd/ ")
        .append("https://solven-stg.s3.amazonaws.com/ ")
        .append("https://solven-loc.s3.amazonaws.com/ ")
        .append("https://solven-prd.s3.amazonaws.com/ ")
        .append("https://solven-public.s3.amazonaws.com/ ")
        .append("https://s3-us-west-2.amazonaws.com/solven-prd/ ")
        .append("https://stats.g.doubleclick.net/ ")
        .append("https://www.google-analytics.com/ ")
        .append("https://www.googletagmanager.com/ ")
        .append("https://www.google.com/ ")
        .append("https://www.google.com.pe/ ")
        .append("https://cdnjs.cloudflare.com/ ")
        .append("https://app.toky.co/resources/images/ ")
        .append("https://syndication.twitter.com/ ")
        .append("https://chart.googleapis.com/ ") //ga
        .append("https://maps.googleapis.com/ ") //maps
        .append("https://maps.google.com/ ") //maps
        .append("https://csi.gstatic.com/ ") //maps
        .append("https://ssl.gstatic.com/ ")
        .append("https://www.gstatic.com/ ")
        .append("https://maps.gstatic.com/ ") //maps
        .append("https://*.googleapis.com/ ") //maps
        .append("https://*.ggpht.com/ ") //maps
        .append("https://js.intercomcdn.com/ ") //maps
        .append("https://api-iam.intercom.io/ ") //maps
        .append("http://www.afip.gob.ar/ ") //maps
        .append("https://static.intercomassets.com/ ")
        .append("https://googleads.g.doubleclick.net/ ")//maps
        .append("http://ojo7.ltroute.com/ ")
        .append("https://dev-solven-c.herokuapp.com/ ")
        .append("https://stg-solven-c.herokuapp.com/ ")
        .append("https://solven.pe/ ")
        .append("https://robinacademia.com/ ")
        .append("https://static.zdassets.com/ ")
        .append("https://ekr.zdassets.com/ ")
        .append("https://*.zopim.com/ ")
        .append("https://*.zopim.io/ ")
        .append("wss://*.zopim.io/ ")
        .append("https://cdn.onesignal.com/ ")
        .append("https://onesignal.com/ ")
        .append("https://solven-public.s3.amazonaws.com/ ")
        .append("https://conv.affcpatrack.com/ ")
        .append("https://adrspain.go2cloud.org/ ")
        .append("https://px.ads.linkedin.com/ ")
        .append("https://snap.licdn.com/ ")
        .append("https://*.vnforapps.com/ ")
        .append("https://*.online-metrix.net/ ")
        .append("https://calendly.com/ ")
        .append(";")
        ;

        //FONT
        csp.append(" font-src 'self' data: ")
        .append("https://s3.amazonaws.com/solven-public/ ")
        .append("https://s3-us-west-2.amazonaws.com/solven-prd/ ")
        .append("https://fonts.gstatic.com/ ")
        .append("https://use.fontawesome.com/ ")
        .append("https://cdn.getmoreproof.com/ ")
        .append("https://js.intercomcdn.com/ ")
        .append("https://static.zdassets.com/ ")
        .append("https://ekr.zdassets.com/ ")
        .append("https://*.zopim.com/ ")
        .append("https://*.zopim.io/ ")
        .append("https://calendly.com/ ")
        .append("wss://*.zopim.io/;");

        //AJAX, CONNECT
        csp.append(" connect-src 'self' https: blob: ")
                .append("https://www.google.com/ ")
                .append("wss://*.intercom.io/ ")
                .append("wss://ci-mpsnare.iovation.com/ ")
                .append("wss://mpsnare.iesnare.com/ ")
                .append("wss://*.hotjar.com/ ")//hotjar
                .append("https://static.zdassets.com/ ")
                .append("https://ekr.zdassets.com/ ")
                .append("https://*.zopim.com/ ")
                .append("https://*.zopim.io/ ")
                .append("wss://*.zopim.com/ ")
                .append("wss://*.zopim.io/")
                .append("https://cdn.onesignal.com/ ")
                .append("https://onesignal.com/ ")
                .append("https://s3.amazonaws.com/solven-public/ ")
                .append("https://solven-public.s3.amazonaws.com/ ")
                .append("https://storage.googleapis.com/ ")
                .append(";")
        ;

        //AUDIO VIDEO, MEDIA
        csp.append(" media-src 'self' blob: data: ")
                .append("https://www.youtube.com/ ")
                .append("https://s3.amazonaws.com/solven-public/ ")
                .append("https://s3-us-west-2.amazonaws.com/solven-prd/ ")
                .append("https://cdn.getmoreproof.com/ ")
                .append("https://ci-mpsnare.iovation.com/ ")
                .append("https://mpsnare.iesnare.com/ ")
                .append("https://static.zdassets.com ")
                .append("https://ekr.zdassets.com/ ")
                .append("https://*.zopim.com/ ")
                .append("https://*.zopim.io/ ")
                .append("wss://*.zopim.io/;");

        //APPLET EMBED, OBJECT
        csp.append(" object-src 'self' ")
                .append("https://mpsnare.iesnare.com/ ")
                //.append("https://cs.luckyorange.net/ ")
                .append("https://s3.amazonaws.com/solven-public/ ")
                .append("https://solven-public.s3.amazonaws.com/ ")
                .append("https://storage.googleapis.com/ ")
                .append("https://ci-mpsnare.iovation.com/;");

        //FRAME/CHILD
        String frameChildRules = new StringBuilder().append((isPrd ? "https://solven.eflglobal.com/ " : "https://aj-demo-uat-external.eflglobal.com/solven/ "))
                .append("https://docs.solven.pe/ ")
                .append("http://localhost:8080/ ")
                .append("https://www.youtube.com/ ")
                .append("https://www.google.com/ ")
                .append("https://secure.gravatar.com/ ")
                .append("https://toky.co/ ")
                .append("https://app.toky.co/ ")
                .append("https://s.adroll.com/ ")
                .append("https://www.googletagmanager.com/ ")
                .append("http://staticxx.facebook.com/ ")
                .append("http://mozilla.github.io/ ")
                .append("https://d10lpsik1i8c69.cloudfront.net/ ")
                .append("https://cdn.getmoreproof.com/ ")
                .append("https://vars.hotjar.com/ ")
                .append("https://onesignal.com/ ")
                .append("https://drive.google.com/ ")
                .append("https://*.vnforapps.com/ ")
                .append("https://*.online-metrix.net/ ")
                .append("https://calendly.com/ ")
                .append("https://*.fls.doubleclick.net/ ")
                .append(";")
                .toString(); //hotjar

        //FRAME/ANCESTORS
//        List<String> domainsIframe = catalogDAO.getEntityBranding().stream()
//                .filter(e -> e.getBranded() && e.getIframeDomainsAllowed() != null)
//                .map(e -> e.getIframeDomainsAllowed())
//                .flatMap(Collection::stream)
//                .collect(Collectors.toList());
        StringBuilder frameAncestorsRules = new StringBuilder();
//        for(String domain : domainsIframe){
//            frameAncestorsRules.append(domain + " ");
//        }
        frameAncestorsRules.append("https://iframetester.com/ ");
        frameAncestorsRules.append("https://webflow.com/ ");
        frameAncestorsRules.append("https://*.webflow.io/ ");
        frameAncestorsRules.append("https://webflow.io/ ");
        frameAncestorsRules.append("https://prueba.bpeople.com/ ");
        frameAncestorsRules.append("https://www.alfinbanco.pe/ ");
        frameAncestorsRules.append("https://prestifacil.com.pe/ ");
        frameAncestorsRules.append("https://www.prestifacil.com.pe/ ");
        frameAncestorsRules.append(";");

        csp.append(" child-src 'self' blob: ").append(frameChildRules);
        csp.append(" frame-src 'self' ").append(frameChildRules);
        //ANCESTORS (WHO CAN FRAME ME)
        csp.append(" frame-ancestors 'self' ").append(frameAncestorsRules.toString());
        //FORM ACTION
        csp.append(" form-action 'self' http://*.solven.pe https://*.solven.pe https://*.solven.com.ar;");
        //UPGRADE: HTTP TO HTTPS
        if (isPrd || (isStg && !reduceSecurityNotProd))
            csp.append(" upgrade-insecure-requests;");
        //BLOCK MIXED CONTENT
        csp.append(" block-all-mixed-content;");

        //MANIFEST
        csp.append(" manifest-src 'self' ")
                .append("https://storage.googleapis.com/ ")
                .append("https://s3.amazonaws.com/solven-public/ ")
                .append("https://solven-public.s3.amazonaws.com/;");

        //This header is an xss fallback protection
        hc.contentSecurityPolicy(csp.toString());

        //this is a fake implementation for A+ DOESNT WORK ANYWAYS.
        //hc.httpPublicKeyPinning().maxAgeInSeconds(0);
        //hc.addHeaderWriter(new StaticHeadersWriter("Public-Key-Pins", "max-age=0"));

        //Aditional Headers
        //hc.addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"));
        //hc.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", " http://solven.pe"));
        if (isStg && reduceSecurityNotProd){
            if (isCli)
                hc.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin",  "http://stg-solven-c.herokuapp.com"));
            if (isBo)
                hc.addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "https://bostg.solven.la"));
        }

        hc.referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER_WHEN_DOWNGRADE);
        hc.addHeaderWriter(new StaticHeadersWriter("X-NewRelic-ID", "AySY5U$PTHWEwUESGJtODwfSGBSDn"));//JARMANDO MADE
        System.out.println("En el spring security");
    }
}