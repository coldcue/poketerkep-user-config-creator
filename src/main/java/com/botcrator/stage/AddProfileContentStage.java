package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.TwitchBlockException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;


public class AddProfileContentStage extends StageImpl {

    private WebDriver webDriver;
    private File image;

    public AddProfileContentStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
        image = new File("tmp/" + wri.getId() + ".jpg");
    }

    @Override
    public void run() throws Exception {
        try {
            logger.info("Adding profile content...");

            //Create tmp directory if not exists
            if (!new File("tmp").isDirectory()) {
                new File("tmp").mkdir();
            }

            //Delete file if exists
            if (image.exists())
                image.delete();

            //Download profile image
            logger.info("Downloading facebook image to " + image.getAbsolutePath());
            BufferedImage bufferedImage = ImageIO.read(new URL(wri.getFacebookData().getImageURL()));
            ImageIO.write(bufferedImage, "jpg", image);

            //Get settings
            webDriver.get("http://www.twitch.tv/settings");
            Thread.sleep(1000);
            wri.checkBlock();

            //Upload image
            webDriver.findElement(By.id("user_profile_image")).sendKeys(image.getCanonicalPath());
            Thread.sleep(250);

            //Fill captitalized username
            WebElement userDisplayname = webDriver.findElement(By.id("user_displayname"));
            userDisplayname.clear();
            userDisplayname.sendKeys(wri.getFacebookData().getUsername());
            Thread.sleep(250);

            //Submit
            logger.info("Submitting profile content");
            webDriver.findElement(By.id("user_settings_submit")).submit();
            Thread.sleep(1000);
            wri.checkBlock();

        } catch (TwitchBlockException e) {
            throw e;
        } catch (Exception e) {
            logger.warning(e.getMessage());
        } finally {
            try {
                //Delete image
                if (image.exists())
                    image.delete();
            } catch (Exception ignored) {

            }
        }
    }
}
