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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class TestUtils {

	private static Logger LOGGER = LogManager.getLogger(Config.class);

	public static void buildCheckFilesGeneralDbProps() {
		Properties envSpecific = loadEnvSpecificDefaults();
		Config.getInstance().setConfig(Config.TOOLBOX_FUNCTION, Config.ToolboxFunction.CheckFiles.name());
		Config.getInstance().setConfig(Config.CF_OUTPUT_FOLDER, "test-check-files-output/"+ UUID.randomUUID().toString()+"/");
		Config.getInstance().setConfig(Config.CF_ADOPTER_NAME, "acme");
		Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
		Config.getInstance().setConfig(Config.CF_DB_TYPE, Config.CheckFilesSupportedDB.POSTGRE.name());
		Config.getInstance().setConfig(Config.CF_DB_URL, envSpecific.getProperty(Config.CF_DB_URL));
		Config.getInstance().setConfig(Config.CF_DB_USERNAME, envSpecific.getProperty(Config.CF_DB_USERNAME));
		Config.getInstance().setConfig(Config.CF_DB_PASSWORD, envSpecific.getProperty(Config.CF_DB_PASSWORD));
		Config.getInstance().setConfig(Config.CF_FILESTORE_DIR, envSpecific.getProperty(Config.CF_FILESTORE_DIR));
		Config.getInstance().setConfig(Config.CF_EMAIL_RECIPIENTS, envSpecific.getProperty(Config.CF_EMAIL_RECIPIENTS));
		Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "20");
		final String advFsKeyBeta = "cf.filestore.advanced.812.44ac1c1a-dd0c-462f-b717-53324c0dd6f9";
		Config.getInstance().setConfig(advFsKeyBeta, envSpecific.getProperty(advFsKeyBeta));
		final String advFsKeyBetaInst2 = "cf.filestore.advanced.1509.44ac1c1a-dd0c-462f-b717-53324c0dd6f9";
		Config.getInstance().setConfig(advFsKeyBetaInst2, envSpecific.getProperty(advFsKeyBetaInst2));
		final String advFsKeyCharlie = "cf.filestore.advanced.812.5a7b2083-2671-414b-8e5b-c4cd9dfa1f30";
		Config.getInstance().setConfig(advFsKeyCharlie, envSpecific.getProperty(advFsKeyCharlie));
		Config.getInstance().setConfig("cf.db.filestore.institution.handle.checkFilesTesting2", "checkFilesTesting2Renamed");
	}

	public static void buildEmailProps() {
		Properties envSpecific = loadEnvSpecificDefaults();
		Config.getInstance().setConfig(Config.EMAIL_SERVER, "smtp.gmail.com:587");
		Config.getInstance().setConfig(Config.EMAIL_SENDER_NAME,
						"testEmailSettingsE2E");
		Config.getInstance().setConfig(Config.EMAIL_SENDER_ADDRESS,
						"testEmailSettingsE2E@example.com");
		Config.getInstance().setConfig(Config.EMAIL_USERNAME,
						envSpecific.getProperty(Config.EMAIL_USERNAME));
		Config.getInstance().setConfig(Config.EMAIL_PASSWORD,
						envSpecific.getProperty(Config.EMAIL_PASSWORD));
	}

	public static Properties loadEnvSpecificDefaults() {
		final String path = "conf/junit.properties";
		try (InputStream input = new FileInputStream(path)) {
			LOGGER.debug("Using [{}] as the configuration file for env specific junit properties.", path);
			Properties envSpecificDefaults = new Properties();
			envSpecificDefaults.load(input);
			return envSpecificDefaults;
		} catch (FileNotFoundException e) {
			LOGGER.warn("Unable to find the config file: {}", e.getMessage());
		} catch (IOException e) {
			LOGGER.warn("Unable to use the config file: [{}] due to - [{}].", path, e.getMessage());
		}
		return null;
	}

	public static void debugDumpList(List<String> strs) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Begin Result Dump");
			for(int i = 0; i < strs.size(); i++) {
				LOGGER.debug("[{}] - {}", i, strs.get(i));
			}
			LOGGER.debug("End Result Dump");
		}
	}
}
