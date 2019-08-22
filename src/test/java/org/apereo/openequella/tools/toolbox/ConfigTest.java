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

import java.util.Properties;

import org.junit.Test;

public class ConfigTest {
	@Test
	public void testConfigDateFormatOeqApi() {
		String date = "2013-01-18T11:39:07.290-07:00";
		try {
			Config.DATE_FORMAT_OEQ_API.parse(date);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigDateFormatConfigFile() {
		String date = "2013-01-18";
		try {
			Config.DATE_FORMAT_CONFIG_FILE.parse(date);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixes() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, "");
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, "");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertTrue("Config is expected to be valid, but is not.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesReqVideo() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, "");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertFalse("Config is expected to be invalid, but was valid.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesReqAudio() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, "");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertFalse("Config is expected to be invalid, but was valid.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesUniqueNoAudio() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, "");
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, ".mp4");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertTrue("Config is expected to be valid, but is not.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesUniqueNoVideo() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, ".mp3");
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, "");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertTrue("Config is expected to be valid, but is not.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesUniqueFailedSingle() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, ".mp4");
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, ".mp4");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertFalse("Config is expected to be invalid, but was valid.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesUniqueFailedMultiple() {
		Properties props = new Properties();
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, ".wma,.mp4");
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, " .mp4, .mov");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertFalse("Config is expected to be invalid, but was valid.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testConfigMigrateToKalturaAttachmentSuffixesUniqueGeneral() {
		Properties props = new Properties();
		props.put(Config.TOOLBOX_FUNCTION, Config.ToolboxFunction.MigrateToKaltura.name());
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO, ".mp3, .wma, .wav");
		props.put(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO, ".mp4,.mov");
		try {
			Config.reset();
			Config.getInstance().init(props);
			Config.getInstance().checkMigrateToKalturaAttachmentSuffixes();
			assertTrue("Config is expected to be valid, but is not.", Config.getInstance().isValidConfig());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

	@Test
	public void testOutputFolderNeeded() {
		Config.reset();
		Config.getInstance().setConfig(Config.CF_MODE, Config.CheckFilesType.DB_ALL_ITEMS_ALL_ATTS.name());
		Config.getInstance().checkConfigsCheckFiles();
		assertFalse(CheckFilesDriver.setup());
		assertFalse(Config.getInstance().isValidConfig());
	}

	// TODO - fix
//	@Test
//	public void testClientNameNeeded() {
//		String props = "testData/props/missing-client-name.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing client.name",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [client.name] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testPingTypeNeeded() {
//		String props = "testData/props/missing-ping-type.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing ping.type",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [ping.type] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testBadPingType() {
//		String props = "testData/props/bad-ping-type.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to bad ping.type",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: ping.type must be 'attachments', 'direct-query-all-items-all-attachments', or 'direct-query-batched-items-attachments-per-item'.",
//										props));
//	}
//
//	@Test
//	public void testHappyPathAttachments() {
//		String props = "testData/props/happy-path-attachments.properties";
//		assertTrue(PingEquellaDriver.setup(props));
//		assertTrue(Config.getInstance().isSetupValid());
//		assertEquals("Config init should have passed", Config.getInstance()
//						.getSetupInvalidReason(), "");
//	}
//
//	@Test
//	public void testHappyPathDirect() {
//		String props = "testData/props/happy-path-direct.properties";
//		assertTrue(PingEquellaDriver.setup(props));
//		assertTrue(Config.getInstance().isSetupValid());
//		assertTrue("Config init should have passed", Config.getInstance()
//						.getSetupInvalidReason().isEmpty());
//	}
//
//	@Test
//	public void testMissingDirectDbUrl() {
//		String props = "testData/props/missing-direct-db-url.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing direct.db.url",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.db.url] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testFilterByCollectionIdAlpha() {
//		String props = "testData/props/direct-filter-by-collection-id-alpha.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to alpha direct.filter.by.collection.id",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: direct.filter.by.collection.id parsed as [five], but should be a number.",
//										props));
//	}
//
//	@Test
//	public void testFilterByCollectionIdLT1() {
//		String props = "testData/props/direct-filter-by-collection-id-lt-1.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to less than 1 direct.filter.by.collection.id",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: direct.filter.by.collection.id parsed as [0], but should be greater than 0.",
//										props));
//	}
//
//	@Test
//	public void testMissingDirectDbUsername() {
//		String props = "testData/props/missing-direct-db-username.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing direct.db.username",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.db.username] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testMissingDirectDbPassword() {
//		String props = "testData/props/missing-direct-db-password.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing direct.db.password",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.db.password] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testMissingDirectDbType() {
//		String props = "testData/props/missing-direct-db-type.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing direct.db.type",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.db.type] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testDirectFilestoreDirMissing() {
//		String props = "testData/props/direct-filestore-dir-missing.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing direct.filestore.dir",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.filestore.dir] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testDirectFilestoreDirNotInstitutionsEndPoint() {
//		String props = "testData/props/direct-filestore-dir-not-institutions-endpoint.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to bad direct.filestore.dir",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Filestore directory /asdf/wef/qwef/as must end in 'Institutions'",
//										props));
//	}
//
//	@Test
//	public void testDirectFilestoreDirDoesNotExist() {
//		String props = "testData/props/direct-filestore-dir-does-not-exist.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to non-existant direct.filestore.dir",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Filestore Institutions directory /asdf/wef/qwef/as/Institutions must exist",
//										props));
//	}
//
//	@Test
//	public void testDirectFilestoreDirMustBeADirectory() {
//		String props = "testData/props/direct-filestore-dir-must-be-a-directory.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to file specified for direct.filestore.dir",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Filestore Institutions 'directory' ...../ping-equella/testData/misc/Institutions must be a directory",
//										props));
//	}
//
//	@Test
//	public void testMissingClientId() {
//		String props = "testData/props/missing-client-id.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing client.id",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [client.id] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testMissingClientSecret() {
//		String props = "testData/props/missing-client-secret.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing client.secret",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [client.secret] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testMissingPingMaxTries() {
//		String props = "testData/props/missing-ping-max-tries.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing ping.max.tries",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [ping.max.tries] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testMissingpingTestTimesToInjectTimeout() {
//		String props = "testData/props/missing-test-times-to-inject-timeout.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing ping.test.times.to.inject.timeout",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [ping.test.times.to.inject.timeout] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testBadDirectDbType() {
//		String props = "testData/props/bad-direct-db-type.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to bad direct.db.type",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: direct.db.type must be 'SQLSERVER'.",
//										props));
//	}
//
//	@Test
//	public void testDirectNumItemsInQueryConfirmIgnoreMissingWhenAllItemsAreQueried() {
//		String props = "testData/props/direct-num-items-in-query-confirm-ignore-when-all-items-are-queried.properties";
//		assertTrue(PingEquellaDriver.setup(props));
//		assertTrue(Config.getInstance().isSetupValid());
//		assertEquals("Config init should be valid", Config.getInstance()
//						.getSetupInvalidReason(), "");
//	}
//
//	@Test
//	public void testDirectNumItemsInQueryMissingWhenItemsAreBatched() {
//		String props = "testData/props/direct-num-items-in-query-missing-when-items-are-batched.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to missing direct.num.items.per.query",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.num.items.per.query] is required and was either not found or empty.",
//										props));
//	}
//
//	@Test
//	public void testDirectNumItemsInQueryAlphaWhenItemsAreBatched() {
//		String props = "testData/props/direct-num-items-in-query-alpha-when-items-are-batched.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to alpha direct.num.items.per.query",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.num.items.per.query] should be a number.",
//										props));
//	}
//
//	@Test
//	public void testDirectNumItemsInQueryLowWhenItemsAreBatched() {
//		String props = "testData/props/direct-num-items-in-query-low-when-items-are-batched.properties";
//		assertFalse(PingEquellaDriver.setup(props));
//		assertFalse(Config.getInstance().isSetupValid());
//		assertEquals(
//						"Config init should have failed due to low direct.num.items.per.query",
//						Config.getInstance().getSetupInvalidReason(),
//						String.format(
//										"Unable to use %s: Property [direct.num.items.per.query] should be above 1.",
//										props));
//	}
//
//	@Test
//	public void testInstitutionHandleAndShortnameEqualAttsDoNotExist() {
//		assertTrue(PingEquellaDriver
//						.setup("testData/props/direct-institutions-cache-handle-and-shortname-equal-atts-do-not-exist.properties"));
//		assertFalse(PingEquellaDriver.run());
//		assertTrue(ReportManager.getInstance().hasFatalErrors());
//		int lastIdx = ReportManager.getInstance().getFatalErrors().size() - 1;
//		assertEquals(
//						ReportManager.getInstance().getFatalErrors().get(lastIdx),
//						"Institution [vanilla] filestore attachments directory "
//										+ "[..../ping-equella/testData/filestores/"
//										+ "testInstitutionHandleAndShortnameEqualAttsDoNotExist/Institutions/vanilla/Attachments] does not exist.");
//
//	}
//
//	@Test
//	public void testInstitutionHandleAndShortnameDiffConfigMissing() {
//		assertTrue(PingEquellaDriver
//						.setup("testData/props/direct-institutions-cache-handle-and-shortname-diff-config-missing.properties"));
//		assertFalse(PingEquellaDriver.run());
//		assertTrue(ReportManager.getInstance().hasFatalErrors());
//		int lastIdx = ReportManager.getInstance().getFatalErrors().size() - 1;
//		assertEquals(
//						ReportManager.getInstance().getFatalErrors().get(lastIdx),
//						"Institution [vanilla] filestore handle is different than shortname, but is not specified in the properties.");
//
//	}
//
//	@Test
//	public void testInstitutionHandleAndShortnameDiffConfigEmpty() {
//		assertTrue(PingEquellaDriver
//						.setup("testData/props/direct-institutions-cache-handle-and-shortname-diff-config-empty.properties"));
//		assertFalse(PingEquellaDriver.run());
//		assertTrue(ReportManager.getInstance().hasFatalErrors());
//		int lastIdx = ReportManager.getInstance().getFatalErrors().size() - 1;
//		assertEquals(
//						ReportManager.getInstance().getFatalErrors().get(lastIdx),
//						"Institution [vanilla] filestore handle is different than shortname, but is not specified in the properties.");
//
//	}
//
//	@Test
//	public void testInstitutionHandleAndShortnameDiffConfigBadHandle() {
//		assertTrue(PingEquellaDriver
//						.setup("testData/props/direct-institutions-cache-handle-and-shortname-diff-config-bad-handle.properties"));
//		assertFalse(PingEquellaDriver.run());
//		assertTrue(ReportManager.getInstance().hasFatalErrors());
//		int lastIdx = ReportManager.getInstance().getFatalErrors().size() - 1;
//		assertEquals(
//						ReportManager.getInstance().getFatalErrors().get(lastIdx),
//						"Institution [vanilla] filestore handle is different than shortname, but the handle (directory) specified in the properties [swirl] does not exist.");
//
//	}
//
//	@Test
//	public void testInstitutionHandleAndShortnameDiffConfigAttsDoNotExist() {
//		assertTrue(PingEquellaDriver
//						.setup("testData/props/direct-institutions-cache-handle-and-shortname-diff-config-atts-do-not-exist.properties"));
//		assertFalse(PingEquellaDriver.run());
//		assertTrue(ReportManager.getInstance().hasFatalErrors());
//		int lastIdx = ReportManager.getInstance().getFatalErrors().size() - 1;
//		assertEquals(
//						ReportManager.getInstance().getFatalErrors().get(lastIdx),
//						"Institution [vanilla] filestore attachments directory [...../ping-equella/testData/filestores/testInstitutionHandleAndShortnameDiffConfigAttsDoNotExist/Institutions/swirl/Attachments] does not exist.");
//
//	}
}
