package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.SoatRecordsResult;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SoatRecordsScrapper extends Scrapper {
    private final static String URL = "https://www.apeseg.org.pe/consultas-soat/";
    private final static int MAX_ATTEMPTS = 5;
    private final static String LANDING_TITLE = "Consultas SOAT – APESEG";
    private final static String NEW_WINDOW_TITLE = "APESEG :: Asociación Peruana de Empresas de Seguros";

    public SoatRecordsScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public SoatRecordsScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public SoatRecordsResult getRecordsByReg(Integer queryId, String plate) throws Exception {

        int attempts = 0;
        boolean success = false;
        Captcha solved = null;
        SoatRecordsResult report = null;
        Object[] allWindows = null;


        plate = plate.replaceAll("-", "");
        if (!plate.matches("[a-zA-Z0-9]*") || plate.length() != 6) {

            report = new SoatRecordsResult();
            report.setQueryId(queryId);
            report.setRegPlate("INVALID PROVIDED PLATE");
            return report;
        }
        System.out.println("Running SOAT bot");


        try {
            openSite(URL);
        } catch (TimeoutException te) {
            te.printStackTrace();
            return null;
        }
        do {
            if (attempts >= MAX_ATTEMPTS) {
                System.out.println("Se hicieron " + MAX_ATTEMPTS + " intentos");
                return null;
            } else if (attempts > 1) {
                reportCaptcha(solved);
            }
            WebDriverWait waitForUrl = new WebDriverWait(driver, 45);
            waitForUrl.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/div[1]/div/main/section[2]/div/div/div[1]/div/div[2]/div/form/div[2]/div[1]/p/img")));
            //screenshot();
            driver.findElement(By.name("textfield")).clear();
            driver.findElement(By.name("textfield")).sendKeys(plate);
            File imageCaptcha = shootWebElement(driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/main/section[2]/div/div/div[1]/div/div[2]/div/form/div[2]/div[1]/p/img")));

            try {
                solved = solveCaptcha(imageCaptcha);
                driver.findElement(By.name("captcha")).sendKeys("wrong");
                //screenshot();
                driver.findElement(By.id("SOATForm")).click();

                allWindows = driver.getWindowHandles().toArray();

                if ("".equalsIgnoreCase(driver.switchTo().window(allWindows[1].toString()).getTitle())) {
                    System.out.println("CAPTCHA FAILED");
                    throw new Exception();
                } else if (localChromeDriver & driver.switchTo().window(allWindows[1].toString()).getTitle().equals(NEW_WINDOW_TITLE) && !isAlertPresent(driver)) {
                    System.out.println("CAPTCHA SUCCEEDED : retrying (No records or wrong captcha)");
                    success = true;
                } else if (!localChromeDriver & driver.getTitle().equals(NEW_WINDOW_TITLE)) {
                    success = true;
                }


            } catch (Throwable thr) {
                if (localChromeDriver) {
                    allWindows = driver.getWindowHandles().toArray();
                    if (allWindows.length == 2) {
                        driver.switchTo().window(allWindows[1].toString()).close();
                        driver.switchTo().window(allWindows[0].toString()).navigate().to(URL);
                    }
                    System.out.println("CAPTCHA FAILED / NO RECORDS : Main Exception");


                }

            } finally {
                attempts++;
                System.out.printf("ATTEMPTS : %d/%d\n", attempts, MAX_ATTEMPTS);
            }

        } while (!success);
        //screenshot();
        if (localChromeDriver) {
            allWindows = driver.getWindowHandles().toArray();
            if (allWindows.length == 2)
                driver.switchTo().window(allWindows[1].toString());
        }


        System.out.println("Fetching table records");
        boolean recordsAreFound = driver.findElements(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr")).size() > 0;
        report = new SoatRecordsResult();

        if(recordsAreFound){
            Integer loopIndex = driver.findElements(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr")).size();

            if (loopIndex != null && loopIndex > 0) {
                report.setQueryId(queryId);
                report.setCompany(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[2]")).getText().trim());
                report.setStartDate(SoatRecordsResult.toDate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[3]")).getText().trim()));
                report.setEndingDate(SoatRecordsResult.toDate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[4]")).getText().trim()));
                report.setRegPlate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[5]")).getText().trim());
                report.setCertificate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[6]")).getText().trim());
                report.setUsage(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[7]")).getText().trim());
                report.setVehicleCategory(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[8]")).getText().trim());
                report.setState(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[9]")).getText().trim());
                report.setTypeOfDocument(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[10]")).getText().trim());
                String createdDate = driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[1]/td[11]")).getText();

                if(createdDate != null && createdDate.length() > 0)
                    report.setCreationDate(SoatRecordsResult.toTimeStamp(createdDate.trim()));

                List<SoatRecordsResult.Reports> list = new ArrayList<>();

                for (int i = 1; i < loopIndex + 1; i++) {
                    SoatRecordsResult.Reports result = new SoatRecordsResult.Reports();
                    result.setCompany(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[2]")).getText().trim());
                    result.setStartDate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[3]")).getText().trim());
                    result.setEndingDate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[4]")).getText().trim());
                    result.setRegPlate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[5]")).getText().trim());
                    result.setCertificate(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[6]")).getText().trim());
                    result.setUsage(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[7]")).getText().trim());
                    result.setVehicleCategory(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[8]")).getText().trim());
                    result.setState(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[9]")).getText().trim());
                    result.setTypeOfDocument(driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[10]")).getText().trim());
                    createdDate = driver.findElement(By.xpath("/html/body/form/center/table/tbody/tr/td/div/table/tbody/tr[" + i + "]/td[11]")).getText();

                    if(createdDate != null && createdDate.length() > 0)
                        report.setCreationDate(SoatRecordsResult.toTimeStamp(createdDate.trim()));

                    list.add(result);
                }
            report.setReports(list);
            }
        }
        System.out.println(report);

        return report;
    }

    private boolean isAlertPresent(RemoteWebDriver dr) {
        try {
            dr.switchTo().alert();
            return true;
        } catch (NoAlertPresentException Ex) {
            return false;
        }   // catch
    }

    public static void main(String[] args) throws Exception {
        SoatRecordsScrapper scrapper = new SoatRecordsScrapper("fturconi", "ostk2004", 15, null);
        scrapper.getRecordsByReg(25, "D3U281");
        scrapper.close();

//        scrapper = new SoatRecordsScrapper("fturconi", "ostk2004", 15, null);
//        scrapper.getRecordsByReg(25, "%%%%%%");
//        scrapper.close();
    }
}
