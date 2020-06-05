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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.Config.ToolboxFunction;

public class Driver {
  private static Logger LOGGER = LogManager.getLogger(Driver.class);

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      LOGGER.error("Exiting - requires a config file.");
      return;
    }
    Config.getInstance().init(args[0]);
    if (!Config.getInstance().isValidConfig()) {
      LOGGER.error("Exiting - invalid config.");
      return;
    }

    // Since the config is valid, this is guaranteed to work.
    ToolboxFunction tool = ToolboxFunction.valueOf(Config.get(Config.TOOLBOX_FUNCTION));
    switch (tool) {
      case MigrateToKaltura:
        {
          (new MigrateItemsToKalturaDriver()).execute();
          break;
        }
      case ExportItems:
        {
          (new ExportItemsDriver()).execute();
          break;
        }
      case Email:
        {
          (new EmailDriver()).execute(args);
          break;
        }
      case FileLister:
        {
          (new FileListerDriver()).execute(args);
          break;
        }
      case ThumbnailV1:
        {
          (new ThumbnailDriver()).execute(args);
          break;
        }
      case JsonReport:
        {
          (new JsonReportDriver()).execute(args);
          break;
        }
      case CheckFiles:
        {
          (new CheckFilesDriver()).execute(args);
          break;
        }
      case AttachmentHash:
        {
          (new AttachmentHashDriver()).execute(args);
          break;
        }
      default:
        {
          LOGGER.error("Exiting - Unimplemented toolbox function of: {}.", tool);
          return;
        }
    }
  }
}
