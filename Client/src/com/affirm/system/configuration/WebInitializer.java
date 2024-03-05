package com.affirm.system.configuration;

import com.affirm.common.util.DispatcherServletHandler;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import javax.servlet.*;
import java.util.EnumSet;

public class WebInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = getContext();
        servletContext.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServletHandler(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
        dispatcher.setInitParameter("throwExceptionIfNoHandlerFound", "true");
//
//        dispatcher = servletContext.addServlet("JAXWSServlet", new WSServlet());
//        dispatcher.setLoadOnStartup(2);
//        dispatcher.addMapping("/WSCredito");

        // Shiro Filter (Spring will lookup for a bean shiroFilter)
        FilterRegistration.Dynamic shiroFilter = servletContext.addFilter("shiroFilter", new DelegatingFilterProxy());
        shiroFilter.setInitParameter("targetFilterLifecycle", "true");
        shiroFilter.addMappingForUrlPatterns(null, false, "/*");

        //Only Cookie session
        servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));

        servletContext.addFilter("resourceUrlEncodingFilter", ResourceUrlEncodingFilter.class).addMappingForUrlPatterns(
                null, true, "/*");
    }

    private WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(SpringRootConfiguration.class);
        context.register(SpringWebConfiguration.class);
        context.register(SpringWebSecurityConfiguration.class);
        context.register(SpringClientConfiguration.class);

        return context;
    }

}
