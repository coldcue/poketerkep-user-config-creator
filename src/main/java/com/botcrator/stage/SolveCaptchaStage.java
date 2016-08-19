package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.WrongCaptchaException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SolveCaptchaStage extends StageImpl {
    private final WebDriver webDriver;

    public SolveCaptchaStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
    }

    @Override
    public void run() throws Exception {
        logger.info("Solving captcha");

        //Click on the checkbox
        {
            // Switch to recaptcha iframe
            WebElement iframe = webDriver.findElement(By.cssSelector("iframe[title=\"recaptcha widget\"]"));
            webDriver.switchTo().frame(iframe);

            //Click on the checkbox
            WebElement anchor = webDriver.findElement(By.id("recaptcha-anchor"));
            new Actions(webDriver).moveToElement(anchor);
            Thread.sleep(250);
            anchor.click();

            //Switch back to main frame
            webDriver.switchTo().defaultContent();
        }

        Thread.sleep(3000);


        logger.info("Waiting for the captcha to be solved...");
        boolean captchaSolved = false;
        for (int i = 0; i < 3 * 60; i++) {
            JavascriptExecutor jse = (JavascriptExecutor) webDriver;
            String response = (String) jse.executeScript("return document.getElementById('g-recaptcha-response').value;");

            if (response != null && response.length() > 0) {
                captchaSolved = true;
                break;
            }

            Thread.sleep(1000);
        }

        if (captchaSolved) {
            logger.info("Captcha solved!");
        } else {
            throw new WrongCaptchaException();
        }

        WebElement submitButton = webDriver.findElement(By.cssSelector("input[type=\"submit\"]"));
        new Actions(webDriver).moveToElement(submitButton);
        Thread.sleep(250);
        submitButton.click();

        Thread.sleep(1000);

        if (webDriver.findElement(By.id("signup-signin")) != null) {
            logger.info("Verification email sent!");
        } else {
            throw new Exception("Something bad happened");
        }


        //Send images
//        try {
//            // Switch to recaptcha iframe
//            WebElement iframe = webDriver.findElement(By.cssSelector("iframe[title=\"recaptcha challenge\"]"));
//            webDriver.switchTo().frame(iframe);
//
//            String description = webDriver.findElement(By.cssSelector("div.rc-imageselect-desc-no-canonical")).getText();
//
//            WebElement image = webDriver.findElement(By.cssSelector("div.rc-imageselect-challenge img"));
//
//            logger.info("Images arrived");
//        } catch (Exception e) {
//            logger.info("No captcha image found, continuing ...");
//        }

    }
}
