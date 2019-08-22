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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;


public class JsonReportDriver {
    private static Logger LOGGER = LogManager.getLogger(JsonReportDriver.class);

    public List<String> execute(String[] args) {
        List<String> results = new ArrayList<>();
        try {

            JsonElement root = FileUtils.parseAsJson(Config.REPORT_JSON_RAW_DATA_FILENAME);
            JsonArray rootArray = root.getAsJsonObject().getAsJsonArray(Config.get(Config.REPORT_JSON_ROOT_ARRAY_KEY));

            // Build the header
            results.add("Location," + Config.get(Config.REPORT_JSON_KEYWORDS));

            // Expose the data
            for(int i = 0; i < rootArray.size(); i++) {
                JsonObject currentObject = rootArray.get(i).getAsJsonObject();
                LOGGER.debug("Working with JSON Object: {}", currentObject);
                String row = FileUtils.exposeKeys(currentObject, Config.get(Config.REPORT_JSON_KEYWORDS));
                String wrapper = Config.get(Config.REPORT_CONFIG_TEMP_FILENAME) + "," + row;
                if(!results.contains(wrapper)) {
                    results.add(wrapper);
                }
            }

            LOGGER.info("Results:");
            StringBuilder sb = new StringBuilder();
            for(String s : results) {
                sb.append(s).append("\n");
            }
            LOGGER.info(sb.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        return results;
    }
}
