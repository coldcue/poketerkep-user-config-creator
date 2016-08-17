package com.botcrator.support.mail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;

public class VerificationLinkExtractorTest {

    private MailChecker mailChecker;

    @Before
    public void before() throws MessagingException {
        mailChecker = new MailChecker("box.netado.hu", "mail@netado.hu", "czb7yMdNK3SHr5j");
        mailChecker.connect();
        mailChecker.openInboxFolder();
    }

    @Test
    public void itShouldGetActivationLink() throws MessagingException, IOException {
        String input = "oilybreath322";
        String expected = "https://club.pokemon.com/us/pokemon-trainer-club/activated/0c1123cde851d198a5a8f6b1d48b2e81";


        Optional<String> actual = VerificationLinkExtractor
                .extractAndDeleteWithUsername(mailChecker.getInbox(), input);

        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(expected, actual.get());

    }

    @After
    public void after() throws MessagingException {
        mailChecker.close();
    }

}