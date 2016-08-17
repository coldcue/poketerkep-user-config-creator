package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.support.ToSAccepter;

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

        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", wri.getProxyPort()));
        ToSAccepter.acceptTos(wri.getUsername(), proxy);

        log.info("ToS Accepted Successfully!");
    }
}
