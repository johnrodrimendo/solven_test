package com.affirm.jobs.webscrapper;

import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.UniversidadPeruResult;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class UniversidadPeruScrapper extends Scrapper {

    private final static String URL = "https://www.universidadperu.com/empresas";

    public UniversidadPeruScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public UniversidadPeruScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public UniversidadPeruResult getData(String docNumber) throws Exception {
        System.out.println("DOCNUMBER " + docNumber);
        UniversidadPeruResult result = new UniversidadPeruResult();

        try {
            openSite(URL);
            System.out.println("OPENED");
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        int tries = 1;
        boolean retry;

        do {
            if (tries >= 5) {
                System.out.println("Se hicieron 4 intentos");
                return null;
            } else if (tries > 1) {
                System.out.println("Falló la consulta");
            }

            WebDriverWait waiter = new WebDriverWait(driver, 30);
            screenshot();
            waiter.until(ExpectedConditions.presenceOfElementLocated(By.id("buscaempresa")));
            screenshot();
            WebElement txtRuc = driver.findElement(By.id("buscaempresa"));
            txtRuc.sendKeys(docNumber);

            driver.findElement(By.xpath("//input[@type='submit' and @value='Buscar']")).click();

            List<WebElement> elementsFound = new ArrayList<>();
            List<WebElement> elementsNotFound = new ArrayList<>();

            try {
                waiter.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='https://www.universidadperu.com/empresas/']")));

                elementsFound = driver.findElements(By.xpath("//a[@href='https://www.universidadperu.com/empresas/']"));
            } catch (TimeoutException | NoSuchElementException e) {
                System.out.println("No se encuentra RUC");
            }

            if (elementsFound.size() == 0) {
                elementsNotFound = driver.findElements(By.xpath("//p[text() = 'Lo más probable es que busques a:']"));

                if (elementsNotFound.isEmpty()) {
                    elementsNotFound = driver.findElements(By.xpath("//h3[text() = 'No hay ninguna empresa ACTIVA que se ajuste a dicha búsqueda.']"));

                    if (elementsNotFound.isEmpty())
                        elementsNotFound = driver.findElements(By.xpath("//h3[text() = 'Búsqueda NO Realizada']"));
                }
            }

            if (elementsFound.isEmpty() && elementsNotFound.isEmpty())
                retry = true;
            else
                retry = false;

            tries++;

        } while (retry);

        if (driver.findElements(By.xpath("//ul/li/strong[text() = 'Teléfonos:']")).size() > 0) {

            List<WebElement> lis = driver.findElements(By.xpath("//span[@itemprop='telephone']"));

            WebElement firstTelephone = lis.get(0).findElement(By.tagName("strong"));

            result.setPhoneNumber(firstTelephone.getText());

            System.out.println("RESULT " + result.toString());

            return result;

        } else {
            System.out.println("No se encuentra tel. laboral");
            return result;
        }
    }

    public static void main(String[] args) throws Exception{
        UniversidadPeruScrapper scrapper = new UniversidadPeruScrapper("fturconi", "ostk2004", 15);
        scrapper.getData("20254138577");
        scrapper.close();
    }
}

