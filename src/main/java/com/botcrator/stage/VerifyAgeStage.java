package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
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

        verifyAgeForm.findElement(By.id("id_dob")).click();
        Thread.sleep(250);

        List<WebElement> selectorElements = verifyAgeForm.findElements(By.cssSelector("div.picker__header div.custom-select-menu"));
        assert selectorElements.size() == 2;

        // Select random month
        {
            WebElement monthSelectMenu = selectorElements.get(0);
            // Click to show months
            monthSelectMenu.click();
            Thread.sleep(250);

            List<WebElement> months = monthSelectMenu.findElements(By.cssSelector("ul.overview > li"));
            assert months.size() == 12;

            // Select random months (they should be visible)
            WebElement monthToSelect = months.get(random.nextInt(11));
            new Actions(webDriver).moveToElement(monthToSelect);
            monthToSelect.click();
        }

        Thread.sleep(250);

        // Get selector elements again
        selectorElements = verifyAgeForm.findElements(By.cssSelector("div.picker__header div.custom-select-menu"));
        assert selectorElements.size() == 2;

        {
            WebElement yearSelectMenu = selectorElements.get(1);
            //Click to show years
            yearSelectMenu.click();
            Thread.sleep(250);

            List<WebElement> years = yearSelectMenu.findElements(By.cssSelector("ul.overview > li"));
            assert years.size() > 21;

            // Select random year
            WebElement yearToSelect = years.get(21 + random.nextInt(30));
            new Actions(webDriver).moveToElement(yearToSelect);
            yearToSelect.click();
        }

        Thread.sleep(500);

        // Click confirm button
        verifyAgeForm.findElement(By.cssSelector("div.picker__footer button")).click();

        Thread.sleep(500);

        webDriver.findElement(By.cssSelector("input[type=\"submit\"]")).click();

        Thread.sleep(250);
    }
}
