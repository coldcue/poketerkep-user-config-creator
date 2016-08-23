package com.botcreator.stage;

import com.botcreator.WebRegisterInstance;
import com.botcreator.support.ToSAccepter;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.logging.Logger;

public class AcceptTosStage extends StageImpl {
    private final Logger log = Logger.getLogger(this.getClass().getName());

    public AcceptTosStage(WebRegisterInstance wri) {
        super(wri);
    }

    @Override
    public void run() throws Exception {
        log.info("Accepting ToS...");

        for (int i = 0; i < 5; i++) {
            boolean success = false;
            try {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", wri.getProxyPort()));
                ToSAccepter.acceptTos(wri.getUsername(), proxy);
                success = true;
            } catch (Exception e) {
                log.warning("Cannot accept ToS, retrying...");
            }

            if (success) {
                break;
            }

            wri.getTor().newCircuit();
            Thread.sleep(5000);
        }


        log.info("ToS Accepted Successfully!");
    }
}
