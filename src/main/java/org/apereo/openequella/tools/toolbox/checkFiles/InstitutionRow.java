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

public class InstitutionRow {
  private int id;
  private String shortname;
  private String name;
  private String url;
  private String uniqueId;
  private String filestoreHandle;

  public int getId() {
    return id;
  }

  public void setId(int i) {
    this.id = i;
  }

  public String getShortname() {
    return shortname;
  }

  public void setShortname(String shortname) {
    this.shortname = shortname;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String toString() {
    return String.format(
        "InstitutionRow:  ID=[%s], shortname=[%s], name=[%s], url=[%s], uniqueId=[%s]",
        id, shortname, name, url, uniqueId);
  }

  public String getFilestoreHandle() {
    return filestoreHandle;
  }

  public void setFilestoreHandle(String filestoreHandle) {
    this.filestoreHandle = filestoreHandle;
  }
}
