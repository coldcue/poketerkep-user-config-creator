package com.botcreator.exception;

/**
 * Created by andrew on 4/28/14.
 */
public class WrongCaptchaException extends Exception {
    public WrongCaptchaException() {
        super("Wrong captcha");
    }
}
