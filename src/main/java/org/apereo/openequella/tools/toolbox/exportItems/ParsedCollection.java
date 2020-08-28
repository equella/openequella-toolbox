package org.apereo.openequella.tools.toolbox.exportItems;

public class ParsedCollection {

  private String uuid;
  private String name;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSignature() {
    return "Collection " + name + " (uuid: " + uuid + ")";
  }
}
