package com.affirm.tests

import com.affirm.common.util.EnviromentSetupHelper
import com.affirm.system.configuration.SpringClientConfiguration
import com.affirm.system.configuration.SpringRootConfiguration
import groovy.transform.CompileStatic
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.WebApplicationContext

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

@CompileStatic
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = [SpringRootConfiguration.class, SpringClientConfiguration.class])
abstract class BaseClientConfig extends HttpClientConfig{

    final String ROOT_PROJECT_DIR = EnviromentSetupHelper.getProjectRootDir()
    final String TEST_DIR = 'testResults'
    final String ABSOLUTE_TEST_DIR = ROOT_PROJECT_DIR + File.separator + TEST_DIR

    @Autowired
    private WebApplicationContext webApplicationContext


    @BeforeAll
    static void setUpAll() {
        println 'initial setup for all test set'

    }


    @BeforeEach
    void setUp() {
        println 'initial setup for individual test'
//        SOURCE https://stackoverflow.com/questions/10441622/write-junit-tests-for-spring-mvc-application-which-internally-relies-upon-contex
//        MOCK WebApplicationContext para resolver MessageSource
        MockServletContext mockServletContext = new MockServletContext("")
        ServletContextListener servletContextListener = new ContextLoaderListener(webApplicationContext)
        ServletContextEvent servletContextEvent = new ServletContextEvent(mockServletContext)
        servletContextListener.contextInitialized(servletContextEvent)
    }

    @AfterEach
    void tearDown() {
        println 'cleanup after individual test'

    }

    @AfterAll
    static void teatrDowAlln() {
        println 'cleanup after tests in class'

    }

    static void checkIfDirExistsElseCreateDir(String pathName) {
        File dir = new File(pathName)
        if (!dir.exists())
            dir.mkdir()
    }

    static void createFileFromBytesArray(String fileAbsPath, byte[] bytesArrays) {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileAbsPath))
        bos.write bytesArrays
        bos.flush()
        bos.close()
    }

    static void createFileFromString(String fileAbsPath, String StringDocument) {
        FileWriter fw = new FileWriter(fileAbsPath)
        fw.write StringDocument
        fw.flush()
        fw.close()
    }

    static {
        EnviromentSetupHelper.loadEnvVariables()
    }

}
