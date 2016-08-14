package com.botcrator.guerillamail;

import com.botcrator.exception.EmailHasNotArrivedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuerillaMailVerifier {
    private String username;
    private GuerillaMailClient guerillaMailClient = null;

    public GuerillaMailVerifier(String username, String ip, InetSocketAddress proxy) throws IOException {
        this.username = username;
        for (int i = 0; i < 20; i++) {
            try {
                guerillaMailClient = new GuerillaMailClient();
                guerillaMailClient.setEmailUser(username);
                break;
            } catch (Exception ignored) {
                if (guerillaMailClient != null)
                    guerillaMailClient.close();
            }
        }
    }

    public String getVerifyURL() throws EmailHasNotArrivedException {

        int id = 0;
        //15 tries mean 2 minutes
        for (int i = 1; i < 15; i++) {
            try {
                Thread.sleep(i * 1000);
                id = guerillaMailClient.checkTwitchEmail();
                if (id != 0) break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (id == 0) throw new EmailHasNotArrivedException();

        String body = null;
        for (int i = 0; i < 20; i++) {
            try {
                body = guerillaMailClient.fetchEmail(id);
                break;
            } catch (Exception ignored) {
            }
        }

        Matcher matcher = Pattern.compile("^.*<a href=\\\"(.+)\\\">", Pattern.DOTALL).matcher(body);
        matcher.find();

        return matcher.group(1);
    }

    public void close() throws IOException {
        guerillaMailClient.close();
    }
}
