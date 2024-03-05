package com.affirm.jobs.webscrapper;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.Util;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jarmando on 06/03/17.
 */
public class ShortTermInitScrapper extends Scrapper {
    int x = 15;

    private final static String URL = "https://stg-solven-c.herokuapp.com/credito-a-corto-plazo";

    protected ShortTermInitScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    private boolean redirectToProcess(Integer docType, String docNumber, String phone, Date birthday) {
        try {
            openSite(URL);
            driver.findElement(By.id("rateButton")).click();
            Thread.sleep(200*x);
            driver.findElement(By.id("apply-for")).click();
            Thread.sleep(200*x);
            driver.findElement(By.id("termsAndConditions")).click();

            WebElement docTypeSelect = driver.findElement(By.id("docType"));
            docTypeSelect.click();
            Thread.sleep(200*x);

            /*
            List<WebElement> options = driver.findElements(By.tagName("option"));
            System.out.println("options" + options.size());
            if (docType == IdentityDocumentType.DNI) {
                System.out.println(options.get(0).getText());
                options.get(0).click();
            }
            else if (docType == IdentityDocumentType.CE){
                System.out.println(options.get(1).getText());
                options.get(1).click();
            }
            else {
                return false;
            }*/

            Select selectTipoDocumento = new Select(driver.findElement(By.id("docType")));
            if (docType == IdentityDocumentType.DNI) {
                selectTipoDocumento.selectByValue("1");
                Thread.sleep(200*x);
            }
            else if (docType == IdentityDocumentType.CE){
                selectTipoDocumento.selectByValue("2");
                Thread.sleep(200*x);
                String birthdayString = new SimpleDateFormat("dd/MM/yyyy").format(birthday);
                driver.findElement(By.id("birthDate")).sendKeys(birthdayString);
            }
            else {
                return false;
            }
            driver.findElement(By.id("docNumber")).sendKeys(docNumber);
            driver.findElement(By.id("cellphone-button")).click();
            Thread.sleep(30000);
            if(driver.findElements(By.className("confirm")).size() > 0) {
                driver.findElement(By.className("confirm")).click();
                Thread.sleep(4000);
            }
            Thread.sleep(200*x);
            driver.findElement(By.id("phoneNumber")).sendKeys(phone);
            driver.findElement(By.id("apply-button")).click();
            Thread.sleep(10000);
            driver.findElement(By.id("smsTokenNumber")).click();
            driver.findElement(By.id("smsTokenNumber")).sendKeys("1234");
            driver.findElement(By.id("validate-button")).click();


            WebDriverWait wait = new WebDriverWait(driver, 40);
            wait.until((WebDriver driver) ->
                    driver.getCurrentUrl().contains("/shorttermloanapplication/"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        ShortTermInitScrapper scrapper = new ShortTermInitScrapper("fturconi", "ostk2004", 15);
        Boolean rTrue = scrapper.redirectToProcess(2, "000555030", "998931099", Util.parseDate("17/06/1978", "dd/MM/yyyy"));
        System.out.println("TRUE: " + rTrue);
        Boolean rFalse = scrapper.redirectToProcess(1, "70771112", "980415200", null);
        System.out.println("FALSE: " + rFalse);
        scrapper.close();
    }
}
