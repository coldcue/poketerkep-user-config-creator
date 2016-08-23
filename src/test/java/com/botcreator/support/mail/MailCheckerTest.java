package com.botcreator.support.mail;


import org.junit.Assert;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.Collection;

public class MailCheckerTest {

    @Test
    public void itShouldConnect() throws MessagingException {
        MailChecker mailChecker = new MailChecker("box.netado.hu", "mail@netado.hu", "czb7yMdNK3SHr5j");

        try {
            mailChecker.connect();
        } catch (MessagingException e) {
            e.printStackTrace();
            Assert.fail("Could not connect");
        } finally {
            mailChecker.close();
        }

    }

    @Test
    public void itShouldOpenInbox() throws MessagingException {
        MailChecker mailChecker = new MailChecker("box.netado.hu", "mail@netado.hu", "czb7yMdNK3SHr5j");

        try {
            mailChecker.connect();
            mailChecker.openInboxFolder();
            Collection<Message> inbox = mailChecker.getInbox();
            inbox.stream()
                    .map(message -> {
                        try {
                            return message.getSubject();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                        return "";
                    })
                    .forEach(System.out::println);
        } catch (MessagingException e) {
            e.printStackTrace();
            Assert.fail("Could not connect");
        } finally {
            mailChecker.close();
        }

    }

}