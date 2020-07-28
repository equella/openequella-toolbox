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

package org.apereo.openequella.tools.toolbox.checkFiles;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apereo.openequella.tools.toolbox.Config;
import org.apereo.openequella.tools.toolbox.utils.Check;
import org.apereo.openequella.tools.toolbox.utils.CheckFilesUtils;
import org.apereo.openequella.tools.toolbox.utils.Pair;
import org.apereo.openequella.tools.toolbox.utils.WhereClauseExpression;

/**
 * Walk through the filestore / institionName / Attachments directory. For each uuid / version,
 * query the DB for the attachments. Then confirm each attachment referenced in the DB is present in
 * the uuid / version 's filestore.
 */
public class CheckFilesDbHandler {
  private static final Logger logger = LogManager.getLogger(CheckFilesDbHandler.class);

  private Connection con;
  private Map<Integer, CollectionRow> collectionsById = new HashMap<Integer, CollectionRow>();
  private Map<String, InstitutionRow> institutionsByShortname =
      new HashMap<String, InstitutionRow>();
  private Map<Integer, InstitutionRow> institutionsById = new HashMap<Integer, InstitutionRow>();
  // item id is the key. First ResultsRow in the cache list is the item
  // details.
  private Map<Integer, List<ResultsRow>> attachmentsCache =
      new HashMap<Integer, List<ResultsRow>>();

  private List<WhereClauseExpression> whereClauseExpressions =
      new ArrayList<WhereClauseExpression>();

  public boolean execute() {
    // Setup database connection
    setupDbConnection(
        Config.get(Config.CF_DB_URL),
        Config.get(Config.CF_DB_USERNAME),
        Config.get(Config.CF_DB_PASSWORD));
    if (ReportManager.getInstance().hasFatalErrors()) {
      closeConnection();
      return false;
    }

    cacheCollections();
    if (ReportManager.getInstance().hasFatalErrors()) {
      closeConnection();
      return false;
    }

    cacheInstitutions();
    if (ReportManager.getInstance().hasFatalErrors()) {
      closeConnection();
      return false;
    }

    confirmFilterByCollection();
    if (ReportManager.getInstance().hasFatalErrors()) {
      closeConnection();
      return false;
    }

    confirmFilterByInstitution();
    if (ReportManager.getInstance().hasFatalErrors()) {
      closeConnection();
      return false;
    }

    confirmAllInstitutionFilestores();
    if (ReportManager.getInstance().hasFatalErrors()) {
      closeConnection();
      return false;
    }

    if (!ReportManager.getInstance().setupReportDetails()) {
      closeConnection();
      return false;
    }

    if (Config.CheckFilesType.valueOf(Config.get(Config.CF_MODE))
        == Config.CheckFilesType.DB_BATCH_ITEMS_PER_ITEM_ATTS_CONFIRM_INLINE) {
      if (!cacheItemsBatched(true)) {
        closeConnection();
        return false;
      }
    } else {
      // Cache Items
      if (Config.get(Config.CF_MODE).contains("ALL_ITEMS")) {
        if (!cacheItemsAll()) {
          closeConnection();
          return false;
        }
      } else {
        if (!cacheItemsBatched(false)) {
          closeConnection();
          return false;
        }
      }

      // Cache Attachments
      if (Config.get(Config.CF_MODE).endsWith("ALL_ATTACHMENTS")) {
        if (!cacheAttachmentsAll()) {
          closeConnection();
          return false;
        }
      } else {
        if (!cacheAttachmentsPerItem()) {
          closeConnection();
          return false;
        }
      }

      // Check attachments one item at a time
      int counter = 0;
      for (Integer itemId : attachmentsCache.keySet()) {
        counter++;
        if (!processItem(itemId, counter)) {
          closeConnection();
          return false;
        }
      }
    }

    closeConnection();
    return true;
  }

  private boolean closeConnection() {
    try {
      if (con != null) {
        con.close();
        logger.debug("Closed DB connection");
      }
      return true;
    } catch (SQLException e) {
      logger.error("Unable to close connection due to {}", e.getMessage(), e);
      return false;
    }
  }

