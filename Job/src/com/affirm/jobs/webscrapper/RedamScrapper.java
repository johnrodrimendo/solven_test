package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.RedamResult;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

/**
 * Created by john on 29/09/16.
 */
public class RedamScrapper extends Scrapper {

    private final static String URL = "https://casillas.pj.gob.pe/redam/";

    public RedamScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public RedamScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public RedamResult getData(Integer docType, String docNumber) throws Exception {

        RedamResult result = new RedamResult();

        try {
            openSite(URL);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        int tries = 1;

        File imageCaptcha;
        Captcha solved = null;
        WebDriverWait waiter = new WebDriverWait(driver, 60);

        do {
            if (tries >= 5) {
                System.out.println("Se hicieron 4 intentos REDAM");
                return null;
            } else if (tries > 1) {
                System.out.println("falló el Captcha");
                reportCaptcha(solved);
                driver.navigate().back();
            }

            WebElement link = waiter.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[index='1'] > a")));
            link.click();

            WebElement form = driver.findElementById("frmDocumentoIdentidad");
            Select selectBuscarPor = new Select(form.findElement(By.tagName("select")));

            if (docType == RedamResult.DNI_TYPE) {
                selectBuscarPor.selectByVisibleText("DNI");
            } else if (docType == RedamResult.CE_TYPE) {
                selectBuscarPor.selectByVisibleText("CARNET EXTRANJERIA");
            }

            form.findElement(By.tagName("input")).click();
            form.findElement(By.tagName("input")).sendKeys(docNumber);

            WebElement captcha = driver.findElementByXPath("//div[@style='float: left; background: #8a1414; position: relative; margin-right: 10px; margin-top: 8px;']");
            imageCaptcha = shootWebElement(captcha);
            solved = solveCaptcha(imageCaptcha);

            driver.findElementById("captcha").sendKeys(solved.text.toUpperCase());

            driver.findElementByClassName("btn-red").click();

            if (driver.findElementsByXPath("//div[@class='message ng-binding']").size() > 0) {
                if (driver.findElementByXPath("//div[@class='message ng-binding']").getText().equals("Los datos ingresados no presentan registros.")) {
                    System.out.println("No presenta registros");
                    return result;
                }
            }
            tries++;
        }
        while (driver.findElements(By.id("frmDocumentoIdentidad")).size() > 0);

        // Listado de morosos

        driver.executeScript("angular.element(document.getElementsByTagName(\"button\")[2]).triggerHandler('click');");

        WebElement modal = (new WebDriverWait(driver, 1000)).until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='modal-content']")));

        //screenshot();

        WebElement datosPersonales = modal.findElement(By.xpath("//table[@class='tbdeudors table table-condensed']"));

        result.setDocumentNumber(datosPersonales.findElements(By.tagName("td")).get(7).getText());

        datosPersonales = modal.findElements(By.xpath("//table[@class='tbdeudors']")).get(0);

        result.setJudicialDistrict(datosPersonales.findElements(By.tagName("td")).get(2).getText());
        result.setCourt(datosPersonales.findElements(By.tagName("td")).get(5).getText());
        result.setSecretary(datosPersonales.findElements(By.tagName("td")).get(8).getText());
        result.setRegistry(datosPersonales.findElements(By.tagName("td")).get(11).getText());
        result.setMonthlyPension(RedamResult.DECIMAL_FORMAT.parse(datosPersonales.findElements(By.tagName("td")).get(14).getText().replace("S/. ", "")).doubleValue());
        result.setAmountDue(RedamResult.DECIMAL_FORMAT.parse(datosPersonales.findElements(By.tagName("td")).get(17).getText().replace("S/. ", "")).doubleValue());
        result.setInterest(RedamResult.DECIMAL_FORMAT.parse(datosPersonales.findElements(By.tagName("td")).get(20).getText().replace("% ", "")).doubleValue());

        System.out.println(result.toString());

        return result;

        //    System.out.println(driver.getPageSource());

        /*driver.findElement(By.id("form1:link3")).click();
        driver.findElement(By.id("form1:textNumeroDocumento1")).sendKeys(docNumber);
        driver.findElement(By.id("form1:btnBuscarDeudor")).click();

        if (driver.findElement(By.id("form1:text8")).getText().equals("Los datos ingresados no presentan registros")) {
            System.out.println("No presenta registros");
            return result;
        } else {
            System.out.println("Presenta registros");
            driver.findElement(By.id("form1:tableEx1:0:imageEx1")).click();

            result.setDocumentNumber(driver.findElement(By.id("form1:text158")).getText());
            result.setJudicialDistrict(driver.findElement(By.id("form1:tableEx3:0:textDescDistritoJudicial2")).getText());
            result.setCourt(driver.findElement(By.id("form1:tableEx3:0:textDjuzgado2")).getText());
            result.setSecretary(driver.findElement(By.id("form1:tableEx3:0:textXnombrejuez2")).getText());
            result.setRegistry(driver.findElement(By.id("form1:tableEx3:0:textNexpediente1")).getText());
            result.setMonthlyPension(RedamResult.DECIMAL_FORMAT.parse(driver.findElement(By.id("form1:tableEx3:0:textNpensionmensual2")).getText()).doubleValue());
            result.setInstallments(Integer.parseInt(driver.findElement(By.id("form1:tableEx3:0:textNcantcuota2")).getText()));
            result.setAmountDue(RedamResult.DECIMAL_FORMAT.parse(driver.findElement(By.id("form1:tableEx3:0:textNimpadeudado1")).getText()).doubleValue());
            result.setInterest(RedamResult.DECIMAL_FORMAT.parse(driver.findElement(By.id("form1:tableEx3:0:textNimpinteres2")).getText()).doubleValue());

            System.out.println(result.toString());

            return result;
        }*/

//        return null;
    }

    public static void main(String[] args) throws Exception {
        RedamScrapper scrapper = new RedamScrapper("fturconi", "ostk2004", 15);

        scrapper.getData(1, "41611649");
        //scrapper.getData(1, "45432586");
        scrapper.close();
    }

}

