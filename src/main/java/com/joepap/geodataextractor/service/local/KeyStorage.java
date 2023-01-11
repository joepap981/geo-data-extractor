package com.joepap.geodataextractor.service.local;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.Maps;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KeyStorage {

    private final Map<String, Boolean> API_KEYS = Maps.newHashMap();
    private String MAIN_KEY = Strings.EMPTY;

    public static void add(List<String> restApiKeys) {
        for (String restApiKey : restApiKeys) {
            if (API_KEYS.isEmpty()) {
                MAIN_KEY = restApiKey;
            }
            API_KEYS.put(restApiKey, true);
        }
    }

    public static String activateKey(String apiKey) {
        API_KEYS.put(apiKey, true);
        MAIN_KEY = apiKey;
        return String.format("Activated key : %s", apiKey);
    }

    public static String deactivateKey(String apiKey) {
        API_KEYS.put(apiKey, false);
        setNewMainKey();
        return String.format("Deactivated key : %s and set new main key : %s", apiKey, MAIN_KEY);
    }

    public static String get() {
        return MAIN_KEY;
    }

    public static String remove(String apiKey) {
        API_KEYS.remove(apiKey);
        if (API_KEYS.isEmpty()) {
            MAIN_KEY = Strings.EMPTY;
        } else if (MAIN_KEY.equals(apiKey)) {
            setNewMainKey();
        }

        return String.format("Removed key %s and set main key %s", apiKey, MAIN_KEY);
    }

    private static void setNewMainKey() {
        for (Map.Entry<String, Boolean> mapEntry : API_KEYS.entrySet()) {
            if (mapEntry.getValue()) {
                MAIN_KEY = mapEntry.getKey();
                break;
            }
        }
    }

    public static String printKeys() {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Boolean> mapEntry : API_KEYS.entrySet()) {
            if (MAIN_KEY.equals(mapEntry.getKey())) {
                sb.append("* ");
            }
            sb.append(mapEntry.getKey())
              .append(mapEntry.getValue() ? " - active" : " - inactive")
              .append('\n');
        }
        if (!sb.isEmpty()) {
            sb.replace(sb.length() -1, sb.length(), "");
        }

        return sb.toString();
    }

    public static boolean hasActiveKey() {
        for (Map.Entry<String, Boolean> mapEntry : API_KEYS.entrySet()) {
            if (mapEntry.getValue()) {
                return true;
            }
        }
        return false;
    }
}