  /**
   * @param itemId assumes this exists in the cache.
   * @param itemCounter item counter
   * @return
   */
  private boolean processItem(int itemId, int itemCounter) {
    try {
      logger.info("Processing item [{}]...", itemCounter);
      ReportManager.getInstance().getStats().incNumTotalItems();

      List<ResultsRow> attachments = attachmentsCache.get(itemId);
      // Remember, the attachmentsCache for an item is always seeded with
      // an 'item' results row.
      // If there is only the seeded ResultsRow, there were no attachments
      // found.
      if (attachments.size() == 1) {
        attachments.get(0).setAttStatus(ResultsRow.NOATT);
        ReportManager.getInstance().stdOutWriteln(attachments.get(0).toString());
      } else {
        boolean allGood = true;
        final int rawTotal = attachments.size();
        final int attTotal = rawTotal - 1; // See reason below.
        // Start attCounter at 1 since the first element is always the
        // item framework.
        for (int attCounter = 1; attCounter < rawTotal; attCounter++) {
          final String stat =
              String.format("item [%s] - att [%s]/[%s]", itemCounter, attCounter, attTotal);
          allGood &= checkAttachment(stat, attachments.get(attCounter));
        }
        if (!allGood) {
          ReportManager.getInstance().getStats().incNumTotalItemsAffected();
        }
      }
    } catch (IOException e) {
      // TODO - add to 'fatal errors' since returning false to stop the run
      logger.fatal("Unrecoverable error while processing item - {}", e.getMessage(), e);
      return false;
    }
    return true;
  }

  private boolean checkAttachment(String stat, ResultsRow attRow) throws IOException {
    ReportManager.getInstance().getStats().incNumTotalAttachments();
    logger.info("Processing item attachment {}", stat);

    // Determine attachment status
    if (attRow.getAttStatus().equals(ResultsRow.IGNORED)) {
      // Consider not an error.
      logger.info("Attachment {} is ignored.", attRow.getAttFilePath());
      ReportManager.getInstance().getStats().incNumTotalAttachmentsIgnored();
    } else if (doesAttachmentExist(stat, attRow)) {
      logger.info("Attachment {} is present.", attRow.getAttFilePath());
      attRow.setAttStatus(ResultsRow.PRESENT);
    } else {
      logger.info("Attachment {} is missing.", attRow.getAttFilePath());
      ReportManager.getInstance().getStats().incNumTotalAttachmentsMissing();
      attRow.setAttStatus(ResultsRow.MISSING);
      ReportManager.getInstance().dualWriteLn(attRow.toString());
      return false;
    }
    // Always send the attachment report to the standard file
    // writer.
    ReportManager.getInstance().stdOutWriteln(attRow.toString());
    return true;
  }

