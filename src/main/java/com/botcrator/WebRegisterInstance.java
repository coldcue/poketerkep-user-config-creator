package com.botcrator;

import com.DeathByCaptcha.Captcha;
import com.botcrator.exception.CaptchaLoadFailException;
import com.botcrator.exception.EmailHasNotArrivedException;
import com.botcrator.exception.UserNameTakenException;
import com.botcrator.exception.WrongCaptchaException;
import com.botcrator.stage.*;
import com.botcrator.support.CaptchaDecoder;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class WebRegisterInstance extends Thread {
    private static final int maxEmailResend = 5;
    private final Logger logger = Logger.getLogger(WebRegisterInstance.class.getSimpleName() + " " + this.getId());

    private final CaptchaDecoder captchaDecoder = new CaptchaDecoder();
    private final int proxyPort;
    private WebDriver webDriver;
    private FirefoxProfile profile;
    private Captcha captcha;
    private String username;
    private Stage currentStage;
    private Exception lastException;

    private boolean success = false;
    private boolean webDriverInitialized = false;

    public WebRegisterInstance(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void initWebDriver() {
        if (webDriverInitialized) return;

        logger.info("Starting firefox...");
        Proxy proxy = new Proxy().setSocksProxy("localhost:" + proxyPort);

        if (Main.firefoxProfile != null) {
            profile = new FirefoxProfile(Main.firefoxProfile);
        } else {
            profile = new FirefoxProfile();
        }

        // Disable images
        //profile.setPreference("permissions.default.image", 2);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        //desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);

        webDriver = new FirefoxDriver(new FirefoxBinary(), profile, desiredCapabilities);
        webDriver.manage().deleteAllCookies();
        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

        webDriverInitialized = true;
    }

    @Override
    public void run() {
        try {
            //Max 20 cycles
            for (int i = 0; i < 20; i++) {
                try {

                    //initWebDriver
                    initWebDriver();

                    //Verify Age
                    if (currentStage == null) {
                        currentStage = new VerifyAgeStage(this);
                        currentStage.run();
                    }

                    //Create account
                    if (currentStage.getClass() == VerifyAgeStage.class) {
                        currentStage = new CreateAccountStage(this);
                        currentStage.run();
                    }

                    //Solve captcha
                    if (currentStage.getClass() == CreateAccountStage.class) {
                        currentStage = new SolveCaptchaStage(this);
                        currentStage.run();
                    }

                    //Verify email
                    if (currentStage.getClass() == SolveCaptchaStage.class) {
                        currentStage = new VerifyEmailStage(this);
                        currentStage.run();
                    }

                    //Accept ToS
                    if (currentStage.getClass() == VerifyEmailStage.class) {
                        currentStage = new AcceptTosStage(this);
                        currentStage.run();
                    }

                    //Save user
                    if (currentStage.getClass() == AcceptTosStage.class) {
                        currentStage = new SaveUserStage(this);
                        currentStage.run();

                        success = true;
                    }


                    if (success) break;

                } catch (EmailHasNotArrivedException | CaptchaLoadFailException | UserNameTakenException | WrongCaptchaException e) {
                    logger.warning(e.getMessage());
                    lastException = e;
                }
            }

        } catch (Exception e) {
            close();
            logger.severe(e.getMessage());
            lastException = e;
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public Captcha getCaptcha() {
        return captcha;
    }

    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }


    public CaptchaDecoder getCaptchaDecoder() {
        return captchaDecoder;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isSuccess() {
        return success;
    }

    public Exception getLastException() {
        return lastException;
    }

    public int getProxyPort() {
        return proxyPort;
    }


    public void close() {
        if (webDriver != null) {
            webDriver.close();
            try {
                //Delete temp profile directory
                Field binaryField = FirefoxDriver.class.getDeclaredField("binary");
                binaryField.setAccessible(true);
                FirefoxBinary binary = (FirefoxBinary) binaryField.get(webDriver);
                File xre_profile_path = new File(binary.getExtraEnv().get("XRE_PROFILE_PATH"));
                FileUtils.deleteDirectory(xre_profile_path);
            } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
}
