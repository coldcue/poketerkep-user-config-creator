package com.botcrator.stage;

import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.ConnectionResetException;
import org.apache.commons.io.IOUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

public class GetIPStage extends StageImpl {

    public GetIPStage(WebRegisterInstance wri) {
        super(wri);
    }

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    @Override
    public void run() throws Exception {
        logger.fine("Getting IP...");

        boolean success = false;
        //3 tries
        for (int i = 0; i < 3; i++) {
            try {
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", wri.getProxyPort()));
                URLConnection urlConnection = new URL("http://bot.whatismyipaddress.com").openConnection(proxy);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(15000);

                String content = IOUtils.toString(urlConnection.getInputStream());

                //noinspection ResultOfMethodCallIgnored
                if (!Pattern.matches(IPADDRESS_PATTERN, content)) throw new ConnectionResetException();
                logger.info("Our IP is: " + content);
                wri.setIp(content);
                success = true;
                break;
            } catch (Exception ignored) {
            }
        }

        if (!success) {
            throw new ConnectionResetException();
        }

    }
}
