package com.botcrator.exception;


public class TwitchBlockException extends Exception {
    public TwitchBlockException() {
        super("Twitch has blocked this IP");
    }
}
