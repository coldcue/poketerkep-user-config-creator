package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.guerillamail.GuerillaMailVerifier;

import java.io.IOException;
import java.net.InetSocketAddress;

public class VerifyEmailStage extends StageImpl {


    private GuerillaMailVerifier guerillaMailVerifier;

    public VerifyEmailStage(WebRegisterInstance wri) throws IOException {
        super(wri);
        //Start guerillamail
        guerillaMailVerifier = new GuerillaMailVerifier(wri.getUsername(), wri.getIp(), new InetSocketAddress("localhost", wri.getProxyPort()));
    }

    @Override
    public void run() throws Exception {
        try {
            logger.info("Verifing e-mail...");
            String verifyURL = guerillaMailVerifier.getVerifyURL();
            logger.info("Verify URL: " + verifyURL);

            //Verify email
            wri.getWebDriver().get(verifyURL);
            wri.checkBlock();
        } finally {
            guerillaMailVerifier.close();
        }
    }

    public void close() throws IOException {
        guerillaMailVerifier.close();
    }
}
