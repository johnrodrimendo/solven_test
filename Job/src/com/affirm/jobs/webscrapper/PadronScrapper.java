package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.transactional.PadronResult;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * @author Romulo Galindo Tanta
 */
public class PadronScrapper extends Scrapper {

    private final static String URL = "https://www.padron.gob.ar/";

    public PadronScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public PadronResult getData(String docNumber, String sexo, String distrito) throws Exception {
        System.out.println("DOCNUMBER " + docNumber);
        PadronResult result = new PadronResult();

        try {
            openSite(URL);
            System.out.println("OPENED");
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        int tries = 5;

        Captcha solved = null;

        System.out.println(">INI>>" + driver.findElement(By.id("resultPage")).isDisplayed());
        for (int i = 0; i < tries; i++) {
            //enviar el numero de documento
            ((JavascriptExecutor) driver).executeScript("document.getElementById('matricula').value = '" + docNumber + "'");

            //Para seleccionar el distrito
            ((JavascriptExecutor) driver).executeScript("$('#distrito option').filter(function() {return this.text == '" + distrito + "'; }).attr('selected', true);");

            //Para seleccionar el sexo
            ((JavascriptExecutor) driver).executeScript("$('input:radio[name=sexo]').val(['" + sexo + "']);");

            //solved-captcha
            solved = solveCaptcha(shootWebElement(driver.findElement(By.id("form")).findElement(By.tagName("img"))));

            ((JavascriptExecutor) driver).executeScript("document.getElementById('captchaResult').value = '" + solved.text + "'");

            //form principal -btn
            driver.findElement(By.id("btnConsulta")).click();

            WebElement elementPase = null;
            try {
                //espero a que el elemento exista
                elementPase = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.id("btnCons")));
            } catch (Exception ep) {
                elementPase = null;
            }

            //validando bueno o malo
            if (i < tries) {
                if (elementPase != null) {
                    i = 99;
                } else {
                    //regresamos a la pantalla inicial!!
                    reportCaptcha(solved);
                }
            } else {
                //Al 5to intento retornar nulo
                return null;
            }
        }

        System.out.println(">FIN>>" + driver.findElement(By.id("resultPage")).isDisplayed());

        //Elemento serializado
        //elemento principal
        String nombre = driver.findElement(By.id("nombre")).getText();
        String matricula = driver.findElement(By.id("documento")).getText();
        String tipodoc = driver.findElement(By.id("tipodoc")).getText();
        String distritoname = driver.findElement(By.id("distritoname")).getText();
        String circuito = driver.findElement(By.id("circuito")).getText();
        String seccion = driver.findElement(By.id("seccion")).getText();

        String establecimiento = "";
        String direccion = "";
        String mesa = "";
        String orden = "";

        List<WebElement> userInfoItems = driver.findElements(By.className("user_info"));
        for (WebElement userInfo : userInfoItems) {
            if (userInfo.getTagName().toLowerCase().contentEquals("strong") & userInfo.getAttribute("class").toLowerCase().contentEquals("user_info descripcion")) {
                establecimiento = userInfo.getText();
            }

            if (userInfo.getTagName().toLowerCase().contentEquals("strong") & userInfo.getAttribute("class").toLowerCase().contentEquals("user_info direccion")) {
                direccion = userInfo.getText();
            }

            if (userInfo.getTagName().toLowerCase().contentEquals("strong") & userInfo.getAttribute("class").toLowerCase().contentEquals("user_info mesa")) {
                mesa = userInfo.getText();
            }

            if (userInfo.getTagName().toLowerCase().contentEquals("strong") & userInfo.getAttribute("class").toLowerCase().contentEquals("user_info orden")) {
                orden = userInfo.getText();
            }
        }

        result.setFullName(nombre);
        result.setMatricula(matricula);
        result.setDistritoName(distritoname);
        result.setCircuito(circuito);
        result.setSeccion(seccion);

        result.setEstablecimiento(establecimiento);
        result.setDireccion(direccion);
        result.setMesa(mesa);
        result.setOrden(orden);

        return result;

    }

    public static void main(String[] args) throws Exception {
        PadronScrapper scrapper = new PadronScrapper("fturconi", "ostk2004", 45);
        //Resultado simple
        PadronResult result = scrapper.getData("26647929", "m", "Capital Federal");

        System.out.println("result = " + new Gson().toJson(result));
        scrapper.close();
    }

}
