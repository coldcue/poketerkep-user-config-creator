package com.botcreator.exception;


public class ConnectionResetException extends Exception {
    public ConnectionResetException() {
        super("Connection reset");
    }
}
