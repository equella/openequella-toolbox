package org.apereo.openequella.tools.toolbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.utils.GeneralUtils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class EmailDriver {
    private static final String HTML_MIME_TYPE = "text/html; charset=UTF-8";
    private static final String TEXT_MIME_TYPE = "text/plain; charset=UTF-8";
    private static final String UTF8 = "UTF-8";


    private static Logger LOGGER = LogManager.getLogger(EmailDriver.class);

    public void execute(Config config, String[] args) {
        if(args.length != 5) {
            LOGGER.error("ERROR - Emailer expects 5 parameters, but has [{}]", args.length);
            return;

        }
        final String rawType = args[1];
        final String rawToAddresses = args[2];
        final String subject = args[3];
        final String body = args[4];

        if (GeneralUtils.isNullOrEmpty(rawToAddresses)) {
            LOGGER.error("ERROR - [email-type] must be specified");
            return;
        }

        String type = TEXT_MIME_TYPE;
        if(rawType.equalsIgnoreCase("html")) {
            type = HTML_MIME_TYPE;
        }

        if (GeneralUtils.isNullOrEmpty(rawToAddresses)) {
            LOGGER.error("ERROR - [to-addresses] must be specified");
            return;
        }

        final String[] toAddresses = rawToAddresses.split(",");

        if (GeneralUtils.isNullOrEmpty(subject)) {
            LOGGER.error("ERROR - [email-subject] must be specified");
            return;
        }

        if (GeneralUtils.isNullOrEmpty(body)) {
            LOGGER.error("ERROR - [email-body] must be specified");
            return;
        }

        String server = config.getConfig(Config.EMAIL_SERVER);
        InternetAddress senderAddr;
        try {
            final String senderEmail = config.getConfig(Config.EMAIL_SENDER_ADDRESS);
            senderAddr = new InternetAddress(senderEmail, config.getConfig(Config.EMAIL_SENDER_NAME), UTF8);
            final Properties props = System.getProperties();

            int ind = server.indexOf(':');
            if (ind != -1) {
                props.put("mail.smtp.port", server.substring(ind + 1));
                server = server.substring(0, ind);
            }

            props.put("mail.host", server);
            props.put("mail.from", senderEmail);
            GeneralUtils.setDefaultIfNotPresent(props, "mail.transport.protocol", "smtp");
            GeneralUtils.setDefaultIfNotPresent(props, "mail.smtp.starttls.enable", "true");
            GeneralUtils.setDefaultIfNotPresent(props, "mail.smtp.auth", "true");
            props.put("mail.smtp.user", config.getConfig(Config.EMAIL_USERNAME));
            props.put("mail.smtp.password", config.getConfig(Config.EMAIL_PASSWORD));

            Session mailSession = Session.getInstance(props, new Authenticator() {
                // Set the account information session，transport will send mail
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getConfig(Config.EMAIL_USERNAME), config.getConfig(Config.EMAIL_PASSWORD));
                }
            });

            Message mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setFrom(senderAddr);

            for (String email : toAddresses) {
                mimeMessage.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email.trim()));
            }
            mimeMessage.setSubject(subject);
            mimeMessage.setHeader("X-Mailer", "openEQUELLA Toolbox");
            mimeMessage.setHeader("MIME-Version", "1.0");
            mimeMessage.setHeader("Content-Type", type);
            mimeMessage.setContent(body, type);
            Transport.send(mimeMessage);
            LOGGER.info("SUCCESS - Email sent to [{}].", rawToAddresses);
        } catch (Exception e) {
            LOGGER.error("ERROR - An exception occurred in trying to send the email:  " + e.getMessage(), e);
        }
    }
}
