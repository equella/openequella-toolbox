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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.api.OpenEquellaRestUtils;
import org.apereo.openequella.tools.toolbox.exportItems.ParsedAttachment;
import org.apereo.openequella.tools.toolbox.exportItems.ParsedItem;
import org.apereo.openequella.tools.toolbox.utils.GeneralUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ExportItemsDriver {
  private static Logger LOGGER = LogManager.getLogger(ExportItemsDriver.class);

  int globalExportedItemCounter = 0;

  public void execute() {
    globalExportedItemCounter = 0;
    Long start = System.currentTimeMillis();
    // Ensure openEQUELLA is accessible
    OpenEquellaRestUtils oeru = new OpenEquellaRestUtils();
    if (!oeru.gatherAccessToken()) {
      LOGGER.error("Ending openEQUELLA run - unable to access {}", Config.get(Config.OEQ_URL));
      return;
    }

    LOGGER.info("Duration to obtain access token: {}ms", (System.currentTimeMillis() - start));

    // Create output file
    File output = new File(Config.get(Config.EXPORT_ITEMS_OUTPUT_FILE));
    if (output.exists()) {
      LOGGER.error(
          "Ending openEQUELLA run - output file already exists: {}", output.getAbsolutePath());
      return;
    }
    CSVPrinter csvPrinter = null;
    try (BufferedWriter writer =
        Files.newBufferedWriter(Paths.get(Config.get(Config.EXPORT_ITEMS_OUTPUT_FILE)))) {
      LOGGER.info("Using output file {} for export", output.getAbsolutePath());

      // Setup CSV writer and 'heading'
      csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
      csvPrinter.printRecord(GeneralUtils.parseCSV(Config.get(Config.EXPORT_ITEMS_COLUMN_FORMAT)));

      // Loop through search results and save to the output file
      while (oeru.hasMoreResourcesToCache()) {
        try {
          start = System.currentTimeMillis();

          List<ParsedItem> nextBatch = oeru.gatherItemsGeneral();
          LOGGER.info(
              "Duration to obtain batch of items: {}ms", (System.currentTimeMillis() - start));

          toCsv(csvPrinter, nextBatch);

        } catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
          LOGGER.error(
              "Error with openEQUELLA run against output file - {} - {}",
              output.getAbsolutePath(),
              e.getMessage());
          return;
        }
      }
      csvPrinter.flush();
      csvPrinter.close();

      LOGGER.info("Export complete.");

    } catch (Exception e) {
      if (csvPrinter != null) {
        try {
          csvPrinter.flush();
          csvPrinter.close();
        } catch (IOException e1) {
          LOGGER.error(e1.getMessage(), e1);
          LOGGER.error("Issue forcing a close of the CSV Printer - {}", e.getMessage());
        }
      }
      LOGGER.error(e.getMessage(), e);
      LOGGER.error("Error ending openEQUELLA run - {}", e.getMessage());
    }
  }

  private void toCsv(CSVPrinter csvPrinter, List<ParsedItem> records) throws Exception {
    LOGGER.info(
        "Printing out batch to CSV file [{}].", Config.get(Config.EXPORT_ITEMS_OUTPUT_FILE));

    long start = System.currentTimeMillis();

    for (ParsedItem ei : records) {
      parseRecord(ei);

      LOGGER.debug(
          "Duration to parse record: {}ms - {}",
          (System.currentTimeMillis() - start),
          ei.getSignature());
      start = System.currentTimeMillis();

      if (Boolean.valueOf(Config.get(Config.EXPORT_ITEMS_ONE_ATT_PER_LINE))) {
        for (List<String> record : displayRecord(ei)) {
          csvPrinter.printRecord(record);
        }
      } else {
        csvPrinter.printRecord(displayRecord(ei).get(0));
      }
      csvPrinter.flush();

      LOGGER.debug("Duration to print record: {}ms", (System.currentTimeMillis() - start));

      start = System.currentTimeMillis();

      globalExportedItemCounter++;
    }

    LOGGER.info("Printing status:  {} items complete.", globalExportedItemCounter);
  }

  // While MigrationUtils.findFirstOccurrenceInXml() is similar, this combines the 'reserved
  // keywords' with a single invocation of parsing the XML.
  public void parseRecord(ParsedItem ei) throws Exception {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    Document doc = dBuilder.parse(new InputSource(new StringReader(ei.getMetadata())));
    XPathFactory xPathfactory = XPathFactory.newInstance();
    XPath xpath = xPathfactory.newXPath();

    Map<String, List<String>> record = new HashMap<>();
    for (String header : GeneralUtils.parseCSV(Config.get(Config.EXPORT_ITEMS_COLUMN_FORMAT))) {
      if (header.contains("/")) {
        // Assume it's a metadata path
        final String metadataPath = "/xml/" + header;
        try {
          XPathExpression expr = xpath.compile(metadataPath);
          Object xmlResults = expr.evaluate(doc, XPathConstants.NODESET);
          NodeList nodeResults = (NodeList) xmlResults;
          for (int i = 0; i < nodeResults.getLength(); i++) {
            ei.addToParsedMetadata(header, nodeResults.item(i).getTextContent());
          }
        } catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
          throw new Exception(
              "Unable to parse column format xpath "
                  + metadataPath
                  + "] due to: "
                  + e.getMessage());
        }
      }
    }

    parseAttachments(ei);
  }

  // While MigrationUtils.findFirstOccurrenceInXml() is similar, this combines the 'reserved
  // keywords' with a single invocation of parsing the XML.
  public List<List<String>> displayRecord(ParsedItem ei) throws Exception {
    List<List<String>> results = new ArrayList<>();

    if ((ei.getAttachments() == null) || ei.getAttachments().isEmpty()) {
      // Empty set
      results.add(createDisplayRecord(ei, null));
    } else if (Boolean.valueOf(Config.get(Config.EXPORT_ITEMS_ONE_ATT_PER_LINE))) {
      for (ParsedAttachment pAtt : ei.getAttachments()) {
        results.add(createDisplayRecord(ei, pAtt));
      }
    } else {
      // Munge all attachments
      ParsedAttachment munged = ei.getMungedAttachment();
      results.add(createDisplayRecord(ei, munged));
    }

    return results;
  }

  public void parseAttachments(ParsedItem ei) {
    JSONObject json = ei.getJson();
    if (json.has(OpenEquellaRestUtils.KEY_ATTS)) {
      JSONArray atts = json.getJSONArray(OpenEquellaRestUtils.KEY_ATTS);
      for (int i = 0; i < atts.length(); i++) {
        ParsedAttachment pAtt = new ParsedAttachment(atts.getJSONObject(i));
        ei.addToParsedAttachments(pAtt);
      }
    }
  }

  public String findFirstKalturaIdInAttachments(ParsedItem ei) {
    JSONObject json = ei.getJson();
    if (json.has(OpenEquellaRestUtils.KEY_ATTS)) {
      JSONArray atts = json.getJSONArray(OpenEquellaRestUtils.KEY_ATTS);
      for (int i = 0; i < atts.length(); i++) {
        JSONObject att = atts.getJSONObject(i);
        if (att.getString(OpenEquellaRestUtils.KEY_ATT_TYPE)
            .equals(OpenEquellaRestUtils.VAL_ATT_TYPE_KALTURA)) {
          return att.getString(OpenEquellaRestUtils.KEY_ATT_MEDIA_ID);
        }
      }
    } else {
      return "";
    }
    return "";
  }

  public String constructAttachmentPath(ParsedItem ei, String attName) {
    String template = Config.get(Config.EXPORT_ITEMS_ATTACHMENT_PATH_TEMPLATE);
    if (template.contains("@HASH")) {
      template = template.replaceFirst("@HASH", (ei.getUuid().hashCode() & 127) + "");
    }

    if (template.contains("@UUID")) {
      template = template.replaceFirst("@UUID", ei.getUuid());
    }

    if (template.contains("@VERSION")) {
      template = template.replaceFirst("@VERSION", (ei.getVersion()) + "");
    }

    if (template.contains("@FILENAME")) {
      template = template.replaceFirst("@FILENAME", attName);
    }

    return template;
  }

  private List<String> createDisplayRecord(ParsedItem pi, ParsedAttachment pAtt) {
    List<String> result = new ArrayList<>();
    LOGGER.debug(
        "Creating display record for {}/{} - {}", pi.getUuid(), pi.getVersion(), pi.getName());
    for (String header : GeneralUtils.parseCSV(Config.get(Config.EXPORT_ITEMS_COLUMN_FORMAT))) {
      if (header.equalsIgnoreCase("uuid")) {
        result.add(pi.getUuid());
      } else if (header.equalsIgnoreCase("version")) {
        result.add(pi.getVersion() + "");
      } else if (header.equalsIgnoreCase("attachment_names")) {
        if (pAtt == null) {
          result.add("");
        } else if (pAtt.get("type").equals("file")) {
          result.add(pAtt.get("filename"));
        } else if (pAtt.get("type").equals("url")) {
          result.add(pAtt.get("url"));
        } else {
          result.add("");
        }
      } else if (header.equalsIgnoreCase("attachment_size")) {
        if (pAtt == null) {
          result.add("");
        } else if (pAtt.get("type").equals("file")) {
          result.add(pAtt.get("size"));
        } else {
          result.add("");
        }
      } else if (header.equalsIgnoreCase("attachment_uuid")) {
        if (pAtt == null) {
          result.add("");
        } else {
          result.add(pAtt.get("uuid"));
        }
      } else if (header.equalsIgnoreCase("attachment_disabled")) {
        if (pAtt == null) {
          result.add("");
        } else if (pAtt.get("type").equals("url")) {
          result.add(pAtt.get("disabled"));
        } else {
          result.add("");
        }
      } else if (header.equalsIgnoreCase("item_datecreated")) {
        result.add(pi.getCreatedDateStr());
      } else if (header.equalsIgnoreCase("item_datemodified")) {
        result.add(pi.getModifiedDateStr());
      } else if (header.equalsIgnoreCase("name")) {
        result.add(pi.getName());
      } else if (header.equalsIgnoreCase("description")) {
        result.add(pi.getDescription());
      } else if (header.equalsIgnoreCase("kaltura_id")) {
        result.add(findFirstKalturaIdInAttachments(pi));
      } else {
        result.add(
            StringUtils.join(
                pi.getParsedMetadata(header), Config.get(Config.EXPORT_ITEMS_MULTI_VALUE_DELIM)));
      }
    }
    return result;
  }
}
