package com.botcrator.stage;


import com.botcrator.WebRegisterInstance;
import com.botcrator.exception.ConnectionResetException;
import com.botcrator.exception.TwitchBlockException;
import org.apache.commons.io.IOUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

public class CheckBlockStage extends StageImpl {
    public CheckBlockStage(WebRegisterInstance wri) {
        super(wri);
    }

    @Override
    public void run() throws Exception {
        try {
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", wri.getProxyPort()));
            URLConnection urlConnection = new URL("http://www.twitch.tv/signup").openConnection(proxy);

            String content = IOUtils.toString(urlConnection.getInputStream());

            boolean matches = Pattern.compile("^.*You have been blocked from using Twitch.*", Pattern.DOTALL).matcher(content).matches();
            if (matches) {
                throw new TwitchBlockException();
            }

            matches = Pattern.compile("^.*Unable to forward this request at this time.*", Pattern.DOTALL).matcher(content).matches();
            if (matches) {
                throw new ConnectionResetException();
            }
        } catch (TwitchBlockException e) {
            throw e;
        } catch (Exception e) {
            logger.warning("Can't open the page with this client, try with firefox...");
        }
    }
}
