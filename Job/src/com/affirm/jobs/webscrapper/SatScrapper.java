package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.SatPlateResult;
import com.affirm.common.model.transactional.SatResult;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by jarmando on 29/09/16.
 */
public class SatScrapper extends Scrapper {

    private final static String URL = "https://www.sat.gob.pe/WebSiteV8/popupv2.aspx?t=6&v=";
    private final static String LIMA_URL = "http://www.pit.gob.pe/pit2007/EstadoCuenta.aspx";
    private final static String RECAPCHA_SITE_KEY = "6Ldy_wsTAAAAAGYM08RRQAMvF96g9O_SNQ9_hFIJ";

    public SatScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public SatPlateResult getTicketsByRegInLima(Integer queryId, String plate) throws Exception{
        System.out.println(String.format("Finding traffic tickets by REG.PLATE : %s", plate));

        int triesExt = 0;
        Captcha solved = null;

        try {
            openSite(LIMA_URL);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        do {
            if (triesExt >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (triesExt > 1) {
                System.out.println("falló el Captcha");
                reportCaptcha(solved);
            }

            WebDriverWait wait = new WebDriverWait(driver, 60);
            wait.until(ExpectedConditions.elementToBeClickable(By.id("btnBuscarPlaca")));

            driver.findElement(By.id("txtPlaca")).sendKeys(plate);

            solved = solveCaptcha(RECAPCHA_SITE_KEY, URL);

            driver.findElement(By.id("btnBuscarPlaca")).click();

            triesExt++;
        } while (driver.findElements(By.className("g-recaptcha")).size() > 0);

        System.out.println("Searching for records");

        int loopIndex = driver.findElements(By.xpath("//*[@id=\"grdEstadoCuenta\"]/tbody/tr")).size() - 1;

        SatPlateResult result = new SatPlateResult();
        List<SatPlateResult.Report> reports = new ArrayList<>();

        Double total = 0.0;
        String msg = driver.findElement(By.id("lblMensajeVacio")).getText();
        result.setMessage(msg);
        result.setQueryId(queryId);
        result.setRegPlate(plate);

        for (int i = 2; i < loopIndex + 2; i++) {
            SatPlateResult.Report report = new SatPlateResult.Report();
            report.setPlate(driver.findElement(By.xpath("//*[@id=\"grdEstadoCuenta\"]/tbody/tr[" + i + "]/td[1]")).getText());
            report.setDoc(driver.findElement(By.xpath("//*[@id=\"grdEstadoCuenta\"]/tbody/tr[" + i + "]/td[3]")).getText());
            report.setDate(driver.findElement(By.xpath("//*[@id=\"grdEstadoCuenta\"]/tbody/tr[" + i + "]/td[4]")).getText());
            report.setAmount(driver.findElement(By.xpath("//*[@id=\"grdEstadoCuenta\"]/tbody/tr[" + i + "]/td[8]")).getText());
            report.setStatus(driver.findElement(By.xpath("//*[@id=\"grdEstadoCuenta\"]/tbody/tr[" + i + "]/td[9]")).getText());
            total = total + Double.valueOf(report.getAmount());
            reports.add(report);
        }

        result.setTotal(total);
        result.setReports(reports);
        result.setReportsJSONArray(new JSONArray(reports));
        return result;
    }

    /*public SatResult getData(Integer docType, String docNumber) throws Exception {
        System.out.println(String.format("DOCTYPE %d - DOCNUMBER %s", docType, docNumber));

        SatResult result = new SatResult();
        int triesExt = 0;
        Captcha solved = null;

        try {
            openSite(URL);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        WebDriverWait waiter = new WebDriverWait(driver, 60);

        driver.switchTo().frame(waiter.until(ExpectedConditions.elementToBeClickable(By.id("fraRightFrame"))));
        driver.findElement(By.id("ui-accordion-menu-header-0")).click();
        driver.findElement(By.id("menuOption01")).click();

        do {
            if (triesExt >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (triesExt > 1) {
                System.out.println("falló el Captcha");
                reportCaptcha(solved);
            }

            Select selectTipoBusqueda = new Select(driver.findElement(By.id("tipoBusqueda")));
            switch (docType) {
                case IdentityDocumentType.DNI: // DNI
                case IdentityDocumentType.CE: // CE
                    selectTipoBusqueda.selectByValue("busqTipoDocIdentidad");
                    Select selectTipoDocumento = new Select(driver.findElement(By.id("ctl00_cplPrincipal_ddlTipoDocu")));
                    selectTipoDocumento.selectByValue(docType == IdentityDocumentType.DNI ? "2" : "4");
                    waiter.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_cplPrincipal_txtDocumento")))
                            .sendKeys(docNumber);
                    break;
                case IdentityDocumentType.RUC: // RUC
                    selectTipoBusqueda.selectByValue("busqRazonSocial");
                    break;
            }

            File imageCaptcha = shootWebElement(driver.findElement(By.className("captcha_class")));
            solved = solveCaptcha(imageCaptcha);
            driver.findElement(By.name("ctl00$cplPrincipal$txtCaptcha")).clear();
            driver.findElement(By.name("ctl00$cplPrincipal$txtCaptcha")).sendKeys((solved.text));
            driver.findElement(By.id("ctl00_cplPrincipal_CaptchaContinue")).click();
            triesExt++;
        } while (driver.findElements(By.id("tipoBusqueda")).size() > 0);

        List<WebElement> resultsElements = driver.findElements(By.id("ctl00_cplPrincipal_grdAdministrados"));
        if (resultsElements.isEmpty()) {
            return result;
        }
        WebElement tableResults = resultsElements.get(0);
        for (WebElement codeRow : tableResults.findElements(By.tagName("tr"))) {
            if (codeRow.getAttribute("class").equals("grillaHeader"))
                continue;
            SatResult.SatIdReport rpc = new SatResult.SatIdReport();
            rpc.setId(codeRow.findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText());
            rpc.setTaxpayer(codeRow.findElements(By.tagName("td")).get(1).findElement(By.tagName("a")).getText());

            if (result.getSatIdReport() == null)
                result.setSatIdReport(new ArrayList<>());
            result.getSatIdReport().add(rpc);
        }

        for (int i = 0; i < result.getSatIdReport().size(); i++) {
            SatResult.SatIdReport idReport = result.getSatIdReport().get(i);

            tableResults = driver.findElement(By.id("ctl00_cplPrincipal_grdAdministrados"));
            List<WebElement> trElements = tableResults.findElements(By.xpath("//tr[not(contains(@class,'grillaHeader'))]"));
            WebElement elementToSearch = null;
            for (WebElement trElement : trElements) {
                if (trElement.findElements(By.tagName("td")).get(0).findElement(By.tagName("a")).getText().equals(idReport.getId())) {
                    elementToSearch = trElement.findElements(By.tagName("td")).get(0).findElement(By.tagName("a"));
                    break;
                }
            }
            elementToSearch.click();

            driver.findElement(By.id("ctl00_cplPrincipal_rbtMostrar_1")).click();
            if (driver.findElements(By.id("ctl00_cplPrincipal_grdEstadoCuenta")).size() > 0) {
                WebElement tableAnualResuls = driver.findElement(By.id("ctl00_cplPrincipal_grdEstadoCuenta"));
                for (WebElement tableRow : tableAnualResuls.findElements(By.tagName("tr"))) {
                    if (tableRow.getAttribute("class").equals("grillaHeader"))
                        continue;
                    SatResult.AnnualReport annualReport = new SatResult.AnnualReport();
                    annualReport.setYear(tableRow.findElements(By.tagName("td")).get(1).getText().equals(" ") ? "total" : tableRow.findElements(By.tagName("td")).get(1).getText());
                    if (!tableRow.findElements(By.tagName("td")).get(2).getText().isEmpty())
                        annualReport.setAmount(Double.parseDouble(tableRow.findElements(By.tagName("td")).get(2).getText()));

                    if (idReport.getAnnualReports() == null)
                        idReport.setAnnualReports(new ArrayList<>());
                    idReport.getAnnualReports().add(annualReport);
                }
            }

            // Go back and do the search again only if its not the last one
            if (i + 1 < result.getSatIdReport().size()) {

                // Click in new search (Because we cant go back, fucking SAT)
                driver.findElement(By.id("ctl00_cplPrincipal_btnNuevaBusqueda")).click();

                driver.findElement(By.id("ui-accordion-menu-header-0")).click();
                driver.findElement(By.id("menuOption01")).click();

                do {
                    if (triesExt >= 5) {
                        System.out.println("Se hicieron 4 intentos");
                        return null;
                    } else if (triesExt > 1) {
                        System.out.println("falló el Captcha");
                        reportCaptcha(solved);
                    }

                    Select selectTipoBusqueda = new Select(driver.findElement(By.id("tipoBusqueda")));
                    switch (docType) {
                        case IdentityDocumentType.DNI: // DNI
                        case IdentityDocumentType.CE: // CE
                            selectTipoBusqueda.selectByValue("busqTipoDocIdentidad");
                            Select selectTipoDocumento = new Select(driver.findElement(By.id("ctl00_cplPrincipal_ddlTipoDocu")));
                            selectTipoDocumento.selectByValue(docType == IdentityDocumentType.DNI ? "2" : "4");
                            driver.findElement(By.id("ctl00_cplPrincipal_txtDocumento")).sendKeys(docNumber);
                            break;
                        case IdentityDocumentType.RUC: // RUC
                            selectTipoBusqueda.selectByValue("busqRazonSocial");
                            break;
                    }

                    File imageCaptcha = shootWebElement(driver.findElement(By.className("captcha_class")));
                    solved = solveCaptcha(imageCaptcha);
                    driver.findElement(By.name("ctl00$cplPrincipal$txtCaptcha")).clear();
                    driver.findElement(By.name("ctl00$cplPrincipal$txtCaptcha")).sendKeys((solved.text));
                    driver.findElement(By.id("ctl00_cplPrincipal_CaptchaContinue")).click();
                    triesExt++;
                } while (driver.findElements(By.id("tipoBusqueda")).size() > 0);
            }
        }

        return result;
    }*/

    private Captcha solveCaptcha(String reCaptchaKey, String url) throws Exception {
        Captcha captcha = solveReCaptcha(reCaptchaKey, url, CountryParam.COUNTRY_PERU);

        if (captcha != null) {
            System.out.println("ReCaptcha is solved");
            ((JavascriptExecutor) driver).executeScript(" document.getElementById('g-recaptcha-response').value = '" + captcha.text + "'");
        } else {
            System.out.println("ReCaptcha could not be solved");
            throw new Exception();
        }

        return captcha;
    }

    public static void main(String[] args) throws Exception {
        SatScrapper scrapper;
        scrapper = new SatScrapper("fturconi", "ostk2004", 15, null);
//        System.out.println(new Gson().toJson(scrapper.getData(IdentityDocumentType.DNI, "46226075")));
        scrapper.close();

        scrapper = new SatScrapper("fturconi", "ostk2004", 15, null);
        System.out.println(new Gson().toJson(scrapper.getTicketsByRegInLima(25, "D3U281")));
        scrapper.close();
    }
}