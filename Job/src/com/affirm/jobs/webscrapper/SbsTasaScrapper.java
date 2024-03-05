package com.affirm.jobs.webscrapper;

import org.openqa.selenium.By;

/**
 * Created by stbn on 03/02/17.
 */
public class SbsTasaScrapper extends Scrapper {

    private final static String URL_SBS = "http://www.sbs.gob.pe/app/stats/tasadiaria_3micro.asp";

    public SbsTasaScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public double getData() throws Exception {

        openSite(URL_SBS);

        System.out.println(driver.findElement(By.className("APLI_fila1")).findElements(By.tagName("td")).get(1).getText().replace("%", ""));

        double tasa = Double.parseDouble(driver.findElement(By.className("APLI_fila1")).findElements(By.tagName("td")).get(1).getText().replace("%", ""));

        return tasa;
    }

    public static void main(String[] args) throws Exception {
        SbsTasaScrapper scrapper = new SbsTasaScrapper("fturconi", "ostk2004", 15);
        scrapper.getData();
        scrapper.close();
    }

}
