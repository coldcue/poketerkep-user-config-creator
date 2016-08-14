package com.botcrator.exception;

public class EmailHasNotArrivedException extends Exception {
    public EmailHasNotArrivedException() {
        super("Twitch email hasn't arrived");
    }
}
