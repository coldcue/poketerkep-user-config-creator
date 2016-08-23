package com.botcreator.stage;

import com.botcreator.WebRegisterInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.Random;

public class VerifyAgeStage extends StageImpl {
    private final WebDriver webDriver;
    private final Random random;

    public VerifyAgeStage(WebRegisterInstance wri) {
        super(wri);
        this.webDriver = wri.getWebDriver();
        this.random = new Random();
    }

    @Override
    public void run() throws Exception {
        //Open signup page
        logger.info("Opening signup page...");
        webDriver.get("https://club.pokemon.com/us/pokemon-trainer-club/sign-up/");

        //Wait for the UI
        Thread.sleep(250);

        WebElement verifyAgeForm = webDriver.findElement(By.cssSelector("form[name=\"verify-age\"]"));

        {
            int year = 1970 + random.nextInt(20);
            int month = 1 + random.nextInt(11);
            int day = 1 + random.nextInt(26);

            String date = String.valueOf(year) +
                    "-" +
                    (month < 10 ? "0" + month : month) +
                    "-" +
                    (day < 10 ? "0" + day : day);

            WebElement id_dob = verifyAgeForm.findElement(By.id("id_dob"));
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            executor.executeScript("arguments[0].value = '" + date + "';", id_dob);
            Thread.sleep(250);
        }

        WebElement submitButton = webDriver.findElement(By.cssSelector("input[type=\"submit\"]"));
        new Actions(webDriver).moveToElement(submitButton);
        Thread.sleep(250);

        submitButton.click();

        Thread.sleep(250);
    }
}