  private boolean doesAttachmentExist(String stat, ResultsRow attRow) {
    int hash = attRow.getItemUuid().hashCode() & 127;

    String attPath =
        String.format("/%d/%s/%s/", hash, attRow.getItemUuid(), attRow.getItemVersion());

    // Prevent odd encoding issues by not taking part in the format operation
    // above.  Allow for a configured set of percent encoded values
    String attName = attRow.getAttFilePath();
    logger.debug("Original attachment name: [{}]", attName);
    if (Config.getInstance().hasConfig(Config.CF_FILENAME_ENCODING_LIST)) {
      for (String key :
          Config.getInstance().getConfigAsStringArray(Config.CF_FILENAME_ENCODING_LIST)) {
        final String original =
            CheckFilesUtils.specialCharReplace(
                Config.get(
                    Config.CF_FILENAME_ENCODING_BASE + key + Config.CF_FILENAME_ENCODING_ORIGINAL));
        final String result =
            Config.get(Config.CF_FILENAME_ENCODING_BASE + key + Config.CF_FILENAME_ENCODING_RESULT);
        logger.trace("Attachment replacement [{}]->[{}]", original, result);
        attName = attName.replaceAll(original, result);
        logger.trace(
            "Attachment name after a replacement of [{}]->[{}]: [{}]", original, result, attName);
      }
    }

    String basePath =
        Config.get(Config.CF_FILESTORE_DIR)
            + "/"
            + institutionsById.get(attRow.getInstitutionId()).getFilestoreHandle()
            + "/Attachments";

    // See if this attachment is in an 'advanced filestore'.
    final String key =
        Config.CF_FILESTORE_DIR_ADV + attRow.getInstitutionId() + "." + attRow.getCollectionUuid();
    logger.debug(
        "Checking if the attachment [{}][{}] has an associated adv filestore via the property [{}].",
        attPath,
        attName,
        key);
    if (Config.getInstance().hasConfig(key)) {
      basePath = Config.get(key);
    }

    final String path = basePath + attPath + attName;
    final String altPath = basePath + "/" + attRow.getCollectionUuid() + attPath + attName;

    if (Config.getInstance().hasConfig(key)) {
      logger.info("Using advanced filestore path [{}] to check attachment.", path);
    } else {
      logger.info("Using path [{}] to check attachment.", path);
    }
    logger.info("Alt path [{}] to check attachment.", altPath);
    logger.info("Att type [{}].", attRow.getAttType());

    final File stdFile = new File(path);
    final File altFile = new File(altPath);

    if (attRow.getAttType().equals(Config.ATT_TYPE_ZIP_ENTRY)) {
      if (attRow.getAttStatus().equals("IN_PROGRESS_ZIP_DIR")) {
        if ((stdFile.exists() && stdFile.isDirectory())
            || (altFile.exists() && altFile.isDirectory())) {
          return true;
        }
        return failAndRunChecks(stdFile, altFile, false);
      }
      // Otherwise, its supposed to be a file
      if ((stdFile.exists() && stdFile.isFile()) || (altFile.exists() && altFile.isFile())) {
        return true;
      }
      return failAndRunChecks(stdFile, altFile);
    } else if (attRow.getAttType().equals(Config.ATT_TYPE_ZIP)) {
      // need to check zip
      if (stdFile.exists() && stdFile.isFile()) {
        checkZip(stat, path.substring(0, path.length() - attName.length()), attName, attRow);
        return true;
      } else if (altFile.exists() && altFile.isFile()) {
        checkZip(stat, altPath.substring(0, altPath.length() - attName.length()), attName, attRow);
        return true;
      }
      return failAndRunChecks(stdFile, altFile);
    }

    // if we get to this point, it should be a normal file
    if ((stdFile.exists() && stdFile.isFile()) || (altFile.exists() && altFile.isFile())) {
      return true;
    }
    return failAndRunChecks(stdFile, altFile);
  }

  private void logIfExistsAndDirOrFile(File f, String type, boolean shouldBeAFile) {
    if (f.exists() && shouldBeAFile && f.isDirectory()) {
      logger.warn("{} file location exists, but is a directory!  [{}]", type, f.getAbsolutePath());
    } else if (f.exists() && !shouldBeAFile && f.isFile()) {
      logger.warn("{} file location exists, but is a directory!  [{}]", type, f.getAbsolutePath());
    }
  }

  private boolean failAndRunChecks(File stdFile, File altFile) {
    logIfExistsAndDirOrFile(stdFile, "standard", true);
    logIfExistsAndDirOrFile(altFile, "alternate", true);
    return false;
  }

  private boolean failAndRunChecks(File stdFile, File altFile, boolean shouldBeAFile) {
    logIfExistsAndDirOrFile(stdFile, "standard", shouldBeAFile);
    logIfExistsAndDirOrFile(altFile, "alternate", shouldBeAFile);
    return false;
  }

