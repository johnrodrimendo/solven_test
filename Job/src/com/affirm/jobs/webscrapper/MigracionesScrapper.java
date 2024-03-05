package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.MigracionesResult;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by stbn on 14/12/16.
 */
public class MigracionesScrapper extends Scrapper {

    private final static String URL = "https://sel.migraciones.gob.pe/servmig-valreg/VerificarCE";

    public MigracionesScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public MigracionesScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public MigracionesResult getData(String docNumber, Date birthDate) throws Exception {

        String[] splittedResult;
        MigracionesResult result = new MigracionesResult();

        try {
            openSite(URL);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        int tries = 1;

        File imageCaptcha;
        Captcha solved = null;

        do {

            if (tries >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (tries > 1) {
                System.out.println("tries " + tries);
                System.out.println("falló el Captcha");
                //System.out.println("Takin SCREENSHOT");
                //screenshot();
                reportCaptcha(solved);
            }

            driver.findElementById("ctl00_bodypage_txtnumerodoc").sendKeys(docNumber);

            Calendar birthDateCal = Calendar.getInstance();
            birthDateCal.setTime(birthDate);
            Select dayOfBirth = new Select(driver.findElement(By.id("ctl00_bodypage_cbodia")));
            dayOfBirth.selectByValue(String.valueOf(birthDateCal.get(Calendar.DAY_OF_MONTH)));
            System.out.println(String.valueOf(birthDateCal.get(Calendar.DAY_OF_MONTH)));
            Select monthOfBirth = new Select(driver.findElement(By.id("ctl00_bodypage_cbomes")));
            monthOfBirth.selectByValue(String.valueOf(birthDateCal.get(Calendar.MONTH) + 1));
            System.out.println(String.valueOf(birthDateCal.get(Calendar.MONTH)));

            Select yearOfBirth = new Select(driver.findElement(By.id("ctl00_bodypage_cboanio")));
            yearOfBirth.selectByValue(String.valueOf(birthDateCal.get(Calendar.YEAR)));
            System.out.println(String.valueOf(birthDateCal.get(Calendar.YEAR)));

            WebElement captcha = driver.findElementByClassName("capcha").findElement(By.tagName("img"));
            imageCaptcha = shootWebElement(captcha);
            solved = solveCaptcha(imageCaptcha);

            driver.findElementById("ctl00_bodypage_txtvalidator").sendKeys(solved.text);

            driver.findElementById("ctl00_bodypage_btnverificar").click();

            if (driver.findElementsById("ctl00_bodypage_lblmensaje").size() > 0) {
                System.out.println("ctl00_bodypage_lblmensaje EXISTE");
                System.out.println(driver.findElementById("ctl00_bodypage_lblmensaje").getText());
                if (driver.findElementById("ctl00_bodypage_lblmensaje").getText().contains("No se encontr")) {
                    return result;
                }

                if (driver.findElementById("ctl00_bodypage_lblmensaje").getText().contains("anulado")) {
                    result.setFullName("ANULADO");
                    return result;
                }
            } else {

                result.setFullName(driver.findElementById("ctl00_bodypage_lblnombre").getText());
                result.setNationality(driver.findElementById("ctl00_bodypage_lblnacionalidad").getText());
                result.setResidence(driver.findElementById("ctl00_bodypage_lblmensaje_residencia").getText());
                result.setTae(driver.findElementById("ctl00_bodypage_lblmensaje_tae").getText());

                System.out.println(result.toString());

                return result;
            }

            tries++;

        } while (result.getFullName() == null);

        return null;

    }

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        String dateInString = "17-06-1978";
        Date date = sdf.parse(dateInString);

        System.out.println(date); //Tue Aug 31 10:20:56 SGT 1982

        long init = System.currentTimeMillis();
        MigracionesScrapper scrapper = new MigracionesScrapper("fturconi", "ostk2004", 40);
        long end = System.currentTimeMillis();
        System.out.println(end-init + " X");
        scrapper.getData("000555030", date);
        end = System.currentTimeMillis();
        System.out.println(end-init + " X");
        scrapper.close();
        end = System.currentTimeMillis();
        System.out.println(end-init + " X");
    }

}