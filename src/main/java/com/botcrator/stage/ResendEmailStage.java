package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.TwitchBlockException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ResendEmailStage extends StageImpl {
    private final WebDriver webDriver;

    public ResendEmailStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
    }

    @Override
    public void run() throws Exception {
        try {
            //Open signup page
            logger.info("Resending email...");
            webDriver.get("http://www.twitch.tv/settings");

            wri.checkBlock();
            Thread.sleep(3000);

            //Click on the button
            webDriver.findElement(By.id("verify_email")).click();

            Thread.sleep(5000);
        } catch (TwitchBlockException e) {
            throw e;
        } catch (Exception ignored) {
            //Catch everything, its a very best effort thing
        }
    }
}