  private void checkZip(String stat, String path, String name, ResultsRow parentAttsRow) {
    try {
      List<ResultsRow> zipAtts = new ArrayList<>();
      ZipFile zipFile = new ZipFile(path + Config.get(Config.GENERAL_OS_SLASH) + name);
      // Unzipped files are stored in a directory named [zip-name].
      // The zip attachment name's format is _zips/[zip-name].zip
      final String unzippedLocation =
          (path.length() > 7) ? name.substring(6) : "UNKNOWN_ZIP_LOCATION";
      File zipDir = new File(path + Config.get(Config.GENERAL_OS_SLASH) + unzippedLocation);
      // There is a risk here that the zip file was expanded and the unzipped location
      // doesn't exist (instead of the user choosing to not expand the zip file)
      logger.info(
          "Reviewing the contents of the zip at [{}]/[{}]/[{}]", path, name, unzippedLocation);

      if (zipDir.exists() && zipDir.isDirectory()) {
        logger.info(
            "Checking the contents of the zip at [{}]/[{}]/[{}]", path, name, unzippedLocation);
        // Treat each zip entry as a file attachment
        // Build a new results row for each zip entry
        zipFile
            .stream()
            .forEach(
                ze -> {
                  ResultsRow rr = ResultsRow.buildItemFrame(parentAttsRow);
                  rr.setAttType(Config.ATT_TYPE_ZIP_ENTRY);
                  rr.setAttFilePath(
                      unzippedLocation + Config.get(Config.GENERAL_OS_SLASH) + ze.getName());
                  rr.setAttUuid(parentAttsRow.getAttUuid());
                  rr.setAttStatus(ze.isDirectory() ? "IN_PROGRESS_ZIP_DIR" : "IN_PROGESS_ZIP_FILE");
                  logger.info("Found zip entry: [{}]", rr.toString());
                  zipAtts.add(rr);
                });
        for (int i = 0; i < zipAtts.size(); i++) {
          checkAttachment(
              stat + String.format(" - sub att [%s]/[%s]", i + 1, zipAtts.size()), zipAtts.get(i));
        }
      } else {
        logger.info(
            "NOT checking the contents of the zip since the unzipped directory does not exist [{}]/[{}]/[{}]",
            path,
            name,
            unzippedLocation);
      }
    } catch (IOException ioe) {
      String msg =
          String.format(
              "Something bad happened when checking the zipfile:  [%s]", ioe.getMessage());
      logger.fatal(msg);
      ReportManager.getInstance().addFatalError(msg);
    }
  }

  /**
   * if there is a filter.by.institution.shortname specified, confirm there's a corresponding entry
   * in the institution cache.
   *
   * <p>Sets a ReportManager fatal error if there isn't a corresponding entry.
   *
   * @return
   */
  private void confirmFilterByInstitution() {
    String shortname = Config.get(Config.CF_FILTER_BY_INSTITUTION);
    if (Check.isEmpty(shortname)) {
      // non-existent / empty means check all institutions.
      return;
    }

    if (!institutionsByShortname.containsKey(shortname)) {
      String msg =
          String.format(
              "The institution shortname to filter by [%s] is not in the institution cache.",
              shortname);
      logger.fatal(msg);
      ReportManager.getInstance().addFatalError(msg);
      return;
    }

    whereClauseExpressions.add(
        new WhereClauseExpression(
            "institution_id = ?",
            institutionsByShortname.get(shortname).getId(),
            whereClauseExpressions.size() + 1));
  }

  /**
   * Using the institution cache, determine if all filestores are accounted for.
   *
   * <p>Sets a ReportManager fatal error if there are any institutions without a corresponding
   * filestore.
   *
   * @return
   */
  private void confirmAllInstitutionFilestores() {
    String filter = Config.get(Config.CF_FILTER_BY_INSTITUTION);
    for (String shortname : institutionsByShortname.keySet()) {
      // filter == null >> looking at all institutions
      // filter == shortname >> only check that institution filestore
      // handle
      if ((Check.isEmpty(filter) || filter.equals(shortname))) {
        InstitutionRow ir = institutionsByShortname.get(shortname);
        ir.setFilestoreHandle(shortname);
        File instFilestore = new File(Config.get(Config.CF_FILESTORE_DIR), ir.getFilestoreHandle());
        if (!instFilestore.exists()) {
          logger.info(
              "Institution filestore [{}] does not exist.  Checking for an alias...",
              instFilestore.getAbsolutePath());
          // Might have changed the filestore handle. The config
          // should
          // specify.
          String handle = Config.getInstance().getFilestoreHandle(shortname);
          if (handle == null || handle.isEmpty()) {
            // Nothing specified. Fail.
            String msg =
                String.format(
                    "Institution [%s] filestore handle is different than shortname, "
                        + "but is not specified in the properties.",
                    shortname);
            ReportManager.getInstance().addFatalError(msg);
            logger.error(msg);
            return;
          }
          ir.setFilestoreHandle(handle);
          instFilestore = new File(Config.get(Config.CF_FILESTORE_DIR), ir.getFilestoreHandle());
          if (!instFilestore.exists()) {
            // Bad handle. Fail.
            String msg =
                String.format(
                    "Institution [%s] filestore handle is different than shortname, "
                        + "but the handle (directory) specified in the properties [%s] does not exist.",
                    shortname, handle);
            ReportManager.getInstance().addFatalError(msg);
            logger.error(msg);
            return;
          }
        }

        // Check that the Attachments directory exists
        File attsHandle = new File(instFilestore, "Attachments");
        if (!attsHandle.exists()) {
          // Bad handle. Fail.
          String msg =
              String.format(
                  "Institution [%s] filestore attachments directory [%s] does not exist.",
                  shortname, attsHandle.getAbsolutePath());
          ReportManager.getInstance().addFatalError(msg);
          logger.error(msg);
          return;
        }

        if (!attsHandle.isDirectory()) {
          // Bad handle. Fail.
          String msg =
              String.format(
                  "Institution [{}] filestore attachments directory [{}] is not a directory.",
                  shortname,
                  attsHandle.getAbsolutePath());
          ReportManager.getInstance().addFatalError(msg);
          logger.error(msg);
          return;
        }
      }
    }
  }

