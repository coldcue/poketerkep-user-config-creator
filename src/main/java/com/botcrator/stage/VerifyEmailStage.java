package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.EmailHasNotArrivedException;
import com.botcrator.support.mail.MailChecker;
import com.botcrator.support.mail.VerificationLinkExtractor;
import org.openqa.selenium.WebDriver;

import javax.mail.Message;
import java.util.Collection;
import java.util.Optional;


public class VerifyEmailStage extends StageImpl {
    private final WebDriver webDriver;

    public VerifyEmailStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
    }

    @Override
    public void run() throws Exception {

        System.out.println("Waiting for verification email...");

        MailChecker mailChecker = null;
        try {
            mailChecker = new MailChecker("box.netado.hu", "mail@netado.hu", "czb7yMdNK3SHr5j");
            mailChecker.connect();

            for (int i = 0; i < 60; i++) {
                Collection<Message> inbox = mailChecker.getInbox();
                Optional<String> link = VerificationLinkExtractor.extractAndDeleteWithUsername(inbox, wri.getUsername());

                if (link.isPresent()) {
                    webDriver.get(link.get());
                    System.out.println("Email verified successfully");
                    return;
                }

                Thread.sleep(1000);
            }

            throw new EmailHasNotArrivedException();

        } finally {
            if (mailChecker != null) {
                mailChecker.close();
            }
        }
    }
}
