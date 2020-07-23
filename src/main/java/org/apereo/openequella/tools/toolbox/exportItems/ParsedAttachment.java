package org.apereo.openequella.tools.toolbox.exportItems;

import java.util.ArrayList;
import java.util.List;
import org.apereo.openequella.tools.toolbox.Config;
import org.json.JSONObject;

public class ParsedAttachment {
  private JSONObject attData;

  public ParsedAttachment(JSONObject jsonObject) {
    this.attData = jsonObject;
  }

  public String get(String key) {
    if (attData.has(key)) {
      return attData.get(key).toString();
    }
    return "";
  }

  public void append(String key, String val) {
    String original = "";
    if (this.attData.has(key)) {
      original = this.attData.getString(key);
    } else {
      original = "";
    }
    this.attData.put(key, original + Config.get(Config.EXPORT_ITEMS_MULTI_VALUE_DELIM + val));
  }

  public List<String> getKeys() {
    final List<String> keys = new ArrayList<>();
    keys.add("type");
    keys.add("uuid");
    keys.add("description");
    keys.add("preview");
    keys.add("restricted");
    keys.add("size");
    keys.add("conversion");
    keys.add("filename");
    keys.add("thumbFilename");
    keys.add("url");
    keys.add("disabled");
    return keys;
  }
}
