package com.botcrator.support;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

import java.io.InputStream;
import java.util.logging.Logger;


public class CaptchaDecoder {
    private final Logger logger = Logger.getLogger(CaptchaDecoder.class.getSimpleName());
    private Client client = new HttpClient("coldcue", "HzV-27R-NQQ-aRq");

    public Captcha decode(InputStream inputStream) {
        Captcha captcha = null;
        try {
            // Put your CAPTCHA image file, file object, input stream,
            // or vector of bytes here:
            captcha = this.client.upload(inputStream);
            if (null != captcha) {
                logger.info("CAPTCHA uploaded: " + captcha.id);

                // Poll for the uploaded CAPTCHA status.
                while (captcha.isUploaded() && !captcha.isSolved()) {
                    Thread.sleep(Client.POLLS_INTERVAL * 1000);
                    captcha = this.client.getCaptcha(captcha);
                }

                if (captcha.isSolved()) {
                    logger.info("CAPTCHA solved: " + captcha.text);

                    // Report incorrectly solved CAPTCHA if neccessary.
                    // Make sure you've checked if the CAPTCHA was in fact
                    // incorrectly solved, or else you might get banned as
                    // abuser.
                    /*if (this.client.report(captcha)) {
                        System.out.println("CAPTCHA " + this.captchaFilename + " reported as incorrectly solved");
                    } else {
                        System.out.println("Failed reporting incorrectly solved CAPTCHA");
                    }*/
                } else {
                    logger.warning("Failed solving CAPTCHA");
                }
            }
        } catch (java.lang.Exception e) {
            System.err.println(e.toString());
        }
        return captcha;
    }

    public Client getClient() {
        return client;
    }
}
