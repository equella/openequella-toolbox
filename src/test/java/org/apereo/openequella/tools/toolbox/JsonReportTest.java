/*
 * Copyright 2018 Apereo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import com.google.gson.JsonParser;
import org.apereo.openequella.tools.toolbox.utils.FileUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JsonReportTest {
	
	@Test
	public void testParseJsonFile() {
		Properties props = new Properties();
		props.put(Config.REPORT_JSON_KEYWORDS, "name, type");
		props.put(Config.REPORT_JSON_ROOT_ARRAY_KEY, "animals");
		props.put(Config.REPORT_JSON_RAW_DATA_FILENAME, "src/test/resources/JsonReportTest-general1.json");
		try {
			Config c = new Config(props);
			c.checkConfigsJsonReport();
			assertTrue("Config is expected to be valid, but is not.", c.isValidConfig());

			JsonElement root = FileUtils.parseAsJson(c, Config.REPORT_JSON_RAW_DATA_FILENAME);
			JsonElement animalsArray = root.getAsJsonObject().getAsJsonArray("animals");
			assertNotNull(animalsArray);
			assertEquals(((JsonArray) animalsArray).size(), 2);
			assertTrue(root.toString().equals("{\"animals\":[{\"name\":\"Tommy\",\"type\":\"cat\",\"noise-level\":\"Loud\",\"birth-year\":2017},{\"name\":\"Fido\",\"type\":\"dog\",\"noise-level\":\"Very Loud\",\"birth-year\":2014}]}"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCreateCsvReportFromJsonFile() {
		Properties props = new Properties();
		props.put(Config.REPORT_JSON_KEYWORDS, "name, type");
		props.put(Config.REPORT_JSON_ROOT_ARRAY_KEY, "animals");
		props.put(Config.REPORT_JSON_RAW_DATA_FILENAME, "src/test/resources/JsonReportTest-general2.json");

		try {
			Config c = new Config(props);
			c.checkConfigsJsonReport();
			assertTrue("Config is expected to be valid, but is not.", c.isValidConfig());

			JsonReportDriver jrd = new JsonReportDriver();
			List<String> results = jrd.execute(c, null);

			assertEquals(results.size(), 5);
			assertEquals(results.get(0), "Location,name, type");
			assertEquals(results.get(1), "JsonReportTest-general2.json,Tommy,cat,");
			assertEquals(results.get(2), "JsonReportTest-general2.json,Fido,dog,");
			assertEquals(results.get(3), "JsonReportTest-general2.json,Gordo,,");
			assertEquals(results.get(4), "JsonReportTest-general2.json,Billy,asdf|qwer,");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
