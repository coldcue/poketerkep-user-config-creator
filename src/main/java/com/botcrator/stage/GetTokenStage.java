package com.botcrator.stage;

import com.DeathByCaptcha.Captcha;
import com.botcrator.WebRegisterInstance;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetTokenStage extends StageImpl {
    private final WebDriver webDriver;
    private final CaptchaDecoder captchaDecoder;
    private Captcha captcha;

    public GetTokenStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
        captchaDecoder = wri.getCaptchaDecoder();
    }

    @Override
    public void run() throws Exception {
        logger.info("Getting IRC Token...");

        webDriver.get("https://api.twitch.tv/kraken/oauth2/authorize?response_type=token&client_id=q6batx0epp608isickayubi39itsckt&redirect_uri=http://twitchapps.com/tmi/&scope=chat_login");
        wri.checkBlock();

        //Authorize
        webDriver.findElement(By.cssSelector("button.primary")).submit();

        try {
            for (int i = 0; i < 3; i++) {
                if (i > 0)
                    captchaDecoder.getClient().report(captcha);
                webDriver.findElement(By.id("recaptcha_image"));
                logger.info("Solving CloudFlare captcha...");
                solveCaptcha();
                webDriver.findElement(By.cssSelector("input.cf-btn-accept")).submit();
                webDriver.get("https://api.twitch.tv/kraken/oauth2/authorize?response_type=token&client_id=q6batx0epp608isickayubi39itsckt&redirect_uri=http://twitchapps.com/tmi/&scope=chat_login");
            }
        } catch (NoSuchElementException ignored) {
            logger.fine("No captcha found, continuing");
        }

        String tmiPasswordField = webDriver.findElement(By.id("tmiExample")).getText();

        Matcher matcher = Pattern.compile("^.*(oauth.+).*", Pattern.DOTALL).matcher(tmiPasswordField);

        //noinspection ResultOfMethodCallIgnored
        matcher.find();

        String token = matcher.group(1);
        logger.info("IRC Token is: " + token);
        wri.setToken(token);
    }

    private void solveCaptcha() throws InterruptedException, IOException {
        //Save image
        BufferedImage image = ImageIO.read(new URL(webDriver.findElement(By.id("recaptcha_image")).findElement(By.tagName("img")).getAttribute("src")));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        //Wait for captcha
        captcha = captchaDecoder.decode(inputStream);

        //Solve captcha
        WebElement recaptcha_response_field = webDriver.findElement(By.id("recaptcha_response_field"));

        //Reset field
        char[] backspaces = new char[30];
        Arrays.fill(backspaces, (char) 8);
        recaptcha_response_field.sendKeys(new String(backspaces));

        recaptcha_response_field.sendKeys(captcha.text);
    }
}
