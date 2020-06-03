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
import org.apereo.openequella.tools.toolbox.checkFiles.ResultComparison;
import org.apereo.openequella.tools.toolbox.utils.CheckFilesUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CheckFilesCompareTests {
	private static final Logger LOGGER = LogManager.getLogger(CheckFilesUtils.class);
	private static final String BASE = "src/test/resources/junit_env/checkfiles-reports/resultFiles/";
	private static final String BASE_TO_SORT = "src/test/resources/junit_env/checkfiles-reports/resultFilesToSort/";

	@Test
	public void testFindOnlyInFirst() {
		Set<String> s1 = new HashSet<String>();
		s1.add("a");
		s1.add("b");
		s1.add("c");
		s1.add("f");
		Set<String> s2 = new HashSet<String>();
		s2.add("c");
		s2.add("d");
		s2.add("e");
		s2.add("f");
		Collection<String> onlyInS1 = CheckFilesUtils.findOnlyInFirst(s1, s2);
		assertTrue(onlyInS1.size() == 2);
		assertTrue(onlyInS1.contains("a"));
		assertTrue(onlyInS1.contains("b"));
		Collection<String> onlyInS2 = CheckFilesUtils.findOnlyInFirst(s2, s1);
		assertTrue(onlyInS2.size() == 2);
		assertTrue(onlyInS2.contains("d"));
		assertTrue(onlyInS2.contains("e"));
	}

	@Test
	public void testLooseCompareSameFile() {
		try {
			File report1 = new File(BASE + "oEQ_checkFiles_all_stats_A.csv");
			LOGGER.debug("report1 - {}", report1.getAbsolutePath());
			File report2 = new File(BASE + "oEQ_checkFiles_all_stats_A.csv");
			LOGGER.debug("report2 - {}", report2.getAbsolutePath());
			assertTrue(report1.exists());
			assertTrue(report2.exists());

			ResultComparison cr = CheckFilesUtils.looseCompare(report1,
							report2);
			assertNotNull(cr);
			assertTrue(cr.areReportsEqual());
			assertEquals(cr.getOnlyInFirst().size(), 0);
			assertEquals(cr.getOnlyInSecond().size(), 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testLooseCompareNewRowInFirst() {
		try {
			File report1 = new File(BASE + "oEQ_checkFiles_all_stats_A.csv");
			LOGGER.debug("report1 - {}", report1.getAbsolutePath());
			File report2 = new File(BASE + "oEQ_checkFiles_all_stats_B_row1_absent.csv");
			LOGGER.debug("report2 - {}", report2.getAbsolutePath());
			assertTrue(report1.exists());
			assertTrue(report2.exists());

			ResultComparison cr = CheckFilesUtils.looseCompare(report1,
							report2);
			assertNotNull(cr);
			assertTrue(!cr.areReportsEqual());
			assertEquals(cr.getOnlyInFirst().size(), 1);
			assertEquals(cr.getOnlyInSecond().size(), 0);
			String onlyInFirst = cr.getOnlyInFirst().get(0).getPrimaryKey();

			assertEquals("checkFilesTesting-8428e745-4cdb-4427-8b63-570c81401b2a-1-[[Attachment UUID not set]]-No Att",	onlyInFirst);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testLooseCompareNewRowInSecond() {
		try {
			File report1 = new File(BASE + "oEQ_checkFiles_all_stats_B_row1_absent.csv");
			LOGGER.debug("report1 - {}", report1.getAbsolutePath());
			File report2 = new File(BASE + "oEQ_checkFiles_all_stats_A.csv");
			LOGGER.debug("report2 - {}", report2.getAbsolutePath());
			assertTrue(report1.exists());
			assertTrue(report2.exists());

			ResultComparison cr = CheckFilesUtils.looseCompare(report1,
							report2);
			assertNotNull(cr);
			assertTrue(!cr.areReportsEqual());
			assertEquals(cr.getOnlyInFirst().size(), 0);
			assertEquals(cr.getOnlyInSecond().size(), 1);
			String onlyInSecond = cr.getOnlyInSecond().get(0).getPrimaryKey();

			assertEquals("checkFilesTesting-8428e745-4cdb-4427-8b63-570c81401b2a-1-[[Attachment UUID not set]]-No Att",	onlyInSecond);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testLooseCompareNewDifferentRowsInBoth() {
		try {
			File report1 = new File(BASE + "oEQ_checkFiles_all_stats_B_row1_absent.csv");
			LOGGER.debug("report1 - {}", report1.getAbsolutePath());
			File report2 = new File(BASE + "oEQ_checkFiles_all_stats_C_row5_absent.csv");
			LOGGER.debug("report2 - {}", report2.getAbsolutePath());
			assertTrue(report1.exists());
			assertTrue(report2.exists());

			ResultComparison cr = CheckFilesUtils.looseCompare(report1,
							report2);
			assertNotNull(cr);
			assertTrue(!cr.areReportsEqual());
			assertEquals(cr.getOnlyInFirst().size(), 1);
			assertEquals(cr.getOnlyInSecond().size(), 1);
			String onlyInFirst = cr.getOnlyInFirst().get(0).getPrimaryKey();
			assertEquals("checkFilesTesting-bd554963-2c55-4da9-9a9f-940ae41b6be1-1-9d98468c-a040-4dfb-aa94-5500a6ccf467-Present",	onlyInFirst);
			String onlyInSecond = cr.getOnlyInSecond().get(0).getPrimaryKey();
			assertEquals("checkFilesTesting-8428e745-4cdb-4427-8b63-570c81401b2a-1-[[Attachment UUID not set]]-No Att",	onlyInSecond);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindPreviousErrorStats() {
		try {
			Config.reset();
			Config.getInstance().setConfig(Config.CF_OUTPUT_FOLDER, BASE_TO_SORT);
			Config.getInstance().setConfig(Config.CF_ADOPTER_NAME, "Apereo");

			File latestFile = CheckFilesUtils.findPreviousErrorStats();
			assertEquals("oEQ_checkFiles_DB_ALL_ITEMS_ALL_ATTS_Apereo_error_stats_2019-08-21.18.18.48.csv",	latestFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
