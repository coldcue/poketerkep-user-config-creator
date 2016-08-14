package com.botcrator.stage;

import com.DeathByCaptcha.Captcha;
import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.CaptchaLoadFailException;
import com.botcrator.support.CaptchaDecoder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

public class SolveCaptchaStage extends StageImpl {
    private final WebDriver webDriver;
    private final CaptchaDecoder captchaDecoder;
    private Captcha captcha;

    public SolveCaptchaStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
        captchaDecoder = wri.getCaptchaDecoder();
        captcha = wri.getCaptcha();
    }

    @Override
    public void run() throws Exception {
        logger.info("Solving captcha...");

        //Check if loaded
        try {
            webDriver.findElement(By.id("recaptcha_image"));
        } catch (NoSuchElementException ignored) {
            throw new CaptchaLoadFailException();
        }

        //Solve captcha
        solveCaptcha();
    }

    private void solveCaptcha() throws InterruptedException, IOException {
        //Save image
        BufferedImage image = ImageIO.read(new URL(webDriver.findElement(By.id("recaptcha_image")).findElement(By.tagName("img")).getAttribute("src")));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        //Wait for captcha
        captcha = captchaDecoder.decode(inputStream);
        wri.setCaptcha(captcha);

        //Solve captcha
        WebElement recaptcha_response_field = webDriver.findElement(By.id("recaptcha_response_field"));

        //Reset field
        char[] backspaces = new char[30];
        Arrays.fill(backspaces, (char) 8);
        recaptcha_response_field.sendKeys(new String(backspaces));

        recaptcha_response_field.sendKeys(captcha.text);
    }
}
