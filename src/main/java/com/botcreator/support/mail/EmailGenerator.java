package com.botcreator.support.mail;


import java.util.Random;

public class EmailGenerator {
    private static final String[] EMAIL_DOMAINS = {"netado.hu"};

    public static String generateEmail(String username) {
        Random random = new Random();
        //return username + '@' + EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length - 1)];
        return username + '@' + "netado.hu";
    }

}
