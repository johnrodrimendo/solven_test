package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.transactional.SisResult;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by stbn on 21/12/16.
 */
public class SuneduScrapper extends Scrapper {

    private final static String URL = "https://rngt.sunedu.gob.pe/";

    public SuneduScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public SuneduScrapper getData(String docNumber) throws Exception {
        System.out.println("DOCNUMBER " + docNumber);
        SisResult result = new SisResult();
        List<WebElement> alert;

        boolean failed = false;

        try {
            openSite(URL);
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

        do {
            if (tries >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (tries > 1) {
                System.out.println("falló el Captcha");
                reportCaptcha(solved);
            }

            driver.findElement(By.id("doc")).clear();
            driver.findElement(By.id("doc")).sendKeys(inDocNumber);

            WebElement captcha = driver.findElement(By.id("captchaImg"));
            imageCaptcha = shootWebElement(captcha);

            solved = solveCaptcha(imageCaptcha);

            driver.findElement(By.id("captcha")).sendKeys(solved.text);

            driver.findElement(By.id("buscar")).click();

            tries++;

            alert = driver.findElements(By.id("frmError"));
            if (alert.size() > 0) {
                System.out.println("HAY ALERTA");
                System.out.println("Content " + alert.get(0).getText());
                System.out.println(driver.getPageSource());
                failed = alert.get(0).getText().endsWith("no es correcto");
                driver.getKeyboard().sendKeys(Keys.ESCAPE);
            }

        } while (failed);

        if (alert.size() > 0) {
            System.out.println("NO HAY ALERTA");
            //close the alert
            driver.getKeyboard().sendKeys(Keys.ESCAPE);
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        long ini = new Date().getTime();

        SuneduScrapper scrapper = new SuneduScrapper("fturconi", "ostk2004", 40);
        scrapper.getData("45432586");
        scrapper.close();
        System.out.println("Se demoró el Scrapper: " + (new Date().getTime() - ini + " milis"));

    }

}
