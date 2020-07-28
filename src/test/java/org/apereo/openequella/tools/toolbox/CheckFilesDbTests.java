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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.checkFiles.ReportManager;
import org.junit.Test;

/**
 * Uses the advanced filestore in optional-config: filestore.advanced = true
 * filestore.additional.ids = 2 filestore.additional.2.path = {adv-filestore-loc}/oeq-filestore-2/
 * filestore.additional.2.name = Filestore ID#2
 */
public class CheckFilesDbTests {
  private static Logger LOGGER = LogManager.getLogger(Config.class);

  private static final int INST_XA_NUM_OF_ALL_RESULTS = 22;
  private static final int INST_XA_NUM_OF_ERR_RESULTS = 20;
  private static final int INST_XB_NUM_OF_ALL_RESULTS = 24;
  private static final int INST_XB_NUM_OF_ERR_RESULTS = 22;
  private static final int INST_XC_COLL_FS2_NUM_ALL_RESULTS = 29;
  private static final int INST_XC_COLL_FS2_NUM_ERR_RESULTS = 20;
  private static final int INST_XD_COLL_LR_NUM_ALL_RESULTS = 30;
  private static final int INST_XD_COLL_LR_NUM_ERR_RESULTS = 20;
  private static final int INST_XF_NUM_OF_ALL_RESULTS = 143;
  private static final int INST_XF_NUM_OF_ERR_RESULTS = 39;
  private static final int INST_ALL_NUM_OF_ALL_RESULTS = 177; // 54
  private static final int INST_ALL_NUM_OF_ERR_RESULTS = 44;
  private static final int OFFSET_FOR_QUERY_STATEMENT = 3;

