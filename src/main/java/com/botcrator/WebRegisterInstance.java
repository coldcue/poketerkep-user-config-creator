package com.botcrator;

import com.DeathByCaptcha.Captcha;
import com.botcrator.exception.*;
import com.botcrator.stage.*;
import com.botcrator.support.CaptchaDecoder;
import com.botcrator.support.SettingsWriter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class WebRegisterInstance extends Thread {
    public static final String PASSWORD = "son0fabietch";
    private static final int maxEmailResend = 5;
    private final Logger logger = Logger.getLogger(WebRegisterInstance.class.getSimpleName() + " " + this.getId());

    private final CaptchaDecoder captchaDecoder = new CaptchaDecoder();
    private final int proxyPort;
    private WebDriver webDriver;
    private FirefoxProfile profile;
    private Captcha captcha;
    private String username;
    private String token;
    private String ip;
    private Stage currentStage;
    private Exception lastException;
    private VerifyEmailStage verifyEmailStage;

    private FacebookData facebookData;

    private int emailFailCount = 0;
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
        profile.setPreference("permissions.default.image", 2);
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);

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
                    //Get IP
                    if (currentStage == null) {
                        currentStage = new GetIPStage(this);
                        currentStage.run();
                    }

                    //Check block
                    if (currentStage.getClass() == GetIPStage.class) {
                        currentStage = new CheckBlockStage(this);
                        currentStage.run();
                    }

                    //Facebook data
                    if (currentStage.getClass() == CheckBlockStage.class) {
                        currentStage = new GetFacebookDataStage(this);
                        currentStage.run();
                    }

                    //initWebDriver
                    initWebDriver();

                    //Fill data
                    if (currentStage.getClass() == GetFacebookDataStage.class
                            || lastException.getClass() == CaptchaLoadFailException.class
                            || lastException.getClass() == UserNameTakenException.class
                            || lastException.getClass() == WrongCaptchaException.class) {
                        currentStage = new FillDataStage(this);
                        currentStage.run();
                    }

                    //Start verify e-mail
                    if (currentStage.getClass() == FillDataStage.class) {
                        if (verifyEmailStage != null)
                            verifyEmailStage.close();
                        verifyEmailStage = new VerifyEmailStage(this);
                    }

                    //Solve captcha
                    if (currentStage.getClass() == FillDataStage.class) {
                        currentStage = new SolveCaptchaStage(this);
                        currentStage.run();
                    }

                    //SignUp
                    if (currentStage.getClass() == SolveCaptchaStage.class) {
                        try {
                            currentStage = new SignUpStage(this);
                            currentStage.run();
                        } catch (UserNameTakenException e) {
                            facebookData = null;
                            throw e;
                        } catch (WrongCaptchaException e) {
                            //Report wrong captcha
                            if (captcha != null)
                                captchaDecoder.getClient().report(captcha);
                            throw e;
                        }
                    }

                    //Add profile content (best effort)
                    if (currentStage.getClass() == SignUpStage.class) {
                        currentStage = new AddProfileContentStage(this);
                        currentStage.run();
                    }

                    //Resend verification email
                    if (currentStage.getClass() == VerifyEmailStage.class && emailFailCount < maxEmailResend) {

                        if (verifyEmailStage != null)
                            verifyEmailStage.close();
                        verifyEmailStage = new VerifyEmailStage(this);

                        currentStage = new ResendEmailStage(this);
                        currentStage.run();
                    }

                    //Verify email
                    if (currentStage.getClass() == AddProfileContentStage.class
                            || (currentStage.getClass() == ResendEmailStage.class && emailFailCount < maxEmailResend)) {
                        currentStage = verifyEmailStage;
                        verifyEmailStage.run();
                    }

                    //Get token
                    if (currentStage.getClass() == VerifyEmailStage.class) {
                        currentStage = new GetTokenStage(this);
                        currentStage.run();
                    }

                    //Save user
                    if (currentStage.getClass() == GetTokenStage.class) {
                        logger.info("A new user have just created -> username: " + username + " token:" + token);

                        //Save every bot
                        PrintWriter printWriter = new PrintWriter(new FileWriter("users.xml", true), true);
                        printWriter.println("<user><name>" + username + "</name><token>" + token + "</token></user>");
                        printWriter.close();

                        SettingsWriter.writeUser(username, token);

                        success = true;
                    }

                    if (success) break;

                } catch (EmailHasNotArrivedException e) {
                    logger.warning(e.getMessage());
                    lastException = e;
                    emailFailCount++;
                } catch (CaptchaLoadFailException | UserNameTakenException | WrongCaptchaException e) {
                    logger.warning(e.getMessage());
                    lastException = e;
                }
            }

        } catch (TwitchBlockException | ConnectionResetException e) {
            close();
            logger.warning(e.getMessage());
            lastException = e;
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

    public void setToken(String token) {
        this.token = token;
    }

    public void checkBlock() throws TwitchBlockException, ConnectionResetException {
        //Check if blocked
        String pageSource = webDriver.getPageSource();
        boolean matches = Pattern.compile("^.*You have been blocked from using Twitch.*", Pattern.DOTALL).matcher(pageSource).matches();
        if (matches) {
            throw new TwitchBlockException();
        }

        matches = Pattern.compile("^.*Unable to forward this request at this time.*", Pattern.DOTALL).matcher(pageSource).matches();
        if (matches) {
            throw new ConnectionResetException();
        }
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public FacebookData getFacebookData() {
        return facebookData;
    }

    public void setFacebookData(FacebookData facebookData) {
        this.facebookData = facebookData;
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
