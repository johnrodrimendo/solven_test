package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.PhoneContractOperator;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.LineaResult;
import com.affirm.common.util.JsonUtil;
import com.affirm.jobs.model.Linea;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by john on 29/09/16.
 */
public class LineaScrapper extends Scrapper {

    private final static String CLARO_URL = "http://aplicaciones.claro.com.pe/ClienteLineasWeb/";

    private final static String MOVISTAR_URL = "http://www.movistar.com.pe/conoce-tus-numeros-moviles";
    private final static String MOVISTAR_RECAPTCHA_SITE_KEY = "6LeJaRUUAAAAAA2iQkfdLPkK-vJ2mBqs8j_XA2-t";

    private final static String BITEL_URL = "https://bitel.com.pe/asistencia/consulta-linea/consultar";

    private final static String ENTEL_URL = "https://mi.entel.pe/portal/server.pt?&uuID={8650ACC7-3803-625E-B1C8-86A46A111000}&parentname=CommunityPage&parentid=113&mode=2&in_hi_userid=200&cached=true";
    private final static String ENTEL_RECAPTCHA_SITE_KEY = "6Ld8Hh8UAAAAALKHSClZ8UHPRtPpdMBJ28c6EJpP";

    public LineaScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public LineaScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public LineaResult getDataClaro(int docType, String docNumber) {

        int tries = 1;
        File imageCaptcha;
        Captcha solved = null;
        WebElement captcha;

        LineaResult result = new LineaResult();

        try {
            openSite(CLARO_URL);

            Select selectTipoDocumento = new Select(driver.findElement(By.id("iddoc")));
            switch (docType) {
                case 1: // DNI
                    selectTipoDocumento.selectByValue("DNI");
                    break;
                case 2: // CE
                    selectTipoDocumento.selectByValue("Carnet");
                    break;
                case 3: // RUC
                    selectTipoDocumento.selectByValue("RUC");
                    break;
            }
            driver.findElement(By.id("numdoc")).sendKeys(docNumber);

            do {
                if (tries >= 5) {
                    System.out.println("Se hicieron 4 intentos - Claro");
                    return null;
                } else if (tries > 1) {
                    System.out.println("falló el Captcha - Claro");
                    //screenshot();
                    reportCaptcha(solved);
                }

                captcha = driver.findElement(By.xpath("//img[contains(@src,'/ClienteLineasWeb/Stoken')]"));
                imageCaptcha = shootWebElement(captcha);

                solved = solveCaptcha(imageCaptcha);
                driver.findElement(By.id("captcha")).clear();
                driver.findElement(By.id("captcha")).sendKeys((solved.text.toLowerCase()));
                driver.findElement(By.xpath("//input[@type='submit' and @class='btn btn-turquesa btn-xlarge']")).click();
                tries++;
            } while (driver.findElements(By.id("errorcaja")).size() > 0);

            if (driver.findElements(By.id("showme")).size() > 0) {

                driver.findElement(By.id("showme")).click();

                WebElement table = driver.findElement(By.id("hello-table")).findElement(By.className("body-table")).findElement(By.tagName("table"));

                List<Linea> lineas = new ArrayList<Linea>();
                Linea l;
                for (WebElement e : table.findElements(By.tagName("tr"))) {
                    System.out.println(e.findElements(By.tagName("td")).get(0).getText().trim());
                    System.out.println(e.findElements(By.tagName("td")).get(1).getText().trim());

                    l = new Linea(e.findElements(By.tagName("td")).get(0).getText().trim(), e.findElements(By.tagName("td")).get(1).getText().trim());
                    lineas.add(l);
                }

                String js_lineas = (new Gson()).toJson(lineas);

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.CLARO);
                result.setLineas(js_lineas);

                System.out.println("lineas Claro " + js_lineas.toString());

                return result;

            } else {
                System.out.println("NO tiene lineas - Claro");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LineaResult getDataMovistar(int docType, String docNumber) {
        int tries = 1;
        Captcha solved = null;

        LineaResult result = new LineaResult();

        Boolean ceroLines;
        try {
            openSite(MOVISTAR_URL);

            do {
                if (tries >= 5) {
                    System.out.println("Se hicieron 4 intentos - Movistar");
                    return null;
                } else if (tries > 1) {
                    System.out.println("falló el Captcha - Movistar");
                    //screenshot();
                    //DONT REPORT IT BECAUSE LOOKS LIKE SOMETIMES THE CAPTCHA IS CORRECT BUT MOVISTAR SAYS IT IS WRONG
                    reportCaptcha(solved);
                }
                Thread.sleep(1000);

                ((JavascriptExecutor) driver).executeScript("document.querySelector('#_consultmobilenumbers_WAR_consultmobilenumbersportlet_documentType').style.display = 'block'");
                Select selectTipoDocumento = new Select(driver.findElement(By.id("_consultmobilenumbers_WAR_consultmobilenumbersportlet_documentType")));

                switch (docType) {
                    case 1:
                        selectTipoDocumento.selectByValue("DNI");
                        break;
                    case 2:
                        selectTipoDocumento.selectByValue("CEX");
                        break;
                    case 3:
                        selectTipoDocumento.selectByValue("RUC");
                        break;
                }

                driver.findElement(By.id("_consultmobilenumbers_WAR_consultmobilenumbersportlet_documentNumber")).sendKeys(docNumber);

                solved = solveCaptcha(MOVISTAR_RECAPTCHA_SITE_KEY, MOVISTAR_URL);

                driver.findElement(By.id("_consultmobilenumbers_WAR_consultmobilenumbersportlet_btnSubmit")).click();

                tries++;
                Thread.sleep(1000);

                Optional<List<WebElement>> errors = Optional.of(driver.findElements(By.className("alert-error")));
                ceroLines = errors.map(e -> {
                    if (e.size() > 0) {
                        System.out.println("*" + e.get(0).getText() + "*");
                        return e.get(0).getText().equals("No hay registros para el Filtro Seleccionado");
                    }
                    return false;
                }).orElse(false);
                if (ceroLines) {
                    System.out.println("No tiene lineas - Movistar");
                    return result;
                }
            } while (driver.findElements(By.className("alert-error")).size() > 0);

            if (driver.findElements(By.className("search_results")).size() > 0) {

                System.out.println("TIENE LINEAS - Movistar");
                WebElement table = driver.findElement(By.tagName("tbody"));

                List<Linea> lineas = new ArrayList<Linea>();
                Linea l;

                for (WebElement e : table.findElements(By.tagName("tr"))) {
                    System.out.println(e.findElements(By.tagName("td")).get(0).getText().trim());
                    System.out.println(e.findElements(By.tagName("td")).get(1).getText().trim());

                    l = new Linea(e.findElements(By.tagName("td")).get(1).getText().trim(), e.findElements(By.tagName("td")).get(0).getText().trim());
                    lineas.add(l);
                }

                String js_lineas = (new Gson()).toJson(lineas);

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.MOVISTAR);
                result.setLineas(js_lineas);

                System.out.println("lineas Movistar " + js_lineas);

                return result;

            } else {
                System.out.println("NO tiene lineas - Movistar");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LineaResult getDataBitel(int docType, String docNumber) {

        int tries = 1;

        LineaResult lineaResult = new LineaResult();

        ResponseEntity<String> response;

        try {
            do {
                if (tries >= 5) {
                    System.out.println("Se hicieron 4 intentos - Bitel");
                    return null;
                } else if (tries > 1) {
                    System.out.println("falló el WS - Bitel");
                }

                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
                map.add("documento", docNumber);
                map.add("tipo", String.valueOf(docType));

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

                response = restTemplate.postForEntity( BITEL_URL, request , String.class );

                JSONObject jsonObjectResponse = new JSONObject(response.getBody());

                boolean result = JsonUtil.getBooleanFromJson(jsonObjectResponse, "result", false);

                if (result) {
                    JSONObject datos = JsonUtil.getJsonObjectFromJson(jsonObjectResponse, "datos", null);

                    if (datos != null) {
                        JSONArray arrayLineas;

                        if (datos.get("lstwssubscriber") instanceof JSONObject){
                            arrayLineas = new JSONArray();
                            arrayLineas.put(datos.get("lstwssubscriber"));
                        } else {
                            arrayLineas = datos.getJSONArray("lstwssubscriber");
                        }

                        List<Linea> lineas = new ArrayList<Linea>();
                        Linea linea;

                        for (int i = 0; i < arrayLineas.length(); i++) {
                            JSONObject jsonObject = arrayLineas.getJSONObject(i);

                            linea = new Linea(JsonUtil.getStringFromJson(jsonObject, "isdn", null),
                                    JsonUtil.getStringFromJson(jsonObject, "servicetype", null));

                            lineas.add(linea);
                        }

                        String js_lineas = (new Gson()).toJson(lineas);

                        lineaResult.setCantidad(arrayLineas.length());
                        lineaResult.setOperador(PhoneContractOperator.BITEL);
                        lineaResult.setLineas(js_lineas);

                        System.out.println("lineas Bitel " + js_lineas);

                        return lineaResult;
                    }
                } else {
                    System.out.println("NO tiene lineas - Bitel");
                    return lineaResult;
                }

                tries++;
            } while (response == null);

            System.out.println("No hubo respuesta del WS - Bitel");
            return lineaResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LineaResult getDataEntel(int docType, String docNumber) {

        int tries = 1;
        Captcha solved = null;

        LineaResult result = new LineaResult();

        try {
            openSite(ENTEL_URL);

            do {
                if (tries >= 5) {
                    System.out.println("Se hicieron 4 intentos - Entel");
                    return null;
                } else if (tries > 1) {
                    System.out.println("falló el Captcha - Entel");
                    driver.navigate().refresh();
                    //DONT REPORT IT BECAUSE LOOKS LIKE SOMETIMES THE CAPTCHA IS CORRECT BUT MOVISTAR SAYS IT IS WRONG
                    reportCaptcha(solved);
                }

                Select selectTipoDocumento = new Select(driver.findElement(By.id("frmConsultaNumerosUsuario:cmbDocType")));
                switch (docType) {
                    case IdentityDocumentType.DNI:
                        selectTipoDocumento.selectByValue("1");
                        break;
                    case IdentityDocumentType.CE:
                        selectTipoDocumento.selectByValue("3");
                        break;
                }
                driver.findElement(By.id("frmConsultaNumerosUsuario:txtNumeroDocumento")).sendKeys(docNumber);

                solved = solveCaptcha(ENTEL_RECAPTCHA_SITE_KEY, ENTEL_URL);

                System.out.println("Consegui bien el captcha - Entel");

                driver.findElement(By.id("frmConsultaNumerosUsuario:btnOk")).click();

                try {
                    WebDriverWait wait = new WebDriverWait(driver, 10);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("frmConsultaNumerosUsuarioResultado")));
                } catch (Exception ex) {
                    System.out.println("Timeout frmConsultaNumerosUsuarioResultado " + ex.getMessage());
                }

                tries++;
            } while (driver.findElements(By.id("frmConsultaNumerosUsuario:btnOk")).size() > 0);

            if (driver.findElements(By.id("frmConsultaNumerosUsuarioResultado:outputMensajeNoEn")).size() > 0) {

                System.out.println("NO tiene lineas - Entel");
                return result;

            } else {

                System.out.println("TIENE LINEAS - Entel");
                WebElement table = driver.findElement(By.id("frmConsultaNumerosUsuarioResultado:dataTable1"));
                table = table.findElement(By.tagName("tbody"));

                List<Linea> lineas = new ArrayList<Linea>();
                Linea l;

                for (WebElement e : table.findElements(By.tagName("tr"))) {
                    System.out.println(e.findElements(By.tagName("td")).get(0).getText().trim());
                    System.out.println(e.findElements(By.tagName("td")).get(1).getText().trim());

                    l = new Linea(e.findElements(By.tagName("td")).get(0).getText().trim(), e.findElements(By.tagName("td")).get(1).getText().trim());
                    lineas.add(l);
                }

                String js_lineas = (new Gson()).toJson(lineas);

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.ENTEL);
                result.setLineas(js_lineas);

                System.out.println("lineas Entel " + js_lineas);

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LineaResult getData(int docType, String docNumber, String operator) {
        if (operator.equals(PhoneContractOperator.CLARO)) {
            return getDataClaro(docType, docNumber);
        } else if (operator.equals(PhoneContractOperator.MOVISTAR)) {
            return getDataMovistar(docType, docNumber);
        } else if (operator.equals(PhoneContractOperator.BITEL)) {
            return getDataBitel(docType, docNumber);
        } else if (operator.equals(PhoneContractOperator.ENTEL)) {
            return getDataEntel(docType, docNumber);
        }
        return null;
    }

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
        long ini = new Date().getTime();
        LineaScrapper scrapper = new LineaScrapper("fturconi", "ostk2004", 40);
        LineaResult lineaResult = null;
//        lineaResult = scrapper.getDataClaro(1, "17522461");
//        lineaResult = scrapper.getDataMovistar(1, "46226075");
//        lineaResult = scrapper.getDataEntel(1, "73193839");
        lineaResult = scrapper.getDataBitel(1, "73193839");

        scrapper.close();
        System.out.println("Se demoró el Scrapper: " + (new Date().getTime() - ini + " milis"));

        if (lineaResult != null) {
            System.out.println("cantidad: " + lineaResult.getCantidad());
            System.out.println("lineas: " + lineaResult.getLineas());
        } else {
            System.out.println("no se encontro resultados");
        }
    }
}
