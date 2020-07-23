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

package org.apereo.openequella.tools.toolbox.api;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.Config;
import org.apereo.openequella.tools.toolbox.exportItems.ParsedItem;
import org.apereo.openequella.tools.toolbox.utils.FileUtils;
import org.apereo.openequella.tools.toolbox.utils.MigrationUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

// import com.kaltura.client.types.MediaEntry;

public class OpenEquellaRestUtils {
  public static final String KEY_ATTS = "attachments";
  public static final String KEY_ATT_TYPE = "type";
  public static final String KEY_ATT_TITLE = "title";
  public static final String KEY_ATT_DESC = "description";
  public static final String KEY_ATT_FILENAME = "filename";
  public static final String KEY_ATT_SIZE = "size";
  public static final String KEY_ATT_LINKS = "links";
  public static final String KEY_ATT_LINKS_VIEW = "view";
  public static final String KEY_ATT_MEDIA_ID = "mediaId";

  public static final String VAL_ATT_TYPE_KALTURA = "kaltura";
  public static final String VAL_ATT_TYPE_FILE = "file";

  private static Logger LOGGER = LogManager.getLogger(OpenEquellaRestUtils.class);

  private String accessToken = "NOTSET";

  private String downloadFolder;

  private String slash;

  private static SimpleDateFormat folderTimestamp = new SimpleDateFormat("yyyyMMdd-HHmmss-sss");

  private String currentDownloadFolder = "";

  private String currentDownloadFile = "";

  private boolean moreResourcesToCache = true;
  // Default
  private int cacheStatRequestedLength = 10;
  private int cacheStatAvailable = -1;
  private int cacheStatLength = -1;
  private int cacheStatStart = 0;

  public OpenEquellaRestUtils() {
    slash = Config.get(Config.GENERAL_OS_SLASH);
    if (Config.getInstance().hasConfig(Config.OEQ_SEARCH_API_REQUESTED_LENGTH)) {
      cacheStatRequestedLength =
          Config.getInstance().getConfigAsInt(Config.OEQ_SEARCH_API_REQUESTED_LENGTH);
    }
    File f = new File(Config.get(Config.GENERAL_DOWNLOAD_FOLDER));
    f.mkdirs();
    downloadFolder = f.getAbsolutePath();
  }

