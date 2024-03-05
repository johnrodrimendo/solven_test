package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.AnsesDetail;
import com.affirm.common.model.transactional.AnsesResult;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Romulo Galindo Tanta
 */
public class AnsesScrapper extends Scrapper {

    private final static String URL = "http://servicioswww.anses.gob.ar/censite";
    private final static String MESSAGE_ERROR = "Su solicitud no puede ser procesada diríjase con su documento de identidad a la UDAI más próxima a su domicilio";

    public final static int DOC_ARG_CUIT = 1;
    public final static int DOC_ARG_CUIL = 2;
    public final static int DOC_ARG_CDI = 3;

    public AnsesScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public AnsesScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public AnsesResult getData(Integer docType, String docNumber) throws Exception {
        System.out.println("DOCNUMBER " + docNumber);
        AnsesResult result = new AnsesResult();

        try {
            openSite(URL);
            System.out.println("OPENED");
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }

        int tries = 5;

        Captcha solved = null;
        String pagina = driver.getPageSource();
        System.out.println("ANSES PAGE SIZE:" + pagina.length());
        //System.out.println("PAGINA ANSES:"+pagina);

        for (int i = 0; i < tries; i++) {
            //enviar el numero de documento
            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtCuitPre').value = '" + docNumber.substring(0, 2) + "'");
            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtCuitDoc').value = '" + docNumber.substring(2, docNumber.length() - 1) + "'");
            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtCuitDV').value= '" + docNumber.substring(docNumber.length() - 1, docNumber.length()) + "'");

            //consultar si el periodo es un input!(por ahora no lo es) comentado
//            //DE
//            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtDesdeMes').value = '" + "01" + "'");
//            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtDesdeAnio').value ='" + "2016" + "'");
//            //A
//            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtHastaMes').value ='" + "08" + "'");
//            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_txtHastaAnio').value ='" + "2017" + "'");
            //captcha
            solved = solveCaptcha(shootWebElement(driver.findElement(By.xpath("/html/body/form/div[3]/div[2]/table/tbody/tr/td/table/tbody/tr[4]/td[2]/div/img"))));

            //input captcha-solved
            ((JavascriptExecutor) driver).executeScript("document.getElementById('ctl00_ContentPlaceHolder1_TxTCodeNumber').setAttribute('value', '" + solved.text + "')");

            //clic
            driver.findElement(By.id("ctl00_ContentPlaceHolder1_btnVerificar")).click();

            WebElement elementPase = null;
            try {
                elementPase = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.id("ctl00_ContentPlaceHolder1_lblNombre")));
            } catch (Exception ep) {
                elementPase = null;
            }
            System.out.println("Elmento retornado:" + elementPase);

