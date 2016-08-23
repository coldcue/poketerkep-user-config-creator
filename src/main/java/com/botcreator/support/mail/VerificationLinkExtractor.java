package com.botcreator.support.mail;

import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationLinkExtractor {

    public static Optional<String> extractAndDeleteWithUsername(Collection<Message> inbox, String username) throws IOException, MessagingException {

        for (Message message : inbox) {
            try {
                Address[] recipients = message.getRecipients(Message.RecipientType.TO);

                if (recipients.length != 1) {
                    continue;
                }

                if (recipients[0].toString().contains(username)) {
                    MimeMultipart content = (MimeMultipart) message.getContent();
                    String result = getTextFromMimeMultipart(content);

                    Matcher matcher = Pattern.compile("^.*href=\"https://club.pokemon.com/us/pokemon-trainer-club/activated/(?<activationToken>\\w+)\".*$", Pattern.MULTILINE + Pattern.DOTALL).matcher(result);

                    if (matcher.find()) {
                        String token = matcher.group("activationToken");

                        message.setFlag(Flags.Flag.DELETED, true);
                        return Optional.of("https://club.pokemon.com/us/pokemon-trainer-club/activated/" + token);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        return Optional.empty();
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws IOException, MessagingException {

        int count = mimeMultipart.getCount();
        if (count == 0)
            throw new MessagingException("Multipart with no body parts not supported.");
        boolean multipartAlt = new ContentType(mimeMultipart.getContentType()).match("multipart/alternative");
        if (multipartAlt)
            // alternatives appear in an order of increasing
            // faithfulness to the original content. Customize as req'd.
            return getTextFromBodyPart(mimeMultipart.getBodyPart(count - 1));
        String result = "";
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            result += getTextFromBodyPart(bodyPart);
        }
        return result;
    }

    private static String getTextFromBodyPart(
            BodyPart bodyPart) throws IOException, MessagingException {

        String result = "";
        if (bodyPart.isMimeType("text/plain")) {
            result = (String) bodyPart.getContent();
        } else if (bodyPart.isMimeType("text/html")) {
            result = (String) bodyPart.getContent();
        } else if (bodyPart.getContent() instanceof MimeMultipart) {
            result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        return result;
    }
}
