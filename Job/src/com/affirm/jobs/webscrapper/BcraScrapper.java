package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Proxy;
import com.affirm.common.model.transactional.BcraResult;
import com.affirm.common.model.transactional.BcraResult.DeudaBanco;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Romulo Galindo Tanta
 */
public class BcraScrapper extends Scrapper {
    //old one http://www.bcra.gov.ar/BCRAyVos/Situacion_Crediticia_CUIT_CUIL.asp
    private final static Logger LOGGER = Logger.getLogger(BcraScrapper.class.getName());

    private final static String BASE_URL = "http://www.bcra.gob.ar/BCRAyVos/";
    private final static String INITIAL_URL = BASE_URL + "Situacion_Crediticia.asp";
    private final static String EXPECTED_URL = BASE_URL + "Resultado_consulta_por_CUIT_CUIL_CDI.asp";
    private final static String RECAPTCHA_SITE_KEY = "6LdwS08UAAAAALS3Vi6zEITCELwuodHhOQLt8lVv";
    private final static int MAX_ATTEMTPS = 5;

    public final static int DOC_ARG_CUIT = 1;
    public final static int DOC_ARG_CUIL = 2;
    public final static int DOC_ARG_CDI = 3;

    private boolean existChequesTable;
    private boolean existDeudoresTable;
    private boolean existHistorialTable;

    public BcraScrapper(String user, String password, int timeout) {
        super(user, password, timeout);
    }

    public BcraScrapper(String user, String password, int timeout, Proxy proxy) {
        super(user, password, timeout, proxy);
    }

    public BcraResult getData(Integer docType, String docNumber) throws Exception {
        LOGGER.info("DOCNUMBER " + docNumber);
        BcraResult result = new BcraResult();
        WebDriverWait waiter = new WebDriverWait(driver, 60);
        existChequesTable = false;
        existDeudoresTable = false;
        existHistorialTable = false;

        try {
            openSite(INITIAL_URL);
            LOGGER.info("BCRA site loaded");
        } catch (TimeoutException e) {
            LOGGER.warning("BCRA site failed to load");
            e.printStackTrace();
            return null;
        }

        int attempt = 1;
        Captcha solved = null;

        do {
            if (attempt > MAX_ATTEMTPS) {
                LOGGER.info("Stopped at " + (MAX_ATTEMTPS) + " attempts ");
                return null;
            } else if (attempt > 1) {
                if (solved != null) {
                    reportReCaptcha(solved);
                }
            }

            try {
                WebElement cuit = waiter.until(ExpectedConditions.elementToBeClickable(By.name("CUIT")));
                ExpectedCondition<Boolean> expectedUrl = ExpectedConditions.urlMatches(EXPECTED_URL);
                cuit.clear();
//                cuit.sendKeys(docNumber);// SOMEHOW IM NOT ALLOWED TO WRITE FROM HERE
                ((JavascriptExecutor) driver).executeScript("document.querySelector('[name=CUIT]').value = '" + docNumber + "'");
                LOGGER.info("Attempt N " + attempt + " - Cracking RECAPTCHA - long process running");
                solved = solveReCaptcha(RECAPTCHA_SITE_KEY, INITIAL_URL, CountryParam.COUNTRY_ARGENTINA);

                if (solved != null) {
                    LOGGER.info("Performing form submission");
                    ((JavascriptExecutor) driver).executeScript(" document.getElementById('g-recaptcha-response').value = '" + solved.text + "'");
                    driver.findElement(By.name("form")).submit();
                    waiter.until(ExpectedConditions.urlToBe(EXPECTED_URL));
                    break;
                }

                if (!driver.getCurrentUrl().equals(INITIAL_URL))
                    driver.navigate().to(INITIAL_URL);

            } catch (Exception e) {
                LOGGER.warning("Exception thrown at BCRAscrapper");
                e.printStackTrace();
            } finally {
                attempt++;
            }
        } while (!driver.getCurrentUrl().equals(EXPECTED_URL));

        LOGGER.info("RECAPTCHA succeeded parsing & fetching user records");
        WebElement elementoPase = null;
        try {
            //espero a que el elemento exista
            elementoPase = new WebDriverWait(driver, this.timeout).until(ExpectedConditions.elementToBeClickable(By.id("aimprimir")));
        } catch (Exception ep) {
            elementoPase = null;
            LOGGER.warning("Exc thrown at finding web element");
            ep.printStackTrace();
        }

        if (elementoPase != null) {

            WebElement divResult = driver.findElement(By.id("aimprimir"));

            List<WebElement> tables = divResult.findElements(By.tagName("table"));
            boolean deudoresFound = false;

            for (WebElement table : tables) {
                if (table.getAttribute("class").contentEquals("table table-BCRA table-bordered table-responsive")) {

                    if (!deudoresFound) {
                        WebElement tableDeudores = table.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/table[1]"));
                        //para todos los deudores

                        List<WebElement> rows = tableDeudores.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
                        for (WebElement row : rows) {
                            List<WebElement> cells = row.findElements(By.tagName("td"));
                            String observations = "-";
                            if (cells.size() > 6) {
                                observations = cells.get(6).getText();
                            }

                            result.getDeudores().add(result.new Deudor(cells.get(0).getText(),
                                    cells.get(1).getText(),
                                    cells.get(2).getText(),
                                    cells.get(3).getText(),
                                    cells.get(4).getText(),
                                    cells.get(5).getText(),
                                    observations
                            ));
                        }
                        deudoresFound = true;
                        existChequesTable = true;
                    }


                } else if (table.getAttribute("class").contentEquals("table table-BCRA table-bordered table-hover table-responsive")) {
                    //para los cheques
                    List<WebElement> rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
                    for (WebElement row : rows) {
                        List<WebElement> cells = row.findElements(By.tagName("td"));
                        result.getCheques().add(result.new Cheque(
                                cells.get(0).getText(),
                                cells.get(1).getText(),
                                cells.get(2).getText(),
                                cells.get(3).getText(),
                                cells.get(4).getText(),
                                cells.get(5).getText(),
                                cells.get(6).getText(),
                                cells.get(7).getText()
                        ));
                    }
                    existDeudoresTable = true;
                } else if (table.getAttribute("class").contentEquals("table table-transparente table-responsive")) {
                    List<WebElement> rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));

                    // La primera fila tiene la fecha y la segunda tiene texto informativo.
                    // Hay 4 tablas con las clases de arriba pero solo la tabla que tiene 2 filas tiene la fecha de origen
                    // La fecha esta en formato MM/AAAA -> 10/2010
                    if (rows.size() == 2) {
                        String messageSituation = rows.get(0).findElement(By.tagName("td")).getText();
                        Integer indexDesde = messageSituation.indexOf("desde");

                        if (indexDesde > 0) {
                            result.setOriginDate(messageSituation.substring(indexDesde + 6, indexDesde + 14).trim());
                        }
                    }
                }
            }