  public boolean gatherAccessToken() {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      String url =
          String.format(
              "%s/oauth/access_token?grant_type=client_credentials&client_id=%s&client_secret=%s&redirect_uri=default",
              Config.get(Config.OEQ_URL),
              Config.get(Config.OEQ_OAUTH_CLIENT_ID),
              Config.get(Config.OEQ_OAUTH_CLIENT_SECRET));
      HttpGet httpget = new HttpGet(url);

      LOGGER.trace("Finding the openEQUELLA access token using [{}]", url);

      HttpResponse response;

      response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();

      int statusCode = response.getStatusLine().getStatusCode();

      String respStr = EntityUtils.toString(entity);
      if (statusCode == HttpStatus.SC_OK) {
        JSONObject jresponse = new JSONObject(new JSONTokener(respStr));
        if (!jresponse.has("access_token")) {
          LOGGER.error(
              "FAILURE:  Request for openEQUELLA access_token didn't return expected JSON key: {}",
              respStr);
          return false;
        } else {
          accessToken = jresponse.getString("access_token");
        }
      } else {
        LOGGER.error(
            "FAILURE:  Request for openEQUELLA access_token failed with [{}]: {}",
            statusCode,
            respStr);
        return false;
      }

    } catch (ParseException e) {
      LOGGER.error(
          "FAILURE:  Request for openEQUELLA access_token failed with ParseException: {}",
          e.getMessage());
      LOGGER.error(e);
      return false;
    } catch (IOException e) {
      LOGGER.error(
          "FAILURE:  Request for openEQUELLA access_token failed with IOException: {}",
          e.getMessage());
      LOGGER.error(e);
      return false;
    } finally {
      try {
        httpclient.close();
      } catch (IOException e) {
        LOGGER.error(
            "FAILURE:  Request for openEQUELLA access_token failed.  Unable to close connection: {}",
            e.getMessage());
        LOGGER.error(e);
        return false;
      }
    }
    LOGGER.info("openEQUELLA access_token gathered: " + accessToken);
    return true;
  }

  // Ideally merge this with gatherItemsGeneral()
  public List<ParsedItem> gatherItems() throws Exception {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    String url =
        Config.get(Config.OEQ_URL)
            + Config.get(Config.OEQ_SEARCH_API)
            + "&start="
            + cacheStatStart
            + "&length="
            + cacheStatRequestedLength;
    HttpGet http = new HttpGet(url);

    http.addHeader("X-Authorization", "access_token=" + accessToken);
    HttpResponse response = httpclient.execute(http);
    HttpEntity entity = response.getEntity();

    int statusCode = response.getStatusLine().getStatusCode();

    String respStr = EntityUtils.toString(entity);
    LOGGER.trace(statusCode);
    LOGGER.trace(respStr);
    if (statusCode != 200) {
      String msg =
          String.format(
              "FAILURE accessing openEQUELLA search api [%s]:  %s - %s", url, statusCode, respStr);
      LOGGER.error(msg);
      throw new Exception(msg);
    }

    List<ParsedItem> cachedItems = new ArrayList<>();

    JSONObject searchResults = new JSONObject(respStr);
    int availableResults = confirmAndGatherInt(searchResults, "available");
    int length = confirmAndGatherInt(searchResults, "length");
    JSONArray itemsArr = confirmAndGatherJsonArray(searchResults, "results");
    int resultsLength = itemsArr.length();
    LOGGER.info(
        "Requested batch of resources.  API stats: start=[{}] returned length=[{}], available=[{}], # of results=[{}]",
        cacheStatStart,
        length,
        availableResults,
        resultsLength);
    if (resultsLength > 0) {
      cacheStatStart += resultsLength;
      for (int i = 0; i < itemsArr.length(); i++) {
        JSONObject resourceObj = itemsArr.getJSONObject(i);

        ParsedItem ei = new ParsedItem();
        ei.setUuid(confirmAndGatherString(resourceObj, "uuid"));
        ei.setVersion(confirmAndGatherInt(resourceObj, "version"));
        // TODO - add item filter here as well

        ei.setName(confirmAndGatherString(resourceObj, "name"));
        // 'description' can be optional.
        ei.setDescription(
            resourceObj.has("description") ? resourceObj.getString("description") : "");
        ei.setMetadata(confirmAndGatherString(resourceObj, "metadata"));
        try {
          String tags =
              MigrationUtils.findFirstOccurrenceInXml(
                  ei.getMetadata(), Config.get(Config.OEQ_SEARCH_KAL_TAGS_XPATH));
          ei.setKalturaTags(tags);
        } catch (Exception e) {
          String msg =
              String.format(
                  "FAILURE parsing / running the xpath query [%s] to find 'Kaltura Tags' in the openEQUELLA resource: %s",
                  Config.get(Config.OEQ_SEARCH_KAL_TAGS_XPATH), ei);
          LOGGER.error(msg);
          throw new Exception(msg);
        }
        ei.setJson(resourceObj);

        if (!checkIfResourceContainKalturaAttachment(resourceObj)) {
          LOGGER.info(
              "CACHED {}/{}: [{}] - MIGRATION CANDIDATE, Kaltura tags identified=[{}]",
              ei.getUuid(),
              ei.getVersion(),
              ei.getName(),
              ei.getKalturaTags());
          cachedItems.add(ei);
        } else {
          LOGGER.info(
              "IGNORED {}/{}: [{}] - NO MIGRATION NEEDED, resource already has a Kaltura attachment.",
              ei.getUuid(),
              ei.getVersion(),
              ei.getName());
        }
      }
    } else {
      moreResourcesToCache = false;
    }

    return cachedItems;
  }

  public List<ParsedItem> gatherItemsGeneral() throws Exception {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    String url =
        Config.get(Config.OEQ_URL)
            + Config.get(Config.OEQ_SEARCH_API)
            + "&start="
            + cacheStatStart
            + "&length="
            + cacheStatRequestedLength;
    HttpGet http = new HttpGet(url);

    http.addHeader("X-Authorization", "access_token=" + accessToken);
    LOGGER.debug("Making the API call: {}", url);
    HttpResponse response = httpclient.execute(http);
    HttpEntity entity = response.getEntity();

    int statusCode = response.getStatusLine().getStatusCode();

    String respStr = EntityUtils.toString(entity);
    LOGGER.trace(statusCode);
    LOGGER.trace(respStr);
    if (statusCode != 200) {
      String msg =
          String.format(
              "FAILURE accessing openEQUELLA search api [%s]:  %s - %s", url, statusCode, respStr);
      LOGGER.error(msg);
      throw new Exception(msg);
    }

    List<ParsedItem> cachedItems = new ArrayList<>();

    JSONObject searchResults = new JSONObject(respStr);
    cacheStatAvailable = confirmAndGatherInt(searchResults, "available");
    cacheStatLength = confirmAndGatherInt(searchResults, "length");
    JSONArray itemsArr = confirmAndGatherJsonArray(searchResults, "results");
    int resultsLength = itemsArr.length();
    LOGGER.info(
        "Requested batch of resources.  API stats: start=[{}] returned length=[{}], available=[{}], # of results=[{}]",
        cacheStatStart,
        cacheStatLength,
        cacheStatAvailable,
        resultsLength);
    if (resultsLength > 0) {
      cacheStatStart += resultsLength;
      for (int i = 0; i < itemsArr.length(); i++) {
        JSONObject resourceObj = itemsArr.getJSONObject(i);

        ParsedItem ei = new ParsedItem();
        ei.setUuid(confirmAndGatherString(resourceObj, "uuid"));
        ei.setVersion(confirmAndGatherInt(resourceObj, "version"));

        ei.setName(confirmAndGatherString(resourceObj, "name"));
        ei.setDescription(
                resourceObj.has("description") ? resourceObj.getString("description") : "");
        ei.setMetadata(confirmAndGatherString(resourceObj, "metadata"));
        ei.setCreatedDate(
                Config.DATE_FORMAT_OEQ_API.parse(confirmAndGatherString(resourceObj, "createdDate")));
        ei.setCreatedDateStr(confirmAndGatherString(resourceObj, "createdDate"));
        ei.setModifiedDateStr(confirmAndGatherString(resourceObj, "modifiedDate"));
        ei.setJson(resourceObj);

        LOGGER.info("CACHED {}", ei.getSignature());
        LOGGER.debug("CACHED JSON {}", ei.getJson());

        cachedItems.add(ei);
      }
    } else {
      moreResourcesToCache = false;
    }

    return cachedItems;
  }

  public boolean hasMoreResourcesToCache() {
    return moreResourcesToCache;
  }

  private int confirmAndGatherInt(JSONObject o, String key) throws Exception {
    if (!o.has(key)) throw new Exception("Unable to find the key " + key + " in " + o);
    return o.getInt(key);
  }

  private long confirmAndGatherLong(JSONObject o, String key) throws Exception {
    if (!o.has(key)) throw new Exception("Unable to find the key " + key + " in " + o);
    return o.getLong(key);
  }

  private String confirmAndGatherString(JSONObject o, String key) { // } throws Exception {
    if (!o.has(key)) {
      return null;
    } else {
      return o.getString(key);
    }
    // throw new Exception("Unable to find the key " + key + " in " + o);

  }

  private JSONArray confirmAndGatherJsonArray(JSONObject o, String key) throws Exception {
    if (!o.has(key)) throw new Exception("Unable to find the key " + key + " in " + o);
    return o.getJSONArray(key);
  }

  private JSONObject confirmAndGatherJsonObj(JSONObject o, String key) throws Exception {
    if (!o.has(key)) throw new Exception("Unable to find the key " + key + " in " + o);
    return o.getJSONObject(key);
  }

  // Only returns true if one of the attachments[X].type = kaltura
  private boolean checkIfResourceContainKalturaAttachment(JSONObject rsc) {
    if (rsc.has(KEY_ATTS)) {
      JSONArray atts = rsc.getJSONArray(KEY_ATTS);
      for (int i = 0; i < atts.length(); i++) {
        JSONObject att = atts.getJSONObject(i);
        if (att.has(KEY_ATT_TYPE)) {
          if (att.getString(KEY_ATT_TYPE).equals(VAL_ATT_TYPE_KALTURA)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  // Find and attempt to download the appropriate attachment to migrate
  // into Kaltura.
  // Criteria:
  // - Description contains a certain phrase
  // - filename should end in .mp4 or .mov
  // If multiple attachments meet the criteria, use the larger one
  public boolean downloadAttachmentForKaltura(ParsedItem ei) {
    LOGGER.info("{} - Beginning to process item.", ei.getSignature());

    int bestFitAttIndex = -1;
    String bestFitAttLink = "";
    String bestFitAttFilename = "";
    String bestFitFileSuffix = "";
    long bestFitAttFilesize = -1;
    JSONArray atts = null;

    JSONObject rsc = ei.getJson();
    if (rsc.has(KEY_ATTS)) {

      atts = rsc.getJSONArray(KEY_ATTS);
      for (int i = 0; i < atts.length(); i++) {

        JSONObject att = atts.getJSONObject(i);
        try {
          if (!confirmAndGatherString(att, KEY_ATT_TYPE).equals(VAL_ATT_TYPE_FILE)) {
            LOGGER.info("{} - Not an attachment to migrate - not a file: ", ei.getSignature(), att);
            continue;
          }

          if (!confirmAndGatherString(att, KEY_ATT_DESC)
              .contains(Config.get(Config.OEQ_SEARCH_ATT_DESC))) {
            LOGGER.info(
                "{} - Not an attachment to migrate - description did not contain [{}]: {}",
                ei.getSignature(),
                Config.get(Config.OEQ_SEARCH_ATT_DESC),
                att);
            continue;
          }
          String filename = confirmAndGatherString(att, KEY_ATT_FILENAME);
          String validSuffix = FileUtils.extractSuffix(filename);

          if (validSuffix == null) {
            LOGGER.info(
                "{} - Not an attachment to migrate - filename doesn't end with one of the following suffixes [{}] OR [{}]: {}",
                ei.getSignature(),
                Config.get(Config.OEQ_SEARCH_ATT_SUFFIXES_AUDIO),
                Config.get(Config.OEQ_SEARCH_ATT_SUFFIXES_VIDEO),
                att);
            continue;
          }

          Long size = confirmAndGatherLong(att, KEY_ATT_SIZE);
          if (size <= bestFitAttFilesize) {
            LOGGER.info(
                "{} - Not an attachment to migrate - it's a smaller size then another valid attachment to migrate: {}",
                ei.getSignature(),
                att);
            continue;
          }

          bestFitAttIndex = i;
          bestFitAttLink =
              confirmAndGatherString(
                  confirmAndGatherJsonObj(att, KEY_ATT_LINKS), KEY_ATT_LINKS_VIEW);
          bestFitAttFilesize = size;
          bestFitAttFilename = filename;
          bestFitFileSuffix = validSuffix;
        } catch (Exception e) {
          LOGGER.info("{} - Unable to check attachment - {}", ei.getSignature(), e.getMessage());
        }
      }
    }

    if (bestFitAttIndex != -1) {
      LOGGER.info(
          "{} - Found attachment to migrate: {}",
          ei.getSignature(),
          atts.getJSONObject(bestFitAttIndex));

      // Download attachment
      LOGGER.info(
          "{} - Using the attachment link for download: {}", ei.getSignature(), bestFitAttLink);

      File targetDir = new File(downloadFolder + slash + UUID.randomUUID());
      targetDir.mkdirs();
      currentDownloadFolder = targetDir.getAbsolutePath();

      File targetFile = new File(targetDir.getAbsolutePath() + slash + bestFitAttFilename);
      currentDownloadFile = targetFile.getAbsolutePath();
      if (FileUtils.downloadWithProgress(
          targetFile,
          bestFitAttLink,
          accessToken,
          Integer.parseInt(Config.get(Config.GENERAL_DOWNLOAD_CHATTER)),
          bestFitAttFilesize)) {
        ei.setFilepath(targetFile.getAbsolutePath());
        ei.setPrimaryFileType(bestFitFileSuffix);
        return true;
      }

      LOGGER.info("{} - Unable to download attachment to migrate to Kaltura.", ei.getSignature());
      return false;

    } else if (atts != null && atts.length() == 0) {
      LOGGER.info(
          "{} - Resource has no attachments.  Nothing to migrate to Kaltura.", ei.getSignature());
      return false;
    } else {
      LOGGER.info("{} - Unable to find attachment to migrate to Kaltura.", ei.getSignature());
      return false;
    }
  }

  // TODO need better process for Kaltura code.
  //	/**
  //	 *
  //	 * @param kalResource
  //	 * @param eqResource
  //	 */
  //	public boolean newVersionEquellaResourceWithKalturaAttachment(MediaEntry kalResource,
  // ParsedItem eqResource) {
  //		try {
  //
  //			JSONObject collJson = new JSONObject();
  //			collJson.put("uuid", eqResource.getJson().getJSONObject("collection").get("uuid"));
  //
  //			JSONArray atts = new JSONArray();
  //			JSONObject eqAttObj = MigrationUtils.convertKalturaToEquellaAttachment(kalResource,
  // config.getConfig(Config.OEQ_KAL_ID));
  //			atts.put(eqAttObj);
  //
  //			// Build the new version of the item in JSON format
  //			JSONObject newVerItem = new JSONObject();
  //			newVerItem.put("uuid", eqResource.getUuid());
  //			newVerItem.put("version", 0);
  //			newVerItem.put("name", eqResource.getName());
  //			newVerItem.put("description", eqResource.getDescription());
  //			String newMetadata =
  // MigrationUtils.cullAndReplaceXmlNodes(eqResource.getJson().getString("metadata"),
  // "temp_fileHandler", eqAttObj.getString("uuid"));
  //			newVerItem.put("metadata", newMetadata);
  //			newVerItem.put("status", "live");
  //			newVerItem.put("collection", collJson);
  //			newVerItem.put("attachments", atts);
  //
  //			// Upload the new version to Equella
  //			CloseableHttpClient httpclient = HttpClients.createDefault();
  //			String url = config.getConfig(Config.OEQ_URL) +
  // "/api/item/?draft=false&waitforindex=true&keeplocked=false";
  //			HttpPost http = new HttpPost(url);
  //
  //			LOGGER.info("Updating the openEQUELLA resource with a new version via " + url + " with " +
  // newVerItem.toString());
  //			http.addHeader("X-Authorization", "access_token=" + accessToken);
  //			StringEntity input = new StringEntity(newVerItem.toString());
  //			input.setContentType("application/json");
  //			http.setEntity(input);
  //			HttpResponse response = httpclient.execute(http);
  //			HttpEntity entity = response.getEntity();
  //
  //			int statusCode = response.getStatusLine().getStatusCode();
  //			if (statusCode == 201) {
  //				LOGGER.info("Successfully updated the openEQUELLA resource with a new version at: {}/{}",
  // eqResource.getUuid(), (eqResource.getVersion()+1));
  //				entity.getContent().close();
  //				FileUtils.removeFileAndParent(currentDownloadFolder, currentDownloadFile);
  //				return true;
  //			} else {
  //				LOGGER.warn("Unable to new version the openEQUELLA resource:  Status code on-save was: {}",
  // statusCode);
  //				LOGGER.warn("openEQUELLA response from trying to update the resource: {}",
  // EntityUtils.toString(entity));
  //			}
  //		} catch (Exception e) {
  //			LOGGER.warn("Unable to new version the openEQUELLA resource: {}", e.getMessage(), e);
  //		}
  //		return false;
  //	}
}
