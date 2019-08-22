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
import org.apereo.openequella.tools.toolbox.checkFiles.ReportManager;
import org.apereo.openequella.tools.toolbox.utils.EmailUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CheckFilesEmailTests {

	private static Logger LOGGER = LogManager.getLogger(CheckFilesEmailTests.class);

	@Test
	public void testEmailSettingsSmokeTest() {
		EmailUtils.resetState();
		Properties envSpecific = TestUtils.loadEnvSpecificDefaults();
		TestUtils.buildCheckFilesGeneralDbProps();
		Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
		TestUtils.buildEmailProps();
		Config.getInstance().checkConfigsCheckFiles();

		assertTrue(CheckFilesDriver.setup(true));
		final String body = "Resultant testEmailSettingsE2E email - " + (new Date());
		EmailUtils.execute("TEXT",
			Config.get(Config.CF_EMAIL_RECIPIENTS),
		"This is a test message for the JUnit test testEmailSettingsE2E",
			body);
		if (ReportManager.getInstance().hasFatalErrors()) {
			fail(ReportManager.getInstance().getLastFatalError());
		}
		assertEquals("TEXT", EmailUtils.getState().getLastRawType());
		assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
		assertEquals("This is a test message for the JUnit test testEmailSettingsE2E", EmailUtils.getState().getLastSubject());
		assertEquals(body, EmailUtils.getState().getLastBody());
		assertEquals(true, EmailUtils.getState().isLastSuccess());
	}

	/**
	 * Should result in a 'Critical Failure' email about the DB connection.
	 */
	@Test
	public void testFailFastEmailWithBadDbConnectionSpecified() {
		Config.reset();
		TestUtils.buildCheckFilesGeneralDbProps();
		Config.getInstance().setConfig(Config.CF_DB_URL, "asdf");
		Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
		TestUtils.buildEmailProps();

		assertTrue(CheckFilesDriver.setup(true));
		assertFalse(CheckFilesDriver.run());
		assertTrue(ReportManager.getInstance().hasFatalErrors());
		assertTrue(ReportManager.getInstance().failFast());
		assertEquals("HTML", EmailUtils.getState().getLastRawType());
		assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
		assertTrue(EmailUtils.getState().getLastSubject().startsWith("CRITICAL FAILURE: oEQ-Toolbox:CheckFiles v"));
		assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
		assertTrue(EmailUtils.getState().getLastBody().startsWith("<html><body><h2>oEQ-Toolbox:CheckFiles v"));
		assertTrue(EmailUtils.getState().getLastBody().endsWith("Report</h2><div><span style='color:red'><b>Report run failed due to an unrecoverable error.</b></span></div><div>Critical errors gathered:</div><div><ul><li>Unable to connect to the DB [asdf] with username [equellauser] and a password of length [9]</li></ul></div><div>Please check the Toolbox logs for more details...</div><br/><br/><div><i>Note:  This report is provided as a guide to the results of the CheckFiles utility.  While efforts have been taken to provide a quality utility to report on the attachment status of openEQUELLA items, this utility is provided AS-IS, with no warranty or promise of support.</i></div></body></html>"));
		assertEquals(true, EmailUtils.getState().isLastSuccess());
	}

	/**
	 * Should result in a 'Critical Failure' email about the DB connection.
	 */
	@Test
	public void testFailFastNoEmailWithBadEmailServerSpecified() {
		Config.reset();
		TestUtils.buildCheckFilesGeneralDbProps();
		TestUtils.buildEmailProps();
		Config.getInstance().setConfig(Config.EMAIL_SERVER, "asdf");
		Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());

		assertTrue(CheckFilesDriver.setup(true));
		assertTrue(CheckFilesDriver.run());
		assertTrue(ReportManager.getInstance().finalizeReporting());
		assertTrue(ReportManager.getInstance().hasFatalErrors());
		assertTrue(ReportManager.getInstance().getLastFatalError().startsWith("ERROR - An exception occurred in trying to send the email:  Couldn't connect to host, port: asdf"));
		if(EmailUtils.getState() != null) {
			LOGGER.debug(EmailUtils.getState());
		}
		assertTrue(EmailUtils.getState() != null);
		assertEquals(false, EmailUtils.getState().isLastSuccess());
	}


	@Test
	public void testNormalEmailNoNewMissingAttachments() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			assertEquals("HTML", EmailUtils.getState().getLastRawType());
			assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
			LOGGER.debug(EmailUtils.getState().toString());
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 7</li><li><b># Of Attachments Checked</b> - 12</li><li><b># Of Attachments Missing</b> - 1</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:blue'><b>NO CHANGE</b></span></div>"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testNormalEmailNewMissingAttachments() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "");

			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			assertEquals("HTML", EmailUtils.getState().getLastRawType());
			assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
			LOGGER.debug(EmailUtils.getState().toString());
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 11</li><li><b># Of Attachments Checked</b> - 20</li><li><b># Of Attachments Missing</b> - 2</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:red'><b>CHANGED</b></span></div><br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/><div>Attachments that are now missing:</div><ul><li><span style='color:red'>checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,FILE,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div><b>Comparison file for previous run"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testOnlyMissingAttachmentsEmailNoNewMissingAttachments() {
		try {
			Config.reset();
			EmailUtils.resetState();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Thread.sleep(2000); // avoid name collision on the reports.
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());
			if(EmailUtils.getState() != null) {
				LOGGER.debug(EmailUtils.getState());
			}
			assertTrue(EmailUtils.getState() == null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testOnlyMissingAttachmentsEmailNewMissingAttachments() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "");

			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			if(EmailUtils.getState() != null) {
				LOGGER.debug(EmailUtils.getState().toString());
			}
			assertEquals("HTML", EmailUtils.getState().getLastRawType());
			assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 11</li><li><b># Of Attachments Checked</b> - 20</li><li><b># Of Attachments Missing</b> - 2</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:red'><b>CHANGED</b></span></div><br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/><div>Attachments that are now missing:</div><ul><li><span style='color:red'>checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,FILE,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div><b>Comparison file for previous run"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testNormalEmailOldMissingResolved() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "1542");

			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			assertEquals("HTML", EmailUtils.getState().getLastRawType());
			assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
			LOGGER.debug(EmailUtils.getState().toString());
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 0</li><li><b># Of Attachments Checked</b> - 0</li><li><b># Of Attachments Missing</b> - 0</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:red'><b>CHANGED</b></span></div><br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/><div>Attachments that are no longer missing:</div><ul><li><span style='color:blue'>checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,FILE,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div><b>Comparison file for previous run"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testOnlyMissingAttachmentsEmailOldMissingResolved() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "1542");

			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			if(EmailUtils.getState() != null) {
				LOGGER.debug(EmailUtils.getState().toString());
			}
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 0</li><li><b># Of Attachments Checked</b> - 0</li><li><b># Of Attachments Missing</b> - 0</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:red'><b>CHANGED</b></span></div><br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/><div>Attachments that are no longer missing:</div><ul><li><span style='color:blue'>checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,FILE,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div><b>Comparison file for previous run"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testNormalEmailNewMissingAttachmentsOldMissingResolved() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "1485");

			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			assertEquals("HTML", EmailUtils.getState().getLastRawType());
			assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
			LOGGER.debug(EmailUtils.getState().toString());
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 1</li><li><b># Of Attachments Checked</b> - 4</li><li><b># Of Attachments Missing</b> - 1</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:red'><b>CHANGED</b></span></div><br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/><div>Attachments that are no longer missing:</div><ul><li><span style='color:blue'>checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,FILE,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div>Attachments that are now missing:</div><ul><li><span style='color:red'>checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,FILE,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div><b>Comparison file for previous run"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testOnlyMissingAttachmentsEmailNewMissingAttachmentsOldMissingResolved() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
			Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

			TestUtils.buildEmailProps();
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "1485");

			Thread.sleep(2000); // avoid name collision on the reports.

			assertTrue(CheckFilesDriver.setup());
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			assertEquals("HTML", EmailUtils.getState().getLastRawType());
			assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
			LOGGER.debug(EmailUtils.getState().toString());
			assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
			assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
			assertTrue(EmailUtils.getState().getLastBody().contains("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li><li><b>Adopter Name</b> - acme</li><li><b># Of Items Checked</b> - 1</li><li><b># Of Attachments Checked</b> - 4</li><li><b># Of Attachments Missing</b> - 1</li></ul><div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div><ul><div>No fatal errors occurred.</div></ul><div style='color:blue'><b>Details of comparison report:</b></div><div>Comparison of <i>missing</i> attachments from previous run to current run: <span style='color:red'><b>CHANGED</b></span></div><br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/><div>Attachments that are no longer missing:</div><ul><li><span style='color:blue'>checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,FILE,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div>Attachments that are now missing:</div><ul><li><span style='color:red'>checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,FILE,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.html\"\",</span></li></ul><div><b>Comparison file for previous run"));
			assertEquals(true, EmailUtils.getState().isLastSuccess());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
