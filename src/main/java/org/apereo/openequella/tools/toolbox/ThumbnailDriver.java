package org.apereo.openequella.tools.toolbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.utils.Check;
import org.apereo.openequella.tools.toolbox.utils.GeneralUtils;
import org.apereo.openequella.tools.toolbox.utils.ImageMagickUtils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Properties;


public class ThumbnailDriver {
    private static final String HTML_MIME_TYPE = "text/html; charset=UTF-8";
    private static final String TEXT_MIME_TYPE = "text/plain; charset=UTF-8";
    private static final String UTF8 = "UTF-8";


    private static Logger LOGGER = LogManager.getLogger(ThumbnailDriver.class);

    public void execute(Config config, String[] args) {
        if(args.length != 2) {
            LOGGER.error("ERROR - oE Toolbox[thumbnail-v1] expects 2 parameters, but has [{}]", args.length);
            return;

        }
        String fileToThumb  = args[1];

        if (Check.isEmpty(fileToThumb)) {
            LOGGER.error("ERROR - [file-to-thumb] must be specified");
            return;
        }

//        if(fileToThumb.endsWith(".PDF") || fileToThumb.endsWith(".pdf")) {
//            fileToThumb; += "[0]";
//        }

        try {
            ImageMagickUtils imu = new ImageMagickUtils();
            imu.setImageMagickPath(config.getConfig(Config.THUMBNAIL_V1_IM_LOCATION));
            imu.afterPropertiesSet();
            imu.generateStandardThumbnail(new File(fileToThumb), new File("./test.jpg"));
            LOGGER.info("SUCCESS - File thumbed at [{}].", fileToThumb);
        } catch (Exception e) {
            LOGGER.error("ERROR - An exception occurred in trying to thumb the file:  " + e.getMessage(), e);
        }
    }
}
