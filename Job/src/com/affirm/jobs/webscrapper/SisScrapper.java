package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.SisResult;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jarmando on 29/09/16.
 */
public class SisScrapper extends Scrapper {

    private final static String URL = "https://app.sis.gob.pe/SisConsultaEnLinea/Consulta/frmConsultaEnLinea.aspx";
    private final static String WRONG_CAPTCHA_SIS = "El id es incorrecto, intente nuevamente";
    //No existen registros para los datos ingresados. Pruebe una Nueva Consulta

    public SisScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public SisScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public SisResult getData(Integer docType, String docNumber) throws Exception {
        System.out.println("DOCTYPE " + docType);
        System.out.println("DOCNUMBER " + docNumber);
        SisResult result = new SisResult();
        try {
            openSite(URL);
            String agent = ((JavascriptExecutor) driver).executeScript("return navigator.userAgent").toString();
            System.out.println("agent: " + agent);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }
        int tries = 1;

        File imageCaptcha;
        Captcha solved = null;

        String inSearchType = "";
        String inDocType = "";
        String inDocNumber = docNumber.trim();

        System.out.println("PAGE SOURCE SIS " + driver.getPageSource());


        WebElement form = driver.findElement(By.name("Form1"));

        Select selectBuscarPor = new Select(driver.findElement(By.id("cboTipoBusqueda")));
        inSearchType = "2";
        selectBuscarPor.selectByValue(inSearchType);


        Select selectTipoDocumento = new Select(form.findElement(By.id("cboTipoDocumento")));
        if (IdentityDocumentType.DNI == docType) {
            inDocType = "1";
        } else if (IdentityDocumentType.CE == docType) {
            inDocType = "2";
        } else {
            throw new IllegalArgumentException("This document type is not supported for this scrapper");
        }
        selectTipoDocumento.selectByValue(inDocType);

        form.findElement(By.id("txtNroDocumento")).clear();
        form.findElement(By.id("txtNroDocumento")).sendKeys(inDocNumber);


        do {
            if (tries >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (tries > 1) {
                System.out.println("captcha fail");
                reportCaptcha(solved);
            }


            WebElement table = form.findElement(By.id("tblFiltro3"));
            WebElement captcha = table.findElement(By.tagName("img"));
            imageCaptcha = shootWebElement(captcha);

            solved = solveCaptcha(imageCaptcha);

            driver.findElement(By.name("CaptchaControl1")).sendKeys(StringUtils.upperCase(solved.text));

            driver.findElement(By.id("btnConsultar")).click();

            tries++;

        } while (driver.findElements(By.name("CaptchaControl1")).size() > 0);

        System.out.println("Captcha control in not around anymore, successful.");


        try {
            driver.findElement(By.id("dgConsulta_ctl02_imgEditar")).click();
        } catch (Exception e) {
            System.out.println("No se encuentra en la base de Sis");
            return result;
        }

        List<WebElement> webTexts = driver.findElement(By.id("Table1")).findElements(By.className("c_texto_02"));

        int expectedRows = 13;
        if (webTexts.size() != expectedRows) {
            System.out.println("WARNING: The number of rows of the result changed from "
                    + expectedRows + " to " + webTexts.size());
        }
        System.out.println("init");
        webTexts.forEach(x -> System.out.println(x.getText()));
        System.out.println("end");

        result.setInDocType(Integer.parseInt(inDocType));
        result.setInDocNumber(inDocNumber);

        result.setFullName(webTexts.get(0).getText());
        result.setDocumentNumber(webTexts.get(1).getText());
        result.setAffiliationNumber(webTexts.get(2).getText());
        result.setInsuranceType(webTexts.get(3).getText() + "\n" + webTexts.get(4).getText());

        result.setInsuredType(webTexts.get(5).getText());
        result.setFormatType(webTexts.get(6).getText());
        System.out.println("texto: " + webTexts.get(7).getText());
        Date date = new SimpleDateFormat("dd / MM / yyyy").parse(webTexts.get(7).getText());
        System.out.println("date: " + date);
        result.setEnrollmentDate(date);
        result.setBenefitPlan(webTexts.get(8).getText());

        result.setHealthCenter(webTexts.get(9).getText());
        result.setHealthCenterAddress(webTexts.get(10).getText());
        result.setStatus(webTexts.get(11).getText());
        result.setValidUntil(webTexts.get(12).getText());

        System.out.println(result);

        return result;
    }

    public static void main(String[] args) throws Exception {
        SisScrapper scrapper = new SisScrapper("fturconi", "ostk2004", 15);
        scrapper.getData(1, "45218393");
        scrapper.close();
    }

}

