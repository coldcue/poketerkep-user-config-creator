package com.botcrator.support.mail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationLinkExtractor {

    public static Optional<String> extractAndDeleteWithUsername(Collection<Message> inbox, String username) throws IOException, MessagingException {

        for (Message message : inbox) {
            Address[] recipients = message.getRecipients(Message.RecipientType.TO);
            assert recipients.length == 1;

            if (recipients[0].toString().contains(username)) {
                String messageBody = message.getContent().toString();
                Matcher matcher = Pattern.compile("^.*href=\"https://club.pokemon.com/us/pokemon-trainer-club/activated/(?<activationToken>\\w+)\".*$", Pattern.MULTILINE + Pattern.DOTALL).matcher(messageBody);

                if (matcher.find()) {
                    String token = matcher.group("activationToken");

                    return Optional.of("https://club.pokemon.com/us/pokemon-trainer-club/activated/" + token);
                }
            }

        }

        return Optional.empty();
    }
}
