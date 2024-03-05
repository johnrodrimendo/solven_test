package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.AfipResult;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Romulo Galindo Tanta
 */
public class AfipScrapper extends Scrapper {

    private final static String URL = "https://seti.afip.gob.ar/padron-puc-constancia-internet/jsp/Constancia.jsp";

    private final static int DOC_ARG_CUIT = 1;
    private final static int DOC_ARG_CUIL = 2;
    private final static int DOC_ARG_CDI = 3;

    private String lineAsterisks = "****************************************************";

    public AfipScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public AfipScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public AfipResult getData(Integer docType, String docNumber) throws Exception {
//        System.out.println("INPUT> DOCNUMBER: " + docNumber);
        AfipResult result = new AfipResult();
//
//        try {
//            openSite(URL);
//            System.out.println("OPENED!");
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        //Resolucion afip basado en for por que con do-while no me permitia
//        int tries = 5;
//
//        Captcha solved = null;
//
//        for (int i = 0; i < tries; i++) {
//            //enviar el numero de documento
//            ((JavascriptExecutor) driver).executeScript("document.getElementsByName('cuit')[0].value = '" + docNumber + "'");
//
//            //solved-captcha
//            solved = solveCaptcha(shootWebElement(driver.findElement(By.id("CaptchaCode"))));
//
//            ((JavascriptExecutor) driver).executeScript("document.getElementById('captchaField').value = '" + solved.text + "'");
//
//            ((JavascriptExecutor) driver).executeScript("ejecutar(true);");
//
//            //form principal>submit
//            driver.findElement(By.id("Constancia")).submit();
//
//            WebElement elementPase = null;
//            try {
//                //espero a que el elemento exista
//                elementPase = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.name("btn_Anterior")));
//            } catch (Exception ep) {
//                elementPase = null;
//            }
//
//            //validando bueno o malo
//            if (i < tries) {
//                if (elementPase != null) {
//                    i = 99;
//                } else {
//                    //regresamos a la pantalla inicial!!
//                    reportCaptcha(solved);
//                }
//            } else {
//                //Al 5to intento retornar nulo
//                return null;
//            }
//        }
//
//        //Luego de que se submitee correctamente
//        if (driver.findElement(By.id("miContextoForm")) != null) {
//
//            //parte1 !no se por que pero sin este comentario da error el xpath
//            System.out.println("(*)>" + driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[2]/td/table/tbody/tr[1]/td/b/i/font")).getText());
//
////            String cuit = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[2]/td/table/tbody/tr[1]/td/b/i/font")).getText();
//            String nombre = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[2]/td/table/tbody/tr[2]/td/b/i/font")).getText();
//
////            result.setCuit(cuit);
//            result.setFullName(nombre);
//
//            //SECCION:IMPUESTO/REGIMEN REGISTRADO Y FECHA DE ALTA
////            String campo_irg1_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[1]/td[1]/font")).getText();
//            String campo_irg1_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[1]/td[2]/font")).getText();
//
////            result.setIrg1(campo_irg1_key);
//            result.setIrg1(campo_irg1_value);
//
////            String campo_irg2_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[2]/td[1]/font")).getText();
//            String campo_irg2_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]/font")).getText();
//
////            result.setCampo_irg2_key(campo_irg2_key);
//            result.setIrg2(campo_irg2_value);
//
////            String campo_irg3_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[3]/td[1]/font")).getText();
//            String campo_irg3_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]/font")).getText();
//
////            result.setCampo_irg3_key(campo_irg3_key);
//            result.setIrg3(campo_irg3_value);
//
////            String campo_irg4_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[4]/td[1]/font")).getText();
//            String campo_irg4_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]/font")).getText();
//
////            result.setCampo_irg4_key(campo_irg4_key);
//            result.setIrg4(campo_irg4_value);
//
//            String campo_irg5 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[6]/td/font")).getText();
//            result.setIrg5(campo_irg5);
//
//            //SECCION:Domicilio Fiscal
//            String campo_df1 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[6]/td/table/tbody/tr[1]/td/font")).getText();
//            String campo_df2 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[6]/td/table/tbody/tr[2]/td/font")).getText();
//            result.setDf1(campo_df1);
//            result.setDf2(campo_df2);
//
//            //SECCION:Dependencia donde se encuentra inscripto
//            String campo_di1 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[7]/td/table/tbody/tr[1]/td/font")).getText();
//            String campo_di2 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[7]/td/table/tbody/tr[2]/td/font")).getText();
//            String campo_di3 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[6]/td/table/tbody/tr[1]/td/font")).getText();
//            result.setDi1(campo_di1);
//            result.setDi2(campo_di2);
//            result.setDi3(campo_di3);
//
//            return result;
//
//        } else {
//            //Si tras 5to intento el elemento no resuelve lo que necesitamos devolvemos null
//            System.out.println("[AfipScrapper][" + docNumber + "] No obtuvimos resultados.");
//            return result;
//
//        }


        //Resolucion afip basado en for por que con do-while no me permitia

        //Se resuelve el captcha para poder pasar a hacer la consulta
        resolveAfipCaptcha(docNumber);

        WebElement tipoCertificado = null;

        try {
            tipoCertificado = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.name("tipoCertificado")));
        } catch (Exception ep) {
        }

        if (tipoCertificado != null) {
            ((JavascriptExecutor) driver).executeScript("document.getElementsByName('tipoCertificado')[0].value = \"" + 1 + "\"");
            driver.findElements(By.className("botonera-out-largo")).get(1).click();
            getResultByType(result);

            ((JavascriptExecutor) driver).executeScript("document.getElementsByName('tipoCertificado')[0].value = \"" + 2 + "\"");
            driver.findElements(By.className("botonera-out-largo")).get(1).click();
            getResultByType(result);
        } else {
            //sino, obtenemos el resultado mostrado en la pantalla.
            getResultByType(result);
        }

        return result;

    }

    public static void main(String[] args) throws Exception {
        AfipScrapper scrapper = new AfipScrapper("fturconi", "ostk2004", 15);
//        AfipResult result = scrapper.getData(AfipScrapper.DOC_ARG_CUIT, "20383176191");//RSPTA AQUI
        AfipResult result = scrapper.getData(AfipScrapper.DOC_ARG_CUIT, "24267194846")  ;//RSPTA AQUI
//        AfipResult result = scrapper.getData(AfipScrapper.DOC_ARG_CUIT, "20075626569");
//        TODO
//        CUANDO ME EQUIVOCO EN EL CODIGO. **ALERTA** DEBERIA VOLVER A INTENTAR
        System.out.println("scrapper = " + new Gson().toJson(result));
        System.out.println("fullname = " + result.getFullName());
        System.out.println("irg = " + (result.getIrgJSONArray() != null ? result.getIrgJSONArray().toString() : "[]"));
        System.out.println("df1 = " + result.getDf1());
        System.out.println("df2 = " + result.getDf2());
        System.out.println("di1 = " + result.getDi1());
        System.out.println("di2 = " + result.getDi2());
        System.out.println("di3 = " + result.getDi3());
        System.out.println("tipo = " + result.getTipo());
        System.out.println("df3 = " + result.getDf3());
        System.out.println("control1 = " + result.getControl1());
        System.out.println("control2 = " + result.getControl2());
        System.out.println("control3 = " + result.getControl3());
        System.out.println("control4 = " + result.getControl4());
        System.out.println("categoria = " + result.getControlCategoria());
        System.out.println("actividad = " + result.getControlActividad());

        scrapper.close();
    }

    public Captcha resolveAfipCaptcha(String docNumber) throws Exception {
        System.out.println("INPUT> DOCNUMBER: " + docNumber);

        try {
            openSite(URL);
            System.out.println("OPENED!");
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Captcha solved = null;
        solved = solveCaptcha(shootWebElement(driver.findElement(By.id("CaptchaCode"))));
        int tries = 3;


        for (int i = 0; i < tries; i++) {
            String pagina = driver.getPageSource();
            System.out.println("PAGINA AFIP SIZE:" + pagina.length());
            //solved-captcha
            solved = solveCaptcha(shootWebElement(driver.findElement(By.id("CaptchaCode"))));

            ((JavascriptExecutor) driver).executeScript("document.getElementsByName('cuit')[0].value = '" + docNumber + "'; document.getElementById('captchaField').value = '" + solved.text + "';ejecutar(true);");

            //form principal>submit
            driver.findElement(By.id("Constancia")).submit();

            System.out.println("Se dio clic al submit");

            WebElement btnAnterior = null;
            WebElement alertText = null;

            try {
                btnAnterior = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.name("btn_Anterior")));
            } catch (Exception ep) {
            }

            try {
                alertText = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/table/tbody/tr[1]/td/table/tbody/tr[1]/td/b/font")));
            } catch (Exception ep) {
            }

            if (btnAnterior != null & alertText != null) {
                reportCaptcha(solved);
                btnAnterior.click();
            } else if (alertText == null) {
                i = 99;
            }
        }

        return solved;
    }

    public void getResultByType(AfipResult result) {
        try {
            driver.findElement(By.id("miContextoForm"));

            //constacia de inscripcion
            String constaciaTipo = "";
            try {
                if (driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr/td[2]/font[2]")) != null) {
                    constaciaTipo = "INSCRIPCION";
                    result.setTipo("CONSTANCIA DE INSCRIPCION");
                }
            } catch (Exception ep) {
                //al no encontrarlo evaluar constacia de opcion
                try {
                    if (driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[3]/td/b[1]/font")) != null) {
                        constaciaTipo = "OPCION";
                        result.setTipo("CONSTANCIA DE OPCION");
                    }
                } catch (Exception ep2) {
                }
            }

            //parte1 !no se por que pero sin este comentario da error el xpath
            String cuit = constaciaTipo.contentEquals("INSCRIPCION")
                    ? driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[2]/td/table/tbody/tr[1]/td/b/i/font")).getText()
                    : driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[1]/td/font")).getText();
            String nombre = constaciaTipo.contentEquals("INSCRIPCION")
                    ? driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[2]/td/table/tbody/tr[2]/td/b/i/font")).getText()
                    : driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[2]/td/font")).getText();

//            result.set(cuit);
            result.setFullName(nombre);

            if (constaciaTipo.contentEquals("INSCRIPCION")) {
                try {
                    //String arrayRegimenes[] = null;
                    JSONArray jsonArray = new JSONArray();
                    String campo_irg_name = null;
                    String campo_irg_date = null;
                    int i = 1;
                    while (driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[" + i + "]/td[1]/font")).getText() != null &&
                            !lineAsterisks.equals(campo_irg_name)) {
                        JSONObject obj = new JSONObject();
                        campo_irg_name = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[" + i + "]/td[1]/font")).getText();
                        campo_irg_date = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[" + i + "]/td[2]/font")).getText();

                        if (!lineAsterisks.equals(campo_irg_name)) {
                            obj.put("ir_name", campo_irg_name);
                            obj.put("ir_date", campo_irg_date);
                            jsonArray.put(obj);
                        }
                        i++;
                    }
                    result.setIrgJSONArray(jsonArray);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                //SECCION:IMPUESTO/REGIMEN REGISTRADO Y FECHA DE ALTA

                String campo_irg1_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[1]/td[1]/font")).getText();
                System.out.println("campo_irg1_key = " + campo_irg1_key);

                // /html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[1]/td[2]/font
                String campo_irg1_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[5]/td/table/tbody/tr[1]/td[3]/i/font")).getText();

//                result.setCampo_irg1_key(campo_irg1_key);
                campo_irg1_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[1]/td[1]/font")).getText();

                String campo_irg1 = campo_irg1_key +
                        " (" + driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[1]/td[2]/font")).getText() + ")";

                if (!lineAsterisks.equals(campo_irg1_key)) {
                    result.setIrg1(campo_irg1);
                }


                String campo_irg2_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[2]/td[1]/font")).getText();
                String campo_irg2_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]/font")).getText();

//                result.setCampo_irg2_key(campo_irg2_key);
                if (!lineAsterisks.equals(campo_irg2_key))
                    result.setIrg2(campo_irg2_key + " (" + campo_irg2_value + ")");

                String campo_irg3_key;
                try {
                    campo_irg3_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[3]/td[1]/font")).getText();
                } catch (Exception ep) {
                    campo_irg3_key = "";
                }

                String campo_irg3_value;
                try {
                    campo_irg3_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[3]/td[2]/font")).getText();
                } catch (Exception ep) {
                    campo_irg3_value = "";
                }

//                result.setCampo_irg3_key(campo_irg3_key);
                if (!lineAsterisks.equals(campo_irg3_key))
                    result.setIrg3(campo_irg3_key + " (" + campo_irg3_value + ")");

                String campo_irg4_key;

                try {
                    campo_irg4_key = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[4]/td[1]/font")).getText();
                } catch (Exception ep) {
                    campo_irg4_key = "";
                }

                String campo_irg4_value;

                try {
                    campo_irg4_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[4]/td[2]/font")).getText();
                } catch (Exception ep) {
                    campo_irg4_value = "";
                }

//                result.setCampo_irg4_key(campo_irg4_key);
                if (!lineAsterisks.equals(campo_irg4_key))
                    result.setIrg4(campo_irg4_key + " (" + campo_irg4_value + ")");

                String campo_irg5;
                try {
                    campo_irg5 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[6]/td/font")).getText();
                } catch (Exception ep) {
                    campo_irg5 = "";
                }
                result.setIrg5(campo_irg5);
                */

                //SECCION:Domicilio Fiscal

                Integer row = 6;
                try {
                    String title = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[5]/td/b/font")).getText();
                    if (title != null && title.equals("Domicilio Fiscal")) {
                        row = 5;
                    }
                } catch (Exception ep) {
                }

                String campo_df1 = "";
                try {
                    campo_df1 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[" + row + "]/td/table/tbody/tr[1]/td/font")).getText();
                } catch (Exception ep) {
                    campo_df1 = "";
                }

                WebElement webElement;
                String campo_df2 = "";
                try {
                    webElement = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[" + row + "]/td/table/tbody/tr[2]/td/font"));
                    if (webElement != null) {
                        campo_df2 = webElement.getText();
                    }
                } catch (Exception ep) {
                    campo_df2 = "";
                }


                result.setDf1(campo_df1);
                result.setDf2(campo_df2);
                //en algunos casos existe
                String campo_df3 = "";
                try {
                    webElement = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[" + row + "]/td/table/tbody/tr[3]/td/font"));
                    if (webElement != null) {
                        campo_df3 = webElement.getText();
                    }

                } catch (Exception ep) {
                    campo_df3 = "";
                }
                result.setDf3(campo_df3);

                //SECCION:Dependencia donde se encuentra inscripto
                String campo_di1 = "";
                try {
                    campo_di1 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[7]/td/table/tbody/tr[1]/td/font")).getText();
                } catch (Exception ep) {
                }


                String campo_di2 = "";
                try {
                    campo_di2 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[7]/td/table/tbody/tr[2]/td/font")).getText();
                } catch (Exception ep) {
                    try {
                        webElement = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[" + row + "]/td/table/tbody/tr[2]/td/font"));
                        if (webElement != null) {
                            campo_di2 = webElement.getText();
                        }

                    } catch (Exception ep2) {
                        campo_di2 = "";
                    }
                }
                String campo_di3;
                try {
                    campo_di3 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[" + row + "]/td/table/tbody/tr[1]/td/font")).getText();
                } catch (Exception ep) {
                    try {
                        campo_di3 = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[7]/td/table/tbody/tr[3]/td/font")).getText();
                    } catch (Exception ep2) {
                        campo_di3 = "";
                    }
                }

                result.setDi1(campo_di1);
                result.setDi2(campo_di2);
                result.setDi3(campo_di3);

            } else {
                String control_campo1_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[3]/td/font")).getText();
                String control_campo2_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[4]/td/font")).getText();
                String control_campo3_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[5]/td/font")).getText();

                String control_campo4_value;
                try {
                    control_campo4_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[6]/td/font")).getText();
                } catch (Exception ep) {
                    try {
                        control_campo4_value = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[7]/td/table/tbody/tr[5]/td/font")).getText();
                    } catch (Exception ep2) {
                        control_campo4_value = "";
                    }
                }

                String control_campo_categoria = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[10]/td/table/tbody/tr/td[2]/table/tbody/tr/td/font[2]")).getText();
                String control_campo_actividad = driver.findElement(By.xpath("/html/body/table[2]/tbody/tr[1]/td[2]/table/tbody/tr[11]/td/font")).getText();

                result.setControl1(control_campo1_value);
                result.setControl2(control_campo2_value);
                result.setControl3(control_campo3_value);
                result.setControl4(control_campo4_value);
                result.setControlCategoria(control_campo_categoria);
                result.setControlActividad(control_campo_actividad);
            }

        } catch (Exception e) {
            //Si tras 5to intento el elemento no resuelve lo que necesitamos devolvemos null
            System.out.println("[AfipScrapper] No obtuvimos resultados.");
        }
    }
}