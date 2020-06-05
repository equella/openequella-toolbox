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
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.checkFiles.CheckFilesDbHandler;
import org.apereo.openequella.tools.toolbox.checkFiles.ReportManager;
import org.apereo.openequella.tools.toolbox.checkFiles.ResultComparison;
import org.apereo.openequella.tools.toolbox.checkFiles.ResultsRow;
import org.apereo.openequella.tools.toolbox.utils.CheckFilesUtils;

/**
 * Consider log4j pattern:
 *
 * <p><Appenders> <Appender type="Console" name="STDOUT"> <Layout type="PatternLayout" pattern="%d
 * %-5p [%c{1}]: %m%n"/> </Appender> </Appenders>
 */
public class CheckFilesDriver {
  private static Logger LOGGER = LogManager.getLogger(CheckFilesDriver.class);

  public List<String> execute(String[] args) {
    List<String> results = new ArrayList<>();
    try {

      if ((args.length > 1) && args[1].equals("-compare")) {
        compareReports(args);
      } else if ((args.length > 0) && args[0].endsWith(".properties")) {
        runCheck(args[0]);
      } else {
        LOGGER.error("ERROR - CheckFiles needs at least the configuration file as a parameter.");
        return results;
      }
    } catch (Exception e) {
      LOGGER.error(e.getMessage());
      e.printStackTrace();
    }

    return results;
  }

  private static void compareReports(String[] args) {
    LOGGER.info("### Starting oEQ-Toolbox:CheckFiles Report Comparison...");
    endCompare:
    {
      if (args.length != 4) {
        LOGGER.error(
            "### Must invoke with two filenames/paths.  Instead found [{}]: [{}]",
            args.length,
            args);
        break endCompare;
      }
      File r1 = new File(args[2]);
      File r2 = new File(args[3]);
      if (!r1.exists()) {
        LOGGER.error("### First file [{}] does not exist.", args[2]);
        break endCompare;
      }
      if (!r2.exists()) {
        LOGGER.error("### Second file [{}] does not exist.", args[3]);
        break endCompare;
      }

      ResultComparison cr = CheckFilesUtils.looseCompare(r1, r2);

      if (cr == null) {
        LOGGER.error("### Error comparing files.");
        break endCompare;
      }

      if (cr.areReportsEqual()) {
        LOGGER.info("Reports ARE similar (in terms of resultant attachments)!");
      } else {
        LOGGER.info(
            "Reports are NOT similar (in terms of resultant attachments)!  Listing differences...");
        LOGGER.info("Extra rows in the first report ({}):", cr.getFirstName());
        for (ResultsRow rr : cr.getOnlyInFirst()) {
          LOGGER.info("\t- {}", rr);
        }
        LOGGER.info("Extra rows in the second report ({}):", cr.getSecondName());
        for (ResultsRow rr : cr.getOnlyInSecond()) {
          LOGGER.info("\t- {}", rr);
        }
      }
    }
    LOGGER.info("### CheckFiles Report Comparison ended.");
  }

  private static void runCheck(String propertiesFilenameStr) {
    LOGGER.info("### Starting CheckFiles report run...");
    LOGGER.info("### Checking configs from {}...", propertiesFilenameStr);

    Config.reset();
    Config.getInstance().init(propertiesFilenameStr);

    if (!setup()) {
      LOGGER.fatal("### Setup check failed.  Exiting...");
      return;
    }
    LOGGER.info("### Config check passed.");

    if (!run()) {
      LOGGER.fatal("### Something went wrong running the report.  Exiting...");
      ReportManager.getInstance().failFast();
      return;
    }

    if (!finalizeRun()) {
      LOGGER.fatal("### Something went wrong finalizing the report.  Exiting...");
      return;
    }

    LOGGER.info("### CheckFiles {} report completed.", Config.get(Config.CF_MODE));
  }

  public static boolean setup() {
    return setup(false);
  }

  public static boolean setup(boolean noFiles) {
    ReportManager.reset();
    if (!Config.getInstance().isValidConfig()) {
      LOGGER.warn("Config is not valid.  Failing setup.");
      return false;
    }
    ReportManager.getInstance().setup(noFiles);
    if (!ReportManager.getInstance().isValid()) {
      LOGGER.warn("Report Manager is not valid.  Failing setup.");
      return false;
    }
    return true;
  }

  public static boolean run() {
    switch (Config.getCheckFilesTypeConfig()) {
      case REST:
        {
          LOGGER.error("REST mode has not been reimplemented.  Exiting.");
          // return (new AttachmentPingHandler()).execute();
          return false;
        }
      case DB_BATCH_ITEMS_PER_ITEM_ATTS:
      case DB_ALL_ITEMS_ALL_ATTS:
        {
          return (new CheckFilesDbHandler()).execute();
        }
      default:
        {
          LOGGER.error("Unknown CheckFiles mode of [{}]...", Config.get(Config.CF_MODE));
          return false;
        }
    }
  }

  public static boolean finalizeRun() {
    return ReportManager.getInstance().finalizeReporting();
  }
}
