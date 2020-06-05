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

package org.apereo.openequella.tools.toolbox;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.utils.Check;
import org.apereo.openequella.tools.toolbox.utils.ImageMagickUtils;

public class ThumbnailDriver {
  private static final String HTML_MIME_TYPE = "text/html; charset=UTF-8";
  private static final String TEXT_MIME_TYPE = "text/plain; charset=UTF-8";
  private static final String UTF8 = "UTF-8";

  private static Logger LOGGER = LogManager.getLogger(ThumbnailDriver.class);

  public void execute(String[] args) {
    if (args.length != 2) {
      LOGGER.error(
          "ERROR - oEQ Toolbox[thumbnail-v1] expects 2 parameters, but has [{}]", args.length);
      return;
    }
    String fileToThumb = args[1];

    if (Check.isEmpty(fileToThumb)) {
      LOGGER.error("ERROR - [file-to-thumb] must be specified");
      return;
    }

    //        if(fileToThumb.endsWith(".PDF") || fileToThumb.endsWith(".pdf")) {
    //            fileToThumb; += "[0]";
    //        }

    try {
      ImageMagickUtils imu = new ImageMagickUtils();
      imu.setImageMagickPath(Config.get(Config.THUMBNAIL_V1_IM_LOCATION));
      imu.afterPropertiesSet();
      imu.generateStandardThumbnail(new File(fileToThumb), new File("./test.jpg"));
      LOGGER.info("SUCCESS - File thumbed at [{}].", fileToThumb);
    } catch (Exception e) {
      LOGGER.error(
          "ERROR - An exception occurred in trying to thumb the file:  " + e.getMessage(), e);
    }
  }
}
