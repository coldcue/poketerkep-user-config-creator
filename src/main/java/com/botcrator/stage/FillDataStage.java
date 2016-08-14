package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.support.UserNameGenerator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FillDataStage extends StageImpl {
    private static final List<String> EMAIL_DOMAINS = Arrays.asList("guerrillamail.com", "guerrillamailblock.com", "sharklasers.com", "guerrillamail.net", "guerrillamail.org", "guerrillamail.biz", "spam4.me", "grr.la", "guerrillamail.de");

    private final WebDriver webDriver;
    private final Random random = new Random();
    private String userName;

    public FillDataStage(WebRegisterInstance wri) {
        super(wri);
        this.webDriver = wri.getWebDriver();
    }

    @Override
    public void run() throws Exception {
        //Open signup page
        logger.info("Opening signup page...");
        webDriver.get("http://www.twitch.tv/signup");

        //Check twitch block
        wri.checkBlock();


        if (wri.getFacebookData() == null) {
            //Generate username
            userName = new UserNameGenerator().generateUserName();
        } else {
            userName = wri.getFacebookData().getUsername();
        }

        wri.setUsername(userName);

        //Wait for the UI
        Thread.sleep(3000);

        webDriver.findElement(By.id("user_login")).sendKeys(userName);
        Thread.sleep(250);

        webDriver.findElement(By.id("user_password")).sendKeys(WebRegisterInstance.PASSWORD);
        Thread.sleep(250);

        //Click on a random month
        WebElement date_month = webDriver.findElement(By.id("date_month"));
        date_month.findElements(By.tagName("option")).get(random.nextInt(12) + 1).click();

        Thread.sleep(250);

        //Click on a random day
        WebElement date_day = webDriver.findElement(By.id("date_day"));
        date_day.findElements(By.tagName("option")).get(random.nextInt(27) + 1).click();

        Thread.sleep(250);

        //Click on a random year
        WebElement date_year = webDriver.findElement(By.id("date_year"));
        date_year.findElements(By.tagName("option")).get(random.nextInt(80) + 1).click();

        Thread.sleep(250);

        //Fill random email
        webDriver.findElement(By.id("user_email")).sendKeys(userName + '@' + EMAIL_DOMAINS.get(random.nextInt(EMAIL_DOMAINS.size() - 1)));

        Thread.sleep(250);

        webDriver.findElement(By.id("user_password")).sendKeys("3");

        Thread.sleep(250);

        Actions clicker = new Actions(webDriver);
        clicker.moveByOffset(500, 500).click().perform();

        Thread.sleep(250);

        clicker = new Actions(webDriver);
        clicker.moveByOffset(400, 500).click().perform();

        Thread.sleep(250);
    }
}