  @Test
  public void testGeneralInstitutionInstXa() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXa");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsInstitutionXaAllCollections(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XA_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,6");
  }

  @Test
  public void testGeneralInstitutionInstXb() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXb");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsInstitutionXbAllCollections(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XB_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,8");
  }

  @Test
  public void testSpecialCharacters() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXc");
    // Learning Resources
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "1874");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsInstitutionXcFS2Collection(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XC_COLL_FS2_NUM_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,5");
  }

  @Test
  public void testFilterByInstitutionAndCollection() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXd");
    // Learning Resources
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "2476");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsFilterByInstitutionXdAndCollection(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XD_COLL_LR_NUM_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,14");
  }

  @Test
  public void testFilterByInstitutionAndCollectionBatchedBy2() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXd");
    // Learning Resources
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "2476");
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "2");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsFilterByInstitutionXdAndCollection(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XD_COLL_LR_NUM_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,19");
  }

  @Test
  public void testFilterByInstitutionAndCollectionBatchedBy9() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXd");
    // Learning Resources
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "2476");
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "9");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsFilterByInstitutionXdAndCollection(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XD_COLL_LR_NUM_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,16");
  }

  @Test
  public void testFilterByInstitutionAndCollectionBatchedBy100() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXd");
    // Learning Resources
    Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "2476");
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "100");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsFilterByInstitutionXdAndCollection(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XD_COLL_LR_NUM_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,15");
  }

  @Test
  public void testAllInstitutions() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsAllInstitutions(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_ALL_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,46");
  }

  @Test
  public void testAllInstitutionsConfirmInlineBatchedByDefault() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance()
        .setConfig(
            Config.CF_MODE,
            Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS_CONFIRM_INLINE.name());
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsAllInstitutions(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        "# Of queries ran,49",
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_ALL_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT));
  }

  @Test
  public void testAllInstitutionsConfirmInlineBatchedBy1() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance()
        .setConfig(
            Config.CF_MODE,
            Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS_CONFIRM_INLINE.name());
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "1");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsAllInstitutions(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        "# Of queries ran,88",
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_ALL_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT));
  }

  @Test
  public void testAllInstitutionsBatchBy1() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "1");

    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsAllInstitutions(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_ALL_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,88");
  }

  @Test
  public void testAllInstitutionsBatchBy10() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance()
        .setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
    Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "10");

    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsAllInstitutions(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_ALL_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT),
        "# Of queries ran,51");
  }

  @Test
  public void testItemsInSingleQueryInstitutionFilterBad() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "swirl");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertFalse(CheckFilesDriver.run());
    assertTrue(ReportManager.getInstance().hasFatalErrors());
    assertEquals(
        "Utility should state the short to filter by is not in the cache.",
        ReportManager.getInstance().getFatalErrors().get(0),
        "The institution shortname to filter by [swirl] is not in the institution cache.");
  }

  @Test
  public void testZipFilesAndMyPages() {
    Config.reset();
    TestUtils.buildCheckFilesGeneralDbProps();
    Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
    Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "instXf");
    Config.getInstance().checkConfigsCheckFiles();

    assertTrue(CheckFilesDriver.setup(true));
    assertTrue(CheckFilesDriver.run());
    assertTrue(CheckFilesDriver.finalizeRun());

    TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
    TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
    confirmResultsInstitutionXf(
        ReportManager.getInstance().getAllStatsWriterList(),
        ReportManager.getInstance().getErrorStatsWriterList());
    assertEquals(
        "# Of queries ran,20",
        ReportManager.getInstance()
            .getAllStatsWriterList()
            .get(INST_XF_NUM_OF_ALL_RESULTS - OFFSET_FOR_QUERY_STATEMENT));
  }

  private void confirmResultsAllInstitutions(List<String> allOriginal, List<String> errOriginal) {
    assertEquals(INST_ALL_NUM_OF_ALL_RESULTS, allOriginal.size());
    // This is the primary flow that will change when new items / institutions are added to the test
    // institution.
    // Sort the results in a sandbox to make checking results a bit simplier
    List<String> all = new ArrayList<>();
    all.addAll(allOriginal);
    Collections.sort(all.subList(7, all.size()));
    List<String> err = new ArrayList<>();
    err.addAll(errOriginal);
    Collections.sort(err.subList(7, err.size()));
    int idx = 7;
    assertEquals(all.get(idx++), "# Of ALL Attachments,157");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,0");
    assertEquals(all.get(idx++), "# Of Items affected,12");
    assertEquals(all.get(idx++), "# Of Items,42");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,24");
    idx += 8; // Skip the non-critical rows
    assertEquals(
        all.get(idx++),
        "instXa,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,0a16eb9d-88af-4951-82bb-40da96516a2f,1,LIVE,file,16fd1eea-86bd-46a4-ae7b-c4b8686bc5dd,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");
    assertEquals(
        all.get(idx++),
        "instXa,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,f61dc75f-62ca-4cbb-abf2-89bd489cb875,1,LIVE,file,d3e8a4db-f36d-4ff7-a074-728ade56f1d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        all.get(idx++),
        "instXb,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,711de2a7-62e9-41b1-93b9-95cc794e6f7a,1,LIVE,file,fb442db8-71f2-408d-b03b-679c4e2d7220,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(
        all.get(idx++),
        "instXb,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d70fa56f-214a-4233-923c-1df83a7c48c1,1,LIVE,file,12abbdce-ffea-4627-aa37-c890cd860a47,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
    assertEquals(
        all.get(idx++),
        "instXb,df727509-77f7-4ce6-a9cb-2c5385565358,3a635ac4-3966-4964-b283-f597d9f11513,1,LIVE,file,e6cd0f56-b3d5-4a5d-9750-9a2119a894ca,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXb,df727509-77f7-4ce6-a9cb-2c5385565358,4a0d9c5d-0197-45b8-8c48-7b947ae9c499,1,LIVE,file,f56b2e48-0c68-42e1-8f79-6aa206c0a259,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        all.get(idx++),
        "instXc,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,471ba862-8ab9-4b8f-92f5-0f53ad2cad4a,1,LIVE,file,0ddbd2bb-80f5-4ce4-ac26-590cb2e7a03f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,2395ec79-9925-46d6-af54-e6eb21dbfe0a,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename-with++.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,39d8186f-6091-4c05-b41c-a907c63f8040,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"áéíóú¿¡üñ.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,416eb301-0ae6-4cb8-925f-02c6cdc61a7f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename with spaces.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,69885964-17f4-47a0-b417-06c1ede16617,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"slashes \\ are fun.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,6ee078cf-5725-438f-a3e0-937130d50277,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"传傳败敗见見.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,836a7eb4-4897-44cc-9609-e843cbd46bba,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"ascii char \\ test +  . ~ $|=><`#^%&:}{ ]*[\")'(!-;?_,.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,bd2832e9-7d33-4ed6-8b85-15578f629ff5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"semi colons ; test.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,e81439a0-e948-4286-963a-008c5119c2c1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\" الأَبْجَدِيَّة.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,ef97a206-79c8-4235-b078-eaf58025ab29,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename.log\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,0b68c63f-afe7-4f35-b91c-f38609500384,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,19fc300e-290d-49aa-9023-63b6e40b81a9,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4c6f102e-6daf-48f8-aafb-0958b762ee62,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,66995857-0006-467a-91b3-470ac8ea59a0,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,7015bfb5-53e0-4c7c-8c7d-a70db97c3441,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,757744c2-0f28-42dd-9884-e3bc548062e9,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,7643df39-86d6-43aa-af77-3ec3bc6d7f1e,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d1a7cd84-295b-4538-815f-d9e35e47bc9e,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d5c80e2f-e23e-4dfe-83d7-99877b974d2b,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,dc4a68d0-4209-440f-9d63-de6122162538,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXe,56396b0e-6923-41b9-b936-95475082a213,58d505f8-beef-47d6-a8bb-f52827ccae47,1,LIVE,file,d91f602c-d7f3-4238-a3cc-28df52afb0d9,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");
    assertEquals(
        all.get(idx++),
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8b594882-5805-4461-8970-d49c8ce41fb9,1,LIVE,file,fb3c52f6-4d9e-4232-bc1c-64e8d3da36b5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");
    assertEquals(
        all.get(idx++),
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8f5254e0-5b0f-4cfe-9af0-9ce2c5d784c5,1,LIVE,file,0578860d-65d7-44bf-8614-08469f268721,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(
        all.get(idx++),
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d8d002f7-63db-4c0c-bf9e-38d6334af37e,1,LIVE,file,3b24bdd0-f4d7-4bb3-a1cc-383faea4f981,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        all.get(idx++),
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,febe7a26-a556-4b96-bf0b-ff441e525a98,1,LIVE,file,89d0ec7a-0724-48ff-adcf-f29099574f01,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test2.txt\",");
    assertEquals(
        all.get(idx++),
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,12f54be4-e536-482d-a7fe-f262ac086b28,1,LIVE,file,66c08dc8-d86f-49eb-84df-20e17e3483d2,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
    assertEquals(
        all.get(idx++),
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,bfc4e251-f9ba-4c0a-8fea-b37634c5997c,1,LIVE,file,14ec0166-7331-4fdf-8e94-4358aa78728c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,6fb62537-8414-41ba-a98f-7b6910658978,1,PERSONAL,html,68152185-1b29-4227-bbf7-17a3e402b546,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/68152185-1b29-4227-bbf7-17a3e402b546/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,6fb62537-8414-41ba-a98f-7b6910658978,1,PERSONAL,html,be21f4ab-defb-44be-92a1-b99c11e866b5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/be21f4ab-defb-44be-92a1-b99c11e866b5/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,84023569-bcb5-404b-af20-480e28f7fc94,1,PERSONAL,html,56e85b46-4767-4c14-9645-7fbe6d4e6f3d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/56e85b46-4767-4c14-9645-7fbe6d4e6f3d/page.html\",",
            all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,file,9f6e3230-55b2-4413-a6da-073e80aede98,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-children.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,file,944206da-c635-4aab-aa0f-d6e8e516d66f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-root-renamed.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,3f7d9194-12f4-4387-b76c-d004c90b7dc6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/3f7d9194-12f4-4387-b76c-d004c90b7dc6/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,b80b9cb7-0eea-44c8-879b-2f739bd86045,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/b80b9cb7-0eea-44c8-879b-2f739bd86045/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,d0a6025e-8620-4864-b8c1-3cfa87dbdb56,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/d0a6025e-8620-4864-b8c1-3cfa87dbdb56/page.html\",",
            all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,34725983-4459-498e-9651-a54ade0509ad,1,LIVE,file,1ed0c642-048e-43e8-9ef0-4760d670b550,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,file,944206da-c635-4aab-aa0f-d6e8e516d66f,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-root-renamed.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,5ef2b291-f6ec-4cee-a444-66fbefed0dfe,1,LIVE,file,372dd2f4-7f21-41f0-b421-94348493fc24,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,file,b1e6c205-952c-4734-8c7b-928cecf4e9a6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,file,9f6e3230-55b2-4413-a6da-073e80aede98,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-children.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,06a07494-0af5-437f-8085-cc300fb682d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,294a036f-0736-43f1-9489-8eaf99928c8e,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,3e7f74cf-08eb-4782-a7e7-ce9ec22cffb6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,43e6095d-f504-4c6a-baac-47b08bfb49c6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,c47e39df-09de-4349-85cf-7a004f6c164f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,file,b1e6c205-952c-4734-8c7b-928cecf4e9a6,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,c7739abe-5afa-4664-8a17-113fe679b6f8,1,LIVE,file,1ed0c642-048e-43e8-9ef0-4760d670b550,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,06a07494-0af5-437f-8085-cc300fb682d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,294a036f-0736-43f1-9489-8eaf99928c8e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,3e7f74cf-08eb-4782-a7e7-ce9ec22cffb6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,43e6095d-f504-4c6a-baac-47b08bfb49c6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,c47e39df-09de-4349-85cf-7a004f6c164f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        all.get(idx++),
        "oeqgeneral,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,79b7fa12-7e96-46b1-a8dc-57eeb4dc799a,1,LIVE,file,a8b37c17-a30e-4fc9-9b41-f4fafa8f96c2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");

    assertEquals(INST_ALL_NUM_OF_ERR_RESULTS, err.size());

    idx = 7;
    assertEquals(all.get(idx++), "# Of ALL Attachments,157");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,0");
    assertEquals(all.get(idx++), "# Of Items affected,12");
    assertEquals(all.get(idx++), "# Of Items,42");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,24");
    idx += 8; // Skip the non-critical rows
    assertEquals(
        err.get(idx++),
        "instXb,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,711de2a7-62e9-41b1-93b9-95cc794e6f7a,1,LIVE,file,fb442db8-71f2-408d-b03b-679c4e2d7220,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(
        err.get(idx++),
        "instXb,df727509-77f7-4ce6-a9cb-2c5385565358,3a635ac4-3966-4964-b283-f597d9f11513,1,LIVE,file,e6cd0f56-b3d5-4a5d-9750-9a2119a894ca,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        err.get(idx++),
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8f5254e0-5b0f-4cfe-9af0-9ce2c5d784c5,1,LIVE,file,0578860d-65d7-44bf-8614-08469f268721,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(
        err.get(idx++),
        "instXe,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d8d002f7-63db-4c0c-bf9e-38d6334af37e,1,LIVE,file,3b24bdd0-f4d7-4bb3-a1cc-383faea4f981,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        err.get(idx++),
        "instXe,e03ff4c0-1d4f-4087-bf87-de4b59c6a243,12f54be4-e536-482d-a7fe-f262ac086b28,1,LIVE,file,66c08dc8-d86f-49eb-84df-20e17e3483d2,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,6fb62537-8414-41ba-a98f-7b6910658978,1,PERSONAL,html,68152185-1b29-4227-bbf7-17a3e402b546,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/68152185-1b29-4227-bbf7-17a3e402b546/page.html\",",
            err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,b80b9cb7-0eea-44c8-879b-2f739bd86045,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/b80b9cb7-0eea-44c8-879b-2f739bd86045/page.html\",",
            err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,file,944206da-c635-4aab-aa0f-d6e8e516d66f,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file7\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,5ef2b291-f6ec-4cee-a444-66fbefed0dfe,1,LIVE,file,372dd2f4-7f21-41f0-b421-94348493fc24,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,file,b1e6c205-952c-4734-8c7b-928cecf4e9a6,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,c7739abe-5afa-4664-8a17-113fe679b6f8,1,LIVE,file,1ed0c642-048e-43e8-9ef0-4760d670b550,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,294a036f-0736-43f1-9489-8eaf99928c8e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
  }

  private void confirmResultsInstitutionXf(List<String> allOriginal, List<String> errOriginal) {
    assertEquals(INST_XF_NUM_OF_ALL_RESULTS, allOriginal.size());
    // This is the primary flow that will change when new items / institutions are added to the test
    // institution.
    // Sort the results in a sandbox to make checking results a bit simplier
    List<String> all = new ArrayList<>();
    all.addAll(allOriginal);
    Collections.sort(all.subList(7, all.size()));
    List<String> err = new ArrayList<>();
    err.addAll(errOriginal);
    Collections.sort(err.subList(7, err.size()));
    int idx = 7;
    assertEquals("# Of ALL Attachments,123", all.get(idx++));
    assertEquals("# Of IGNORED Attachments,0", all.get(idx++));
    assertEquals("# Of Items affected,7", all.get(idx++));
    assertEquals("# Of Items,16", all.get(idx++));
    assertEquals("# Of MISSING Attachments,19", all.get(idx++));
    idx += 8; // Skip the non-critical rows
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,6fb62537-8414-41ba-a98f-7b6910658978,1,PERSONAL,html,68152185-1b29-4227-bbf7-17a3e402b546,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/68152185-1b29-4227-bbf7-17a3e402b546/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,6fb62537-8414-41ba-a98f-7b6910658978,1,PERSONAL,html,be21f4ab-defb-44be-92a1-b99c11e866b5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/be21f4ab-defb-44be-92a1-b99c11e866b5/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,84023569-bcb5-404b-af20-480e28f7fc94,1,PERSONAL,html,56e85b46-4767-4c14-9645-7fbe6d4e6f3d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/56e85b46-4767-4c14-9645-7fbe6d4e6f3d/page.html\",",
            all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,file,9f6e3230-55b2-4413-a6da-073e80aede98,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-children.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,216a958a-c444-4fff-be65-b9f9a8e8f801,1,LIVE,zip_entry,1a8380e2-4a53-4752-b8d2-5d3179b2c51f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,file,944206da-c635-4aab-aa0f-d6e8e516d66f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-root-renamed.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,231004ad-cfb3-4e30-bfac-3795161dbc2f,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,3f7d9194-12f4-4387-b76c-d004c90b7dc6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/3f7d9194-12f4-4387-b76c-d004c90b7dc6/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,b80b9cb7-0eea-44c8-879b-2f739bd86045,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/b80b9cb7-0eea-44c8-879b-2f739bd86045/page.html\",",
            all.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,d0a6025e-8620-4864-b8c1-3cfa87dbdb56,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/d0a6025e-8620-4864-b8c1-3cfa87dbdb56/page.html\",",
            all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,34725983-4459-498e-9651-a54ade0509ad,1,LIVE,file,1ed0c642-048e-43e8-9ef0-4760d670b550,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4cf1e4b4-8e12-4d34-bec7-956bb2dc03fa,1,LIVE,zip_entry,5480c552-16a4-49a0-9e15-9cef70998591,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,file,944206da-c635-4aab-aa0f-d6e8e516d66f,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-root-renamed.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,5ef2b291-f6ec-4cee-a444-66fbefed0dfe,1,LIVE,file,372dd2f4-7f21-41f0-b421-94348493fc24,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,file,b1e6c205-952c-4734-8c7b-928cecf4e9a6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8abf0c54-3912-4004-90d7-5e1fc825e80a,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,file,9f6e3230-55b2-4413-a6da-073e80aede98,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA-children.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,8adc967a-086b-40d9-a37c-cfb58a889dc4,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,06a07494-0af5-437f-8085-cc300fb682d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,294a036f-0736-43f1-9489-8eaf99928c8e,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,3e7f74cf-08eb-4782-a7e7-ce9ec22cffb6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,43e6095d-f504-4c6a-baac-47b08bfb49c6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,file,c47e39df-09de-4349-85cf-7a004f6c164f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,a34b1964-2e48-4a6a-b780-4488775d5984,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,file,b1e6c205-952c-4734-8c7b-928cecf4e9a6,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,c7739abe-5afa-4664-8a17-113fe679b6f8,1,LIVE,file,1ed0c642-048e-43e8-9ef0-4760d670b550,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,06a07494-0af5-437f-8085-cc300fb682d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,294a036f-0736-43f1-9489-8eaf99928c8e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,3e7f74cf-08eb-4782-a7e7-ce9ec22cffb6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,43e6095d-f504-4c6a-baac-47b08bfb49c6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,c47e39df-09de-4349-85cf-7a004f6c164f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_zips/zipA.zip\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/file2.txt\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        all.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        all.get(idx++));

    assertEquals(INST_XF_NUM_OF_ERR_RESULTS, err.size());

    idx = 7;
    assertEquals("# Of ALL Attachments,123", err.get(idx++));
    assertEquals("# Of IGNORED Attachments,0", err.get(idx++));
    assertEquals("# Of Items affected,7", err.get(idx++));
    assertEquals("# Of Items,16", err.get(idx++));
    assertEquals("# Of MISSING Attachments,19", err.get(idx++));
    idx += 8; // Skip the non-critical rows
    assertEquals(
            "instXf,6b356e2e-e6a0-235a-5730-15ad1d8ad630,6fb62537-8414-41ba-a98f-7b6910658978,1,PERSONAL,html,68152185-1b29-4227-bbf7-17a3e402b546,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/68152185-1b29-4227-bbf7-17a3e402b546/page.html\",",
            err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,1d04fb6a-eecb-4fef-858e-87aa3553eb66,1,LIVE,zip_entry,00207a00-5e75-4e78-ba04-8e6d5fc3b875,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-children.zip/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
            "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,3076b0c1-d4c2-4e6a-bd38-4e7aede75280,1,LIVE,html,b80b9cb7-0eea-44c8-879b-2f739bd86045,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"_mypages/b80b9cb7-0eea-44c8-879b-2f739bd86045/page.html\",",
            err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,file,944206da-c635-4aab-aa0f-d6e8e516d66f,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file7\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,56783aec-123e-4bad-878b-2aa99dfcd17d,1,LIVE,zip_entry,b156d930-fd85-4bf0-9eba-1b74ab903cd4,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA-root-renamed.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,5ef2b291-f6ec-4cee-a444-66fbefed0dfe,1,LIVE,file,372dd2f4-7f21-41f0-b421-94348493fc24,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,file,b1e6c205-952c-4734-8c7b-928cecf4e9a6,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/file3.rtf\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file7\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,b94fa56c-dbdd-45a5-9a66-3e485b2eaaa3,1,LIVE,zip_entry,3c525335-80ab-415f-816a-4a46353ff6e1,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder5/file6.csv\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,c7739abe-5afa-4664-8a17-113fe679b6f8,1,LIVE,file,1ed0c642-048e-43e8-9ef0-4760d670b550,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,file,294a036f-0736-43f1-9489-8eaf99928c8e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
    assertEquals(
        "instXf,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,fac2ee35-293a-4b73-8560-65935632386b,1,LIVE,zip_entry,e2a2aecf-eaa7-4e81-96b3-e973b3be0565,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"zipA.zip/zipA/folder1/folder4/file8\",",
        err.get(idx++));
  }

  private void confirmResultsInstitutionXaAllCollections(List<String> all, List<String> err) {
    assertEquals(INST_XA_NUM_OF_ALL_RESULTS, all.size());

    int idx = 7;
    assertEquals(
        all.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        all.get(idx++),
        "instXa,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,f61dc75f-62ca-4cbb-abf2-89bd489cb875,1,LIVE,file,d3e8a4db-f36d-4ff7-a074-728ade56f1d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        all.get(idx++),
        "instXa,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,0a16eb9d-88af-4951-82bb-40da96516a2f,1,LIVE,file,16fd1eea-86bd-46a4-ae7b-c4b8686bc5dd,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");
    assertEquals(all.get(idx++), "Stats");
    assertEquals(all.get(idx++), "# Of Items,2");
    assertEquals(all.get(idx++), "# Of Items affected,0");
    assertEquals(all.get(idx++), "# Of ALL Attachments,2");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,0");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,0");

    assertEquals(INST_XA_NUM_OF_ERR_RESULTS, err.size());

    idx = 7;
    assertEquals(
        err.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(err.get(idx++), "Stats");
    assertEquals(err.get(idx++), "# Of Items,2");
    assertEquals(err.get(idx++), "# Of Items affected,0");
    assertEquals(err.get(idx++), "# Of ALL Attachments,2");
    assertEquals(err.get(idx++), "# Of MISSING Attachments,0");
    assertEquals(err.get(idx++), "# Of IGNORED Attachments,0");
  }

  private void confirmResultsInstitutionXbAllCollections(List<String> all, List<String> err) {
    assertEquals(INST_XB_NUM_OF_ALL_RESULTS, all.size());

    int idx = 7;
    assertEquals(
        all.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        all.get(idx++),
        "instXb,df727509-77f7-4ce6-a9cb-2c5385565358,4a0d9c5d-0197-45b8-8c48-7b947ae9c499,1,LIVE,file,f56b2e48-0c68-42e1-8f79-6aa206c0a259,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        all.get(idx++),
        "instXb,df727509-77f7-4ce6-a9cb-2c5385565358,3a635ac4-3966-4964-b283-f597d9f11513,1,LIVE,file,e6cd0f56-b3d5-4a5d-9750-9a2119a894ca,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXb,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,711de2a7-62e9-41b1-93b9-95cc794e6f7a,1,LIVE,file,fb442db8-71f2-408d-b03b-679c4e2d7220,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(
        all.get(idx++),
        "instXb,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d70fa56f-214a-4233-923c-1df83a7c48c1,1,LIVE,file,12abbdce-ffea-4627-aa37-c890cd860a47,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
    assertEquals(all.get(idx++), "Stats");
    assertEquals(all.get(idx++), "# Of Items,4");
    assertEquals(all.get(idx++), "# Of Items affected,2");
    assertEquals(all.get(idx++), "# Of ALL Attachments,4");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,2");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,0");

    assertEquals(INST_XB_NUM_OF_ERR_RESULTS, err.size());

    idx = 7;
    assertEquals(
        err.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        err.get(idx++),
        "instXb,df727509-77f7-4ce6-a9cb-2c5385565358,3a635ac4-3966-4964-b283-f597d9f11513,1,LIVE,file,e6cd0f56-b3d5-4a5d-9750-9a2119a894ca,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        err.get(idx++),
        "instXb,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,711de2a7-62e9-41b1-93b9-95cc794e6f7a,1,LIVE,file,fb442db8-71f2-408d-b03b-679c4e2d7220,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(err.get(idx++), "Stats");
    assertEquals(err.get(idx++), "# Of Items,4");
    assertEquals(err.get(idx++), "# Of Items affected,2");
    assertEquals(err.get(idx++), "# Of ALL Attachments,4");
    assertEquals(err.get(idx++), "# Of MISSING Attachments,2");
    assertEquals(err.get(idx++), "# Of IGNORED Attachments,0");
  }

  private void confirmResultsInstitutionXcFS2Collection(List<String> all, List<String> err) {
    assertEquals(INST_XC_COLL_FS2_NUM_ALL_RESULTS, all.size());

    int idx = 7;
    assertEquals(
        all.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,6ee078cf-5725-438f-a3e0-937130d50277,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"传傳败敗见見.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,2395ec79-9925-46d6-af54-e6eb21dbfe0a,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename-with++.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,ef97a206-79c8-4235-b078-eaf58025ab29,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,416eb301-0ae6-4cb8-925f-02c6cdc61a7f,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename with spaces.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,836a7eb4-4897-44cc-9609-e843cbd46bba,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"ascii char \\ test +  . ~ $|=><`#^%&:}{ ]*[\")'(!-;?_,.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,39d8186f-6091-4c05-b41c-a907c63f8040,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"áéíóú¿¡üñ.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,e81439a0-e948-4286-963a-008c5119c2c1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\" الأَبْجَدِيَّة.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,bd2832e9-7d33-4ed6-8b85-15578f629ff5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"semi colons ; test.log\",");
    assertEquals(
        all.get(idx++),
        "instXc,df727509-77f7-4ce6-a9cb-2c5385565358,f232bc42-8871-4f92-b431-f65a94346731,1,LIVE,file,69885964-17f4-47a0-b417-06c1ede16617,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"slashes \\ are fun.log\",");
    assertEquals(all.get(idx++), "Stats");
    assertEquals(all.get(idx++), "# Of Items,1");
    assertEquals(all.get(idx++), "# Of Items affected,0");
    assertEquals(all.get(idx++), "# Of ALL Attachments,9");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,0");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,0");

    assertEquals(INST_XC_COLL_FS2_NUM_ERR_RESULTS, err.size());

    idx = 7;
    assertEquals(
        err.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(err.get(idx++), "Stats");
    assertEquals(err.get(idx++), "# Of Items,1");
    assertEquals(err.get(idx++), "# Of Items affected,0");
    assertEquals(err.get(idx++), "# Of ALL Attachments,9");
    assertEquals(err.get(idx++), "# Of MISSING Attachments,0");
    assertEquals(err.get(idx++), "# Of IGNORED Attachments,0");
  }

  private void confirmResultsFilterByInstitutionXdAndCollection(
      List<String> all, List<String> err) {
    assertEquals(INST_XD_COLL_LR_NUM_ALL_RESULTS, all.size());

    int idx = 7;
    assertEquals(
        all.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,7015bfb5-53e0-4c7c-8c7d-a70db97c3441,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,7643df39-86d6-43aa-af77-3ec3bc6d7f1e,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,dc4a68d0-4209-440f-9d63-de6122162538,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d1a7cd84-295b-4538-815f-d9e35e47bc9e,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,66995857-0006-467a-91b3-470ac8ea59a0,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,757744c2-0f28-42dd-9884-e3bc548062e9,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,d5c80e2f-e23e-4dfe-83d7-99877b974d2b,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,19fc300e-290d-49aa-9023-63b6e40b81a9,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,4c6f102e-6daf-48f8-aafb-0958b762ee62,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(
        all.get(idx++),
        "instXd,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,0b68c63f-afe7-4f35-b91c-f38609500384,1,LIVE,file,a7eafe30-77b3-449e-affc-4a002518409d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.js\",");
    assertEquals(all.get(idx++), "Stats");
    assertEquals(all.get(idx++), "# Of Items,10");
    assertEquals(all.get(idx++), "# Of Items affected,0");
    assertEquals(all.get(idx++), "# Of ALL Attachments,10");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,0");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,0");

    assertEquals(INST_XD_COLL_LR_NUM_ERR_RESULTS, err.size());

    idx = 7;
    assertEquals(
        err.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(err.get(idx++), "Stats");
    assertEquals(err.get(idx++), "# Of Items,10");
    assertEquals(err.get(idx++), "# Of Items affected,0");
    assertEquals(err.get(idx++), "# Of ALL Attachments,10");
    assertEquals(err.get(idx++), "# Of MISSING Attachments,0");
    assertEquals(err.get(idx++), "# Of IGNORED Attachments,0");
  }

  private void confirmResultsAllInstitutionsAllItems(List<String> all, List<String> err) {
    assertEquals(57, all.size());

    int idx = 7;
    assertEquals(
        all.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,8428e745-4cdb-4427-8b63-570c81401b2a,1,LIVE,[[Attachment type not set]],[[Attachment UUID not set]],No Att,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,44ac1c1a-dd0c-462f-b717-53324c0dd6f9,fa6b69a3-4524-4797-93ed-5f9c4cbcb28f,1,LIVE,file,ebf79de1-a336-4886-bec7-3a060666f700,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"plus and spaces+.pdf\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,44ac1c1a-dd0c-462f-b717-53324c0dd6f9,fa6b69a3-4524-4797-93ed-5f9c4cbcb28f,1,LIVE,file,1d961dd4-2e03-42f2-b028-7c59600c4241,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename.log\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cceab569-d101-445f-a59d-5cd5500a2b0c,1,LIVE,file,22b26083-6f4f-42fe-abca-befee0eb92a9,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html.zip\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,d896a882-a86a-4f5e-9650-c5f13a6d89d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ) test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,c4307b91-58c9-4106-bf5b-816ba07e0c3d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ( test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,53d76799-35d3-4b3e-8634-3b1666b778f5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char \" test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,3e796f00-731c-4eb2-a483-d93796650099,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char : test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,be8d38ca-4216-444a-b388-fc8de5d782b6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char & test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,9495760a-f8d1-4219-bbe7-0d6518d4bedf,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char control test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,21d6c550-2ff1-4112-84f6-1f0ca567b6dd,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test\\this.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,9db63958-5d1b-47b5-aa3d-0d569a598c72,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"|.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,0c85de42-66b7-4446-8e16-7166756214f6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"*.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,ae61a098-cfc4-452f-8268-695fa0fee94c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"^.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,de9a3fcf-dd07-4e53-99df-ebde4b11c493,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"..txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,a700d38d-bbd4-4b9a-a1fd-4438607bfec8,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\\.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,3749c0c3-fcf8-427e-98a2-61fd3184f03a,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char % [ ] { } ( ) asdf\\ asdf  \\ asdf = 123 \\ test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,8672147b-3d81-4ca4-84df-41829107e11b,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"fileWithPeriodAfterNonleadingBackslashesRoot\\fileWithPeriodAfterNonleadingBackslashesSubName\\.fileWithPeriodAfterNonleadingBackslashesLeaf\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,bb8f1968-eff2-41b2-9a68-705c4d7f1f2d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ? test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,e92dfc79-1a30-4682-ab01-b231b70004a1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ' test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,bd554963-2c55-4da9-9a9f-940ae41b6be1,1,LIVE,file,9d98468c-a040-4dfb-aa94-5500a6ccf467,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,file,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,dfe3782d-54b6-4aa2-873d-23c2ef6bf2a8,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,726cb654-3f24-4f6b-8c50-02cc5ddbdfeb,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,custom,9827302b-9012-4314-8f1b-053897eadd00,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,395d0c73-dfad-4f01-bb6b-cbcef11ed4db,1,LIVE,file,a4920601-3e3d-4c11-9560-8b9bd9c1baf3,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting2,44ac1c1a-dd0c-462f-b717-53324c0dd6f9,4d410ae3-072a-445c-a2e3-04a0bea8aceb,1,LIVE,file,d95a8e4d-f5b3-4280-a2fc-3bdd68acc26d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename-with++.log\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,2ed14dab-9103-4073-b8ce-1f134c40e7c5,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,7bf92ca9-84c3-4d9c-8917-d8f806443e10,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,369f980e-b3ae-4b47-9d2f-461a96230828,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cb0461fb-1d0a-4942-ba75-49ed630f0e28,1,LIVE,file,12780b70-31e4-4a93-9e1b-13ab7d09eb78,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cb0461fb-1d0a-4942-ba75-49ed630f0e28,1,LIVE,file,f5f61878-c139-42e1-8ccf-fd985f7c5667,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,attachment,8169c3d4-f6ed-4255-b17c-9f3136f1b85f,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,9964d765-6ead-4fb1-9752-166e7dbf206c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,2d4fe299-4eaf-499d-956e-571c30f13fa0,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"الأَبْجَدِيَّة.pdf\",");
    assertEquals(
        all.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,a0954d15-9e55-449a-9f04-a1c065e92440,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"传傳败敗见見.pdf\",");
    assertEquals(all.get(idx++), "Stats");
    assertEquals(all.get(idx++), "# Of Items,12");
    assertEquals(all.get(idx++), "# Of Items affected,2");
    assertEquals(all.get(idx++), "# Of ALL Attachments,36");
    assertEquals(all.get(idx++), "# Of MISSING Attachments,2");
    assertEquals(all.get(idx++), "# Of IGNORED Attachments,5");

    assertEquals(22, err.size());

    idx = 7;
    assertEquals(
        err.get(idx++),
        "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
    assertEquals(
        err.get(idx++),
        "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,file,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
    assertEquals(
        err.get(idx++),
        "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
    assertEquals(err.get(idx++), "Stats");
    assertEquals(err.get(idx++), "# Of Items,12");
    assertEquals(err.get(idx++), "# Of Items affected,2");
    assertEquals(err.get(idx++), "# Of ALL Attachments,36");
    assertEquals(err.get(idx++), "# Of MISSING Attachments,2");
    assertEquals(err.get(idx++), "# Of IGNORED Attachments,5");
  }
}
