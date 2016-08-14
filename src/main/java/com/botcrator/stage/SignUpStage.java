package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.TwitchBlockException;
import com.botcrator.exception.UserNameTakenException;
import com.botcrator.exception.WrongCaptchaException;
import org.openqa.selenium.By;

public class SignUpStage extends StageImpl {
    public SignUpStage(WebRegisterInstance wri) {
        super(wri);
    }

    @Override
    public void run() throws Exception {
        //Submit form
        wri.getWebDriver().findElement(By.id("subwindow_create_submit")).submit();

        //Wait for processing
        Thread.sleep(5000);

        //Check block
        wri.checkBlock();

        //Check for errors
        String login_error_message = wri.getWebDriver().findElement(By.id("login_error_message")).getText();
        if (login_error_message.equals("Please type the correct captcha phrase.")) {
            throw new WrongCaptchaException();
        } else if (login_error_message.equals("You are trying to sign up for accounts too fast")) {
            throw new TwitchBlockException();
        } else if (login_error_message.equals("Login has already been taken")) {
            throw new UserNameTakenException();
        }
    }
}
