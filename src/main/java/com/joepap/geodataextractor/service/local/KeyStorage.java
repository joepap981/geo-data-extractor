package com.joepap.geodataextractor.service.local;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KeyStorage {

    private static String API_KEY = "b31f77ef8ea0b5bfeca35c06f8c2cf2a";

    public static void set(String restApiKey) {
        API_KEY = restApiKey;
    }

    public static String get() {
        return API_KEY;
    }
}
