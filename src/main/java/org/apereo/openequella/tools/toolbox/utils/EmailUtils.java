/*
 * Licensed to The Apereo Foundation under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * The Apereo Foundation licenses this file to you under the Apache License,
 * Version 2.0, (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apereo.openequella.tools.toolbox.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.Config;
import org.apereo.openequella.tools.toolbox.checkFiles.ReportManager;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtils {
  private static final String HTML_MIME_TYPE = "text/html; charset=UTF-8";
  private static final String TEXT_MIME_TYPE = "text/plain; charset=UTF-8";
  private static final String UTF8 = "UTF-8";


  private static Logger LOGGER = LogManager.getLogger(EmailUtils.class);

  private static EmailState lastState = null;

  public static void resetState() {
    lastState = null;
    LOGGER.debug("Email state reset");
  }

  public static EmailState getState() {
    return lastState;
  }

  public static void execute(String rawType, String rawToAddresses, String subject, String body) {
    // Save state for an audit (testing)
    lastState = new EmailState(rawType, rawToAddresses, subject, body);

    if (GeneralUtils.isNullOrEmpty(rawToAddresses)) {
      logError("ERROR - [email-type] must be specified");
      return;
    }

    String type = TEXT_MIME_TYPE;
    if(rawType.equalsIgnoreCase("html")) {
      type = HTML_MIME_TYPE;
    }

    if (GeneralUtils.isNullOrEmpty(rawToAddresses)) {
      logError("ERROR - [to-addresses] must be specified");
      return;
    }

    final String[] toAddresses = rawToAddresses.split(",");

    if (GeneralUtils.isNullOrEmpty(subject)) {
      logError("ERROR - [email-subject] must be specified");
      return;
    }

    if (GeneralUtils.isNullOrEmpty(body)) {
      logError("ERROR - [email-body] must be specified");
      return;
    }

    String server = Config.get(Config.EMAIL_SERVER);
    InternetAddress senderAddr;
    try {
      final String senderEmail = Config.get(Config.EMAIL_SENDER_ADDRESS);
      senderAddr = new InternetAddress(senderEmail, Config.get(org.apereo.openequella.tools.toolbox.Config.EMAIL_SENDER_NAME), UTF8);
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
      props.put("mail.smtp.user", Config.get(Config.EMAIL_USERNAME));
      props.put("mail.smtp.password", Config.get(Config.EMAIL_PASSWORD));

      Session mailSession = Session.getInstance(props, new Authenticator() {
        // Set the account information session，transport will send mail
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(Config.get(org.apereo.openequella.tools.toolbox.Config.EMAIL_USERNAME), Config.get(Config.EMAIL_PASSWORD));
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
      lastState.setSuccess(true);
    } catch (Exception e) {
      logError(e, "ERROR - An exception occurred in trying to send the email:  %s", e.getMessage());
      lastState.setSuccess(false);
    }
  }

  private static void logError(String msg) {
    logError(null, msg);
  }

  private static void logError(Exception e, String msg, Object... args) {
    final String err = String.format(msg, args);
    if(Config.get(Config.TOOLBOX_FUNCTION).equalsIgnoreCase(Config.ToolboxFunction.CheckFiles.name())) {
      ReportManager.getInstance().addFatalError(err);
    }
    LOGGER.error(err, e);
  }
}
