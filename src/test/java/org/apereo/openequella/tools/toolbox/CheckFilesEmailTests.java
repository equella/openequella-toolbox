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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.checkFiles.ReportManager;
import org.apereo.openequella.tools.toolbox.utils.Check;
import org.apereo.openequella.tools.toolbox.utils.EmailUtils;
import org.junit.Test;

public class CheckFilesEmailTests {

  private static Logger LOGGER = LogManager.getLogger(CheckFilesEmailTests.class);
  private static final String COLL_ID_1_MISSING_ATTS = "3615";
  private static final String COLL_ID_2_MISSING_ATTS = "3123";
  private static final String COLL_ID_3_NO_MISSING_ATTS = "3626";
  private static final String INST_SHORTNAME = "instXe";

  // Unused - consider removing
  //	@Test
  //	public void testEmailSettingsSmokeTest() {
  //		EmailUtils.resetState();
  //		Properties envSpecific = TestUtils.loadEnvSpecificDefaults();
  //		TestUtils.buildCheckFilesGeneralDbProps();
  //		Config.getInstance().setConfig(Config.CF_EMAIL_MODE,
  // Config.CheckFilesEmailMode.NORMAL.name());
  //		TestUtils.buildEmailProps();
  //		Config.getInstance().checkConfigsCheckFiles();
  //
  //		assertTrue(CheckFilesDriver.setup(true));
  //		final String body = "Resultant testEmailSettingsE2E email - " + (new Date());
  //		EmailUtils.execute("TEXT",
  //			Config.get(Config.CF_EMAIL_RECIPIENTS),
  //		"This is a test message for the JUnit test testEmailSettingsE2E",
  //			body);
  //		if (ReportManager.getInstance().hasFatalErrors()) {
  //			fail(ReportManager.getInstance().getLastFatalError());
  //		}
  //		assertEquals("TEXT", EmailUtils.getState().getLastRawType());
  //		assertEquals(Config.get(Config.CF_EMAIL_RECIPIENTS),
  // EmailUtils.getState().getLastRawToAddresses());
  //		assertEquals("This is a test message for the JUnit test testEmailSettingsE2E",
  // EmailUtils.getState().getLastSubject());
  //		assertEquals(body, EmailUtils.getState().getLastBody());
  //		assertEquals(true, EmailUtils.getState().isLastSuccess());
  //	}

