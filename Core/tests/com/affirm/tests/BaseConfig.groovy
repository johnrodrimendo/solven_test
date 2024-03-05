package com.affirm.tests

import com.affirm.common.util.EnviromentSetupHelper
import com.affirm.system.configuration.SpringRootConfiguration
import groovy.transform.CompileStatic
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.security.SecureRandom

@CompileStatic
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SpringRootConfiguration.class)
abstract class BaseConfig {

    final String ROOT_PROJECT_DIR = EnviromentSetupHelper.getProjectRootDir()
    final String TEST_DIR = 'testResults'
    final String ABSOLUTE_TEST_DIR = ROOT_PROJECT_DIR + '/' + TEST_DIR
    final Locale LOCALE_PE = new Locale("es", "PE")

    @BeforeAll
    static void setUpAll() {
        println 'initial setup for all test set'
    }

    @BeforeEach
    void setUp() {
        println 'initial setup for individual test'
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

    static {
        EnviromentSetupHelper.loadEnvVariables()
    }

    static int randomInt(int min, int max) {
        Random r = new Random()
        return r.nextInt((max - min) + 1) + min
    }

    static String randomAlphabeticString(int n) {
        SecureRandom rnd = new SecureRandom()
        final String AB = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
        StringBuilder sb = new StringBuilder(n)
        for (int i = 0; i < n; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())))
        sb.toString()
    }

    static void createFileFromString(String fileAbsPath, String StringDocument) {
        FileWriter fw = new FileWriter(fileAbsPath)
        fw.write StringDocument
        fw.flush()
        fw.close()
    }
}
