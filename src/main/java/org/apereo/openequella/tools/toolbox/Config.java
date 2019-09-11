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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {

	private static Config config;

  public static final String VERSION = "1.4.1-SNAPSHOT";
	// toolbox function
	public static final String TOOLBOX_FUNCTION = "toolbox.function";
	public static enum ToolboxFunction {
		MigrateToKaltura,
		ExportItems,
		Email,
		FileLister,
		ThumbnailV1,
		JsonReport,
		CheckFiles,
		AttachmentHash
	}

	//Example 2013-01-18T17:38:47.986-07:00
	public static final SimpleDateFormat DATE_FORMAT_OEQ_API = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	//Example 2013-01-18
	public static final SimpleDateFormat DATE_FORMAT_CONFIG_FILE = new SimpleDateFormat("yyyy-MM-dd");

	// MigrateToKaltura
	public static final String KAL_PARTNER_ID = "migrate-to-kaltura.kal.partner.id";
	public static final String KAL_USER_ID = "migrate-to-kaltura.kal.user.id";
	public static final String KAL_ADMIN_SECRET = "migrate-to-kaltura.kal.admin.secret";
	public static final String KAL_SERVICE_URL = "migrate-to-kaltura.kal.service.url";
	public static final String KAL_CATEGORIES = "migrate-to-kaltura.kal.categories";
	public static final String OEQ_SEARCH_ATT_DESC = "migrate-to-kaltura.oeq.search.attachment.description";
	public static final String OEQ_SEARCH_ATT_SUFFIXES_AUDIO = "migrate-to-kaltura.oeq.search.attachment.suffixes.audio";
	public static final String OEQ_SEARCH_ATT_SUFFIXES_VIDEO = "migrate-to-kaltura.oeq.search.attachment.suffixes.video";
	public static final String OEQ_SEARCH_KAL_TAGS_XPATH = "migrate-to-kaltura.oeq.search.kal.tags.xpath";
	public static final String OEQ_KAL_ID = "migrate-to-kaltura.oeq.kal.id";
	public static final String GENERAL_MAX_ITEMS_TO_MIGRATE = "migrate-to-kaltura.general.max.items.to.migrate";

	// ExportItems
	public static final String EXPORT_ITEMS_OUTPUT_FILE = "export.items.output";
	public static final String EXPORT_ITEMS_COLUMN_FORMAT = "export.items.columnFormat";
	public static final String EXPORT_ITEMS_FILTER_DATE_CREATED = "export.items.filter.dateCreated";
	public static final String EXPORT_ITEMS_ATTACHMENT_PATH_TEMPLATE = "export.items.attachment.path.template";

	// Email
	public static final String EMAIL_SERVER = "email.server";
	public static final String EMAIL_USERNAME = "email.username";
	public static final String EMAIL_PASSWORD = "email.password";
	public static final String EMAIL_SENDER_ADDRESS = "email.sender.address";
	public static final String EMAIL_SENDER_NAME = "email.sender.name";

	//Thumbnail
	public static final String THUMBNAIL_V1_IM_LOCATION = "thumbnail.v1.im.location";

	// general
	public static final String OEQ_URL = "oeq.url";
	public static final String OEQ_OAUTH_CLIENT_ID = "oeq.oauth.client.id";
	public static final String OEQ_OAUTH_CLIENT_SECRET = "oeq.oauth.client.secret";
	public static final String OEQ_SEARCH_API = "oeq.search.api";
	public static final String GENERAL_OS_SLASH = "general.os.slash";
	public static final String GENERAL_DOWNLOAD_FOLDER = "general.download.folder";
	public static final String GENERAL_DOWNLOAD_CHATTER = "general.download.chatter";
	public static final String OEQ_SEARCH_API_REQUESTED_LENGTH = "oeq.search.api.requested.length";

	// JSON report
	public static final String REPORT_JSON_KEYWORDS = "report.json.keywords";
	public static final String REPORT_JSON_RAW_DATA_FILENAME = "report.json.raw.data.filename";
	public static final String REPORT_JSON_ROOT_ARRAY_KEY = "report.json.root.array.key";
	public static final String REPORT_CONFIG_TEMP_FILENAME = "report.json.TEMP.filename";

	// Check Files (CF)
  public static final String CF_OUTPUT_FOLDER = "cf.output.folder";
  public static final String CF_ADOPTER_NAME = "cf.adopter.name";
  public static final String CF_MODE = "cf.mode";
  public static final String CF_EMAIL_MODE = "cf.email.mode";
  public static final String CF_EMAIL_RECIPIENTS = "cf.email.recipients";
  public static final String CF_INSTITUTION_SHORTNAME = "cf.institution.shortname";
  public static final String CF_MAX_TRIES = "cf.max.tries";
  public static final String CF_TESTING_TIMEOUT = "cf.testing.timeout";
  public static final String CF_DB_URL = "cf.db.url";
  public static final String CF_DB_USERNAME = "cf.db.username";
  public static final String CF_DB_PASSWORD = "cf.db.password";
  public static final String CF_DB_TYPE = "cf.db.type";
  public static final String CF_FILESTORE_DIR = "cf.filestore.directory";
  public static final String CF_NUM_OF_ITEMS_PER_QUERY = "cf.num.items.per.query";
  // See note in conf/blank.properties
  public static final String CF_FILESTORE_DIR_ADV = "cf.filestore.advanced.";
  public static final String CF_FILTER_BY_COLLECTION = "cf.filter.by.collection";
	public static final String CF_FILTER_BY_INSTITUTION = "cf.filter.by.institution";
	public static final String CF_COMPARE_MISSING_ATTS_AFTER_RUN = "cf.compare.missing.atts.after.run";


	public enum CheckFilesType {
    REST,  // Implemented, but needs testing after the open source effort
    DB_ALL_ITEMS_ALL_ATTS, // Currently the only supported method
    DB_BATCH_ITEMS_PER_ITEM_ATTS // Implemented, but needs testing after the open source effort
  }

  public enum CheckFilesSupportedDB {
    POSTGRE
  }

  public enum CheckFilesEmailMode {
    NONE, NORMAL, ONLY_NEW_MISSING_ATTACHMENTS_OR_ERRORS
  }

  private static Logger LOGGER = LogManager.getLogger(Config.class);

	private Properties store;
	private boolean validConfig = true;
	private String filepath;

	private Map<String, List<String>> convertedCsvToLists = new HashMap<>();

	public static Config getInstance() {
		if(config == null) {
      config = new Config();
    }
    return config;
	}

	public static void reset() {
		LOGGER.info("resetting the Config!");
	  config = null;
  }

	// Meant for testing purposes
	public void init(Properties props) {
		store = props;
	}


	public void init(String path) {
		filepath = path;
		try (InputStream input = new FileInputStream(path)) {
			LOGGER.debug("Using [{}] as the configuration file.", path);
			store = new Properties();
			store.load(input);

			checkConfigs();
		} catch (FileNotFoundException e) {
			LOGGER.warn("Unable to find the config file: {}", e.getMessage());
			validConfig = false;
		} catch (IOException e) {
			LOGGER.warn("Unable to use the config file: [{}] due to - [{}].", path, e.getMessage());
			validConfig = false;
		}
	}

	private void checkConfigs() {
    try {
      checkConfig(TOOLBOX_FUNCTION, true, true);
      if(validConfig) {
        switch  (ToolboxFunction.valueOf(getConfig(TOOLBOX_FUNCTION))) {
          case MigrateToKaltura: {
            checkConfigsGeneral();
            checkConfigsMigrateToKaltura();
            break;
          }
          case ExportItems: {
            checkConfigsGeneral();
            checkConfigsExportItems();
            break;
          }
          case Email: {
            checkConfigsEmail();
            break;
          }
          case FileLister: {
            checkConfigsFileLister();
            break;
          }
          case ThumbnailV1: {
            checkConfigsThumbnailV1();
            break;
          }
          case JsonReport: {
            checkConfigsJsonReport();
            break;
          }
					case CheckFiles: {
						checkConfigsCheckFiles();
						break;
					}
					case AttachmentHash: {
						// There is no extra configuration needed to review
						break;
					}
          default: {
            // Should never will happen.  The exception will be thrown first
            LOGGER.warn("Toolbox function not implemented: {}", getConfig(TOOLBOX_FUNCTION));
            validConfig = false;
          }
        }
      }
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Unknown value for {}: {}", TOOLBOX_FUNCTION, getConfig(TOOLBOX_FUNCTION));
      validConfig = false;
    }
  }

	private void checkConfigsGeneral() {
		checkConfig(OEQ_OAUTH_CLIENT_ID, true, true);
		checkConfig(OEQ_OAUTH_CLIENT_SECRET, false, true);
		checkConfig(OEQ_SEARCH_API, true, true);
		checkConfig(GENERAL_OS_SLASH, true, true);
		checkConfig(GENERAL_DOWNLOAD_FOLDER, true, true);
		checkConfig(GENERAL_DOWNLOAD_CHATTER, true, true);
		if(validConfig) checkInt(GENERAL_DOWNLOAD_CHATTER, true);
		checkConfig(OEQ_SEARCH_API_REQUESTED_LENGTH, true, false);
		if(hasConfig(OEQ_SEARCH_API_REQUESTED_LENGTH)) {
			checkInt(OEQ_SEARCH_API_REQUESTED_LENGTH, true);
			if(validConfig) {
				int i = getConfigAsInt(OEQ_SEARCH_API_REQUESTED_LENGTH);
				if(i < 1) {
					LOGGER.warn("Property {} must be greater than zero.", OEQ_SEARCH_API_REQUESTED_LENGTH);
					validConfig = false;
				}
			}
		}
	}

	private void checkConfigsMigrateToKaltura() {
		checkConfig(KAL_PARTNER_ID, false, true);
		if(validConfig) checkInt(KAL_PARTNER_ID, false);
		checkConfig(KAL_USER_ID, true, true);
		checkConfig(KAL_ADMIN_SECRET, false, true);
		checkConfig(KAL_SERVICE_URL, true, true);
		checkConfig(KAL_CATEGORIES, true, true);
		checkConfig(OEQ_SEARCH_KAL_TAGS_XPATH, true, true);
		checkConfig(OEQ_SEARCH_ATT_DESC, true, true);
		checkConfig(OEQ_KAL_ID, true, true);
		checkMigrateToKalturaAttachmentSuffixes();
		checkConfig(GENERAL_MAX_ITEMS_TO_MIGRATE, true, false);
		if(hasConfig(GENERAL_MAX_ITEMS_TO_MIGRATE)) {
			checkInt(GENERAL_MAX_ITEMS_TO_MIGRATE, true);
			if(validConfig) {
				int i = getConfigAsInt(GENERAL_MAX_ITEMS_TO_MIGRATE);
				if(i < 1) {
					LOGGER.warn("Property {} must be greater than zero.", GENERAL_MAX_ITEMS_TO_MIGRATE);
					validConfig = false;
				}
			}
		}
	}

	private void checkConfigsEmail() {
		checkConfig(EMAIL_SERVER, true, true);
		checkConfig(EMAIL_USERNAME, true, true);
		checkConfig(EMAIL_PASSWORD, false, true);
		checkConfig(EMAIL_SENDER_ADDRESS, true, true);
		checkConfig(EMAIL_SENDER_NAME, true, true);
	}

	public void checkConfigsJsonReport() {
		checkConfig(REPORT_JSON_KEYWORDS, true, true);
		checkConfig(REPORT_JSON_RAW_DATA_FILENAME, true, true);
		checkConfig(REPORT_JSON_ROOT_ARRAY_KEY, true, true);
	}

  public void checkConfigsCheckFiles() {
    checkConfig(CF_OUTPUT_FOLDER, true, true);
    checkConfig(CF_ADOPTER_NAME, true, true);
    checkConfig(CF_MODE, true, true);
    checkConfig(CF_EMAIL_MODE, true, true);
		checkConfig(CF_COMPARE_MISSING_ATTS_AFTER_RUN, true, false);

    if(validConfig) {
    	checkEnum(CF_EMAIL_MODE, CheckFilesEmailMode.class, true);
		}
    if(validConfig && CheckFilesEmailMode.valueOf(getConfig(CF_EMAIL_MODE)) != CheckFilesEmailMode.NONE) {
      checkConfigsEmail();
      checkConfig(CF_EMAIL_RECIPIENTS, true, true);
    }
    if(validConfig) {
      switch(CheckFilesType.valueOf(getConfig(CF_MODE))) {
      case REST: {
        LOGGER.warn("WARNING:  This mode needs more testing...");

        checkConfig(OEQ_URL, true, true);
        checkConfig(OEQ_OAUTH_CLIENT_ID, true, true);
        checkConfig(OEQ_OAUTH_CLIENT_SECRET, false, true);
        checkConfig(CF_INSTITUTION_SHORTNAME, true, true);
        checkConfig(CF_MAX_TRIES, true, true);
        if(validConfig) checkInt(CF_MAX_TRIES, true);
        checkConfig(CF_TESTING_TIMEOUT, true, true);
        if(validConfig) checkInt(CF_TESTING_TIMEOUT, true);
        break;
      } case DB_BATCH_ITEMS_PER_ITEM_ATTS: {
        LOGGER.warn("WARNING:  This mode needs more testing...");
      } case DB_ALL_ITEMS_ALL_ATTS: {
        checkConfig(CF_DB_URL, true, true);
        checkConfig(CF_DB_USERNAME, true, true);
        checkConfig(CF_DB_PASSWORD, false, true);
        checkConfig(CF_DB_TYPE, true, true);
        if(validConfig) checkEnum(CF_DB_TYPE, CheckFilesSupportedDB.class, true);
        checkConfig(CF_FILESTORE_DIR, true, true);
        checkConfig(CF_NUM_OF_ITEMS_PER_QUERY, true, true);
        checkConfig(CF_FILTER_BY_COLLECTION, true, false);
        checkConfig(CF_FILTER_BY_INSTITUTION, true, false);
        break;
      } default: {
        LOGGER.warn("Property {} must be a known value  Instead was [{}].", CF_MODE, getConfig(CF_MODE));
        validConfig = false;
      }
      }
    }
  }

  private void checkConfigsThumbnailV1() {
		checkConfig(THUMBNAIL_V1_IM_LOCATION, true, true);
	}

	private void checkConfigsExportItems() {
		checkConfig(EXPORT_ITEMS_OUTPUT_FILE, true, true);
		if(validConfig) {
			if(!getConfig(EXPORT_ITEMS_OUTPUT_FILE).endsWith(".csv")) {
				LOGGER.warn("Property {} must end in [.csv].", EXPORT_ITEMS_OUTPUT_FILE);
				validConfig = false;
			}
		}
		checkConfig(EXPORT_ITEMS_COLUMN_FORMAT, true, true);
		checkConfig(EXPORT_ITEMS_FILTER_DATE_CREATED, true, false);
		checkConfig(EXPORT_ITEMS_ATTACHMENT_PATH_TEMPLATE, true, true);
	}

	private void checkConfigsFileLister() {
		checkConfig(GENERAL_OS_SLASH, true, true);
	}

	private void checkConfig(String key, boolean displayValue, boolean required) {
		if(store.containsKey(key)) {
			String value = displayValue ? store.getProperty(key) : "****";
			LOGGER.debug("Found property [{}]=[{}].", key, value);
		} else {
			String state = required ? "required" : "optional";
			LOGGER.debug("Unable to find {} property [{}] in {}.", state, key, filepath);
			if(required) {
				validConfig = false;
			}
		}
	}

  public void checkInt(String key, boolean displayValue) {
    try{
      Integer.parseInt(store.getProperty(key));
    } catch (NumberFormatException e) {
      String value = displayValue ? store.getProperty(key) : "****";
      LOGGER.info("Unable to parse property [{}]=[{}] as an integer.", key, value);
      validConfig = false;
    }
  }

  public void checkNotNull(String key, Object obj, boolean displayValue) {
    if(obj == null) {
      String value = displayValue ? store.getProperty(key) : "****";
      LOGGER.info("Unable to parse property [{}]=[{}] as an integer.", key, value);
      validConfig = false;
    }
  }

  public void checkEnum(String key, Class enumClass, boolean displayValue) {
    try {
      Enum.valueOf(enumClass, store.getProperty(key));
    } catch(IllegalArgumentException e) {
      String value = displayValue ? store.getProperty(key) : "****";
      LOGGER.warn("Property {} must be a known value  Instead was [{}].", key, getConfig(key));
      validConfig = false;
    }
  }
	
	public boolean isValidConfig() {
		return validConfig;
	}
	
	public String getConfig(String key) {
		return store.getProperty(key);
	}

	public static CheckFilesEmailMode getCheckFilesEmailModeConfig() {
    return CheckFilesEmailMode.valueOf(getInstance().getConfig(CF_EMAIL_MODE));
  }

  public static CheckFilesType getCheckFilesTypeConfig() {
    return CheckFilesType.valueOf(getInstance().getConfig(CF_MODE));
  }

  /**
   * Probably would be good to generalize this.
   * @param shortname
   * @return
   */
  public String getFilestoreHandle(String shortname) {
    return store.getProperty(String.format(
            "cf.db.filestore.institution.handle.%s", shortname));
  }
	
	public List<String> getConfigAsList(String key) {
		return convertedCsvToLists.get(key);
	}
	
	/**
	 *  Assumes checkInt(key) and isValidConfig() has already been called
	 */
	public int getConfigAsInt(String key) {
		return Integer.parseInt(store.getProperty(key));
	}
	
	public boolean hasConfig(String key) {
		return store.containsKey(key);
	}
	
	public void checkMigrateToKalturaAttachmentSuffixes() {
		checkConfig(OEQ_SEARCH_ATT_SUFFIXES_AUDIO, true, true);
		checkConfig(OEQ_SEARCH_ATT_SUFFIXES_VIDEO, true, true);

		if (validConfig) {
			// The script needs to know what type of media the attachment to upload is.  
			// To ensure an unambiguous decision, the audio suffixes and video suffixes must be unique.

			List<String> audios = Arrays.asList(getConfig(OEQ_SEARCH_ATT_SUFFIXES_AUDIO).toUpperCase().split(","));
			List<String> videos = Arrays.asList(getConfig(OEQ_SEARCH_ATT_SUFFIXES_VIDEO).toUpperCase().split(","));

			if (audios.size() != 0 && videos.size() != 0) {
				for (String a : audios) {
					if (a.trim().length() != 0) {
						if (videos.contains(a.trim())) {
							LOGGER.info("{} and {} must have distinct (case insensitive) sets.  Found [{}] in [{}].", OEQ_SEARCH_ATT_SUFFIXES_AUDIO, OEQ_SEARCH_ATT_SUFFIXES_VIDEO, a, getConfig(OEQ_SEARCH_ATT_SUFFIXES_VIDEO));
							validConfig = false;
							return;
						}
					}
				}

				for (String v : videos) {
					if (v.trim().length() != 0) {
						if (audios.contains(v.trim())) {
							LOGGER.info("{} and {} must have distinct (case insensitive) sets.  Found [{}] in [{}].", OEQ_SEARCH_ATT_SUFFIXES_AUDIO, OEQ_SEARCH_ATT_SUFFIXES_VIDEO, v, getConfig(OEQ_SEARCH_ATT_SUFFIXES_AUDIO));
							validConfig = false;
							return;
						}
					}
				}
			}

			convertedCsvToLists.put(OEQ_SEARCH_ATT_SUFFIXES_AUDIO, audios);
			convertedCsvToLists.put(OEQ_SEARCH_ATT_SUFFIXES_VIDEO, videos);
		}
	}

	public void setConfig(String key, String val) {
	  if(this.store == null) {
      this.store = new Properties();
    }
		this.store.setProperty(key, val);
	}

	// convenience method
	public static String get(String key) {
	  return Config.getInstance().getConfig(key);
  }
}
