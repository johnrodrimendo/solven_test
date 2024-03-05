package com.affirm.jobs.webscrapper;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

/**
 * Created by john on 29/09/16.
 */
public class Scrapper implements AutoCloseable {
    // discussion
    // https://news.ycombinator.com/item?id=11839303
    // option1 run chrome driver in headless selenium
    // http://stackoverflow.com/questions/40536516/cant-start-chrome-driver-via-selenium-in-headless-mode
    // option2 run nightmarejs stop using selenium
    // http://www.nightmarejs.org/
    public static String SHOT_LOCK = "SHOT_LOCK";

    protected int timeout;
    protected Client client;
    protected Client clientRecaptcha;
    protected RemoteWebDriver driver;
    protected boolean localChromeDriver = false;
    private boolean log = false;
    private com.affirm.common.model.catalog.Proxy proxy;

    private final static Logger LOGGER = Logger.getLogger(Scrapper.class.getName());


    protected Scrapper(String user, String password, int timeout) {
        try {
            client = new SocketClient(user, password);
            clientRecaptcha = new HttpClient(user, password);
            this.timeout = timeout;
            setupDriver();
        } catch (Throwable ex) {
            LOGGER.warning("Error creando el scrapper");
            ex.printStackTrace();
        }
    }

    protected Scrapper(String user, String password, int timeout, com.affirm.common.model.catalog.Proxy proxy) {
        try {
            client = new SocketClient(user, password);
            clientRecaptcha = new HttpClient(user, password);
            this.timeout = timeout;
            setProxy(proxy);
            setupDriver();
        } catch (Throwable ex) {
            LOGGER.warning("Error creando el scrapper");
            ex.printStackTrace();
        }
    }

