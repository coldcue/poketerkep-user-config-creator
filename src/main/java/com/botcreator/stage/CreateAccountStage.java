package com.botcreator.stage;

import com.botcreator.WebRegisterInstance;
import com.botcreator.support.UserNameGenerator;
import com.botcreator.support.mail.EmailGenerator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class CreateAccountStage extends StageImpl {
    private final WebDriver webDriver;

    public CreateAccountStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
    }

    @Override
    public void run() throws Exception {
        logger.info("Creating Account ...");
        final String userName = UserNameGenerator.generateUserName();
        final String email = EmailGenerator.generateEmail(userName);

        wri.setUsername(userName);

        // Get the form element
        WebElement form = webDriver.findElement(By.id("user-signup-create-account-form"));

        //Username
        {
            form.findElement(By.id("id_username")).sendKeys(userName);
            Thread.sleep(100);
        }

        //Password
        {
            String password = UserNameGenerator.getPassword(userName);
            form.findElement(By.id("id_password")).sendKeys(password);
            Thread.sleep(100);

            form.findElement(By.id("id_confirm_password")).sendKeys(password);
            Thread.sleep(100);
        }

        //Email
        {
            WebElement id_email = form.findElement(By.id("id_email"));
            new Actions(webDriver).moveToElement(id_email);
            Thread.sleep(100);
            id_email.sendKeys(email);

            WebElement id_confirm_email = form.findElement(By.id("id_confirm_email"));
            new Actions(webDriver).moveToElement(id_confirm_email);
            Thread.sleep(100);
            id_confirm_email.sendKeys(email);

        }

        //PTC Public profile
        {
            JavascriptExecutor javascriptExecutor = (JavascriptExecutor) webDriver;
            javascriptExecutor.executeScript("document.getElementById(\"id_public_profile_opt_in_1\").checked = true;");
        }

        //Accept terms
        {
            WebElement id_terms = form.findElement(By.id("id_terms"));
            new Actions(webDriver).moveToElement(id_terms);
            Thread.sleep(100);
            id_terms.click();
        }
    }
}
