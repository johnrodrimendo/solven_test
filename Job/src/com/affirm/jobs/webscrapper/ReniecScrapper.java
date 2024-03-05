package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.ReniecResult;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

/**
 * Created by john on 29/09/16.
 */
public class ReniecScrapper extends Scrapper {
    private static Logger logger = Logger.getLogger(ReniecScrapper.class);

    private final static String URL = "https://cel.reniec.gob.pe/valreg/valreg.do";

    public ReniecScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public ReniecScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public ReniecResult getData(String docType, String docNumber) throws Exception {

        File imageCaptcha;
        String[] splittedResult;
        ReniecResult result = new ReniecResult();

        try {
            openSite(URL);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        new WebDriverWait(driver, 90).until(ExpectedConditions.elementToBeClickable(By.name("nuDni")));
        ((JavascriptExecutor) driver).executeScript("document.getElementsByName('nuDni')[0].setAttribute('value', '" + docNumber + "')");
        WebElement captcha = driver.findElement(By.id("imagcodigo"));
        WebElement txtCaptcha = driver.findElement(By.name("imagen"));
        WebElement btnConsultar = driver.findElement(By.name("bot_consultar"));

        imageCaptcha = shootWebElement(captcha);
        screenshot();
        Captcha solved = (solveCaptcha(imageCaptcha));
        txtCaptcha.sendKeys((solved.text).toUpperCase());
        btnConsultar.click();

        WebElement data = driver.findElement(By.className("style2"));

        String strResult = data.getText();
        if (!strResult.startsWith("Ingrese el c")) {
            strResult = strResult.replace("caracter verificación anterior)", "");
            strResult = strResult.replace("\n", "/");
            strResult = strResult.replace(" (", "/");
            strResult = strResult.replace(" - ", "/");

            splittedResult = strResult.split("/");
            logger.debug("Size " + splittedResult.length);
            if (splittedResult.length > 2) {

                result.setFull_name(splittedResult[0]);
                result.setDocument_number(splittedResult[1]);
                result.setNineth_digit(splittedResult[2]);
                result.setPrevious_character_verification(splittedResult[3]);

                logger.debug("FINAL " + result.toString());

                return result;
            } else {
                logger.debug("Resultado nulo en RENIEC");
                // returns a non empty result because the BOT ran OK
                return result;
            }
        } else {
            logger.debug("Falló el captcha");
            //reportCaptcha(solved);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ReniecScrapper scrapper = new ReniecScrapper("fturconi", "ostk2004", 15);
        scrapper.getData("", "29573022");
        scrapper.close();
    }
}

