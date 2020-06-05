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
import org.apereo.openequella.tools.toolbox.utils.GeneralUtils;

public class AttachmentHashDriver {
  private static Logger LOGGER = LogManager.getLogger(AttachmentHashDriver.class);

  public void execute(String[] args) {
    if (args.length < 2) {
      LOGGER.error(
          "No parameters found!  AttachmentHash parameters - [req] config file, [req] att-uuid");
      return;
    }

    final int res = GeneralUtils.findHash(args[1]);
    LOGGER.info("Hash is [{}]", res);
  }
}
