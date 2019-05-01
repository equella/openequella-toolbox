package org.apereo.openequella.tools.toolbox.utils;

import java.util.Properties;

public class GeneralUtils {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static void setDefaultIfNotPresent(Properties p, String key, String val) {
        if(!p.containsKey(key)) {
            p.setProperty(key, val);
        }
    }
}