            //elementos deudas a los bancos principal
            try {
                List<DeudaBanco> listaDeudaBanco = new ArrayList<>();

                List<WebElement> divAcordions = driver.findElements(By.id("accordion"));
                for (WebElement webElementBanco : divAcordions) {
                    String nombreBanco = webElementBanco.findElement(By.xpath("div/div/h5/a")).getText();
                    DeudaBanco deudaBanco = result.new DeudaBanco();

                    WebElement table = webElementBanco.findElement(By.tagName("table"));
                    List<WebElement> rows = table.findElements(By.tagName("tr"));
                    List<DeudaBanco.RegistroDeuda> registroDeudas = new ArrayList<>();

                    int i = 0;
                    for (WebElement row : rows) {
                        if (i > 0) {
                            List<WebElement> cells = row.findElements(By.tagName("td"));
                            String periodo = cells.get(0).getAttribute("innerHTML");
                            String situacion = cells.get(1).getAttribute("innerHTML");
                            String monto = cells.get(2).getAttribute("innerHTML");

                            registroDeudas.add(deudaBanco.new RegistroDeuda(periodo, situacion, monto));
                        }
                        i++;
                    }
                    deudaBanco.setNombre(nombreBanco);
                    deudaBanco.setHistorial(registroDeudas);
                    listaDeudaBanco.add(deudaBanco);
                }
                result.setHistorialDeudas(listaDeudaBanco);
                existHistorialTable = true;
                LOGGER.info(result.getHistorialDeudas().size() + " debt records found");
            } catch (Exception e) {
                LOGGER.warning("Exception thrown while retrieving bank debts");
                e.printStackTrace();
            }

            //validar si hay mensaje de alerta
//            try {
//                result.setAlerta(divResult.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/center/b")).getText());
//            } catch (Exception ep) {
//                LOGGER.info("ep = " + ep);
//            }

            return result;

        } else {
            //Si tras 5 intentos el elemento no es lo esperado retorna nulo
            LOGGER.info("[BcraScrapper][" + docNumber + "] No records found.");
            return null;

        }
    }

    public boolean isExistChequesTable() {
        return existChequesTable;
    }

    public boolean isExistDeudoresTable() {
        return existDeudoresTable;
    }

    public boolean isExistHistorialTable() {
        return existHistorialTable;
    }

    public static void main(String[] args) throws Exception {
        String[] docs = {"20231684213"};

        BcraScrapper scrapper = new BcraScrapper("fturconi", "ostk2004", 30);
        int success = 0;
        int fails = 0;

        try {
            for (String doc : docs) {
                BcraResult result = scrapper.getData(DOC_ARG_CDI, doc);

                if (result != null) {
                    System.out.println("deudores : " + new Gson().toJson(result.getDeudores()));
                    System.out.println("cheques : " + new Gson().toJson(result.getCheques()));
                    System.out.println("origin_date : " + result.getOriginDate());
                    System.out.println("historial deudas : " + new Gson().toJson(result.getHistorialDeudas()));

                    success++;
                } else {
                    System.out.println("no trajo resultados");

                    fails++;
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Exception thrown at main BCRA test");
            e.printStackTrace();
        } finally {
            scrapper.close();
            LOGGER.info("PASSED :" + success + " FAILED :" + fails);
            System.out.println("PASSED :" + success + " FAILED :" + fails);
        }
    }
}

