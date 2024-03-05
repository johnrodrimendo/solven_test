package com.affirm.jobs.webscrapper;

import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.SbsResult;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;

/**
 * Created by john on 29/09/16.
 */
public class SbsScrapper extends Scrapper {

    public final static int SBS_BANCA = 1;
    public final static int SBS_FINANCIERA = 2;

    public final static int DNI = 1;
    public final static int CE = 2;
    public final static int RUC = 3;

    private final static String URL_SBS_B = "http://www.sbs.gob.pe/app/pp/EstadisticasSAEEPortal/Paginas/TIActivaTipoCreditoEmpresa.aspx?tip=B";
    private final static String URL_SBS_F = "http://www.sbs.gob.pe/app/pp/EstadisticasSAEEPortal/Paginas/TIActivaTipoCreditoEmpresa.aspx?tip=F";

    public SbsScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public SbsScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public List getData(int sbsType) throws Exception {

        List result = new ArrayList<>();

        try {
            if (sbsType == SBS_BANCA) {
                openSite(URL_SBS_B);
            } else if (sbsType == SBS_FINANCIERA) {
                openSite(URL_SBS_F);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        List rowTags = new ArrayList<String>();
        List colTags = new ArrayList<String>();
        List values = new ArrayList<>();
        List rowsTR;
        int count = 0;

        //ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
        WebElement date_box = driver.findElement(By.id("ctl00_cphContent_rdpDate_dateInput"));
        WebElement tableData = driver.findElement(By.id("ctl00_cphContent_rpgActualMn_ctl00_DataZone_DT")); //Data
        WebElement tableFull = driver.findElement(By.id("ctl00_cphContent_rpgActualMn_OT")); //Filas

        for (WebElement tr : tableData.findElements(By.tagName("tr"))) {
            if (count == 0) {
                for (WebElement th : tr.findElements(By.tagName("th"))) {
                    System.out.print(th.getText() + "\t");
                    colTags.add(th.getText());
                }
                System.out.print("\n");
            }
            for (WebElement td : tr.findElements(By.tagName("td"))) {
                System.out.print(td.getText() + "\t\t");
                values.add(td.getText());
            }
            System.out.print("\n");
            count++;
        }

        // Etiqueta de Fila
        boolean capture = false;
        for (WebElement tr : tableFull.findElements(By.tagName("tr"))) {
            if (tr.getAttribute("id").equals("ctl00_cphContent_rpgActualMn_OT__lhRow")) {
                rowTags.add(tr.getText());
                break;
            } else if (capture == true) {
                rowTags.add(tr.getText());
            } else if (tr.getAttribute("id").equals("ctl00_cphContent_rpgActualMn_OT__fhRow")) {
                rowTags.add(tr.getText());
                capture = true;
            }
        }
        ;

        System.out.println("Tamaño del elementos = " + values.size());
        System.out.println("Tamaño de Columnas = " + colTags.size());
        System.out.println("Tamaño del Filas = " + rowTags.size());

        int index = 0;
        boolean consumo = false;
        for (Object row : rowTags) {
            for (Object col : colTags) {
                if (row.equals("Consumo") || (row.equals("Tarjetas de Crédito") && consumo) || row.equals("Microempresas") || row.equals("Préstamos no Revolventes para libre disponibilidad a más de 360 días")) {

                    consumo = row.equals("Consumo") ? true : consumo;

                    SbsResult item = new SbsResult();

                    item.setProducto(row.toString());
                    item.setEntidad(col.toString());
                    //if (!values.get(index).toString().equals("-") || !values.get(index).toString().equals("-")) {
                    System.out.println("values.get(index).toString() " + values.get(index).toString());
                    if (isNumber(values.get(index).toString())) {
                        System.out.println("Es entero!");
                        item.setTasa(Double.parseDouble(values.get(index).toString()));
                    }

                    result.add(item);

                    System.out.print(index + "\t" + row + " \t ");
                    System.out.print(col);
                    System.out.println("\t" + values.get(index));
                }
                index++;
            }
            System.out.print("\n");
        }

        for (Object res : result) {
            System.out.println("SBS RESULT " + ((SbsResult) res).toString());
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        long ini = new Date().getTime();
        SbsScrapper scrapper = new SbsScrapper("fturconi", "ostk2004", 15);
        scrapper.getData(SBS_BANCA);
        scrapper.close();
        System.out.println("Se demoró el Scrapper: " + (new Date().getTime() - ini + " milis"));
    }

}
