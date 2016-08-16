package com.botcrator.support.mail;

import javax.mail.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

public class MailChecker {

    private final String host;
    private final String user;
    private final String password;
    private Store store;

    public MailChecker(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public void connect() throws MessagingException {
        //create properties field
        Properties properties = new Properties();

        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "115");
        properties.put("mail.pop3.starttls.enable", "false");
        Session emailSession = Session.getDefaultInstance(properties);

        //create the POP3 store object and connect with the pop server
        store = emailSession.getStore("pop3");

        store.connect(host, user, password);
    }

    public Collection<Message> getInbox() throws MessagingException, IOException {
        //create the folder object and open it
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);

        // retrieve the messages from the folder in an array and print it
        Message[] messages = emailFolder.getMessages();

        //close the store and folder objects
        emailFolder.close(false);

        return Collections.unmodifiableCollection(Arrays.asList(messages));
    }


    public void close() throws MessagingException {
        store.close();
    }
}