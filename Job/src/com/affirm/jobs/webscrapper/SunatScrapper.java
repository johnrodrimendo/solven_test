package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.SunatResult;
import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * Created by john on 29/09/16.
 */
public class SunatScrapper extends Scrapper {

    private int substractIndex = 0;

    private final static String URL = "http://www.sunat.gob.pe/cl-ti-itmrconsruc/FrameCriterioBusquedaMovil.jsp";

    public SunatScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public SunatScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public SunatResult getData(int docType, String docNumber) throws Exception {
        System.out.println("DocType: " + docType + " DocNumber: " + docNumber);
        int tries = 1;
        File imageCaptcha;
        Captcha solved = null;
        WebElement captcha;

        SunatResult result = new SunatResult();

        try {
            openSite(URL);
        } catch (TimeoutException e) {
            e.printStackTrace();
            return result;
        }

        do {
            if (tries >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (tries > 1) {
                System.out.println("falló el Captcha");
                reportCaptcha(solved);
                driver.navigate().back();
            }

            while (driver.findElements(By.id("btnPorDocumento")).size() == 0) {
                System.out.println("reloading " + tries);
                openSite(URL);
                tries++;
                if (tries == 5) {
                    return null;
                }
            }

            if (docType == SunatResult.DNI_TYPE) {
                driver.findElement(By.id("btnPorDocumento")).click();
                driver.findElement(By.id("txtNumeroDocumento")).sendKeys(docNumber);
            } else if (docType == SunatResult.CEX_TYPE) {
                driver.findElement(By.id("btnPorDocumento")).click();
                Select selectTipoDocumento = new Select(driver.findElement(By.id("cmbTipoDoc")));
                selectTipoDocumento.selectByValue("4");
                driver.findElement(By.id("txtNumeroDocumento")).sendKeys(docNumber);
            } else if (docType == SunatResult.RUC_TYPE) {
                driver.findElement(By.id("btnPorRuc")).click();
                driver.findElement(By.id("txtRuc")).sendKeys(docNumber);
            }

            captcha = driver.findElement(By.id("imgCodigo"));
            imageCaptcha = shootWebElement(captcha);
            solved = solveCaptcha(imageCaptcha);
            driver.findElement(By.id("txtCodigo")).sendKeys((solved.text).toUpperCase());
            driver.findElement(By.id("btnAceptar")).click();

            tries++;
        }
        while (driver.getTitle().equals(".:: Pagina de Mensajes ::."));

        List rows = null;
        WebElement listGroup;
        if (docType == SunatResult.DNI_TYPE || docType == SunatResult.CEX_TYPE) {
            if (!driver.findElements(By.className("list-group-item-heading")).isEmpty()) {
                result.setRuc(driver.findElements(By.className("list-group-item-heading")).get(0).getText().split(": ")[1]);
                result.setTradeName(driver.findElements(By.className("list-group-item-heading")).get(1).getText());
                result.setLocated(driver.findElements(By.className("list-group-item-text")).get(0).getText().split(": ")[1]);
                result.setStatus(driver.findElements(By.className("list-group-item-text")).get(1).getText().split(": ")[1]);
                driver.findElement(By.tagName("a")).click();

                listGroup = driver.findElement(By.className("list-group"));
                rows = listGroup.findElements(By.className("col-sm-7"));
            } else {
                System.out.println("com.nab.webscrapper.SunatScrapper.getData(): RETORNA NULO");
                return result;
            }
        } else if (docType == SunatResult.RUC_TYPE) {

            if (!driver.findElements(By.className("list-group")).isEmpty()) {
                String rucType = docNumber.substring(0,2);
                listGroup = driver.findElement(By.className("list-group"));
                rows = listGroup.findElements(By.className("col-sm-7"));
                substractIndex = ((WebElement)rows.get(1)).getText().contains("PERSONA NATURAL") ? 0 : 1; // Cuando es una persona natural se muestra una fila de mas
                result.setRuc(docNumber);
                result.setTradeName(((WebElement) rows.get(0)).getText().split(" - ")[1]);    // PERSONA NATURAL SIN NEGOCIO
                result.setStatus(((WebElement) rows.get(3)).getText());    // PERSONA NATURAL SIN NEGOCIO
            } else {
                System.out.println("com.nab.webscrapper.SunatScrapper.getData(): RETORNA NULO");
                return result;
            }
        }
        result.setTaxpayerType(((WebElement) rows.get(1)).getText());    // PERSONA NATURAL SIN NEGOCIO
        result.setRegisterDate(SunatResult.DATE_FORMAT.parse(((WebElement) rows.get(4 - substractIndex)).getText()));    // 02/05/2012
        result.setTaxpayerCondition(((WebElement) rows.get(6 - substractIndex)).getText());    // HABIDO
        result.setFiscalAddress(((WebElement) rows.get(7 - substractIndex)).getText());    // JR. VIVIANO PAREDES NRO. 851 URB. SAN JUAN (ESPALDA DEL COLEGIO MARISTAS 2DO PISO)LIMA - LIMA - SAN JUAN DE MIRAFLORES

        List<WebElement> economicRows = ((WebElement) rows.get(8 - substractIndex)).findElements(By.tagName("td"));
        JSONArray economicActivities = new JSONArray();
        for (WebElement activity : economicRows) {
            economicActivities.put(activity.getText());
        }
        result.setEconomicActivities(economicActivities.toString());    // 74996 - OTRAS ACTIVIDADES EMPRESARIALES NCP.

        List<WebElement> voucherRows = ((WebElement) rows.get(9 - substractIndex)).findElements(By.tagName("td"));
        JSONArray vouchers = new JSONArray();
        for (WebElement activity : voucherRows) {
            vouchers.put(activity.getText());
        }
        result.setVoucher(vouchers.toString());    // RECIBO POR HONORARIOS

        result.setDigitalVouchers(((WebElement) rows.get(10 - substractIndex)).getText());    // RECIBO POR HONORARIO (desde 15/07/2014)
        result.setPleJoinedSince(((WebElement) rows.get(11 - substractIndex)).getText());    // -

        List<WebElement> padronRows = ((WebElement) rows.get(12 - substractIndex)).findElements(By.tagName("td"));
        JSONArray padrones = new JSONArray();
        for (WebElement activity : padronRows) {
            padrones.put(activity.getText());
        }
        result.setPadron(padrones.toString());    // NINGUNO

        System.out.println("-> " + result.toString());

        return result;

    }

    public static void main(String[] args) throws Exception {
        SunatScrapper scrapper = new SunatScrapper("fturconi", "ostk2004", 15);
//        scrapper.getData(SunatResult.RUC_TYPE, "10454325864"); // Persona natural con ruc
//        scrapper.getData(SunatResult.DNI_TYPE, "45432586"); // Persona natural con dni
        scrapper.getData(SunatResult.RUC_TYPE, "10462260755"); // Persona juridica con ruc
//        scrapper.getData(SunatResult.RUC_TYPE, "15479512831"); // Sucesiones indivisas, sociedades conyugales, FFAA, FFPP, DPI, CEX
//        scrapper.getData(SunatResult.RUC_TYPE, "17221748514"); // Fecha inscripcion entre 1993 y 2000
//        scrapper.getData(SunatResult.CEX_TYPE, "115867");
//        System.out.println("sr" + sr);
//        scrapper.getData("", "56198035");
        scrapper.close();
    }
}

