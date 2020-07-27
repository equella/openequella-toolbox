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

import java.util.List;
import java.util.Properties;
import org.apereo.openequella.tools.toolbox.exportItems.ParsedItem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class ExportItemsDriverTest {
  public static final String DATE_CREATED_STR = "2014-11-12T11:43:05.543-05:00";
  public static final String DATE_MODIFIED_STR = "2015-11-12T11:43:05.543-05:00";

  @Test
  public void testParseAttachmentFilenames() throws Exception {
    // Build out test data
    ParsedItem ei = new ParsedItem();
    ei.setJson(buildEquellaItemAttSetup1());
    ei.setName("name1");
    ei.setUuid("1-2-3-4");
    ei.setVersion(1);
    ei.setMetadata(
        "<xml><metadata><learning><content>nif,ty</content><content>This is \" some data!</content></learning><keywords><keyword>k1</keyword></keywords><general>asdf</general><keywords><keyword>k2</keyword><keyword>k3</keyword></keywords></metadata></xml>");
    ei.setCreatedDateStr(DATE_CREATED_STR);
    ei.setModifiedDateStr(DATE_MODIFIED_STR);
    // Set configs
    Properties props = buildGeneralExportItemsProps();
    props.put(
        Config.EXPORT_ITEMS_ATTACHMENT_PATH_TEMPLATE,
        "/Attachments/@HASH/@UUID/@VERSION/@FILENAME");
    props.put(Config.EXPORT_ITEMS_ONE_ATT_PER_LINE, "true");
    props.put(Config.EXPORT_ITEMS_MULTI_VALUE_DELIM, "|");
    Config.reset();
    Config.getInstance().init(props);
    Config.getInstance().checkConfigs();

    // Run test
    ExportItemsDriver eid = new ExportItemsDriver();
    eid.parseRecord(ei);
    List<List<String>> results = eid.displayRecord(ei);

    assertEquals(6, results.size());
    assertEquals(11, results.get(0).size());
    assertEquals("name1", results.get(0).get(0));
    assertEquals("1-2-3-4", results.get(0).get(1));
    assertEquals("1", results.get(0).get(2));
    assertEquals("", results.get(0).get(3));
    assertEquals("nif,ty|This is \" some data!", results.get(0).get(4));
    assertEquals("k1|k2|k3", results.get(0).get(5));
    assertEquals("", results.get(0).get(6));
    assertEquals("5-8-7-6", results.get(0).get(7));
    assertEquals("", results.get(0).get(8));
    assertEquals(DATE_CREATED_STR, results.get(0).get(9));
    assertEquals(DATE_MODIFIED_STR, results.get(0).get(10));

    assertEquals(11, results.get(1).size());
    assertEquals("name1", results.get(1).get(0));
    assertEquals("1-2-3-4", results.get(1).get(1));
    assertEquals("1", results.get(1).get(2));
    assertEquals("", results.get(1).get(3));
    assertEquals("nif,ty|This is \" some data!", results.get(1).get(4));
    assertEquals("k1|k2|k3", results.get(1).get(5));
    assertEquals("", results.get(1).get(6));
    assertEquals("8-5-6-8", results.get(1).get(7));
    assertEquals("", results.get(1).get(8));
    assertEquals(DATE_CREATED_STR, results.get(1).get(9));
    assertEquals(DATE_MODIFIED_STR, results.get(1).get(10));

    assertEquals(11, results.get(2).size());
    assertEquals("name1", results.get(2).get(0));
    assertEquals("1-2-3-4", results.get(2).get(1));
    assertEquals("1", results.get(2).get(2));
    assertEquals("https://apereo.org/oeq-examples/fake-url-1", results.get(2).get(3));
    assertEquals("nif,ty|This is \" some data!", results.get(2).get(4));
    assertEquals("k1|k2|k3", results.get(2).get(5));
    assertEquals("", results.get(2).get(6));
    assertEquals("url-att-1-uuid", results.get(2).get(7));
    assertEquals("false", results.get(2).get(8));
    assertEquals(DATE_CREATED_STR, results.get(2).get(9));
    assertEquals(DATE_MODIFIED_STR, results.get(2).get(10));

    assertEquals(11, results.get(3).size());
    assertEquals("name1", results.get(3).get(0));
    assertEquals("1-2-3-4", results.get(3).get(1));
    assertEquals("1", results.get(3).get(2));
    assertEquals("file-att-2 with spaces and quotes \" and pipes |.pdf", results.get(3).get(3));
    assertEquals("nif,ty|This is \" some data!", results.get(3).get(4));
    assertEquals("k1|k2|k3", results.get(3).get(5));
    assertEquals("85645", results.get(3).get(6));
    assertEquals("file-att-2-uuid", results.get(3).get(7));
    assertEquals("", results.get(3).get(8));
    assertEquals(DATE_CREATED_STR, results.get(3).get(9));
    assertEquals(DATE_MODIFIED_STR, results.get(3).get(10));

    assertEquals(11, results.get(4).size());
    assertEquals("name1", results.get(4).get(0));
    assertEquals("1-2-3-4", results.get(4).get(1));
    assertEquals("1", results.get(4).get(2));
    assertEquals("https://apereo.org/oeq-examples/fake-url-2", results.get(4).get(3));
    assertEquals("nif,ty|This is \" some data!", results.get(4).get(4));
    assertEquals("k1|k2|k3", results.get(4).get(5));
    assertEquals("", results.get(4).get(6));
    assertEquals("url-att-2-uuid", results.get(4).get(7));
    assertEquals("true", results.get(4).get(8));
    assertEquals(DATE_CREATED_STR, results.get(4).get(9));
    assertEquals(DATE_MODIFIED_STR, results.get(4).get(10));

    assertEquals(11, results.get(5).size());
    assertEquals("name1", results.get(5).get(0));
    assertEquals("1-2-3-4", results.get(5).get(1));
    assertEquals("1", results.get(5).get(2));
    assertEquals("file-att-1.pdf", results.get(5).get(3));
    assertEquals("nif,ty|This is \" some data!", results.get(5).get(4));
    assertEquals("k1|k2|k3", results.get(5).get(5));
    assertEquals("43432", results.get(5).get(6));
    assertEquals("file-att-1-uuid", results.get(5).get(7));
    assertEquals("", results.get(5).get(8));
    assertEquals(DATE_CREATED_STR, results.get(5).get(9));
    assertEquals(DATE_MODIFIED_STR, results.get(5).get(10));
  }

  @Test
  public void testExportItemsItemExclusionConfig() throws Exception {
    Properties props = buildGeneralExportItemsProps();
    props.put(
        Config.EXPORT_ITEMS_ATTACHMENT_PATH_TEMPLATE,
        "/Attachments/@HASH/@UUID/@VERSION/@FILENAME");
    props.put(Config.EXPORT_ITEMS_ONE_ATT_PER_LINE, "true");
    props.put(Config.EXPORT_ITEMS_MULTI_VALUE_DELIM, "|");
    props.put(Config.EXPORT_ITEMS_ITEM_EXCLUSIONS, "asdf");
    Config.reset();
    Config.getInstance().init(props);
    Config.getInstance().checkConfigs();
    assertFalse("Config should not be valid", Config.getInstance().isValidConfig());

    props.put(Config.EXPORT_ITEMS_ITEM_EXCLUSIONS, "qwerty/asdf");
    Config.reset();
    Config.getInstance().init(props);
    Config.getInstance().checkConfigs();
    assertFalse("Config should not be valid", Config.getInstance().isValidConfig());

    props.put(Config.EXPORT_ITEMS_ITEM_EXCLUSIONS, "12345678-1234-1234-1234-123456789012/one");
    Config.reset();
    Config.getInstance().init(props);
    Config.getInstance().checkConfigs();
    assertFalse("Config should not be valid", Config.getInstance().isValidConfig());

    props.put(Config.EXPORT_ITEMS_ITEM_EXCLUSIONS, "12345678-1234-1234-1234-123456789012/78");
    Config.reset();
    Config.getInstance().init(props);
    Config.getInstance().checkConfigs();
    assertTrue("Config should be valid", Config.getInstance().isValidConfig());

    props.put(
        Config.EXPORT_ITEMS_ITEM_EXCLUSIONS,
        "12345678-1234-1234-1234-123456789012/78,12345678-1234-1234-1234-123456789056/1,12345678-1234-1234-1234-123456789012/77");
    Config.reset();
    Config.getInstance().init(props);
    Config.getInstance().checkConfigs();
    assertTrue("Config should be valid", Config.getInstance().isValidConfig());
  }

  @Test
  public void testFindFirstKalturaIdInAttachments() {
    JSONObject json = new JSONObject();
    JSONObject att1 = new JSONObject();
    att1.put("type", "kaltura");
    att1.put("description", "This is an interesting kaltura link description");
    att1.put("mediaId", "0_12345");
    JSONObject att2 = new JSONObject();
    att2.put("type", "file");
    att2.put("description", "This is an interesting file description");
    att2.put("filename", "myfile.pdf");
    JSONObject att3 = new JSONObject();
    att3.put("type", "kaltura");
    att3.put("description", "This is an interesting kaltura link description");
    att3.put("mediaId", "0_98765");
    JSONArray atts = new JSONArray();
    atts.put(att1);
    atts.put(att2);
    atts.put(att3);
    json.put("attachments", atts);
    ParsedItem ei = new ParsedItem();
    ei.setJson(json);
    ExportItemsDriver eid = new ExportItemsDriver();
    assertEquals("0_12345", eid.findFirstKalturaIdInAttachments(ei));
  }

  private Properties buildGeneralExportItemsProps() {
    Properties props = new Properties();
    props.put(Config.TOOLBOX_FUNCTION, "ExportItems");
    props.put(Config.OEQ_URL, "https://someurl");
    props.put(Config.OEQ_OAUTH_CLIENT_ID, "1234");
    props.put(Config.OEQ_OAUTH_CLIENT_SECRET, "asdf");
    props.put(Config.OEQ_SEARCH_API_REQUESTED_LENGTH, "10");
    props.put(Config.OEQ_SEARCH_API, "/api/search");
    props.put(Config.GENERAL_OS_SLASH, "/");
    props.put(Config.GENERAL_DOWNLOAD_FOLDER, "/temp");
    props.put(Config.GENERAL_DOWNLOAD_CHATTER, "400");
    props.put(
        Config.EXPORT_ITEMS_OUTPUT_FILE, "/tmp/export-file" + System.currentTimeMillis() + ".csv");
    props.put(Config.EXPORT_ITEMS_FILTER_DATE_CREATED, "1960-01-01");
    props.put(
        Config.EXPORT_ITEMS_COLUMN_FORMAT,
        "name,UUID,Version,attachment_names,metadata/learning/content,metadata/keywords/keyword,attachment_size,attachment_uuid,attachment_disabled,item_datecreated,item_datemodified");
    props.put(Config.EXPORT_ITEMS_ATTACHMENT_PATH_TEMPLATE, "@FILENAME");

    return props;
  }

  // Noted as '1' and enabled
  private JSONObject buildUrlAttachment1() {
    JSONObject a = new JSONObject();
    a.put("type", "url");
    a.put("uuid", "url-att-1-uuid");
    a.put("description", "This is an interesting url-att-1 description");
    a.put("preview", false);
    a.put("restricted", false);
    a.put("url", "https://apereo.org/oeq-examples/fake-url-1");
    a.put("disabled", false);
    // does not build out links
    return a;
  }

  // Noted as '2' and disabled
  private JSONObject buildUrlAttachment2() {
    JSONObject a = new JSONObject();
    a.put("type", "url");
    a.put("uuid", "url-att-2-uuid");
    a.put("description", "This is an interesting url-att-2 description");
    a.put("preview", false);
    a.put("restricted", false);
    a.put("url", "https://apereo.org/oeq-examples/fake-url-2");
    a.put("disabled", true);
    // does not build out links
    return a;
  }

  private JSONObject buildFileAttachment1() {
    JSONObject a = new JSONObject();
    a.put("type", "file");
    a.put("uuid", "file-att-1-uuid");
    a.put("description", "This is an interesting file-att-1 description");
    a.put("preview", false);
    a.put("restricted", false);
    a.put("filename", "file-att-1.pdf");
    a.put("size", 43432);
    a.put("conversion", false);
    a.put("thumbFilename", "_THUMBS/file-att-1.pdf");
    // does not build out links / thumbnail
    return a;
  }

  private JSONObject buildFileAttachment2() {
    JSONObject a = new JSONObject();
    a.put("type", "file");
    a.put("uuid", "file-att-2-uuid");
    a.put("description", "This is an interesting file-att-2 description");
    a.put("preview", false);
    a.put("restricted", false);
    a.put("filename", "file-att-2 with spaces and quotes \" and pipes |.pdf");
    a.put("size", 85645);
    a.put("conversion", false);
    a.put("thumbFilename", "_THUMBS/file-att-2.pdf");
    // does not build out links / thumbnail
    return a;
  }

  private JSONObject buildEquellaItemAttSetup1() {
    JSONObject json = new JSONObject();
    JSONObject att1 = new JSONObject();
    att1.put("type", "kaltura");
    att1.put("uuid", "5-8-7-6");
    att1.put("description", "This is an interesting kaltura link description");
    att1.put("title", "A title of a kaltura link");
    JSONObject att2 = new JSONObject();
    att2.put("type", "custom");
    att2.put("uuid", "8-5-6-8");
    att2.put("description", "This is an interesting custom description");
    att2.put("filename", "not real");
    JSONArray atts = new JSONArray();
    atts.put(att1);
    atts.put(att2);
    atts.put(buildUrlAttachment1());
    atts.put(buildFileAttachment2());
    atts.put(buildUrlAttachment2());
    atts.put(buildFileAttachment1());
    json.put("attachments", atts);
    return json;
  }
}
