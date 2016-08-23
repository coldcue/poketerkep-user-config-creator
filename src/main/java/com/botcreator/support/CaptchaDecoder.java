package com.botcreator.support;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

import java.io.InputStream;
import java.util.logging.Logger;


public class CaptchaDecoder {
    private final Logger logger = Logger.getLogger(CaptchaDecoder.class.getSimpleName());
    private Client client = new HttpClient("coldcue", "M5q86z480D");

    public Captcha decode(InputStream inputStream) {
        return null;
    }

    public Client getClient() {
        return client;
    }
}
