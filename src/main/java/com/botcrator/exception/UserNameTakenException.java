package com.botcrator.exception;

public class UserNameTakenException extends Exception {
    public UserNameTakenException() {
        super("User name has been already taken");
    }
}
