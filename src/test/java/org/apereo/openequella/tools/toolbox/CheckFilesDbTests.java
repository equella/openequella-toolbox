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
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.checkFiles.ReportManager;
import org.junit.Test;


/**
 * Uses the advanced filestore in optional-config:
 * filestore.advanced = true
 * filestore.additional.ids = 2
 * filestore.additional.2.path = {adv-filestore-loc}/oeq-filestore-2/
 * filestore.additional.2.name = Filestore ID#2
 */

public class CheckFilesDbTests {
	private static Logger LOGGER = LogManager.getLogger(Config.class);

  /**
   * Smoke test with an advanced filestore,
	 * some missing attachments, and special characters
   */
	@Test
	public void testAllInstitutionsAllItemsInSingleQuery() {
		try {
			Config.reset();
      TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().checkConfigsCheckFiles();

      assertTrue(CheckFilesDriver.setup(true));
      assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsAllInstitutionsAllItems(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(53), "# Of queries ran,16");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAllInstitutionsAllItemsInSingleQueryInstitutionFilterEmpty() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsAllInstitutionsAllItems(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());


			assertEquals(12, ReportManager.getInstance().getStats().getNumGrandTotalItems());
			assertEquals(12, ReportManager.getInstance().getStats().getNumTotalItems());
			assertEquals(36, ReportManager.getInstance().getStats().getNumGrandTotalAttachments());
			assertEquals(36, ReportManager.getInstance().getStats().getNumTotalAttachments());
			assertEquals(2, ReportManager.getInstance().getStats().getNumTotalItemsAffected());
			assertEquals(2, ReportManager.getInstance().getStats().getNumTotalAttachmentsMissing());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAllInstitutionsAllItemsBatchedBy1() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "1");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsAllInstitutionsAllItems(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(53), "# Of queries ran,28");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testAllInstitutionsAllItemsBatchedBy10() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "10");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsAllInstitutionsAllItems(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(53), "# Of queries ran,18");

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testItemsInSingleQueryInstitutionFilterBad() {
		try {
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

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFilterByCollectionItemsBatchedBy6() {
		try {
      Config.reset();
      TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
      Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
      Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "6");
      Config.getInstance().checkConfigsCheckFiles();

      assertTrue(CheckFilesDriver.setup(true));
      assertTrue(CheckFilesDriver.run());
      assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsFilterByCollection845(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(45), "# Of queries ran,14");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private void confirmResultsAllInstitutionsAllItems(List<String> all, List<String> err) {
		assertEquals(57, all.size());

		int idx = 7;
		assertEquals(all.get(idx++), "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,8428e745-4cdb-4427-8b63-570c81401b2a,1,LIVE,[[Attachment type not set]],[[Attachment UUID not set]],No Att,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,44ac1c1a-dd0c-462f-b717-53324c0dd6f9,fa6b69a3-4524-4797-93ed-5f9c4cbcb28f,1,LIVE,file,ebf79de1-a336-4886-bec7-3a060666f700,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"plus and spaces+.pdf\",");
		assertEquals(all.get(idx++), "checkFilesTesting,44ac1c1a-dd0c-462f-b717-53324c0dd6f9,fa6b69a3-4524-4797-93ed-5f9c4cbcb28f,1,LIVE,file,1d961dd4-2e03-42f2-b028-7c59600c4241,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename.log\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cceab569-d101-445f-a59d-5cd5500a2b0c,1,LIVE,file,22b26083-6f4f-42fe-abca-befee0eb92a9,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html.zip\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,d896a882-a86a-4f5e-9650-c5f13a6d89d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ) test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,c4307b91-58c9-4106-bf5b-816ba07e0c3d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ( test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,53d76799-35d3-4b3e-8634-3b1666b778f5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char \" test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,3e796f00-731c-4eb2-a483-d93796650099,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char : test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,be8d38ca-4216-444a-b388-fc8de5d782b6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char & test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,9495760a-f8d1-4219-bbe7-0d6518d4bedf,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char control test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,21d6c550-2ff1-4112-84f6-1f0ca567b6dd,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test\\this.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,9db63958-5d1b-47b5-aa3d-0d569a598c72,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"|.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,0c85de42-66b7-4446-8e16-7166756214f6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"*.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,ae61a098-cfc4-452f-8268-695fa0fee94c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"^.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,de9a3fcf-dd07-4e53-99df-ebde4b11c493,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"..txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,a700d38d-bbd4-4b9a-a1fd-4438607bfec8,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\\.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,3749c0c3-fcf8-427e-98a2-61fd3184f03a,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char % [ ] { } ( ) asdf\\ asdf  \\ asdf = 123 \\ test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,8672147b-3d81-4ca4-84df-41829107e11b,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"fileWithPeriodAfterNonleadingBackslashesRoot\\fileWithPeriodAfterNonleadingBackslashesSubName\\.fileWithPeriodAfterNonleadingBackslashesLeaf\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,bb8f1968-eff2-41b2-9a68-705c4d7f1f2d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ? test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,e92dfc79-1a30-4682-ab01-b231b70004a1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ' test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,6e85ce64-9a11-c5e7-69a4-bd30ec61007f,bd554963-2c55-4da9-9a9f-940ae41b6be1,1,LIVE,file,9d98468c-a040-4dfb-aa94-5500a6ccf467,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.rtf\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,file,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,dfe3782d-54b6-4aa2-873d-23c2ef6bf2a8,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,726cb654-3f24-4f6b-8c50-02cc5ddbdfeb,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,custom,9827302b-9012-4314-8f1b-053897eadd00,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,395d0c73-dfad-4f01-bb6b-cbcef11ed4db,1,LIVE,file,a4920601-3e3d-4c11-9560-8b9bd9c1baf3,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting2,44ac1c1a-dd0c-462f-b717-53324c0dd6f9,4d410ae3-072a-445c-a2e3-04a0bea8aceb,1,LIVE,file,d95a8e4d-f5b3-4280-a2fc-3bdd68acc26d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test-filename-with++.log\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,2ed14dab-9103-4073-b8ce-1f134c40e7c5,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,7bf92ca9-84c3-4d9c-8917-d8f806443e10,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,369f980e-b3ae-4b47-9d2f-461a96230828,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cb0461fb-1d0a-4942-ba75-49ed630f0e28,1,LIVE,file,12780b70-31e4-4a93-9e1b-13ab7d09eb78,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cb0461fb-1d0a-4942-ba75-49ed630f0e28,1,LIVE,file,f5f61878-c139-42e1-8ccf-fd985f7c5667,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,attachment,8169c3d4-f6ed-4255-b17c-9f3136f1b85f,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,9964d765-6ead-4fb1-9752-166e7dbf206c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,2d4fe299-4eaf-499d-956e-571c30f13fa0,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"الأَبْجَدِيَّة.pdf\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,a0954d15-9e55-449a-9f04-a1c065e92440,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"传傳败敗见見.pdf\",");
		assertEquals(all.get(idx++), "Stats");
		assertEquals(all.get(idx++), "# Of Items,12");
		assertEquals(all.get(idx++), "# Of Items affected,2");
		assertEquals(all.get(idx++), "# Of ALL Attachments,36");
		assertEquals(all.get(idx++), "# Of MISSING Attachments,2");
		assertEquals(all.get(idx++), "# Of IGNORED Attachments,5");

		assertEquals(22, err.size());

		idx = 7;
		assertEquals(err.get(idx++), "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
		assertEquals(err.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,file,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
		assertEquals(err.get(idx++), "checkFilesTesting,5a7b2083-2671-414b-8e5b-c4cd9dfa1f30,ad7a2f0d-7ad5-44cb-9dc6-824f0da38351,1,LIVE,file,a78040c6-4bd6-48ff-be8a-ce90bcf8f81e,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
		assertEquals(err.get(idx++), "Stats");
		assertEquals(err.get(idx++), "# Of Items,12");
		assertEquals(err.get(idx++), "# Of Items affected,2");
		assertEquals(err.get(idx++), "# Of ALL Attachments,36");
		assertEquals(err.get(idx++), "# Of MISSING Attachments,2");
		assertEquals(err.get(idx++), "# Of IGNORED Attachments,5");
	}

	private void confirmResultsFilterByCollection845(List<String> all, List<String> err) {
		assertEquals(48, all.size());

		int idx = 7;
		assertEquals(all.get(idx++), "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,8428e745-4cdb-4427-8b63-570c81401b2a,1,LIVE,[[Attachment type not set]],[[Attachment UUID not set]],No Att,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cceab569-d101-445f-a59d-5cd5500a2b0c,1,LIVE,file,22b26083-6f4f-42fe-abca-befee0eb92a9,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html.zip\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,d896a882-a86a-4f5e-9650-c5f13a6d89d2,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ) test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,c4307b91-58c9-4106-bf5b-816ba07e0c3d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ( test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,53d76799-35d3-4b3e-8634-3b1666b778f5,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char \" test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,3e796f00-731c-4eb2-a483-d93796650099,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char : test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,be8d38ca-4216-444a-b388-fc8de5d782b6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char & test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,9495760a-f8d1-4219-bbe7-0d6518d4bedf,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char control test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,21d6c550-2ff1-4112-84f6-1f0ca567b6dd,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test\\this.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,9db63958-5d1b-47b5-aa3d-0d569a598c72,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"|.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,0c85de42-66b7-4446-8e16-7166756214f6,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"*.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,ae61a098-cfc4-452f-8268-695fa0fee94c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"^.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,de9a3fcf-dd07-4e53-99df-ebde4b11c493,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"..txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,a700d38d-bbd4-4b9a-a1fd-4438607bfec8,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"\\.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,3749c0c3-fcf8-427e-98a2-61fd3184f03a,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char % [ ] { } ( ) asdf\\ asdf  \\ asdf = 123 \\ test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,bb8f1968-eff2-41b2-9a68-705c4d7f1f2d,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ? test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,9da6c4f4-e4f9-431a-93a4-0438a7762747,1,LIVE,file,e92dfc79-1a30-4682-ab01-b231b70004a1,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"char ' test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,file,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,395d0c73-dfad-4f01-bb6b-cbcef11ed4db,1,LIVE,file,a4920601-3e3d-4c11-9560-8b9bd9c1baf3,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,2ed14dab-9103-4073-b8ce-1f134c40e7c5,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,7bf92ca9-84c3-4d9c-8917-d8f806443e10,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,bdb56fa6-00a9-4c2f-89b0-b0de01382b3c,1,LIVE,attachment,369f980e-b3ae-4b47-9d2f-461a96230828,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cb0461fb-1d0a-4942-ba75-49ed630f0e28,1,LIVE,file,12780b70-31e4-4a93-9e1b-13ab7d09eb78,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"syllabus.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cb0461fb-1d0a-4942-ba75-49ed630f0e28,1,LIVE,file,f5f61878-c139-42e1-8ccf-fd985f7c5667,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"keyword.txt\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,attachment,8169c3d4-f6ed-4255-b17c-9f3136f1b85f,Ignored,[[Attachment resp code not set]],\"[[Item name not set]]\",\"[[Attachment file path not set]]\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,9964d765-6ead-4fb1-9752-166e7dbf206c,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"oùdéjàn.html\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,2d4fe299-4eaf-499d-956e-571c30f13fa0,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"الأَبْجَدِيَّة.pdf\",");
		assertEquals(all.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,cbd87c8a-c291-4b9f-861d-6d4478f21685,1,LIVE,file,a0954d15-9e55-449a-9f04-a1c065e92440,Present,[[Attachment resp code not set]],\"[[Item name not set]]\",\"传傳败敗见見.pdf\",");
		assertEquals(all.get(idx++), "Stats");
		assertEquals(all.get(idx++), "# Of Items,8");
		assertEquals(all.get(idx++), "# Of Items affected,1");
		assertEquals(all.get(idx++), "# Of ALL Attachments,27");
		assertEquals(all.get(idx++), "# Of MISSING Attachments,1");
		assertEquals(all.get(idx++), "# Of IGNORED Attachments,4");

		assertEquals(21, err.size());

		idx = 7;
		assertEquals(err.get(idx++), "Institution Shortname,Collection UUID,Item UUID,Item Version,ItemStatus,Attachment Type,Attachment UUID,Attachment Status,Attachment Response Code,Item Name,Attachment Filepath");
		assertEquals(err.get(idx++), "checkFilesTesting,97574753-4054-4684-b135-9f8475124a8e,25ecc776-076b-4c9b-b2cf-1f3808007d9d,1,LIVE,file,9465f317-d2a7-48d3-a6e2-27e142b534e8,Missing,[[Attachment resp code not set]],\"[[Item name not set]]\",\"test.html\",");
		assertEquals(err.get(idx++), "Stats");
		assertEquals(err.get(idx++), "# Of Items,8");
		assertEquals(err.get(idx++), "# Of Items affected,1");
		assertEquals(err.get(idx++), "# Of ALL Attachments,27");
		assertEquals(err.get(idx++), "# Of MISSING Attachments,1");
		assertEquals(err.get(idx++), "# Of IGNORED Attachments,4");
	}
	@Test
	public void testFilterByCollectionItemsBatchedBy1000() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "1000");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsFilterByCollection845(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(45), "# Of queries ran,13");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFilterByCollectionItemsBatchedBy2() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_NUM_OF_ITEMS_PER_QUERY, "2");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsFilterByCollection845(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(45), "# Of queries ran,16");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testFilterByCollectionItemsInSingleQuery() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsFilterByCollection845(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
			assertEquals(ReportManager.getInstance().getAllStatsWriterList().get(45), "# Of queries ran,12");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFilterByCollectionAndByInstitutionItemsInSingleQuery() {
		try {
			Config.reset();
			TestUtils.buildCheckFilesGeneralDbProps();
			Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
			Config.getInstance().setConfig(Config.CF_EMAIL_MODE, Config.CheckFilesEmailMode.NONE.name());
			Config.getInstance().setConfig(Config.CF_FILTER_BY_COLLECTION, "845");
			Config.getInstance().setConfig(Config.CF_FILTER_BY_INSTITUTION, "checkFilesTesting");
			Config.getInstance().checkConfigsCheckFiles();

			assertTrue(CheckFilesDriver.setup(true));
			assertTrue(CheckFilesDriver.run());
			assertTrue(CheckFilesDriver.finalizeRun());

			TestUtils.debugDumpList(ReportManager.getInstance().getAllStatsWriterList());
			TestUtils.debugDumpList(ReportManager.getInstance().getErrorStatsWriterList());
			confirmResultsFilterByCollection845(ReportManager.getInstance().getAllStatsWriterList(),
							ReportManager.getInstance().getErrorStatsWriterList());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