  private void setupDbConnection(String dbUrl, String un, String pw) {
    try {
      Class.forName("org.postgresql.Driver");
      String connectionUrl = String.format("%s?user=%s&password=%s", dbUrl, un, pw);
      con = DriverManager.getConnection(connectionUrl);
      logger.info(String.format("Connected to %s", dbUrl));
      con.setAutoCommit(false);
      // Check the connection works.
      String SQL = "SELECT id, uuid FROM item limit 1";
      Statement stmt = con.createStatement();
      long start = System.currentTimeMillis();
      ResultSet rs = stmt.executeQuery(SQL);
      boolean hasResults = rs.next();
      rs.close();
      long dur = System.currentTimeMillis() - start;
      if (hasResults) {
        logger.info("Confirmed DB connection in 0 ms.");
        ReportManager.getInstance().getStats().queryRan(dur);
        return;
      } else {
        String msg =
            String.format(
                "Unable to confirm connection to the DB [%s] with username [%s] and a password of length [%d]",
                Config.get(Config.CF_DB_URL),
                Config.get(Config.CF_DB_USERNAME),
                Config.get(Config.CF_DB_PASSWORD).length());
        logger.fatal(msg);
        ReportManager.getInstance().addFatalError(msg);
        return;
      }
    } catch (Exception e) {
      String msg =
          String.format(
              "Unable to connect to the DB [%s] with username [%s] and a password of length [%d]",
              Config.get(Config.CF_DB_URL),
              Config.get(Config.CF_DB_USERNAME),
              Config.get(Config.CF_DB_PASSWORD).length());
      logger.fatal(msg, e);
      ReportManager.getInstance().addFatalError(msg);
      return;
    }
  }

  private boolean cacheAttachmentsPerItem() {
    long start = System.currentTimeMillis();
    int counter = 0;
    try {
      // For each item cached, query for the associated attachments.
      for (List<ResultsRow> itemRowSet : attachmentsCache.values()) {
        Pair<Boolean, Integer> result = cacheAttachmentsPerItem(itemRowSet.get(0).getItemId());
        if (!result.getFirst()) {
          return false;
        }
        counter += result.getSecond();
      }
    } catch (Exception e) {
      logger.error("Unable to cache attachments (query per item) - {}", e.getMessage(), e);
      return false;
    }
    long dur = System.currentTimeMillis() - start;
    logger.info("Cached {} attachments (query per item).  Duration {} ms.", counter, dur);
    ReportManager.getInstance().getStats().incNumGrandTotalAttachments(counter);
    return true;
  }

