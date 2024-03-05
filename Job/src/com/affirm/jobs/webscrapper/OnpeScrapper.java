package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.OnpeDetail;
import com.affirm.common.model.transactional.OnpeResult;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tfarro on 15/05/18.
 */
public class OnpeScrapper extends Scrapper {
    private final static String URL = "http://aplicaciones007.jne.gob.pe/multas/";

    protected OnpeScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public OnpeScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public void resolveOnpeCaptcha(String docNumber) throws Exception {
        System.out.println("INPUT> DOCNUMBER: " + docNumber);

        Captcha solved = null;
        int tries = 4;

        for (int i = 0; i < tries; i++) {
            System.out.println("Intento: " + (i + 1));
            try {
                openSite(URL);
                System.out.println("OPENED!");
            } catch (TimeoutException e) {
                e.printStackTrace();
                reportCaptcha(solved);
            }

            String pagina = driver.getPageSource();
            System.out.println("PAGINA ONPE SIZE:" + pagina.length());

            driver.findElement(By.className("cod_dni")).sendKeys(docNumber);
            //solved-captcha
            solved = solveCaptcha(shootWebElement(driver.findElement(By.id("imgCaptcha"))));
            //form principal>consultar
            driver.findElement(By.className("cod_seg")).sendKeys((solved.text).toUpperCase());
            Thread.sleep(5000);// LOADING ANIMATION
            driver.findElement(By.id("btnConsultar")).click();

            System.out.println("Se dio clic al consultar");

            WebElement btnCloseAlert = null;

            try {
                btnCloseAlert = new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.className("close")));
            } catch (Exception ep) {
            }

            if (btnCloseAlert == null) {
                System.out.println("ENTRO!!!");
                i = 99;
            } else {
                driver.findElement(By.className("close")).click();
            }
        }
    }

    public OnpeResult getData(String docNumber) throws Exception {
        System.out.println(" DocNumber: " + docNumber);

        OnpeResult result = new OnpeResult();
        List<OnpeDetail> onpeDetails = new ArrayList<>();
        OnpeDetail detail = new OnpeDetail();

        resolveOnpeCaptcha(docNumber);

        WebElement btnConsultar = null;
        try {
            btnConsultar = driver.findElement(By.id("btnConsultar"));
        } catch (Exception e) {
        }

        if (btnConsultar != null) {
            System.out.println("Max de intentos. No se encontraron resultados");
            return result;
        }

        List<WebElement> noEncontradoElements = driver.findElements(By.id("pDni_noencontrado"));

        if (noEncontradoElements.isEmpty()) {
            List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr"));
            result.setFullName(driver.findElement(By.id("pNombreCompleto")).getText());
            if (rows.size() > 1) {
                for (int i = 1; i <= rows.size(); i++) {
                    detail = new OnpeDetail();
                    detail.setCode(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr[" + i + "]/td[1]")).getText());
                    detail.setElectoralProcess(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr[" + i + "]/td[2]")).getText());
                    detail.setOmissionType(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr[" + i + "]/td[3]")).getText());
                    detail.setAmount(Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr[" + i + "]/td[4]")).getText()));
                    detail.setCollectionStage(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr[" + i + "]/td[5]")).getText());
                    onpeDetails.add(detail);
                }
            } else if (rows.size() == 1) {
                detail.setCode(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr/td[1]")).getText());
                detail.setElectoralProcess(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr/td[2]")).getText());
                detail.setOmissionType(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr/td[3]")).getText());
                detail.setAmount(Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr/td[4]")).getText()));
                detail.setCollectionStage(driver.findElement(By.xpath("//*[@id=\"multasPendientes\"]/div/table/tbody/tr/td[5]")).getText());

                onpeDetails.add(detail);
            }
            result.setDetails(onpeDetails);
        } else {
            detail.setMessage(driver.findElement(By.xpath("//*[@id=\"resultadoConsulta\"]/div[1]/div[1]/div")).getText());
            onpeDetails.add(detail);

            result.setDetails(onpeDetails);
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        OnpeScrapper scrapper = new OnpeScrapper("fturconi", "ostk2004", 40);
        System.out.println(new Gson().toJson(scrapper.getData("43325782"))); //with result
//        scrapper.getData("46226075"); // without result
        scrapper.close();
    }
}