  /** Should result in a 'Critical Failure' email about the DB connection. */
  @Test
  public void testFailFastEmailWithBadDbConnectionSpecified() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_DB_URL, "asdf");
    TestUtils.setDefaultEmailProps();

    assertTrue(CheckFilesDriver.setup(true));
    assertFalse(CheckFilesDriver.run());
    assertTrue(ReportManager.getInstance().hasFatalErrors());
    assertTrue(ReportManager.getInstance().failFast());
    assertEquals("HTML", EmailUtils.getState().getLastRawType());
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    assertThat(
        EmailUtils.getState().getLastSubject(),
        startsWith("CRITICAL FAILURE: oEQ-Toolbox:CheckFiles v"));
    assertThat(EmailUtils.getState().getLastSubject(), endsWith("Report"));
    assertThat(
        EmailUtils.getState().getLastBody(),
        startsWith("<html><body><h2>oEQ-Toolbox:CheckFiles v"));
    assertThat(
        EmailUtils.getState().getLastBody(),
        endsWith(
            "Report</h2><div><span style='color:red'><b>Report run failed due to an unrecoverable error.</b></span></div><div>Critical errors gathered:</div><div><ul><li>Unable to connect to the DB [asdf] with username [equellauser] and a password of length [9]</li></ul></div><div>Please check the Toolbox logs for more details...</div><br/><br/><div><i>Note:  This report is provided as a guide to the results of the CheckFiles utility.  While efforts have been taken to provide a quality utility to report on the attachment status of openEQUELLA items, this utility is provided AS-IS, with no warranty or promise of support.</i></div></body></html>"));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  /** Should result in a 'Critical Failure' email about the DB connection. */
  @Test
  public void testFailFastNoEmailWithBadEmailServerSpecified() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    TestUtils.setDefaultEmailProps();
    Config.getInstance().setConfig(Config.DEV_MODE, "");

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(ReportManager.getInstance().finalizeReporting());
    assertTrue(ReportManager.getInstance().hasFatalErrors());
    for (String msg : ReportManager.getInstance().getFatalErrors()) {
      LOGGER.info(msg);
    }
    assertTrue(
        ReportManager.getInstance()
            .getLastFatalError()
            .startsWith(
                "ERROR - An exception occurred in trying to send the email:  Couldn't connect to host, port: not.exists.apereo.org"));
    if (EmailUtils.getState() != null) {
      LOGGER.debug(EmailUtils.getState());
    }
    assertTrue(EmailUtils.getState() != null);
    assertEquals(false, EmailUtils.getState().isLastSuccess());
  }

  @Test
  public void testNormalEmailNoNewMissingAttachments() throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, INST_SHORTNAME);
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    TestUtils.setDefaultEmailProps();
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
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    LOGGER.debug(EmailUtils.getState().toString());
    assertThat(EmailUtils.getState().getLastSubject(), startsWith("oEQ-Toolbox:CheckFiles v"));
    assertThat(EmailUtils.getState().getLastSubject(), endsWith("Report"));
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(2, 2, 1, false, null, null)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  @Test
  public void testNormalEmailNewMissingAttachments() throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, INST_SHORTNAME);
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    TestUtils.setDefaultEmailProps();
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
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    LOGGER.debug(EmailUtils.getState().toString());
    assertThat(EmailUtils.getState().getLastSubject(), startsWith("oEQ-Toolbox:CheckFiles v"));
    assertThat(EmailUtils.getState().getLastSubject(), endsWith("Report"));
    List<String> newMissing = new ArrayList<>();
    newMissing.add(
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d8d002f7-63db-4c0c-bf9e-38d6334af37e,1,LIVE,FILE,3b24bdd0-f4d7-4bb3-a1cc-383faea4f981,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.txt\"\",");
    newMissing.add(
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8f5254e0-5b0f-4cfe-9af0-9ce2c5d784c5,1,LIVE,FILE,0578860d-65d7-44bf-8614-08469f268721,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"syllabus.html\"\",");
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(7, 7, 3, true, newMissing, null)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  private String buildExpectedBody(
      int numOfItems,
      int numOfAtts,
      int numOfMissingAtts,
      boolean changed,
      List<String> newMissing,
      List<String> newResolved) {
    StringBuffer sb = new StringBuffer();
    sb.append("<li><b>CheckFiles Type</b> - DB_ALL_ITEMS_ALL_ATTS</li>");
    sb.append("<li><b>Adopter Name</b> - acme</li>");
    sb.append("<li><b># Of Items Checked</b> - " + numOfItems + "</li>");
    sb.append("<li><b># Of Attachments Checked</b> - " + numOfAtts + "</li>");
    sb.append("<li><b># Of Attachments Missing</b> - " + numOfMissingAtts + "</li></ul>");
    // enhance to build errors
    sb.append("<div style='color:blue'><b>oEQ-Toolbox:CheckFiles Errors:</b></div>");
    sb.append("<ul><div>No fatal errors occurred.</div></ul>");
    sb.append("<div style='color:blue'><b>Details of comparison report:</b></div>");
    sb.append("<div>Comparison of <i>missing</i> attachments from previous run to current run: ");
    if (changed) {
      sb.append("<span style='color:red'><b>CHANGED</b></span></div>");
      sb.append(
          "<br/><div><i><b>Placement legend: </b> Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath</i></div><br/>");

      if (!Check.isEmpty(newResolved)) {
        sb.append("<div>Attachments that are no longer missing:</div>");
        sb.append("<ul>");
        for (String s : newResolved) {
          sb.append("<li><span style='color:blue'>");
          sb.append(s);
          sb.append("</span></li>");
        }
        sb.append("</ul>");
      }

      if (!Check.isEmpty(newMissing)) {
        sb.append("<div>Attachments that are now missing:</div>");
        sb.append("<ul>");
        for (String s : newMissing) {
          sb.append("<li><span style='color:red'>");
          sb.append(s);
          sb.append("</span></li>");
        }
        sb.append("</ul>");
      }
    } else {
      sb.append("<span style='color:blue'><b>NO CHANGE</b></span></div>");
    }
    sb.append("<div><b>Comparison file for previous run");
    return sb.toString();
  }

  @Test
  public void testOnlyMissingAttachmentsEmailNoNewMissingAttachments() throws InterruptedException {
    Config.reset();
    EmailUtils.resetState();
    TestUtils.buildCheckFilesGeneralDbProps();
    TestUtils.setDefaultEmailProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, INST_SHORTNAME);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    Thread.sleep(2000); // avoid name collision on the reports.
    Config.getInstance()
        .setConfig(
            Config.CF_EMAIL_MODE,
            Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());
    if (EmailUtils.getState() != null) {
      LOGGER.debug(EmailUtils.getState());
    }
    assertTrue(EmailUtils.getState() == null);
  }

  @Test
  public void testOnlyMissingAttachmentsEmailNewMissingAttachments() throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    TestUtils.setDefaultEmailProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, INST_SHORTNAME);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    Config.getInstance()
        .setConfig(
            Config.CF_EMAIL_MODE,
            Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "");

    Thread.sleep(2000); // avoid name collision on the reports.

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    if (EmailUtils.getState() != null) {
      LOGGER.debug(EmailUtils.getState().toString());
    }
    assertEquals("HTML", EmailUtils.getState().getLastRawType());
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
    assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
    List<String> newMissing = new ArrayList<>();
    newMissing.add(
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d8d002f7-63db-4c0c-bf9e-38d6334af37e,1,LIVE,FILE,3b24bdd0-f4d7-4bb3-a1cc-383faea4f981,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.txt\"\",");
    newMissing.add(
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8f5254e0-5b0f-4cfe-9af0-9ce2c5d784c5,1,LIVE,FILE,0578860d-65d7-44bf-8614-08469f268721,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"syllabus.html\"\",");
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(7, 7, 3, true, newMissing, null)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  @Test
  public void testNormalEmailOldMissingResolved() throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    TestUtils.setDefaultEmailProps();
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_3_NO_MISSING_ATTS);

    Thread.sleep(2000); // avoid name collision on the reports.

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    assertEquals("HTML", EmailUtils.getState().getLastRawType());
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    LOGGER.debug(EmailUtils.getState().toString());
    assertTrue(EmailUtils.getState().getLastSubject().startsWith("oEQ-Toolbox:CheckFiles v"));
    assertTrue(EmailUtils.getState().getLastSubject().endsWith("Report"));
    List<String> newResolved = new ArrayList<>();
    newResolved.add(
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,12f54be4-e536-482d-a7fe-f262ac086b28,1,LIVE,FILE,66c08dc8-d86f-49eb-84df-20e17e3483d2,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"keyword.txt\"\",");
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(1, 1, 0, true, null, newResolved)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  @Test
  public void testOnlyMissingAttachmentsEmailOldMissingResolved() throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    TestUtils.setDefaultEmailProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    Config.getInstance()
        .setConfig(
            Config.CF_EMAIL_MODE,
            Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_3_NO_MISSING_ATTS);

    Thread.sleep(2000); // avoid name collision on the reports.

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    if (EmailUtils.getState() != null) {
      LOGGER.debug(EmailUtils.getState().toString());
    }
    assertThat(EmailUtils.getState().getLastSubject(), startsWith("oEQ-Toolbox:CheckFiles v"));
    assertThat(EmailUtils.getState().getLastSubject(), endsWith("Report"));
    List<String> newResolved = new ArrayList<>();
    newResolved.add(
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,12f54be4-e536-482d-a7fe-f262ac086b28,1,LIVE,FILE,66c08dc8-d86f-49eb-84df-20e17e3483d2,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"keyword.txt\"\",");
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(1, 1, 0, true, null, newResolved)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  @Test
  public void testNormalEmailNewMissingAttachmentsOldMissingResolved() throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    TestUtils.setDefaultEmailProps();
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NORMAL.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_2_MISSING_ATTS);

    Thread.sleep(2000); // avoid name collision on the reports.

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    assertEquals("HTML", EmailUtils.getState().getLastRawType());
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    LOGGER.debug(EmailUtils.getState().toString());
    assertThat(EmailUtils.getState().getLastSubject(), startsWith("oEQ-Toolbox:CheckFiles v"));
    assertThat(EmailUtils.getState().getLastSubject(), endsWith("Report"));
    List<String> newResolved = new ArrayList<>();
    newResolved.add(
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,12f54be4-e536-482d-a7fe-f262ac086b28,1,LIVE,FILE,66c08dc8-d86f-49eb-84df-20e17e3483d2,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"keyword.txt\"\",");
    List<String> newMissing = new ArrayList<>();
    newMissing.add(
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d8d002f7-63db-4c0c-bf9e-38d6334af37e,1,LIVE,FILE,3b24bdd0-f4d7-4bb3-a1cc-383faea4f981,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"test.txt\"\",");
    newMissing.add(
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8f5254e0-5b0f-4cfe-9af0-9ce2c5d784c5,1,LIVE,FILE,0578860d-65d7-44bf-8614-08469f268721,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"syllabus.html\"\",");
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(4, 4, 2, true, newMissing, newResolved)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }

  @Test
  public void testOnlyMissingAttachmentsEmailNewMissingAttachmentsOldMissingResolved()
      throws InterruptedException {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    TestUtils.setDefaultEmailProps();
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_1_MISSING_ATTS);
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, INST_SHORTNAME);
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().setConfig(Config.CF_COMPARE_MISSING_ATTS_AFTER_RUN, "true");

    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    Config.getInstance()
        .setConfig(
            Config.CF_EMAIL_MODE,
            Config.CheckFilesEmailMode.ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, COLL_ID_3_NO_MISSING_ATTS);

    Thread.sleep(2000); // avoid name collision on the reports.

    assertTrue(CheckFilesDriver.setup());
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    assertEquals("HTML", EmailUtils.getState().getLastRawType());
    assertEquals(
        Config.get(Config.CF_EMAIL_RECIPIENTS), EmailUtils.getState().getLastRawToAddresses());
    LOGGER.debug(EmailUtils.getState().toString());
    assertThat(EmailUtils.getState().getLastSubject(), containsString("oEQ-Toolbox:CheckFiles v"));
    assertThat(EmailUtils.getState().getLastSubject(), containsString("Report"));
    List<String> newResolved = new ArrayList<>();
    newResolved.add(
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,12f54be4-e536-482d-a7fe-f262ac086b28,1,LIVE,FILE,66c08dc8-d86f-49eb-84df-20e17e3483d2,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\"keyword.txt\"\",");
    assertThat(
        EmailUtils.getState().getLastBody(),
        containsString(buildExpectedBody(1, 1, 0, true, null, newResolved)));
    assertEquals(true, EmailUtils.getState().isLastSuccess());
  }
}