  private Pair<Boolean, Integer> cacheAttachmentsPerItem(int itemId) {
    long start = System.currentTimeMillis();
    int counter = 0;
    try {
      long batchStart = System.currentTimeMillis();

      PreparedStatement stmt =
          con.prepareStatement(
              "select a.type, a.url, a.value1, a.uuid, a.id "
                  + "from attachment a where a.item_id = ?");
      stmt.setInt(1, itemId);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        counter++;
        ResultsRow attRow =
            inflateAttRow(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getInt(5),
                itemId);
        // Note - at this point, attRow will not be null.
        attachmentsCache.get(attRow.getItemId()).add(attRow);
        logger.info("Attachment ({}) gathered: {}", counter, attRow.toString());
      }
      rs.close();
      long batchDur = System.currentTimeMillis() - batchStart;
      ReportManager.getInstance().getStats().queryRan(batchDur);
      logger.debug("Cached a batch of attachments.  Duration {} ms.", batchDur);
    } catch (Exception e) {
      logger.error("Unable to cache attachments (query per item) - {}", e.getMessage(), e);
      return Pair.pair(false, counter);
    }
    long dur = System.currentTimeMillis() - start;
    logger.info("Cached {} attachments (query per item).  Duration {} ms.", counter, dur);
    ReportManager.getInstance().getStats().incNumGrandTotalAttachments(counter);
    return Pair.pair(true, counter);
  }

  private boolean cacheAttachmentsAll() {
    long start = System.currentTimeMillis();
    int counter = 0;
    try {
      PreparedStatement stmt =
          con.prepareStatement(
              "select a.type, a.url, a.value1, a.uuid, a.id, a.item_id " + "from attachment a");
      ResultSet rs = stmt.executeQuery();
      // For each attachment, access the appropriate item attachment list
      // and add the attachment
      while (rs.next()) {
        counter++;
        ResultsRow attRow =
            inflateAttRow(
                rs.getString(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getInt(5),
                rs.getInt(6));
        if (attRow != null) {
          attachmentsCache.get(attRow.getItemId()).add(attRow);
          logger.info("Attachment ({}) gathered: {}", counter, attRow.toString());
        }
      }
      rs.close();
    } catch (Exception e) {
      logger.error("Unable to cache attachments (single query) - {}", e.getMessage(), e);
      return false;
    }
    long dur = System.currentTimeMillis() - start;
    ReportManager.getInstance().getStats().queryRan(dur);
    logger.info("Cached {} attachments (single query).  Duration {} ms.", counter, dur);
    ReportManager.getInstance().getStats().incNumGrandTotalAttachments(counter);
    return true;
  }

  private ResultsRow inflateAttRow(
      String attType, String attUrl, String value1, String attUuid, int attId, int itemId) {
    // If an attachment was found without the parent item, log a
    // warning, but effectively ignore.
    if (!attachmentsCache.containsKey(itemId)) {
      logger.warn(
          "Found an attachment whose itemId DOES NOT EXIST in the item cache.  "
              + "Ignoring (not caching): type=[{}], url=[{}], value1=[{}], uuid=[{}], attId=[{}], itemId=[{}]",
          attType,
          attUrl,
          value1,
          attUuid,
          attId,
          itemId);
      return null;
    }

    // Build out the general attributes
    ResultsRow attRow = ResultsRow.buildItemFrame(attachmentsCache.get(itemId).get(0));

    // inflate the static metadata
    attRow.setAttId(attId);
    attRow.setAttUuid(attUuid);
    attRow.setAttUrl(attUrl);
    attRow.setAttType(attType);

    if (attRow.getAttType().equals(Config.ATT_TYPE_FILE)) {
      attRow.setAttFilePath(attRow.getAttUrl());
    } else if (attRow.getAttType().equals(Config.ATT_TYPE_ZIP)) {
      attRow.setAttFilePath(attRow.getAttUrl());
    } else if (attRow.getAttType().equals(Config.ATT_TYPE_HTML)) {
      attRow.setAttFilePath(String.format("_mypages/%s/page.html", attRow.getAttUuid()));
    } else if (attRow.getAttType().equals(Config.ATT_TYPE_IMS)) {
      // TODO
      attRow.setAttStatus(ResultsRow.IGNORED);
    } else if (attRow.getAttType().equals(Config.ATT_TYPE_IMSRES)) {
      // TODO
      attRow.setAttStatus(ResultsRow.IGNORED);
    } else if (attRow.getAttType().equals(Config.ATT_TYPE_CUSTOM)
        && value1.equals(Config.ATT_TYPE_CUSTOM_SCORM)) {
      attRow.setAttType(Config.ATT_TYPE_CUSTOM_SCORM);
      // Unsupported for now.
      // filepath = String.format("_IMS/%s", attRow.getAttUrl());
      // TODO
      attRow.setAttStatus(ResultsRow.IGNORED);
    } else {
      // Ignore all other attachments
      // 'attachment', 'custom' > !'scorm', etc
      attRow.setAttStatus(ResultsRow.IGNORED);
    }

    return attRow;
  }

  private void cacheCollections() {
    long start = System.currentTimeMillis();
    try {
      PreparedStatement stmt =
          con.prepareStatement(
              "select c.id, e.uuid, e.institution_id "
                  + "from item_definition c "
                  + "inner join base_entity e on c.id = e.id");
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        CollectionRow cr = new CollectionRow();
        cr.setId(rs.getInt(1));
        cr.setInstitutionId(rs.getInt(3));
        cr.setUuid(rs.getString(2));

        collectionsById.put(cr.getId(), cr);
        // collectionsByUuid.put(rs.getString(2), rs.getInt(1));
        logger.info("Cached collection:  {}", cr.toString());
      }
      rs.close();
    } catch (Exception e) {
      String msg = String.format("Unable to cache collections - %s", e.getMessage());
      logger.fatal(msg, e);
      ReportManager.getInstance().addFatalError(msg);
      return;
    }
    long dur = System.currentTimeMillis() - start;
    ReportManager.getInstance().getStats().queryRan(dur);
    logger.info("Cached collections.  Duration {} ms.", dur);
    return;
  }

  private void cacheInstitutions() {
    long start = System.currentTimeMillis();
    try {
      PreparedStatement stmt =
          con.prepareStatement(
              "select i.id, i.short_name, i.name, i.unique_id, i.url "
                  + "from institution i order by i.short_name");
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        InstitutionRow ir = new InstitutionRow();
        ir.setId(rs.getInt(1));
        ir.setShortname(rs.getString(2));
        ir.setName(rs.getString(3));
        ir.setUniqueId(rs.getString(4));
        ir.setUrl(rs.getString(5));
        institutionsByShortname.put(ir.getShortname(), ir);
        institutionsById.put(ir.getId(), ir);
        logger.info("Cached institution: {}", ir.toString());
      }
      rs.close();
    } catch (Exception e) {
      String msg = String.format("Unable to cache institutions - %s", e.getMessage());
      logger.fatal(msg, e);
      ReportManager.getInstance().addFatalError(msg);
      return;
    }
    long dur = System.currentTimeMillis() - start;
    ReportManager.getInstance().getStats().queryRan(dur);
    logger.info("Cached institutions.  Duration {} ms.", dur);
    return;
  }

  private void confirmFilterByCollection() {
    if (Check.isEmpty(Config.get(Config.CF_FILTER_BY_COLLECTION))) {
      logger.info("Filter by collection ID not specified.  Not filtering by collection");
      return;
    }

    final int id = Config.getInstance().getConfigAsInt(Config.CF_FILTER_BY_COLLECTION);
    if (!collectionsById.containsKey(id)) {
      String msg =
          String.format(
              "Unable to find the collection ID [%s].  Not a valid filter.",
              Config.get(Config.CF_FILTER_BY_COLLECTION));
      logger.fatal(msg);
      ReportManager.getInstance().addFatalError(msg);
      return;
    }

    whereClauseExpressions.add(
        new WhereClauseExpression("item_definition_id = ?", id, whereClauseExpressions.size() + 1));
  }

  private boolean cacheItemsAll() {
    long start = System.currentTimeMillis();
    try {
      PreparedStatement stmt =
          con.prepareStatement(
              String.format(
                  "select id, uuid, version, status, item_definition_id, institution_id from item %s order by id",
                  WhereClauseExpression.makeWhereClause(whereClauseExpressions)));
      WhereClauseExpression.setParms(stmt, whereClauseExpressions);

      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        ResultsRow itemRow = new ResultsRow();
        itemRow.setItemId(rs.getInt(1));
        itemRow.setItemUuid(rs.getString(2));
        itemRow.setItemVersion("" + rs.getInt(3));
        itemRow.setItemStatus(rs.getString(4));
        int collId = rs.getInt(5);
        if (collectionsById.containsKey(collId)) {
          itemRow.setCollectionUuid(collectionsById.get(collId).getUuid());
        } else {
          logger.warn(
              "Expected collection ID [{}] not cached for item {}/{}.",
              collId,
              itemRow.getItemUuid(),
              itemRow.getItemVersion());
          itemRow.setCollectionUuid("" + collId);
        }
        itemRow.setInstitutionId(rs.getInt(6));
        itemRow.setInstitutionShortname(
            institutionsById.get(itemRow.getInstitutionId()).getShortname());
        List<ResultsRow> seeder = new ArrayList<ResultsRow>();
        seeder.add(itemRow);
        attachmentsCache.put(itemRow.getItemId(), seeder);

        logger.info("Found [{}] item:  {}", (attachmentsCache.size()), itemRow.toString());
      }
      rs.close();
    } catch (Exception e) {
      logger.error("Unable to cache items (single query) - {}", e.getMessage(), e);
      return false;
    }
    long dur = System.currentTimeMillis() - start;
    ReportManager.getInstance().getStats().queryRan(dur);
    logger.info("Cached items (single query).  Duration {} ms.", dur);
    ReportManager.getInstance().getStats().incNumGrandTotalItems(attachmentsCache.size());
    return true;
  }

  /**
   * Fails in SQL Server 2008. Works in SQL Server 2012.
   *
   * @return
   */
  private boolean cacheItemsBatched(boolean confirmInline) {
    long start = System.currentTimeMillis();
    try {
      String sql =
          String.format(
              "select id, uuid, version, status, item_definition_id, institution_id from item %s order by id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
              WhereClauseExpression.makeWhereClause(whereClauseExpressions));
      logger.debug("SQL:  [{}]", sql);
      int offsetCounter = 0;
      boolean hasMore = false;
      do {
        // Assume no rows are returned.
        hasMore = false;
        long batchStart = System.currentTimeMillis();

        PreparedStatement stmt = con.prepareStatement(sql);
        WhereClauseExpression.setParms(stmt, whereClauseExpressions);
        stmt.setInt(
            whereClauseExpressions.size() + 1,
            offsetCounter * Config.getInstance().getConfigAsInt(Config.CF_NUM_OF_ITEMS_PER_QUERY));
        stmt.setInt(
            whereClauseExpressions.size() + 2,
            Config.getInstance().getConfigAsInt(Config.CF_NUM_OF_ITEMS_PER_QUERY));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
          hasMore = true;
          ResultsRow itemRow = new ResultsRow();
          itemRow.setItemId(rs.getInt(1));
          itemRow.setItemUuid(rs.getString(2));
          itemRow.setItemVersion("" + rs.getInt(3));
          itemRow.setItemStatus(rs.getString(4));
          int collId = rs.getInt(5);
          if (collectionsById.containsKey(collId)) {
            itemRow.setCollectionUuid(collectionsById.get(collId).getUuid());
          } else {
            logger.warn(
                "Expected collection ID [{}] not cached for item {}/{}.",
                collId,
                itemRow.getItemUuid(),
                itemRow.getItemVersion());
            itemRow.setCollectionUuid("" + collId);
          }
          itemRow.setInstitutionId(rs.getInt(6));
          itemRow.setInstitutionShortname(
              institutionsById.get(itemRow.getInstitutionId()).getShortname());

          List<ResultsRow> seeder = new ArrayList<ResultsRow>();
          seeder.add(itemRow);
          attachmentsCache.put(itemRow.getItemId(), seeder);

          logger.info("Found the [{}]th item:  {}", (attachmentsCache.size()), itemRow.toString());
          if (confirmInline) {
            Pair<Boolean, Integer> result = cacheAttachmentsPerItem(itemRow.getItemId());
            if (result.getFirst()) {
              if (!processItem(itemRow.getItemId(), attachmentsCache.size())) {
                rs.close();
                throw new Exception(
                    "Unable to check attachments for item [" + itemRow.getItemId() + "]");
              }
            } else {
              rs.close();
              throw new Exception(
                  "Unable to cache attachments for item [" + itemRow.getItemId() + "]");
            }
          }
        }
        rs.close();
        offsetCounter++;
        long batchDur = System.currentTimeMillis() - batchStart;
        ReportManager.getInstance().getStats().queryRan(batchDur);
        if (hasMore) {
          logger.info("Cached a batch of items.  Duration {} ms.", batchDur);
        } else {
          logger.info("No more batches of items found to cache.  Duration {} ms.", batchDur);
        }

      } while (hasMore);
    } catch (Exception e) {
      logger.error("Unable to cache items (batched queries) - {}", e.getMessage(), e);
      return false;
    }
    long dur = System.currentTimeMillis() - start;
    logger.info("Cached items (batched queries).  Total duration {} ms.", dur);
    ReportManager.getInstance().getStats().incNumGrandTotalItems(attachmentsCache.size());
    return true;
  }
}
