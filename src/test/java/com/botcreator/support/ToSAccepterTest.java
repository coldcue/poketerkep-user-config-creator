package com.botcreator.support;

import org.junit.Test;

import java.net.Proxy;


public class ToSAccepterTest {
    @Test
    public void acceptTos() throws Exception {
        ToSAccepter.acceptTos("stinkyelbow682", Proxy.NO_PROXY);
    }

}