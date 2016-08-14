package com.botcrator.exception;

public class CaptchaLoadFailException extends Exception {
    public CaptchaLoadFailException() {
        super("Captcha hasn't loaded");
    }
}
