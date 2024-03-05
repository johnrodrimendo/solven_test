package com.affirm.system.configuration; /**
 * Created by jrodriguez on 23/05/16.
 */

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

@javax.servlet.annotation.WebListener()
public class WebListener implements ServletContextListener {

    static final Logger logger = Logger.getLogger(WebListener.class);

    // Public constructor is required by servlet spec
    public WebListener() {
    }

    // -------------------------------------------------------
    // ServletContextListener implementation
    // -------------------------------------------------------
    public void contextInitialized(ServletContextEvent sce) {
      /* This method is called when the servlet context is
         initialized(when the Web application is deployed). 
         You can initialize servlet context related data here.
      */
        try {
            String log4jPath = String.format("/WEB-INF/log4j%s.properties",
                    Configuration.hostEnvIsProduction() ? ".prd" :
                            Configuration.hostEnvIsDev() ? ".dev" :
                                    Configuration.hostEnvIsStage() ? ".stg" :
                                            "");
            InputStream input = sce.getServletContext().getResourceAsStream(log4jPath);
            Properties properties = new Properties();
            properties.load(input);
            PropertyConfigurator.configure(properties);
        } catch (Exception e) {
            System.out.println("The Log4j configuratoin failed!!");
            e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
      /* This method is invoked when the Servlet Context 
         (the Web application) is undeployed or 
         Application Server shuts down.
      */
    }
}
