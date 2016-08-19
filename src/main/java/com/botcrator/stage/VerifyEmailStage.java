package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.EmailHasNotArrivedException;
import com.botcrator.support.mail.MailChecker;
import com.botcrator.support.mail.VerificationLinkExtractor;
import org.openqa.selenium.WebDriver;

import javax.mail.Message;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;


public class VerifyEmailStage extends StageImpl {
    private final Logger log = Logger.getLogger(this.getClass().getName());
    private final WebDriver webDriver;

    public VerifyEmailStage(WebRegisterInstance wri) {
        super(wri);
        webDriver = wri.getWebDriver();
    }

    @Override
    public void run() throws Exception {

        log.info("Waiting for verification email...");

        MailChecker mailChecker = null;
        try {
            mailChecker = new MailChecker("box.netado.hu", "mail@netado.hu", "czb7yMdNK3SHr5j");
            mailChecker.connect();

            for (int i = 0; i < 30; i++) {
                mailChecker.openInboxFolder();
                Collection<Message> inbox = mailChecker.getInbox();
                Optional<String> link = VerificationLinkExtractor.extractAndDeleteWithUsername(inbox, wri.getUsername());
                mailChecker.closeInboxFolder();

                if (link.isPresent()) {
                    webDriver.get(link.get());
                    log.info("Email verified successfully");
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