    private void setupDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);

        ArrayList<String> cliArgsCap = new ArrayList<String>();

        if(this.proxy != null) {
            cliArgsCap.add("--proxy=" + this.proxy.getIp() + ":" + this.proxy.getPort());

            if(proxy.getId() < 300) {
                cliArgsCap.add("--proxy-type=" + this.proxy.getProxyType());
            }
            else {
                if(proxy.getCountryId() == CountryParam.COUNTRY_PERU){
                    cliArgsCap.add("--proxy-auth=lum-customer-hl_6ccd12b2-zone-zone_peru:8qjcu7jznhny");
                }else if(proxy.getCountryId() == CountryParam.COUNTRY_ARGENTINA){
                    cliArgsCap.add("--proxy-auth=lum-customer-hl_6ccd12b2-zone-testzone:ijvmuewpfkfn");
                }
            }
//            cliArgsCap.add("--proxy=127.0.0.1:9050");

//            cliArgsCap.add("--proxy-type=http");
        }

        if (Configuration.hostEnvIsNotLocal() || !localChromeDriver) {
            cliArgsCap.add("--ignore-ssl-errors=true");
            cliArgsCap.add("--web-security=false");
            cliArgsCap.add("--webdriver-loglevel=NONE");
        }

        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");

        if (Configuration.hostEnvIsNotLocal()) {
            capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/app/vendor/phantomjs/bin/phantomjs");
            driver = new PhantomJSDriver(capabilities);//PRD
        } else {
            if (localChromeDriver) {
                System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER_PATH"));
                driver = new ChromeDriver(capabilities);//LOCAL
            } else {
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, System.getenv("PHANTOMJS_DRIVER_PATH"));
                driver = new PhantomJSDriver(capabilities);//LOCAL
            }
        }
    }

    protected void openSite(String page) {
        LOGGER.info("Loading URL : "+page);
        driver.manage().window().setSize(new Dimension(1280, 1024));
        driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS);
        waitForLoad(driver, page);
    }

    protected void waitForLoad(WebDriver driver, String page) throws TimeoutException {
        long ini = new Date().getTime();
        driver.get(page);
        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(pageLoadCondition);
        LOGGER.info("URL loaded in : " + (new Date().getTime() - ini + " milis"));
    }

    protected void waitForLoad(WebDriver driver) throws TimeoutException {
        long ini = new Date().getTime();
        ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(pageLoadCondition);
    }

    protected boolean reportCaptcha(Captcha captcha) {
        try {
            if (client.report(captcha)) {
                LOGGER.info("CAPTCHA reported as incorrectly solved");
                return true;
            } else {
                LOGGER.info("Failed reporting incorrectly solved CAPTCHA");
                return false;
            }
        } catch (IOException | com.DeathByCaptcha.Exception ex) {
            LOGGER.warning("IOException | DeathByCAptcha Ex thrown RECAPTCHA" );
            ex.printStackTrace();

        }
        return false;
    }

    protected boolean reportReCaptcha(Captcha captcha) {
        try {
            if (clientRecaptcha.report(captcha)) {
                LOGGER.info("RECAPTCHA reported as incorrectly solved");
                return true;
            } else {
                LOGGER.info("Failed reporting incorrectly solved RECAPTCHA");
                return false;
            }
        } catch (IOException | com.DeathByCaptcha.Exception ex) {
            LOGGER.warning("IOException | DeathByCAptcha Ex thrown CAPTCHA" );
            ex.printStackTrace();
        }
        return false;
    }

    protected Captcha solveReCaptcha(String siteKey, String url, int countryId) throws Exception{
        Captcha captcha = null;
        long startTime = System.currentTimeMillis();

        try {
            // Upload a reCAPTCHA and poll for its status with 120 seconds timeout.
            // Put your proxy, proxy type, page googlekey, page url and solving timeout (in seconds)
            // 0 or nothing for the default timeout value.
            if (CountryParam.COUNTRY_ARGENTINA == countryId && proxy != null) {
                captcha = clientRecaptcha.decode(String.format("http://%s:%s", proxy.getIp(), proxy.getPort()));
//                captcha = clientRecaptcha.decode("http://200.61.176.241:8080", "http", siteKey, url);
            } else {
                captcha = clientRecaptcha.decode(null, "http", siteKey, url);
                long endTime = System.currentTimeMillis();
                LOGGER.info("RECAPTCHA ran in " + (endTime - startTime)/1000 + " seconds");
            }

            if (captcha != null && captcha.isSolved()) {
                LOGGER.info("RECAPTCHA solved: " + captcha.text);
                return captcha;
            } else {
                LOGGER.info("Failed solving RECAPTCHA");
            }
        } catch (SocketTimeoutException ex2) {
            LOGGER.warning("Ex thrown - Timeout - RECAPTCHA failed in " + (System.currentTimeMillis()- startTime)/1000 + " seconds" );
            ex2.printStackTrace();

        } catch (IOException | com.DeathByCaptcha.Exception ex) {
            LOGGER.warning("RECAPTCHA failed in " + (System.currentTimeMillis()- startTime)/1000 + " seconds");
            ex.printStackTrace();

        }

        return captcha;
    }

    protected Captcha solveCaptcha(File imageCaptcha) {
        Captcha captcha = null;
        try {
            captcha = client.upload(imageCaptcha);

            if (null != captcha) {
                LOGGER.info("CAPTCHA " + imageCaptcha.getName() + " uploaded: " + captcha.id);

                // Poll for the uploaded CAPTCHA status.
                while (captcha.isUploaded() && !captcha.isSolved()) {
//                    Thread.sleep(Client.POLLS_INTERVAL * 500);
                    LOGGER.info("Request solved CAPTCHA");
                    captcha = this.client.getCaptcha(captcha);
                }

                if (captcha.isSolved()) {
                    LOGGER.info("CAPTCHA " + imageCaptcha.getName() + " solved: " + captcha.text);
                    return captcha;

                } else {
                    LOGGER.info("Failed solving CAPTCHA");
                }
            } else {
                LOGGER.info("CAPTCHA is NULL");
            }
        } catch (IOException | com.DeathByCaptcha.Exception ex) {
            LOGGER.warning("IOException | DeathByCaptchaExc thrown at solving CAPTCHA");
            ex.printStackTrace();
        }
        return null;
    }

    protected File shootWebElement(WebElement element) throws IOException {
        synchronized (SHOT_LOCK) {
            File screen = ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
            Point p = element.getLocation();
            int width = element.getSize().getWidth();
            int height = element.getSize().getHeight();
            BufferedImage img = ImageIO.read(screen);
            BufferedImage dest = img.getSubimage(p.getX(), p.getY(), width, height);
            ImageIO.write(dest, "png", screen);

            if (Configuration.hostEnvIsLocal()) {
                LOGGER.info("saving WebElement SCREENSHOT to file");
                File f = new File("captcha.png");
                FileUtils.copyFile(screen, f);
            }
            return screen;
        }
    }

    /*protected File shootWebElement(WebElement element) throws IOException {
        System.out.println("shootWebElement");
        synchronized (SHOT_LOCK) {
            File f = File.createTempFile("captcha_", ".tmp");
            ImageIO.write(new AShot().takeScreenshot(driver, element).getImage(), "PNG", f);
            System.out.println("SHOOTED");
            return f;
        }
    }*/

    protected File screenshot() throws IOException {

        File screen = ((TakesScreenshot) this.driver).getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(screen);
        ImageIO.write(img, "png", screen);

        if (Configuration.hostEnvIsLocal()) {
            LOGGER.info("saving SCREENSHOT to file");
            File f = new File(UUID.randomUUID().toString()+".png");
            FileUtils.copyFile(screen, f);
            LOGGER.info("saving SCREENSHOT in"+f.getAbsolutePath()+f.getName());

        }

        return screen;
    }

    @Override
    public void close() {
        if (driver != null)
            driver.quit();
    }

    public static void main(String[] args) throws Exception {

    }

    public boolean tryAgainCaptcha(int[] currentTry, int maxTry, Captcha solved, boolean report) throws IOException {
        if (currentTry[0] > maxTry) {
            LOGGER.info("Se hicieron " + (currentTry[0] - 1) + " intentos.");
            return false;
        } else if (currentTry[0] > 1) {
            LOGGER.info("fallo el CAPTCHA");
            screenshot();
            if (report) {
                reportCaptcha(solved);
            }
        }
        return true;
    }

    public void switchTo(WebDriver driver, String title) {
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            if (driver.getTitle().equals(title)) {
                return;
            }
        }
        throw new IllegalArgumentException("Unable to find window with that title");
    }

    public void waitForId(WebDriver driver, String id) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(presenceOfElementLocated(By.id(id)));
    }

    @Override
    public String toString() {
        return "Scrapper{" +
                "timeout=" + timeout +
                ", client=" + client +
                ", driver=" + driver +
                ", localChromeDriver=" + localChromeDriver +
                ", log=" + log +
                '}';
    }

    protected WebElement getWebElementById(WebDriver driver, String idElement) {
        WebElement w = null;
        try {
            w = driver.findElement(By.id(idElement));
        } catch (Exception ep) {
        }
        return w;
    }
    public com.affirm.common.model.catalog.Proxy getProxy() {
        return proxy;
    }
    public void setProxy(com.affirm.common.model.catalog.Proxy proxy) {
        this.proxy = proxy;
    }
}
