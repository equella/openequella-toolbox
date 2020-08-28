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

package org.apereo.openequella.tools.toolbox.exportItems;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class ParsedItem {
  private String uuid;
  private int version;
  private String name;
  private String description;
  private String filepath;
  private String kalturaMediaId;
  private JSONObject json;
  private String metadata;
  private Map<String, List<String>> parsedMetadata;
  private List<ParsedAttachment> attachments;
  private String kalturaTags;
  private Date createdDate;
  private String modifiedDateStr;
  private String createdDateStr;
  private String primaryFileType;
  private String collectionUuid;

  public String getKalturaMediaId() {
    return kalturaMediaId;
  }

  public void setKalturaMediaId(String kalturaMediaId) {
    this.kalturaMediaId = kalturaMediaId;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }

  public String toString() {
    return name + ": Desc=[" + description + "], UUID=" + uuid + ", Version=" + version;
  }

  public String getSignature() {
    return String.format("%s/%s: [%s]", uuid, version, name);
  }

  public void setJson(JSONObject resourceObj) {
    this.json = resourceObj;
  }

  public JSONObject getJson() {
    return this.json;
  }

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public String getKalturaTags() {
    return kalturaTags;
  }

  public void setKalturaTags(String kalturaTags) {
    this.kalturaTags = kalturaTags;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date date) {
    createdDate = date;
  }

  public String getCreatedDateStr() {
    return createdDateStr;
  }

  public void setCreatedDateStr(String date) {
    createdDateStr = date;
  }

  public String getModifiedDateStr() {
    return modifiedDateStr;
  }

  public void setModifiedDateStr(String date) {
    modifiedDateStr = date;
  }

  public void setPrimaryFileType(String suffix) {
    primaryFileType = suffix;
  }

  public String getPrimaryFileType() {
    return primaryFileType;
  }

  public String getCollectionUuid() {
    return collectionUuid;
  }

  public void setCollectionUuid(String collectionUuid) {
    this.collectionUuid = collectionUuid;
  }

  public Map<String, List<String>> getParsedMetadata() {
    return parsedMetadata;
  }

  public List<String> getParsedMetadata(String key) {
    if ((parsedMetadata != null) && parsedMetadata.containsKey(key)) {
      return parsedMetadata.get(key);
    }
    return new ArrayList<>();
  }

  public void addToParsedMetadata(String key, String val) {
    if (this.parsedMetadata == null) {
      this.parsedMetadata = new HashMap<>();
    }
    if (!this.parsedMetadata.containsKey(key)) {
      this.parsedMetadata.put(key, new ArrayList<>());
    }
    this.parsedMetadata.get(key).add(val);
  }

  public List<ParsedAttachment> getAttachments() {
    return attachments;
  }

  public void addToParsedAttachments(ParsedAttachment pAtt) {
    if (this.attachments == null) {
      this.attachments = new ArrayList<>();
    }
    this.attachments.add(pAtt);
  }

  public ParsedAttachment getMungedAttachment() {
    ParsedAttachment pa = new ParsedAttachment(new JSONObject());
    for (ParsedAttachment pAttToMunge : this.attachments) {
      for (String key : pAttToMunge.getKeys()) {
        pa.append(key, pAttToMunge.get(key));
      }
    }
    return pa;
  }
}