            try {
                if (driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblMensaje")) != null && driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblMensaje")).getText().equals("Número de Documento Inexistente")) {
                    return result;
                }
            } catch (Exception e) {

            }

            try {
                String message = driver.findElement(By.id("ctl00_ContentPlaceHolder1_pnlMensaje")).getText();

                if (MESSAGE_ERROR.equals(message)) {
                    System.out.println("[AfipScrapper] respuesta :" + MESSAGE_ERROR);
                }
                System.out.println("[AfipScrapper][" + docNumber + "] No obtuvimos resultados.");


            } catch (Exception e) {
                e.printStackTrace();
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


        try {
            if (driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblNombre")) != null) {

                try {
                    System.out.println("element " + By.id("ctl00_ContentPlaceHolder1_lblOOSS"));
                    System.out.println("PAGINA:" + driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblOOSS")).getText());

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Datos del titular
                String apellidos_y_nombres = driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblNombre")).getText();
                result.setFullName(apellidos_y_nombres);

                String cuil_cuit = "";
                if (this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_lblCUIL") == null) {
                    cuil_cuit = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_lblCuil").getText();
                } else {
                    cuil_cuit = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_lblCUIL").getText();
                }

                result.setCuilCuit(cuil_cuit);

            /*
            !Antecedentes
            Cada elemento de la respuesta sea buena o mala ingresa a lista y se define asi:
            [KEY][TEXTO_DEsCRIPTIVO][ESTADO:true|false]
             */
                //==trSIJPDDJJ==
                try {
                    WebElement e_trSIJPDDJJ = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trSIJPDDJJ");
                    System.out.println("e_trSIJPDDJJ = " + e_trSIJPDDJJ);
                    if (e_trSIJPDDJJ != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgSIJPDDJJ") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("SIJPDDJJ");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblSIJPDDJJ")).getText());
                        //detail.setValue(e_trSIJPDDJJ.findElement(By.id("ctl00_ContentPlaceHolder1_imgSIJPDDJJ")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trSUAF==
                try {
                    WebElement e_trSUAF = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trSUAF");
                    System.out.println("e_trSUAF = " + e_trSUAF);
                    if (e_trSUAF != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgSUAF") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("SUAF");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblSUAF")).getText());
                        //detail.setValue(e_trSUAF.findElement(By.id("ctl00_ContentPlaceHolder1_imgSUAF")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //==trDDJJNoSipa==
                try {
                    WebElement e_trDDJJNoSipa = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trDDJJNoSipa");
                    System.out.println("e_trDDJJNoSipa = " + e_trDDJJNoSipa);
                    if (e_trDDJJNoSipa != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgDDJJNoSipa") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("DDJJNoSipa");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblDDJJNoSipa")).getText());
                        //detail.setValue(e_trDDJJNoSipa.findElement(By.id("ctl00_ContentPlaceHolder1_ImgDDJJNoSipa")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trSIJPAUT==
                WebElement e_trSIJPAUT = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trSIJPAUT");
                System.out.println("e_trSIJPAUT = " + e_trSIJPAUT);
                //this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgSIJPAUT") != null

                try {
                    if (e_trSIJPAUT != null && driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblSIJPAUT")) != null) {
                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("SIJPAUT");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblSIJPAUT")).getText());
                        //detail.setValue(e_trSIJPAUT.findElement(By.id("ctl00_ContentPlaceHolder1_imgSIJPAUT")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //==trSIJPDOM==

                try {
                    WebElement e_trSIJPDOM = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trSIJPDOM");
                    if (e_trSIJPDOM != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgSIJPDOM") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("SIJPDOM");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblSIJPDOM")).getText());
                        //detail.setValue(e_trSIJPDOM.findElement(By.id("ctl00_ContentPlaceHolder1_imgSIJPDOM")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trMaternidad==

                try {
                    WebElement e_trMaternidad = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trMaternidad");
                    if (e_trMaternidad != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgMaternidad") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("Maternidad");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblMaternidad")).getText());
                        //detail.setValue(e_trMaternidad.findElement(By.id("ctl00_ContentPlaceHolder1_imgMaternidad")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trDesempleo==

                try {
                    WebElement e_trDesempleo = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trDesempleo");
                    if (e_trDesempleo != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgDesempleo") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("Desempleo");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblDesempleo")).getText());
                        //detail.setValue(e_trDesempleo.findElement(By.id("ctl00_ContentPlaceHolder1_imgDesempleo")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trPlanSoc==
                try {
                    WebElement e_trPlanSoc = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trPlanSoc");
                    if (e_trPlanSoc != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgPlanSoc") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("PlanSoc");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblPlanSoc")).getText());
                        //detail.setValue(e_trPlanSoc.findElement(By.id("ctl00_ContentPlaceHolder1_imgPlanSoc")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trRUB==
                try {
                    WebElement e_trRUB = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trRUB");
                    if (e_trRUB != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgRUB") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("RUB");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblRUB")).getText());
                        //detail.setValue(e_trRUB.findElement(By.id("ctl00_ContentPlaceHolder1_imgRUB")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trBenNoSipa==
                try {
                    WebElement e_trBenNoSipa = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trBenNoSipa");
                    if (e_trBenNoSipa != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgBenNoSipa") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("BenNoSipa");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblBenNoSipa")).getText());
                        //detail.setValue(e_trBenNoSipa.findElement(By.id("ctl00_ContentPlaceHolder1_ImgBenNoSipa")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trANME==
                try {
                    WebElement e_trANME = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trANME");
                    if (e_trANME != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgANME") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("ANME");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblANME")).getText());
                        //detail.setValue(e_trANME.findElement(By.id("ctl00_ContentPlaceHolder1_imgANME")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trOOSS==
                try {
                    WebElement e_trOOSS = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trlOOSS");//driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblOOSS"));//this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trOOSS");

                    //driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblOOSS")).getText()

                    //&& this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_imgOOSS") != null
                    if (e_trOOSS != null) {
                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("OOSS");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblOOSS")).getText());
                        //detail.setValue(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblOOSS")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));
                        //e_trOOSS.findElement(By.id("ctl00_ContentPlaceHolder1_imgOOSS")
                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trMadres614==
                try {
                    WebElement e_trMadres614 = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trMadres614");
                    if (e_trMadres614 != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgMadres614") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("Madres614");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblMadres614")).getText());
                        //detail.setValue(e_trMadres614.findElement(By.id("ctl00_ContentPlaceHolder1_ImgMadres614")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trAUH==
                try {
                    WebElement e_trAUH = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trAUH");
                    if (e_trAUH != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgAUH") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("AUH");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblAUH")).getText());
                        //detail.setValue(e_trAUH.findElement(By.id("ctl00_ContentPlaceHolder1_ImgAUH")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trProgresar==
                try {
                    WebElement e_trProgresar = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trProgresar");
                    if (e_trProgresar != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgProgresar") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("Progresar");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblProgresar")).getText());
                        //detail.setValue(e_trProgresar.findElement(By.id("ctl00_ContentPlaceHolder1_ImgProgresar")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trEfector==
                try {
                    WebElement e_trEfector = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trEfector");
                    if (e_trEfector != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgEfector") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("Efector");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblEfector")).getText());
                        //detail.setValue(e_trEfector.findElement(By.id("ctl00_ContentPlaceHolder1_ImgEfector")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //==trPensionNC==
                try {
                    WebElement e_trPensionNC = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trPensionNC");
                    if (e_trPensionNC != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgPensionNC") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("PensionNC");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblPensionNC")).getText());
                        //detail.setValue(e_trPensionNC.findElement(By.id("ctl00_ContentPlaceHolder1_ImgPensionNC")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //==trIPensionNC==
                try {
                    WebElement e_trIPensionNC = this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trIPensionNC");
                    if (e_trIPensionNC != null && this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_ImgIPensionNC") != null) {

                        AnsesDetail detail = new AnsesDetail();
                        detail.setKey("IPensionNC");
                        detail.setValue(null);
                        detail.setText(driver.findElement(By.id("ctl00_ContentPlaceHolder1_ImgIPensionNC")).getText());
                        //detail.setValue(e_trIPensionNC.findElement(By.id("ctl00_ContentPlaceHolder1_ImgIPensionNC")).getAttribute("src").toLowerCase().contains("ico_complete.gif"));

                        result.getDetails().add(detail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //ctl00_ContentPlaceHolder1_lblOOSS
                try {
                    if (this.getWebElementById(driver, "ctl00_ContentPlaceHolder1_trIPensionNC") != null) {
                        result.setCodigoTransaccion(driver.findElement(By.id("ctl00_ContentPlaceHolder1_lblTransaccion")).getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;

            } else {
                //Si tras 5to intento el elemento no resuelve lo que necesitamos devolvemos null
                System.out.println("[AfipScrapper][" + docNumber + "] No obtuvimos resultados.");
                return result;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;

    }

    public static void main(String[] args) throws Exception {
        AnsesScrapper scrapper = new AnsesScrapper("fturconi", "ostk2004", 45);
        //EJEMPLO1
        //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20161987337");

        //EJEMPLO2

        //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20337949232");
        //
        //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "20299992412");

        //AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "27299992417");
        // 20072962940 23146233759

        AnsesResult result = scrapper.getData(AnsesScrapper.DOC_ARG_CUIT, "27290743171");
        //20266479299
        System.out.println("result = " + new Gson().toJson(result));
        System.out.println("fullname = " + result.getFullName());
        System.out.println("document number = " + result.getCuilCuit());
        System.out.println("codigo transaccion = " + result.getCodigoTransaccion());
        System.out.println("detalles = " + (result.getDetails() != null && !result.getDetails().isEmpty() ? new Gson().toJson(result.getDetails()) : null));
        scrapper.close();
    }

}
