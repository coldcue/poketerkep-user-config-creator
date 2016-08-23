package com.botcreator.support.mail;

import javax.mail.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class MailChecker {

    private final String host;
    private final String user;
    private final String password;
    private Store store;
    private Folder emailFolder;

    public MailChecker(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public void connect() throws MessagingException {
        //create properties field
        Properties properties = new Properties();

        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        //create the POP3 store object and connect with the pop server
        store = emailSession.getStore("pop3s");

        store.connect(host, user, password);
    }

    public void openInboxFolder() throws MessagingException {
        //create the folder object and open it
        emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);
    }

    public Collection<Message> getInbox() throws MessagingException, NullPointerException {
        // retrieve the messages from the folder in an array and print it
        Message[] messages = emailFolder.getMessages();

        return Arrays.asList(messages);
    }

    public void closeInboxFolder() throws MessagingException {
        //close the store and folder objects
        if (emailFolder != null) {
            emailFolder.close(false);
            emailFolder = null;
        }
    }

    public void close() throws MessagingException {
        closeInboxFolder();
        store.close();
    }
}